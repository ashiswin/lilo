package com.lilo.lilo;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lilo.lilo.adapters.ItineraryAdapter;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.ItineraryStorage;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItineraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItineraryFragment extends Fragment {

    public ItineraryFragment() {
        // Required empty public constructor
    }

    public static ItineraryFragment newInstance() {
        ItineraryFragment fragment = new ItineraryFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_itinerary, container, false);

        RecyclerView lstDestinations = (RecyclerView) rootView.findViewById(R.id.lstDestinations);
        RecyclerView.Adapter adapter = new ItineraryAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lstDestinations.setLayoutManager(layoutManager);

        lstDestinations.setAdapter(adapter);

        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itinerary, menu);
    }
}
