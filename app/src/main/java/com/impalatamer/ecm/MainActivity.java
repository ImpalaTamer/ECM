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

public class MainActivity extends AppCompatActivity {

    //TODO are my visibilty modifiers good?
    //TODO can I set variables here?

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //TODO store these in a bundle so they are saved during configuration changes
    private static String contactValue;
    private static boolean notificationsSwitchValue;
    private static int frequencyValue;

    private static Timer timer;

    NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO set notifications switch to state stored in bundle

        //TODO make sure to understand this code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        } else {
            getContacts();
        }

        final Button saveBtn = (Button) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //TODO grab state of contact field
                AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
                contactValue = textView.getText().toString();
                mBuilder.setContentTitle("Remember this: " + contactValue);
                //mBuilder.setContentText("Remember this: " + contactValue); //TODO why do i need this here?

                //TODO fix this value
                //TODO make input field always center
                //grab state of frequency field
                EditText frequency = (EditText) findViewById(R.id.enter_frequency);
                frequencyValue = Integer.parseInt(frequency.getText().toString());
                frequencyValue = frequencyValue * 60000; //convert minutes to ms

                //grab state of switch
                Switch notificationsSwitch = (Switch) findViewById(R.id.notifications_switch);
                notificationsSwitchValue = notificationsSwitch.isChecked();

                //TODO is this necessary?
                if(timer != null){
                    timer.cancel();
                    timer.purge();
                }

                timer = new Timer(true); //TODO isDaemon = true, does this enhance performance?

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {

                        //TODO found compatibility solution in stackoverflow, where is this in android documentation?
                        NotificationManagerCompat n = NotificationManagerCompat.from(getApplicationContext());
                        n.notify(1, mBuilder.build());

                        //TODO how come i can access mBuilder here?
                    }
                };

                timer.scheduleAtFixedRate(tt, frequencyValue, frequencyValue);

                //toast feedback when save button is pressed
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }

        });

        //TODO build notification system
        Toast.makeText(getApplicationContext(), "onCreate notification builder", Toast.LENGTH_SHORT);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //mBuilder.setContentTitle("hmm");
        //mBuilder.setContentText("Memorize this: " + contactValue);

    }

    //TODO need to understand this, apparently it is much faster query
    private void getContacts() {

        ArrayList contacts = new ArrayList<String>();

        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

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
                Toast.makeText(this, "Need to access your Contacts in order to retrieve a Contact's number for you to memorize", Toast.LENGTH_SHORT).show();
            }

        }
    }


}


