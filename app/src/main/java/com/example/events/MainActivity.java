package com.example.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.example.event_invitations_app.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    ImageView clear, getImage, copy;
    EditText recgText;
    Uri imageUri;
    TextRecognizer textRecognizer;

    //AWS attributes
    private BasicAWSCredentials credentials;
    private AmazonS3Client s3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = findViewById(R.id.clear);
        getImage = findViewById(R.id.getImage);
        copy = findViewById(R.id.copy);
        recgText = findViewById(R.id.recgText);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //press on Camera button opens two option for upload image: using Gallery app or Camera app
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
               //onActivityResult();
            }
        });


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = recgText.getText().toString();

                if (text.isEmpty()) {

                    Toast.makeText(MainActivity.this, "Tere is no text to copy", Toast.LENGTH_SHORT).show();
                }
                else{
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Data", recgText.getText().toString());
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(MainActivity.this, "Text copy to Clipboard", Toast.LENGTH_SHORT).show();

                    }

            }
        });



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = recgText.getText().toString();

                if (text.isEmpty()){

                    Toast.makeText(MainActivity.this, "There is no text to clear", Toast.LENGTH_SHORT).show();

                }else{
                    recgText.setText("");
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Activity.RESULT_OK){
            Log.d("myTag", "Image accept");

            if(data != null){

                imageUri = data.getData();
                Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();

                recognizeText();
            }
            else {
                Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recognizeText() {

        if (imageUri != null){

            Intent intent = new Intent(MainActivity.this, AwsActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            finish();
            startActivity(intent);

        }
    }
}