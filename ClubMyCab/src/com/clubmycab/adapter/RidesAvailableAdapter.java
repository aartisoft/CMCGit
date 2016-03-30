package com.clubmycab.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.MemberRideFragmentActivity;
import com.clubmycab.R;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.utility.StringTags;
import com.google.gson.Gson;

public class RidesAvailableAdapter extends BaseAdapter {
	private ArrayList<RideDetailsModel> arrayList;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public void init(Context context, ArrayList<RideDetailsModel> arrayList) {
		this.arrayList = arrayList;
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		if (arrayList != null && arrayList.size() > 0) {
			return arrayList.size();
		} else {
			return 0;
		}
	}

	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return arrayList.indexOf(getItem(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.myrides_list_row_pager, parent,
					false);
			holder = new ViewHolder(convertView);
			// holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RideDetailsModel rideDetailsModel = arrayList.get(position);
		
		//holder.myridesbannerusername.setText(arrayList.get(position).ge);
		holder.fromtolocationvalue.setText(rideDetailsModel.getFromShortName().trim() + " > " + rideDetailsModel.getToShortName().trim());
		holder.datetext.setText(arrayList.get(position).getTravelDate());
		holder.timetext.setText(arrayList.get(position).getTravelTime());
		
		try {
			String[] arr = rideDetailsModel.getSeat_Status().split("/");
			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			holder.seatstext.setText("Total seats : "
					+ (total + StringTags.TAT_ADD_TOTAL));
			holder.tvAvSeats.setText("Available : " + ava);
		} catch (Exception e) {
			holder.seatstext.setText("Total seats :");
			holder.tvAvSeats.setText("Available :");
		}

	//	RideDetailsModel rideDetailsModel2 = new RideDetailsModel();

		if (rideDetailsModel.getRideType().equals(NewHomeScreen.CAR_POOL_ID)) {

			//int arg2 = NewHomeScreen.viewPagerHome.getCurrentItem();
			//Log.d("arg2", "" + arg2);

			/*rideDetailsModel = NewHomeScreen.arrayListInvitations
					.get(arg2);*/

			holder.carpoolchargestext.setVisibility(View.VISIBLE);
			holder.carpoolchargestext.setText("Per seat charge :  \u20B9"
					+ rideDetailsModel.getPerKmCharge() + "/km");
			
			holder.myridesbannerusername.setText(rideDetailsModel.getOwnerName().trim() + " (Car Pool)");

		} else if (rideDetailsModel.getRideType().equals(NewHomeScreen.SHARE_CAB_ID)) {
			holder.carpoolchargestext.setVisibility(View.INVISIBLE);
			
			holder.myridesbannerusername.setText(rideDetailsModel.getOwnerName().trim() + " (Cab Share)");
		}
		
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				String mobileNumber = "";
				RideDetailsModel rideDetailsModel = arrayList.get(position);

				if (rideDetailsModel.getRideType().equals(NewHomeScreen.CAR_POOL_ID)) {
					mobileNumber = rideDetailsModel.getMobileNumber();
				} else if (rideDetailsModel.getRideType().equals(NewHomeScreen.SHARE_CAB_ID)) {
					mobileNumber = rideDetailsModel.getMobileNumber();
				}

				if (mobileNumber.equalsIgnoreCase("121")) {
					final Intent mainIntent = new Intent(context,
							CheckPoolFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
					context.startActivity(mainIntent);

				} else {

					final Intent mainIntent = new Intent(context,
							MemberRideFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
					context.startActivity(mainIntent);
				}

			}
		});


		return convertView;
	}

	/* private view holder class */
	private class ViewHolder {
		private TextView myridesbannerusername,fromtolocationvalue,datetext,timetext,seatstext,tvAvSeats,carpoolchargestext;
		public ViewHolder(View view) {
		 myridesbannerusername = (TextView) view.findViewById(R.id.myridesbannerusername);
		 fromtolocationvalue = (TextView) view.findViewById(R.id.fromtolocationvalue);
		 datetext = (TextView) view.findViewById(R.id.datetext);
		 timetext = (TextView) view.findViewById(R.id.timetext);
		 seatstext = (TextView) view	.findViewById(R.id.seatstext);
		 tvAvSeats = (TextView) view.findViewById(R.id.tvAvSeats);
		 carpoolchargestext = (TextView) view.findViewById(R.id.carpoolchargestext);
		 }
	}
	
	

}
