package com.example.events.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.event_invitations_app.R;

public class LastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        // Get the title from the intent
        String title = getIntent().getStringExtra("title");

        // Display the title in a TextView
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title);


        ImageView niceImageView = findViewById(R.id.nice_imageView);
        niceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finish(); // Close the current activity
                // If you want to close the entire application, use System.exit(0);
                //System.exit(0);


                // Open new activity with the title "Event added to calendar"
                Intent intent = new Intent(LastActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


    }
}