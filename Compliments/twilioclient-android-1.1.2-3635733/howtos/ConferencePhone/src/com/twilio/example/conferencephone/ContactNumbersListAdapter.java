/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

class ContactNumbersListAdapter extends BaseAdapter implements ListAdapter
{
    static class ContactNumber
    {
        public final String typeString;
        public final String numberString;

        public ContactNumber(String typeString, String numberString)
        {
            this.typeString = typeString;
            this.numberString = numberString;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof ContactNumber))
                return false;

            ContactNumber other = (ContactNumber)o;
            return numberString.equals(other.numberString);
        }
    }

    private final Context context;
    private final List<ContactNumber> contactNumbers;

    public ContactNumbersListAdapter(Context context,
                                     List<ContactNumber> contactNumbers)
    {
        super();
        this.context = context;
        this.contactNumbers = contactNumbers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewGroup v = (ViewGroup)convertView;
        if (v == null)
            v = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.number_list_row, null);

        ContactNumber cn = contactNumbers.get(position);

        TextView title = (TextView)v.findViewById(R.id.number);
        title.setText(cn.numberString);

        TextView detail = (TextView)v.findViewById(R.id.number_type);
        detail.setText(cn.typeString);

        return v;
    }

    @Override
    public ContactNumber getItem(int position)
    {
        return contactNumbers.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getCount()
    {
        return contactNumbers.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return contactNumbers.isEmpty();
    }
}
