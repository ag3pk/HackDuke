/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.conferencephone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ParticipantListAdapter extends ArrayAdapter<Participant>
{
    private final Context context;
    private final List<Participant> participants;

    public ParticipantListAdapter(Context context)
    {
        super(context, 0);
        this.context = context;
        this.participants = new ArrayList<Participant>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewGroup v = (ViewGroup)convertView;
        if (v == null)
            v = (ViewGroup)LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null);

        Participant participant = participants.get(position);

        TextView title = (TextView)v.findViewById(android.R.id.text1);
        title.setText(participant.getContactString());

        TextView detail = (TextView)v.findViewById(android.R.id.text2);
        detail.setText(participant.getType() == Participant.Type.CLIENT ? "Client Name" : "Phone Number");

        return v;
    }

    @Override
    public Participant getItem(int position)
    {
        return participants.get(position);
    }

    @Override
    public int getCount()
    {
        return participants.size();
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
        return participants.isEmpty();
    }

    @Override
    public void add(Participant participant)
    {
        participants.add(participant);
        notifyDataSetChanged();
    }

    @Override
    public void insert(Participant participant, int index)
    {
        participants.add(index, participant);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Participant participant)
    {
        if (participants.remove(participant))
            notifyDataSetChanged();
    }

    @Override
    public void clear()
    {
        participants.clear();
        notifyDataSetChanged();
    }

    public List<Participant> getParticipants()
    {
        return participants;
    }
}