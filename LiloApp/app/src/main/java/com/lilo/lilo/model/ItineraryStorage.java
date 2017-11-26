package com.lilo.lilo.model;

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

    public ItineraryAdapter adapter;
    String filename;

    private static ItineraryStorage instance;

    public static ItineraryStorage newInstance(String filename) {
        if(instance == null) {
            instance = new ItineraryStorage(filename);
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

            JSONArray arr = new JSONArray(sb.toString());

            for(int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                Destination d = new Destination();

                d.id = o.getInt("id");
                d.name = o.getString("name");
                d.details = o.getString("details");

                destinationIds.add(d.id);
                destinations.add(d);
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
        if(destinationIds.contains(d.id)) return false;

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

    private void store() {
        JSONArray arr = new JSONArray();
        for(Destination de : destinations) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", de.id);
                o.put("name", de.name);
                o.put("details", de.details);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            arr.put(o);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(arr.toString());
            writer.flush();
            writer.close();
            Log.d("ItineraryStorage", arr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
