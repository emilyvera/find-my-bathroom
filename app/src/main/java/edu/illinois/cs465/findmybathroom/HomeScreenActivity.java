package edu.illinois.cs465.findmybathroom;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import edu.illinois.cs465.findmybathroom.databinding.ActivityHomeScreenBinding;

public class HomeScreenActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityHomeScreenBinding binding;
    private ImageButton btnAddBathroom;
    ArrayList<Marker> AllMarkers = new ArrayList<Marker>();
    DatabaseHelper bathroomDb;
    boolean MapReady = false;
    TextView bathroomText;
    LinearLayout detailsCard;
    Button reviewButton;

    // Filters start

    public boolean shouldShowBathroom(String type, String all_gender, String wheelchair, String diaper) {
        boolean showBathroom = true;
        if (type.equals("bathroom") && ((CheckedTextView) findViewById(R.id.BathroomType)).isChecked()) {

            if (((CheckedTextView) findViewById(R.id.BathroomGender)).isChecked() && all_gender.equals("0")) {
                showBathroom = false;
            }

            if (((CheckedTextView) findViewById(R.id.BathroomWheelchair)).isChecked() && wheelchair.equals("0")) {
                showBathroom = false;
            }

            if (((CheckedTextView) findViewById(R.id.BathroomDiaper)).isChecked() && diaper.equals("0")) {
                showBathroom = false;
            }

        } else if (type.equals("gas station") && ((CheckedTextView) findViewById(R.id.gasStationType)).isChecked()) {
            /*
                Todo: Add bathroom filters
             */
        }

        return showBathroom;
    }

    public void typeButtonClicked(View v)
    {
        CheckedTextView real_view = (CheckedTextView) v;
        real_view.toggle();

        int new_view = (real_view.isChecked()) ? View.VISIBLE : View.GONE;

        View bathroomFilters = findViewById(R.id.BathroomFilters);
        View gasStationFilters = findViewById(R.id.GasStationFilters);

        View filters_to_change = v.getId() == R.id.BathroomType ? bathroomFilters : gasStationFilters;

        filters_to_change.setVisibility(new_view);


        boolean show_options = ((CheckedTextView) findViewById(R.id.BathroomType)).isChecked() || ((CheckedTextView) findViewById(R.id.gasStationType)).isChecked();
        findViewById(R.id.FeaturesHolder).setVisibility( show_options ? View.VISIBLE : View.GONE);
        findViewById(R.id.VerificationHolder).setVisibility( show_options ? View.VISIBLE : View.GONE);
        updateMap();
    }

    public void filterButtonClicked(View v)
    {
        CheckedTextView real_view = (CheckedTextView) v;
        real_view.toggle();
        updateMap();
    }


    public void filterExpandClicked(View v) {
        ((CheckedTextView) v).toggle();
        int new_view = (((CheckedTextView) v).isChecked()) ? View.VISIBLE : View.GONE;
        findViewById(R.id.FilterHolder).setVisibility(new_view);
    }

    // Filters end

    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addButton:
                    // doStuff
                    startActivity(new Intent(HomeScreenActivity.this, AddBathroomActivity.class));
                    break;
                case R.id.reviewButton:
                    // doStuff
                    startActivity(new Intent(HomeScreenActivity.this, ReviewActivity.class));
                    break;
            }
        }
    };

    public void updateMap() {
        if (MapReady) {
            // Remove existing bathrooms from the map
            for (Marker mLocationMarker : AllMarkers) {
                mLocationMarker.remove();
            }
            AllMarkers.clear();

            // Place existing bathrooms on the map
            Cursor cursor = bathroomDb.getReadableDatabase().rawQuery("select * from bathroom_data", null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    @SuppressLint("Range") Double latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                    @SuppressLint("Range") Double longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("LOCATION_TYPE"));
                    @SuppressLint("Range") String all_gender = cursor.getString(cursor.getColumnIndex("IS_ALL_GENDER"));
                    @SuppressLint("Range") String wheelchair = cursor.getString(cursor.getColumnIndex("IS_WHEELCHAIR_ACCESSIBLE"));
                    @SuppressLint("Range") String diaper = cursor.getString(cursor.getColumnIndex("HAS_DIAPER_STATION"));

                    if (shouldShowBathroom(type, all_gender, wheelchair, diaper)) {
                        LatLng location = new LatLng(latitude, longitude);

                        Marker bathroomMarker = mMap.addMarker(new MarkerOptions().position(location));
                        AllMarkers.add(bathroomMarker);
                    }

                    cursor.moveToNext();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bathroomDb = new DatabaseHelper(this);

        btnAddBathroom = (ImageButton) findViewById(R.id.addButton);
        btnAddBathroom.setOnClickListener(handler);

        bathroomText = (TextView) findViewById(R.id.bathroom_name_text);

        detailsCard = (LinearLayout) findViewById(R.id.details_card);

        reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(handler);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int bathroomId = Integer.parseInt(marker.getId().substring(1, marker.getId().length())) + 1;
                Log.v("bathroom id", String.valueOf(bathroomId));
                Cursor cursor = bathroomDb.getReadableDatabase().rawQuery("select * from bathroom_data where ID=" + bathroomId, null);

                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String bathroomName = cursor.getString(cursor.getColumnIndex("BUILDING_NAME"));
                    bathroomText.setText(bathroomName);
                    detailsCard.setVisibility(View.VISIBLE);
                }
                cursor.close();
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                detailsCard.setVisibility(View.GONE);
            }
        });

        MapReady = true;
        updateMap();

        // Replace with user's current location later
        LatLng quad = new LatLng(40.107519, -88.22722);
        mMap.addMarker(new MarkerOptions().position(quad).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quad, 17));

        findViewById(R.id.Filters).bringToFront();
    }
}