package com.lilo.lilo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lilo.lilo.adapters.DestinationAdapter;
import com.lilo.lilo.itineraryplanner.BruteSolver;
import com.lilo.lilo.itineraryplanner.FastSolver;
import com.lilo.lilo.itineraryplanner.Location;
import com.lilo.lilo.itineraryplanner.Parser;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.ItineraryStorage;
import com.lilo.lilo.model.Route;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {
    MainApplication m;

    AutoCompleteTextView edtStart;
    Button btnStart;

    ArrayAdapter<String> adapter;
    ItineraryStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        m = (MainApplication) getApplicationContext();
        storage = ItineraryStorage.newInstance(getFilesDir().getAbsolutePath() + "itinerary.json");

        edtStart = (AutoCompleteTextView) findViewById(R.id.edtStart);
        btnStart = (Button) findViewById(R.id.btnStart);

        StringRequest destinationRequest = new StringRequest(m.SERVER_URL + "/GetDestinations.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject result = new JSONObject(response);

                    if(!result.getBoolean("success")) {
                        Toast.makeText(PlannerActivity.this, "Failed to get destinations: " + result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        ArrayList<String> names = new ArrayList<>();
                        for(int i = 0; i < result.getJSONArray("destinations").length(); i++) {
                            names.add(result.getJSONArray("destinations").getJSONObject(i).getString("name"));
                        }
                        adapter = new ArrayAdapter<>(PlannerActivity.this, android.R.layout.simple_list_item_1, names);
                        edtStart.setAdapter(adapter);

                        btnStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String start = edtStart.getText().toString();
                                storage.start = null;

                                if(start.isEmpty()) {
                                    Toast.makeText(PlannerActivity.this, "Please choose a start point", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                try {
                                    for(int i = 0; i < result.getJSONArray("destinations").length(); i++) {
                                        if(result.getJSONArray("destinations").getJSONObject(i).getString("name").equals(start)) {
                                            JSONObject d = result.getJSONArray("destinations").getJSONObject(i);
                                            storage.start = new Destination();
                                            storage.start.id = d.getInt("id");
                                            storage.start.name = d.getString("name");
                                            storage.start.details = d.getString("details");
                                            storage.start.lat = d.getString("lat");
                                            storage.start.lon = d.getString("lon");
                                        }
                                    }

                                    if(storage.start == null) {
                                        Toast.makeText(PlannerActivity.this, "Invalid start point chosen", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    final ProgressDialog dialog = new ProgressDialog(PlannerActivity.this);
                                    dialog.setIndeterminate(true);
                                    dialog.setTitle("Planning");
                                    dialog.setMessage("Please wait while we plan your route");
                                    dialog.setCancelable(false);
                                    dialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PlannerActivity.this);
                                            boolean useFastSolver = preferences.getBoolean("prefSolver", false);

                                            // TODO: Perform planning and cancel dialog
                                            Parser parser = new Parser(getApplicationContext());
                                            ItineraryStorage storage = PlannerActivity.this.storage;
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            List<Location> locationlist = parser.converter(storage.destinations,storage.start);
                                            Location start = locationlist.remove(0);
                                            for (Location d : locationlist){
                                                Log.d(d.name + " " + d.locationID,"DESTINATION PATH");
                                            }

                                            List<double[]> solution;
                                            if (false){
                                                //TODO: add in max price
                                                FastSolver solver = new FastSolver(start,locationlist,10);
                                                solver.run(start);
                                                solution = solver.solution;
                                            } else {
                                                BruteSolver solver = new BruteSolver(start,locationlist,10);
                                                solver.run(start);
                                                solution = solver.solution;
                                            }
                                            
                                            storage.routes = parser.edgesToRoute(solution);
                                            storage.destinations = parser.edgesToDestination(solution,storage.destinations);

                                            for (double[] e : solution){
                                                Log.d(Arrays.toString(e),"PATH");
                                            }
                                            for (Destination d : storage.destinations){
                                                Log.d(d.name + " " + d.id,"DESTINATION PATH");
                                            }
                                            int totaltime = 0;
                                            double totalprice = 0;
                                            for (Route r : storage.routes){
                                                totaltime += r.time;
                                                totalprice += r.cost;
                                                Log.d("" + r.transport + "" + r.cost + "" + r.time,"ROUTES" );
                                            }

                                            Log.d("" + totaltime/60,"" + totalprice);



                                            //TODO: Run solver
                                            //TODO: convert listlocation back to listdestination
                                            //TODO: convert edges to paths
                                            //TODO: update storage
                                            // Code that should run when planning is complete
                                             runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      dialog.cancel();
                                                      setResult(RESULT_OK);
                                                      finish();
                                                  }
                                              });
                                        }
                                    }).start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }
}
