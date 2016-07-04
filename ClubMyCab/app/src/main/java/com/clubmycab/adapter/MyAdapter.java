package com.clubmycab.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.ListItem;

import java.util.ArrayList;

/**
 * Created by newpc on 7/6/16.
 */
public class MyAdapter extends ArrayAdapter<ListItem> {

    LayoutInflater inflater;
    ArrayList<ListItem> objects;
    ViewHolder holder = null;

    public MyAdapter(Context context, int textViewResourceId, ArrayList<ListItem> objects) {
        super(context, textViewResourceId, objects);
        inflater = ((Activity) context).getLayoutInflater();
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        ListItem listItem = objects.get(position);
        View row = convertView;

        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.item_payment_type, parent, false);
            holder.name = (TextView) row.findViewById(R.id.name);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.name.setText(listItem.name);


        return row;
    }

    static class ViewHolder {
        TextView name;
    }
}
