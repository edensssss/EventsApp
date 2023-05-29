package com.example.events.Convertors;

public class JsonConverter {


    public static String getFileName(String filePath) {

        final String suffix = ".json";

        // Extract the file name from the file path
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1); // Get the substring after the last '/'
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); // Remove the file extension

        //add suffix to filePath
        fileName += suffix;

        return fileName;
    }
}
