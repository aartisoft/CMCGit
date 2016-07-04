package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CircularImageView;
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
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RateCabActivity extends Activity implements
		AsyncTaskResultListener, View.OnClickListener{


	private JSONArray cabsJSONArray;
	private JSONObject selectedJsonObject;
	//private String mobileNumber;
	private String cabIDIntent;
	private String notificationIDIntent;

	private String cabratingresp;
	private HashMap<String, JSONObject> hashMap;
	ArrayList<String> arrayListCabs;

	private String currentRating;
	boolean exceptioncheck = false;
    private String selectedReason;
    private Tracker tracker;
    private AppEventsLogger logger;
    private CircularImageView profilepic;
    private ImageView notificationimg;
    private CircleImageView drawerprofilepic;
    private String FullName, MobileNumber, myprofileresp, imagenameresp;
    private TextView username, drawerusername, unreadnoticount;
    private RelativeLayout unreadnoticountrl;
    private String ownerMobNumber, memberMobNum,_date, _time, ownerPicName;
    private String ownerFromAddress,ownerToAddress;
    private String ownerName;
    private String MobileNumberstr;
    private String notificationId;
    // private TextView TextViewRateCab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rate_cab_x);
        SharedPreferences mPrefs = getSharedPreferences(
                "FacebookData", 0);
        MobileNumberstr = mPrefs.getString("MobileNumber", "");

		setResult(Activity.RESULT_CANCELED);
        findViewById(R.id.tvVehicleCondition).setOnClickListener(this);
        findViewById(R.id.tvRideGiverBehav).setOnClickListener(this);
        findViewById(R.id.tvPunctuality).setOnClickListener(this);
        findViewById(R.id.tvOther).setOnClickListener(this);
        findViewById(R.id.llMainLayout).setOnClickListener(this);

        ((TextView)findViewById(R.id.tvVehicleCondition)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvRideGiverBehav)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvPunctuality)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvUserName)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvDate)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvTime)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvPlaceFrom)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvPlaceTo)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvOther)).setTypeface(FontTypeface.getTypeface(RateCabActivity.this, AppConstants.HELVITICA));

    /*   ImageView imageView = (ImageView) findViewById(R.id.sidemenu);
		imageView.setVisibility(View.GONE);
		imageView = (ImageView) findViewById(R.id.notificationimg);
		imageView.setVisibility(View.GONE);*/

	//	mobileNumber = getIntent().getStringExtra("CabsR" + "" + "atingMobileNumber");
		cabIDIntent = getIntent().getStringExtra("cabIDIntent");
		notificationIDIntent = getIntent().getStringExtra("notificationIDString");
        _date = getIntent().getStringExtra("date");
        _time =  getIntent().getStringExtra("time");
        notificationId =  getIntent().getStringExtra("notificationId");

        (((TextView)findViewById(R.id.tvDate))).setText(_date);
        (((TextView)findViewById(R.id.tvTime))).setText(_time);
        if (ownerPicName != null){
            String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                    +ownerPicName;
            //Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(holder.ivUserImage);
            Picasso.with(RateCabActivity.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivUserImage));

        }
        String comefrom = getIntent().getStringExtra("comefrom");

		if (comefrom != null) {

			if (comefrom.equalsIgnoreCase("GCM")) {

				String params = "rnum="
						+ "&nid="
						+ notificationIDIntent
						+ "&auth="
						+ GlobalMethods
								.calculateCMCAuthString(notificationIDIntent);
				String endpoint = GlobalVariables.ServiceUrl
						+ "/UpdateNotificationStatusToRead.php";
				Log.d("RateCabActivity",
						"UpdateNotificationStatusToRead endpoint : " + endpoint
								+ " params : " + params);
				new GlobalAsyncTask(this, endpoint, params, null, this, false,
						"UpdateNotificationStatusToRead", false);

			}

		}
        getNotificationDetail();

	//	hashMap = new HashMap<String, JSONObject>();

/*		try {
			cabsJSONArray = new JSONArray(getIntent().getStringExtra(
					"CabsJSONArrayString"));
			for (int i = 0; i < cabsJSONArray.length(); i++) {
				Log.d("RateCab", "cabsJSONArray : "
						+ cabsJSONArray.getJSONObject(i).get("CabName")
								.toString()
						+ " "
						+ cabsJSONArray.getJSONObject(i).get("CarType")
								.toString());
				try {
					String cabName = cabsJSONArray.getJSONObject(i)
							.get("CabName").toString();
					String carType = cabsJSONArray.getJSONObject(i)
							.get("CarType").toString();
					if (!cabName.isEmpty() && !cabName.equalsIgnoreCase("null")) {
						if (!carType.isEmpty()
								&& !carType.equalsIgnoreCase("null")) {
							hashMap.put(
									cabsJSONArray.getJSONObject(i)
											.get("CabName").toString()
											+ " ("
											+ cabsJSONArray.getJSONObject(i)
													.get("CarType").toString()
											+ ")",
									cabsJSONArray.getJSONObject(i));
						} else {
							hashMap.put(
									cabsJSONArray.getJSONObject(i)
											.get("CabName").toString(),
									cabsJSONArray.getJSONObject(i));
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> arrayList = new ArrayList<String>();
		for (String string : hashMap.keySet()) {
			arrayList.add(string);
		}

		arrayListCabs = arrayList;*/
