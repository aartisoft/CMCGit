package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.BookaCabFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class MyProfileActivity extends Activity {
	TextView fullnametxt;
	TextView emailtxt;
	TextView dobtxt;
	TextView mobiletxt;
	TextView gendertxt;

	EditText fullnameedittext;
	EditText emailedittext;
	EditText dobedittext;
	EditText mobileedittext;
	EditText genderedittext;

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	ImageView sidemenu;

	CircularImageView profilebannerimage;
	TextView profilebannerusername;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;

	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView mypreferences;
	TextView about;

	String FullName;
	String MobileNumberstr;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Button updateprofile;

	String imagenameresp;
	String myprofileresp;
	String updateprofileresp;

	Calendar myCalendar = Calendar.getInstance();

	String readunreadnotiresp;
	Bitmap mIcon11;

	
	RelativeLayout myprofilerl;

	String imageuploadchkstr;
	Bitmap mainbmp = null;
	String imageuploadresp;
	Tracker tracker;
	
	boolean exceptioncheck = false;

	DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			String myFormat = "dd/MM/yyyy"; // In which you need put here
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

			dobedittext.setText(sdf.format(myCalendar.getTime()));
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MyProfileActivity.this);
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							finish();

							startActivity(intent);

						}
					});

			builder.show();
			return;
		}
		


		GoogleAnalytics analytics = GoogleAnalytics.getInstance(MyProfileActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyProfile");

		myprofilerl = (RelativeLayout) findViewById(R.id.myprofilerl);
		myprofilerl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("myprofilerl", "myprofilerl");
			}
		});

		

