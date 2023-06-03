package com.example.events.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.event_invitations_app.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    ImageView clear, getImage, copy;
    EditText recgText;
    Uri imageUri;
    TextRecognizer textRecognizer;

    //Temporary line:
    ImageView main_imageView;

    //AWS attributes
    private BasicAWSCredentials credentials;
    private AmazonS3Client s3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        getImage = findViewById(R.id.upload_imageView);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //press on Camera button opens two option for upload image: using Gallery app or Camera app
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("myTag", "onActivityResult");


        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {

                    imageUri = data.getData();
                    saveAndTransferImage(imageUri);

            }
            } else {
                    Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
                }
            }



        private void convertAndTransferImage () {

            if (imageUri != null) { // The Uri of the image you want to save

                Log.e("myTag", "ImageUri is not NULL!!!!!!!");
                //Transfer image to AWS ACTIVITY
                Intent intent = new Intent(MainActivity.this, AwsActivity.class);
                intent.putExtra("imageUri", imageUri.toString());
                //finish();
                startActivity(intent);
            }

            }


    private void saveImageToGallery(Bitmap image) {
        // Create a new file to save the image to.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "image.jpg");

// Try to save the image to the file.
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e) {
            // Handle the exception.
        }

// Tell the media scanner to scan the file so that it will be visible in the gallery.
        MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, null, null);
    }


    private void saveAndTransferImage(Uri imageUri) {
        AlertDialog.Builder builder=CreateBuilder();


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentResolver resolver = getContentResolver();

                // Get the `InputStream` from the `Uri`.
                InputStream inputStream = null;
                try {
                    inputStream = resolver.openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapFactory bitmapFactory = new BitmapFactory();
                Bitmap bitmap = bitmapFactory.decodeStream(inputStream);
                saveImageToGallery(bitmap);
                convertAndTransferImage();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do not save the image.
                convertAndTransferImage();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @NonNull
    private AlertDialog.Builder CreateBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView title = new TextView(this);
        title.setText("Save Image");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);
        // Set the message of the `AlertDialog`.
        builder.setMessage("Do you want to save the image?");

        return builder;
    }

}