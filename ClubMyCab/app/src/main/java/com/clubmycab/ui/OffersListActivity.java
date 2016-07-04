package com.clubmycab.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class OffersListActivity extends Activity implements
		AsyncTaskResultListener , View.OnClickListener{

	private TextView textViewCredits;
	private String mobileNumber, userDataResponse;
	private JSONArray offersJSONArray;
	private Tracker tracker;
	private AppEventsLogger logger;
	private ImageView notificationimg;
	private String FullName, MobileNumber, myprofileresp, imagenameresp;
	private TextView  unreadnoticount;
	private RelativeLayout unreadnoticountrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offers_list);
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(OffersListActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("Offers");

		if (!isOnline()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    OffersListActivity.this);
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
		((TextView)findViewById(R.id.tvReferEarn)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvDetailText)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvReferEarnCode)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvReferCodeNumber)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvInvite)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvApply)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
		((EditText)findViewById(R.id.etPromoCode)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this,AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.textViewOffersCredits)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this, AppConstants.HELVITICA));

		((TextView)findViewById(R.id.tvHeading)).setText("Offers");
        findViewById(R.id.flBackArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(OffersListActivity.this,
                        NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
		textViewCredits = (TextView) findViewById(R.id.textViewOffersCredits);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		mobileNumber = mPrefs.getString("MobileNumber", "");
		setNotificationAndProfileImage();
		findViewById(R.id.tvInvite).setOnClickListener(this);
		findViewById(R.id.tvApply).setOnClickListener(this);
		findViewById(R.id.llMainLayout).setOnClickListener(this);

	}

	private void setNotificationAndProfileImage() {
		logger = AppEventsLogger.newLogger(this);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		findViewById(R.id.flNotifications).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(OffersListActivity.this, NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

			}
		});
		String endpoint = GlobalVariables.ServiceUrl + "/FetchUnreadNotificationCount.php";
		String authString = MobileNumber;
		String params = "MobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
		new GlobalAsyncTask(this, endpoint, params, new FetchUnreadNotificationCountHandler(), this, false, "FetchUnreadNotificationCount", false);

	}
	private void selectImage() {
		final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
		AlertDialog.Builder builder = new AlertDialog.Builder(OffersListActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		userData();
	}

	private void userData() {

		String endpoint = GlobalVariables.ServiceUrl + "/userData.php";
		String authString = mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("userData", "userData endpoint : " + endpoint + " params : "
				+ params);
		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
				OffersListActivity.this, false, "userData", false);
	}

	private void getOffers() {

		String endpoint = GlobalVariables.ServiceUrl + "/getOffers.php";
		String authString = mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("getOffers", "getOffers endpoint : " + endpoint + " params : "
				+ params);
		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
				OffersListActivity.this, true, "getOffers", false);
	}

