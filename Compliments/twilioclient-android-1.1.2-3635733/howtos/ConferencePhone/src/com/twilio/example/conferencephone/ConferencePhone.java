/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twilio.client.Connection;
import com.twilio.client.ConnectionListener;
import com.twilio.client.Device;
import com.twilio.client.Device.Capability;
import com.twilio.client.Twilio;

import android.content.Context;

public class ConferencePhone implements ConnectionListener
{
    // TODO: change these to point to the scripts on your public server
    private static final String AUTH_PHP_SCRIPT = "http://companyfoo.com/auth.php";
    private static final String MAKE_CALL_PHP_SCRIPT = "http://companyfoo.com/make-call.php";

    public interface LoginListener
    {
        public void onLoginStarted();
        public void onLoginFinished();
        public void onLoginError(Exception error);
    }

    public interface ConferenceListener
    {
        public void onConferenceConnecting();
        public void onConferenceConnected();
        public void onConferenceConnectionFailed(Exception error);
        public void onConferenceDisconnecting();
        public void onConferenceDisconnected();
        public void onConferenceError(Exception error);
    }

    private final Context context;
    private LoginListener loginListener;
    private ConferenceListener conferenceListener;

    private static boolean twilioSdkInited;
    private static boolean twilioSdkInitInProgress;
    private String queuedConnectConferenceName;

    private Device device;
    private Connection connection;
    private String conferenceName;

    public ConferencePhone(Context context)
    {
        this.context = context;;
    }

    public void setListeners(LoginListener loginListener,
                             ConferenceListener conferenceListener)
    {
        this.loginListener = loginListener;
        this.conferenceListener = conferenceListener;
    }

    private String getCapabilityToken() throws Exception
    {
        // This runs synchronously for simplicity!  In a real application you'd
        // want to do network I/O on a separate thread to avoid ANRs.
        return HttpHelper.httpGet(AUTH_PHP_SCRIPT);
    }

    private boolean isCapabilityTokenValid()
    {
        if (device == null || device.getCapabilities() == null)
            return false;
        long expTime = (Long)device.getCapabilities().get(Capability.EXPIRATION);
        return expTime - System.currentTimeMillis() / 1000 > 0;
    }

    private String getRestUrl(List<Participant> participants)
    {
        StringBuilder builder = new StringBuilder(MAKE_CALL_PHP_SCRIPT);
        builder.append("?conferenceName=").append(conferenceName);

        for (Participant participant : participants) {
            builder.append("&participants[]=");
            if (participant.getType() == Participant.Type.CLIENT)
                builder.append("client:");
            builder.append(participant.getContactString());
        }

        return builder.toString();
    }

    public void login()
    {
        if (loginListener != null)
            loginListener.onLoginStarted();

        if (!twilioSdkInited) {
            if (twilioSdkInitInProgress)
                return;

            twilioSdkInitInProgress = true;

            Twilio.initialize(context, new Twilio.InitListener()
            {
                @Override
                public void onInitialized()
                {
                    twilioSdkInited = true;
                    twilioSdkInitInProgress = false;
                    reallyLogin();
                }

                @Override
                public void onError(Exception error)
                {
                    twilioSdkInitInProgress = false;
                    if (loginListener != null)
                        loginListener.onLoginError(error);
                }
            });
        } else
            reallyLogin();
    }

    public void reallyLogin()
    {
        try {
            String capabilityToken = getCapabilityToken();
            if (device == null)
                device = Twilio.createDevice(capabilityToken, null);
            else
                device.updateCapabilityToken(capabilityToken);

            if (loginListener != null)
                loginListener.onLoginFinished();

            if (queuedConnectConferenceName != null) {
                // If someone called connect() before we finished initializing
                // the SDK, let's take care of that here.
                connect(queuedConnectConferenceName);
                queuedConnectConferenceName = null;
            }
        } catch (Exception e) {
            if (device != null)
                device.release();
            device = null;

            if (loginListener != null)
                loginListener.onLoginError(e);
        }
    }

    public void connect(String conferenceName)
    {
        if (twilioSdkInitInProgress) {
            // If someone calls connect() before the SDK is initialized, we'll remember
            // the conference name and try to connect later.
            queuedConnectConferenceName = conferenceName;
            return;
        }

        if (!isCapabilityTokenValid())
            login();

        if (device == null)
            return;

        if (canMakeOutgoing()) {
            if (conferenceName != null)
                conferenceName = conferenceName.trim();

            if (conferenceName == null || conferenceName.length() == 0) {
                if (conferenceListener != null)
                    conferenceListener.onConferenceConnectionFailed(new Exception("No conference name specified"));
                return;
            }

            disconnect();

            this.conferenceName = conferenceName;
            Map<String, String> parameters = new HashMap<String, String>(1);
            parameters.put("ConferenceName", conferenceName);

            connection = device.connect(parameters, this);
            if (connection == null && conferenceListener != null)
                conferenceListener.onConferenceConnectionFailed(new Exception("Couldn't create new connection"));
        }
    }

    public void disconnect()
    {
        if (connection != null) {
            if (conferenceListener != null)
                conferenceListener.onConferenceDisconnecting();
            connection.disconnect();  // will null it out in onDisconnected()
        }
    }

    public void release()
    {
        disconnect();
        if (device != null)
        {
            device.release();
            device = null;
        }
    }

    public boolean isConnected()
    {
        return connection != null && connection.getState() == Connection.State.CONNECTED;
    }

    public boolean canMakeOutgoing()
    {
        if (device == null)
            return false;

        Map<Capability, Object> caps = device.getCapabilities();
        return caps.containsKey(Capability.OUTGOING) && (Boolean)caps.get(Capability.OUTGOING);
    }

    public void performCalls(List<Participant> participants)
    {
        try {
            String requestUrl = getRestUrl(participants);
            HttpHelper.httpGet(requestUrl);
        } catch (Exception e) {
            if (conferenceListener != null)
                conferenceListener.onConferenceError(e);
        }
    }

    @Override
    public void onConnecting(Connection inConnection)
    {
        if (conferenceListener != null)
            conferenceListener.onConferenceConnecting();
    }

    @Override
    public void onConnected(Connection inConnection)
    {
        if (conferenceListener != null)
            conferenceListener.onConferenceConnected();
    }

    @Override
    public void onDisconnected(Connection inConnection)
    {
        if (connection == inConnection)
            connection = null;
        if (conferenceListener != null)
            conferenceListener.onConferenceDisconnected();
    }

    @Override
    public void onDisconnected(Connection inConnection,
                               int inErrorCode,
                               String inErrorMessage)
    {
        if (connection == inConnection)
            connection = null;
        if (conferenceListener != null)
            conferenceListener.onConferenceError(new Exception(inErrorMessage));
    }
}
