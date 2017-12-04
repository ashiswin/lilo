package com.lilo.lilo.itineraryplanner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lilo.lilo.MainApplication;
import com.lilo.lilo.adapters.DestinationAdapter;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.Route;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Oon Tong on 11/29/2017.
 */

public class Parser {
    public double[][][] taxi;
    double[][][] bus;
    double[][][] walk;

    int valuesParsed;

    ParserCompleteListener listener;


    public List<Location> converter(ArrayList<Destination> dest, Destination start){

        int index = -1;
        for (Destination removeStart : dest) {
            if (removeStart.name.equals(start.name)) {
                index = dest.indexOf(removeStart);
            }
        }
        if (index != -1){
            dest.remove(index);
        }
        dest.add(0,start);

        List<Location> locations = new LinkedList<>();
        for (Destination d : dest){
            Location l = new Location(d.name,d.id);
            locations.add(l);
        }
        //edges = mode of transport,next,time,price

        for (Location l : locations){
            for (Location edge: locations){
                if (l != edge){
                    l.edges.add(new double[]{(double)3,(double)edge.locationID,taxi[l.locationID-1][edge.locationID-1][0],taxi[l.locationID-1][edge.locationID-1][1]});
                    l.edges.add(new double[]{(double)2,(double)edge.locationID,bus[l.locationID-1][edge.locationID-1][0],bus[l.locationID-1][edge.locationID-1][1]});
                    l.edges.add(new double[]{(double)1,(double)edge.locationID,walk[l.locationID-1][edge.locationID-1][0],walk[l.locationID-1][edge.locationID-1][1]});

                }
            }
        }

        return locations;
    }

    public void setParserCompleteListener(ParserCompleteListener listener) {
        this.listener = listener;
    }

    public Parser(Context context) {
        valuesParsed = 0;
        MainApplication m = (MainApplication) context.getApplicationContext();

        StringRequest taxi = new StringRequest(Request.Method.POST,m.SERVER_URL + "/taxi.txt", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{

                    double[][][] e = stringToArray(response);

                    Parser.this.taxi = e;
                    valuesParsed++;

                    if(valuesParsed == 3 && listener != null) {
                        listener.onComplete(Parser.this);
                    }
                } catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        StringRequest bus = new StringRequest(Request.Method.POST,m.SERVER_URL + "/bus.txt", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    double[][][] e = stringToArray(response);
                    Parser.this.bus = e;
                    valuesParsed++;

                    if(valuesParsed == 3 && listener != null) {
                        listener.onComplete(Parser.this);
                    }
                } catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        StringRequest walk = new StringRequest(Request.Method.POST,m.SERVER_URL + "/walk.txt", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{
                    double[][][] e = stringToArrayWalk(response);
                    Parser.this.walk = e;
                    valuesParsed++;

                    if(valuesParsed == 3 && listener != null) {
                        listener.onComplete(Parser.this);
                    }
                } catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        m.queue.add(walk);
        m.queue.add(bus);
        m.queue.add(taxi);
    }
    //cost in dollars, distance in m, time in s

    public double[][][] stringToArray(String s){
        String[] lines = s.split("\n");
        String[][] eachline = new String[lines.length][];
        for (int i = 0;i < lines.length;i++){
            eachline[i] = lines[i].split("\t");
        }
        double[][][] result = new double[eachline.length][eachline[0].length][];

        for (int i = 0; i < lines.length;i++){
            for (int j = 0; j < eachline.length;j++){
                String[] priceDistTime = eachline[i][j].substring(1,eachline[i][j].length()-1).split(", ");
                double[] timePrice = new double[2];
                timePrice[0] = Double.parseDouble(priceDistTime[2]);
                timePrice[1] = Double.parseDouble(priceDistTime[0]);
                result[i][j] = timePrice;
            }
        }
        return result;
    }


    public double[][][] stringToArrayWalk(String s){
        String[] lines = s.split("\n");
        String[][] eachline = new String[lines.length][];
        for (int i = 0;i < lines.length;i++){
            eachline[i] = lines[i].split("\t");
        }
        double[][][] result = new double[eachline.length][eachline[0].length][];

        for (int i = 0; i < lines.length;i++){
            for (int j = 0; j < eachline.length;j++){
                String[] priceDistTime = eachline[i][j].substring(1,eachline[i][j].length()-1).split(", ");
                double[] timePrice = new double[2];
                timePrice[0] = Double.parseDouble(priceDistTime[1]);
                timePrice[1] = 0.0;
                result[i][j] = timePrice;
            }
        }
        return result;
    }

    public ArrayList<Route> edgesToRoute(List<double[]> edges){
        ArrayList<Route> routes = new ArrayList<>();

        for (double[] edge : edges){
            Route route = new Route();
            switch((int)edge[0]){
                case 1:
                    route.transport = "WALK";
                    break;
                case 2:
                    route.transport = "BUS";
                    break;
                case 3:
                    route.transport = "CAB";
                    break;
            }
            route.cost = edge[3];
            route.time = (int)edge[2];
            routes.add(route);
        }

        return routes;
    }

    public ArrayList<Destination> edgesToDestination(List<double[]> edges,ArrayList<Destination> destinations){
        ArrayList<Destination> result = new ArrayList<>();

        for (int i = 0; i < edges.size()-1;i++){
            double[] edge = edges.get(i);
            for (Destination d : destinations){
                if (d.id == (int)edge[1]){
                    result.add(d);
                }
            }
        }
        return result;
    }

    public interface ParserCompleteListener {
        void onComplete(Parser parser);
    }
}
