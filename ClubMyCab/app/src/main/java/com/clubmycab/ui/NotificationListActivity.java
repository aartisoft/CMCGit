package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.AllNotificationListViewAdapter;
import com.clubmycab.R;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationListActivity extends Activity {

	private String FullName;
	private String MobileNumberstr;
	private String allnotificationresp;
	private DynamicListView onnotificationlistview;
	private AllNotificationListViewAdapter adapter;
	private ArrayList<String> NotificationId = new ArrayList<String>();
	private ArrayList<String> NotificationType = new ArrayList<String>();
	private ArrayList<String> SentMemberName = new ArrayList<String>();
	private ArrayList<String> SentMemberNumber = new ArrayList<String>();
	private ArrayList<String> ReceiveMemberName = new ArrayList<String>();
	private ArrayList<String> ReceiveMemberNumber = new ArrayList<String>();
	private ArrayList<String> Message = new ArrayList<String>();
	private ArrayList<String> CabId = new ArrayList<String>();
	private ArrayList<String> Poolid = new ArrayList<String>();
	private ArrayList<String> UserLatLong = new ArrayList<String>();
	private ArrayList<String> DateTime = new ArrayList<String>();
	private ArrayList<String> Status = new ArrayList<String>();
	private ArrayList<String> RefId = new ArrayList<String>();
	private ArrayList<String> routeId = new ArrayList<String>();
	private ArrayList<String> imageName = new ArrayList<String>();
	private ArrayList<String> fromAddress = new ArrayList<String>();
	private ArrayList<String> toAddress = new ArrayList<String>();
	private ImageView notificationimg;
	private LinearLayout allnotificationiconsll;
	private ImageView notificationsclearall;
	private ImageView notificationsmarkallread;
	private TextView about;
	private	String gotopoolresp;
	private String cabratingresp;
	private	String address;
	private String latlongmap;
	private String readunreadnotiresp;
	private RelativeLayout allnotificationrl;
	private Tracker tracker;
	private boolean exceptioncheck = false;
	private Dialog onedialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_notification_request);
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
		((TextView) findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(NotificationListActivity.this, AppConstants.HELVITICA));
		((TextView) findViewById(R.id.tvHeading)).setText("Notifications");
		findViewById(R.id.flBackArrow).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(NotificationListActivity.this, NewHomeScreen.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		allnotificationrl = (RelativeLayout) findViewById(R.id.allnotificationrl);
		allnotificationrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("allnotificationrl", "allnotificationrl");
			}
		});
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		allnotificationiconsll = (LinearLayout) findViewById(R.id.allnotificationiconsll);
		notificationsclearall = (ImageView) findViewById(R.id.notificationsclearall);
		notificationsmarkallread = (ImageView) findViewById(R.id.notificationsmarkallread);
		notificationimg.setVisibility(View.GONE);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");
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

		findViewById(R.id.flDelete).setOnClickListener(new OnClickListener() {
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

	}

	@Override
	protected void onResume() {
		super.onResume();
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
			try{
				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(NotificationListActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}
			}catch (Exception e){
				e.printStackTrace();
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

			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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

			String clearallres = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				clearallres = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("clearallres", "" + stringBuilder.toString());

			if (clearallres != null && clearallres.length() > 0
					&& clearallres.contains("Unauthorized Access")) {
				Log.e("NotificationListActivity",
						"clearallres Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(NotificationListActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
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
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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

			String markallres = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				markallres = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("markallres", "" + stringBuilder.toString());

			if (markallres != null && markallres.length() > 0
					&& markallres.contains("Unauthorized Access")) {
				Log.e("NotificationListActivity",
						"markallres Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(NotificationListActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	// /////////////

	private class ConnectionTaskForFetchAllNotification extends
			AsyncTask<String, Void, Void> {


		@Override
		protected void onPreExecute() {
            try {
               showProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }
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
            try{
               hideProgressBar();
                if(TextUtils.isEmpty(allnotificationresp) )
                    return;
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!TextUtils.isEmpty(allnotificationresp) && allnotificationresp.contains("Unauthorized Access")) {
                    Log.e("NotificationListActivity",
                            "allnotificationresp Unauthorized Access");
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!TextUtils.isEmpty(allnotificationresp) && allnotificationresp.equalsIgnoreCase("No Notification !!")) {
                    Toast.makeText(NotificationListActivity.this,
                            "" + allnotificationresp, Toast.LENGTH_LONG).show();
                    allnotificationiconsll.setVisibility(View.GONE);
					findViewById(R.id.flDelete).setVisibility(View.GONE);
                } else {
					findViewById(R.id.flDelete).setVisibility(View.VISIBLE);

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
                    routeId.clear();
                    imageName.clear();
                    fromAddress.clear();
                    toAddress.clear();
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
                                routeId.add(subArray.getJSONObject(i)
                                        .getString("routeId").toString());
                                imageName.add(subArray.getJSONObject(i)
                                        .optString("imagename").toString());
                                fromAddress.add(subArray.getJSONObject(i)
                                        .optString("imagename").toString());
                                toAddress.add(subArray.getJSONObject(i)
                                        .optString("imagename").toString());
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
                                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                                        "CabId_Rejected")
                                                || NotificationType
                                                .get(arg2)
                                                .toString()
                                                .trim()
                                                .equalsIgnoreCase(
                                                        "tripcompleted")) {
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
								/*		String[] arr = Message.get(arg2).split(
												"-");
										Log.d("arr", ""
												+ arr[1].toString().trim());

										address = arr[1].toString().trim();*/
                                            latlongmap = UserLatLong.get(arg2);

                                            Intent mainIntent = new Intent(
                                                    NotificationListActivity.this,
                                                    LocationInMapFragmentActivity.class);
                                            mainIntent.putExtra("address", "");
                                            mainIntent.putExtra("latlongmap",
                                                    latlongmap);
                                            mainIntent.putExtra("routeId",
                                                    routeId.get(arg2));

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
                                                    XMyClubsActivty.class);
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

										/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new ConnectionTaskForCabRating()
													.executeOnExecutor(
															AsyncTask.THREAD_POOL_EXECUTOR,
															CabId.get(arg2)
																	.toString()
																	.trim(),
															Integer.toString(arg2),SentMemberNumber.get(arg2), ReceiveMemberNumber.get(arg2));
										} else {
											new ConnectionTaskForCabRating()
													.execute(
															CabId.get(arg2)
																	.toString()
																	.trim(),
															Integer.toString(arg2),SentMemberNumber.get(arg2), ReceiveMemberNumber.get(arg2));
										}*/

                                            // New Code-------------------->
                                            Intent intent = new Intent(NotificationListActivity.this,
                                                    RateCabActivity.class);
                                            intent.putExtra("CabsJSONArrayString", cabratingresp);
                                            intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
                                            intent.putExtra("cabIDIntent", CabId.get(arg2).toString().trim());
                                            intent.putExtra("notificationIDString", Integer.toString(arg2));
                                            intent.putExtra("imagename", imageName.get(arg2));
                                            intent.putExtra("date", getNotificationDate(arg2));
                                            intent.putExtra("time", getTime(arg2));
                                            intent.putExtra("fromaddress", fromAddress.get(arg2));
                                            intent.putExtra("toaddress", toAddress.get(arg2));
                                            intent.putExtra("notificationId", NotificationId.get(arg2));

                                            startActivityForResult(intent, 1);

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
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
			try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = MobileNumberstr.toString().trim()
					+ nid.toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(NIDBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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

			String swipedeleteres = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				swipedeleteres = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("swipedeleteres", "" + stringBuilder.toString());

			if (swipedeleteres != null && swipedeleteres.length() > 0
					&& swipedeleteres.contains("Unauthorized Access")) {
				Log.e("NotificationListActivity",
						"swipedeleteres Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(NotificationListActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	// /////////////////
	// ///////

	private class ConnectionTaskForseenotification extends
			AsyncTask<String, Void, Void> {


		@Override
		protected void onPreExecute() {
			try{
               showProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }
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

			try{
               hideProgressBar();

                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (gotopoolresp.contains("Unauthorized Access")) {
                    Log.e("NotificationListActivity",
                            "gotopoolresp Unauthorized Access");
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



                    try {

                        Gson gson = new Gson();
                        ArrayList<RideDetailsModel> arrayRideDetailsModels = gson
                                .fromJson(
                                        gotopoolresp,
                                        new TypeToken<ArrayList<RideDetailsModel>>() {
                                        }.getType());
                        RideDetailsModel rideDetailsModel = arrayRideDetailsModels
                                .get(0);


                        if (rideDetailsModel.getMobileNumber().equalsIgnoreCase(
                                MobileNumberstr)) {

                            final Intent mainIntent = new Intent(
                                    NotificationListActivity.this,
                                    XCheckPoolFragmentActivty.class);

                            mainIntent.putExtra("RideDetailsModel",
                                    gson.toJson(rideDetailsModel));



                            NotificationListActivity.this.startActivity(mainIntent);

                        } else {
                            final Intent mainIntent = new Intent(
                                    NotificationListActivity.this,
                                    XMemberRideFragmentActivity.class);

                            mainIntent.putExtra("RideDetailsModel",
                                    gson.toJson(rideDetailsModel));


                            NotificationListActivity.this.startActivity(mainIntent);
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = cid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
				NewHomeScreen.class);
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
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (gotopoolresp != null && gotopoolresp.length() > 0
                        && gotopoolresp.contains("Unauthorized Access")) {
                    Log.e("NotificationListActivity",
                            "gotopoolresp Unauthorized Access");
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
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = sts + ownname + ownnum + rfid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(RefIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(AcceptedBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (gotopoolresp != null && gotopoolresp.length() > 0
                        && gotopoolresp.contains("Unauthorized Access")) {
                    Log.e("NotificationListActivity",
                            "gotopoolresp Unauthorized Access");
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
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = sts + ownname + ownnum + rfid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(RefIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(AcceptedBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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


		private String cabIDString;
		private String notificationIDString;

		@Override
		protected void onPreExecute() {
			try{
                showProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }
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
			try{
              hideProgressBar();

                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (cabratingresp != null && cabratingresp.length() > 0
                        && cabratingresp.contains("Unauthorized Access")) {
                    Log.e("NotificationListActivity",
                            "cabratingresp Unauthorized Access");
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

                    Intent intent = new Intent(NotificationListActivity.this, RateCabActivity.class);
                    intent.putExtra("CabsJSONArrayString", cabratingresp);
                    intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
                    intent.putExtra("cabIDIntent", cabIDString);
                    intent.putExtra("notificationIDString", notificationIDString);
                    startActivityForResult(intent, 1);

                }
            }catch (Exception e){
                e.printStackTrace();
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
			String authString = cabID;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIDNameValuePair);
			nameValuePairList.add(authValuePair);

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

			try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(NotificationListActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
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

			String authString = nid + rnum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(rnumBasicNameValuePair);
			nameValuePairList.add(nidBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}

	private String getNotificationDate(int position){
		String date = "";
		try{
			String[] arr = DateTime.get(position).split(" ");

			String[] arr1 = arr[0].toString().trim().split("-");

			int month = (int)Double.parseDouble(arr1[1].toString().trim());
			date= arr1[2].toString().trim()+" "+getMontString(month);
		}catch (Exception e){
			e.printStackTrace();
		}
		return date;
	}

	private String getTime(int position){
		try{
			String[] arr = DateTime.get(position).split(" ");
			String _24HourTime = arr[1].toString().trim();
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			Date _24HourDt = null;
			try {
				_24HourDt = _24HourSDF.parse(_24HourTime);
				System.out.println(_24HourDt);
				System.out.println(_12HourSDF.format(_24HourDt));
				return  _12HourSDF.format(_24HourDt);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";
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

	private void showProgressBar(){
		try{
			onedialog = new Dialog(NotificationListActivity.this);
			onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onedialog.setContentView(R.layout.dialog_ishare_loader);
			onedialog.setCancelable(false);
			onedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			// onedialog.getWindow().setB(getResources().getColor(R.color.colorTransparent));
			onedialog.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void hideProgressBar(){
		try{
			if(onedialog != null)
				onedialog.dismiss();
			onedialog = null;
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