//		mNav = new SimpleSideDrawer(this);
//		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
//
//		findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				// mainhomepagerl.setAlpha((float) 0.3);
//				mNav.toggleLeftDrawer();
//
//			}
//		});
//
//		myprofile = (TextView) findViewById(R.id.myprofile);
//		myprofile.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		myrides = (TextView) findViewById(R.id.myrides);
//		myrides.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		bookacab = (TextView) findViewById(R.id.bookacab);
//		bookacab.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		sharemylocation = (TextView) findViewById(R.id.sharemylocation);
//		sharemylocation.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		myclubs = (TextView) findViewById(R.id.myclubs);
//		myclubs.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		sharethisapp = (TextView) findViewById(R.id.sharethisapp);
//		sharethisapp.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		mypreferences = (TextView) findViewById(R.id.mypreferences);
//		mypreferences.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//		about = (TextView) findViewById(R.id.about);
//		about.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));
//
//		myprofile.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//			}
//		});
//
//		myrides.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("MyRides Click")
//						.setAction("MyRides Click").setLabel("MyRides Click")
//						.build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this, MyRidesActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
//
//		bookacab.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("BookaCab Click")
//						.setAction("BookaCab Click").setLabel("BookaCab Click")
//						.build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this, BookaCabFragmentActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
//
//		sharemylocation.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("ShareLocation Click")
//						.setAction("ShareLocation Click")
//						.setLabel("ShareLocation Click").build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this,
//						ShareLocationFragmentActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
//
//		myclubs.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("MyClubs Click")
//						.setAction("MyClubs Click").setLabel("MyClubs Click")
//						.build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this, MyClubsActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
//
//		sharethisapp.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("ShareApp Click")
//						.setAction("ShareApp Click").setLabel("ShareApp Click")
//						.build());
//
//				Intent sendIntent = new Intent();
//				sendIntent.setAction(Intent.ACTION_SEND);
//				sendIntent
//						.putExtra(
//								Intent.EXTRA_TEXT,
//								"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ http://tinyurl.com/n7j6chq");
//				sendIntent.setType("text/plain");
//				startActivity(Intent.createChooser(sendIntent, "Share Via"));
//
//			}
//		});
//
//		mypreferences.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("Settings Click")
//						.setAction("Settings Click").setLabel("Settings Click")
//						.build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this,
//						SettingActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
//
//		about.setOnClickListener(new View.OnClickListener() {
//			@SuppressWarnings("deprecation")
//			@Override
//			public void onClick(View arg0) {
//				mNav.toggleDrawer();
//
//				tracker.send(new HitBuilders.EventBuilder()
//						.setCategory("About Click").setAction("About Click")
//						.setLabel("About Click").build());
//
//				Intent mainIntent = new Intent(MyProfileActivity.this,
//						AboutPagerFragmentActivity.class);
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//			}
//		});
		
		UniversalDrawer drawer = new UniversalDrawer(this,tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		profilebannerimage = (CircularImageView) findViewById(R.id.profilebannerimage);
		profilebannerusername = (TextView) findViewById(R.id.profilebannerusername);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

		profilebannerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		profilebannerusername.setText(FullName);

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(MyProfileActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		
		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		fullnametxt = (TextView) findViewById(R.id.fullnametxt);
		emailtxt = (TextView) findViewById(R.id.emailtxt);
		dobtxt = (TextView) findViewById(R.id.dobtxt);
		mobiletxt = (TextView) findViewById(R.id.mobiletxt);
		gendertxt = (TextView) findViewById(R.id.gendertxt);

		fullnameedittext = (EditText) findViewById(R.id.fullnameedittext);
		emailedittext = (EditText) findViewById(R.id.emailedittext);
		dobedittext = (EditText) findViewById(R.id.dobedittext);
		mobileedittext = (EditText) findViewById(R.id.mobileedittext);
		genderedittext = (EditText) findViewById(R.id.genderedittext);

		fullnameedittext.setEnabled(false);
		mobileedittext.setEnabled(false);

		updateprofile = (Button) findViewById(R.id.updateprofile);

		fullnametxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		fullnameedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		emailtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		emailedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		dobtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		dobedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		mobiletxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		mobileedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		gendertxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		genderedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		updateprofile.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		dobedittext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new DatePickerDialog(MyProfileActivity.this, date1, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		dobedittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					new DatePickerDialog(MyProfileActivity.this, date1, myCalendar
							.get(Calendar.YEAR),
							myCalendar.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			}

		});

		// /////////////

		genderedittext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MyProfileActivity.this);
				builder.setTitle("Select Gender");

				final CharSequence str[] = { "Male", "Female" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub
						genderedittext.setText(str[position]);
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});

		genderedittext
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {

							// TODO Auto-generated method stub
							AlertDialog dialog;
							AlertDialog.Builder builder = new AlertDialog.Builder(
									MyProfileActivity.this);
							builder.setTitle("Select Gender");

							final CharSequence str[] = { "Male", "Female" };

							builder.setItems(str,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int position) {
											// TODO Auto-generated method stub
											genderedittext
													.setText(str[position]);
										}
									});

							dialog = builder.create();
							dialog.show();

						}
					}

				});

		updateprofile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method

				if (emailedittext.getText().toString().trim().isEmpty()
						&& genderedittext.getText().toString().trim().isEmpty()
						&& dobedittext.getText().toString().trim().isEmpty()) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MyProfileActivity.this);

					builder.setMessage("Please enter the details");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else {

					String eml = emailedittext.getText().toString().trim();
					String gnd = genderedittext.getText().toString().trim();
					String dob = dobedittext.getText().toString().trim();

					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MyProfileActivity.this);
						builder.setTitle("Internet Connection Error");
						builder.setMessage("ClubMyCab requires Internet connection");
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();

						return;
					} else {

						Log.d("all set", "all set");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForUpdateMyProfile()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											eml, dob, gnd);
						} else {
							new ConnectionTaskForUpdateMyProfile().execute(eml,
									dob, gnd);
						}
					}

				}

			}
		});

		// ///////////////
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			new ConnectionTaskForreadunreadnotification()
//					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		} else {
//			new ConnectionTaskForreadunreadnotification().execute();
//		}

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imgname = mPrefs111.getString("imgname", "");
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

			// new ConnectionTaskForfetchimagename().execute();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForfetchimagename()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForfetchimagename().execute();
			}

		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
			profilebannerimage.setImageBitmap(yourSelectedImage);
		}

		profilebannerimage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				selectImage();
			}
		});

		// ///////////
		SharedPreferences mPrefs1111 = getSharedPreferences("MyProfile", 0);
		String myprofile = mPrefs1111.getString("myprofile", "");

		if (myprofile.isEmpty() || myprofile == null
				|| myprofile.equalsIgnoreCase("")) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchMyProfile()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchMyProfile().execute();
			}

		} else {

			if (myprofile.equalsIgnoreCase("No Data")) {
				Toast.makeText(MyProfileActivity.this, "" + myprofile,
						Toast.LENGTH_LONG).show();
			} else {
				JSONArray subArray = null;
				try {
					subArray = new JSONArray(myprofile);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < subArray.length(); i++) {
					try {

						fullnameedittext.setText(subArray.getJSONObject(i)
								.getString("FullName").toString().trim());
						mobileedittext.setText(subArray.getJSONObject(i)
								.getString("MobileNumber").toString().trim()
								.substring(4));
						emailedittext.setText(subArray.getJSONObject(i)
								.getString("Email").toString().trim());
						genderedittext.setText(subArray.getJSONObject(i)
								.getString("Gender").toString().trim());
						dobedittext.setText(subArray.getJSONObject(i)
								.getString("DOB").toString().trim());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}

	// ////////////////////////
	// ///////
	private class ConnectionTaskForfetchimagename extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionfetchimagename mAuth1 = new AuthenticateConnectionfetchimagename();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			
			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
				return;
			}

			if (imagenameresp == null) {

				profilepic.setImageResource(R.drawable.cabappicon);
				drawerprofilepic.setImageResource(R.drawable.cabappicon);
				profilebannerimage.setImageResource(R.drawable.cabappicon);

			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);
				profilebannerimage.setImageBitmap(mIcon11);
			}

		}
	}

	public class AuthenticateConnectionfetchimagename {

		public AuthenticateConnectionfetchimagename() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchimagename.php";
			HttpPost httpPost11 = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.d("httpResponse", "" + httpResponse11);

			InputStream inputStream11 = httpResponse11.getEntity().getContent();
			InputStreamReader inputStreamReader11 = new InputStreamReader(
					inputStream11);

			BufferedReader bufferedReader11 = new BufferedReader(
					inputStreamReader11);

			StringBuilder stringBuilder11 = new StringBuilder();

			String bufferedStrChunk11 = null;

			while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
				imagenameresp = stringBuilder11.append(bufferedStrChunk11)
						.toString();
			}

			Log.d("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else {
				String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ imagenameresp;
				String urldisplay = url1.toString().trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imagenameresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}

		}
	}

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// ///////
//	private class ConnectionTaskForreadunreadnotification extends
//			AsyncTask<String, Void, Void> {
//
//		@Override
//		protected void onPreExecute() {
//
//		}
//
//		@Override
//		protected Void doInBackground(String... args) {
//			AuthenticateConnectionreadunreadnotification mAuth1 = new AuthenticateConnectionreadunreadnotification();
//			try {
//				mAuth1.connection();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				exceptioncheck = true;
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void v) {
//
//			if (exceptioncheck) {
//				exceptioncheck = false;
//				Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
//				return;
//			}
//			
//			if (readunreadnotiresp.equalsIgnoreCase("0")) {
//
//				unreadnoticountrl.setVisibility(View.GONE);
//
//			} else {
//
//				unreadnoticountrl.setVisibility(View.VISIBLE);
//				unreadnoticount.setText(readunreadnotiresp);
//			}
//		}
//
//	}
//
//	public class AuthenticateConnectionreadunreadnotification {
//
//		public AuthenticateConnectionreadunreadnotification() {
//
//		}
//
//		public void connection() throws Exception {
//
//			// Connect to google.com
//			HttpClient httpClient = new DefaultHttpClient();
//			String url_select11 = GlobalVariables.ServiceUrl
//					+ "/FetchUnreadNotificationCount.php";
//			HttpPost httpPost = new HttpPost(url_select11);
//			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
//					"MobileNumber", MobileNumberstr);
//
//			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
//			nameValuePairList.add(MobileNumberBasicNameValuePair);
//
//			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
//					nameValuePairList);
//			httpPost.setEntity(urlEncodedFormEntity);
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//
//			Log.d("httpResponse", "" + httpResponse);
//
//			InputStream inputStream = httpResponse.getEntity().getContent();
//			InputStreamReader inputStreamReader = new InputStreamReader(
//					inputStream);
//
//			BufferedReader bufferedReader = new BufferedReader(
//					inputStreamReader);
//
//			StringBuilder stringBuilder = new StringBuilder();
//
//			String bufferedStrChunk = null;
//
//			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
//				readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
//						.toString();
//			}
//
//			Log.d("readunreadnotiresp", "" + readunreadnotiresp);
//
//		}
//	}

	// ///////

	private class ConnectionTaskForFetchMyProfile extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyProfileActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyProfile mAuth1 = new AuthenticateConnectionFetchMyProfile();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			
			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
				return;
			}

			if (myprofileresp.equalsIgnoreCase("No Data")) {
				Toast.makeText(MyProfileActivity.this, "" + myprofileresp,
						Toast.LENGTH_LONG).show();
			} else {
				JSONArray subArray = null;
				try {
					subArray = new JSONArray(myprofileresp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < subArray.length(); i++) {
					try {

						fullnameedittext.setText(subArray.getJSONObject(i)
								.getString("FullName").toString().trim());
						mobileedittext.setText(subArray.getJSONObject(i)
								.getString("MobileNumber").toString().trim()
								.substring(4));
						emailedittext.setText(subArray.getJSONObject(i)
								.getString("Email").toString().trim());
						genderedittext.setText(subArray.getJSONObject(i)
								.getString("Gender").toString().trim());
						dobedittext.setText(subArray.getJSONObject(i)
								.getString("DOB").toString().trim());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public class AuthenticateConnectionFetchMyProfile {

		public AuthenticateConnectionFetchMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchmyprofile.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myprofileresp", "" + stringBuilder.toString());

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyProfile", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("myprofile", myprofileresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	// ///////

	private class ConnectionTaskForUpdateMyProfile extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyProfileActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionUpdateMyProfile mAuth1 = new AuthenticateConnectionUpdateMyProfile();
			try {
				mAuth1.email = args[0];
				mAuth1.dob = args[1];
				mAuth1.gender = args[2];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
				return;
			}
			
			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Update Profile").setAction("Update Profile")
					.setLabel("Update Profile").build());

			if (updateprofileresp.equalsIgnoreCase("update success")) {

				Toast.makeText(MyProfileActivity.this, "Profile updated successfully",
						Toast.LENGTH_LONG).show();
			} else {

				Toast.makeText(MyProfileActivity.this, "" + updateprofileresp,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public class AuthenticateConnectionUpdateMyProfile {

		public String email;
		public String dob;
		public String gender;

		public AuthenticateConnectionUpdateMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/updatemyprofile.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);
			BasicNameValuePair EmailBasicNameValuePair = new BasicNameValuePair(
					"Email", email);
			BasicNameValuePair GenderBasicNameValuePair = new BasicNameValuePair(
					"Gender", gender);
			BasicNameValuePair DOBBasicNameValuePair = new BasicNameValuePair(
					"DOB", dob);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);
			nameValuePairList.add(EmailBasicNameValuePair);
			nameValuePairList.add(GenderBasicNameValuePair);
			nameValuePairList.add(DOBBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				updateprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("updateprofileresp", "" + stringBuilder.toString());

			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select111 = GlobalVariables.ServiceUrl
					+ "/fetchmyprofile.php";
			HttpPost httpPost1 = new HttpPost(url_select111);
			BasicNameValuePair UserNumberBasicNameValuePair1 = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair1);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myprofileresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myprofileresp", "" + stringBuilder1.toString());

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyProfile", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("myprofile", myprofileresp.toString().trim());
			editor1.commit();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent mainIntent = new Intent(MyProfileActivity.this, HomeActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			mainbmp = null;
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {

					Log.d("f.getAbsolutePath()", "" + f.getAbsolutePath());
					imageuploadchkstr = "fromcamera";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								f.getAbsolutePath());
					} else {
						new ConnectionTaskForImageUpload().execute(f
								.getAbsolutePath());
					}

					/*
					 * Bitmap bitmapOrg; BitmapFactory.Options bitmapOptions =
					 * new BitmapFactory.Options();
					 * 
					 * bitmapOrg = BitmapFactory.decodeFile(f.getAbsolutePath(),
					 * bitmapOptions);
					 * 
					 * int width = bitmapOrg.getWidth(); int height =
					 * bitmapOrg.getHeight(); int newWidth = 200; int newHeight
					 * = 200;
					 * 
					 * // calculate the scale - in this case = 0.4f float
					 * scaleWidth = ((float) newWidth) / width; float
					 * scaleHeight = ((float) newHeight) / height;
					 * 
					 * // createa matrix for the manipulation Matrix matrix =
					 * new Matrix(); // resize the bit map
					 * matrix.postScale(scaleWidth, scaleHeight); // rotate the
					 * Bitmap // matrix.postRotate(45);
					 * 
					 * // recreate the new Bitmap Bitmap resizedBitmap =
					 * Bitmap.createBitmap(bitmapOrg, 0, 0, width, height,
					 * matrix, true);
					 * 
					 * mainbmp = resizedBitmap;
					 * 
					 * profilepic.setImageBitmap(mainbmp);
					 * 
					 * String imgString = Base64.encodeToString(
					 * getBytesFromBitmap(mainbmp), Base64.NO_WRAP);
					 * 
					 * if (Build.VERSION.SDK_INT >=
					 * Build.VERSION_CODES.HONEYCOMB) { new
					 * ConnectionTaskForImageUpload().executeOnExecutor(
					 * AsyncTask.THREAD_POOL_EXECUTOR, imgString); } else { new
					 * ConnectionTaskForImageUpload().execute(imgString); }
					 */} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();

				if (picturePath.toString().trim().contains("http")) {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromurl";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				} else {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromgallerynormal";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				}
			}
		} else {
			mainbmp = null;
			Log.d("Result not ok", "Result not ok");
		}

	}

	// ///////

	private class ConnectionTaskForImageUpload extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyProfileActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionImageUpload mAuth1 = new AuthenticateConnectionImageUpload();
			try {
				mAuth1.picturePath = args[0];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			
			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyProfileActivity.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
				return;
			}

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Change Profile Image")
					.setAction("Change Profile Image")
					.setLabel("Change Profile Image").build());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

				Toast.makeText(
						MyProfileActivity.this,
						"Error uploading Image, Please try again or use a different image",
						Toast.LENGTH_SHORT).show();
			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);
				profilebannerimage.setImageBitmap(mIcon11);

				Toast.makeText(MyProfileActivity.this, "Image Uploaded",
						Toast.LENGTH_SHORT).show();

				if (mIcon11 != null) {
					mIcon11 = null;
				}
			}
		}

	}

	public class AuthenticateConnectionImageUpload {

		String picturePath;

		public AuthenticateConnectionImageUpload() {

		}

		public void connection() throws Exception {

			String imagestr = null;

			if (imageuploadchkstr.toString().trim().equalsIgnoreCase("fromurl")) {
				URL url = new URL(picturePath);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				Log.d("img width", "" + myBitmap.getWidth());
				Log.d("img height", "" + myBitmap.getHeight());

				int width = 0;
				int height = 0;

				if (myBitmap.getWidth() <= myBitmap.getHeight()) {

					width = 200;
					height = (myBitmap.getHeight() * 200) / myBitmap.getWidth();
				} else {
					width = (myBitmap.getWidth() * 200) / myBitmap.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				myBitmap = scaleBitmap(myBitmap, width, height);

				Log.d("resize width", "" + myBitmap.getWidth());
				Log.d("resize height", "" + myBitmap.getHeight());

				mainbmp = myBitmap;
				if (myBitmap != null) {
					myBitmap = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			} else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromgallerynormal")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromcamera")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			if (mainbmp != null) {
				mainbmp = null;
			}

			String url_select = GlobalVariables.ServiceUrl + "/imageupload.php";

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);
			BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
					"imagestr", imagestr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(ImageBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				imageuploadresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("imageuploadresp", "" + stringBuilder.toString());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

			} else {

				String urldisplay = GlobalVariables.ServiceUrl
						+ "/ProfileImages/" + imageuploadresp.trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imageuploadresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}
		}
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);

		float scaleX = newWidth / (float) bitmap.getWidth();
		float scaleY = newHeight / (float) bitmap.getHeight();
		float pivotX = 0;
		float pivotY = 0;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}
}
