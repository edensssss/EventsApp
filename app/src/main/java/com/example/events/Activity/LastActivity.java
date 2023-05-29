package com.example.events.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    }
}