package com.example.events.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.view.View;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.CalendarContract;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.example.event_invitations_app.R;

public class CalendarActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText locationEditText;
    private EditText  startDatetimeEditText;
    private EditText  endDatetimeEditText;
    private EditText descriptionEditText;
    private static final int PERMISSION_REQUEST_WRITE_CALENDAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander);

        //Retrieve the Uri from the intent
        String jsonFileContent = getIntent().getStringExtra("jsonFileContent");

        Log.e("CALENDAR ACTIVITY: JSON Content", jsonFileContent);

        TextInputLayout titleInputLayout = findViewById(R.id.titleTextView);
        titleEditText = titleInputLayout.getEditText();
        TextInputLayout locationInputLayout = findViewById(R.id.locationTextView);
        locationEditText = locationInputLayout.getEditText();
        TextInputLayout startDatetimeInputLayout = findViewById(R.id.startdatetimeTextView);
        startDatetimeEditText = startDatetimeInputLayout.getEditText();
        TextInputLayout endDatetimeInputLayout = findViewById(R.id.enddatetimeTextView);
        endDatetimeEditText = endDatetimeInputLayout.getEditText();
        TextInputLayout descriptionInputLayout = findViewById(R.id.descriptionTextView);
        descriptionEditText = descriptionInputLayout.getEditText();


        try {
            //String jsonString = loadJSONFromAsset("event.json");
            JSONObject eventJson = new JSONObject(jsonFileContent);

            // Retrieve event details from JSON object
            String title = eventJson.getString("title");
            String location = eventJson.getString("location");
            String startDateTime = eventJson.getString("startDateTime");
            String endDateTime = eventJson.getString("endDateTime");
            String description = eventJson.getString("description");


            // Set the event details to the TextViews
            titleEditText.setText(title);
            locationEditText.setText(location);
            startDatetimeEditText.setText(startDateTime);
            endDatetimeEditText.setText(endDateTime);
            descriptionEditText.setText(description);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a reusable OnClickListener for date and time selection
        View.OnClickListener dateTimeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                Calendar selectedCalendar = Calendar.getInstance();
                                                selectedCalendar.set(year, month, dayOfMonth, hourOfDay, minute);

                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                                String selectedDateTime = dateFormat.format(selectedCalendar.getTime());

                                                // Determine which EditText was clicked and set the selected date/time
                                                if (v == startDatetimeEditText) {
                                                    startDatetimeEditText.setText(selectedDateTime);
                                                } else if (v == endDatetimeEditText) {
                                                    endDatetimeEditText.setText(selectedDateTime);
                                                }
                                            }
                                        }, hour, minute, false);

                                timePickerDialog.show();
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        };

        // Set the OnClickListener for start and end date/time fields
        startDatetimeEditText.setOnClickListener(dateTimeClickListener);
        endDatetimeEditText.setOnClickListener(dateTimeClickListener);

        ImageView addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for calendar write permission
                if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    addEventToCalendar();
                } else {
                    // Request permission
                    requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_WRITE_CALENDAR);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEventToCalendar();
            } else {
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void addEventToCalendar() {


        try {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String startDateTimeStr = startDatetimeEditText.getText().toString();
            String endDateTimeStr = endDatetimeEditText.getText().toString();

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            Date startDateTime = format.parse(startDateTimeStr);
            Date endDateTime = format.parse(endDateTimeStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDateTime);

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.DESCRIPTION, description);
            values.put(CalendarContract.Events.EVENT_LOCATION, location);
            values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endDateTime.getTime());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            // Insert the event
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ) {
                if (endDateTime.getTime() > startDateTime.getTime()) {
                    {
                        cr.insert(CalendarContract.Events.CONTENT_URI, values);
                        // Open new activity with the title "Event added to calendar"
                        Intent intent = new Intent(this, InvitationSuccessActivity.class);
                        intent.putExtra("title", "Event added to calendar");
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "end date time can't be before start date time", Toast.LENGTH_SHORT).show();

                }
            }
            else {
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
        }
    }


    public String loadJSONFromAsset(String filename) {
        String jsonString;
        try {
            InputStream inputStream = getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;


    }
}