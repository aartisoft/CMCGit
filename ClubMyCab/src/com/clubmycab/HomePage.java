package com.clubmycab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class HomePage extends Activity {

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	LinearLayout homeclubmycabll;
	LinearLayout homebookacabll;
	LinearLayout homehereiamll;

	ImageView sidemenu;
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
	String MobileNumber;

	Bitmap mainbmp = null;

	String imageuploadresp;

	String readunreadnotiresp;
	String imagenameresp;
	String myclubsresp;

	private static long back_pressed;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Bitmap mIcon11 = null;

	UrlConstant checkurl;

	RelativeLayout homepagerl;

	String imageuploadchkstr;

	Tracker tracker;

	AppEventsLogger logger;
	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
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

		// ////////////////////

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(HomePage.this);
		tracker = analytics.newTracker("UA-63477985-1");

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("HomePage");

		logger = AppEventsLogger.newLogger(this);

		// tracker.send(new HitBuilders.EventBuilder().setCategory("UX")
		// .setAction("click").setLabel("submit").build());
		//
		// // Builder parameters can overwrite the screen name set on the
		// tracker.
		// tracker.send(new HitBuilders.EventBuilder().setCategory("UX")
		// .setAction("click").setLabel("help popup")
		// .setScreenName("help popup dialog").build());

		// ///////////////////

		homepagerl = (RelativeLayout) findViewById(R.id.homepagerl);
		homepagerl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.e("homepagerl", "homepagerl");
			}
		});

		checkurl = new UrlConstant();

		mNav = new SimpleSideDrawer(this);
		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);

		findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mNav.toggleLeftDrawer();

			}
		});

		myprofile = (TextView) findViewById(R.id.myprofile);
		myprofile.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myrides = (TextView) findViewById(R.id.myrides);
		myrides.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		bookacab = (TextView) findViewById(R.id.bookacab);
		bookacab.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		sharemylocation = (TextView) findViewById(R.id.sharemylocation);
		sharemylocation.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myclubs = (TextView) findViewById(R.id.myclubs);
		myclubs.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		sharethisapp = (TextView) findViewById(R.id.sharethisapp);
		sharethisapp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		mypreferences = (TextView) findViewById(R.id.mypreferences);
		mypreferences.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		about = (TextView) findViewById(R.id.about);
		about.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		myprofile.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyProfile Click")
						.setAction("MyProfile Click")
						.setLabel("MyProfile Click").build());

				Intent mainIntent = new Intent(HomePage.this, MyProfile.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		myrides.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyRides Click")
						.setAction("MyRides Click").setLabel("MyRides Click")
						.build());

				Intent mainIntent = new Intent(HomePage.this, MyRides.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		bookacab.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("BookaCab Click")
						.setAction("BookaCab Click").setLabel("BookaCab Click")
						.build());

				Intent mainIntent = new Intent(HomePage.this, BookaCab.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
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

				Intent mainIntent = new Intent(HomePage.this,
						ShareLocation.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		myclubs.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyClubs Click")
						.setAction("MyClubs Click").setLabel("MyClubs Click")
						.build());

				Intent mainIntent = new Intent(HomePage.this, MyClubs.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		sharethisapp.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("ShareApp Click")
						.setAction("ShareApp Click").setLabel("ShareApp Click")
						.build());

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ http://tinyurl.com/n7j6chq");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share Via"));

			}
		});

		mypreferences.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Settings Click")
						.setAction("Settings Click").setLabel("Settings Click")
						.build());

				Intent mainIntent = new Intent(HomePage.this,
						SettingDetails.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		about.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("About Click").setAction("About Click")
						.setLabel("About Click").build());

				Intent mainIntent = new Intent(HomePage.this,
						MainActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		homeclubmycabll = (LinearLayout) findViewById(R.id.homeclubmycabll);
		homebookacabll = (LinearLayout) findViewById(R.id.homebookacabll);
		homehereiamll = (LinearLayout) findViewById(R.id.homehereiamll);

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setText(FullName);

		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		homeclubmycabll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						HomePage.this, R.anim.button_click_anim);
				homeclubmycabll.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("ClubMyCab Click")
								.setAction("ClubMyCab Click")
								.setLabel("ClubMyCab Click").build());

						logger.logEvent("HomePage ClubMyCab Click");

						Intent mainIntent = new Intent(HomePage.this,
								Invite.class);
						startActivityForResult(mainIntent, 500);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		homebookacabll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						HomePage.this, R.anim.button_click_anim);
				homebookacabll.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("Book A Cab (HomePage)")
								.setAction("BookaCab Click")
								.setLabel("BookaCab Click").build());

						logger.logEvent("HomePage BookaCab Click");

						Intent mainIntent = new Intent(HomePage.this,
								BookaCab.class);
						startActivityForResult(mainIntent, 500);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		homehereiamll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						HomePage.this, R.anim.button_click_anim);
				homehereiamll.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("ShareLocation (HomePage)")
								.setAction("ShareLocation (HomePage)")
								.setLabel("ShareLocation (HomePage)").build());

						logger.logEvent("HomePage ShareLocation Click");

						Intent mainIntent = new Intent(HomePage.this,
								ShareLocation.class);
						startActivityForResult(mainIntent, 500);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		profilepic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				selectImage();
			}
		});

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(HomePage.this,
						AllNotificationRequest.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		// ///////////////
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForreadunreadnotification()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForreadunreadnotification().execute();
		}

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

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
			if (yourSelectedImage != null) {
				yourSelectedImage = null;
			}
		}

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
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchMyClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchMyClubs().execute();
		}
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
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
			Log.e("Result not ok", "Result not ok");
		}

	}

	// ///////

	private class ConnectionTaskForImageUpload extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(HomePage.this);

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
				Toast.makeText(HomePage.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (imageuploadresp.equalsIgnoreCase("Error")) {

				Toast.makeText(
						HomePage.this,
						"Error uploading Image, Please try again or use a different image",
						Toast.LENGTH_SHORT).show();
			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);

				Toast.makeText(HomePage.this, "Image Uploaded",
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

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("ImageUpload Click")
					.setAction("ImageUpload Click")
					.setLabel("ImageUpload Click").build());

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

			String url_select = checkurl.GetServiceUrl() + "/imageupload.php";

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);
			BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
					"imagestr", imagestr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(ImageBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

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

			Log.e("imageuploadresp", "" + stringBuilder.toString());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

			} else {

				String urldisplay = checkurl.GetServiceUrl()
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

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 80, stream);
		return stream.toByteArray();
	}

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			Toast.makeText(getBaseContext(), "Press once again to exit!",
					Toast.LENGTH_SHORT).show();
			back_pressed = System.currentTimeMillis();
		}
	}

	// ///////
	private class ConnectionTaskForreadunreadnotification extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionreadunreadnotification mAuth1 = new AuthenticateConnectionreadunreadnotification();
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
				Toast.makeText(HomePage.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (readunreadnotiresp.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount.setText(readunreadnotiresp);
			}
		}

	}

	public class AuthenticateConnectionreadunreadnotification {

		public AuthenticateConnectionreadunreadnotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();

			String url_select = checkurl.GetServiceUrl()
					+ "/FetchUnreadNotificationCount.php";

			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.e("readunreadnotiresp", "" + readunreadnotiresp);

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
				Toast.makeText(HomePage.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (imagenameresp == null) {

				profilepic.setImageResource(R.drawable.cabappicon);
				drawerprofilepic.setImageResource(R.drawable.cabappicon);

			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);
			}

		}
	}

	public class AuthenticateConnectionfetchimagename {

		public AuthenticateConnectionfetchimagename() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select = checkurl.GetServiceUrl()
					+ "/fetchimagename.php";
			HttpPost httpPost11 = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair(
					"MobileNumber", MobileNumber);

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.e("httpResponse", "" + httpResponse11);

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

			Log.e("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else {

				String url1 = checkurl.GetServiceUrl() + "/ProfileImages/"
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

	// ///////
	private class ConnectionTaskForFetchMyProfile extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
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

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(HomePage.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	public class AuthenticateConnectionFetchMyProfile {

		public AuthenticateConnectionFetchMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = checkurl.GetServiceUrl()
					+ "/fetchmyprofile.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumber);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String myprofileresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.e("myprofileresp", "" + myprofileresp);

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyProfile", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("myprofile", myprofileresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// ////////////////////
	// ///////
	private class ConnectionTaskForFetchMyClubs extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyClubs mAuth1 = new AuthenticateConnectionFetchMyClubs();
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
				Toast.makeText(HomePage.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (myclubsresp.equalsIgnoreCase("No Users of your Club")) {

				SharedPreferences mPrefs11111 = getSharedPreferences(
						"NoClubsAlert", 0);
				String AlertPreference = mPrefs11111.getString(
						"AlertPreference", "");
				Log.d("HomePage", "AlertPreference : " + AlertPreference);

				if (AlertPreference.isEmpty()) {
					showNoClubDialog();
				} else if (AlertPreference.contains("Remind")) {
					String[] string = AlertPreference.split("\\|");
					long diff = System.currentTimeMillis()
							- Long.parseLong(string[1]);
					Log.d("HomePage", "AlertPreference Remind current : "
							+ System.currentTimeMillis() + " last : "
							+ string[1] + " diff : " + diff);
					if (diff > (12 * 60 * 1000)) {
						showNoClubDialog();
					}
				}

			}

		}
	}

	private void showNoClubDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);

		builder.setMessage("You are not a member of any clubs yet! Let's start by creating your first.");
		builder.setPositiveButton("OK\r\n",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent mainIntent = new Intent(HomePage.this,
								MyClubs.class);
						startActivity(mainIntent);
					}
				});
		builder.setNeutralButton("Remind me later",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences1 = getSharedPreferences(
								"NoClubsAlert", 0);
						SharedPreferences.Editor editor1 = sharedPreferences1
								.edit();
						editor1.putString(
								"AlertPreference",
								"Remind|"
										+ Long.toString(System
												.currentTimeMillis()));
						editor1.commit();
					}
				});
		builder.setNegativeButton("Don't show this again",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences1 = getSharedPreferences(
								"NoClubsAlert", 0);
						SharedPreferences.Editor editor1 = sharedPreferences1
								.edit();
						editor1.putString("AlertPreference", "Don't");
						editor1.commit();
					}
				});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();
	}

	public class AuthenticateConnectionFetchMyClubs {

		public AuthenticateConnectionFetchMyClubs() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = checkurl.GetServiceUrl() + "/Fetch_Club.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber.toString().trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			myclubsresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myclubsresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.e("myclubsresp", "" + myclubsresp);

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
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

	@Override
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}
}
