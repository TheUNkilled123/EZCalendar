package com.example.ezcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class AddEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Button save = findViewById(R.id.saveButton);
        EditText titleText = findViewById(R.id.editText);
        EditText descText = findViewById(R.id.editText2);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (String.valueOf(titleText.getText()).equals("")){
                    titleText.setText("Title");
                }
                if (String.valueOf(descText.getText()).equals("")){
                    descText.setText("No description provided.");
                }

                Intent passBackIntent = new Intent();
                passBackIntent.putExtra("title",String.valueOf(titleText.getText()));
                passBackIntent.putExtra("desc",String.valueOf(descText.getText()));
                setResult(RESULT_OK, passBackIntent);
                finish();

            }
        });

    }
}