/*
		Spinner spinner = (Spinner) findViewById(R.id.spinnerRateCab);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item_rate_cab, arrayListCabs);
		arrayAdapter.setDropDownViewResource(R.layout.list_item_rate_cab);
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("RateCab",
						"spinner onItemSelected : "
								+ arrayListCabs.get(position));
				selectedJsonObject = hashMap.get(arrayListCabs.get(position)
						.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});*/

		RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarRateCab);
		ratingBar
				.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float progress, boolean fromUser) {
						Log.d("RateCab", "ratingBar onRatingChanged : "
								+ progress);
						currentRating = Float.toString(progress);
						if(progress<4){
							findViewById(R.id.llReasonDialog).setVisibility(View.VISIBLE);
                            resetAll();
						}else {
							findViewById(R.id.llReasonDialog).setVisibility(View.GONE);

						}
                        if(progress > 0 && progress <4 && !TextUtils.isEmpty(selectedReason)){
                            findViewById(R.id.buttonRateCab).setEnabled(true);
                            findViewById(R.id.buttonRateCab).setBackgroundColor(getResources().getColor(R.color.color_app_blue));
                        }else if(progress >=4){
                            findViewById(R.id.buttonRateCab).setEnabled(true);
                            findViewById(R.id.buttonRateCab).setBackgroundColor(getResources().getColor(R.color.color_app_blue));
                        }else {
                            findViewById(R.id.buttonRateCab).setEnabled(false);
                            findViewById(R.id.buttonRateCab).setBackgroundColor(getResources().getColor(R.color.color_app_text_light));

                        }
					}
				});

		// TextViewRateCab = (TextView)findViewById(R.id.textViewRateCabRating);
		//
		// SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarRateCab);
		// seekBar.getProgressDrawable().setColorFilter(new
		// PorterDuffColorFilter(R.color.seek_bar_color,
		// PorterDuff.Mode.MULTIPLY));
		// seekBar.setOnSeekBarChangeListener(new
		// SeekBar.OnSeekBarChangeListener() {
		//
		// @Override
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProgressChanged(SeekBar seekBar, int progress, boolean
		// fromUser) {
		// // TODO Auto-generated method stub
		// // Log.d("RateCab", "seekBar onProgressChanged : " + progress);
		// TextViewRateCab.setText(String.format("%1.1f", (1.0f * progress /
		// 10)));
		// }
		// });

		Button button = (Button) findViewById(R.id.buttonRateCab);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {


                sendRating();
			}
		});
        initializeDarawer();
        setNotificationAndProfileImage();

	}
    private void initializeDarawer() {
        GoogleAnalytics analytics = GoogleAnalytics
                .getInstance(RateCabActivity.this);
        tracker = analytics
                .newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("HomePage");
        logger = AppEventsLogger.newLogger(this);
        UniversalDrawer drawer = new UniversalDrawer(this, tracker);
        drawer.createDrawer();
        GlobalVariables.ActivityName = "NewRideCreationScreen";
        profilepic = (CircularImageView) findViewById(R.id.profilepic);
        notificationimg = (ImageView) findViewById(R.id.notificationimg);
        drawerprofilepic = (CircleImageView) findViewById(R.id.drawerprofilepic);
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        MobileNumber = mPrefs.getString("MobileNumber", "");
        username = (TextView) findViewById(R.id.username);
        username.setText(FullName);
        drawerusername = (TextView) findViewById(R.id.drawerusername);
        drawerusername.setText(FullName);
        drawerusername.setTypeface(Typeface.createFromAsset(getAssets(), AppConstants.HELVITICA));
        unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
        unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        findViewById(R.id.flNotifications).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(RateCabActivity.this,
                        NotificationListActivity.class);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);

            }
        });
    }
    private void setNotificationAndProfileImage() {
        String endpoint = GlobalVariables.ServiceUrl + "/FetchUnreadNotificationCount.php";
        String authString = MobileNumber;
        String params = "MobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
        new GlobalAsyncTask(this, endpoint, params, new FetchUnreadNotificationCountHandler(), this, false, "FetchUnreadNotificationCount", false);

    }
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RateCabActivity.this);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvVehicleCondition:
            case R.id.tvRideGiverBehav:
            case R.id.tvPunctuality:
            case R.id.tvOther:
                setSelection(v.getId());
                break;
            case R.id.llMainLayout:
                break;
        }
    }


    public void getResult(String response, String uniqueID) {
        if (uniqueID.equals("FetchUnreadNotificationCount")) {
            if (response != null && response.length() > 0
                    && response.contains("Unauthorized Access")) {
                Log.e("HomeActivity",
                        "FetchUnreadNotificationCount Unauthorized Access");
                Toast.makeText(RateCabActivity.this,
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
        }else if(uniqueID.equals("sendrating")){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.get("status").toString().equals("success")) {
                    if(!jsonObject.isNull("message")){
                       /* AlertDialog alertDialog = new AlertDialog.Builder(RateCabActivity.this).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage(jsonObject.optString("message"));

                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        });
                        alertDialog.show();*/
                        finish();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(uniqueID.equals("getcabdetail")){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.get("status").toString().equals("success")) {
                    if(!jsonObject.isNull("data")){
                       ownerMobNumber = jsonObject.getJSONObject("data").getString("MobileNumber");
                        ownerName = jsonObject.getJSONObject("data").getString("OwnerName");
                        ownerFromAddress = jsonObject.getJSONObject("data").getString("FromShortName");
                        ownerToAddress = jsonObject.getJSONObject("data").getString("ToShortName");
                        ownerPicName = jsonObject.getJSONObject("data").getString("imagename");
                        (((TextView)findViewById(R.id.tvUserName))).setText(ownerName);
                        (((TextView)findViewById(R.id.tvPlaceFrom))).setText(ownerFromAddress);
                        (((TextView)findViewById(R.id.tvPlaceTo))).setText(ownerToAddress);
                        if (ownerPicName != null){
                            String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                                    +ownerPicName;
                            //Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(holder.ivUserImage);
                            Picasso.with(RateCabActivity.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivUserImage));

                        }
                        String[] arr2 = jsonObject.getJSONObject("data").getString("TravelDate").trim().split("/");
                        int month = Integer.parseInt(arr2[1]);
                        int date = Integer.parseInt(arr2[0]);
                        (((TextView)findViewById(R.id.tvDate))).setText(String.format("%02d",date)+" "+getMontString(month));
                        String time = jsonObject.getJSONObject("data").getString("TravelTime").trim();
                        (((TextView)findViewById(R.id.tvTime))).setText(time);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setSelection(int id){
        if(id == R.id.tvVehicleCondition){
            selectedReason = getResources().getString(R.string.reason_1);
            findViewById(R.id.tvVehicleCondition).setBackgroundColor(Color.parseColor("#e8e8e8"));
            findViewById(R.id.tvRideGiverBehav).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvPunctuality).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvOther).setBackgroundColor(Color.parseColor("#ffffff"));

        }else if(id == R.id.tvRideGiverBehav){
            selectedReason = getResources().getString(R.string.reason_2);
            findViewById(R.id.tvVehicleCondition).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvRideGiverBehav).setBackgroundColor(Color.parseColor("#e8e8e8"));
            findViewById(R.id.tvPunctuality).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvOther).setBackgroundColor(Color.parseColor("#ffffff"));

        }else if(id == R.id.tvPunctuality){
            selectedReason = getResources().getString(R.string.reason_3);
            findViewById(R.id.tvVehicleCondition).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvRideGiverBehav).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvPunctuality).setBackgroundColor(Color.parseColor("#e8e8e8"));
            findViewById(R.id.tvOther).setBackgroundColor(Color.parseColor("#ffffff"));

        }else if(id == R.id.tvOther){
            selectedReason = getResources().getString(R.string.reason_4);
            findViewById(R.id.tvVehicleCondition).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvRideGiverBehav).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvPunctuality).setBackgroundColor(Color.parseColor("#ffffff"));
            findViewById(R.id.tvOther).setBackgroundColor(Color.parseColor("#e8e8e8"));
        }
        findViewById(R.id.buttonRateCab).setEnabled(true);
        findViewById(R.id.buttonRateCab).setBackgroundColor(getResources().getColor(R.color.color_app_blue));
    }
    private void resetAll(){
        selectedReason = "";
        findViewById(R.id.tvVehicleCondition).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.tvRideGiverBehav).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.tvPunctuality).setBackgroundColor(Color.parseColor("#ffffff"));
        findViewById(R.id.tvOther).setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void sendRating(){
        if(Double.parseDouble(currentRating)<4 && TextUtils.isEmpty(selectedReason)){
            CustomDialog.showDialog(RateCabActivity.this, "Please select a reason for this rating");
            return;
        }
            String endpoint = GlobalVariables.ServiceUrl + "/rateUser.php";
            String authString =cabIDIntent+MobileNumberstr+notificationId+ownerMobNumber+currentRating+selectedReason;
            if(Double.parseDouble(currentRating)>=4){
                selectedReason = "";
            }
            String params = "ownerNumber="+ownerMobNumber+"&memberNumber="+MobileNumberstr+"&rating="+currentRating+"&cabId="+cabIDIntent+"&reason="+selectedReason+"&notificationId="+notificationId+ "&auth="
                    + GlobalMethods.calculateCMCAuthString(authString);

            new GlobalAsyncTask(RateCabActivity.this, endpoint, params,
                    null, RateCabActivity.this, true, "sendrating",
                    false);

    }

    private void getNotificationDetail(){
        String endpoint = GlobalVariables.ServiceUrl + "/getRideDetail.php";
        String authString = cabIDIntent;
        String params = "cabId="+cabIDIntent+"&auth=" + GlobalMethods.calculateCMCAuthString(authString);

        new GlobalAsyncTask(RateCabActivity.this, endpoint, params,
                null, RateCabActivity.this, true, "getcabdetail",
                false);
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