//	private void claimOffers(String offerID) {
//
//		String endpoint = GlobalVariables.ServiceUrl + "/claimOffer.php";
//		String authString = offerID + mobileNumber;
//		String params = "mobileNumber=" + mobileNumber + "&id=" + offerID
//				+ "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
//		Log.d("claimOffers", "claimOffers endpoint : " + endpoint
//				+ " params : " + params);
//		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
//				OffersListActivity.this, true, "claimOffers", false);
//	}

	@Override
	public void getResult(String response, String uniqueID) {
		if (uniqueID.equals("FetchUnreadNotificationCount")) {
			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"FetchUnreadNotificationCount Unauthorized Access");
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount
						.setText(GlobalVariables.UnreadNotificationCount);
			}
		}else if (uniqueID.equals("getOffers")) {
			Log.d("OffersListActivity", "getOffers response : " + response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("OffersListActivity", "getOffers Unauthorized Access");
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {

					offersJSONArray = new JSONArray(jsonObject.get("data")
							.toString());

					if (offersJSONArray.length() > 0) {
						final JSONObject jsonObjAmount = offersJSONArray.getJSONObject(0);
						((TextView)findViewById(R.id.tvDetailText)).setText(jsonObjAmount.get("title").toString());
						findViewById(R.id.flInfo).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try{
									CustomDialog.showDialog(OffersListActivity.this,jsonObjAmount.get("terms").toString());
								}catch (Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				} else {
					Toast.makeText(OffersListActivity.this,
							jsonObject.get("message").toString(),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			}
		} else if (uniqueID.equals("userData")) {
			Log.d("OffersListActivity", "userData response : " + response);
			if(response == null){
				return;
			}

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("OffersListActivity", "userData Unauthorized Access");
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					JSONObject jsonObject2 = new JSONObject(jsonObject.get("data").toString());

					if(!jsonObject2.isNull("referralCode") && !TextUtils.isEmpty(jsonObject2.getString("referralCode"))){
						findViewById(R.id.card_view2).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.tvReferCodeNumber)).setText(jsonObject2.get("referralCode").toString());
					}else {
						findViewById(R.id.card_view2).setVisibility(View.GONE);
					}
					textViewCredits.setText("Your reward points : " + jsonObject2.get("totalCredits").toString());


				}

				userDataResponse = response;

				getOffers();
				getCouponList();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			}
		} else if(uniqueID.equalsIgnoreCase("applypromocode")){
			try{
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					if(!jsonObject.isNull("message")){
						CustomDialog.showDialog(OffersListActivity.this,jsonObject.optString("message"));
						getCouponList();
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}else if(uniqueID.equalsIgnoreCase("getCouponList")){
			try{
				final JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					if(!jsonObject.isNull("data")){
						findViewById(R.id.card_view4).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.tvPromoCode)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this, AppConstants.HELVITICA));
						((TextView)findViewById(R.id.tvTitle)).setTypeface(FontTypeface.getTypeface(OffersListActivity.this, AppConstants.HELVITICA));
						((TextView)findViewById(R.id.tvPromoCode)).setText(jsonObject.getJSONObject("data").optString("code"));
						((TextView)findViewById(R.id.tvTitle)).setText(jsonObject.getJSONObject("data").optString("title"));
						findViewById(R.id.card_view4).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									CustomDialog.showDialog(OffersListActivity.this,jsonObject.getJSONObject("data").optString("terms"));

								}catch (Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				}else {
					findViewById(R.id.card_view4).setVisibility(View.GONE);

				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
//		else if (uniqueID.equals("claimOffers")) {
//			Log.d("OffersListActivity", "claimOffers response : " + response);
//
//			if (response != null && response.length() > 0
//					&& response.contains("Unauthorized Access")) {
//				Log.e("OffersListActivity", "claimOffers Unauthorized Access");
//				Toast.makeText(OffersListActivity.this,
//						getResources().getString(R.string.exceptionstring),
//						Toast.LENGTH_LONG).show();
//				return;
//			}
//
//			try {
//				JSONObject jsonObject = new JSONObject(response);
//
//				Toast.makeText(OffersListActivity.this,
//						jsonObject.get("message").toString(), Toast.LENGTH_LONG)
//						.show();
//
//				if (jsonObject.get("status").toString().equals("success")) {
//					userData();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Toast.makeText(OffersListActivity.this,
//						getResources().getString(R.string.exceptionstring),
//						Toast.LENGTH_LONG).show();
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tvInvite:
				try {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Invite Friends")
                            .setAction("Invite Friends")
                            .setLabel("Invite Friends").build());
					JSONObject userDataJSONObject = new JSONObject(userDataResponse);
					JSONObject offerJSONObject = new JSONObject(offersJSONArray.get(0).toString());

					if (userDataJSONObject.get("status").toString()
							.equals("success")) {
						JSONObject jsonObject = new JSONObject(
								userDataJSONObject.get("data")
										.toString());
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);

						if (Double.parseDouble(offerJSONObject.get(
								"useCount").toString()) >= Double
								.parseDouble(offerJSONObject.get(
										"maxUsePerUser").toString())) {
							sendIntent
									.putExtra(
											Intent.EXTRA_TEXT,
											"I am using this cool app 'iShareRyde' to share rides. Check it out @ "
													+ GlobalVariables.ShareThisAppLink);
						} else {
							sendIntent
									.putExtra(
											Intent.EXTRA_TEXT,
											"I am using this cool app 'iShareRyde' to share rides. Check it out @ "
													+ GlobalVariables.ShareThisAppLink
													+ ". Use my referral code "
													+ jsonObject
													.get("referralCode")
													.toString()
													+ " and earn credits worth Rs."
													+ offerJSONObject
													.get("amount")
													.toString());
						}
						sendIntent.setType("text/plain");
						startActivity(Intent.createChooser(sendIntent,
								"Share Via"));

					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(OffersListActivity.this,
							getResources().getString(
									R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
				}
				break;

			case R.id.tvApply:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Offer applied")
                        .setAction("Apply button pressed")
                        .setLabel("Favourite locations").build());
				if(!TextUtils.isEmpty(((EditText)findViewById(R.id.etPromoCode)).getText().toString().trim())){
					sendCouponCode(((EditText)findViewById(R.id.etPromoCode)).getText().toString().trim());
				}else {
					((EditText)findViewById(R.id.etPromoCode)).setError("Please enter promo code");
				}
				break;
			case R.id.llMainLayout:

				break;
		}
	}

	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return offersJSONArray.length();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = (View) getLayoutInflater().inflate(
						R.layout.list_item_offers, parent, false);
			}

			try {
				JSONObject jsonObject = offersJSONArray.getJSONObject(position);

				TextView textView = (TextView) convertView
						.findViewById(R.id.textViewOfferDescription);
				textView.setText(jsonObject.get("title").toString());

				textView = (TextView) convertView
						.findViewById(R.id.textViewOfferUserStatus);
				textView.setText(jsonObject.get("UserOfferStatus").toString());


			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(OffersListActivity.this,
				NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void sendCouponCode(String couponCode){
		String promoCode = ((EditText)findViewById(R.id.etPromoCode)).getText().toString().trim();
		String endpoint = GlobalVariables.ServiceUrl + "/attachCouponWithUser.php";
		String authString = mobileNumber+promoCode;
		String params = "mobileNumber="+mobileNumber+"&offerCode="+promoCode+ "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		new GlobalAsyncTask(OffersListActivity.this, endpoint, params,
				null, OffersListActivity.this, true, "applypromocode",
				false);
	}

	private void getCouponCode(String couponCode){
		String promoCode = ((EditText)findViewById(R.id.etPromoCode)).getText().toString().trim();
		String endpoint = GlobalVariables.ServiceUrl + "/attachCouponWithUser.php";
		String authString = mobileNumber+promoCode;
		String params = "mobileNumber="+mobileNumber+"&offerCode="+promoCode+ "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		new GlobalAsyncTask(OffersListActivity.this, endpoint, params,
				null, OffersListActivity.this, true, "applypromocode",
				false);
	}

	private void getCouponList(){
		String endpoint = GlobalVariables.ServiceUrl + "/checkAttachedCoupons.php";
		String authString = mobileNumber;
		String params = "mobileNumber="+mobileNumber+"&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		new GlobalAsyncTask(OffersListActivity.this, endpoint, params,
				null, OffersListActivity.this, true, "getCouponList",
				false);
	}



}
