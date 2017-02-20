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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //TODO are my visibilty modifiers good?
    //TODO can I set variables here?

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //TODO store these in a bundle so they are saved during configuration changes
    private static String contactValue;
    private static boolean notificationsSwitchValue;
    private static int frequencyValue;

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
            getContacts2();
        }

        final Button saveBtn = (Button) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //TODO grab state of contact field
                AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
                contactValue = textView.getText().toString();

                //TODO fix this value
                //grab state of frequency field
                EditText frequency = (EditText) findViewById(R.id.enter_frequency);
                //frequencyValue = Integer.parseInt(frequency.getText().toString());

                //grab state of switch
                Switch notificationsSwitch = (Switch) findViewById(R.id.notifcations_switch);
                notificationsSwitchValue = notificationsSwitch.isChecked();

                //TODO found compatibility solution in stackoverflow, where is this in android documentation?
                NotificationManagerCompat n = NotificationManagerCompat.from(getApplicationContext());
                n.notify(1, mBuilder.build());

            }

        });

        //TODO build notification system
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Memorize this!");
        mBuilder.setContentText(contactValue);

    }

    private void getContacts() {

        //TODO what is cursor loader for? I dont seem to need it here?

        Cursor c = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.Data.DISPLAY_NAME }, null, null, null );

        //TODO what if a contact is added while the application is runnning?

        ArrayList contacts = new ArrayList<String>();
        c.moveToFirst();

        while(!c.isAfterLast()) {

            //TODO Why is DISPLAY_NAME string and not just a number to represent the column?
            contacts.add(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            c.moveToNext();
        }

        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contacts);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
        textView.setAdapter(adapter);
    }

    //TODO need to understand this
    private void getContacts2() {

        ArrayList contacts = new ArrayList<String>();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts.add(name + "   " + phoneNumber);

        }

        phones.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contacts);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.enter_contact);
        textView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts2();

            } else {
                Toast.makeText(this, "Need to access your Contacts in order to retrieve a Contact's number for you to memorize", Toast.LENGTH_SHORT).show();
            }

        }
    }


}


