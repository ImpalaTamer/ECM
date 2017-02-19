package com.impalatamer.ecm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

                //grab state of frequency field
                EditText frequency = (EditText) findViewById(R.id.enter_frequency);
                frequencyValue = Integer.parseInt(frequency.getText().toString());

                //grab state of switch
                Switch notificationsSwitch = (Switch) findViewById(R.id.notifcations_switch);
                notificationsSwitchValue = notificationsSwitch.isChecked();

            }

        });


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


