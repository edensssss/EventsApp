package com.example.events;

public class JsonConverter {


    public static String getFileName(String filePath) {

        final String suffix = ".json";
        final String prefix = "/";

        //https://data-inventations.s3.eu-west-1.amazonaws.com/IMG_20230527_151535022.json

        // Extract the file name from the file path
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1); // Get the substring after the last '/'
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); // Remove the file extension

        //add prefix to filePath
        //fileName = prefix + fileName;

        //add suffix to filePath
        fileName += suffix;


        return fileName;
    }
}
