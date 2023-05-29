package com.example.events.AWS_S3;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.example.events.Activity.AwsActivity;
import com.example.events.Activity.MainActivity;

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

        try {
            // Specify the S3 bucket name

            // Specify the local directory to save the downloaded file
            File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create the file object with the specified file key
            File downloadedFile = new File(downloadDirectory, jsonFileKey);

            // Define the timeout duration in milliseconds (6 seconds)
            long timeoutDuration = 6000;

            // Get the start time
            long startTime = System.currentTimeMillis();

            // Download the file from S3 with a timeout
            while (System.currentTimeMillis() - startTime < timeoutDuration) {
                try {
                    // Check if the file exists in the S3 bucket
                    if (s3Client.doesObjectExist(BUCKET_NAME, jsonFileKey)) {
                        // File found, proceed with downloading

                        // Download the file from S3
                        S3Object s3Object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, jsonFileKey));

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

                        // Break the loop since file was successfully downloaded
                        break;
                    }
                } catch (AmazonServiceException e) {
                    // Catch Amazon S3 service exceptions
                    e.printStackTrace();
                } catch (IOException e) {
                    // Catch IO errors
                    e.printStackTrace();
                }

                // Sleep for a short duration before retrying
                Thread.sleep(100);
            }
        } catch (SdkClientException | InterruptedException e) {
            // Catch errors during communication with the SDK or interruption errors
            e.printStackTrace();
        }

        return downloadedFilePath;
    }

}