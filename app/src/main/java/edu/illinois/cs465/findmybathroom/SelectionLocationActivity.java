package edu.illinois.cs465.findmybathroom;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.illinois.cs465.findmybathroom.databinding.ActivitySelectionLocationBinding;

public class SelectionLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySelectionLocationBinding binding;

    private Button btnYes;
    private Button btnNo;

    Marker newBathroomMarker;
    DatabaseHelper bathroomDb;

    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            Bundle extras = getIntent().getExtras();
            String location_type = extras.getString("location_type");
            double latitude = newBathroomMarker.getPosition().latitude;
            double longitude = newBathroomMarker.getPosition().longitude;
            String building_name = extras.getString("building_name");
            int is_all_gender = extras.getInt("is_all_gender");
            int is_wheelchair_accessible = extras.getInt("is_wheelchair_accessible");
            int has_diaper_station = extras.getInt("has_diaper_station");
            String location_description = extras.getString("location_description");

            switch (v.getId()) {
                case R.id.yesButton:
                    // doStuff
                    bathroomDb.insertData(location_type, latitude, longitude, building_name, is_all_gender, is_wheelchair_accessible, has_diaper_station, location_description);
                    startActivity(new Intent(SelectionLocationActivity.this, HomeScreenActivity.class));
                    break;
                case R.id.noButton:
                    // doStuff
                    startActivity(new Intent(SelectionLocationActivity.this, HomeScreenActivity.class));
                    break;
            }
        }
    };

    GoogleMap.OnMarkerDragListener dragListener = new GoogleMap.OnMarkerDragListener (){

        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectionLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnYes = (Button) findViewById(R.id.yesButton);
        btnYes.setOnClickListener(handler);

        btnNo = (Button) findViewById(R.id.noButton);
        btnNo.setOnClickListener(handler);

        bathroomDb = new DatabaseHelper(this);
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
        mMap.setOnMarkerDragListener(dragListener);

        // Replace with user's current location later
        LatLng quad = new LatLng(40.107519, -88.22722);
        newBathroomMarker = mMap.addMarker(new MarkerOptions().position(quad).title("Marker on the quad").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quad, 17));
    }
}