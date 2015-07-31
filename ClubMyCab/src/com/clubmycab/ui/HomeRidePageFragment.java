package com.clubmycab.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.MemberRideFragmentActivity;
import com.clubmycab.R;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.gson.Gson;

public class HomeRidePageFragment extends Fragment {

	private String floc, tloc, tdate, ttime, sets, ownnam, ownimgnam;
	AQuery aq;

	public static final String ARGUMENTS_BITMAP = "HelpPageFragment.ArgumentsBitmap";

	public static HomeRidePageFragment newInstance(RideDetailsModel model) {

		Bundle args = new Bundle();
		args.putString("floc", model.getFromShortName());
		args.putString("tloc", model.getToShortName());
		args.putString("tdate", model.getTravelDate());
		args.putString("ttime", model.getTravelTime());
		args.putString("sets", model.getSeat_Status());
		args.putString("ownnam", model.getOwnerName());
		args.putString("ownimgnam", model.getImagename());

		HomeRidePageFragment helpPageFragment = new HomeRidePageFragment();
		helpPageFragment.setArguments(args);

		return helpPageFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		this.floc = getArguments().getString("floc");
		this.tloc = getArguments().getString("tloc");
		this.tdate = getArguments().getString("tdate");
		this.ttime = getArguments().getString("ttime");
		this.sets = getArguments().getString("sets");
		this.ownnam = getArguments().getString("ownnam");
		this.ownimgnam = getArguments().getString("ownimgnam");

		this.aq = new AQuery(getActivity());

		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View convertView = (View) inflater.inflate(R.layout.myrides_list_row,
				container, false);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				// mypoollist.setOnItemClickListener(new OnItemClickListener() {
				// @Override
				// public void onItemClick(AdapterView<?> arg0, View arg1,
				// int arg2, long arg3) {
				int arg2 = HomeActivity.viewPagerHome.getCurrentItem();
				// Toast.makeText(getActivity(), "call::"+arg2,
				// Toast.LENGTH_SHORT).show();

				Log.d("arg2", "" + arg2);

				RideDetailsModel rideDetailsModel = HomeActivity.arrayRideDetailsModels
						.get(arg2);
				String mobileNumber = rideDetailsModel.getMobileNumber();

				if (mobileNumber.equalsIgnoreCase("121")) {

					final Intent mainIntent = new Intent(getActivity(),
							CheckPoolFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));

					// mainIntent.putExtra("CabId", CabId.get(arg2));
					// mainIntent.putExtra("MobileNumber",
					// MobileNumber.get(arg2));
					// mainIntent.putExtra("OwnerName",
					// OwnerName.get(arg2));
					// mainIntent.putExtra("OwnerImage",
					// imagename.get(arg2));
					// mainIntent.putExtra("FromLocation",
					// FromLocation.get(arg2));
					// mainIntent.putExtra("ToLocation",
					// ToLocation.get(arg2));
					//
					// mainIntent.putExtra("FromShortName",
					// FromShortName.get(arg2));
					// mainIntent.putExtra("ToShortName",
					// ToShortName.get(arg2));
					//
					// mainIntent.putExtra("TravelDate",
					// TravelDate.get(arg2));
					// mainIntent.putExtra("TravelTime",
					// TravelTime.get(arg2));
					// mainIntent.putExtra("Seats", Seats.get(arg2));
					// mainIntent.putExtra("RemainingSeats",
					// RemainingSeats.get(arg2));
					// mainIntent.putExtra("Seat_Status",
					// Seat_Status.get(arg2));
					// mainIntent.putExtra("Distance",
					// Distance.get(arg2));
					// mainIntent.putExtra("OpenTime",
					// OpenTime.get(arg2));
					//
					// mainIntent.putExtra("CabStatus",
					// CabStatus.get(arg2));
					//
					// mainIntent.putExtra("BookingRefNo",
					// BookingRefNo.get(arg2));
					// mainIntent.putExtra("DriverName",
					// DriverName.get(arg2));
					// mainIntent.putExtra("DriverNumber",
					// DriverNumber.get(arg2));
					// mainIntent.putExtra("CarNumber",
					// CarNumber.get(arg2));
					// mainIntent.putExtra("CabName",
					// CabName.get(arg2));
					//
					// mainIntent.putExtra("ExpTripDuration",
					// ExpTripDuration.get(arg2));
					// mainIntent.putExtra("status", status.get(arg2));

					getActivity().startActivity(mainIntent);

				} else {

					final Intent mainIntent = new Intent(getActivity(),
							MemberRideFragmentActivity.class);
					mainIntent.putExtra("RideDetailsModel",
							(new Gson()).toJson(rideDetailsModel));

					// mainIntent.putExtra("CabId", CabId.get(arg2));
					// mainIntent.putExtra("MobileNumber",
					// MobileNumber.get(arg2));
					// mainIntent.putExtra("OwnerName",
					// OwnerName.get(arg2));
					// mainIntent.putExtra("OwnerImage",
					// imagename.get(arg2));
					// mainIntent.putExtra("FromLocation",
					// FromLocation.get(arg2));
					// mainIntent.putExtra("ToLocation",
					// ToLocation.get(arg2));
					//
					// mainIntent.putExtra("FromShortName",
					// FromShortName.get(arg2));
					// mainIntent.putExtra("ToShortName",
					// ToShortName.get(arg2));
					//
					// mainIntent.putExtra("TravelDate",
					// TravelDate.get(arg2));
					// mainIntent.putExtra("TravelTime",
					// TravelTime.get(arg2));
					// mainIntent.putExtra("Seats", Seats.get(arg2));
					// mainIntent.putExtra("RemainingSeats",
					// RemainingSeats.get(arg2));
					// mainIntent.putExtra("Seat_Status",
					// Seat_Status.get(arg2));
					// mainIntent.putExtra("Distance",
					// Distance.get(arg2));
					// mainIntent.putExtra("OpenTime",
					// OpenTime.get(arg2));
					//
					// mainIntent.putExtra("CabStatus",
					// CabStatus.get(arg2));
					//
					// mainIntent.putExtra("BookingRefNo",
					// BookingRefNo.get(arg2));
					// mainIntent.putExtra("DriverName",
					// DriverName.get(arg2));
					// mainIntent.putExtra("DriverNumber",
					// DriverNumber.get(arg2));
					// mainIntent.putExtra("CarNumber",
					// CarNumber.get(arg2));
					// mainIntent.putExtra("CabName",
					// CabName.get(arg2));
					//
					// mainIntent.putExtra("ExpTripDuration",
					// ExpTripDuration.get(arg2));
					// mainIntent.putExtra("status", status.get(arg2));

					getActivity().startActivity(mainIntent);
				}

			}
		});

		// ImageView imageView = (ImageView) view
		// .findViewById(R.id.imageViewHelpPage);
		//
		// imageView.setImageDrawable(mBitmapDrawable);
		//

		LinearLayout llCircularHeader = (LinearLayout) convertView
				.findViewById(R.id.llCircularHeader);
		llCircularHeader.setVisibility(View.GONE);
		CircularImageView myridesbannerimage = (CircularImageView) convertView
				.findViewById(R.id.myridesbannerimage);
		TextView myridesbannerusername = (TextView) convertView
				.findViewById(R.id.myridesbannerusername);
		TextView fromtolocationvalue = (TextView) convertView
				.findViewById(R.id.fromtolocationvalue);
		TextView datetext = (TextView) convertView.findViewById(R.id.datetext);
		TextView timetext = (TextView) convertView.findViewById(R.id.timetext);
		TextView seatstext = (TextView) convertView
				.findViewById(R.id.seatstext);
		TextView tvAvSeats = (TextView) convertView
				.findViewById(R.id.tvAvSeats);

		if (ownimgnam.trim().isEmpty()) {

			Log.d("image nahi hai", "" + ownimgnam.trim());

		} else {
			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ ownimgnam.trim();
			aq.id(myridesbannerimage).image(url, true, true);
		}
		myridesbannerusername.setText(ownnam.trim());
		fromtolocationvalue.setText(floc.trim() + " > " + tloc.trim());
		datetext.setText(tdate.trim());
		timetext.setText(ttime.trim());

		try {
			String[] arr = sets.split("/");

			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			seatstext.setText("Total seats : "
					+ (total + StringTags.TAT_ADD_TOTAL));
			tvAvSeats.setText("Available : " + ava);
		} catch (Exception e) {
			seatstext.setText("Total seats :");
			tvAvSeats.setText("Available :");
		}

		return convertView;
	}

}
