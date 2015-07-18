package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.AllNotificationListViewAdapter;
import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.MemberRideFragmentActivity;
import com.clubmycab.R;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

public class NotificationListActivity extends Activity {

	String FullName;
	String MobileNumberstr;

	String allnotificationresp;
	DynamicListView onnotificationlistview;
	AllNotificationListViewAdapter adapter;

	ArrayList<String> NotificationId = new ArrayList<String>();
	ArrayList<String> NotificationType = new ArrayList<String>();
	ArrayList<String> SentMemberName = new ArrayList<String>();
	ArrayList<String> SentMemberNumber = new ArrayList<String>();
	ArrayList<String> ReceiveMemberName = new ArrayList<String>();
	ArrayList<String> ReceiveMemberNumber = new ArrayList<String>();
	ArrayList<String> Message = new ArrayList<String>();
	ArrayList<String> CabId = new ArrayList<String>();
	ArrayList<String> Poolid = new ArrayList<String>();
	ArrayList<String> UserLatLong = new ArrayList<String>();
	ArrayList<String> DateTime = new ArrayList<String>();
	ArrayList<String> Status = new ArrayList<String>();
	ArrayList<String> RefId = new ArrayList<String>();

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	LinearLayout allnotificationiconsll;
	ImageView notificationsclearall;
	ImageView notificationsmarkallread;

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

	String gotopoolresp;
	String cabratingresp;

	String address;
	String latlongmap;

	String readunreadnotiresp;

