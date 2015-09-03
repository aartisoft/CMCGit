package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
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
	// TextView about;
	private TextView faq, tvaskforquery;

	LinearLayout myclubslayout;
	LinearLayout myrideslayout;
	LinearLayout myprofilelayout;
	LinearLayout mywalletslayout;
	LinearLayout settingslayout;
	LinearLayout shareapplayout;
	private LinearLayout llaskforquery;
	// LinearLayout aboutlayout;
	LinearLayout faqlayout;

	LinearLayout sharelocationlayout;

	Context context;

	private boolean exceptioncheck;

	private String result;

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
		// aboutlayout = (LinearLayout) ((Activity) context)
		// .findViewById(R.id.aboutlayout);
		faqlayout = (LinearLayout) ((Activity) context)
				.findViewById(R.id.faqlayout);
		llaskforquery = (LinearLayout) ((Activity) context)
				.findViewById(R.id.llaskforquery);
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
		// about = (TextView) ((Activity) context).findViewById(R.id.about);
		// about.setTypeface(Typeface.createFromAsset(context.getAssets(),
		// "NeutraText-Light.ttf"));

		faq = (TextView) ((Activity) context).findViewById(R.id.faq);
		faq.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"NeutraText-Light.ttf"));
		tvaskforquery = (TextView) ((Activity) context)
				.findViewById(R.id.tvaskforquery);
		tvaskforquery.setTypeface(Typeface.createFromAsset(context.getAssets(),
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

				if (!GlobalVariables.ActivityName
						.equals("ShareThisAppActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("ShareApp Click")
							.setAction("ShareApp Click")
							.setLabel("ShareApp Click").build());

					Intent mainIntent = new Intent(context,
							ShareThisAppActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "ShareThisAppActivity";
				}

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
							FirstLoginWalletsActivity.class);
					mainIntent.putExtra("from", "wallet");
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

				if (!GlobalVariables.ActivityName
						.equals("ShareLocationFragmentActivity")) {
					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("ShareLocation Click")
							.setAction("ShareLocation Click")
							.setLabel("ShareLocation Click").build());

					Intent mainIntent = new Intent(context,
							ShareLocationFragmentActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "ShareLocationFragmentActivity";
				}
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
		faqlayout.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName.equals("FAQActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Faq Click").setAction("Faq Click")
							.setLabel("Faq Click").build());

					Intent mainIntent = new Intent(context, FAQActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "FAQActivity";
				}
			}
		});
		llaskforquery.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				mNav.toggleDrawer();

				if (!GlobalVariables.ActivityName
						.equals("ReportAProblemActivity")) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("ReportAProblem Click")
							.setAction("ReportAProblem Click")
							.setLabel("ReportAProblem Click").build());

					Intent mainIntent = new Intent(context,
							NeedSupportFragmentActivity.class);
					context.startActivity(mainIntent);
					((Activity) context).overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					GlobalVariables.ActivityName = "FAQActivity";
				}
			}
		});

	}

}
