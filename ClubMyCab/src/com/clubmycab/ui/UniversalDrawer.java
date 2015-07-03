package com.clubmycab.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;
import com.clubmycab.utility.GlobalVariables;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class UniversalDrawer {

	public static UniversalDrawer instance;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;
	Tracker tracker;

	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView wallets;
	TextView mypreferences;
	TextView about;

	LinearLayout myclubslayout;
	LinearLayout myrideslayout;
	LinearLayout myprofilelayout;
	LinearLayout mywalletslayout;
	LinearLayout settingslayout;
	LinearLayout shareapplayout;
	LinearLayout aboutlayout;
	LinearLayout sharelocationlayout;

	Context context;

	public UniversalDrawer(Context context, Tracker tracker) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.tracker = tracker;
	}

	public void createDrawer() {

		mNav = new SimpleSideDrawer((Activity) context);
		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);

		((Activity) context).findViewById(R.id.sidemenu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

						mNav.toggleLeftDrawer();

					}
				});

		myclubslayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.myclubslayout);
		myrideslayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.myrideslayout);
		myprofilelayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.myprofilelayout);
		mywalletslayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.mywalletslayout);
		settingslayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.settingslayout);
		shareapplayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.shareapplayout);
		aboutlayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.aboutlayout);
		sharelocationlayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.sharelocationlayout);

		myprofile = (TextView) ((Activity) context)
				.findViewById(R.id.myprofile);
		myprofile.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		myrides = (TextView) ((Activity) context).findViewById(R.id.myrides);
		myrides.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		bookacab = (TextView) ((Activity) context).findViewById(R.id.bookacab);
		bookacab.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		sharemylocation = (TextView) ((Activity) context)
				.findViewById(R.id.sharemylocation);
		sharemylocation.setTypeface(Typeface.createFromAsset(
				context.getAssets(), "NeutraText-Light.ttf"));
		myclubs = (TextView) ((Activity) context).findViewById(R.id.myclubs);
		myclubs.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		sharethisapp = (TextView) ((Activity) context)
				.findViewById(R.id.sharethisapp);
		sharethisapp.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		wallets = (TextView) ((Activity) context).findViewById(R.id.mywallets);
		wallets.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));

		mypreferences = (TextView) ((Activity) context)
				.findViewById(R.id.mypreferences);
		mypreferences.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		about = (TextView) ((Activity) context).findViewById(R.id.about);
		about.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));

		myprofilelayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();
				if (!GlobalVariables.ActivityName.equals("MyProfileActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("MyProfile Click")
							.setAction("MyProfile Click")
							.setLabel("MyProfile Click").build());

					Intent mainIntent = new Intent(context,
							MyProfileActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "MyProfileActivity";
				}
			}
		});

		myrideslayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

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
		});

		myclubslayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("MyClubsActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("MyClubs Click")
							.setAction("MyClubs Click")
							.setLabel("MyClubs Click").build());

					Intent mainIntent = new Intent(context,
							MyClubsActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "MyClubsActivity";
				}
			}
		});

		shareapplayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				Intent mainIntent = new Intent(context,
						FavoriteLocationsAcivity.class);
				context.startActivity(mainIntent);
				((Activity) context).overridePendingTransition(
						R.anim.slide_in_right, R.anim.slide_out_left);

				// tracker.send(new HitBuilders.EventBuilder()
				// .setCategory("ShareApp Click")
				// .setAction("ShareApp Click").setLabel("ShareApp Click")
				// .build());
				//
				// Intent sendIntent = new Intent();
				// sendIntent.setAction(Intent.ACTION_SEND);
				// sendIntent
				// .putExtra(
				// Intent.EXTRA_TEXT,
				// "I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ http://tinyurl.com/n7j6chq");
				// sendIntent.setType("text/plain");
				// context.startActivity(Intent.createChooser(sendIntent,
				// "Share Via"));

			}
		});

		mywalletslayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();
				if (!GlobalVariables.ActivityName.equals("WalletsAcitivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Wallets Click")
							.setAction("Wallets Click")
							.setLabel("Wallets Click").build());

					Intent mainIntent = new Intent(context,
							WalletsAcitivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "WalletsAcitivity";
				}

			}
		});

		sharemylocation.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("ShareLocation Click")
						.setAction("ShareLocation Click")
						.setLabel("ShareLocation Click").build());

				Intent mainIntent = new Intent(context,
						ShareLocationFragmentActivity.class);
				context.startActivity(mainIntent);
				((Activity) context).overridePendingTransition(
						R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		settingslayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("SettingActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Settings Click")
							.setAction("Settings Click")
							.setLabel("Settings Click").build());

					Intent mainIntent = new Intent(context,
							SettingActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "SettingActivity";
				}

			}
		});
		aboutlayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName
						.equals("AboutPagerFragmentActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("About Click")
							.setAction("About Click").setLabel("About Click")
							.build());

					Intent mainIntent = new Intent(context,
							AboutPagerFragmentActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "AboutPagerFragmentActivity";
				}
			}
		});

	}

}
