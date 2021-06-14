package com.example.ezcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.Date;

public class EventDesc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_desc);

        TextView titleTV = findViewById(R.id.titleTV);
        TextView descTV = findViewById(R.id.descTV);
        TextView dateTV = findViewById(R.id.createdAtTV);
        MaterialButton baton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String date = "Reminder created at ";
        date = date + intent.getStringExtra("time");

        date = date.substring(0, date.indexOf("GMT"));
        titleTV.setText(title);
        descTV.setText(desc);
        dateTV.setText(date);

        baton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }

}