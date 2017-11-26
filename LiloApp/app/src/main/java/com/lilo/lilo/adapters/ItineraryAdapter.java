package com.lilo.lilo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lilo.lilo.R;
import com.lilo.lilo.ViewDestinationActivity;
import com.lilo.lilo.model.Destination;
import com.lilo.lilo.model.ItineraryStorage;

/**
 * Created by ashis on 11/26/2017.
 */

public class ItineraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ItineraryStorage storage;

    public Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public View layout;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.viwCard);
            txtName = (TextView) itemView.findViewById(R.id.txtName);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewDestinationIntent = new Intent(context, ViewDestinationActivity.class);
                    Destination d = storage.destinations.get(position / 2);

                    viewDestinationIntent.putExtra("lat", d.lat);
                    viewDestinationIntent.putExtra("lon", d.lon);
                    viewDestinationIntent.putExtra("name", d.name);
                    viewDestinationIntent.putExtra("details", d.details);
                    viewDestinationIntent.putExtra("id", d.id);
                    context.startActivity(viewDestinationIntent);
                }
            });
        }
    }

    public class DirectionViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTransport;
        public TextView txtTime;
        public TextView txtCost;

        public DirectionViewHolder(View itemView) {
            super(itemView);

            txtTransport = (TextView) itemView.findViewById(R.id.txtTransport);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtCost = (TextView) itemView.findViewById(R.id.txtCost);
        }
    }

    public ItineraryAdapter(Context context) {
        this.storage = ItineraryStorage.newInstance(context.getFilesDir() + "itinerary.json");
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder vh;
        if(viewType == 1) {
            View v = inflater.inflate(R.layout.list_item_itinerary, parent, false);
            vh = new ItineraryAdapter.ViewHolder(v);
        }
        else {
            View v = inflater.inflate(R.layout.list_item_direction, parent, false);
            vh = new ItineraryAdapter.DirectionViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position % 2 == 1) {
            ViewHolder h = (ViewHolder) holder;
            Destination d = storage.destinations.get(position / 2);
            h.txtName.setText(d.name);
            h.position = position;
        }
        else {
            DirectionViewHolder h = (DirectionViewHolder) holder;
            h.txtTransport.setText("Walking");
            h.txtTime.setText("Long");
            h.txtCost.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return storage.destinations.size() * 2 + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }
}
