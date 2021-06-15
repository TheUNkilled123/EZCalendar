package com.example.ezcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_REQUEST_CODE = 0;

    Calendar cal = Calendar.getInstance();
    Calendar cal1 = Calendar.getInstance();
    Date newDate = cal.getTime();

    FirebaseFirestore db;
    ListView lv;
    ArrayList<String> dataList;
    ArrayAdapter<String> listAdapter;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        lv = findViewById(R.id.listView);
        dataList = new ArrayList<String>();
        //Array adapter za ListView događaja
        listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, dataList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                // Set the text color of TextView (ListView Item)
                tv.setMaxWidth(200);
                tv.setTextColor(Color.BLACK);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tv.setAutoSizeTextTypeUniformWithConfiguration(1, 22, 1, TypedValue.COMPLEX_UNIT_DIP);
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),EventDesc.class);
                        db.collection("events")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(((String) tv.getText()).equals((String) document.get("eventTitle"))){
                                                intent.putExtra("desc",(String) document.get("eventDesc"));
                                                intent.putExtra("title",(String) tv.getText());
                                                Timestamp datetemp = (Timestamp) document.get("dateCreated");
                                                intent.putExtra("time", datetemp.toDate().toString());
                                                startActivity(intent);
                                                break;
                                            }
                                        }
                                    } else {
                                        Log.w("OUTPUT", "Error getting documents.", task.getException());
                                    }
                                });

                    }
                });

                // Generate ListView Item using TextView
                return view;
            }
        };
        lv.setDividerHeight(20);
        lv.setAdapter(listAdapter);

        CalendarView calView = findViewById(R.id.calendarView);
        ExtendedFloatingActionButton baton = findViewById(R.id.addEvent);
        calView.setDate(cal.getTimeInMillis());

        //Fires when date changed in the calendar
        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                cal.set(year, month, dayOfMonth);
                newDate = cal.getTime();
                dataList.clear();
                listAdapter.notifyDataSetChanged();
                //Get data from FireStore
                cal.set(Calendar.SECOND, 1);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);

                cal1.setTime(cal.getTime());
                cal1.add(cal1.DATE,1);
                db.collection("events")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Timestamp ts1= (Timestamp) document.getData().get("date");
                                    if(ts1.getSeconds() >= cal.getTimeInMillis()/1000 && ts1.getSeconds() <= cal1.getTimeInMillis()/1000){
                                        String titleValue = (String) document.getData().get("eventTitle");
                                        dataList.add(titleValue);
                                    }
                                }
                                listAdapter.notifyDataSetChanged();
                            } else {
                                Log.w("OUTPUT", "Error getting documents.", task.getException());
                            }
                        });

            }});

        baton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getDataIntent = new Intent(getApplicationContext(), AddEvent.class);
                startActivityForResult(getDataIntent, ACTIVITY_REQUEST_CODE);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String titleString = data.getStringExtra("title");
                String descString = data.getStringExtra("desc");

                cal.set(Calendar.SECOND, 1);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);

                cal1.setTime(cal.getTime());
                cal1.add(cal1.DATE,1);

                Map<String, Object> event = new HashMap<>();
                event.put("date", newDate);
                event.put("eventTitle", titleString);
                event.put("eventDesc", descString);
                event.put("dateCreated", Calendar.getInstance().getTime());

                db.collection("events")
                        .add(event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("E", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("E", "Error adding document", e);
                            }
                        });


                db.collection("events")
                        //
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dataList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Timestamp ts1= (Timestamp) document.getData().get("date");
                                    if(ts1.getSeconds() >= cal.getTimeInMillis()/1000 && ts1.getSeconds() <= cal1.getTimeInMillis()/1000){
                                        String titleValue = (String) document.getData().get("eventTitle");
                                        dataList.add(titleValue);                                    }
                                }
                                listAdapter.notifyDataSetChanged();
                            } else {
                                Log.w("OUTPUT", "Error getting documents.", task.getException());
                            }
                        });
            }
        }
    }
}


//Calendar.getInstance().set(year, month, day, hour, minutes, seconds);


// Saving data
/*
Map<String, Object> event = new HashMap<>();
        event.put("date", "10/06/2021");
                event.put("eventTitle", "Zubar");
                event.put("eventDesc", "Kao nesto moras kod zubara ali samo ako te boli zub ili nesto test droime skaldjasdjaksjdkasjd.");


                db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
@Override
public void onSuccess(DocumentReference documentReference) {
        Log.d("E", "DocumentSnapshot added with ID: " + documentReference.getId());
        }
        })
        .addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        Log.w("E", "Error adding document", e);
        }
        });

 */

//Reading data

/*
db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("OUTPUT", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w("OUTPUT", "Error getting documents.", task.getException());
                    }
                });
*/