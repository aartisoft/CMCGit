package com.clubmycab.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.Communicator;
import com.clubmycab.FareCalculatorNewCarPool;
import com.clubmycab.LocationShareForRideService;
import com.clubmycab.R;
import com.clubmycab.StartTripPHPAlarm;
import com.clubmycab.UpcomingStartTripAlarm;
import com.clubmycab.adapter.JoinedMemberAdapter;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.JoinedMemberModel;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CheckNetworkConnection;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class XCheckPoolFragmentActivty extends AppCompatActivity implements GlobalAsyncTask.AsyncTaskResultListener, View.OnClickListener {

    private RideDetailsModel rideDetailsModel;
    private GoogleMap checkpoolmap;
    private String comefrom;
    private String FullName, MemberNumberstr, OwnerMobileNumber;
    private SharedPreferences mPrefs;
    private Dialog onedialog;
    private ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();
    private ArrayList<Double> FaredistanceList = new ArrayList<Double>();
    private ArrayList<LatLng> FareLocationList = new ArrayList<LatLng>();
    private ArrayList<String> FareMobNoList = new ArrayList<String>();
    private ArrayList<LatLng> FareMemberPickLocaton = new ArrayList<LatLng>();
    private ArrayList<LatLng> FareMemberDropLocaton = new ArrayList<LatLng>();
    private ArrayList<String> ShowMemberName = new ArrayList<String>();
    private ArrayList<String> ShowMemberNumber = new ArrayList<String>();
    private ArrayList<String> ShowMemberLocationAddress = new ArrayList<String>();
    private ArrayList<String> ShowMemberLocationLatLong = new ArrayList<String>();
    private ArrayList<String> ShowMemberImageName = new ArrayList<String>();
    private ArrayList<String> ShowMemberStatus = new ArrayList<String>();
    private ArrayList<String> ShowMemberLocationAddressEnd = new ArrayList<String>();
    private ArrayList<String> ShowMemberLocationLatLongEnd = new ArrayList<String>();
    private ArrayList<String> steps = new ArrayList<String>();
    private ArrayList<String> Summary = new ArrayList<String>();
    private ArrayList<String> startaddress = new ArrayList<String>();
    private ArrayList<String> endaddress = new ArrayList<String>();
    private ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
    private ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
    private ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
    private ArrayList<LatLng> via_waypoint = new ArrayList<LatLng>();
    private ArrayList<String> via_waypointstrarr = new ArrayList<String>();
    private String showmembersresp, CompletePageResponse;
    private ListView lvJoinedMemeber;
    private JoinedMemberAdapter joinedMemberAdapter;
    private ArrayList<JoinedMemberModel> joinedMemberArray = new ArrayList<JoinedMemberModel>();
    private String dropuserfrompopupresp;
    private String ownerName;
    private GoogleAnalytics analytics;
    private Tracker tracker;
    private String ownercancelpoolresp;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private boolean isComeFromShowHistory;
    private String startresp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xcheck_pool_fragment_activty);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvStart).setOnClickListener(this);
        findViewById(R.id.cardBack).setOnClickListener(this);

        analytics = GoogleAnalytics.getInstance(XCheckPoolFragmentActivty.this);
        tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("Owner Ride Joined Screen");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        MemberNumberstr = mPrefs.getString("MobileNumber", "");
        lvJoinedMemeber = (ListView) findViewById(R.id.lvJoinedMember);
        joinedMemberAdapter = new JoinedMemberAdapter();
        joinedMemberAdapter.init(XCheckPoolFragmentActivty.this, joinedMemberArray, isComeFromShowHistory,tracker);
        lvJoinedMemeber.setAdapter(joinedMemberAdapter);
        getBundleData();
        initViews();
        initBottomSliderViews();

    }

    private void initBottomSliderViews() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }, 6000);
        ((TextView) findViewById(R.id.tvStart)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvTripCompleted)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvUserName)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvDate)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvTime)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvPlaceFrom)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvPlaceTo)).setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this, AppConstants.HELVITICA));

        if (rideDetailsModel != null) {
            ((TextView) findViewById(R.id.tvUserName)).setText(rideDetailsModel.getOwnerName());
            ((TextView) findViewById(R.id.tvTime)).setText(rideDetailsModel.getTravelTime());
            String[] arr2 = rideDetailsModel.getTravelDate().toString().trim().split("/");
            int month = Integer.parseInt(arr2[1]);
            int date = Integer.parseInt(arr2[0]);
            ((TextView) findViewById(R.id.tvDate)).setText(String.format("%02d", date) + " " + getMontString(month));
            ((TextView) findViewById(R.id.tvPlaceFrom)).setText(rideDetailsModel.getFromLocation());
            ((TextView) findViewById(R.id.tvPlaceTo)).setText(rideDetailsModel.getToLocation());
            if (rideDetailsModel.getImagename() != null) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Click").setAction("User Image Clicked")
                        .setLabel("Pic Clicked").build());
                String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                        + rideDetailsModel.getImagename().toString().trim();
                // Glide.with(XCheckPoolFragmentActivty.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivOwnerImage));
                Picasso.with(XCheckPoolFragmentActivty.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView) findViewById(R.id.ivOwnerImage));

            }
            if (rideDetailsModel.getCabStatus().equalsIgnoreCase("A")) {
                if (rideDetailsModel.getStatus().equalsIgnoreCase("0")) {
                    ((TextView) findViewById(R.id.tvStart)).setText("START");
                } else if (rideDetailsModel.getStatus().equalsIgnoreCase("1")) {
                    ((TextView) findViewById(R.id.tvStart)).setText("FINISH");

                } else if (rideDetailsModel.getStatus().equalsIgnoreCase("2")) {
                    ((TextView) findViewById(R.id.tvStart)).setText("FINISHED");
                    findViewById(R.id.tvCancel).setVisibility(View.GONE);
                }
            }
        }
        if (isComeFromShowHistory) {
            if (isComeFromShowHistory) {
                findViewById(R.id.tvStart).setVisibility(View.GONE);
                findViewById(R.id.tvCancel).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.tvTripCompleted)).setVisibility(View.VISIBLE);
            }
        }
        findViewById(R.id.ivOwnerImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                        + rideDetailsModel.getImagename().toString().trim();
                Intent intent = new Intent(XCheckPoolFragmentActivty.this, ImageScreen.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        if (CheckNetworkConnection.isNetworkAvailable(XCheckPoolFragmentActivty.this)) {
            checkpoolmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.checkpoolmap)).getMap();

            if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {// Active Ride
                // Do Action on active Ride--------------->
                // checkpoolbottomtabsll.setVisibility(View.VISIBLE);
            } else {
                //  checkpoolbottomtabsll.setVisibility(View.GONE);
            }
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
            if (status != ConnectionResult.SUCCESS) { // Google Play Services are
                int requestCode = 10;
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
                dialog.show();
            } else {
                getMemebersListforRide();
            }
        } else {
            CheckNetworkConnection.showConnectionErrorDialog(XCheckPoolFragmentActivty.this);
            return;
        }

    }

    /**
     * UtitlitMethods
     */

    /**
     * Fetch list of member joined to this group
     */
    private void getMemebersListforRide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForShowMembersOnMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskForShowMembersOnMap().execute();
        }
    }


    /**
     * This is used when no member is joined to this ride it will return mutiple routes
     */
    private void fetchMultipleRoute() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForDirections().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskForDirections().execute();
        }

    }

    private void startTrip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForStartTrip().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    rideDetailsModel.getCabId());
        } else {
            new ConnectionTaskForStartTrip().execute(rideDetailsModel
                    .getCabId());
        }
    }

    private void finishTrip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForMarkTripCompleted().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rideDetailsModel.getCabId());
        } else {
            new ConnectionTaskForMarkTripCompleted().execute(rideDetailsModel.getCabId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                tracker.send(new HitBuilders.EventBuilder().setCategory("Cancel ride").setAction("Cancel ride button pressed").setLabel("Cancel ride").build());
                cancelTrip();
                break;
            case R.id.tvStart:
                if (((TextView) findViewById(R.id.tvStart)).getText().toString().equalsIgnoreCase("START")) {
                    startTrip();
                } else if (((TextView) findViewById(R.id.tvStart)).getText().toString().equalsIgnoreCase("FINISH")) {

                    finishTrip();
                }
                break;
            case R.id.cardBack:
                finish();
                break;
        }
    }

    private class ConnectionTaskForStartTrip extends
            AsyncTask<String, Void, Void> {
        boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionStartTrip mAuth1 = new AuthenticateConnectionStartTrip();
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
            hideProgressBar();
            try {
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XCheckPoolFragmentActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(startresp)) {
                    JSONObject jsonObject = new JSONObject(startresp);
                    if (!jsonObject.isNull("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(XCheckPoolFragmentActivty.this).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Success!");
                        alertDialog.setMessage("Enjoy your ride. Remind your riders for payment when they get in the car.");

                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //scheduleStartTripNotification();

                                Intent mainIntent = new Intent(
                                        XCheckPoolFragmentActivty.this,
                                        NewHomeScreen.class);

                                mainIntent.putExtra("comefrom", "comefrom");
                                mainIntent.putExtra("cabID", "-1");// false condition can be improved

                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivityForResult(mainIntent, 500);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                finish();

                            }
                        });
                        alertDialog.show();
                        //CustomDialog.showDialog(XCheckPoolFragmentActivty.this,jsonObject.getString("message"));
                        ((TextView) findViewById(R.id.tvStart)).setText("FINISH");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class AuthenticateConnectionStartTrip {

        public String cid;
        private boolean exceptioncheck;

        public AuthenticateConnectionStartTrip() {

        }

        public void connection() throws Exception {

            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/startTripNotification.php";
            HttpPost httpPost = new HttpPost(url_select);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
                    cid);

            String authString = cid;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            nameValuePairList.add(CabIdValuePair);
            nameValuePairList.add(authValuePair);

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                    nameValuePairList);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);

            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                startresp = stringBuilder.append(bufferedStrChunk).toString();
            }

            Log.d("startresp", "" + startresp);

            if (startresp != null && startresp.length() > 0
                    && startresp.contains("Unauthorized Access")) {
                Log.e("CheckPoolFragmentActivity",
                        "AuthenticateConnectionStartTrip Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(CheckPoolFragmentActivity.this,
                // getResources().getString(R.string.exceptionstring),
                // Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public void sendRemoveMemberRequest(final String memberNum, final String memberName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                XCheckPoolFragmentActivty.this);
        builder.setMessage("Are you sure you want to remove this person from the ride?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new ConnectionTaskFordropuserfrompopup()
                                    .executeOnExecutor(
                                            AsyncTask.THREAD_POOL_EXECUTOR,
                                            memberNum, memberName);
                        } else {
                            new ConnectionTaskFordropuserfrompopup()
                                    .execute(memberNum, memberNum);
                        }
                    }
                });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView) dialog
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }


    private void getBundleData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Gson gson = new Gson();
            rideDetailsModel = gson.fromJson(intent.getStringExtra("RideDetailsModel"), RideDetailsModel.class);
            if (rideDetailsModel != null) {
                OwnerMobileNumber = rideDetailsModel.getMobileNumber();
                ownerName = rideDetailsModel.getOwnerName();
            }
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("comefrom")) {
                    comefrom = intent.getStringExtra("comefrom");
                    if (comefrom.equalsIgnoreCase("showhistory")) {
                        isComeFromShowHistory = true;
                        return;

                    }
                    if (!TextUtils.isEmpty(comefrom)) {
                        if (comefrom.equalsIgnoreCase("GCM")) {
                            String nid = intent.getStringExtra("nid");
                            String params = "rnum=" + "&nid=" + nid + "&auth=" + GlobalMethods.calculateCMCAuthString(nid);
                            String endpoint = GlobalVariables.ServiceUrl + "/UpdateNotificationStatusToRead.php";
                            Log.d("XCheckPoolFragmentActivty", "UpdateNotificationStatusToRead endpoint : " + endpoint + " params : " + params);
                            new GlobalAsyncTask(this, endpoint, params, null, this, false, "UpdateNotificationStatusToRead", false);
                        }
                    }
                }


            }
        }
    }

    private void showProgressBar(){
        try{
            onedialog = new Dialog(XCheckPoolFragmentActivty.this);
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

    /**
     * ---------------------------Utility Maps----------------------->
     */
    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    /**
     * ----------------------------------------------------Network Request--------------------------->
     */
    private class ConnectionTaskForShowMembersOnMap extends AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionShowMembers mAuth1 = new AuthenticateConnectionShowMembers();
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
            hideProgressBar();
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XCheckPoolFragmentActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (showmembersresp != null && showmembersresp.length() > 0
                        && showmembersresp.contains("Unauthorized Access")) {
                    Log.e("XCheckPoolFragmentActivty",
                            "showmembersresp Unauthorized Access");
                    Toast.makeText(XCheckPoolFragmentActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                FaredistanceList.clear();
                FareLocationList.clear();
                FareMemberDropLocaton.clear();
                FareMemberPickLocaton.clear();
                FareMobNoList.clear();

                // Added Owner no and start end location for fare
                FareMobNoList.add(rideDetailsModel.getMobileNumber());
                String fromL[] = rideDetailsModel.getsLatLon().split(",");
                String endL[] = rideDetailsModel.geteLatLon().split(",");

                LatLng llFrom = new LatLng(Double.parseDouble(fromL[0]),
                        Double.parseDouble(fromL[1]));
                LatLng llTo = new LatLng(Double.parseDouble(endL[0]), Double.parseDouble(endL[1]));

                FareMemberPickLocaton.add(llFrom);
                FareMemberDropLocaton.add(llTo);

                if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {
                    if (joinedMemberAdapter != null) {
                        joinedMemberArray.clear();
                        joinedMemberAdapter.init(XCheckPoolFragmentActivty.this, joinedMemberArray, isComeFromShowHistory,tracker);
                        joinedMemberAdapter.notifyDataSetChanged();
                    }
                    fetchMultipleRoute();
                } else {

                    // mycalculatorbtn.setVisibility(View.VISIBLE);

                    ShowMemberName.clear();
                    ShowMemberNumber.clear();
                    ShowMemberLocationAddress.clear();
                    ShowMemberLocationLatLong.clear();
                    ShowMemberImageName.clear();
                    ShowMemberStatus.clear();
                    ShowMemberLocationAddressEnd.clear();
                    ShowMemberLocationLatLongEnd.clear();

                    joinedMemberArray.clear();
                    try {
                        JSONArray subArray = new JSONArray(showmembersresp);
                        for (int i = 0; i < subArray.length(); i++) {
                            try {
                                JoinedMemberModel joinedMemberModel = new JoinedMemberModel();
                                joinedMemberModel.setMemeberName(subArray.getJSONObject(i).optString("MemberName"));
                                joinedMemberModel.setMemberNumber(subArray.getJSONObject(i).optString("MemberNumber"));
                                joinedMemberModel.setMemberImageName(subArray.getJSONObject(i).optString("MemberImageName"));
                                joinedMemberModel.setHasBoarded(subArray.getJSONObject(i).optString("hasBoarded"));

                                joinedMemberArray.add(joinedMemberModel);

                                ShowMemberName.add(subArray.getJSONObject(i)
                                        .getString("MemberName").toString());
                                ShowMemberNumber.add(subArray.getJSONObject(i)
                                        .getString("MemberNumber").toString());
                                ShowMemberLocationAddress.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberLocationAddress")
                                        .toString());
                                ShowMemberLocationLatLong.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberLocationlatlong")
                                        .toString());
                                ShowMemberImageName.add(subArray.getJSONObject(i)
                                        .getString("MemberImageName").toString());
                                ShowMemberStatus.add(subArray.getJSONObject(i)
                                        .getString("Status").toString());

                                ShowMemberLocationAddressEnd.add(subArray.getJSONObject(i).getString("MemberEndLocationAddress").toString());
                                ShowMemberLocationLatLongEnd.add(subArray.getJSONObject(i).getString("MemberEndLocationlatlong").toString());

                                // Added for fare calculation
                                FareMobNoList.add(subArray.getJSONObject(i).getString("MemberNumber").toString());

                                String arr[] = subArray.getJSONObject(i)
                                        .getString("MemberLocationlatlong")
                                        .toString().split(",");

                                LatLng l = new LatLng(Double.parseDouble(arr[0]),
                                        Double.parseDouble(arr[1]));
                                FareMemberPickLocaton.add(l);
                                String arr1[] = subArray.getJSONObject(i)
                                        .getString("MemberEndLocationlatlong")
                                        .toString().split(",");

                                LatLng l1 = new LatLng(Double.parseDouble(arr1[0]),
                                        Double.parseDouble(arr1[1]));
                                FareMemberDropLocaton.add(l1);
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if (joinedMemberAdapter != null) {
                            joinedMemberAdapter.init(XCheckPoolFragmentActivty.this, joinedMemberArray, isComeFromShowHistory,tracker);
                            joinedMemberAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    String wayPoint = "&waypoints=optimize:true";

                    if (!showmembersresp.equalsIgnoreCase("No Members joined yet")) {
                        steps.clear();
                        Summary.clear();
                        startaddress.clear();
                        endaddress.clear();
                        startaddlatlng.clear();
                        endaddlatlng.clear();
                        listGeopoints.clear();
                        via_waypoint.clear();
                        via_waypointstrarr.clear();
                        for (int i = 0; i < rectlinesarr.size(); i++) {

                            Polyline polyline = checkpoolmap
                                    .addPolyline(rectlinesarr.get(i));
                            polyline.remove();

                        }
                        checkpoolmap.clear();

                        rectlinesarr.clear();

                        // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

                        if (!showmembersresp
                                .equalsIgnoreCase("No Members joined yet")) {
                            for (int i = 0; i < ShowMemberLocationLatLong.size(); i++) {

                                String latlong[] = ShowMemberLocationLatLong.get(i)
                                        .split(",");

                                wayPoint += "%7C" + latlong[0] + "," + latlong[1];
                                if (!ShowMemberLocationLatLongEnd.get(i)
                                        .equalsIgnoreCase("")) {
                                    String latlong1[] = ShowMemberLocationLatLongEnd
                                            .get(i).split(",");
                                    wayPoint += "%7C" + latlong1[0] + ","
                                            + latlong1[1];
                                }

                            }

                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new ConnectionTaskForSingleRoot().executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR, wayPoint);
                        } else {
                            new ConnectionTaskForSingleRoot().execute(wayPoint);
                        }

                    } else {


                        if (rideDetailsModel.getCabStatus().equals("A")
                                && rideDetailsModel.getStatus().equals("0")) {
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                        "dd/MM/yyyy hh:mm aa");
                                Date date = simpleDateFormat.parse(rideDetailsModel.getTravelDate() + " " + rideDetailsModel.getTravelTime());
                                ////////////////////////////Commented////////////////////////
                          /*  ArrayList<String> arrayList = readBookedOrCarPreference();
                            if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
                                showTripStartDialog();
                            }*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (rideDetailsModel.getCabStatus().equals("A")
                                && rideDetailsModel.getStatus().equals("2")) {
                            Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
                            extraParams.put("cabid", rideDetailsModel.getCabId());

                            if (rideDetailsModel.getRideType().equals("1")) {
                                FareCalculatorNewCarPool fareCalculatorNewCarPool = new FareCalculatorNewCarPool(
                                        FareMobNoList, FareMemberPickLocaton,
                                        FareMemberDropLocaton, FareLocationList,
                                        FaredistanceList,
                                        Double.valueOf(rideDetailsModel
                                                .getPerKmCharge()));

                                HashMap<String, Double> hashMap = fareCalculatorNewCarPool
                                        .getFareSplit();

                                Log.d("XCheckPoolFragmentActivty",
                                        "fareCalculatorNew : " + hashMap);
                                //  sendFareSplitToMembers(hashMap);
                            } else {
                                // showRideCompleteDialog();
                            }
                        } else if (rideDetailsModel.getCabStatus().equals("A")
                                && rideDetailsModel.getStatus().equals("3")) {
                            // showPaymentDialog();
                        } else if (rideDetailsModel.getCabStatus().equals("A")) {
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                        "dd/MM/yyyy hh:mm aa");
                                Date date = simpleDateFormat.parse(rideDetailsModel
                                        .getTravelDate()
                                        + " "
                                        + rideDetailsModel.getTravelTime());

                                long expDuration = Long.parseLong(rideDetailsModel
                                        .getExpTripDuration());

                                if (System.currentTimeMillis() >= (date.getTime() + expDuration * 1000)) {
                                    Log.d("XCheckPoolFragmentActivty",
                                            "ExpTripDuration trip completed");
                               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new ConnectionTaskForTripCompleted().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rideDetailsModel.getCabId());
                                } else {
                                    new ConnectionTaskForTripCompleted().execute(rideDetailsModel.getCabId());
                                }*/
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    checkpoolmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker arg0) {

                            if (arg0.getSnippet().equalsIgnoreCase("start")) {

                                arg0.showInfoWindow();
                                // showStartEndLocDialog("START LOCATION",rideDetailsModel.getOwnerName(), rideDetailsModel.getMobileNumber(), rideDetailsModel.getFromLocation(), rideDetailsModel.getImagename());

                            } else if (arg0.getSnippet().equalsIgnoreCase(
                                    "end")) {
                                arg0.showInfoWindow();
                                // showStartEndLocDialog("END LOCATION",rideDetailsModel.getOwnerName(), rideDetailsModel.getMobileNumber(), rideDetailsModel.getToLocation(), rideDetailsModel.getImagename());


                            } else {

                                if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
                                     arg0.showInfoWindow();
                                }
                            }

                            return false;
                        }

                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public class AuthenticateConnectionShowMembers {

        public AuthenticateConnectionShowMembers() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/ShowMemberOnMap.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                    "CabId", rideDetailsModel.getCabId());

            String authString = rideDetailsModel.getCabId();
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
                showmembersresp = stringBuilder.append(bufferedStrChunk)
                        .toString();
            }

            Log.d("showmembersresp", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForSingleRoot extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionSingleRoot mAuth1 = new AuthenticateConnectionSingleRoot();
            try {
                mAuth1.wayPointUrl = args[0];
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

            hideProgressBar();
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XCheckPoolFragmentActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // int index = 0;
                rectlinesarr.clear();

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
                        rnd.nextInt(256));
                for (int i = 0; i < steps.size(); i++) {
                    ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
                    JSONArray subArray123;
                    try {
                        subArray123 = new JSONArray(steps.get(i));
                        for (int i111 = 0; i111 < subArray123.length(); i111++) {
                            String locationstr = subArray123.getJSONObject(i111).getString("start_location").toString();
                            JSONObject jsonObject11 = new JSONObject(locationstr);
                            double lat1 = Double.parseDouble(jsonObject11.getString("lat"));
                            double lng1 = Double.parseDouble(jsonObject11.getString("lng"));
                            listGeopoints.add(new LatLng(lat1, lng1));
                            String locationstr1 = subArray123.getJSONObject(i111).getString("polyline").toString();
                            JSONObject jsonObject111 = new JSONObject(locationstr1);
                            String points = jsonObject111.getString("points");
                            ArrayList<LatLng> arr = decodePoly(points);
                            for (int j = 0; j < arr.size(); j++) {
                                listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                            }
                            String locationstr11 = subArray123.getJSONObject(i111).getString("end_location").toString();
                            JSONObject jsonObject1111 = new JSONObject(locationstr11);
                            double lat11 = Double.parseDouble(jsonObject1111.getString("lat"));
                            double lng11 = Double.parseDouble(jsonObject1111.getString("lng"));
                            listGeopoints.add(new LatLng(lat11, lng11));
                            PolylineOptions rectLine = new PolylineOptions().width(8).color(getResources().getColor(R.color.color_app_blue));
                            rectLine.addAll(listGeopoints);
                            rectlinesarr.add(rectLine);

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (ActivityCompat.checkSelfPermission(XCheckPoolFragmentActivty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(XCheckPoolFragmentActivty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                checkpoolmap.setMyLocationEnabled(true);

                checkpoolmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {

                        // setOnMapClick(point);
                    }
                });

                for (int i = 0; i < startaddlatlng.size(); i++) {

                    Log.d("startaddlatlng", "" + startaddlatlng.size());

                    if (i == 0)
                        checkpoolmap.addMarker(new MarkerOptions()
                                .position(startaddlatlng.get(i))
                                .title(startaddress.get(i))
                                .snippet("start")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.start)));

                    else if (i == (startaddlatlng.size() - 1))

                        checkpoolmap.addMarker(new MarkerOptions()
                                .position(endaddlatlng.get(i))
                                .title(endaddress.get(i))
                                .snippet("end")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.end)));
                    else {

                        checkpoolmap
                                .addMarker(new MarkerOptions()
                                        .position(startaddlatlng.get(i))
                                        .title(startaddress.get(i))
                                        // .snippet(String.valueOf(i) + "," + "Pick")
                                        .snippet("Pick"+" , "+ShowMemberName.get(i-1))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        checkpoolmap
                                .addMarker(new MarkerOptions()
                                        .position(endaddlatlng.get(i))
                                        .title(endaddress.get(i))
                                        // .snippet(String.valueOf(i) + "," + "Drop")
                                        .snippet("Drop"+" , "+ShowMemberName.get(i-1))

                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }

                }
                LatLngBounds.Builder bc = null;

                for (int i = 0; i < rectlinesarr.size(); i++) {
                    checkpoolmap.addPolyline(rectlinesarr.get(i));

                    List<LatLng> points = rectlinesarr.get(i).getPoints();

                    bc = new LatLngBounds.Builder();

                    for (LatLng item : points) {
                        bc.include(item);
                    }
                }

                bc.include(startaddlatlng.get(0));
                bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
                checkpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        bc.build(), 50));

                Log.d("FareMobno", "" + FareMobNoList);
                Log.d("FareDistance", "" + FaredistanceList);
                Log.d("FareLocation", "" + FareLocationList);
                Log.d("FarePick", "" + FareMemberPickLocaton);
                Log.d("FareDrop", "" + FareMemberDropLocaton);

                // //////////////////////////
                if (rideDetailsModel.getCabStatus().equals("A")
                        && rideDetailsModel.getStatus().equals("0")) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                "dd/MM/yyyy hh:mm aa");
                        Date date = simpleDateFormat.parse(rideDetailsModel
                                .getTravelDate()
                                + " "
                                + rideDetailsModel.getTravelTime());

                        //   ArrayList<String> arrayList = readBookedOrCarPreference();

                        // Log.d("XCheckPoolFragmentActivty", "startTime : " +
                        // date.getTime());

                        if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.START_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
                            if (rideDetailsModel.getBookingRefNo() == null
                                    || rideDetailsModel.getBookingRefNo().isEmpty()
                                    || rideDetailsModel.getBookingRefNo()
                                    .equalsIgnoreCase("null")) {
                         /*   if (arrayList == null) {
                                showCabBookingDialog(true);
                            } else if (arrayList != null
                                    && arrayList.indexOf(rideDetailsModel
                                    .getCabId()) == -1) {
                                showCabBookingDialog(true);
                            } else {
                                showTripStartDialog();
                            }*/
                            } else {
                                //showTripStartDialog();
                            }
                        } else if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
                            if (rideDetailsModel.getBookingRefNo() == null
                                    || rideDetailsModel.getBookingRefNo().isEmpty()
                                    || rideDetailsModel.getBookingRefNo()
                                    .equalsIgnoreCase("null")) {
                           /* if (arrayList == null) {
                                showCabBookingDialog(false);
                            } else if (arrayList != null
                                    && arrayList.indexOf(rideDetailsModel
                                    .getCabId()) == -1) {
                                showCabBookingDialog(false);
                            }*/
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (rideDetailsModel.getCabStatus().equals("A")
                        && rideDetailsModel.getStatus().equals("2")) {
                    // For affle traking view
                    Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
                    extraParams.put("cabid", rideDetailsModel.getCabId());

                    if (rideDetailsModel.getRideType().equals("1")) {
                        FareCalculatorNewCarPool fareCalculatorNewCarPool = new FareCalculatorNewCarPool(
                                FareMobNoList, FareMemberPickLocaton,
                                FareMemberDropLocaton, FareLocationList,
                                FaredistanceList, Double.valueOf(rideDetailsModel
                                .getPerKmCharge()));

                        HashMap<String, Double> hashMap = fareCalculatorNewCarPool
                                .getFareSplit();

                        Log.d("XCheckPoolFragmentActivty", "fareCalculatorNew : "
                                + hashMap);

                        // sendFareSplitToMembers(hashMap);
                    } else {
                        //  showRideCompleteDialog();
                    }
                } else if (rideDetailsModel.getCabStatus().equals("A")
                        && rideDetailsModel.getStatus().equals("3")) {
                    // showPaymentDialog();
                } else if (rideDetailsModel.getCabStatus().equals("A")) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                "dd/MM/yyyy hh:mm aa");
                        Date date = simpleDateFormat.parse(rideDetailsModel
                                .getTravelDate()
                                + " "
                                + rideDetailsModel.getTravelTime());

                        long expDuration = Long.parseLong(rideDetailsModel
                                .getExpTripDuration());

                        if (System.currentTimeMillis() >= (date.getTime() + expDuration * 1000)) {
                            Log.d("XCheckPoolFragmentActivty",
                                    "ExpTripDuration trip completed");
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new ConnectionTaskForTripCompleted()
                                    .executeOnExecutor(
                                            AsyncTask.THREAD_POOL_EXECUTOR,
                                            rideDetailsModel.getCabId());
                        } else {
                            new ConnectionTaskForTripCompleted()
                                    .execute(rideDetailsModel.getCabId());
                        }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public class AuthenticateConnectionSingleRoot {
        private String wayPointUrl;

        public AuthenticateConnectionSingleRoot() {

        }

        public void connection() throws Exception {
            String fromlatLong = rideDetailsModel.getsLatLon();
            String tolatLong = rideDetailsModel.geteLatLon();

            String source = rideDetailsModel.getFromLocation().replaceAll(" ",
                    "%20");
            String dest = rideDetailsModel.getToLocation().replaceAll(" ",
                    "%20");
            Address locationAddressFrom = null, locationAddressTo = null;

          /*  String fromAdd = rideDetailsModel.getFromLocation();
            String toAdd = rideDetailsModel.getToLocation();
            Geocoder fcoder = new Geocoder(XCheckPoolFragmentActivty.this);
            try {
                ArrayList<Address> adresses = (ArrayList<Address>) fcoder
                        .getFromLocationName(fromAdd, 50);

                for (Address add : adresses) {
                    locationAddressFrom = add;
                }

                adresses = (ArrayList<Address>) fcoder.getFromLocationName(
                        toAdd, 50);
                for (Address add : adresses) {
                    locationAddressTo = add;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            String src = locationAddressFrom.getLatitude() + ","
                    + locationAddressFrom.getLongitude();
            String des = locationAddressTo.getLatitude() + ","
                    + locationAddressTo.getLongitude();

            Log.d("src:", "" + src);
            Log.d("des", "" + des);*/

            // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

            String url = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin="
                    + fromlatLong
                    + "&destination="
                    + tolatLong
                    + wayPointUrl
                    + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                    + GlobalVariables.GoogleMapsAPIKey;

            Log.d("url single path", "" + url);

            CompletePageResponse = new Communicator().executeHttpGet(url);

            CompletePageResponse = CompletePageResponse
                    .replaceAll("\\\\/", "/");

            JSONObject jsonObject = new JSONObject(CompletePageResponse);

            String name = jsonObject.getString("routes");

            JSONArray subArray = new JSONArray(name);
            Summary.clear();

            for (int i = 0; i < subArray.length(); i++) {

                Summary.add(subArray.getJSONObject(i).getString("summary")
                        .toString());

                String name1 = subArray.getJSONObject(i).getString("legs")
                        .toString();

                JSONArray subArray1 = new JSONArray(name1);

                for (int i1 = 0; i1 < subArray1.length(); i1++) {

                    // int i1 = 0;
                    startaddress.add(subArray1.getJSONObject(i1)
                            .getString("start_address").toString());
                    endaddress.add(subArray1.getJSONObject(i1)
                            .getString("end_address").toString());

                    String startadd = subArray1.getJSONObject(i1)
                            .getString("start_location").toString();

                    JSONObject jsonObject1 = new JSONObject(startadd);
                    double lat = Double.parseDouble(jsonObject1
                            .getString("lat"));
                    double lng = Double.parseDouble(jsonObject1
                            .getString("lng"));

                    startaddlatlng.add(new LatLng(lat, lng));

                    //
                    String endadd = subArray1.getJSONObject(i1)
                            .getString("end_location").toString();

                    JSONObject jsonObject41 = new JSONObject(endadd);
                    double lat4 = Double.parseDouble(jsonObject41
                            .getString("lat"));
                    double lng4 = Double.parseDouble(jsonObject41
                            .getString("lng"));

                    endaddlatlng.add(new LatLng(lat4, lng4));

                    // Code fro get distance and duration

                    String duration = subArray1.getJSONObject(i1)
                            .getString("duration").toString();
                    JSONObject jsonObjectDuraton = new JSONObject(duration);

                    //  durationList.add(jsonObjectDuraton.getInt("value"));

                    String distance = subArray1.getJSONObject(i1)
                            .getString("distance").toString();
                    JSONObject jsonObjectDistance = new JSONObject(distance);

                    //   distanceList.add(jsonObjectDistance.getInt("value"));
                    // ////////////

                    if (i1 == 0) {
                        FareLocationList.add(new LatLng(lat, lng));
                        FareLocationList.add(new LatLng(lat4, lng4));
                        FaredistanceList.add(0.0);
                        FaredistanceList.add(Double.parseDouble(""
                                + jsonObjectDistance.getInt("value")));

                    } else {
                        FareLocationList.add(new LatLng(lat4, lng4));
                        FaredistanceList.add(Double.parseDouble(""
                                + jsonObjectDistance.getInt("value")));

                    }

                    steps.add(subArray1.getJSONObject(i1).getString("steps")
                            .toString());

                    // //////////////
                    String mska = subArray1.getJSONObject(i1)
                            .getString("via_waypoint").toString();

                    if (mska.equalsIgnoreCase("[]")) {
                        via_waypoint.add(new LatLng(0, 0));
                    } else {
                        JSONArray subArray12 = new JSONArray(mska);

                        for (int i11 = 0; i11 < subArray12.length(); i11++) {

                            String locationstr = subArray12.getJSONObject(i11)
                                    .getString("location").toString();

                            JSONObject jsonObject1111 = new JSONObject(
                                    locationstr);
                            double lat1111 = Double.parseDouble(jsonObject1111
                                    .getString("lat"));
                            double lng1111 = Double.parseDouble(jsonObject1111
                                    .getString("lng"));

                            via_waypoint.add(new LatLng(lat1111, lng1111));

                        }
                    }
                }
            }

            // /////
            Log.d("Summary", "" + Summary);
            Log.d("startaddress", "" + startaddress);
            Log.d("endaddress", "" + endaddress);
            Log.d("startaddlatlng", "" + startaddlatlng);
            Log.d("endaddlatlng", "" + endaddlatlng);
            Log.d("via_waypoint", "" + via_waypoint);

            for (int i = 0; i < via_waypoint.size(); i++) {
                String asd = MapUtilityMethods.getAddress(
                        XCheckPoolFragmentActivty.this,
                        via_waypoint.get(i).latitude,
                        via_waypoint.get(i).longitude);
                via_waypointstrarr.add(asd);
            }
            Log.d("via_waypointstrarr", "" + via_waypointstrarr);
        }
    }

    private class ConnectionTaskForDirections extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionGetDirection mAuth1 = new AuthenticateConnectionGetDirection();
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
            hideProgressBar();
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XCheckPoolFragmentActivty.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
                    return;
                }

                for (int i = 0; i < rectlinesarr.size(); i++) {

                    Polyline polyline = checkpoolmap.addPolyline(rectlinesarr
                            .get(i));
                    polyline.remove();

                }
                checkpoolmap.clear();

                rectlinesarr.clear();

                for (int i = 0; i < steps.size(); i++) {

                    ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

                    JSONArray subArray123;
                    try {
                        subArray123 = new JSONArray(steps.get(i));
                        for (int i111 = 0; i111 < subArray123.length(); i111++) {

                            String locationstr = subArray123.getJSONObject(i111)
                                    .getString("start_location").toString();

                            JSONObject jsonObject11 = new JSONObject(locationstr);
                            double lat1 = Double.parseDouble(jsonObject11
                                    .getString("lat"));
                            double lng1 = Double.parseDouble(jsonObject11
                                    .getString("lng"));

                            listGeopoints.add(new LatLng(lat1, lng1));

                            // /
                            String locationstr1 = subArray123.getJSONObject(i111)
                                    .getString("polyline").toString();

                            JSONObject jsonObject111 = new JSONObject(locationstr1);
                            String points = jsonObject111.getString("points");
                            ArrayList<LatLng> arr = decodePoly(points);
                            for (int j = 0; j < arr.size(); j++) {
                                listGeopoints.add(new LatLng(arr.get(j).latitude,
                                        arr.get(j).longitude));
                            }
                            // /
                            String locationstr11 = subArray123.getJSONObject(i111)
                                    .getString("end_location").toString();

                            JSONObject jsonObject1111 = new JSONObject(
                                    locationstr11);
                            double lat11 = Double.parseDouble(jsonObject1111
                                    .getString("lat"));
                            double lng11 = Double.parseDouble(jsonObject1111
                                    .getString("lng"));

                            listGeopoints.add(new LatLng(lat11, lng11));



                            PolylineOptions rectLine = new PolylineOptions().width(
                                    8).color(getResources().getColor(R.color.color_app_blue));
                            rectLine.addAll(listGeopoints);
                            rectlinesarr.add(rectLine);

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                try{
                    // Enabling MyLocation Layer of Google Map
                    checkpoolmap.setMyLocationEnabled(true);

                    LatLngBounds.Builder bc = null;

                    for (int i = 0; i < rectlinesarr.size(); i++) {
                        checkpoolmap.addPolyline(rectlinesarr.get(i));

                        List<LatLng> points = rectlinesarr.get(i).getPoints();

                        bc = new LatLngBounds.Builder();

                        for (LatLng item : points) {
                            bc.include(item);
                        }
                    }

                    checkpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));

                    checkpoolmap.addMarker(new MarkerOptions().position(startaddlatlng.get(0)).title(startaddress.get(0))
                            .snippet("start").icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));

                    checkpoolmap
                            .addMarker(new MarkerOptions()
                                    .position(endaddlatlng.get(0))
                                    .title(endaddress.get(0))
                                    .snippet("end")
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.end)));
                    checkpoolmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (marker.getSnippet().equalsIgnoreCase("start")) {

                                marker.showInfoWindow();
                                //showStartEndLocDialog("START LOCATION",rideDetailsModel.getOwnerName(), rideDetailsModel.getMobileNumber(), rideDetailsModel.getFromLocation(), rideDetailsModel.getImagename());

                            } else if (marker.getSnippet().equalsIgnoreCase("end")) {
                                marker.showInfoWindow();
                                //showStartEndLocDialog("END LOCATION",rideDetailsModel.getOwnerName(), rideDetailsModel.getMobileNumber(), rideDetailsModel.getToLocation(), rideDetailsModel.getImagename());


                            }
                            return false;
                        }
                    });
                    hideProgressBar();
                }catch (Exception e){
                    e.printStackTrace();
                }

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForShowMembersOnMap()
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new ConnectionTaskForShowMembersOnMap().execute();
            }*/

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public class AuthenticateConnectionGetDirection {

        public AuthenticateConnectionGetDirection() {

        }

        public void connection() throws Exception {
            steps.clear();
            Summary.clear();
            startaddress.clear();
            endaddress.clear();
            startaddlatlng.clear();
            endaddlatlng.clear();
            listGeopoints.clear();
            via_waypoint.clear();
            via_waypointstrarr.clear();
            rectlinesarr.clear();
           /* String source = rideDetailsModel.getFromLocation().replaceAll(" ", "%20").replaceAll(" ", "%0A").replaceAll("\n","%0A");;
            String dest = rideDetailsModel.getToLocation().replaceAll(" ", "%20").replaceAll(" ", "%0A").replaceAll("\n","%0A");;
         */
            String source = rideDetailsModel.getsLatLon();
            String dest = rideDetailsModel.geteLatLon();

            String url = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + source + "&destination=" + dest + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                    + GlobalVariables.GoogleMapsAPIKey;
            Log.d("url", "" + url);
            CompletePageResponse = new Communicator().executeHttpGet(url);
            CompletePageResponse = CompletePageResponse.replaceAll("\\\\/", "/");
            JSONObject jsonObject = new JSONObject(CompletePageResponse);
            String name = jsonObject.getString("routes");
            JSONArray subArray = new JSONArray(name);

            double tempDistance = 0;
            int index = 0;
            for (int i = 0; i < subArray.length(); i++) {

                Summary.add(subArray.getJSONObject(i).getString("summary")
                        .toString());

                String name1 = subArray.getJSONObject(i).getString("legs")
                        .toString();

                JSONArray subArray1 = new JSONArray(name1);

                for (int i1 = 0; i1 < subArray1.length(); i1++) {
                    //originalDistance = Integer.parseInt(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value").toString());

                    if(i == 0){
                        tempDistance = Double.parseDouble(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value"));
                    }else {
                        if(Double.parseDouble(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value")) < tempDistance){
                            tempDistance =  Double.parseDouble(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value"));
                            index = i;
                        }
                    }

                }
            }

           // for (int i = 0; i < subArray.length(); i++) {
                Summary.add(subArray.getJSONObject(index).getString("summary").toString());
                String name1 = subArray.getJSONObject(index).getString("legs").toString();
                JSONArray subArray1 = new JSONArray(name1);
                for (int i1 = 0; i1 < subArray1.length(); i1++) {
                    startaddress.add(subArray1.getJSONObject(i1).getString("start_address").toString());
                    endaddress.add(subArray1.getJSONObject(i1).getString("end_address").toString());
                    String startadd = subArray1.getJSONObject(i1).getString("start_location").toString();
                    JSONObject jsonObject1 = new JSONObject(startadd);
                    double lat = Double.parseDouble(jsonObject1.getString("lat"));
                    double lng = Double.parseDouble(jsonObject1.getString("lng"));
                    startaddlatlng.add(new LatLng(lat, lng));
                    String endadd = subArray1.getJSONObject(i1).getString("end_location").toString();
                    JSONObject jsonObject41 = new JSONObject(endadd);
                    double lat4 = Double.parseDouble(jsonObject41.getString("lat"));
                    double lng4 = Double.parseDouble(jsonObject41.getString("lng"));
                    endaddlatlng.add(new LatLng(lat4, lng4));
                    steps.add(subArray1.getJSONObject(i1).getString("steps").toString());
                    String mska = subArray1.getJSONObject(i1).getString("via_waypoint").toString();
                    if (mska.equalsIgnoreCase("[]")) {
                        via_waypoint.add(new LatLng(0, 0));
                    } else {
                        JSONArray subArray12 = new JSONArray(mska);
                        for (int i11 = 0; i11 < subArray12.length(); i11++) {
                            String locationstr = subArray12.getJSONObject(i11).getString("location").toString();
                            JSONObject jsonObject1111 = new JSONObject(locationstr);
                            double lat1111 = Double.parseDouble(jsonObject1111.getString("lat"));
                            double lng1111 = Double.parseDouble(jsonObject1111.getString("lng"));
                            via_waypoint.add(new LatLng(lat1111, lng1111));

                        }
                    }
                }
           // }

            Log.d("Summary", "" + Summary);
            Log.d("startaddress", "" + startaddress);
            Log.d("endaddress", "" + endaddress);
            Log.d("startaddlatlng", "" + startaddlatlng);
            Log.d("endaddlatlng", "" + endaddlatlng);
            Log.d("via_waypoint", "" + via_waypoint);

            for (int i = 0; i < via_waypoint.size(); i++) {
                String asd = MapUtilityMethods.getAddress(
                        XCheckPoolFragmentActivty.this,
                        via_waypoint.get(i).latitude,
                        via_waypoint.get(i).longitude);
                via_waypointstrarr.add(asd);
            }
            Log.d("via_waypointstrarr", "" + via_waypointstrarr);
        }
    }

    private class ConnectionTaskFordropuserfrompopup extends AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectiondropuserfrompopup mAuth1 = new AuthenticateConnectiondropuserfrompopup();
            try {
                mAuth1.memnum = args[0];
                mAuth1.memname = args[1];
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
                   Toast.makeText(XCheckPoolFragmentActivty.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
                   return;
               }

               if (dropuserfrompopupresp != null && dropuserfrompopupresp.length() > 0 && dropuserfrompopupresp.contains("Unauthorized Access")) {
                   Log.e("CheckPoolFragmentActivity", "dropuserfrompopupresp Unauthorized Access");
                   Toast.makeText(XCheckPoolFragmentActivty.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
                   return;
               }

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForDirections()
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new ConnectionTaskForDirections().execute();
            }*/
               getMemebersListforRide();
           }catch (Exception e){
               e.printStackTrace();
           }

        }

    }

    public class AuthenticateConnectiondropuserfrompopup {

        public String memnum;
        public String memname;

        public AuthenticateConnectiondropuserfrompopup() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/dropuserfrompopup.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair("CabId", rideDetailsModel.getCabId());
            BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair("OwnerName", rideDetailsModel.getOwnerName());
            BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair("OwnerNumber", rideDetailsModel.getOwnerName());
            BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair("MemberName", memname);
            BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair("MemberNumber", memnum);
            BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
                    "Message", rideDetailsModel.getOwnerName()
                    + " has removed you from the ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName());

            String authString = rideDetailsModel.getCabId() + memname + memnum
                    + rideDetailsModel.getOwnerName()
                    + " has removed you from the ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName()
                    + rideDetailsModel.getOwnerName()
                    + rideDetailsModel.getOwnerName();
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(OwnerNameBasicNameValuePair);
            nameValuePairList.add(OwnerNumberBasicNameValuePair);
            nameValuePairList.add(MemberNameBasicNameValuePair);
            nameValuePairList.add(MemberNumberBasicNameValuePair);
            nameValuePairList.add(MessageBasicNameValuePair);
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
                dropuserfrompopupresp = stringBuilder.append(bufferedStrChunk)
                        .toString();
            }

            Log.d("dropuserfrompopupresp", "" + stringBuilder.toString());
        }
    }

    @Override
    public void getResult(String response, String uniqueID) {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
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

    ////////////////////////////////////
    private void cancelTrip() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                XCheckPoolFragmentActivty.this);
        builder.setMessage("Are you sure you want to cancel the ride?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Cancel Ride").setAction("Cancel Ride")
                        .setLabel("Cancel Ride").build());

                UpcomingStartTripAlarm upcomingStartTripAlarm = new UpcomingStartTripAlarm();
                upcomingStartTripAlarm
                        .cancelBothAlarms(XCheckPoolFragmentActivty.this);

                StartTripPHPAlarm startTripPHPAlarm = new StartTripPHPAlarm();
                startTripPHPAlarm.cancelAlarm(XCheckPoolFragmentActivty.this);

                stopService(new Intent(XCheckPoolFragmentActivty.this,
                        LocationShareForRideService.class));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForownercancelpool()
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ConnectionTaskForownercancelpool().execute();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView) dialog
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }
    private class ConnectionTaskForownercancelpool extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

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
            AuthenticateConnectionownercancelpool mAuth1 = new AuthenticateConnectionownercancelpool();
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

              if (exceptioncheck) {
                  exceptioncheck = false;
                  Toast.makeText(XCheckPoolFragmentActivty.this,
                          getResources().getString(R.string.exceptionstring),
                          Toast.LENGTH_LONG).show();
                  return;
              }

              if (ownercancelpoolresp != null && ownercancelpoolresp.length() > 0
                      && ownercancelpoolresp.contains("Unauthorized Access")) {
                  Log.e("CheckPoolFragmentActivity",
                          "ownercancelpoolresp Unauthorized Access");
                  Toast.makeText(XCheckPoolFragmentActivty.this,
                          getResources().getString(R.string.exceptionstring),
                          Toast.LENGTH_LONG).show();
                  return;
              }

              Intent mainIntent = new Intent(XCheckPoolFragmentActivty.this,
                      NewHomeScreen.class);
              mainIntent.putExtra("comefrom", "comefrom");
              mainIntent.putExtra("cabID", "-1");// false condition can be improved
              mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                      | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(mainIntent);

              finish();

          }catch (Exception e){
              e.printStackTrace();
          }
        }

    }

    public class AuthenticateConnectionownercancelpool {

        public AuthenticateConnectionownercancelpool() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/cancelpoolbyowner.php";
            HttpPost httpPost = new HttpPost(url_select);

            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                    "CabId", rideDetailsModel.getCabId());
            BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
                    "OwnerName", rideDetailsModel.getOwnerName());
            BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
                    "OwnerNumber", rideDetailsModel.getMobileNumber());
            BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
                    "Message", rideDetailsModel.getOwnerName()
                    + " cancelled the ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName());

            String authString = rideDetailsModel.getCabId()
                    + rideDetailsModel.getOwnerName()
                    + " cancelled the ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName()
                    + rideDetailsModel.getOwnerName()
                    + rideDetailsModel.getMobileNumber();
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(OwnerNameBasicNameValuePair);
            nameValuePairList.add(OwnerNumberBasicNameValuePair);
            nameValuePairList.add(MessageBasicNameValuePair);
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
                ownercancelpoolresp = stringBuilder.append(bufferedStrChunk)
                        .toString();
            }

            Log.d("ownercancelpoolresp", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForMarkTripCompleted extends
            AsyncTask<String, Void, Void> {
        boolean exceptioncheck;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("CheckPoolFragmentActivity",
                    "AuthenticateConnectionMarkTripCompleted cabid : "
                            + args[0]);
            AuthenticateConnectionMarkTripCompleted mAuth1 = new AuthenticateConnectionMarkTripCompleted();
            try {
                if (args.length > 1) {
                    mAuth1.cabid = args[0];
                    mAuth1.owner = args[1];
                    mAuth1.mobileNumber = args[2];
                } else {
                    mAuth1.cabid = args[0];
                }

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
                    Toast.makeText(XCheckPoolFragmentActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                ((TextView)findViewById(R.id.tvStart)).setText("FINISHED");
                Intent mainIntent = new Intent(XCheckPoolFragmentActivty.this, NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mainIntent.putExtra("comefrom", "newridecreation");
                mainIntent.putExtra("cabID", "-1");// This is false condition for restrictiong my rides to not open checkpool (Map for owner)
                startActivity(mainIntent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public class AuthenticateConnectionMarkTripCompleted {

            public String cabid;
            public String owner;
            public String mobileNumber;

            public AuthenticateConnectionMarkTripCompleted() {

            }

            public void connection() throws Exception {

                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl
                        + "/tripCompleted.php";
                HttpPost httpPost = new HttpPost(url_select);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

                BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
                        cabid);

                nameValuePairList.add(CabIdValuePair);

                if (owner != null && mobileNumber != null && !owner.isEmpty()
                        && !mobileNumber.isEmpty()) {
                    BasicNameValuePair ownerNameValuePair = new BasicNameValuePair(
                            "owner", owner);
                    BasicNameValuePair mobileNumberNameValuePair = new BasicNameValuePair(
                            "mobileNumber", mobileNumber);

                    String authString = cabid + mobileNumber + owner;
                    BasicNameValuePair authValuePair = new BasicNameValuePair(
                            "auth",
                            GlobalMethods.calculateCMCAuthString(authString));

                    nameValuePairList.add(ownerNameValuePair);
                    nameValuePairList.add(mobileNumberNameValuePair);
                    nameValuePairList.add(authValuePair);
                } else {
                    String authString = cabid;
                    BasicNameValuePair authValuePair = new BasicNameValuePair(
                            "auth",
                            GlobalMethods.calculateCMCAuthString(authString));
                    nameValuePairList.add(authValuePair);
                }

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                        nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                InputStream inputStream = httpResponse.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);

                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                String result = null;
                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    result = stringBuilder.append(bufferedStrChunk).toString();
                }

                Log.d("tripCompleted", "tripCompleted : " + result);

                if (result != null && result.length() > 0
                        && result.contains("Unauthorized Access")) {
                    Log.e("CheckPoolFragmentActivity",
                            "tripCompleted Unauthorized Access");
                    exceptioncheck = true;
                    // Toast.makeText(CheckPoolFragmentActivity.this,
                    // getResources().getString(R.string.exceptionstring),
                    // Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


    private void showStartEndLocDialog(String heading,String mname, final String mnum, String mlocadd, String mimgname){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_start_end);
        TextView tvHeading = (TextView) dialog.findViewById(R.id.tvHeading);
        tvHeading.setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this,AppConstants.HELVITICA));
        tvHeading.setText(heading);
        TextView memname = (TextView) dialog.findViewById(R.id.tvName);
        memname.setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this,AppConstants.HELVITICA));
        memname.setText(mname.toUpperCase());
        TextView memAddress = (TextView) dialog.findViewById(R.id.tvLocation);
        memAddress.setTypeface(FontTypeface.getTypeface(XCheckPoolFragmentActivty.this,AppConstants.HELVITICA));
        memAddress.setText(mlocadd);
        dialog.show();

    }
   


}
