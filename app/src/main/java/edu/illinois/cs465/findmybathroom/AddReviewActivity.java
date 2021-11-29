package edu.illinois.cs465.findmybathroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class AddReviewActivity extends FragmentActivity {
    Button btnYes, btnNo;
    TextView bathroomNameText;
    DatabaseHelper bathroomDb;
    RatingBar ratingBar;
    ImageView verifiedCheck;

    View.OnClickListener handler = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yesButton:
                    // doStuff
                    Bundle extras = getIntent().getExtras();
                    int id = extras.getInt("id");

                    bathroomDb.updateRating(id, ratingBar.getRating());
                    startActivity(new Intent(AddReviewActivity.this, HomeScreenActivity.class));
                    break;
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

        bathroomDb = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();
        String bathroomName = extras.getString("bathroom_name");

        btnYes = (Button) findViewById(R.id.yesButton);
        btnNo = (Button) findViewById(R.id.noButton);
        btnYes.setOnClickListener(handler);
        btnNo.setOnClickListener(handler);

        bathroomNameText = (TextView) findViewById(R.id.bathroom_name_review_page);
        bathroomNameText.setText(bathroomName);

        ratingBar = (RatingBar) findViewById(R.id.simpleRatingBar);

        verifiedCheck = (ImageView) findViewById(R.id.verified_check);
        int id = extras.getInt("id");
        Cursor cursor = bathroomDb.getReadableDatabase().rawQuery("select * from bathroom_data where ID=" + id, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int isCommunityVerified = cursor.getInt(cursor.getColumnIndex("IS_COMMUNITY_VERIFIED"));
            if (isCommunityVerified == 0) {
                verifiedCheck.setVisibility(View.INVISIBLE);
            } else {
                verifiedCheck.setVisibility(View.VISIBLE);
            }
        }
        cursor.close();
    }
}