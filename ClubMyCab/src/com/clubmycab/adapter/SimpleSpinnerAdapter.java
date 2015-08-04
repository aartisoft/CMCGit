package com.clubmycab.adapter;

import java.util.ArrayList;

import com.clubmycab.R;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class SimpleSpinnerAdapter extends ArrayAdapter<String> {
	int groupid;
	Context context;
	LayoutInflater inflater;
	private ArrayList<String> itemsName;

	public SimpleSpinnerAdapter(Context context, int vg, int id,
			ArrayList<String> itemsName, boolean flag) {
		super(context, id, itemsName);
		this.context = context;
		groupid = vg;
		this.itemsName = itemsName;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View itemView = inflater.inflate(groupid, parent, false);
		TextView textpart = (TextView) itemView.findViewById(R.id.textview);

	
		textpart.setText(itemsName.get(position));
		return itemView;

	}

}