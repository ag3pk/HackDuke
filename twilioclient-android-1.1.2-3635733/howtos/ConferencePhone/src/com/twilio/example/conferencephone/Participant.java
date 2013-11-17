/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

public class Participant
{
    public enum Type
    {
        PSTN,
        CLIENT
    }

    private final String contactString;
    private final Type type;

    public Participant(String contactString, Type type)
    {
        this.contactString = contactString;
        this.type = type;
    }

    public String getContactString()
    {
        return contactString;
    }

    public Type getType()
    {
        return type;
    }
}
