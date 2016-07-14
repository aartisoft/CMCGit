package com.clubmycab.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.PrivateRide;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.XCheckPoolFragmentActivty;
import com.clubmycab.ui.XMemberRideFragmentActivity;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class PrivateRidesAdapter extends BaseAdapter {
	private ArrayList<PrivateRide> arrayList;
	private Context context;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private String MobileNumberstr;

	public void init(Context context, ArrayList<PrivateRide> arrayList) {
		this.arrayList = arrayList;
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		SharedPreferences mPrefs = context.getSharedPreferences(
				"FacebookData", 0);
		MobileNumberstr = mPrefs.getString("MobileNumber", "");
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
		//return null;
	}

	@Override
	public long getItemId(int position) {
		return arrayList.indexOf(getItem(position));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_private_grp_rides_x, parent,
					false);
			holder = new ViewHolder(convertView);
			// holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	/*	if(arrayList.get(position).getOwnerName() !=null){
			StringBuilder builder = new StringBuilder(arrayList.get(position).getOwnerName()) ;
			if(arrayList.get(position).getRideType()!=null){
				String rideType = arrayList.get(position).getRideType();
				if(rideType.equalsIgnoreCase("1") || rideType.equalsIgnoreCase("4")){
					builder.append("(Car Pool)");
				}else if(rideType.equalsIgnoreCase("2") || rideType.equalsIgnoreCase("5")){
					builder.append("(Cab Share)");
				}
			}
			holder.tvRideOwnerName.setText(builder.toString().toUpperCase());

		}
		if(arrayList.get(position).getTravelDate() !=null)
			holder.datetext.setText(arrayList.get(position).getTravelDate());
		if(arrayList.get(position).getTravelTime() !=null)
			holder.timetext.setText(arrayList.get(position).getTravelTime());
		if(arrayList.get(position).getSeats() !=null)
			holder.seatstext.setText("Total seats : "+arrayList.get(position).getSeats());
		if(arrayList.get(position).getRemainingSeats() !=null)
			holder.tvRemainingSeats.setText("Remaining seats : "+arrayList.get(position).getRemainingSeats());
		if(arrayList.get(position).getFromShortName() !=null && arrayList.get(position).getToShortName() !=null)
			holder.tvFromToLoc.setText(arrayList.get(position).getFromShortName()+" - \n"+arrayList.get(position).getToShortName());
	*/

		if(arrayList.get(position).getOwnerName() != null){
			holder.tvUserName.setText(arrayList.get(position).getOwnerName());
		}
		if(arrayList.get(position).getTravelDate() != null){
			String[] arr2 = arrayList.get(position).getTravelDate().toString().trim().split("/");
			int month = Integer.parseInt(arr2[1]);
			int date = Integer.parseInt(arr2[0]);
			holder.tvDate.setText(String.format("%02d",date)+" "+getMontString(month)+" "+ arr2[2]);
		}
		if(arrayList.get(position).getTravelTime() != null){
			holder.tvTime.setText(arrayList.get(position).getTravelTime());
		}
		if(arrayList.get(position).getFromShortName() != null){
			holder.tvPlaceFrom.setText(arrayList.get(position).getFromShortName());
		}
		if(arrayList.get(position).getToShortName() != null){
			holder.tvPlaceTo.setText(arrayList.get(position).getToShortName());
		}
		if (arrayList.get(position).getRemainingSeats() != null){
			holder.tvSeatCount.setText(arrayList.get(position).getRemainingSeats());
		}
		if (arrayList.get(position).getImagename() != null){
			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ arrayList.get(position).getImagename().toString().trim();
			//Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(holder.ivUserImage);
			Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(holder.ivUserImage);

		}
		if(arrayList.get(position).getRideType()!=null){
			String rideType = arrayList.get(position).getRideType();
			if(rideType.equalsIgnoreCase("1") || rideType.equalsIgnoreCase("4")){
				if (arrayList.get(position).getVehicleModel() != null){
					holder.tvModelNumber.setText(arrayList.get(position).getVehicleModel());
				}
				if (arrayList.get(position).getRegistrationNumber() != null){
					holder.tvRegNo.setText(arrayList.get(position).getRegistrationNumber().toUpperCase(Locale.getDefault()));
				}
				if (arrayList.get(position).getIsCommercial() != null){
					if(arrayList.get(position).getIsCommercial().equalsIgnoreCase(AppConstants.COMMERCIAL)){
						holder.ivCabImage.setImageResource(R.drawable.car_taxi);
					}else {
						holder.ivCabImage.setImageResource(R.drawable.car);
					}
				}
			}else if(rideType.equalsIgnoreCase("2") || rideType.equalsIgnoreCase("5")){
				// builder.append("(Cab Share)");
				//if (arrayList.get(position).getVehicleModel() != null){
					holder.tvModelNumber.setText("CAB SHARE");
				//}
			//	if (arrayList.get(position).getRegistrationNumber() != null){
					holder.tvRegNo.setText("");
				//}
				holder.ivCabImage.setImageResource(R.drawable.car_taxi);

			}
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				String mobileNumber = "";
				PrivateRide rideDetailsModel = arrayList.get(position);

				if (rideDetailsModel.getRideType().equals(NewHomeScreen.CAR_POOL_ID)) {
					mobileNumber = rideDetailsModel.getMobileNumber();
				} else if (rideDetailsModel.getRideType().equals(NewHomeScreen.SHARE_CAB_ID)) {
					mobileNumber = rideDetailsModel.getMobileNumber();
				}

				if (mobileNumber.equalsIgnoreCase("121")) {
					final Intent mainIntent = new Intent(context,
							XCheckPoolFragmentActivty.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
					context.startActivity(mainIntent);

				} else {

					final Intent mainIntent = new Intent(context,
							XMemberRideFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
					context.startActivity(mainIntent);
				}

			}
		});
		holder.tvJoinNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PrivateRide rideDetailsModel = arrayList.get(position);
				if(rideDetailsModel.getMobileNumber() != null){
					if (rideDetailsModel.getMobileNumber().equalsIgnoreCase(MobileNumberstr)) {
						final Intent mainIntent = new Intent(context,
								XCheckPoolFragmentActivty.class);
						mainIntent.putExtra("RideDetailsModel",
								(new Gson()).toJson(rideDetailsModel));
						context.startActivity(mainIntent);

					} else {

						final Intent mainIntent = new Intent(context, XMemberRideFragmentActivity.class);
						mainIntent.putExtra("RideDetailsModel",
								(new Gson()).toJson(rideDetailsModel));
						context.startActivity(mainIntent);
					}
				}
			}
		});
		return convertView;
	}

	/* private view holder class */
	/*private class ViewHolder {
		private TextView tvRideOwnerName, seatstext, tvRemainingSeats,datetext,timetext,tvFromToLoc;
		public ViewHolder(View view) {
			tvRideOwnerName = (TextView)view.findViewById(R.id.myridesbannerusername);
			seatstext = (TextView)view.findViewById(R.id.seatstext);
			tvRemainingSeats = (TextView)view.findViewById(R.id.tvAvSeats);
			datetext = (TextView) view.findViewById(R.id.datetext);
			timetext = (TextView) view.findViewById(R.id.timetext);
			tvFromToLoc = (TextView)view.findViewById(R.id.tvFromToLocatio);

			tvRideOwnerName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			seatstext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			seatstext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));

		}
	}*/

	public class ViewHolder {

		/*public CircularImageView myridesbannerimage;
		public TextView myridesbannerusername;
		public TextView fromtolocationvalue;
		public TextView datetext;
		public TextView timetext;
		public TextView seatstext, tvAvSeats;*/
		private TextView tvJoinNow, tvUserName, tvDate,tvTime,tvPlaceFrom,tvPlaceTo,tvSeatAvLable,tvSeatCount,tvModelNumber,tvRegNo ;
		private ImageView ivUserImage, ivCabImage;

		public ViewHolder(View view){
			tvUserName = (TextView)view.findViewById(R.id.tvUserName);
			tvDate = (TextView)view.findViewById(R.id.tvDate);
			tvTime = (TextView)view.findViewById(R.id.tvTime);
			tvPlaceFrom = (TextView)view.findViewById(R.id.tvPlaceFrom);
			tvPlaceTo = (TextView)view.findViewById(R.id.tvPlaceTo);
			tvSeatAvLable = (TextView)view.findViewById(R.id.tvSeatAvLable);
			tvSeatCount = (TextView)view.findViewById(R.id.tvSeatCount);
			tvModelNumber = (TextView)view.findViewById(R.id.tvModelNumber);
			tvRegNo = (TextView)view.findViewById(R.id.tvRegNo);
			ivUserImage = (ImageView)view.findViewById(R.id.ivUserImage);
			ivCabImage = (ImageView)view.findViewById(R.id.ivCabImage);
			tvJoinNow = (TextView)view.findViewById(R.id.tvJoinNow);

			tvUserName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvDate.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvTime.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceFrom.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceTo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatAvLable.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatCount.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvModelNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvRegNo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvJoinNow.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));



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
