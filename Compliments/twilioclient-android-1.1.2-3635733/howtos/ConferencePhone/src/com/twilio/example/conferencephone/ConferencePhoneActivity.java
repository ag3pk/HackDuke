/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class ConferencePhoneActivity extends Activity implements View.OnClickListener,
                                                                 ConferencePhone.LoginListener,
                                                                 ConferencePhone.ConferenceListener
{
    private static final Handler handler = new Handler();

    private ConferencePhone phone;

    private ImageButton dialButton;
    private ImageButton hangupButton;
    private ImageButton addCallButton;
    private EditText conferenceNameField;
    private EditText logTextBox;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dialButton = (ImageButton)findViewById(R.id.dial_button);
        dialButton.setOnClickListener(this);
        hangupButton = (ImageButton)findViewById(R.id.hangup_button);
        hangupButton.setOnClickListener(this);
        addCallButton = (ImageButton)findViewById(R.id.add_call_button);
        addCallButton.setOnClickListener(this);

        conferenceNameField = (EditText)findViewById(R.id.conference_name_field);
        logTextBox = (EditText)findViewById(R.id.log_text_box);

        ConferencePhoneApplication.getInstance().initialize();
        phone = ConferencePhoneApplication.getInstance().getPhone();
        phone.setListeners(this, this);
        phone.login();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        phone.setListeners(null, null);
        phone = null;
    }

    @Override
    public void onClick(View view)
    {
        int viewId = view.getId();

        if (viewId == R.id.dial_button)
            handleDial();
        else if (viewId == R.id.hangup_button)
            handleHangup();
        else if (viewId == R.id.add_call_button)
            handleAddCall();
    }

    private void handleDial()
    {
        phone.connect(conferenceNameField.getText().toString());
    }

    private void handleHangup()
    {
        phone.disconnect();
    }

    private void handleAddCall()
    {
        startActivity(new Intent(this, AddCallsActivity.class));
    }

    private void addStatusMessage(final String message)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                logTextBox.append('-' + message + '\n');
            }
        });
    }

    private void addStatusMessage(int stringId)
    {
        addStatusMessage(getString(stringId));
    }

    private void syncButtons()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (phone.isConnected()) {
                    dialButton.setEnabled(false);
                    hangupButton.setEnabled(true);
                    addCallButton.setEnabled(true);
                } else {
                    dialButton.setEnabled(true);
                    hangupButton.setEnabled(false);
                    addCallButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onConferenceConnecting()
    {
        addStatusMessage(R.string.attempting_to_connect);
        syncButtons();
    }

    @Override
    public void onConferenceConnected()
    {
        addStatusMessage(R.string.connected);
        syncButtons();
    }

    @Override
    public void onConferenceConnectionFailed(Exception error)
    {
        addStatusMessage(String.format(getString(R.string.conference_error_fmt), error.getLocalizedMessage()));
        syncButtons();
    }

    @Override
    public void onConferenceDisconnecting()
    {
        addStatusMessage(R.string.disconnect_attempt);
        syncButtons();
    }

    @Override
    public void onConferenceDisconnected()
    {
        addStatusMessage(R.string.disconnected);
        syncButtons();
    }

    @Override
    public void onConferenceError(Exception error)
    {
        addStatusMessage(String.format(getString(R.string.conference_error_fmt), error.getLocalizedMessage()));
        syncButtons();
    }

    @Override
    public void onLoginStarted()
    {
        addStatusMessage("Logging in...");
        syncButtons();
    }

    @Override
    public void onLoginFinished()
    {
        if (phone.canMakeOutgoing())
            addStatusMessage(R.string.phone_ready);
        else
            addStatusMessage(R.string.no_outgoing_capability);
        syncButtons();
    }

    @Override
    public void onLoginError(Exception error)
    {
        addStatusMessage(String.format(getString(R.string.login_error_fmt), error.getLocalizedMessage()));
        syncButtons();
    }
}