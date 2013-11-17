/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.basicphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.twilio.example.basicphone.BasicPhone.BasicConnectionListener;
import com.twilio.example.basicphone.BasicPhone.BasicDeviceListener;
import com.twilio.example.basicphone.BasicPhone.LoginListener;

public class BasicPhoneActivity extends Activity implements LoginListener,
                                                            BasicConnectionListener,
                                                            BasicDeviceListener,
                                                            View.OnClickListener,
                                                            CompoundButton.OnCheckedChangeListener
{
    private static final Handler handler = new Handler();

    private BasicPhone phone;

    private ImageButton mainButton;
    private ToggleButton speakerButton;
    private EditText logTextBox;
    private AlertDialog incomingAlert;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mainButton = (ImageButton)findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);
        speakerButton = (ToggleButton)findViewById(R.id.speaker_toggle);
        speakerButton.setOnCheckedChangeListener(this);
        logTextBox = (EditText)findViewById(R.id.log_text_box);

        phone = BasicPhone.getInstance(getApplicationContext());
        phone.setListeners(this, this, this);
        phone.login();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (phone.handleIncomingIntent(getIntent())) {
            showIncomingAlert();
            addStatusMessage(R.string.got_incoming);
            syncMainButton();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (phone != null) {
            phone.setListeners(null, null, null);
            phone = null;
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.main_button) {
            if (!phone.isConnected())
                phone.connect();
            else
                phone.disconnect();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked)
    {
        if (button.getId() == R.id.speaker_toggle)
            phone.setSpeakerEnabled(isChecked);
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

    private void syncMainButton()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (phone.isConnected()) {
                    switch (phone.getConnectionState()) {
                        case DISCONNECTED:
                            mainButton.setImageResource(R.drawable.idle);
                            break;
                        case CONNECTED:
                            mainButton.setImageResource(R.drawable.inprogress);
                            break;
                        default:
                            mainButton.setImageResource(R.drawable.dialing);
                            break;
                    }
                } else if (phone.hasPendingConnection())
                    mainButton.setImageResource(R.drawable.dialing);
                else
                    mainButton.setImageResource(R.drawable.idle);
            }
        });
    }

    private void showIncomingAlert()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (incomingAlert == null) {
                    incomingAlert = new AlertDialog.Builder(BasicPhoneActivity.this)
                        .setTitle(R.string.incoming_call)
                        .setMessage(R.string.incoming_call_message)
                        .setPositiveButton(R.string.answer, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                phone.acceptConnection();
                            }
                        })
                        .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                phone.ignoreIncomingConnection();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                phone.ignoreIncomingConnection();
                            }
                        })
                        .create();
                    incomingAlert.show();
                }
            }
        });
    }

    private void hideIncomingAlert()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (incomingAlert != null) {
                    incomingAlert.dismiss();
                    incomingAlert = null;
                }
            }
        });
    }

    @Override
    public void onLoginStarted()
    {
        addStatusMessage(R.string.logging_in);
    }

    @Override
    public void onLoginFinished()
    {
        addStatusMessage(phone.canMakeOutgoing() ? R.string.outgoing_ok : R.string.no_outgoing_capability);
        addStatusMessage(phone.canAcceptIncoming() ? R.string.incoming_ok : R.string.no_incoming_capability);
        syncMainButton();
    }

    @Override
    public void onLoginError(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.login_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.login_error_unknown);
        syncMainButton();
    }

    @Override
    public void onIncomingConnectionDisconnected()
    {
        hideIncomingAlert();
        addStatusMessage(R.string.incoming_disconnected);
        syncMainButton();
    }

    @Override
    public void onConnectionConnecting()
    {
        addStatusMessage(R.string.attempting_to_connect);
        syncMainButton();
    }

    @Override
    public void onConnectionConnected()
    {
        addStatusMessage(R.string.connected);
        syncMainButton();
    }

    @Override
    public void onConnectionFailedConnecting(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.couldnt_establish_outgoing_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.couldnt_establish_outgoing);
    }

    @Override
    public void onConnectionDisconnecting()
    {
        addStatusMessage(R.string.disconnect_attempt);
        syncMainButton();
    }

    @Override
    public void onConnectionDisconnected()
    {
        addStatusMessage(R.string.disconnected);
        syncMainButton();
    }

    @Override
    public void onConnectionFailed(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.connection_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.connection_error);
        syncMainButton();
    }

    @Override
    public void onDeviceStartedListening()
    {
        addStatusMessage(R.string.device_listening);
    }

    @Override
    public void onDeviceStoppedListening(Exception error)
    {
        if (error != null)
            addStatusMessage(String.format(getString(R.string.device_listening_error_fmt), error.getLocalizedMessage()));
        else
            addStatusMessage(R.string.device_not_listening);
    }
}
