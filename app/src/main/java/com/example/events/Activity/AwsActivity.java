package com.example.events.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.event_invitations_app.R;
import com.example.events.Convertors.JsonConverter;
import com.example.events.AWS_S3.S3Downloader;
import com.example.events.AWS_S3.S3Uploader;

import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

import java.io.BufferedReader;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.regions.Region;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
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

                    //Get image name from imageUri using JsonConverter
                    String jsonFileName = JsonConverter.getFileName(imageUri.toString());

                    //Download json file from S3
                    String jsonFilePath  = S3Downloader.downloadJsonFile(AwsActivity.this, jsonFileName);

                    //Print event details
                    String jsonFileContent = readFileContent(jsonFilePath);
                    Log.e("JSON Content", jsonFileContent);


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