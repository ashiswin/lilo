package com.lilo.lilo;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class SearchActivity extends AppCompatActivity {
    ArrayList<String> names;
    JSONArray destinations;
    SearchView srcSearch;
    ListView lstResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, processedResults);
                lstResults.setAdapter(adapter);

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }
}
