package edu.illinois.cs465.findmybathroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddBathroomActivity extends AppCompatActivity {

    private Button btnNext;

    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nextButton:
                    // doStuff
                    startActivity(new Intent(AddBathroomActivity.this, SelectionLocationActivity.class));
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
    }
}