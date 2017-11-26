package com.lilo.lilo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by ashis on 11/24/2017.
 */

public class DestinationEventAdapter extends BaseAdapter {
    public JSONArray data;

    public DestinationEventAdapter(JSONArray data) {
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        else {
            itemView = convertView;
        }

        try {
            ((TextView) itemView.findViewById(android.R.id.text1)).setText(data.getJSONObject(position).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemView;
    }
}
