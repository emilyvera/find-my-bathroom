package edu.illinois.cs465.findmybathroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddBathroomActivity extends AppCompatActivity {

    private Button btnNext;

    private CheckBox is_bathroom;
//    private CheckBox is_gas_station;
    private EditText building_name;
    private CheckBox is_all_gender;
    private CheckBox is_wheelchair_accessible;
    private CheckBox has_diaper_station;

    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nextButton:
                    // doStuff
                    Intent i = new Intent(AddBathroomActivity.this, SelectionLocationActivity.class);
                    i.putExtra("location_type", is_bathroom.isChecked() ? "bathroom" : "gas station");
                    i.putExtra("building_name", building_name.getText().toString());
                    i.putExtra("is_all_gender", is_all_gender.isChecked() ? 1 : 0);
                    i.putExtra("is_wheelchair_accessible", is_wheelchair_accessible.isChecked() ? 1 : 0);
                    i.putExtra("has_diaper_station", has_diaper_station.isChecked() ? 1 : 0);

                    startActivity(i);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);

        btnNext = (Button) findViewById(R.id.nextButton);
        btnNext.setOnClickListener(handler);

        is_bathroom = (CheckBox) findViewById(R.id.checkbox_bathroom);
        building_name = (EditText) findViewById(R.id.editTextBuildingName);
        is_all_gender = (CheckBox) findViewById(R.id.checkbox_all_gender);
        is_wheelchair_accessible = (CheckBox) findViewById(R.id.checkbox_wheelchair_accessible);
        has_diaper_station = (CheckBox) findViewById(R.id.checkbox_diaper_station);
    }
}