	RelativeLayout allnotificationrl;
	Tracker tracker;

	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_notification_request);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					NotificationListActivity.this);
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

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(NotificationListActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Notifications");

		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Notifications Opened - Bell Icon")
				.setAction("Notifications Opened - Bell Icon")
				.setLabel("Notifications Opened - Bell Icon").build());

		allnotificationrl = (RelativeLayout) findViewById(R.id.allnotificationrl);
		allnotificationrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("allnotificationrl", "allnotificationrl");
			}
		});

		// mNav = new SimpleSideDrawer(this);
		// mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		//
		// findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		//
		// // mainhomepagerl.setAlpha((float) 0.3);
		// mNav.toggleLeftDrawer();
		//
		// }
		// });
		//
		// myprofile = (TextView) findViewById(R.id.myprofile);
		// myprofile.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// myrides = (TextView) findViewById(R.id.myrides);
		// myrides.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// bookacab = (TextView) findViewById(R.id.bookacab);
		// bookacab.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// sharemylocation = (TextView) findViewById(R.id.sharemylocation);
		// sharemylocation.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// myclubs = (TextView) findViewById(R.id.myclubs);
		// myclubs.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// sharethisapp = (TextView) findViewById(R.id.sharethisapp);
		// sharethisapp.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// mypreferences = (TextView) findViewById(R.id.mypreferences);
		// mypreferences.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// about = (TextView) findViewById(R.id.about);
		// about.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		//
		// myprofile.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyProfile Click")
		// .setAction("MyProfile Click")
		// .setLabel("MyProfile Click").build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// MyProfileActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// myrides.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyRides Click")
		// .setAction("MyRides Click").setLabel("MyRides Click")
		// .build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// MyRidesActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// bookacab.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("BookaCab Click")
		// .setAction("BookaCab Click").setLabel("BookaCab Click")
		// .build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// BookaCabFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// sharemylocation.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ShareLocation Click")
		// .setAction("ShareLocation Click")
		// .setLabel("ShareLocation Click").build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// ShareLocationFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// myclubs.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyClubs Click")
		// .setAction("MyClubs Click").setLabel("MyClubs Click")
		// .build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// MyClubsActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// sharethisapp.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
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
		// startActivity(Intent.createChooser(sendIntent, "Share Via"));
		//
		// }
		// });
		//
		// mypreferences.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("Settings Click")
		// .setAction("Settings Click").setLabel("Settings Click")
		// .build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// SettingActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// about.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("About Click").setAction("About Click")
		// .setLabel("About Click").build());
		//
		// Intent mainIntent = new Intent(NotificationListActivity.this,
		// AboutPagerFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		allnotificationiconsll = (LinearLayout) findViewById(R.id.allnotificationiconsll);
		notificationsclearall = (ImageView) findViewById(R.id.notificationsclearall);
		notificationsmarkallread = (ImageView) findViewById(R.id.notificationsmarkallread);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		notificationimg.setVisibility(View.GONE);
		allnotificationiconsll.setVisibility(View.VISIBLE);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationsclearall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						NotificationListActivity.this);
				builder.setMessage("Are you sure you want to clear all notifications? To remove individual notification, swipe them");
				builder.setCancelable(true);
				builder.setPositiveButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();
							}
						});
				builder.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (NotificationId.size() > 0) {
									NotificationId.clear();
									NotificationType.clear();
									SentMemberName.clear();
									SentMemberNumber.clear();
									ReceiveMemberName.clear();
									ReceiveMemberNumber.clear();
									Message.clear();
									CabId.clear();
									Poolid.clear();
									UserLatLong.clear();
									DateTime.clear();
									Status.clear();
									RefId.clear();

									adapter.notifyDataSetChanged();

									new clearallnotificationtask().execute();

								} else {
									Toast.makeText(
											NotificationListActivity.this,
											"No Notifications to clear!!",
											Toast.LENGTH_LONG).show();
								}
							}
						});
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		});

		notificationsmarkallread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Boolean chk = false;

				for (int i = 0; i < NotificationId.size(); i++) {

					if (Status.get(i).toString().trim().equalsIgnoreCase("U")) {
						chk = true;

						break;
					}
				}

				if (chk) {

					Status.clear();

					for (int i = 0; i < NotificationId.size(); i++) {
						Status.add("R");
					}

					adapter.notifyDataSetChanged();
					new markallasnotificationtask().execute();

				} else {
					Toast.makeText(NotificationListActivity.this,
							"No Notifications to mark as read!!",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {
		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchAllNotification()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchAllNotification().execute();
		}
	}

	private class clearallnotificationtask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionclearallnotificationtask mAuth1 = new AuthenticateConnectionclearallnotificationtask();
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionclearallnotificationtask {

		public AuthenticateConnectionclearallnotificationtask() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/changenotificationstatus.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}

	// ///////

	private class markallasnotificationtask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionmarkallasnotificationtask mAuth1 = new AuthenticateConnectionmarkallasnotificationtask();
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionmarkallasnotificationtask {

		public AuthenticateConnectionmarkallasnotificationtask() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/changenotificationstatusasread.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}

	// /////////////

	private class ConnectionTaskForFetchAllNotification extends
			AsyncTask<String, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(
				NotificationListActivity.this);

		@Override
		protected void onPreExecute() {

			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchAllNotification mAuth1 = new AuthenticateConnectionFetchAllNotification();
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (allnotificationresp.equalsIgnoreCase("No Notification !!")) {
				Toast.makeText(NotificationListActivity.this,
						"" + allnotificationresp, Toast.LENGTH_LONG).show();
				allnotificationiconsll.setVisibility(View.GONE);
			} else {

				NotificationId.clear();
				NotificationType.clear();
				SentMemberName.clear();
				SentMemberNumber.clear();
				ReceiveMemberName.clear();
				ReceiveMemberNumber.clear();
				Message.clear();
				CabId.clear();
				Poolid.clear();
				UserLatLong.clear();
				DateTime.clear();
				Status.clear();
				RefId.clear();

				try {
					JSONArray subArray = new JSONArray(allnotificationresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							NotificationId.add(subArray.getJSONObject(i)
									.getString("NotificationId").toString());
							NotificationType.add(subArray.getJSONObject(i)
									.getString("NotificationType").toString());
							SentMemberName.add(subArray.getJSONObject(i)
									.getString("SentMemberName").toString());
							SentMemberNumber.add(subArray.getJSONObject(i)
									.getString("SentMemberNumber").toString());
							ReceiveMemberName.add(subArray.getJSONObject(i)
									.getString("ReceiveMemberName").toString());
							ReceiveMemberNumber.add(subArray.getJSONObject(i)
									.getString("ReceiveMemberNumber")
									.toString());
							Message.add(subArray.getJSONObject(i)
									.getString("Message").toString());
							CabId.add(subArray.getJSONObject(i)
									.getString("CabId").toString());
							Poolid.add(subArray.getJSONObject(i)
									.getString("Poolid").toString());
							UserLatLong.add(subArray.getJSONObject(i)
									.getString("UserLatLong").toString());
							DateTime.add(subArray.getJSONObject(i)
									.getString("DateTime").toString());
							Status.add(subArray.getJSONObject(i)
									.getString("Status").toString());
							RefId.add(subArray.getJSONObject(i)
									.getString("RefId").toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					onnotificationlistview = (DynamicListView) findViewById(R.id.onnotificationlistview);
					adapter = new AllNotificationListViewAdapter(
							NotificationListActivity.this, SentMemberName,
							SentMemberNumber, ReceiveMemberName,
							ReceiveMemberNumber, Message, CabId, DateTime,
							Status, NotificationType);

					AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(
							adapter);
					animationAdapter.setAbsListView(onnotificationlistview);
					onnotificationlistview.setAdapter(animationAdapter);

					onnotificationlistview
							.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, final int arg2, long arg3) {

									if (Status.get(arg2).toString().trim()
											.equalsIgnoreCase("U")) {
										Status.set(arg2, "R");
										adapter.notifyDataSetChanged();

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new updatenotificationstatusasread()
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															ReceiveMemberNumber
																	.get(arg2)
																	.toString()
																	.trim(),
															NotificationId
																	.get(arg2)
																	.toString()
																	.trim());
										} else {
											new updatenotificationstatusasread()
													.execute(
															ReceiveMemberNumber
																	.get(arg2),
															NotificationId
																	.get(arg2));
										}
									} else {
										Log.d("READ", "already read");
									}

									tracker.send(new HitBuilders.EventBuilder()
											.setCategory(
													"Notification Opened - Push notification")
											.setAction(
													"Notification Opened - Push notification")
											.setLabel(
													"Notification Opened - Push notification")
											.build());

									if (NotificationType.get(arg2).toString()
											.trim()
											.equalsIgnoreCase("CabId_Invited")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_Joined")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_UpdateLocation")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_Dropped")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_Left")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_CancelRide")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_Approved")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"CabId_Rejected")) {
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new ConnectionTaskForseenotification()
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															CabId.get(arg2));
										} else {
											new ConnectionTaskForseenotification()
													.execute(CabId.get(arg2));
										}
									} else if (NotificationType.get(arg2)
											.toString().trim()
											.equalsIgnoreCase("CabId_Refered")) {

										AlertDialog.Builder builder = new AlertDialog.Builder(
												NotificationListActivity.this);
										builder.setMessage("Do you want to invite friend refered by "
												+ SentMemberName.get(arg2)
														.toString().trim()
												+ " for this ride?");
										builder.setCancelable(true);
										builder.setPositiveButton(
												"Yes",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {

														if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
															new ConnectionTaskForacceptrejectrefer()
																	.executeOnExecutor(
																			AsyncTask.THREAD_POOL_EXECUTOR,
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"Yes");
														} else {
															new ConnectionTaskForacceptrejectrefer()
																	.execute(
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"Yes");
														}

													}
												});
										builder.setNegativeButton(
												"No",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {

														if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
															new ConnectionTaskForacceptrejectrefer()
																	.executeOnExecutor(
																			AsyncTask.THREAD_POOL_EXECUTOR,
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"No");
														} else {
															new ConnectionTaskForacceptrejectrefer()
																	.execute(
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"No");
														}

													}
												});
										AlertDialog dialog = builder.show();
										TextView messageText = (TextView) dialog
												.findViewById(android.R.id.message);
										messageText.setGravity(Gravity.CENTER);
										dialog.show();
									} else if (NotificationType
											.get(arg2)
											.toString()
											.trim()
											.equalsIgnoreCase(
													"Share_LocationUpdate")) {
										String[] arr = Message.get(arg2).split(
												"-");
										Log.d("arr", ""
												+ arr[1].toString().trim());

										address = arr[1].toString().trim();
										latlongmap = UserLatLong.get(arg2);

										Intent mainIntent = new Intent(
												NotificationListActivity.this,
												LocationInMapFragmentActivity.class);
										mainIntent.putExtra("address", address);
										mainIntent.putExtra("latlongmap",
												latlongmap);
										NotificationListActivity.this
												.startActivity(mainIntent);

									} else if (NotificationType.get(arg2)
											.toString().trim()
											.equalsIgnoreCase("PoolId_Added")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"PoolId_Removed")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"PoolId_Left")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"PoolId_ClubDeleted")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"PoolId_Approved")
											|| NotificationType
													.get(arg2)
													.toString()
													.trim()
													.equalsIgnoreCase(
															"PoolId_Rejected")) {

										Intent mainIntent = new Intent(
												NotificationListActivity.this,
												MyClubsActivity.class);
										mainIntent.putExtra("comefrom",
												"comefrom");
										startActivityForResult(mainIntent, 500);
										overridePendingTransition(
												R.anim.slide_in_right,
												R.anim.slide_out_left);

									} else if (NotificationType.get(arg2)
											.toString().trim()
											.equalsIgnoreCase("PoolId_Refered")) {

										AlertDialog.Builder builder = new AlertDialog.Builder(
												NotificationListActivity.this);
										builder.setMessage("Do you want to add friend refered by "
												+ SentMemberName.get(arg2)
														.toString().trim());
										builder.setCancelable(true);
										builder.setPositiveButton(
												"Yes",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {

														if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
															new ConnectionTaskForacceptrejectreferforclub()
																	.executeOnExecutor(
																			AsyncTask.THREAD_POOL_EXECUTOR,
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"Yes");
														} else {
															new ConnectionTaskForacceptrejectreferforclub()
																	.execute(
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"Yes");
														}

													}
												});
										builder.setNegativeButton(
												"No",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {

														if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
															new ConnectionTaskForacceptrejectreferforclub()
																	.executeOnExecutor(
																			AsyncTask.THREAD_POOL_EXECUTOR,
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"No");
														} else {
															new ConnectionTaskForacceptrejectreferforclub()
																	.execute(
																			RefId.get(
																					arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberName
																					.get(arg2)
																					.toString()
																					.trim(),
																			ReceiveMemberNumber
																					.get(arg2)
																					.toString()
																					.trim(),
																			"No");
														}

													}
												});
										AlertDialog dialog = builder.show();
										TextView messageText = (TextView) dialog
												.findViewById(android.R.id.message);
										messageText.setGravity(Gravity.CENTER);
										dialog.show();
									} else if (NotificationType.get(arg2)
											.toString().trim()
											.equalsIgnoreCase("Cab_Rating")) {

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new ConnectionTaskForCabRating()
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															CabId.get(arg2)
																	.toString()
																	.trim(),
															Integer.toString(arg2));
										} else {
											new ConnectionTaskForCabRating()
													.execute(
															CabId.get(arg2)
																	.toString()
																	.trim(),
															Integer.toString(arg2));
										}

									} else {

									}

								}
							});

					onnotificationlistview
							.enableSwipeToDismiss(new OnDismissCallback() {
								@Override
								public void onDismiss(final ViewGroup listView,
										final int[] reverseSortedPositions) {
									for (int position : reverseSortedPositions) {

										Log.d("position", "" + position);

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new SwipeToDeleteParticularNotiFication()
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															NotificationId
																	.get(position));
										} else {
											new SwipeToDeleteParticularNotiFication()
													.execute(NotificationId
															.get(position));
										}

										NotificationId.remove(position);
										NotificationType.remove(position);
										SentMemberName.remove(position);
										SentMemberNumber.remove(position);
										ReceiveMemberName.remove(position);
										ReceiveMemberNumber.remove(position);
										Message.remove(position);
										CabId.remove(position);
										Poolid.remove(position);
										UserLatLong.remove(position);
										DateTime.remove(position);
										Status.remove(position);
										RefId.remove(position);

										adapter.notifyDataSetChanged();
									}
								}
							});

					// onnotificationlistview
					// .setOnScrollListener(new OnScrollListener() {
					// @Override
					// public void onScroll(AbsListView view,
					// int firstVisibleItem,
					// int visibleItemCount, int totalItemCount) {
					//
					// if (firstVisibleItem + visibleItemCount >=
					// totalItemCount) {
					// // End has been reached
					// AlertDialog.Builder builder = new AlertDialog.Builder(
					// AllNotificationRequest.this);
					// builder.setMessage("End has been reached");
					// builder.setPositiveButton(
					// "OK",
					// new DialogInterface.OnClickListener() {
					// public void onClick(
					// DialogInterface dialog,
					// int which) {
					//
					// }
					// });
					//
					// builder.show();
					// }
					// }
					//
					// @Override
					// public void onScrollStateChanged(
					// AbsListView view, int scrollState) {
					//
					// }
					// });

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public class AuthenticateConnectionFetchAllNotification {

		public AuthenticateConnectionFetchAllNotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/FetchMyAllNotification.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

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
				allnotificationresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("allnotificationresp", "" + stringBuilder.toString());
		}
	}

	// ///////

	private class SwipeToDeleteParticularNotiFication extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateSwipeToDeleteParticularNotiFication mAuth1 = new AuthenticateSwipeToDeleteParticularNotiFication();
			try {
				mAuth1.nid = args[0];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateSwipeToDeleteParticularNotiFication {

		public String nid;

		public AuthenticateSwipeToDeleteParticularNotiFication() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/swipetodeletenotification.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MobileNumberstr.toString().trim());
			BasicNameValuePair NIDBasicNameValuePair = new BasicNameValuePair(
					"NID", nid.toString().trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(NIDBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}

	// /////////////////
	// ///////

	private class ConnectionTaskForseenotification extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				NotificationListActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionseenotification mAuth1 = new AuthenticateConnectionseenotification();
			try {
				mAuth1.cid = args[0];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (gotopoolresp.equalsIgnoreCase("This Ride no longer exist")) {
				Toast.makeText(NotificationListActivity.this,
						"This ride no longer exists!", Toast.LENGTH_LONG)
						.show();
			} else {

				String CabIdstr = null;
				String MobileNumber = null;
				String OwnerName = null;
				String OwnerImage = null;
				String FromLocation = null;
				String ToLocation = null;
				String FromShortName = null;
				String ToShortName = null;
				String TravelDate = null;
				String TravelTime = null;
				String Seats = null;
				String RemainingSeats = null;
				String Distance = null;
				String OpenTime = null;
				String CabStatus = null;
				String Seat_Status = null;

				String BookingRefNo = null;
				String DriverName = null;
				String DriverNumber = null;
				String CarNumber = null;
				String ExpTripDuration = null;
				String status = null;

				try {
					JSONArray subArray = new JSONArray(gotopoolresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							CabIdstr = subArray.getJSONObject(i)
									.getString("CabId").toString();
							MobileNumber = subArray.getJSONObject(i)
									.getString("MobileNumber").toString();
							OwnerName = subArray.getJSONObject(i)
									.getString("OwnerName").toString();
							OwnerImage = subArray.getJSONObject(i)
									.getString("imagename").toString();
							FromLocation = subArray.getJSONObject(i)
									.getString("FromLocation").toString();
							ToLocation = subArray.getJSONObject(i)
									.getString("ToLocation").toString();

							FromShortName = subArray.getJSONObject(i)
									.getString("FromShortName").toString();
							ToShortName = subArray.getJSONObject(i)
									.getString("ToShortName").toString();

							TravelDate = subArray.getJSONObject(i)
									.getString("TravelDate").toString();
							TravelTime = subArray.getJSONObject(i)
									.getString("TravelTime").toString();
							Seats = subArray.getJSONObject(i)
									.getString("Seats").toString();
							RemainingSeats = subArray.getJSONObject(i)
									.getString("RemainingSeats").toString();
							Distance = subArray.getJSONObject(i)
									.getString("Distance").toString();
							OpenTime = subArray.getJSONObject(i)
									.getString("OpenTime").toString();
							CabStatus = subArray.getJSONObject(i)
									.getString("CabStatus").toString();
							Seat_Status = subArray.getJSONObject(i)
									.getString("Seat_Status").toString();

							BookingRefNo = subArray.getJSONObject(i)
									.getString("BookingRefNo").toString();
							DriverName = subArray.getJSONObject(i)
									.getString("DriverName").toString();
							DriverNumber = subArray.getJSONObject(i)
									.getString("DriverNumber").toString();
							CarNumber = subArray.getJSONObject(i)
									.getString("CarNumber").toString();

							ExpTripDuration = subArray.getJSONObject(i)
									.getString("ExpTripDuration").toString();
							status = subArray.getJSONObject(i)
									.getString("status").toString();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (MobileNumber.equalsIgnoreCase(MobileNumberstr)) {

						final Intent mainIntent = new Intent(
								NotificationListActivity.this,
								CheckPoolFragmentActivity.class);
						mainIntent.putExtra("CabId", CabIdstr);
						mainIntent.putExtra("MobileNumber", MobileNumber);
						mainIntent.putExtra("OwnerName", OwnerName);
						mainIntent.putExtra("OwnerImage", OwnerImage);
						mainIntent.putExtra("FromLocation", FromLocation);
						mainIntent.putExtra("ToLocation", ToLocation);

						mainIntent.putExtra("FromShortName", FromShortName);
						mainIntent.putExtra("ToShortName", ToShortName);

						mainIntent.putExtra("TravelDate", TravelDate);
						mainIntent.putExtra("TravelTime", TravelTime);
						mainIntent.putExtra("Seats", Seats);
						mainIntent.putExtra("RemainingSeats", RemainingSeats);
						mainIntent.putExtra("Seat_Status", Seat_Status);
						mainIntent.putExtra("Distance", Distance);
						mainIntent.putExtra("OpenTime", OpenTime);

						mainIntent.putExtra("CabStatus", CabStatus);

						mainIntent.putExtra("BookingRefNo", BookingRefNo);
						mainIntent.putExtra("DriverName", DriverName);
						mainIntent.putExtra("DriverNumber", DriverNumber);
						mainIntent.putExtra("CarNumber", CarNumber);

						mainIntent.putExtra("ExpTripDuration", ExpTripDuration);
						mainIntent.putExtra("status", status);

						NotificationListActivity.this.startActivity(mainIntent);

					} else {
						final Intent mainIntent = new Intent(
								NotificationListActivity.this,
								MemberRideFragmentActivity.class);
						mainIntent.putExtra("CabId", CabIdstr);
						mainIntent.putExtra("MobileNumber", MobileNumber);
						mainIntent.putExtra("OwnerName", OwnerName);
						mainIntent.putExtra("OwnerImage", OwnerImage);

						mainIntent.putExtra("FromLocation", FromLocation);
						mainIntent.putExtra("ToLocation", ToLocation);

						mainIntent.putExtra("FromShortName", FromShortName);
						mainIntent.putExtra("ToShortName", ToShortName);

						mainIntent.putExtra("TravelDate", TravelDate);
						mainIntent.putExtra("TravelTime", TravelTime);
						mainIntent.putExtra("Seats", Seats);
						mainIntent.putExtra("RemainingSeats", RemainingSeats);
						mainIntent.putExtra("Seat_Status", Seat_Status);
						mainIntent.putExtra("Distance", Distance);
						mainIntent.putExtra("OpenTime", OpenTime);
						mainIntent.putExtra("CabStatus", CabStatus);
						mainIntent.putExtra("BookingRefNo", BookingRefNo);
						mainIntent.putExtra("DriverName", DriverName);
						mainIntent.putExtra("DriverNumber", DriverNumber);
						mainIntent.putExtra("CarNumber", CarNumber);

						mainIntent.putExtra("ExpTripDuration", ExpTripDuration);
						mainIntent.putExtra("status", status);

						NotificationListActivity.this.startActivity(mainIntent);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public class AuthenticateConnectionseenotification {

		public String cid;

		public AuthenticateConnectionseenotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/GoToPool.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", cid);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);

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
				gotopoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("gotopoolresp", "" + stringBuilder.toString());
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Intent mainIntent = new Intent(NotificationListActivity.this,
				HomeActivity.class);
		mainIntent.putExtra("from", "normal");
		mainIntent.putExtra("message", "null");
		mainIntent.putExtra("CabId", "null");
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(mainIntent);

		super.onBackPressed();
	}

	// ///////////////////////
	private class ConnectionTaskForacceptrejectrefer extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionacceptrejectrefer mAuth1 = new AuthenticateConnectionacceptrejectrefer();
			try {
				mAuth1.rfid = args[0];
				mAuth1.ownname = args[1];
				mAuth1.ownnum = args[2];
				mAuth1.sts = args[3];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchAllNotification()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchAllNotification().execute();
			}
		}

	}

	public class AuthenticateConnectionacceptrejectrefer {

		public String rfid;
		public String ownname;
		public String ownnum;
		public String sts;

		public AuthenticateConnectionacceptrejectrefer() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/referFriendRideStepTwo.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair RefIdBasicNameValuePair = new BasicNameValuePair(
					"RefId", rfid);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", ownname);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", ownnum);
			BasicNameValuePair AcceptedBasicNameValuePair = new BasicNameValuePair(
					"Accepted", sts);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(RefIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(AcceptedBasicNameValuePair);

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
				gotopoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("gotopoolresp", "" + stringBuilder.toString());
		}
	}

	// /////////////////
	// ///////

	private class ConnectionTaskForacceptrejectreferforclub extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionacceptrejectreferforclub mAuth1 = new AuthenticateConnectionacceptrejectreferforclub();
			try {
				mAuth1.rfid = args[0];
				mAuth1.ownname = args[1];
				mAuth1.ownnum = args[2];
				mAuth1.sts = args[3];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchAllNotification()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchAllNotification().execute();
			}
		}

	}

	public class AuthenticateConnectionacceptrejectreferforclub {

		public String rfid;
		public String ownname;
		public String ownnum;
		public String sts;

		public AuthenticateConnectionacceptrejectreferforclub() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/referFriendStepTwo.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair RefIdBasicNameValuePair = new BasicNameValuePair(
					"RefId", rfid);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", ownname);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", ownnum);
			BasicNameValuePair AcceptedBasicNameValuePair = new BasicNameValuePair(
					"Accepted", sts);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(RefIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(AcceptedBasicNameValuePair);

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
				gotopoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("gotopoolresp", "" + stringBuilder.toString());
		}
	}

	private class ConnectionTaskForCabRating extends
			AsyncTask<String, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(
				NotificationListActivity.this);
		private String cabIDString;
		private String notificationIDString;

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionCabRating mAuth1 = new AuthenticateConnectionCabRating();
			try {
				cabIDString = args[0];
				notificationIDString = args[1];
				mAuth1.cabID = args[0];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (cabratingresp.isEmpty() || cabratingresp == null
					|| cabratingresp.toLowerCase().contains("no cabs found")) {
				Toast.makeText(NotificationListActivity.this,
						"Sorry, no cabs were found", Toast.LENGTH_SHORT).show();
			} else {

				Intent intent = new Intent(NotificationListActivity.this,
						RateCabActivity.class);
				intent.putExtra("CabsJSONArrayString", cabratingresp);
				intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
				intent.putExtra("cabIDIntent", cabIDString);
				intent.putExtra("notificationIDString", notificationIDString);
				startActivityForResult(intent, 1);

			}

		}

	}

	public class AuthenticateConnectionCabRating {

		public String cabID;

		public AuthenticateConnectionCabRating() {

		}

		public void connection() throws Exception {
			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/getCabs.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIDNameValuePair = new BasicNameValuePair(
					"CabID", cabID);
			// Log.d("AllNotificationRequest",
			// "AuthenticateConnectionCabRating cabID : " + cabID);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIDNameValuePair);

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
				cabratingresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("cabratingresp", "" + stringBuilder.toString());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("onActivityResult",
				"onActivityResult requestCode : "
						+ requestCode
						+ " resultCode : "
						+ resultCode
						+ " data : "
						+ (data == null ? "null" : data
								.getStringExtra("notificationID")));

		if (requestCode == 1 && resultCode == RESULT_OK) {

			int position = Integer.parseInt(data
					.getStringExtra("notificationID"));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new SwipeToDeleteParticularNotiFication().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR,
						NotificationId.get(position));
			} else {
				new SwipeToDeleteParticularNotiFication()
						.execute(NotificationId.get(position));
			}

			NotificationId.remove(position);
			NotificationType.remove(position);
			SentMemberName.remove(position);
			SentMemberNumber.remove(position);
			ReceiveMemberName.remove(position);
			ReceiveMemberNumber.remove(position);
			Message.remove(position);
			CabId.remove(position);
			Poolid.remove(position);
			UserLatLong.remove(position);
			DateTime.remove(position);
			Status.remove(position);
			RefId.remove(position);

			adapter.notifyDataSetChanged();

		}
	}

	// /////////////////
	// ///////

	private class updatenotificationstatusasread extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			Authenticateupdatenotificationstatusasread mAuth1 = new Authenticateupdatenotificationstatusasread();
			try {
				mAuth1.rnum = args[0];
				mAuth1.nid = args[1];
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
				Toast.makeText(NotificationListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class Authenticateupdatenotificationstatusasread {

		public String rnum;
		public String nid;

		public Authenticateupdatenotificationstatusasread() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/UpdateNotificationStatusToRead.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair rnumBasicNameValuePair = new BasicNameValuePair(
					"rnum", rnum);
			BasicNameValuePair nidBasicNameValuePair = new BasicNameValuePair(
					"nid", nid);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(rnumBasicNameValuePair);
			nameValuePairList.add(nidBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}
}
