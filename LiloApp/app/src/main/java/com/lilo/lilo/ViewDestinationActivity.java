package com.lilo.lilo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lilo.lilo.adapters.DestinationSlideshowAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewDestinationActivity extends AppCompatActivity {
    MainApplication m;

    MapView mapDestination;
    TextView txtDetails;
    ViewPager pgrSlideshow;
    ImageButton btnLeftNav, btnRightNav;

    DestinationSlideshowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        m = (MainApplication) getApplicationContext();

        mapDestination = (MapView) findViewById(R.id.mapDestination);
        txtDetails = (TextView) findViewById(R.id.txtDetails);
        pgrSlideshow = (ViewPager) findViewById(R.id.pgrSlideshow);
        btnLeftNav = (ImageButton) findViewById(R.id.btnLeftNav);
        btnRightNav = (ImageButton) findViewById(R.id.btnRightNav);

        txtDetails.setText(getIntent().getStringExtra("details"));

        mapDestination.onCreate(savedInstanceState);

        final String lat = getIntent().getStringExtra("lat");
        final String lon = getIntent().getStringExtra("lon");

        mapDestination.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("ViewDestinationActivity", lat + " | " + lon);
                LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                googleMap.addMarker(new MarkerOptions().position(destination).title(getIntent().getStringExtra("name")));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
            }
        });

        StringRequest imagesRequest = new StringRequest(m.SERVER_URL + "/GetImages.php?id=" + getIntent().getIntExtra("id", 0), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    if(!result.getBoolean("success")) {
                        Toast.makeText(ViewDestinationActivity.this, "Failed to load images: " + result.getString("mesage"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        JSONArray images = result.getJSONArray("images");
                        if(images.length() == 0) {
                            pgrSlideshow.setVisibility(View.GONE);
                            return;
                        }
                        Bitmap[] sources = new Bitmap[images.length()];

                        for(int i = 0; i < images.length(); i++) {
                            String base64String = images.getJSONObject(i).getString("src");
                            String base64Image = base64String.split(",")[1];

                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            sources[i] = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        }

                        adapter = new DestinationSlideshowAdapter(sources, ViewDestinationActivity.this);
                        pgrSlideshow.setAdapter(adapter);

                        btnLeftNav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int tab = pgrSlideshow.getCurrentItem();
                                if (tab > 0) {
                                    tab--;
                                    pgrSlideshow.setCurrentItem(tab);
                                } else if (tab == 0) {
                                    pgrSlideshow.setCurrentItem(tab);
                                }
                            }
                        });

                        btnRightNav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int tab = pgrSlideshow.getCurrentItem();
                                tab++;
                                pgrSlideshow.setCurrentItem(tab);
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

        m.queue.add(imagesRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapDestination.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapDestination.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapDestination.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapDestination.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapDestination.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapDestination.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapDestination.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_destination, menu);
        return true;
    }

}
