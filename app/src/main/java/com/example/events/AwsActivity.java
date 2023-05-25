package com.example.events;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.event_invitations_app.R;
import com.google.mlkit.vision.common.InputImage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;


public class AwsActivity extends AppCompatActivity implements Serializable, Comparable<Region>, SdkPojo, ToCopyableBuilder<PutObjectRequest.Builder,PutObjectRequest> {

    //Temporary line:
    ImageView imageView;

    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws);

        imageView = findViewById(R.id.temporaryImageView);

        //Retrieve the Uri from the intent
        String imageUriString = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriString);

        //Display image on screen
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Upload imageInput to S3
                    S3Uploader.uploadImage(AwsActivity.this, imageUri);
                    //Your code goes here
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();





    }

    @Override
    public int compareTo(Region o) {
        return 0;
    }

    @Override
    public List<SdkField<?>> sdkFields() {
        return null;
    }

    @Override
    public PutObjectRequest.Builder toBuilder() {
        return null;
    }
}