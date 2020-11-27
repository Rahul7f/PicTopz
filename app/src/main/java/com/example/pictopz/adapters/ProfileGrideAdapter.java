package com.example.pictopz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.pictopz.R;


public class ProfileGrideAdapter extends BaseAdapter {

    Context context;
    int logos[];
    LayoutInflater inflter;

    public ProfileGrideAdapter(Context applicationContext, int[] logos) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.gride_layout, null); // inflate the layout
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageResource(logos[position]); // set logo images
        return convertView;
    }
}
