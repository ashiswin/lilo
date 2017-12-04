package com.lilo.lilo;


import android.content.Intent;
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
import android.widget.TextView;

import com.lilo.lilo.adapters.ItineraryAdapter;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.ItineraryStorage;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItineraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItineraryFragment extends Fragment {
    RecyclerView.Adapter adapter;
    ItineraryStorage storage;
    TextView txtStart;
    View viwCard;

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
        TextView txtNoDestinations = (TextView) rootView.findViewById(R.id.txtNoDestinations);

        txtStart = (TextView) rootView.findViewById(R.id.txtStart);
        viwCard = rootView.findViewById(R.id.viwCard);

        adapter = new ItineraryAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lstDestinations.setLayoutManager(layoutManager);

        lstDestinations.setAdapter(adapter);
        storage = ItineraryStorage.newInstance(getActivity());

        if(storage.start != null) {
            viwCard.setVisibility(View.VISIBLE);
            txtStart.setText(storage.start.name);
        }
        else {
            viwCard.setVisibility(View.GONE);
        }

        if(storage.destinations.size() == 0) {
            lstDestinations.setVisibility(View.GONE);
            txtNoDestinations.setVisibility(View.VISIBLE);
        }
        else {
            lstDestinations.setVisibility(View.VISIBLE);
            txtNoDestinations.setVisibility(View.GONE);
        }

        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_plan) {
            Intent plannerIntent = new Intent(getActivity(), PlannerActivity.class);
            startActivityForResult(plannerIntent, 0);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itinerary, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
            if(storage.start != null) {
                viwCard.setVisibility(View.VISIBLE);
                txtStart.setText(storage.start.name);
            }
            else {
                viwCard.setVisibility(View.GONE);
            }

            storage.store();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
