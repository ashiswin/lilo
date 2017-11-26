package com.lilo.lilo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.lilo.lilo.adapters.DestinationEventAdapter;
import com.lilo.lilo.adapters.DestinationSlideshowAdapter;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.ItineraryStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewDestinationActivity extends AppCompatActivity {
    MainApplication m;

    MapView mapDestination;
    TextView txtDetails, txtPhotoTitle, txtEventTitle;
    ViewPager pgrSlideshow;
    ImageButton btnLeftNav, btnRightNav;
    ListView lstEvents;
    ScrollView scrScroll;

    DestinationSlideshowAdapter adapter;
    ItineraryStorage storage;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination_wrapper);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        m = (MainApplication) getApplicationContext();
        storage = ItineraryStorage.newInstance(getFilesDir().getAbsolutePath() + "itinerary.json");

        mapDestination = (MapView) findViewById(R.id.mapDestination);
        txtDetails = (TextView) findViewById(R.id.txtDetails);
        pgrSlideshow = (ViewPager) findViewById(R.id.pgrSlideshow);
        btnLeftNav = (ImageButton) findViewById(R.id.btnLeftNav);
        btnRightNav = (ImageButton) findViewById(R.id.btnRightNav);
        txtPhotoTitle = (TextView) findViewById(R.id.txtPhotoTitle);
        txtEventTitle = (TextView) findViewById(R.id.txtEventTitle);
        lstEvents = (ListView) findViewById(R.id.lstEvents);
        scrScroll = (ScrollView) findViewById(R.id.scrScroll);

        txtDetails.setText(getIntent().getStringExtra("details"));

        mapDestination.onCreate(savedInstanceState);

        final String lat = getIntent().getStringExtra("lat");
        final String lon = getIntent().getStringExtra("lon");

        scrScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float alpha = 255 * (scrollY / ((float) mapDestination.getHeight() - toolbar.getHeight()));
                toolbar.getBackground().setAlpha(Math.min((int) alpha, 255));
                Log.d("ViewDestinationActivity", alpha + " ");
            }
        });
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
                            btnLeftNav.setVisibility(View.GONE);
                            btnRightNav.setVisibility(View.GONE);
                            txtPhotoTitle.setVisibility(View.GONE);
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

        StringRequest eventsRequest = new StringRequest(m.SERVER_URL + "/GetEvents.php?id=" + getIntent().getIntExtra("id", 0), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    if(!result.getBoolean("success")) {
                        Toast.makeText(ViewDestinationActivity.this, "Failed to load events: " + result.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        JSONArray events = result.getJSONArray("events");
                        if(events.length() == 0) {
                            lstEvents.setVisibility(View.GONE);
                            txtEventTitle.setVisibility(View.GONE);
                            return;
                        }
                        else {
                            DestinationEventAdapter adapter = new DestinationEventAdapter(events);
                            lstEvents.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(lstEvents);
                        }
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
        m.queue.add(eventsRequest);
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
        else if(id == R.id.action_add_destination) {
            Destination d = new Destination();
            d.id = getIntent().getIntExtra("id", 0);
            d.name = getIntent().getStringExtra("name");
            d.details = getIntent().getStringExtra("details");
            d.lat = getIntent().getStringExtra("lat");
            d.lon = getIntent().getStringExtra("lon");

            if(!storage.add(d)) {
                Snackbar.make(findViewById(android.R.id.content), "Already added destination to itinerary", Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(findViewById(android.R.id.content), "Added destination to itinerary", Snackbar.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_destination, menu);
        return true;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
