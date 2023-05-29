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

import java.io.BufferedReader;
import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.FileReader;
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
                    Log.e("File name imageUri", imageUri.toString());

                    //Upload imageInput to S3
                    S3Uploader.uploadImage(AwsActivity.this, imageUri);




                    String jsonFileName = JsonConverter.getFileName(imageUri.toString());
                    Log.e("File path ti S3", jsonFileName.toString());

                    String filePath  = S3Downloader.downloadJsonFile(AwsActivity.this, jsonFileName);

                    // Log the JSON content
                    Log.e("JSON Content", filePath.toString());


                    String fileContent = readFileContent(filePath);
                    Log.d("JSON Content", fileContent);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();





    }


    private String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
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