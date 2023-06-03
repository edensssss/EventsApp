package com.example.events.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.event_invitations_app.R;

public class InvitationFailedActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_failed);


        ImageView tryAgainIV = findViewById(R.id.try_again_imageView);

        tryAgainIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open new activity with the title "Event added to calendar"
                Intent intent = new Intent(InvitationFailedActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


    }




}