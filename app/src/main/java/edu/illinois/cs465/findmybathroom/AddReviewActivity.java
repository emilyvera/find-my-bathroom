package edu.illinois.cs465.findmybathroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class AddReviewActivity extends FragmentActivity {
    Button btnYes, btnNo;
    TextView bathroomNameText;
    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yesButton:
                case R.id.noButton:
                    // doStuff
                    startActivity(new Intent(AddReviewActivity.this, HomeScreenActivity.class));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        Bundle extras = getIntent().getExtras();
        String bathroomName = extras.getString("bathroom_name");

        btnYes = (Button) findViewById(R.id.yesButton);
        btnNo = (Button) findViewById(R.id.noButton);
        btnYes.setOnClickListener(handler);
        btnNo.setOnClickListener(handler);

        bathroomNameText = (TextView) findViewById(R.id.bathroom_name_review_page);
        bathroomNameText.setText(bathroomName);
    }
}