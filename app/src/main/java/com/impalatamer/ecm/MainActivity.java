package com.impalatamer.ecm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


//Ideas
//change the icon based on whether or not the app is running
//add default text to the "enter contact" field
//be able to add multiple active contacts
//add statistics such as how many times the notification runs and how long it takes for you to learn the contact
//can i use this to help me memorize song lyrics

//To do Primary Importance level


//To do Secondary
//TODO change the ECM app icon title to Memorizer

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String TAG = "MainActivity";

    //TODO 1 store these in a bundle so they are saved during configuration changes?
    //TODO 1 set notification switch in bundle
    private static String contactValue;
    private static boolean notificationsSwitchValue;
    private static int frequencyValue;
    private static Timer timer;
    private static NotificationCompat.Builder nBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 2 make sure to understand this code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        } else {
            getContacts();
        }

        //build notification system
        nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);

        final Button saveBtn = (Button) findViewById(R.id.save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //grab state of contact field
                AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
                contactValue = textView.getText().toString();
                nBuilder.setContentTitle("Remember this: " + contactValue);

                //grab state of frequency field
                EditText frequency = (EditText) findViewById(R.id.enter_frequency);
                frequencyValue = Integer.parseInt(frequency.getText().toString());
                frequencyValue = frequencyValue * 60000; //convert minutes to ms

                //grab state of switch
                Switch notificationsSwitch = (Switch) findViewById(R.id.notifications_switch);
                notificationsSwitchValue = notificationsSwitch.isChecked();

                //TODO 2 is this necessary?
                //TODO 2 do i need to kill thread in onDestroy?
                if(timer != null){
                    timer.cancel();
                    timer.purge();
                }

                if(notificationsSwitchValue == true){

                    timer = new Timer(true); //TODO isDaemon = true, does this enhance performance?

                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {

                            NotificationManagerCompat n = NotificationManagerCompat.from(getApplicationContext());
                            n.notify(1, nBuilder.build());

                        }
                    };

                    timer.scheduleAtFixedRate(tt, frequencyValue, frequencyValue);
                }

                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getContacts() {

        ArrayList contacts = new ArrayList<String>();

        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cur.moveToNext())
        {
            String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts.add(name + " " + phoneNumber);

        }

        cur.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contacts);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
        textView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();

            } else {
                Toast.makeText(this, "Need to access your Contacts in order to retrieve information for you to memorize", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    


}


