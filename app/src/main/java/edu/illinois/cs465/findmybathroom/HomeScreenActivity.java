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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

    private Button btnAddBathroom;
    ArrayList<Marker> AllMarkers = new ArrayList<Marker>();
    DatabaseHelper bathroomDb;
    boolean MapReady = false;
    TextView bathroomText;
    TextView amenity1;
    TextView amenity2;
    ImageView checkbox1;
    ImageView checkbox2;
    LinearLayout detailsCard;
    Button reviewButton;
    String bathroomName;
    RatingBar ratingBar;
    int bathroomId;
    ImageView verifiedCheck;
    TextView locationDescription;

    // Filters start

    public boolean shouldShowBathroom(String type, int all_gender, int wheelchair, int diaper, int verified) {
        boolean showBathroom = true;
        if (type.equals("bathroom") && ((CheckedTextView) findViewById(R.id.BathroomType)).isChecked()) {

            if (((CheckedTextView) findViewById(R.id.BathroomGender)).isChecked() && all_gender == 0) {
                showBathroom = false;
            }

            if (((CheckedTextView) findViewById(R.id.BathroomWheelchair)).isChecked() && wheelchair == 0) {
                showBathroom = false;
            }

            if (((CheckedTextView) findViewById(R.id.BathroomDiaper)).isChecked() && diaper == 0) {
                showBathroom = false;
            }

            if (((CheckedTextView) findViewById(R.id.VerifiedChecked)).isChecked() && verified == 0) {
                showBathroom = false;
            }
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
                    Intent i = new Intent(HomeScreenActivity.this, AddReviewActivity.class);
                    i.putExtra("bathroom_name", bathroomName);
                    i.putExtra("id", bathroomId);
                    startActivity(i);
                    break;
//                case R.id.searchButton:
//                    expandFilter();
//                    break;
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

                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("ID"));
                    @SuppressLint("Range") Double latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                    @SuppressLint("Range") Double longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("LOCATION_TYPE"));
                    @SuppressLint("Range") int all_gender = cursor.getInt(cursor.getColumnIndex("IS_ALL_GENDER"));
                    @SuppressLint("Range") int wheelchair = cursor.getInt(cursor.getColumnIndex("IS_WHEELCHAIR_ACCESSIBLE"));
                    @SuppressLint("Range") int diaper = cursor.getInt(cursor.getColumnIndex("HAS_DIAPER_STATION"));
                    @SuppressLint("Range") int verified = cursor.getInt(cursor.getColumnIndex("IS_COMMUNITY_VERIFIED"));

                    if (shouldShowBathroom(type, all_gender, wheelchair, diaper, verified)) {
                        LatLng location = new LatLng(latitude, longitude);

                        Marker bathroomMarker = mMap.addMarker(new MarkerOptions().position(location));
                        bathroomMarker.setTag(id);
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

        btnAddBathroom = (Button) findViewById(R.id.addButton);
        btnAddBathroom.setOnClickListener(handler);

        bathroomText = (TextView) findViewById(R.id.bathroom_name_text);

        amenity1 = (TextView) findViewById(R.id.amenity_1);
        amenity2 = (TextView) findViewById(R.id.amenity_2);
        checkbox1 = (ImageView) findViewById(R.id.checkbox_1);
        checkbox2 = (ImageView) findViewById(R.id.checkbox_2);

        detailsCard = (LinearLayout) findViewById(R.id.details_card);

        reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(handler);

//        findViewById(R.id.searchButton).setOnClickListener(handler);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        verifiedCheck = (ImageView) findViewById(R.id.verified_bathroom_check);

        locationDescription = (TextView) findViewById(R.id.locationDescriptionText);
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
                    Marker currentLocation = mMap.addMarker(new MarkerOptions().position(quad).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    currentLocation.setTag(-1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quad, 17));
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("Range")
            @Override
            public boolean onMarkerClick(Marker marker) {
                bathroomId = (int) marker.getTag();
                Log.v("bathroom id", String.valueOf(bathroomId));
                Cursor cursor = bathroomDb.getReadableDatabase().rawQuery("select * from bathroom_data where ID=" + bathroomId, null);

                if (cursor.moveToFirst()) {
                    bathroomName = cursor.getString(cursor.getColumnIndex("BUILDING_NAME"));
                    float rating = cursor.getFloat(cursor.getColumnIndex("RATING"));
                    String description = cursor.getString(cursor.getColumnIndex("LOCATION_DESCRIPTION"));
                    int isCommunityVerified = cursor.getInt(cursor.getColumnIndex("IS_COMMUNITY_VERIFIED"));

                    int all_gender = cursor.getInt(cursor.getColumnIndex("IS_ALL_GENDER"));
                    int wheelchair = cursor.getInt(cursor.getColumnIndex("IS_WHEELCHAIR_ACCESSIBLE"));
                    int diaper = cursor.getInt(cursor.getColumnIndex("HAS_DIAPER_STATION"));
                    Log.v("homescreen rating", String.valueOf(rating));
                    ratingBar.setRating(rating);
                    bathroomText.setText(bathroomName);
                    locationDescription.setText("• " + description);

                    if (isCommunityVerified == 0) {
                        verifiedCheck.setVisibility(View.INVISIBLE);
                    } else {
                        verifiedCheck.setVisibility(View.VISIBLE);
                    }
                    detailsCard.setVisibility(View.VISIBLE);

                    // Set amenities
                    if (wheelchair == 1 && all_gender == 1) {
                        amenity1.setText("Wheelchair accessible");
                        amenity2.setText("All-gender");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.VISIBLE);
                        checkbox2.setVisibility(View.VISIBLE);
                    } else if (wheelchair == 1 && diaper == 1) {
                        amenity1.setText("All-gender");
                        amenity2.setText("Diaper changing station");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.VISIBLE);
                        checkbox2.setVisibility(View.VISIBLE);
                    } else if (wheelchair == 1 && diaper == 1) {
                        amenity1.setText("Wheelchair accessible");
                        amenity2.setText("Diaper changing station");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.VISIBLE);
                        checkbox2.setVisibility(View.VISIBLE);
                    } else if (wheelchair == 1) {
                        amenity1.setText("Wheelchair accessible");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.GONE);
                        checkbox2.setVisibility(View.GONE);
                    } else if (all_gender == 1) {
                        amenity1.setText("All-gender");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.GONE);
                        checkbox2.setVisibility(View.GONE);
                    } else if (diaper == 1) {
                        amenity1.setText("Diaper changing station");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.VISIBLE);
                        amenity2.setVisibility(View.GONE);
                        checkbox2.setVisibility(View.GONE);
                    } else {
                        amenity1.setText("No amenities recorded");

                        amenity1.setVisibility(View.VISIBLE);
                        checkbox1.setVisibility(View.GONE);
                        amenity2.setVisibility(View.GONE);
                        checkbox2.setVisibility(View.GONE);
                    }

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