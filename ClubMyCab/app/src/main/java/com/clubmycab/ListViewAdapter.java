package com.clubmycab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;

import java.util.ArrayList;

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
	ArrayList<Boolean> isOwner;

	public ListViewAdapter(Context context, ArrayList<String> floc,
			ArrayList<String> tloc, ArrayList<String> tdate,
			ArrayList<String> ttime, ArrayList<String> sets,
			ArrayList<String> ownnam, ArrayList<String> ownimgnam, ArrayList<Boolean> isOwner) {
		this.context = context;
		this.fromlocation = floc;
		this.tolocation = tloc;
		this.traveldate = tdate;
		this.traveltime = ttime;
		this.seats = sets;
		this.ownername = ownnam;
		this.ownerimagename = ownimgnam;
		this.aq = new AQuery(context);
		this.isOwner = isOwner;

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

		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.myrides_list_row_x, parent,
					false);
			viewholder = new ViewHolder(convertView);

			/*viewholder.myridesbannerimage = (CircularImageView) convertView
					.findViewById(R.id.myridesbannerimage);
			viewholder.myridesbannerusername = (TextView) convertView
					.findViewById(R.id.myridesbannerusername);
			viewholder.fromtolocationvalue = (TextView) convertView
					.findViewById(R.id.fromtolocationvalue);
			viewholder.datetext = (TextView) convertView
					.findViewById(R.id.datetext);
			viewholder.timetext = (TextView) convertView
					.findViewById(R.id.timetext);
			viewholder.seatstext = (TextView) convertView
					.findViewById(R.id.seatstext);
			viewholder.tvAvSeats = (TextView) convertView
					.findViewById(R.id.tvAvSeats);*/

			convertView.setTag(viewholder);

		} else
			viewholder = (ViewHolder) convertView.getTag();

		// Locate the TextViews in listview_item.xml

		/*if (ownerimagename.get(position).toString().trim().isEmpty()) {

			Log.d("image nahi hai", ""
					+ ownername.get(position).toString().trim());

		} else {
			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ ownerimagename.get(position).toString().trim();
			aq.id(viewholder.myridesbannerimage).image(url, true, true);
		}
		viewholder.myridesbannerusername.setText(ownername.get(position)
				.toString().trim());
		viewholder.fromtolocationvalue.setText(fromlocation.get(position)
				.toString().trim()
				+ " > " + tolocation.get(position).toString().trim());
		viewholder.datetext.setText(traveldate.get(position).toString().trim());
		viewholder.timetext.setText(traveltime.get(position).toString().trim());

		try {
			String[] arr = seats.get(position).toString().trim().split("/");

			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			viewholder.seatstext.setText("Total seats : "
					+ (total + StringTags.TAT_ADD_TOTAL));
			viewholder.tvAvSeats.setText("Available : " + ava);
		} catch (Exception e) {
			viewholder.seatstext.setText("Total seats :");
			viewholder.tvAvSeats.setText("Available :");
		}*/
		//--------------------------------->
		String[] arr = seats.get(position).toString().trim().split("/");

		int total = Integer.parseInt(arr[1]);
		int filled = Integer.parseInt(arr[0]);
		int ava = total - filled;
		if(isOwner.get(position)){
			viewholder.tvSeatAvLable.setText("Seat Filled");
			viewholder.tvSeatCount .setText(String.valueOf(filled));
		}else {
			viewholder.tvSeatAvLable.setText("Seat Available");
			viewholder.tvSeatCount .setText(String.valueOf(ava));
		}

		viewholder.tvUserName.setText(ownername.get(position).toString().trim());
		viewholder.tvPlaceFrom.setText(fromlocation.get(position).toString().trim());
	//	viewholder.tvDate.setText(traveldate.get(position).toString().trim());
		viewholder.tvTime.setText(traveltime.get(position).toString().trim());
		viewholder.tvPlaceTo .setText(tolocation.get(position).toString().trim());
		String[] arr2 = traveldate.get(position).toString().trim().split("/");
		int month = Integer.parseInt(arr2[1]);
		int date = Integer.parseInt(arr2[0]);
		viewholder.tvDate.setText(String.format("%02d",date)+" "+getMontString(month));
        if(!TextUtils.isEmpty(ownerimagename.get(position).toString())){
            String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                    + ownerimagename.get(position).toString().trim();
            Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
        }




		return convertView;
	}

	public class ViewHolder {

		/*public CircularImageView myridesbannerimage;
		public TextView myridesbannerusername;
		public TextView fromtolocationvalue;
		public TextView datetext;
		public TextView timetext;
		public TextView seatstext, tvAvSeats;*/
		private TextView tvUserName, tvDate,tvTime,tvPlaceFrom,tvPlaceTo,tvSeatAvLable,tvSeatCount,tvModelNumber,tvVehicleNumber ;
		private ImageView ivUserImage;
        public ViewHolder(View view){
			tvUserName = (TextView)view.findViewById(R.id.tvUserName);
			tvDate = (TextView)view.findViewById(R.id.tvDate);
			tvTime = (TextView)view.findViewById(R.id.tvTime);
			tvPlaceFrom = (TextView)view.findViewById(R.id.tvPlaceFrom);
			tvPlaceTo = (TextView)view.findViewById(R.id.tvPlaceTo);
			tvSeatAvLable = (TextView)view.findViewById(R.id.tvSeatAvLable);
			tvSeatCount = (TextView)view.findViewById(R.id.tvSeatCount);
			tvModelNumber = (TextView)view.findViewById(R.id.tvModelNumber);
			tvVehicleNumber = (TextView)view.findViewById(R.id.tvModelNumber);
            ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);

			tvUserName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvDate.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvTime.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceFrom.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceTo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatAvLable.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatCount.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvModelNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvVehicleNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));



		}
	}

	private String getMontString(int month) {
		String monthString = "";
		switch (month) {
			case 1:
				monthString = "JAN";
				break;
			case 2:
				monthString = "FEB";
				break;
			case 3:
				monthString = "MAR";
				break;
			case 4:
				monthString = "APR";
				break;
			case 5:
				monthString = "MAY";
				break;
			case 6:
				monthString = "JUN";
				break;
			case 7:
				monthString = "JUL";
				break;
			case 8:
				monthString = "AUG";
				break;
			case 9:
				monthString = "SEP";
				break;
			case 10:
				monthString = "OCT";

				break;
			case 11:
				monthString = "NOV";
				break;
			case 12:
				monthString = "DEC";
				break;


		}

		return monthString;
	}
}