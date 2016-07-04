package com.clubmycab;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopThreeRidesAdaptor extends BaseAdapter {

	// Declare Variables
	Context context;
	ArrayList<String> fromlocation;
	ArrayList<String> tolocation;
	ArrayList<String> traveldate;
	ArrayList<String> traveltime;
	ArrayList<String> seats;
	LayoutInflater inflater;

	public TopThreeRidesAdaptor(Context context, ArrayList<String> floc,
			ArrayList<String> tloc, ArrayList<String> tdate,
			ArrayList<String> ttime, ArrayList<String> sets) {
		this.context = context;
		this.fromlocation = floc;
		this.tolocation = tloc;
		this.traveldate = tdate;
		this.traveltime = ttime;
		this.seats = sets;
	}

	@Override
	public int getCount() {
		return fromlocation.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		TextView fromtolocationvalue;
		TextView datetext;
		TextView timetext;
		TextView seatstext;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.topthreerides_list_row,
				parent, false);

		// Locate the TextViews in listview_item.xml
		fromtolocationvalue = (TextView) itemView
				.findViewById(R.id.fromtolocationvalue);
		datetext = (TextView) itemView.findViewById(R.id.datetext);
		timetext = (TextView) itemView.findViewById(R.id.timetext);
		seatstext = (TextView) itemView.findViewById(R.id.seatstext);

		fromtolocationvalue.setText(fromlocation.get(position).toString()
				.trim()
				+ " > " + tolocation.get(position).toString().trim());
		datetext.setText(traveldate.get(position).toString().trim());
		timetext.setText(traveltime.get(position).toString().trim());
		seatstext.setText(seats.get(position).toString().trim() + " Seats");

		return itemView;
	}
}