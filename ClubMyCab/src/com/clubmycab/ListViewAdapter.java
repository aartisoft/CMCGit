package com.clubmycab;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	ArrayList<String> fromlocation;
	ArrayList<String> tolocation;
	ArrayList<String> traveldate;
	ArrayList<String> traveltime;
	ArrayList<String> seats;
	ArrayList<String> ownername;
	ArrayList<String> ownerimagename;
	LayoutInflater inflater;
	AQuery aq;

	UrlConstant checkurl;

	public ListViewAdapter(Context context, ArrayList<String> floc,
			ArrayList<String> tloc, ArrayList<String> tdate,
			ArrayList<String> ttime, ArrayList<String> sets,
			ArrayList<String> ownnam, ArrayList<String> ownimgnam) {
		this.context = context;
		this.fromlocation = floc;
		this.tolocation = tloc;
		this.traveldate = tdate;
		this.traveltime = ttime;
		this.seats = sets;
		this.ownername = ownnam;
		this.ownerimagename = ownimgnam;
		this.aq = new AQuery(context);
		checkurl = new UrlConstant();
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

		CircularImageView myridesbannerimage;
		TextView myridesbannerusername;
		TextView fromtolocationvalue;
		TextView datetext;
		TextView timetext;
		TextView seatstext;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.myrides_list_row, parent,
				false);

		// Locate the TextViews in listview_item.xml

		myridesbannerimage = (CircularImageView) itemView
				.findViewById(R.id.myridesbannerimage);
		myridesbannerusername = (TextView) itemView
				.findViewById(R.id.myridesbannerusername);
		fromtolocationvalue = (TextView) itemView
				.findViewById(R.id.fromtolocationvalue);
		datetext = (TextView) itemView.findViewById(R.id.datetext);
		timetext = (TextView) itemView.findViewById(R.id.timetext);
		seatstext = (TextView) itemView.findViewById(R.id.seatstext);

		if (ownerimagename.get(position).toString().trim().isEmpty()) {

			Log.i("image nahi hai", ""
					+ ownername.get(position).toString().trim());

		} else {
			String url = checkurl.GetServiceUrl() + "/ProfileImages/"
					+ ownerimagename.get(position).toString().trim();
			aq.id(myridesbannerimage).image(url, true, true);
		}
		myridesbannerusername
				.setText(ownername.get(position).toString().trim());
		fromtolocationvalue.setText(fromlocation.get(position).toString()
				.trim()
				+ " > " + tolocation.get(position).toString().trim());
		datetext.setText(traveldate.get(position).toString().trim());
		timetext.setText(traveltime.get(position).toString().trim());
		seatstext.setText(seats.get(position).toString().trim() + " Seats");

		return itemView;
	}
}