package com.example.events;
import android.content.Context;
import android.net.Uri;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;



import java.io.File;

public class S3Uploader {

    public static void uploadImage(Context context, Uri imageUri) {

        // AWS S3 credentials
        final String ACCESS_ID = "AKIAV3C4ZI4UUPKW4SUM";
        final String SECRET_KEY = "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";
        final String BUCKET_NAME = "data-inventations";
        final String TAG = "S3Uploader";

        Regions REGION = Regions.EU_WEST_1; // Replace YOUR_REGION with the appropriate region (e.g., Regions.US_EAST_1)

        // Create a new S3 client
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_ID, SECRET_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(REGION));

        // Extract the file name from the Uri
        String fileName = imageUri.getLastPathSegment();

        try {
            // Upload the file to S3 bucket
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, new File(imageUri.getPath()));
            s3Client.putObject(putObjectRequest);

            // Successful upload
            // You can handle the success case as per your requirement
        } catch (AmazonServiceException e) {
            // Failed to upload
            e.printStackTrace();

            // You can handle the error case as per your requirement
        }
    } //end



}
