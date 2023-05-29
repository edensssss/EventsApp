package com.example.events;

import android.content.Context;
import android.os.Environment;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import software.amazon.awssdk.core.exception.SdkClientException;


public class S3Downloader {

    public static String downloadJsonFile(Context context, String jsonFileKey) {


        // AWS S3 credentials
        final String ACCESS_ID = "AKIAV3C4ZI4UUPKW4SUM";
        final String SECRET_KEY = "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";
        final String BUCKET_NAME = "data-inventations";
        final String TAG = "S3Uploader";

        Regions REGION = Regions.EU_WEST_1; // Replace YOUR_REGION with the appropriate region (e.g., Regions.US_EAST_1)




        String downloadedFilePath = null;
        boolean fileFound = false;

        // Create a new S3 client
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_ID, SECRET_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(REGION));


        // Specify the S3 bucket name
        String bucketName = "data-inventations";

        // Specify the local directory to save the downloaded file
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create the file object with the specified file key
        File downloadedFile = new File(downloadDirectory, jsonFileKey);

        while (!fileFound) {
            try {
                // Check if the file exists in the bucket
                boolean fileExists = s3Client.doesObjectExist(bucketName, jsonFileKey);

                if (fileExists) {
                    // Download the file from S3
                    S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, jsonFileKey));

                    // Get the input stream from the S3 object
                    InputStream inputStream = s3Object.getObjectContent();

                    // Create the output stream to write the file locally
                    OutputStream outputStream = new FileOutputStream(downloadedFile);

                    // Copy the contents of the input stream to the output stream
                    IOUtils.copy(inputStream, outputStream);

                    // Close the streams
                    inputStream.close();
                    outputStream.close();

                    downloadedFilePath = downloadedFile.getAbsolutePath();
                    fileFound = true;
                } else {
                    // Sleep for a certain duration before checking again
                    Thread.sleep(1000); // Adjust the sleep duration as needed
                }
            } catch (AmazonServiceException e) {
                // Catch Amazon S3 service exceptions
                e.printStackTrace();
            } catch (SdkClientException | IOException | InterruptedException e) {
                // Catch errors during communication with the SDK, IO errors, or interrupted sleep
                e.printStackTrace();
            }
        }

        return downloadedFilePath;
    }


    }
