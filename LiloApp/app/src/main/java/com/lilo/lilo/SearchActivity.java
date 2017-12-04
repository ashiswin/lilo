package com.lilo.lilo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class SearchActivity extends AppCompatActivity {
    ArrayList<String> names;
    JSONArray destinations;
    SearchView srcSearch;
    ListView lstResults;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        names = new ArrayList<>();
        try {
            destinations = new JSONArray(getIntent().getStringExtra("destinations"));
            for(int i = 0; i < destinations.length(); i++) {
                names.add(destinations.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        srcSearch = (SearchView) findViewById(R.id.srcSearch);
        lstResults = (ListView) findViewById(R.id.lstResults);

        srcSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<ExtractedResult> results = FuzzySearch.extractSorted(newText, names, 5);
                ArrayList<String> processedResults = new ArrayList<>();
                for(ExtractedResult r : results) {
                    processedResults.add(r.getString());
                }
                adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, processedResults);
                lstResults.setAdapter(adapter);

                return true;
            }
        });

        lstResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < destinations.length(); i++) {
                    try {
                        if(destinations.getJSONObject(i).getString("name").equals(adapter.getItem(position))) {
                            Intent viewDestinationIntent = new Intent(SearchActivity.this, ViewDestinationActivity.class);
                            try {
                                JSONObject destination = destinations.getJSONObject(i);
                                viewDestinationIntent.putExtra("lat", destination.getString("lat"));
                                viewDestinationIntent.putExtra("lon", destination.getString("lon"));
                                viewDestinationIntent.putExtra("name", destination.getString("name"));
                                viewDestinationIntent.putExtra("details", destination.getString("details"));
                                viewDestinationIntent.putExtra("id", destination.getInt("id"));
                                startActivity(viewDestinationIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        if(!preferences.getBoolean("prefPatriotism", false)) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }
        else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryPatriot)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
