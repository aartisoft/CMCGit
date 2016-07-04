package com.clubmycab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;

import java.util.ArrayList;

public class RidesAvailAdapter extends BaseAdapter {
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
			convertView = inflater.inflate(R.layout.item_rides_avail, parent,
					false);
			holder = new ViewHolder(convertView);
			// holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
	/*	RideDetailsModel rideDetailsModel = arrayList.get(position);
		
		//holder.myridesbannerusername.setText(arrayList.get(position).ge);
		holder.tvFromToLocation.setText(rideDetailsModel.getFromShortName().trim() + " > " + rideDetailsModel.getToShortName().trim());
		holder.datetext.setText(arrayList.get(position).getTravelDate());
		holder.timetext.setText(arrayList.get(position).getTravelTime());
		
		try {
			String[] arr = rideDetailsModel.getSeat_Status().split("/");
			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			holder.seatstext.setText("Total seats : "+ (total + StringTags.TAT_ADD_TOTAL));
			holder.tvRemainingSeats.setText("Available : " + ava);
		} catch (Exception e) {
			holder.seatstext.setText("Total seats :");
			holder.tvRemainingSeats.setText("Available :");
		}

	//	RideDetailsModel rideDetailsModel2 = new RideDetailsModel();

		if (rideDetailsModel.getRideType().equals(NewHomeScreen.CAR_POOL_ID)) {
			holder.tvRideOwnerName.setText(rideDetailsModel.getOwnerName().trim() + " (Car Pool)");

		} else if (rideDetailsModel.getRideType().equals(NewHomeScreen.SHARE_CAB_ID)) {
			holder.tvRideOwnerName.setVisibility(View.INVISIBLE);
			
			holder.tvRideOwnerName.setText(rideDetailsModel.getOwnerName().trim() + " (Cab Share)");
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
		});*/
		return convertView;
	}

	/* private view holder class */
	private class ViewHolder {
		private TextView  tvRideOwnerName, tvFromToLocation, seatstext, tvRemainingSeats,datetext,timetext;
		public ViewHolder(View view) {
			tvRideOwnerName = (TextView)view.findViewById(R.id.myridesbannerusername);
			tvFromToLocation = (TextView)view.findViewById(R.id.tvFromToLocation);
			seatstext = (TextView)view.findViewById(R.id.seatstext);
			tvRemainingSeats = (TextView)view.findViewById(R.id.tvAvSeats);
			datetext = (TextView) view.findViewById(R.id.datetext);
			timetext = (TextView) view.findViewById(R.id.timetext);
			 
			tvRideOwnerName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvFromToLocation.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			seatstext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			seatstext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));

		}
	}

}
