/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import android.app.Application;

public class ConferencePhoneApplication extends Application
{
    private static ConferencePhoneApplication instance;
    public static final ConferencePhoneApplication getInstance()
    {
        return instance;
    }

    public ConferencePhoneApplication()
    {
        instance = this;
    }

    private ConferencePhone phone;

    public void initialize()
    {
        if (phone == null)
            phone = new ConferencePhone(this);
    }

    public ConferencePhone getPhone()
    {
        return phone;
    }
}
