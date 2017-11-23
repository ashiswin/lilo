package com.lilo.lilo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lilo.lilo.R;
import com.lilo.lilo.ViewDestinationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ashis on 11/24/2017.
 */

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    JSONArray data;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public ImageView imgThumbnail;
        public View layout;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.viwCard);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.imgThumbnail);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewDestinationIntent = new Intent(context, ViewDestinationActivity.class);
                    try {
                        JSONObject destination = data.getJSONObject(position);
                        viewDestinationIntent.putExtra("lat", destination.getString("lat"));
                        viewDestinationIntent.putExtra("lon", destination.getString("lon"));
                        viewDestinationIntent.putExtra("name", destination.getString("name"));
                        viewDestinationIntent.putExtra("details", destination.getString("details"));
                        viewDestinationIntent.putExtra("id", destination.getInt("id"));
                        context.startActivity(viewDestinationIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public DestinationAdapter(JSONArray data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_destination, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject destination = data.getJSONObject(position);
            holder.txtName.setText(destination.getString("name"));
            holder.position = position;

            if(destination.getString("thumbnail").isEmpty()) return;

            String base64String = destination.getString("thumbnail");
            String base64Image = base64String.split(",")[1];

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            holder.imgThumbnail.setImageBitmap(decodedByte);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }
}
