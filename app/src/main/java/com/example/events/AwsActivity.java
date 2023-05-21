package com.example.events;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.event_invitations_app.R;
import com.google.mlkit.vision.common.InputImage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;

public class AwsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws);

        // Retrieve the image URI from the Intent
        String imageUriString = getIntent().getStringExtra("imageUri");

        // Create the InputImage object from the image URI
        InputImage inputImage = InputImage.fromFilePath(AwsActivity.this, Uri.parse(imageUriString));



        //Upload imageInput to S3

        // Replace the placeholders with your access key ID, secret access key, and desired AWS region
        String accessKey = Constants.ACCESS_ID;
        String secretKey = Constants.SECRET_KEY;
        String region = Constants.region;

        // Create an AwsBasicCredentials object with your credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Create an S3Client with the configured credentials and desired AWS region
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .build();

        // Specify the S3 bucket name and key for the uploaded file
        String bucketName = Constants.BUCKET_NAME;
        String key = "image.jpg";

        // Create a File object with the path to the picture you want to upload
        File fileToUpload = new File("C:\\Users\\edens\\AndroidStudioProjects\\Events\\app\\src\\main\\res\\drawable-v24\\img.jpg");

        // Create a PutObjectRequest with the bucket name, key, and file to upload
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // Upload the file to Amazon S3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s3Client.putObject(putObjectRequest, fileToUpload.toPath());
        }

    }
}