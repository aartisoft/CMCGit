package com.clubmycab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.XCheckPoolFragmentActivty;
import com.clubmycab.ui.XMemberRideFragmentActivity;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ShowHistoryRidesAdaptor extends PagingBaseAdapter<String> {

	private final SharedPreferences mPrefs;
	private final String MobileNumberstr;
	Context context;
	LayoutInflater inflater;
	AQuery aq;

	public ShowHistoryRidesAdaptor(Context context) {
		this.context = context;
		this.aq = new AQuery(context);
		mPrefs = context.getSharedPreferences("FacebookData", 0);
		MobileNumberstr = mPrefs.getString("MobileNumber", "");	}

	@Override
	public int getCount() {
		return items.size();
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

		convertView = inflater.inflate(R.layout.item_myrides_history, parent,
					false);
		viewholder=new ViewHolder(convertView);
		convertView.setTag(viewholder);
		}
		else
			viewholder=(ViewHolder)convertView.getTag();
			
		if (items.get(position).getImagename().toString().trim().isEmpty()) {

		} else {
			String url =  GlobalVariables.ServiceUrl
					+ "/ProfileImages/"
					+ items.get(position).getImagename().toString().trim();
			Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
		}
		String[] arr = items.get(position).getSeat_Status().toString().trim().split("/");
		int total = Integer.parseInt(arr[1]);
		int filled = Integer.parseInt(arr[0]);
		int ava = total - filled;
		if(items.get(position).getIsOwner().equalsIgnoreCase("Y")){
			viewholder.tvSeatAvLable.setText("Seats Filled");
			viewholder.tvSeatCount .setText(String.valueOf(filled));
		}else {
			viewholder.tvSeatAvLable.setText("Seats Available");
			viewholder.tvSeatCount .setText(String.valueOf(ava));
		}
		viewholder.tvUserName.setText(items.get(position).getOwnerName().toString().trim());
		viewholder.tvPlaceFrom.setText(items.get(position).getFromShortName().trim());
		viewholder.tvPlaceTo .setText(items.get(position).getToShortName().trim());
		String[] arr2 = items.get(position).getTravelDate().toString().trim().split("/");
		int month = Integer.parseInt(arr2[1]);
		int date = Integer.parseInt(arr2[0]);
		viewholder.tvDate.setText(String.format("%02d",date)+" "+getMontString(month));
		viewholder.tvTime.setText(items.get(position).getTravelTime().toString().trim());

		if (items.get(position).getVehicleModel() != null){
			viewholder.llVehicleDetail.setVisibility(View.VISIBLE);
			viewholder.tvModelNumber.setText(items.get(position).getVehicleModel());
		}else {
			viewholder.llVehicleDetail.setVisibility(View.GONE);
		}
		if (items.get(position).getRegistrationNumber() != null){
			viewholder.tvRegNo.setText(items.get(position).getRegistrationNumber().toUpperCase(Locale.getDefault()));
		}
		if (items.get(position).getIsCommercial() != null){
			if(items.get(position).getIsCommercial().equalsIgnoreCase(AppConstants.COMMERCIAL)){
				viewholder.ivCabImage.setImageResource(R.drawable.car_taxi);
			}else {
				viewholder.ivCabImage.setImageResource(R.drawable.car);
			}
		}
		if(items.get(position).getRideType().equalsIgnoreCase("3")){
			viewholder.flCall.setVisibility(View.VISIBLE);
			viewholder.llSeatsAv.setVisibility(View.GONE);
			viewholder.ivUserImage.setVisibility(View.GONE);
			viewholder.ivCabTypeIcon.setVisibility(View.VISIBLE);
			viewholder.tvCancel.setVisibility(View.GONE);
			viewholder.ivCabImage.setImageResource(R.drawable.car_taxi);
			if (items.get(position).getCarNumber() != null){
				String str[] = items.get(position).getCarNumber().split("\\(");
				viewholder.tvModelNumber.setText(str[1].trim().replace(")","").toUpperCase(Locale.getDefault()));
				viewholder.tvRegNo.setText(str[0].trim().toUpperCase(Locale.getDefault()));

			}
			if(!TextUtils.isEmpty(items.get(position).getDriverName())){
				viewholder.tvUserName.setText(items.get(position).getDriverName());
			}
			if(!TextUtils.isEmpty(items.get(position).getCabName())){
                   /* String url = GlobalVariables.ServiceUrl + "/ProfileImages/" + ownerimagename.get(position).toString().trim();
                    Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
    */              String cabName = items.get(position).getCabName();
				if(!TextUtils.isEmpty(cabName)){
					if(cabName.equalsIgnoreCase(AppConstants.UBER)){
						viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_uber_icon);
					}else if(cabName.equalsIgnoreCase(AppConstants.OLA)){
						viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_ola_icon);

					}else if(cabName.equalsIgnoreCase(AppConstants.MERU)){
						viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_meru_icon);

					}else if(cabName.equalsIgnoreCase(AppConstants.MEGA)){
						viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_mega_icon);

					}else if(cabName.equalsIgnoreCase(AppConstants.TAXIFORSURE)){
						viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_tfs_icon);
					}
				}
			}
			viewholder.flCall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!TextUtils.isEmpty(items.get(position).getDriverNumber())){
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + items.get(position).getDriverNumber().trim()));
						context.startActivity(intent);
					}
				}
			});

		}

		/*viewholder.fromtolocationvalue.setText(items.get(position).getFromShortName()
				.toString().trim()
				+ " > "
				+ items.get(position).getToShortName().toString().trim());
		viewholder.	datetext.setText(items.get(position).getTravelDate().toString().trim());
		viewholder.timetext.setText(items.get(position).getTravelTime().toString().trim());

		try{
			String []arr=items.get(position).getSeat_Status().toString().trim().split("/");
			
			int total=Integer.parseInt(arr[1]);
			int filled=Integer.parseInt(arr[0]);
			int ava=total-filled;
			viewholder.seatstext.setText("Total seats : "+(total+StringTags.TAT_ADD_TOTAL));
			viewholder.tvAvSeats.setText("Available : "+ava);
			}catch(Exception e){
				viewholder.seatstext.setText("Total seats :");
				viewholder.tvAvSeats.setText("Available :");
			}*/
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RideDetailsModel rideDetailsModel = items
						.get(position);
				String mobileNumber = rideDetailsModel
						.getMobileNumber();

				if (mobileNumber.equalsIgnoreCase(MobileNumberstr)) {

					final Intent mainIntent = new Intent(context,
							XCheckPoolFragmentActivty.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
                    mainIntent.putExtra("comefrom","showhistory");

					context.startActivity(mainIntent);

				} else {

					final Intent mainIntent = new Intent(context,
							XMemberRideFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));
                    mainIntent.putExtra("comefrom","showhistory");

					context.startActivity(mainIntent);
				}
			}
		});

		return convertView;
	}
	
	/*public class ViewHolder{
	public 	CircularImageView myridesbannerimage;
	public 	TextView myridesbannerusername;
	public	TextView fromtolocationvalue;
	public	TextView datetext;
	public	TextView timetext;
	public	TextView seatstext;
	public	TextView tvAvSeats;
	}*/
	public class ViewHolder {

		/*public CircularImageView myridesbannerimage;
        public TextView myridesbannerusername;
        public TextView fromtolocationvalue;
        public TextView datetext;
        public TextView timetext;
        public TextView seatstext, tvAvSeats;*/
		private TextView tvUserName, tvCancel,tvDate,tvTime,tvPlaceFrom,tvPlaceTo,tvSeatAvLable,tvSeatCount,tvModelNumber,tvRegNo ;
		private ImageView ivUserImage,ivCabImage,ivCabTypeIcon;
		private LinearLayout llVehicleDetail;
		private FrameLayout flCall;
		private LinearLayout llSeatsAv;
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
			ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
			ivCabImage = (ImageView) view.findViewById(R.id.ivCabImage);
			llVehicleDetail = (LinearLayout)view.findViewById(R.id.llVehicleDetails);
			tvCancel = (TextView)view.findViewById(R.id.tvCancel);

			tvUserName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvDate.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvTime.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceFrom.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvPlaceTo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatAvLable.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvSeatCount.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvModelNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			tvRegNo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
			flCall = (FrameLayout)view.findViewById(R.id.flCall);
			llSeatsAv = (LinearLayout) view.findViewById(R.id.llSeatsAv);
			ivCabTypeIcon = (ImageView)view.findViewById(R.id.ivCabIcon);



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