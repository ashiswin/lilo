package com.lilo.lilo;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewDestinationActivity extends AppCompatActivity {
    MapView mapDestination;
    TextView txtDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_destination);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        mapDestination = (MapView) findViewById(R.id.mapDestination);
        txtDetails = (TextView) findViewById(R.id.txtDetails);

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
}
