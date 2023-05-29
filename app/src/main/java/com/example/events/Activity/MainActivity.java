package com.example.events.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.event_invitations_app.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ImageView clear, getImage, copy;
    EditText recgText;
    Uri imageUri;
    TextRecognizer textRecognizer;

    ///Temp:
    //Temporary line:
    ImageView main_imageView;

    //AWS attributes
    private BasicAWSCredentials credentials;
    private AmazonS3Client s3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getImage = findViewById(R.id.getImage);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        main_imageView = findViewById(R.id.main_imageView);

        //press on Camera button opens two option for upload image: using Gallery app or Camera app
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("myTag", "onActivityResult");


        if (resultCode == Activity.RESULT_OK){

            if(data != null){

                imageUri = data.getData();

                //Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();

                convertAndTransferImage();
            }
            else {
                Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void convertAndTransferImage() {

        if (imageUri != null){ // The Uri of the image you want to save

            Log.e("myTag", "ImageUri is not NULL!!!!!!!");

            //Display image on screen SUCCESSFULLY
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                main_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Transfer image to AWS ACTIVITY
            Intent intent = new Intent(MainActivity.this, AwsActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            //finish();
            startActivity(intent);



        }
    }
}