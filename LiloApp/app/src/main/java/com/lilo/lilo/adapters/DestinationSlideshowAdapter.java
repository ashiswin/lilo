package com.lilo.lilo.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lilo.lilo.R;

/**
 * Created by ashis on 11/24/2017.
 */

public class DestinationSlideshowAdapter extends PagerAdapter {
    Context context;
    Bitmap[] sources;

    public DestinationSlideshowAdapter(Bitmap[] data, Context context){
        sources = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager_destination, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imgThumbnail);
        imageView.setImageBitmap(sources[position]);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView viewer = new ImageView(context);
                viewer.setImageBitmap(sources[position]);
                dialog.setContentView(viewer);

                dialog.show();
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
