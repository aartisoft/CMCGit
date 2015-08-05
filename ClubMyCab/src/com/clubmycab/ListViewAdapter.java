package com.clubmycab;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;

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
ViewHolder viewholder;

if(convertView==null){
	inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	convertView = inflater.inflate(R.layout.myrides_list_row, parent,
			false);
	viewholder=new ViewHolder();
	
	viewholder.myridesbannerimage = (CircularImageView) convertView
			.findViewById(R.id.myridesbannerimage);
	viewholder.myridesbannerusername = (TextView) convertView
			.findViewById(R.id.myridesbannerusername);
	viewholder.fromtolocationvalue = (TextView) convertView
			.findViewById(R.id.fromtolocationvalue);
	viewholder.datetext = (TextView) convertView.findViewById(R.id.datetext);
	viewholder.timetext = (TextView) convertView.findViewById(R.id.timetext);
	viewholder.seatstext = (TextView) convertView.findViewById(R.id.seatstext);
	viewholder.tvAvSeats=(TextView)convertView.findViewById(R.id.tvAvSeats);
	
	convertView.setTag(viewholder);
	
}
else
	viewholder=(ViewHolder)convertView.getTag();
	

		

		// Locate the TextViews in listview_item.xml

		
		if (ownerimagename.get(position).toString().trim().isEmpty()) {

			Log.d("image nahi hai", ""
					+ ownername.get(position).toString().trim());

		} else {
			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ ownerimagename.get(position).toString().trim();
			aq.id(viewholder.myridesbannerimage).image(url, true, true);
		}
		viewholder.myridesbannerusername
				.setText(ownername.get(position).toString().trim());
		viewholder.fromtolocationvalue.setText(fromlocation.get(position).toString()
				.trim()
				+ " > " + tolocation.get(position).toString().trim());
		viewholder.datetext.setText(traveldate.get(position).toString().trim());
		viewholder.timetext.setText(traveltime.get(position).toString().trim());
		
		try{
		String []arr=seats.get(position).toString().trim().split("/");
		
		int total=Integer.parseInt(arr[1]);
		int filled=Integer.parseInt(arr[0]);
		int ava=total-filled;
		viewholder.seatstext.setText("Total seats : "+(total+StringTags.TAT_ADD_TOTAL));
		viewholder.tvAvSeats.setText("Available : "+ava);
		}catch(Exception e){
			viewholder.seatstext.setText("Total seats :");
			viewholder.tvAvSeats.setText("Available :");
		}

		return convertView;
	}
	public class ViewHolder{
		
	public 	CircularImageView myridesbannerimage;
	public	TextView myridesbannerusername;
	public	TextView fromtolocationvalue;
	public	TextView datetext;
	public	TextView timetext;
	public	TextView seatstext,tvAvSeats;
	}
}