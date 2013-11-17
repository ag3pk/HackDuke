/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.example.conferencephone.ContactNumbersListAdapter.ContactNumber;

public class AddCallsActivity extends Activity
{
    private static final String TAG = "AddCallsActivity";
    private static final int CODE_PICK_CONTACT = 1000;

    private Button makeCallsButton;
    private ListView listView;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.add_calls);

        listView = (ListView)findViewById(R.id.participant_list);
        listView.setAdapter(new ParticipantListAdapter(this));

        makeCallsButton = (Button)findViewById(R.id.make_calls_button);
        makeCallsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                List<Participant> participants = ((ParticipantListAdapter)listView.getAdapter()).getParticipants();
                ConferencePhoneApplication.getInstance().getPhone().performCalls(participants);
                finish();
            }
        });

        final EditText nameField = (EditText)findViewById(R.id.name_field);
        nameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    String contactString = nameField.getText().toString().trim();
                    if (contactString.length() == 0)
                        return false;

                    addParticipant(contactString);
                    nameField.setText(null);

                    return true;
                }

                return false;
            }
        });

        ImageButton contactsButton = (ImageButton)findViewById(R.id.add_contact_button);
        contactsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_PICK_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_PICK_CONTACT && data != null) {
            Uri contactUri = data.getData();
            if (contactUri != null)
                addNumberFromContact(contactUri);
        }
    }

    private String getTypeString(int type, String otherLabel)
    {
        switch (type) {
            case ContactsContract.CommonDataKinds.BaseTypes.TYPE_CUSTOM:
                return otherLabel != null ? otherLabel : getString(R.string.other);
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.home);
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getString(R.string.mobile);
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.work);
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.other);
            default:
                return null;
        }
    }

    private void addNumberFromContact(Uri contactUri)
    {
        Cursor contact = getContentResolver().query(contactUri, new String[] { ContactsContract.Contacts._ID }, null, null, null);
        Cursor numbers = null;

        try {
            if (!contact.moveToFirst())
                return;

            long contactId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));

            Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = {
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
            };
            String selection = ContactsContract.Data.CONTACT_ID + "=?";
            String[] selectionArgs = { String.valueOf(contactId) };
            numbers = getContentResolver().query(phoneUri, projection, selection, selectionArgs, null);

            int typeIndex = numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            int labelIndex = numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
            int numberIndex = numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            final List<ContactNumber> contactNumbers = new LinkedList<ContactNumber>();

            while (numbers.moveToNext()) {
                int type = numbers.getInt(typeIndex);
                String typeString = getTypeString(type, numbers.getString(labelIndex));
                if (typeString == null)
                    continue;

                String number = numbers.getString(numberIndex);
                if (number != null) {
                    ContactNumber cn = new ContactNumber(typeString, number);
                    if (contactNumbers.indexOf(cn) == -1)
                        contactNumbers.add(cn);
                }
            }

            if (contactNumbers.size() == 1)
                addParticipant(contactNumbers.get(0).numberString);
            else {
                new AlertDialog.Builder(this)
                    .setTitle(R.string.select_number)
                    .setAdapter(new ContactNumbersListAdapter(this, contactNumbers), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            addParticipant(contactNumbers.get(which).numberString);
                        }
                    }).create().show();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to add contact: " + e.getLocalizedMessage());
        } finally {
            if (numbers != null)
                numbers.close();
            if (contact != null)
                contact.close();
        }
    }

    private static boolean isPhoneNumber(String string)
    {
        return string.matches("^\\+?[0123456789() -]+$");
    }

    private void addParticipant(String contactString)
    {
        Participant participant = null;

        if (isPhoneNumber(contactString))
            participant = new Participant(contactString, Participant.Type.PSTN);
        else
            participant = new Participant(contactString, Participant.Type.CLIENT);

        ((ParticipantListAdapter)listView.getAdapter()).add(participant);

        makeCallsButton.setEnabled(true);
    }
}
