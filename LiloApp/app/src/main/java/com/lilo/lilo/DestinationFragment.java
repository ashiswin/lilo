package com.lilo.lilo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lilo.lilo.adapters.DestinationAdapter;

import org.json.JSONException;
import org.json.JSONObject;

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

}
