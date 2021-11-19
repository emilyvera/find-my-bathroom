package edu.illinois.cs465.findmybathroom;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import edu.illinois.cs465.findmybathroom.databinding.ActivityHomeScreenBinding;

public class HomeScreenActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityHomeScreenBinding binding;
    private FusedLocationProviderClient fusedLocationClient;


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

//        } else if (type.equals("gas station") && ((CheckedTextView) findViewById(R.id.gasStationType)).isChecked()) {
//            /*
//                Bathroom filters removed
//             */
        } else {
            showBathroom = false;
        }

        return showBathroom;
    }

    public void typeButtonClicked(View v) {
        // No longer used since gas station was removed
        CheckedTextView real_view = (CheckedTextView) v;
        real_view.toggle();

        int new_view = (real_view.isChecked()) ? View.VISIBLE : View.GONE;

        View bathroomFilters = findViewById(R.id.BathroomFilters);
//        View gasStationFilters = findViewById(R.id.GasStationFilters);

        View filters_to_change = v.getId() == R.id.BathroomType ? bathroomFilters : null;

        filters_to_change.setVisibility(new_view);


        boolean show_options = ((CheckedTextView) findViewById(R.id.BathroomType)).isChecked();
        findViewById(R.id.FeaturesHolder).setVisibility(show_options ? View.VISIBLE : View.GONE);
        findViewById(R.id.VerificationHolder).setVisibility(show_options ? View.VISIBLE : View.GONE);
        updateMap();
    }

    public void filterButtonClicked(View v) {
        CheckedTextView real_view = (CheckedTextView) v;
        real_view.toggle();
        updateMap();
    }

    public void expandFilter() {
        View v = findViewById(R.id.ExpandFilter);
        ((CheckedTextView) v).toggle();
        int new_view = (((CheckedTextView) v).isChecked()) ? View.VISIBLE : View.GONE;
        findViewById(R.id.FilterHolder).setVisibility(new_view);
    }

    public void filterExpandClicked(View v) {
        expandFilter();
    }

    // Filters end

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addButton:
                    // doStuff
                    startActivity(new Intent(HomeScreenActivity.this, AddBathroomActivity.class));
                    break;
                case R.id.reviewButton:
                    // doStuff
                    startActivity(new Intent(HomeScreenActivity.this, AddReviewActivity.class));
                    break;
                case R.id.searchButton:
                    expandFilter();
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

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        int locationRequestCode = 1000;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},locationRequestCode);
            Log.d("myTag", "No Permission");
        }

        bathroomDb = new DatabaseHelper(this);

        btnAddBathroom = (ImageButton) findViewById(R.id.addButton);
        btnAddBathroom.setOnClickListener(handler);

        bathroomText = (TextView) findViewById(R.id.bathroom_name_text);

        detailsCard = (LinearLayout) findViewById(R.id.details_card);

        reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(handler);

        findViewById(R.id.searchButton).setOnClickListener(handler);

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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.d("myTag", "Location set");

//                    Adding bathroom is centered around the quad location and emulator doesn't grab location data properly
//                     - replaceing with hardcoded lat/long
//                    LatLng currLatLong = new LatLng(location.getLatitude(), location.getLongitude());

                    LatLng quad = new LatLng(40.107519, -88.22722);
                    mMap.addMarker(new MarkerOptions().position(quad).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quad, 17));
                }
            }
        });

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

        findViewById(R.id.Filters).bringToFront();
    }
}