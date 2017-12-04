package com.lilo.lilo.model;

import android.content.Context;
import android.util.Log;

import com.lilo.lilo.adapters.ItineraryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ashis on 11/26/2017.
 */

public class ItineraryStorage {
    public TreeSet<Integer> destinationIds;
    public ArrayList<Destination> destinations;
    public ArrayList<Route> routes;

    public Destination start;

    public ItineraryAdapter adapter;
    String filename;

    private static ItineraryStorage instance;

    public static ItineraryStorage newInstance(Context context) {
        if(instance == null) {
            instance = new ItineraryStorage(context.getFilesDir() + "/itinerary.json");
        }

        return instance;
    }
    private ItineraryStorage(String filename) {
        destinationIds = new TreeSet<>();
        destinations = new ArrayList<>();
        routes = new ArrayList<>();

        this.filename = filename;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            StringBuilder sb = new StringBuilder();

            while((line = reader.readLine()) != null) {
                Log.d("ItineraryStorage", line);
                sb.append(line);
            }

            JSONObject itineraryStuff = new JSONObject(sb.toString());
            JSONArray arr = itineraryStuff.getJSONArray("itinerary");

            for(int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                Destination d = new Destination();

                d.id = o.getInt("id");
                d.name = o.getString("name");
                d.details = o.getString("details");
                d.lat = o.getString("lat");
                d.lon = o.getString("lon");

                destinationIds.add(d.id);
                destinations.add(d);
            }

            arr = itineraryStuff.getJSONArray("routes");

            for(int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                Route r = new Route();

                r.cost = o.getDouble("cost");
                r.time = o.getInt("time");
                r.transport = o.getString("transport");

                routes.add(r);
            }

            JSONObject s = itineraryStuff.getJSONObject("start");
            if(s.getInt("id") != -1) {
                start = new Destination();
                start.id = s.getInt("id");
                start.name = s.getString("name");
                start.details = s.getString("details");
                start.lat = s.getString("lat");
                start.lon = s.getString("lon");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Skip loading since file doesn't exist
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean add(Destination d) {
        if(destinationIds.contains(d.id) || (start != null && start.id == d.id)) return false;

        destinationIds.add(d.id);
        destinations.add(d);

        store();

        if(adapter != null) adapter.notifyDataSetChanged();

        return true;
    }

    public void remove(Destination d) {
        destinations.remove(d);
        destinationIds.remove(d.id);

        if(adapter != null) adapter.notifyDataSetChanged();

        store();
    }

    public void store() {
        JSONArray arr = new JSONArray();
        for(Destination de : destinations) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", de.id);
                o.put("name", de.name);
                o.put("details", de.details);
                o.put("lat", de.lat);
                o.put("lon", de.lon);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            arr.put(o);
        }

        JSONArray routeArr = new JSONArray();
        for(Route r : routes) {
            JSONObject o = new JSONObject();
            try {
                o.put("cost", r.cost);
                o.put("time", r.time);
                o.put("transport", r.transport);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            routeArr.put(o);
        }

        JSONObject s = new JSONObject();
        try {
            if (start != null) {
                s.put("id", start.id);
                s.put("name", start.name);
                s.put("details", start.details);
                s.put("lat", start.lat);
                s.put("lon", start.lon);
            }
            else {
                s.put("id", -1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject o = new JSONObject();
        try {
            o.put("itinerary", arr);
            o.put("routes", routeArr);
            o.put("start", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(o.toString());
            writer.flush();
            writer.close();
            Log.d("ItineraryStorage", arr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
