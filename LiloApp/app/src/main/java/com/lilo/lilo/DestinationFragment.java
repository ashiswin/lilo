package com.lilo.lilo;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lilo.lilo.adapters.DestinationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {
    RecyclerView lstDestinations;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    MainApplication m;
    public DestinationFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static DestinationFragment newInstance() {
        DestinationFragment fragment = new DestinationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_destination, container, false);

        m = (MainApplication) getActivity().getApplicationContext();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Destinations");

        lstDestinations = (RecyclerView) rootView.findViewById(R.id.lstDestinations);

        layoutManager = new LinearLayoutManager(getActivity());
        lstDestinations.setLayoutManager(layoutManager);

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading destinations");
        dialog.setMessage("Please wait while we load available destinations");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        StringRequest destinationRequest = new StringRequest(m.SERVER_URL + "/GetDestinations.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);

                    if(!result.getBoolean("success")) {
                        Toast.makeText(getActivity(), "Failed to get destinations: " + result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        adapter = new DestinationAdapter(result.getJSONArray("destinations"), getActivity());
                        lstDestinations.setAdapter(adapter);

                        dialog.cancel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        m.queue.add(destinationRequest);

        return rootView;
    }

    public void sortAZ() throws JSONException {
        JSONArray jsonArr = ((DestinationAdapter) adapter).data;
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get("name");
                    valB = (String) b.get("name");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                return valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        ((DestinationAdapter) adapter).data = sortedJsonArray;
        adapter.notifyDataSetChanged();
    }

    public void sortZA() throws JSONException {
        JSONArray jsonArr = ((DestinationAdapter) adapter).data;
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "name";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                return valB.compareTo(valA);
            }
        });

        for (int i = 0; i < jsonArr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        ((DestinationAdapter) adapter).data = sortedJsonArray;
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_sort) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Sort by");
            String[] modes = {"A-Z", "Z-A", "Popularity"};
            builder.setItems(modes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switch(which){
                        case 0:
                            try {
                                sortAZ();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            try {
                                sortZA();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
