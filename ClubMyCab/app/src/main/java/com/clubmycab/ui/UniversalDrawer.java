package com.clubmycab.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class UniversalDrawer {

	public static UniversalDrawer instance;

	private SimpleSideDrawer mNav;
	TextView drawerusername, tvEmail, tvMobileNumber;
	Tracker tracker;

	TextView myprofile;
	TextView sharemylocation;
	TextView myclubs;
	// TextView sharethisapp;
	TextView wallets;
	TextView mypreferences;
	TextView offers;
	// TextView about;
	private TextView faq, tvaskforquery;

	FrameLayout myclubslayout;
	//FrameLayout myrideslayout;
	FrameLayout myprofilelayout;
	FrameLayout mywalletslayout;
	FrameLayout settingslayout;
	// FrameLayout shareapplayout;
	FrameLayout offerslayout;
	private FrameLayout llaskforquery;
	// FrameLayout aboutlayout;
	FrameLayout faqlayout;

	FrameLayout sharelocationlayout;

	Context context;

	private boolean exceptioncheck;

	private String result;
	private SharedPreferences mPrefs;
	private String FullName,MobileNumber;
	private String Email;

	public UniversalDrawer(Context context, Tracker tracker) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.tracker = tracker;
		Log.d("UniversalDrawer", "ActivityName : "
				+ GlobalVariables.ActivityName);
	}

	public void createDrawer() {

		mNav = new SimpleSideDrawer((Activity) context);
		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		mPrefs = context.getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		Email = mPrefs.getString("Email","");
		((Activity) context).findViewById(R.id.sidemenu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

						mNav.toggleLeftDrawer();

					}
				});

		myclubslayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.myclubslayout);
	/*	myrideslayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.myrideslayout);*/
		myprofilelayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.myprofilelayout);
		mywalletslayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.mywalletslayout);
		settingslayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.settingslayout);
		// shareapplayout = (LinearLayout) ((Activity) context)
		// .findViewById(R.id.shareapplayout);
		// aboutlayout = (LinearLayout) ((Activity) context)
		// .findViewById(R.id.aboutlayout);
		faqlayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.faqlayout);
		llaskforquery = (FrameLayout) ((Activity) context)
				.findViewById(R.id.llaskforquery);
		sharelocationlayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.sharelocationlayout);
		offerslayout = (FrameLayout) ((Activity) context)
				.findViewById(R.id.offerslayout);

		myprofile = (TextView) ((Activity) context)
				.findViewById(R.id.myprofile);
		myprofile.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		drawerusername = (TextView)  ((Activity) context).findViewById(R.id.drawerusername);
		tvEmail = (TextView)  ((Activity) context).findViewById(R.id.tvEmail);
		tvMobileNumber = (TextView)  ((Activity) context).findViewById(R.id.tvMobNumber);
		drawerusername.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		tvEmail.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		tvMobileNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		drawerusername.setText(FullName);
		if(!TextUtils.isEmpty(MobileNumber)){
			tvMobileNumber.setText("+91 "+MobileNumber.substring(4));
		}
		if(!TextUtils.isEmpty(Email)){
			tvEmail.setText(Email);
		}

		sharemylocation = (TextView) ((Activity) context)
				.findViewById(R.id.sharemylocation);
		sharemylocation.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));
		myclubs = (TextView) ((Activity) context).findViewById(R.id.myclubs);
		myclubs.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));
		// sharethisapp = (TextView) ((Activity) context)
		// .findViewById(R.id.sharethisapp);
		// sharethisapp.setTypeface(Typeface.createFromAsset(context.getAssets(),
		// AppConstants.HELVITICA));
		wallets = (TextView) ((Activity) context).findViewById(R.id.mywallets);
		wallets.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));

		mypreferences = (TextView) ((Activity) context)
				.findViewById(R.id.mypreferences);
		mypreferences.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));
		// about = (TextView) ((Activity) context).findViewById(R.id.about);
		// about.setTypeface(Typeface.createFromAsset(context.getAssets(),
		// AppConstants.HELVITICA));

		faq = (TextView) ((Activity) context).findViewById(R.id.faq);
		faq.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));
		tvaskforquery = (TextView) ((Activity) context)
				.findViewById(R.id.tvaskforquery);
		tvaskforquery.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));

		offers = (TextView) ((Activity) context).findViewById(R.id.offers);
		offers.setTypeface(FontTypeface.getTypeface(context,AppConstants.HELVITICA));

		myprofilelayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();
				if (!GlobalVariables.ActivityName.equals("MyProfileActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("My Profile Opened")
							.setAction("My Profile Opened")
							.setLabel("My Profile Opened").build());

					Intent mainIntent = new Intent(context,
							MyProfileActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "MyProfileActivity";
				}
			}
		});

	/*	myrideslayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();
				Log.d("UniversalDrawer", "myrideslayout ActivityName : "
						+ GlobalVariables.ActivityName);

				if (!GlobalVariables.ActivityName.equals("MyRidesActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("MyRides Click")
							.setAction("MyRides Click")
							.setLabel("MyRides Click").build());

					Intent mainIntent = new Intent(context,
							MyRidesActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "MyRidesActivity";
				}
			}
		});*/

		myclubslayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("MyClubsActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("My Groups Opened")
							.setAction("My Groups pressed")
							.setLabel("My Groups Opened").build());

					Intent mainIntent = new Intent(context,
							XMyClubsActivty.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "MyClubsActivity";
				}
			}
		});

		// shareapplayout.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// if (!GlobalVariables.ActivityName
		// .equals("ShareThisAppActivity")) {
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ShareApp Click")
		// .setAction("ShareApp Click")
		// .setLabel("ShareApp Click").build());
		//
		// Intent sendIntent = new Intent();
		// sendIntent.setAction(Intent.ACTION_SEND);
		// sendIntent
		// .putExtra(
		// Intent.EXTRA_TEXT,
		// "I am using this cool app 'iShareRyde' to share & book cabs. Check it out @ "
		// + GlobalVariables.ShareThisAppLink);
		// sendIntent.setType("text/plain");
		// context.startActivity(Intent.createChooser(sendIntent,
		// "Share Via"));
		// }
		//
		// }
		// });

		mywalletslayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();
				if (!GlobalVariables.ActivityName.equals("WalletsAcitivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Wallet Opened")
							.setAction("Wallet pressed")
							.setLabel("Wallet Opened").build());

					Intent mainIntent = new Intent(context,
							PaymentMethodActivity.class);
					mainIntent.putExtra("from", "wallet");
					context.startActivity(mainIntent);

					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "WalletsAcitivity";
				}

			}
		});

		sharemylocation.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName
						.equals("ShareLocationFragmentActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Share my location opened")
							.setAction("Share my location pressed")
							.setLabel("Share my location opened").build());

					Intent mainIntent = new Intent(context,
							ShareLocationFragmentActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "ShareLocationFragmentActivity";
				}
			}
		});

		settingslayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("SettingActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Settings Opened")
							.setAction("Settings pressed")
							.setLabel("Settings Opened").build());

					Intent mainIntent = new Intent(context,
							SettingActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "SettingActivity";
				}

			}
		});
		// aboutlayout.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		//
		// mNav.toggleDrawer();
		//
		// if (!GlobalVariables.ActivityName
		// .equals("AboutPagerFragmentActivity")) {
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("About Click")
		// .setAction("About Click").setLabel("About Click")
		// .build());
		//
		// Intent mainIntent = new Intent(context,
		// AboutPagerFragmentActivity.class);
		// context.startActivity(mainIntent);
		// ((Activity) context).overridePendingTransition(
		// R.anim.slide_in_right, R.anim.slide_out_left);
		// GlobalVariables.ActivityName = "AboutPagerFragmentActivity";
		// }
		// }
		// });
		faqlayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("FAQActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("FAQs Opened").setAction("FAQs pressed")
							.setLabel("FAQs Opened").build());

					Intent mainIntent = new Intent(context, FAQActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "FAQActivity";
				}
			}
		});
		llaskforquery.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName
						.equals("ReportAProblemActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Support opened")
							.setAction("Support pressed")
							.setLabel("Support opened").build());

					Intent mainIntent = new Intent(context,
							NeedSupportFragmentActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "ReportAProblemActivity";
				}
			}
		});

		offerslayout.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("OffersListActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Offers Opened")
							.setAction("Offers pressed").setLabel("Offers Opened")
							.build());

					Intent mainIntent = new Intent(context,
							OffersListActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "OffersListActivity";
				}
			}
		});

	}

}
