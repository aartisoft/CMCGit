package com.clubmycab.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CabApplication;
import com.clubmycab.CircularImageView;
import com.clubmycab.Communicator;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.ListItem;
import com.clubmycab.model.LocationModel;
import com.clubmycab.model.LocationModelListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.AppReponse;
import com.clubmycab.utility.CheckNetworkConnection;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class XMemberRideFragmentActivity extends AppCompatActivity implements View.OnClickListener,GlobalAsyncTask.AsyncTaskResultListener {
    private static final String WALLET_NOT_LINKED = "1";
    private static final String INSUFFICIENT_BALANCE = "2";
    private static final String SUFFICIENT_BALANCE = "3";

    private GoogleAnalytics analytics;
    private Tracker tracker;
    private GoogleMap joinpoolmap;
    private RideDetailsModel rideDetailsModel;
    private String OwnerMobileNumber,comefrom;
    private SharedPreferences mPrefs;
    private String FullName,MemberNumberstr;
    private Dialog onedialog;
    private String checkpoolalreadyjoinresp;
    private ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();
    private ArrayList<String> steps = new ArrayList<String>();
    private TextView joinpoolchangelocationtext;
    private LatLng memberlocationlatlong;
    private String memberlocationaddressFrom = null, memberlocationaddressTo = null, usermemimagename = null;
    private String CompletePageResponse;
    private ArrayList<String> Summary = new ArrayList<String>();
    private ArrayList<String> startaddress = new ArrayList<String>();
    private ArrayList<String> endaddress = new ArrayList<String>();
    private ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
    private ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
    private ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
    private ArrayList<LatLng> polyLine = new ArrayList<LatLng>();

    private ArrayList<LatLng> via_waypoint = new ArrayList<LatLng>();
    private ArrayList<String> via_waypointstrarr = new ArrayList<String>();
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
    private ArrayList<String> phonenoarraynew = new ArrayList<String>();
    ArrayList<Integer> durationList = new ArrayList<Integer>();
    ArrayList<Integer> distanceList = new ArrayList<Integer>();

    private Marker mylocationmarkerFrom, mylocationmarkerTo;
    private String memberDistance = "";
    private String showmembersresp,usermemname = null,usermemnumber = null, usermemlocadd = null,usermemlocaddEnd = null, usermemloclatlongEnd = null
            , usermemloclatlong = null, usermemst = null;
    private CircularImageView memimage;
    private ImageView locationmarker;
    private Button buttonLocationMarker;
    boolean isPick = false;
    private LatLng memberlocationlatlongTo;
    private Location mycurrentlocationobject;
    private String joinpoolresponse;
    private TextView tvJoin;
    private LatLng toLatLang;
    private String droppoolresp;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private boolean isComeFromShowHistory;
    private boolean isRefreshNeeded;
    private int totalFare;
    private Dialog paymentDialog;
    private int originalDistance;
    private boolean isTokenExist;
    public  static final int SELECT_PAYMENT_REQUEST =  1001;
    private boolean isRoutePlotted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmember_ride_fragment);
        findViewById(R.id.cardBack).setOnClickListener(this);

        analytics = GoogleAnalytics.getInstance(XMemberRideFragmentActivity.this);
        tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("Join Ride Screen");
        tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Opened").setLabel("Join Ride Map Screen").build());

        mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        MemberNumberstr = mPrefs.getString("MobileNumber", "");
        mycurrentlocationobject = CabApplication.getInstance().getFirstLocation();
        initViews();
        initBottomSliderViews();

    }

    private void initBottomSliderViews() {
        slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }, 6000);
        findViewById(R.id.llCall).setOnClickListener(this);
        //((TextView)findViewById(R.id.tvStart)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvTripCompleted)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvUserName)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvDate)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvTime)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));
        if(rideDetailsModel != null){
            ((TextView)findViewById(R.id.tvUserName)).setText(rideDetailsModel.getOwnerName());
            ((TextView)findViewById(R.id.tvTime)).setText(rideDetailsModel.getTravelTime());
            String[] arr2 = rideDetailsModel.getTravelDate().toString().trim().split("/");
            int month = Integer.parseInt(arr2[1]);
            int date = Integer.parseInt(arr2[0]);
            ((TextView)findViewById(R.id.tvDate)).setText(String.format("%02d",date)+" "+getMontString(month));
            ((TextView)findViewById(R.id.tvPlaceFrom)).setText(rideDetailsModel.getFromShortName());
            ((TextView)findViewById(R.id.tvPlaceTo)).setText(rideDetailsModel.getToShortName());
            if (rideDetailsModel.getImagename() != null){
                String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                        +rideDetailsModel.getImagename().toString().trim();
                // Glide.with(XCheckPoolFragmentActivty.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivOwnerImage));
                Picasso.with(XMemberRideFragmentActivity.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into((ImageView)findViewById(R.id.ivOwnerImage));

            }
        }
        if(isComeFromShowHistory){
            findViewById(R.id.tvJoin).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvTripCompleted)).setVisibility(View.VISIBLE);
            findViewById(R.id.tvLeave).setVisibility(View.GONE);
            findViewById(R.id.llCall).setVisibility(View.GONE);

        }
        findViewById(R.id.ivOwnerImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                        +rideDetailsModel.getImagename().toString().trim();
                Intent intent = new Intent(XMemberRideFragmentActivity.this, ImageScreen.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        try{
            joinpoolmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.joinpoolmap)).getMap();
            joinpoolchangelocationtext = (TextView) findViewById(R.id.joinpoolchangelocationtext);
            joinpoolchangelocationtext.setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
            tvJoin = (TextView)findViewById(R.id.tvJoin);
            joinpoolmap.setMyLocationEnabled(true);
           // joinpoolmap.setOnMapClickListener(onMapClickListener);
            locationmarker = (ImageView) findViewById(R.id.locationmarker);
            locationmarker.setVisibility(View.GONE);
            buttonLocationMarker = (Button) findViewById(R.id.buttonLocationMarker);
            buttonLocationMarker.setVisibility(View.GONE);
            buttonLocationMarker.setOnClickListener(this);
            findViewById(R.id.tvJoin).setOnClickListener(this);
            findViewById(R.id.tvLeave).setOnClickListener(this);

            getBundleData();
            if(CheckNetworkConnection.isNetworkAvailable(XMemberRideFragmentActivity.this)){
                sendCheckPoolAreadyJoined();
            }else {
                CheckNetworkConnection.showConnectionErrorDialog(XMemberRideFragmentActivity.this);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getBundleData() {
        Intent intent = getIntent();
              if(intent != null && intent.getExtras() != null){
            Gson gson = new Gson();
            rideDetailsModel = gson.fromJson(intent.getStringExtra("RideDetailsModel"), RideDetailsModel.class);
            if(rideDetailsModel  != null){
                    OwnerMobileNumber = rideDetailsModel.getMobileNumber();
                Log.d("rideDetailsModel", "" + rideDetailsModel.toString() + " statusTrip : " + rideDetailsModel.getStatus());
            }
            if(intent.getExtras().containsKey("comefrom")){
                comefrom = intent.getStringExtra("comefrom");
                if(comefrom.equalsIgnoreCase("showhistory")){
                    isComeFromShowHistory = true;
                    return;

                }
                if (!TextUtils.isEmpty(comefrom)) {
                    if (comefrom.equalsIgnoreCase("GCM")) {
                        String nid = intent.getStringExtra("nid");
                        String params = "rnum=" + "&nid=" + nid + "&auth=" + GlobalMethods.calculateCMCAuthString(nid);
                        String endpoint = GlobalVariables.ServiceUrl + "/UpdateNotificationStatusToRead.php";
                        Log.d("MemberRideFragment", "UpdateNotificationStatusToRead endpoint : " + endpoint + " params : " + params);
                        new GlobalAsyncTask(this, endpoint, params, null, this, false, "UpdateNotificationStatusToRead", false);
                    }
                    Log.d("comefrom", "" + comefrom);
                }
            }
        }
    }

    /**
     * Network Reuqest------------------------------------->
     */
    private void sendCheckPoolAreadyJoined(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForcheckpoolalreadyjoined().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new ConnectionTaskForcheckpoolalreadyjoined().execute();
            }
        }

    }

  /*  private void sendPaymentRequest( String ownerMobileNumber){
       *//* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskPaymentRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rideDetailsModel.getCabId());
        } else {
            new ConnectionTaskPaymentRequest().execute(rideDetailsModel.getCabId());
        }*//*
        SharedPreferences sharedPreferences = getSharedPreferences("MobikwikToken", 0);
        String token = sharedPreferences.getString("token", "");
        logTransaction(String.valueOf(totalFare), "0",
                GlobalVariables.Mobikwik_MerchantName,
                GlobalVariables.Mobikwik_Mid, token,
                MemberNumberstr.substring(4),
                ownerMobileNumber.substring(4),
                rideDetailsModel.getCabId());
        sendPaymentRequest(String.valueOf(totalFare),rideDetailsModel.getCabId(),MemberNumberstr.substring(4),
                ownerMobileNumber.substring(4));
    }*/

    private class ConnectionTaskPaymentRequest extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;
        private String tripCompleteResponse;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticatePaymentRequest mAuth1 = new AuthenticatePaymentRequest();
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
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                // For affle traking view
                Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
                extraParams.put("cabid", rideDetailsModel.getCabId());
                     if(!TextUtils.isEmpty(tripCompleteResponse)){
                    JSONObject jsonObject = new JSONObject(tripCompleteResponse);
                    if(jsonObject.optString("msg").equalsIgnoreCase(AppReponse.TRIP_COMPLETED)){
                        AlertDialog alertDialog = new AlertDialog.Builder(XMemberRideFragmentActivity.this).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage(jsonObject.optString("msg"));

                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //scheduleStartTripNotification();

                                Intent mainIntent = new Intent(
                                        XMemberRideFragmentActivity.this,
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
                        ((TextView)findViewById(R.id.tvStart)).setText("FINISH");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        public class AuthenticatePaymentRequest {

            public String cid;

            public AuthenticatePaymentRequest() {

            }

            public void connection() throws Exception {

                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl
                        + "/updateCabStatus.php";
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
                String startresp = null;

                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    tripCompleteResponse = stringBuilder.append(bufferedStrChunk).toString();
                }

                Log.d("completedresp", "" + tripCompleteResponse);

                if (tripCompleteResponse != null && tripCompleteResponse.length() > 0
                        && startresp.contains("Unauthorized Access")) {
                    Log.e("CheckPoolFragmentActivity",
                            "AuthenticateConnectionTripCompleted Unauthorized Access");
                    exceptioncheck = true;
                    // Toast.makeText(CheckPoolFragmentActivity.this,
                    // getResources().getString(R.string.exceptionstring),
                    // Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    /**
     * Utility Methods--------------------------------------------------->
     */
    private GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng point) {
            // fixit
            setOnMapClick(point);
        }
    };
   /* private void showProgressBar(){
       try{
           onedialog = new ProgressDialog(XMemberRideFragmentActivity.this);
           onedialog.setMessage("Please Wait...");
           onedialog.setCancelable(false);
           onedialog.setCanceledOnTouchOutside(false);
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

    }*/
   private void showProgressBar(){
       try{
           onedialog = new Dialog(XMemberRideFragmentActivity.this);
           onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
           onedialog.setContentView(R.layout.dialog_ishare_loader);
           onedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
           onedialog.setCancelable(false);
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

    private void showStartEndLocDialog(String heading,String mname, final String mnum, String mlocadd, String mimgname){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_start_end);
        TextView tvHeading = (TextView) dialog.findViewById(R.id.tvHeading);
        tvHeading.setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        tvHeading.setText(heading);
        TextView memname = (TextView) dialog.findViewById(R.id.tvName);
        memname.setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        memname.setText(mname.toUpperCase());
        TextView memAddress = (TextView) dialog.findViewById(R.id.tvLocation);
        memAddress.setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        memAddress.setText(mlocadd);
        dialog.show();

    }

    private void showAlertDialog(String mname, final String mnum,
                                 String mlocadd, String mimgname) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.memberdeatilspopupbyuser);

        memimage = (CircularImageView) dialog.findViewById(R.id.memimage);

        String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadImageTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, url1);
        } else {
            new DownloadImageTask().execute(url1);
        }

        TextView memname = (TextView) dialog.findViewById(R.id.memname);
        memname.setText(mname.toUpperCase());
        dialog.show();

        TextView memlocationadd = (TextView) dialog
                .findViewById(R.id.memlocationadd);
        memlocationadd.setText(mlocadd);

        LinearLayout call = (LinearLayout) dialog.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Call Member").setAction("Call Member")
                        .setLabel("Call Member").build());

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mnum));
                startActivity(intent);

                dialog.dismiss();
            }
        });

        LinearLayout addtocontacts = (LinearLayout) dialog
                .findViewById(R.id.addtocontacts);

        if (phonenoarraynew.indexOf(mnum.toString().trim().substring(4)) != -1) {

            addtocontacts.setVisibility(View.GONE);
        } else {
            addtocontacts.setVisibility(View.VISIBLE);
        }

        // Boolean chk = null;
        // Log.i("number array", "" + phonenoarray.toString());
        //
        // for (int i = 0; i < phonenoarray.size(); i++) {
        //
        // if (phonenoarray.get(i).toString().trim()
        // .equalsIgnoreCase(mnum.toString().trim())) {
        // chk = true;
        // break;
        // } else {
        // chk = false;
        // }
        // }
        //
        // if (chk) {
        // addtocontacts.setVisibility(View.GONE);
        // } else {
        // addtocontacts.setVisibility(View.VISIBLE);
        // }

        addtocontacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(Intent.ACTION_INSERT,
                        ContactsContract.Contacts.CONTENT_URI);
                intent = new Intent(
                        ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri
                        .parse("tel:" + mnum));
                startActivity(intent);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showAlertDialogmylocation(String mname, final String mnum,
                                           String mlocadd, String mimgname) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mylocationpopup);

        memimage = (CircularImageView) dialog.findViewById(R.id.memimage);

        String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadImageTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, url1);
        } else {
            new DownloadImageTask().execute(url1);
        }

        TextView memname = (TextView) dialog.findViewById(R.id.memname);
        memname.setText(mname.toUpperCase());
        dialog.show();

        TextView memlocationadd = (TextView) dialog
                .findViewById(R.id.memlocationadd);
        memlocationadd.setText(mlocadd);

        dialog.show();
    }
    private void showRideCompleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                XMemberRideFragmentActivity.this);
        View builderView = (View) getLayoutInflater().inflate(
                R.layout.dialog_fare_ride_complete, null);

        builder.setView(builderView);
        final AlertDialog dialog = builder.create();

        LinearLayout linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletesettledll);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare already settled")
                        .setAction("Fare already settled")
                        .setLabel("Fare already settled").build());

                dialog.dismiss();
                // fixit
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            rideDetailsModel.getCabId());
                } else {
                    new ConnectionTaskForMarkTripCompleted()
                            .execute(rideDetailsModel.getCabId());
                }*/
            }
        });

        linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletepaidelsell);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare paid by other")
                        .setAction("Fare paid by other")
                        .setLabel("Fare paid by other").build());

                dialog.dismiss();

                Toast.makeText(
                        XMemberRideFragmentActivity.this,
                        "We will let you know when your friend shares the fare details & the amount you owe",
                        Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(XMemberRideFragmentActivity.this,
                        NewHomeScreen.class);
                mainIntent.putExtra("from", "normal");
                mainIntent.putExtra("message", "null");
                mainIntent.putExtra("CabId", "null");
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });

        linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletecalculatell);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare calculate")
                        .setAction("Fare calculate").setLabel("Fare calculate")
                        .build());

                dialog.dismiss();
                // fixit
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForShowMembersFareCalculation()
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ConnectionTaskForShowMembersFareCalculation().execute();
                }*/
            }
        });

        dialog.show();

    }

    private void showPaymentDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                XMemberRideFragmentActivity.this);
        View builderView = (View) getLayoutInflater().inflate(
                R.layout.dialog_fare_ride_complete_payment, null);

        builder.setView(builderView);
        final AlertDialog dialog = builder.create();

        LinearLayout linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletefaresettledll);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare settled in cash")
                        .setAction("Fare settled in cash")
                        .setLabel("Fare settled in cash").build());

                dialog.dismiss();
                // fixit
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            rideDetailsModel.getCabId(), OwnerMobileNumber,
                            MemberNumberstr);
                } else {
                    new ConnectionTaskForMarkTripCompleted().execute(
                            rideDetailsModel.getCabId(), OwnerMobileNumber,
                            MemberNumberstr);
                }*/
            }
        });

        linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletefarewalletll);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare settled by wallet")
                        .setAction("Fare settled by wallet")
                        .setLabel("Fare settled by wallet").build());

                dialog.dismiss();

                SharedPreferences sharedPreferences = getSharedPreferences(
                        "MobikwikToken", 0);
                String token = sharedPreferences.getString("token", "");

                if (token != null && !token.isEmpty()) {
                    // fixit
                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new ConnectionTaskForGetMyFare().executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR,
                                rideDetailsModel.getCabId(), MemberNumberstr,
                                "isWalletToWallet");
                    } else {
                        new ConnectionTaskForGetMyFare().execute(
                                rideDetailsModel.getCabId(), MemberNumberstr,
                                "isWalletToWallet");
                    }*/
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            XMemberRideFragmentActivity.this);
                    builder.setMessage("You cannot make a transfer as you do not have a wallet integrated yet, would you like to add a wallet now?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent mainIntent = new Intent(
                                            XMemberRideFragmentActivity.this,
                                            FirstLoginWalletsActivity.class);
                                    mainIntent.putExtra("from", "wallet");
                                    startActivity(mainIntent);
                                }
                            });

                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                }
                            });

                    builder.show();
                }

            }
        });

        linearLayout = (LinearLayout) builderView
                .findViewById(R.id.ridecompletefareclubcreditsll);
        linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Fare settled in Credits")
                        .setAction("Fare settled in Credits")
                        .setLabel("Fare settled in Credits").build());

                dialog.dismiss();

                userData(MemberNumberstr);
            }
        });

        dialog.show();

    }


    private void userData(String mobileNumber) {

        String endpoint = GlobalVariables.ServiceUrl + "/userData.php";
        String authString = mobileNumber;
        String params = "mobileNumber=" + mobileNumber + "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);
        Log.d("XMemberRideFragmentActivity", "userData endpoint : " + endpoint
                + " params : " + params);
        new GlobalAsyncTask(XMemberRideFragmentActivity.this, endpoint, params,
                null, XMemberRideFragmentActivity.this, true, "userData", false);
    }

    Handler mapHandler = new Handler();

    // fixit
    private void setOnMapClick(LatLng point) {

        joinpoolmap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 18));
        joinpoolmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(final CameraPosition cameraPosition) {
        if(mapHandler != null){
            mapHandler.removeCallbacksAndMessages(null);
        }
                mapHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LatLng mapcenter = cameraPosition.target;

                        if (!isPick) {
                            memberlocationlatlong = mapcenter;
                           /* memberlocationaddressFrom = MapUtilityMethods.getAddress(
                                    XMemberRideFragmentActivity.this,
                                    mapcenter.latitude, mapcenter.longitude);*/
                            locationHandler(memberlocationlatlong);

                          /*  Log.d("memberlocationlatlong", "" + memberlocationlatlong);
                            Log.d("memberlocationaddress", ""
                                    + memberlocationaddressFrom);*/

                           /* joinpoolchangelocationtext
                                    .setText(memberlocationaddressFrom);*/
                        } else {
                            memberlocationlatlongTo = mapcenter;
                            locationHandler(memberlocationlatlongTo);

                          /*  memberlocationaddressTo = MapUtilityMethods.getAddress(
                                    XMemberRideFragmentActivity.this,
                                    mapcenter.latitude, mapcenter.longitude);*/

                          /*  Log.d("memberlocationlatlongTo", ""
                                    + memberlocationlatlongTo);
                            Log.d("memberlocationaddressTo", ""
                                    + memberlocationaddressTo);*/

/*
                            joinpoolchangelocationtext.setText(memberlocationaddressTo);
*/
                        }
                    }
                },100);


            }
        });
        if (TextUtils.isEmpty(memberlocationaddressFrom)) {

            findViewById(R.id.flLocationName).setVisibility(View.VISIBLE);
            locationmarker.setVisibility(View.VISIBLE);
            buttonLocationMarker.setVisibility(View.VISIBLE);
        }

        if (!isPick) {
            memberlocationlatlong = point;
            memberlocationaddressFrom = MapUtilityMethods.getAddress(
                    XMemberRideFragmentActivity.this, point.latitude,
                    point.longitude);
            Log.d("memberlocationlatlong", "" + memberlocationlatlong);
            Log.d("memberlocationaddress", "" + memberlocationaddressFrom);
            joinpoolchangelocationtext.setText(memberlocationaddressFrom);

        } else {
            memberlocationlatlongTo = point;
            memberlocationaddressTo = MapUtilityMethods.getAddress(
                    XMemberRideFragmentActivity.this, point.latitude,
                    point.longitude);
            joinpoolchangelocationtext.setText(memberlocationaddressTo);

            Log.d("memberlocationlatlongTo", "" + memberlocationlatlongTo);
            Log.d("memberlocationaddressTo", "" + memberlocationaddressTo);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llCall:
               if(rideDetailsModel != null){
                   if(rideDetailsModel.getMobileNumber() != null){
                       Intent intent = new Intent(Intent.ACTION_DIAL);
                       intent.setData(Uri.parse("tel:" + rideDetailsModel.getMobileNumber().substring(4).trim()));
                       startActivity(intent);
                   }
               }
                break;
            case R.id.tvJoin:
            if(!isRoutePlotted){// This condtion is needed if route is not plotted due to network error and user pressed confirm button
                if(CheckNetworkConnection.isNetworkAvailable(XMemberRideFragmentActivity.this)){
                    sendCheckPoolAreadyJoined();
                }else {
                    CheckNetworkConnection.showConnectionErrorDialog(XMemberRideFragmentActivity.this);

                }
                return;
            }
              if(tvJoin.getText().toString().equalsIgnoreCase("CONFIRM")){
                  //checkTokenExist();
                  //checkBalanceNew();
                  joinPoolClicked();
                  tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Click").setLabel("Confirm Button Click").build());

              }else if(tvJoin.getText().toString().equalsIgnoreCase("PAY")){
                  showPaymentProgressDialog();
                  tracker.send(new HitBuilders.EventBuilder().setCategory("Pay").setAction("Pay Clicked").setLabel("Pay").build());

              }

                break;
            case R.id.buttonLocationMarker:

              /*  if(!isWalletLinked()){
                    showWallletDialog();
                    return;
                }
                checkUserBalance();*/

                joinPoolClicked();
                break;

            case R.id.tvLeave:
                tracker.send(new HitBuilders.EventBuilder().setCategory("Leave ride").setAction("Leave ride button pressed").setLabel("Offer ride info").build());

                leaveRide();
                break;
            case R.id.cardBack:
                finish();
                break;
        }
    }


    /**
     * Network Request----------------------------------------------------------------------------------->
     */
    private class ConnectionTaskForcheckpoolalreadyjoined extends
            AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectioncheckpoolalreadyjoined mAuth1 = new AuthenticateConnectioncheckpoolalreadyjoined();
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
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   hideProgressBar();
                   return;
               }

               if (checkpoolalreadyjoinresp != null
                       && checkpoolalreadyjoinresp.length() > 0
                       && checkpoolalreadyjoinresp.contains("Unauthorized Access")) {
                   Log.e("XMemberRideFragmentActivity",
                           "checkpoolalreadyjoinresp Unauthorized Access");
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   hideProgressBar();
                   return;
               }

               if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

                   if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
                       // fixit
                   /* beforejoinpoolll.setVisibility(View.VISIBLE);
                    afterjoinpoolll.setVisibility(View.GONE);*/
                       findViewById(R.id.tvLeave).setVisibility(View.GONE);
                       findViewById(R.id.llCall).setVisibility(View.GONE);
                       tvJoin.setText("CONFIRM");

                   } else {
                       // fixit
                   /* beforejoinpoolll.setVisibility(View.GONE);
                    afterjoinpoolll.setVisibility(View.GONE);*/
                   }

               } else {

                   try{
                       JSONArray jsonArray = new JSONArray(checkpoolalreadyjoinresp);
                       String hasBorded = jsonArray.getJSONObject(0).optString("hasBoarded");
                       memberlocationaddressFrom = jsonArray.getJSONObject(0).optString("MemberLocationAddress");
                       memberlocationaddressTo = jsonArray.getJSONObject(0).optString("MemberEndLocationAddress");
                       memberDistance = jsonArray.getJSONObject(0).optString("distance");
                       if(hasBorded.equalsIgnoreCase("0")){
                           if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
                               findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                               findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                               tvJoin.setText("PAY");
                           } else {
                               findViewById(R.id.tvLeave).setVisibility(View.GONE);
                               findViewById(R.id.llCall).setVisibility(View.GONE);
                           }
                       }else if(hasBorded.equalsIgnoreCase("1")){
                           findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                           findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                           tvJoin.setVisibility(View.GONE);
                           findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                           ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                       }else if(hasBorded.equalsIgnoreCase("2")){
                           findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                           findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                           tvJoin.setVisibility(View.GONE);
                           findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                           ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                       }

                   }catch (Exception e){
                       e.printStackTrace();
                   }
               }

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                   // fixit
                   new ConnectionTaskForDirections().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
               } else {
                   new ConnectionTaskForDirections().execute();
               }
           }catch (Exception e){
               e.printStackTrace();
           }

        }
    }

    public class AuthenticateConnectioncheckpoolalreadyjoined {

        public AuthenticateConnectioncheckpoolalreadyjoined() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/checkpoolalreadyjoined.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                    "CabId", rideDetailsModel.getCabId());
            BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
                    "MemberNumber", MemberNumberstr);

            String authString = rideDetailsModel.getCabId() + MemberNumberstr;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(MemberNumberBasicNameValuePair);
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
                checkpoolalreadyjoinresp = stringBuilder.append(
                        bufferedStrChunk).toString();
            }

            Log.d("checkpoolalreadyjoinresp", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForDirections extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
           // showProgressBar();
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
            //hideProgressBar();
            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    hideProgressBar();
                    return;
                }
                polyLine.clear();
                for (int i = 0; i < steps.size(); i++) {

                    ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

                    JSONArray subArray123;
                    try {
                        subArray123 = new JSONArray(steps.get(i));
                        for (int i111 = 0; i111 < subArray123.length(); i111++) {

                            String locationstr = subArray123.getJSONObject(i111).getString("start_location").toString();

                            JSONObject jsonObject11 = new JSONObject(locationstr);
                            double lat1 = Double.parseDouble(jsonObject11
                                    .getString("lat"));
                            double lng1 = Double.parseDouble(jsonObject11
                                    .getString("lng"));

                            listGeopoints.add(new LatLng(lat1, lng1));

                            // /
                            String locationstr1 = subArray123.getJSONObject(i111).getString("polyline").toString();

                            JSONObject jsonObject111 = new JSONObject(locationstr1);
                            String points = jsonObject111.getString("points");
                            ArrayList<LatLng> arr = decodePoly(points);
                            for (int j = 0; j < arr.size(); j++) {
                                listGeopoints.add(new LatLng(arr.get(j).latitude,
                                        arr.get(j).longitude));
                            }
                            // /
                            String locationstr11 = subArray123.getJSONObject(i111).getString("end_location").toString();

                            JSONObject jsonObject1111 = new JSONObject(locationstr11);
                            double lat11 = Double.parseDouble(jsonObject1111.getString("lat"));
                            double lng11 = Double.parseDouble(jsonObject1111.getString("lng"));

                            listGeopoints.add(new LatLng(lat11, lng11));

                            Random rnd = new Random();

                            PolylineOptions rectLine = new PolylineOptions().width(
                                    8).color(getResources().getColor(R.color.color_app_blue));
                            rectLine.addAll(listGeopoints);
                            polyLine.addAll(listGeopoints);
                            rectlinesarr.add(rectLine);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                joinpoolmap.setMyLocationEnabled(true);

                joinpoolchangelocationtext = (TextView) findViewById(R.id.joinpoolchangelocationtext);
                joinpoolchangelocationtext.setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this, AppConstants.HELVITICA));



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForShowMembersOnMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ConnectionTaskForShowMembersOnMap().execute();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            // fixit
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForOwnerLocation().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        rideDetailsModel.getCabId());
            } else {
                new ConnectionTaskForOwnerLocation().execute(rideDetailsModel
                        .getCabId());
            }*/

        }

    }

    public class AuthenticateConnectionGetDirection {

        public AuthenticateConnectionGetDirection() {

        }

        public void connection() throws Exception {

            String source = rideDetailsModel.getFromLocation().replaceAll(" ",
                    "%20");
            String dest = rideDetailsModel.getToLocation().replaceAll(" ",
                    "%20");
            String fromlatLong = rideDetailsModel.getsLatLon();
            String tolatLong = rideDetailsModel.geteLatLon();


            // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

            String url = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin="
                    + fromlatLong
                    + "&destination="
                    + tolatLong
                    + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                    + GlobalVariables.GoogleMapsAPIKey;

            Log.d("url", "" + url);

            CompletePageResponse = new Communicator().executeHttpGet(url);

            CompletePageResponse = CompletePageResponse
                    .replaceAll("\\\\/", "/");

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

          //  for (int i = 0; i < subArray.length(); i++) {

                Summary.add(subArray.getJSONObject(index).getString("summary")
                        .toString());

                String name1 = subArray.getJSONObject(index).getString("legs")
                        .toString();

                JSONArray subArray1 = new JSONArray(name1);

                for (int i1 = 0; i1 < subArray1.length(); i1++) {
                    originalDistance = Integer.parseInt(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value").toString());

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

                    if (i1 == 0) {
                        FareLocationList.add(new LatLng(lat, lng));
                        FareLocationList.add(new LatLng(lat4, lng4));

                    } else
                        FareLocationList.add(new LatLng(lat4, lng4));

                    // ////////////

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


            // /////
            Log.d("Summary", "" + Summary);
            Log.d("startaddress", "" + startaddress);
            Log.d("endaddress", "" + endaddress);
            Log.d("startaddlatlng", "" + startaddlatlng);
            Log.d("endaddlatlng", "" + endaddlatlng);
            Log.d("via_waypoint", "" + via_waypoint);

            for (int i = 0; i < via_waypoint.size(); i++) {
                String asd = MapUtilityMethods.getAddress(
                        XMemberRideFragmentActivity.this,
                        via_waypoint.get(i).latitude,
                        via_waypoint.get(i).longitude);
                via_waypointstrarr.add(asd);
            }
            Log.d("via_waypointstrarr", "" + via_waypointstrarr);
        }
    }

    private class ConnectionTaskForShowMembersOnMap extends
            AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;
        @Override
        protected void onPreExecute() {
            //showProgressBar();
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

            try{
                hideProgressBar();
                if(slidingUpPanelLayout != null){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                }
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            "Rote not Plotted",
                            Toast.LENGTH_LONG).show();
                   // hideProgressBar();

                    return;
                }

                if (showmembersresp != null && showmembersresp.length() > 0
                        && showmembersresp.contains("Unauthorized Access")) {
                    Log.e("XMemberRideFragmentActivity",
                            "showmembersresp Unauthorized Access");
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                FaredistanceList.clear();
                FareLocationList.clear();
                FareMemberDropLocaton.clear();
                FareMemberPickLocaton.clear();
                FareMobNoList.clear();

                if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

                } else {

                    // mycalculatorbtn.setVisibility(View.VISIBLE);

                    try {

                        JSONArray subArray = new JSONArray(checkpoolalreadyjoinresp);
                        for (int i = 0; i < subArray.length(); i++) {
                            try {
                                usermemname = subArray.getJSONObject(i).getString("MemberName").toString();
                                usermemnumber = subArray.getJSONObject(i).getString("MemberNumber").toString();
                                usermemlocadd = subArray.getJSONObject(i).getString("MemberLocationAddress").toString();
                                usermemloclatlong = subArray.getJSONObject(i).getString("MemberLocationlatlong").toString();
                                usermemlocaddEnd = subArray.getJSONObject(i).getString("MemberEndLocationAddress").toString();
                                usermemloclatlongEnd = subArray.getJSONObject(i).getString("MemberEndLocationlatlong").toString();
                                usermemimagename = subArray.getJSONObject(i).getString("MemberImageName").toString();
                                usermemst = subArray.getJSONObject(i).getString("Status").toString();

                                // Added Owner no and start end location for fare

                                FareMobNoList.add(subArray.getJSONObject(i).getString("OwnerNumber").toString());

                            /*Address locationAddressFrom = null, locationAddressTo = null;

                            String fromAdd = rideDetailsModel.getFromLocation();
                            String toAdd = rideDetailsModel.getToLocation();
                            Geocoder fcoder = new Geocoder(
                                    XMemberRideFragmentActivity.this);
                            try {
                                ArrayList<Address> adresses = (ArrayList<Address>) fcoder
                                        .getFromLocationName(fromAdd, 50);

                                for (Address add : adresses) {
                                    locationAddressFrom = add;
                                }

                                adresses = (ArrayList<Address>) fcoder
                                        .getFromLocationName(toAdd, 50);
                                for (Address add : adresses) {
                                    locationAddressTo = add;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            String src = locationAddressFrom.getLatitude()
                                    + "," + locationAddressFrom.getLongitude();
                            String des = locationAddressTo.getLatitude() + ","
                                    + locationAddressTo.getLongitude();
*/
                                String fromL[] = rideDetailsModel.getsLatLon().split(",");
                                String endL[] = rideDetailsModel.geteLatLon().split(",");

                                LatLng llFrom = new LatLng(Double.parseDouble(fromL[0]),
                                        Double.parseDouble(fromL[1]));
                                LatLng llTo = new LatLng(Double.parseDouble(endL[0]), Double.parseDouble(endL[1]));

                                FareMemberPickLocaton.add(llFrom);
                                FareMemberDropLocaton.add(llTo);

                                FareMobNoList.add(usermemnumber);

                                String arr[] = usermemloclatlong.split(",");

                                LatLng l = new LatLng(Double.parseDouble(arr[0]),
                                        Double.parseDouble(arr[1]));
                                FareMemberPickLocaton.add(l);
                                String arr1[] = usermemloclatlongEnd.split(",");

                                LatLng l1 = new LatLng(Double.parseDouble(arr1[0]),
                                        Double.parseDouble(arr1[1]));
                                FareMemberDropLocaton.add(l1);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String[] latlong = usermemloclatlong.split(",");
                    LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
                            Double.parseDouble(latlong[1]));

                    mylocationmarkerFrom = joinpoolmap
                            .addMarker(new MarkerOptions()
                                    .position(lt)
                                    .snippet("Pick")
                                    .title(usermemlocadd)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    mylocationmarkerTo = joinpoolmap.addMarker(new MarkerOptions().position(lt).snippet("Drop")
                            .title(usermemlocaddEnd).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    memberlocationlatlong = lt;
                    memberlocationaddressFrom = usermemlocadd;

                    locationmarker.setVisibility(View.GONE);
                    buttonLocationMarker.setVisibility(View.GONE);
                    findViewById(R.id.flLocationName).setVisibility(View.GONE);

                    joinpoolmap.setOnMapClickListener(null);
                    joinpoolmap.setOnCameraChangeListener(null);
                }

                if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

                } else {

                    ShowMemberName.clear();
                    ShowMemberNumber.clear();
                    ShowMemberLocationAddress.clear();
                    ShowMemberLocationLatLong.clear();
                    ShowMemberImageName.clear();
                    ShowMemberStatus.clear();
                    ShowMemberLocationAddressEnd.clear();
                    ShowMemberLocationLatLongEnd.clear();

                    try {
                        JSONArray subArray = new JSONArray(showmembersresp);
                        for (int i = 0; i < subArray.length(); i++) {
                            try {
                                ShowMemberName.add(subArray.getJSONObject(i)
                                        .getString("MemberName").toString());
                                ShowMemberNumber.add(subArray.getJSONObject(i)
                                        .getString("MemberNumber").toString());
                                ShowMemberLocationAddress.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberLocationAddress")
                                        .toString());
                                ShowMemberLocationAddressEnd.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberEndLocationAddress")
                                        .toString());

                                ShowMemberLocationLatLong.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberLocationlatlong")
                                        .toString());

                                ShowMemberLocationLatLongEnd.add(subArray
                                        .getJSONObject(i)
                                        .getString("MemberEndLocationlatlong")
                                        .toString());
                                ShowMemberImageName.add(subArray.getJSONObject(i)
                                        .getString("MemberImageName").toString());
                                ShowMemberStatus.add(subArray.getJSONObject(i)
                                        .getString("Status").toString());

                                // Added for fare calculation
                                FareMobNoList.add(subArray.getJSONObject(i)
                                        .getString("MemberNumber").toString());

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < ShowMemberName.size(); i++) {
                        String[] latlongStart = ShowMemberLocationLatLong.get(i)
                                .split(",");
                        String[] latlongEnd = ShowMemberLocationLatLongEnd.get(i)
                                .split(",");
                        LatLng ltStart = new LatLng(
                                Double.parseDouble(latlongStart[0]),
                                Double.parseDouble(latlongStart[1]));
                        LatLng ltEnd = new LatLng(
                                Double.parseDouble(latlongEnd[0]),
                                Double.parseDouble(latlongEnd[1]));
                        joinpoolmap
                                .addMarker(new MarkerOptions()
                                        .position(ltStart)
                                        .snippet("Pick"+" , "+ShowMemberImageName.get(i))
                                        .title(ShowMemberLocationAddress.get(i))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        if (!ShowMemberLocationAddressEnd.get(i).equalsIgnoreCase(
                                ""))
                            joinpoolmap
                                    .addMarker(new MarkerOptions()
                                            .position(ltEnd)
                                            .snippet("Drop"+" , "+ShowMemberImageName.get(i))
                                            .title(ShowMemberLocationAddressEnd
                                                    .get(i))
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }
                }
                // For single root

                // https://maps.googleapis.com/maps/api/directions/json?origin=-23.3246,-51.1489&destination=-23.2975,-51.2007&%20waypoints=optimize:true|-23.3246,-51.1489|-23.3206,-51.1459|-23.2975,-51.2007&sensor=false
                String wayPoint = "&waypoints=optimize:true";

                if (!showmembersresp.equalsIgnoreCase("No Members joined yet") || !checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

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

                        Polyline polyline = joinpoolmap.addPolyline(rectlinesarr
                                .get(i));
                        polyline.remove();

                    }
                    joinpoolmap.clear();

                    rectlinesarr.clear();

                    // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

                    if (!showmembersresp.equalsIgnoreCase("No Members joined yet")) {
                        for (int i = 0; i < ShowMemberLocationLatLong.size(); i++) {

                            String latlong[] = ShowMemberLocationLatLong.get(i)
                                    .split(",");
                            String latlong1[] = ShowMemberLocationLatLongEnd.get(i)
                                    .split(",");

                            wayPoint += "%7C" + latlong[0] + "," + latlong[1]
                                    + "%7C" + latlong1[0] + "," + latlong1[1];

                        }

                    }
                    if (!checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {
                        String latlong[] = usermemloclatlong.split(",");
                        String latlong1[] = usermemloclatlongEnd.split(",");

                        wayPoint += "%7C" + latlong[0] + "," + latlong[1] + "%7C"
                                + latlong1[0] + "," + latlong1[1];

                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new ConnectionTaskForSingleRoot().executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, wayPoint);
                    } else {
                        new ConnectionTaskForSingleRoot().execute(wayPoint);
                    }

                } else {

                    LatLngBounds.Builder bc = null;

                    for (int i = 0; i < rectlinesarr.size(); i++) {
                        joinpoolmap.addPolyline(rectlinesarr.get(i));

                        List<LatLng> points = rectlinesarr.get(i).getPoints();

                        bc = new LatLngBounds.Builder();

                        for (LatLng item : points) {
                            bc.include(item);
                        }
                    }

                    if(bc != null){
                        joinpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                bc.build(), 50));

                        joinpoolmap.addMarker(new MarkerOptions()
                                .position(startaddlatlng.get(0))
                                .title(startaddress.get(0))
                                .snippet("start")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.start)));
                        toLatLang = endaddlatlng.get(0);
                        joinpoolmap.addMarker(new MarkerOptions().position(endaddlatlng.get(0)).title(endaddress.get(0)).snippet("end").icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
                        isRoutePlotted = true;
                    }

                }

                joinpoolmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg0) {

                        if (arg0.getSnippet().equals("start")) {

                            if (rideDetailsModel.getCabStatus().toString().trim()
                                    .equalsIgnoreCase("A")) {
                           /* showAlertDialog(rideDetailsModel.getOwnerName(),
                                    rideDetailsModel.getMobileNumber(),
                                    rideDetailsModel.getFromLocation(),
                                    rideDetailsModel.getMobileNumber() + ".jpg");*/
                            /*showStartEndLocDialog("START LOCATION",rideDetailsModel.getOwnerName(),
                                    rideDetailsModel.getMobileNumber(),
                                    rideDetailsModel.getFromLocation(),
                                    rideDetailsModel.getMobileNumber() + ".jpg");*/
                                arg0.showInfoWindow();
                            }

                        } else if (arg0.getSnippet().equals("end")) {

                            if (rideDetailsModel.getCabStatus().toString().trim()
                                    .equalsIgnoreCase("A")) {
                          /*  showAlertDialog(rideDetailsModel.getOwnerName(),
                                    rideDetailsModel.getMobileNumber(),
                                    rideDetailsModel.getToLocation(),
                                    rideDetailsModel.getMobileNumber() + ".jpg");*/
                           /* showStartEndLocDialog("END LOCATION",rideDetailsModel.getOwnerName(),
                                    rideDetailsModel.getMobileNumber(),
                                    rideDetailsModel.getToLocation(),
                                    rideDetailsModel.getMobileNumber() + ".jpg");*/
                                arg0.showInfoWindow();

                            }

                        } else if (arg0.getSnippet().equals("Pick")) {

                            if (rideDetailsModel.getCabStatus().toString().trim()
                                    .equalsIgnoreCase("A")) {
/*
                            showAlertDialogmylocation(usermemname, usermemnumber, usermemlocadd, usermemimagename);
*/
                                // showStartEndLocDialog("PICKUP",usermemname, usermemnumber, usermemlocadd, usermemimagename);
                                arg0.showInfoWindow();

                            }

                        } else if (arg0.getSnippet().equals("Drop")) {

                            if (rideDetailsModel.getCabStatus().toString().trim()
                                    .equalsIgnoreCase("A")) {
                                //  showAlertDialogmylocation(usermemname, usermemnumber, usermemlocaddEnd, usermemimagename);
                                //  showStartEndLocDialog("DROP",usermemname, usermemnumber, usermemlocaddEnd, usermemimagename);
                                arg0.showInfoWindow();

                            }

                        }

                        else if (arg0.getTitle().equals("Last updated at")) {
                            // Log.d("MemberRideFragment",
                            // "setOnMarkerClickListener OwnerLocation : ");
                            arg0.showInfoWindow();
                        } else {

                            if (checkpoolalreadyjoinresp
                                    .equalsIgnoreCase("fresh pool")) {

                                if (rideDetailsModel.getCabStatus().toString()
                                        .trim().equalsIgnoreCase("A")) {
                                    Toast.makeText(
                                            XMemberRideFragmentActivity.this,
                                            "Join ride to see details of other members",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else {

                                if (rideDetailsModel.getCabStatus().toString()
                                        .trim().equalsIgnoreCase("A")) {
                             /*   final Integer index = Integer.parseInt(arg0
                                        .getSnippet());*/

                                /*showAlertDialog(ShowMemberName.get(index),
                                        ShowMemberNumber.get(index),
                                        ShowMemberLocationAddress.get(index),
                                        ShowMemberImageName.get(index));*/
                                    arg0.showInfoWindow();

                                }
                            }

                        }

                        return false;
                    }

                });

                if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {
                    // fix it
                /*joinPoolPopUp(rideDetailsModel.getFromShortName(), rideDetailsModel.getToShortName(), rideDetailsModel.getTravelDate(),
                        rideDetailsModel.getTravelTime(), rideDetailsModel.getSeat_Status(), rideDetailsModel.getOwnerName(),
                        rideDetailsModel.getImagename());*/

                    if (rideDetailsModel.getRemainingSeats().equalsIgnoreCase("0")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                XMemberRideFragmentActivity.this);
                        builder.setMessage("The ride is already full, you can join another ride or create your own.");
                        builder.setCancelable(false);
                        builder.setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        AlertDialog dialog = builder.show();
                        TextView messageText = (TextView) dialog
                                .findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.CENTER);
                        dialog.show();

                    }
                }

                // //////////////////////////
                if (rideDetailsModel.getCabStatus().equals("A")
                        && rideDetailsModel.getStatus().equals("2")) {

                    // For affle traking view
                    Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
                    extraParams.put("cabid", rideDetailsModel.getCabId());


                    if (!rideDetailsModel.getRideType().equals("1")) {
                        showRideCompleteDialog();
                    }
                } else if (rideDetailsModel.getCabStatus().equals("A")
                        && rideDetailsModel.getStatus().equals("3")) {
                    showPaymentDialog();
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
                            Log.d("XMemberRideFragmentActivity",
                                    "ExpTripDuration trip completed");
                            // fixit
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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
                hideProgressBar();
                e.printStackTrace();
            }
            // //////////////////////////
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
            BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
                    "MemberNumber", MemberNumberstr);

            String authString = rideDetailsModel.getCabId() + MemberNumberstr;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(MemberNumberBasicNameValuePair);
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

            Log.d("showmembersresp:", "" + stringBuilder.toString());
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
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // int index = 0;
                rectlinesarr.clear();
                polyLine.clear();

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
                        rnd.nextInt(256));
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

                            // Random rnd = new Random();
                            // int color = Color.argb(255, rnd.nextInt(256),
                            // rnd.nextInt(256), rnd.nextInt(256));

                            PolylineOptions rectLine = new PolylineOptions().width(
                                    8).color(getResources().getColor(R.color.color_app_blue));
                            rectLine.addAll(listGeopoints);
                            polyLine.addAll(listGeopoints);
                            rectlinesarr.add(rectLine);

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

           /* joinpoolmap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.joinpoolmap)).getMap();

            joinpoolmap.setMyLocationEnabled(true);

            joinpoolchangelocationtext = (TextView) findViewById(R.id.joinpoolchangelocationtext);
            joinpoolchangelocationtext.setTypeface(Typeface.createFromAsset(
                    getAssets(), AppConstants.HELVITICA));
*/
           /* joinpoolmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    //setOnMapClick(point);
                }
            });*/

                for (int i = 0; i < startaddlatlng.size(); i++) {

                    Log.d("duration:distance", "" + durationList.get(i) + ":"
                            + distanceList.get(i));

                    if (i == 0) {
                        joinpoolmap.addMarker(new MarkerOptions()
                                .position(startaddlatlng.get(i))
                                .title(startaddress.get(i))
                                .snippet("start")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.start)));
                        isRoutePlotted = true;
                    } else if (i == (startaddlatlng.size() - 1)) {
                        toLatLang = endaddlatlng.get(i);
                        joinpoolmap.addMarker(new MarkerOptions()
                                .position(endaddlatlng.get(i))
                                .title(endaddress.get(i))
                                .snippet("end")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.end)));
                    } else {

                        joinpoolmap
                                .addMarker(new MarkerOptions()
                                        .position(startaddlatlng.get(i))
                                        .title(startaddress.get(i))
                                        .snippet("Pick")
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        joinpoolmap
                                .addMarker(new MarkerOptions()
                                        .position(endaddlatlng.get(i))
                                        .title(endaddress.get(i))
                                        .snippet("Drop")
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    }

                }
                LatLngBounds.Builder bc = null;

                for (int i = 0; i < rectlinesarr.size(); i++) {
                    joinpoolmap.addPolyline(rectlinesarr.get(i));

                    List<LatLng> points = rectlinesarr.get(i).getPoints();

                    bc = new LatLngBounds.Builder();

                    for (LatLng item : points) {
                        bc.include(item);
                    }
                }

                try{
                    bc.include(startaddlatlng.get(0));
                    bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
                    joinpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));

                    // for(int i=0;i<FareMobNoList.size();i++){

                    Log.d("FareMobno", "" + FareMobNoList);
                    Log.d("FareDistance", "" + FaredistanceList);
                    Log.d("FareLocation", "" + FareLocationList);
                    Log.d("FarePick", "" + FareMemberPickLocaton);
                    Log.d("FareDrop", "" + FareMemberDropLocaton);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            // /}

        }

        public class AuthenticateConnectionSingleRoot {
            private String wayPointUrl;

            public AuthenticateConnectionSingleRoot() {

            }

            public void connection() throws Exception {

                String source = rideDetailsModel.getFromLocation().replaceAll(" ",
                        "%20");
                String dest = rideDetailsModel.getToLocation().replaceAll(" ",
                        "%20");
                String fromlatLong = rideDetailsModel.getsLatLon();
                String tolatLong = rideDetailsModel.geteLatLon();


                Address locationAddressFrom = null, locationAddressTo = null;

                String fromAdd = rideDetailsModel.getFromLocation();
                String toAdd = rideDetailsModel.getToLocation();
             ///   Geocoder fcoder = new Geocoder(XMemberRideFragmentActivity.this);
              /*  try {
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

                        durationList.add(jsonObjectDuraton.getInt("value"));

                        String distance = subArray1.getJSONObject(i1)
                                .getString("distance").toString();
                        JSONObject jsonObjectDistance = new JSONObject(distance);

                        distanceList.add(jsonObjectDistance.getInt("value"));
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
                            XMemberRideFragmentActivity.this,
                            via_waypoint.get(i).latitude,
                            via_waypoint.get(i).longitude);
                    via_waypointstrarr.add(asd);
                }
                Log.d("via_waypointstrarr", "" + via_waypointstrarr);
            }
        }

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try{
                hideProgressBar();
                memimage.setImageBitmap(result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getResult(String response, String uniqueID) {
        if (uniqueID.equals("userbalance")) {
            Log.d("FirstLoginWalletActivity", "userbalance response : "
                    + response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    Log.d("FirstLoginWalletActivity", "userbalance response : ");
                    String userBalance = jsonObject.optString("balanceamount");
                    int minFare = Integer.parseInt(rideDetailsModel.getPerKmCharge());
                    int distance = (int)Double.parseDouble(rideDetailsModel.getDistance().replace("km","").trim());
                    int totalFare = distance*minFare;
                    if(!TextUtils.isEmpty(userBalance)){
                        if(Integer.parseInt(userBalance)<totalFare){
                          //  CustomDialog.showDialog(XMemberRideFragmentActivity.this, getResources().getString(R.string.msg_low_balance_for_ride));
                           showLowBalanceDialog();
                        }else {
                            joinPoolClicked();
                        }
                    }
                  /*  otphardtext.setText("Your wallet balance : \u20B9"
                            + jsonObject.getString("balanceamount"));
                    tokenRegenerate();
                    Button button = (Button)findViewById(R.id.buttonTopUpWallet);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            openAppOrMSite();
                        }
                    });*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (uniqueID.equals("logTransaction")) {
            Log.d("MemberRideFragmentActivity", "logTransaction response : "
                    + response);

            if (response != null && response.length() > 0
                    && response.contains("Unauthorized Access")) {
                Log.e("MemberRideFragmentActivity",
                        "logTransaction Unauthorized Access");
                Toast.makeText(XMemberRideFragmentActivity.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.get("status").toString()
                        .equalsIgnoreCase("success")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            "MobikwikToken", 0);
                    String token = sharedPreferences.getString("token", "");

                 /*   initiatePeerTransfer(MemberNumberstr.substring(4),
                            rideDetailsModel.getMobileNumber().substring(4), String.valueOf(totalFare), "0",
                            jsonObject.get("orderId").toString(), token);
                */} else {
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            "Something went wrong, please try again",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(XMemberRideFragmentActivity.this,
                        "Something went wrong, please try again",
                        Toast.LENGTH_LONG).show();
            }
        } else if (uniqueID.equals("initiatePeerTransfer")) {
            Log.d("MemberRideFragmentActivity", "initiatePeerTransfer response : " + response);

            SharedPreferences sharedPreferences = getSharedPreferences(
                    "MobikwikToken", 0);
            String token = sharedPreferences.getString("token", "");

            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.get("status").toString().equalsIgnoreCase("success")) {
                    isRefreshNeeded = true;
                    paymentDialog.findViewById(R.id.llProgressView).setVisibility(View.GONE);
                    paymentDialog.findViewById(R.id.llPaymetResult).setVisibility(View.VISIBLE);
                    ((TextView)paymentDialog.findViewById(R.id.tvPaymentStatus)).setText(jsonObject.getString("status").toUpperCase());
                    ((TextView)paymentDialog.findViewById(R.id.tvMsg2)).setText(jsonObject.getString("message"));
                    tvJoin.setVisibility(View.GONE);
                    findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                } else {
                    isRefreshNeeded = true;
                    // Need status of user payment
                    paymentDialog.findViewById(R.id.llProgressView).setVisibility(View.GONE);
                    paymentDialog.findViewById(R.id.llPaymetResult).setVisibility(View.VISIBLE);
                    ((TextView)paymentDialog.findViewById(R.id.tvPaymentStatus)).setText("Payment Error");
                    ((TextView)paymentDialog.findViewById(R.id.tvMsg2)).setText(jsonObject.getString("message"));
                    tvJoin.setVisibility(View.GONE);
                    findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                }
                //tokenRegenerate(MemberNumberstr.substring(4), token, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uniqueID.equals("tokenregenerate")) {
            Log.d("CheckPoolFragmentActivity", "tokenregenerate response : "
                    + response);
            try {

                JSONObject jsonObject = new JSONObject(response);
                String token = jsonObject.get("token").toString();

                SharedPreferences sharedPreferences = getSharedPreferences(
                        "MobikwikToken", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (uniqueID.equals("tokenRegenerateReTransfer")) {
            Log.d("CheckPoolFragmentActivity",
                    "tokenRegenerateReTransfer response : " + response);
            try {

                JSONObject jsonObject = new JSONObject(response);
                String token = jsonObject.get("token").toString();

                SharedPreferences sharedPreferences = getSharedPreferences(
                        "MobikwikToken", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.commit();

              /*  logTransaction(String.valueOf(totalFare), "0",
                        GlobalVariables.Mobikwik_MerchantName,
                        GlobalVariables.Mobikwik_Mid, token,
                        OwnerMobileNumber.substring(4),
                        rideDetailsModel.getMobileNumber(), rideDetailsModel.getCabId());
*/            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(uniqueID.equalsIgnoreCase("checkBalance")){
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    /*String userBalance = jsonObject.optString("balance");
                    int minFare = Integer.parseInt(rideDetailsModel.getPerKmCharge());
                    int distance = (int)Double.parseDouble(rideDetailsModel.getDistance().replace("km","").trim());
                    int totalFare = distance*minFare;
                    if(!TextUtils.isEmpty(userBalance)){
                        if(Integer.parseInt(userBalance)<totalFare){
                            if(isTokenExist){
                                showLowBalanceDialog();
                            }else {
                                checkTokenExist();
                            }
                        }else {
                            joinPoolClicked();
                        }
                    }*/

                    if(jsonObject.optString("walletStatusCode").equalsIgnoreCase(WALLET_NOT_LINKED)){
                        paymentDialog.dismiss();
                        showWallletDialog();
                    }else if(jsonObject.optString("walletStatusCode").equalsIgnoreCase(INSUFFICIENT_BALANCE)){
                        paymentDialog.dismiss();
                        showLowBalanceDialog();
                    }else if(jsonObject.optString("walletStatusCode").equalsIgnoreCase(SUFFICIENT_BALANCE)){
                        sendPaymentRequest(String.valueOf(totalFare),rideDetailsModel.getCabId(),MemberNumberstr.substring(4), rideDetailsModel.getMobileNumber().substring(4));

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(uniqueID.equalsIgnoreCase("getToken")){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("success")) {
                    if(!TextUtils.isEmpty(jsonObject.getString("token"))){
                        isTokenExist = true;
                        checkBalanceNew();
                    }
                }else {
                    isTokenExist =  false;
                    showWallletDialog();
                }
                L.mesaage("");
            }catch (Exception e){
                e.printStackTrace();
            }
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

    private void joinPoolClicked(){
        isRefreshNeeded = true;
        if (locationmarker.getVisibility() == View.VISIBLE) {
            if (!isPick) {
                if (TextUtils.isEmpty(memberlocationaddressFrom)) {
                    Toast.makeText(XMemberRideFragmentActivity.this, "Please select pick location", Toast.LENGTH_LONG).show();
                    return;

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                    builder.setMessage(memberlocationaddressFrom.toUpperCase());
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    tracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Set From Location")
                                            .setAction("Set From Location")
                                            .setLabel("Set From Location").build());
                                   /* isPick = true;
                                    mylocationmarkerFrom = joinpoolmap.addMarker(new MarkerOptions().position(memberlocationlatlong).snippet("Pick")
                                                    .title(usermemlocadd).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    joinpoolmap.animateCamera(CameraUpdateFactory.newLatLngZoom(toLatLang, 16));
                                    buttonLocationMarker.setText("Select Drop Location");*/
                                    validatePickupPoint();

                                }
                            });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();
                }
            }

            else {

                if (TextUtils.isEmpty(memberlocationaddressTo)) {
                  //  Toast.makeText(XMemberRideFragmentActivity.this, "Select drop location", Toast.LENGTH_LONG).show();

                    return;
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                    builder.setMessage(memberlocationaddressTo.toUpperCase());
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mylocationmarkerTo = joinpoolmap.addMarker(new MarkerOptions().position(memberlocationlatlongTo)
                                                    .snippet("Drop").title(usermemlocadd).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    Log.d("memberlocationlatlong:::", "" + memberlocationlatlong);
                                    Log.d("memberlocationaddress:::", "" + memberlocationaddressFrom);
                                    Log.d("memberlocationlatlongTo:::", "" + memberlocationlatlongTo);
                                    Log.d("memberlocationaddressTo:::", "" + memberlocationaddressTo);

                                    // isPick=true;
                                    locationmarker.setVisibility(View.GONE);
                                    buttonLocationMarker.setVisibility(View.GONE);
                                    validateDropPoint();
                                    tracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Set To Location")
                                            .setAction("Set To Location")
                                            .setLabel("Set To Location").build());

                                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        new ConnectionTaskForJoiningapool().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    } else {
                                        new ConnectionTaskForJoiningapool().execute();
                                    }*/


                                  /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        new ConnectionTaskForJoiningapool().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    } else {
                                        new ConnectionTaskForJoiningapool().execute();
                                    }*/

                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();
                }

            }

        } else {
            Location location = mycurrentlocationobject;
            Log.d("XMemberRideFragmentActivity", "joinpoolmap location : " + location);
            if (location != null) {

                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                joinpoolmap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                setOnMapClick(point);
                buttonLocationMarker.setText("Select Pickup Location");
            } else {
                Toast.makeText(
                        XMemberRideFragmentActivity.this,
                        "Please select your location to join the ride, by clicking on map",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ConnectionTaskForJoiningapool extends
            AsyncTask<String, Void, Void> {
        boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionFetchNotification mAuth1 = new AuthenticateConnectionFetchNotification();
            try {
                mAuth1.connection();
            } catch (Exception e) {
                // TODO Auto-generated catch block
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
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }

               if (joinpoolresponse != null && joinpoolresponse.length() > 0
                       && joinpoolresponse.contains("Unauthorized Access")) {
                   Log.e("MemberRideFragmentActivity",
                           "joinpoolresponse Unauthorized Access");
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }
               if(!TextUtils.isEmpty(joinpoolresponse) && joinpoolresponse.equalsIgnoreCase("Error")){
                   AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                   builder.setTitle("Opps!");
                   builder.setMessage("This ride is no longer available");
                   builder.setCancelable(false);
                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                           Intent mainIntent = new Intent(XMemberRideFragmentActivity.this,
                                   NewHomeScreen.class);
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                   | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivityForResult(mainIntent, 500);
                           overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                       }
                   });
                   AlertDialog dialog = builder.show();
                   TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                   messageText.setGravity(Gravity.CENTER);
                   dialog.show();
                   return;
               }
               AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
               builder.setTitle("Success!");
               builder.setMessage("Enjoy your ride. Please pay when you get in the car.");
               builder.setCancelable(false);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                           new ConnectionTaskForcheckpoolalreadyjoinednew()
                                   .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                       } else {
                           new ConnectionTaskForcheckpoolalreadyjoinednew().execute();
                       }

                       tracker.send(new HitBuilders.EventBuilder()
                               .setCategory("Join Ride").setAction("Join Ride")
                               .setLabel("Join Ride").build());
                   }
               });
               AlertDialog dialog = builder.show();
               TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
               messageText.setGravity(Gravity.CENTER);
               dialog.show();
           }catch (Exception e){
               e.printStackTrace();
           }
        }

    }

    public class AuthenticateConnectionFetchNotification {

        public AuthenticateConnectionFetchNotification() {

        }

        public void connection() throws Exception {
            double lat = memberlocationlatlong.latitude;
            double longi = memberlocationlatlong.longitude;
            double latEnd = memberlocationlatlongTo.latitude;
            double longiEnd = memberlocationlatlongTo.longitude;
            String distance =  getDistance(memberlocationaddressFrom, memberlocationaddressTo).replace("km","").trim();
            String latlong = String.valueOf(lat) + "," + String.valueOf(longi);
            String latlongEnd = String.valueOf(latEnd) + "," + String.valueOf(longiEnd);
            String poolId;
            if(rideDetailsModel.getPoolId() == null){
                poolId = "";
            }else {
                poolId = rideDetailsModel.getPoolId();
            }
            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/joinpool.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair("CabId", rideDetailsModel.getCabId());
            BasicNameValuePair distanceNameValuePair = new BasicNameValuePair("distance", distance);
            BasicNameValuePair MemberEndLocationAddressBasicNameValuePair = new BasicNameValuePair("MemberEndLocationAddress", memberlocationaddressTo);
            BasicNameValuePair MemberEndLocationlatlongBasicNameValuePair = new BasicNameValuePair("MemberEndLocationlatlong", latlongEnd);
            BasicNameValuePair MemberLocationAddressBasicNameValuePair = new BasicNameValuePair("MemberLocationAddress", memberlocationaddressFrom);
            BasicNameValuePair MemberLocationlatlongBasicNameValuePair = new BasicNameValuePair("MemberLocationlatlong", latlong);
            BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair("MemberName", FullName);
            BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair("MemberNumber", MemberNumberstr);
            BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair("Message", FullName + " has joined your ride from " + rideDetailsModel.getFromShortName() + " to " + rideDetailsModel.getToShortName());
            BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair("OwnerName", rideDetailsModel.getOwnerName());
            BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair("OwnerNumber", rideDetailsModel.getMobileNumber());
            BasicNameValuePair poolIdPair = new BasicNameValuePair("PoolId",poolId);
            BasicNameValuePair rideTypePair = new BasicNameValuePair("rideType", rideDetailsModel.getRideType());
            BasicNameValuePair StatusBasicNameValuePair = new BasicNameValuePair("Status", "Nothing");


            String authString = rideDetailsModel.getCabId()
                    +distance
                    + memberlocationaddressTo + latlongEnd
                    + memberlocationaddressFrom + latlong + FullName
                    + MemberNumberstr + FullName
                    + " has joined your ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName()
                    + rideDetailsModel.getOwnerName()
                    + rideDetailsModel.getMobileNumber() +poolId+rideDetailsModel.getRideType()+ "Nothing";

            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));
            Log.d("authString",authString);
            Log.d("authValuePair",GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(distanceNameValuePair);
            nameValuePairList.add(OwnerNameBasicNameValuePair);
            nameValuePairList.add(OwnerNumberBasicNameValuePair);
            nameValuePairList.add(MemberNameBasicNameValuePair);
            nameValuePairList.add(MemberNumberBasicNameValuePair);
            nameValuePairList.add(MemberLocationAddressBasicNameValuePair);
            nameValuePairList.add(MemberLocationlatlongBasicNameValuePair);
            nameValuePairList.add(StatusBasicNameValuePair);
            nameValuePairList.add(MessageBasicNameValuePair);
            nameValuePairList.add(MemberEndLocationAddressBasicNameValuePair);
            nameValuePairList.add(MemberEndLocationlatlongBasicNameValuePair);
            nameValuePairList.add(poolIdPair);
            nameValuePairList.add(rideTypePair);

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
                joinpoolresponse = stringBuilder.append(bufferedStrChunk)
                        .toString();
            }

            Log.d("joinpoolresponse", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForcheckpoolalreadyjoinednew extends
            AsyncTask<String, Void, Void> {
        boolean exceptioncheck ;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectioncheckpoolalreadyjoinednew mAuth1 = new AuthenticateConnectioncheckpoolalreadyjoinednew();
            try {
                mAuth1.connection();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            try{
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    hideProgressBar();
                    return;
                }

                if (checkpoolalreadyjoinresp != null
                        && checkpoolalreadyjoinresp.length() > 0
                        && checkpoolalreadyjoinresp.contains("Unauthorized Access")) {
                    Log.e("MemberRideFragmentActivity",
                            "checkpoolalreadyjoinrespnew Unauthorized Access");
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    hideProgressBar();
                    return;
                }

                if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

                    if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
                        // fixit
                   /* beforejoinpoolll.setVisibility(View.VISIBLE);
                    afterjoinpoolll.setVisibility(View.GONE);*/
                        findViewById(R.id.tvLeave).setVisibility(View.GONE);
                        findViewById(R.id.llCall).setVisibility(View.GONE);
                        tvJoin.setText("CONFIRM");

                    } else {
                        // fixit
                   /* beforejoinpoolll.setVisibility(View.GONE);
                    afterjoinpoolll.setVisibility(View.GONE);*/
                    }

                } else {

                    try{
                        JSONArray jsonArray = new JSONArray(checkpoolalreadyjoinresp);
                        String hasBorded = jsonArray.getJSONObject(0).optString("hasBoarded");
                        memberlocationaddressFrom = jsonArray.getJSONObject(0).optString("MemberLocationAddress");
                        memberlocationaddressTo = jsonArray.getJSONObject(0).optString("MemberEndLocationAddress");
                        memberDistance = jsonArray.getJSONObject(0).optString("distance");
                        if(hasBorded.equalsIgnoreCase("0")){
                            if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
                                findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                                findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                                tvJoin.setText("PAY");
                            } else {
                                findViewById(R.id.tvLeave).setVisibility(View.GONE);
                                findViewById(R.id.llCall).setVisibility(View.GONE);
                            }
                        }else if(hasBorded.equalsIgnoreCase("1")){
                            findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                            findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                            tvJoin.setVisibility(View.GONE);
                            findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                        }else if(hasBorded.equalsIgnoreCase("2")){
                            findViewById(R.id.llCall).setVisibility(View.VISIBLE);
                            findViewById(R.id.tvLeave).setVisibility(View.VISIBLE);
                            tvJoin.setVisibility(View.GONE);
                            findViewById(R.id.tvTripCompleted).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.tvTripCompleted)).setText("ENJOY YOUR RIDE");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new ConnectionTaskForDirectionsnew()
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ConnectionTaskForDirectionsnew().execute();
                }
            }catch (Exception e){
                hideProgressBar();
                e.printStackTrace();
            }

        }
    }

    public class AuthenticateConnectioncheckpoolalreadyjoinednew {

        public AuthenticateConnectioncheckpoolalreadyjoinednew() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/checkpoolalreadyjoined.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                    "CabId", rideDetailsModel.getCabId());
            BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
                    "MemberNumber", MemberNumberstr);

            String authString = rideDetailsModel.getCabId() + MemberNumberstr;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(MemberNumberBasicNameValuePair);
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
                checkpoolalreadyjoinresp = stringBuilder.append(
                        bufferedStrChunk).toString();
            }

            Log.d("checkpoolalreadyjoinresp:", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForDirectionsnew extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            //showProgressBar();
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionGetDirectionnew mAuth1 = new AuthenticateConnectionGetDirectionnew();
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
           // hideProgressBar();
           try{
               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   hideProgressBar();

                   return;
               }

               joinpoolmap.clear();
               polyLine.clear();

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
                           //
                           rectLine.addAll(listGeopoints);
                           polyLine.addAll(listGeopoints);
                           rectlinesarr.add(rectLine);

                       }
                   } catch (JSONException e) {

                       e.printStackTrace();
                   }
                   double lat = memberlocationlatlong.latitude;
                   double longi = memberlocationlatlong.longitude;
                   LatLng ll = new LatLng(lat, longi);

                   // ::::: Check joinride location is within 5 km of path or not
                   // :::://

                   // boolean isOnPath = PolyUtil.isLocationOnPath(ll,
                   // listGeopoints,
                   // false, maxDistace);
                   // Log.d("isLocationonpath", "" + isOnPath);

               }

               LatLngBounds.Builder bc = null;

               for (int i = 0; i < rectlinesarr.size(); i++) {
                   joinpoolmap.addPolyline(rectlinesarr.get(i));

                   List<LatLng> points = rectlinesarr.get(i).getPoints();

                   bc = new LatLngBounds.Builder();

                   for (LatLng item : points) {
                       bc.include(item);
                   }
               }

               // joinpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
               // bc.build(), 50));
               //
               // joinpoolmap.addMarker(new MarkerOptions()
               // .position(startaddlatlng.get(0))
               // .title(startaddress.get(0))
               // .snippet("start")
               // .icon(BitmapDescriptorFactory
               // .fromResource(R.drawable.start)));
               //
               // joinpoolmap
               // .addMarker(new MarkerOptions()
               // .position(endaddlatlng.get(0))
               // .title(endaddress.get(0))
               // .snippet("end")
               // .icon(BitmapDescriptorFactory
               // .fromResource(R.drawable.end)));

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                   new ConnectionTaskForShowMembersOnMap()
                           .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
               } else {
                   new ConnectionTaskForShowMembersOnMap().execute();
               }
           }catch (Exception e){
               hideProgressBar();
               e.printStackTrace();
           }

        }

    }

    public class AuthenticateConnectionGetDirectionnew {

        public AuthenticateConnectionGetDirectionnew() {

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
            FareLocationList.clear();

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

          //  for (int i = 0; i < subArray.length(); i++) {

                Summary.add(subArray.getJSONObject(index).getString("summary").toString());
                String name1 = subArray.getJSONObject(index).getString("legs").toString();
                JSONArray subArray1 = new JSONArray(name1);
                for (int i1 = 0; i1 < subArray1.length(); i1++) {

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

                    if (i1 == 0) {
                        FareLocationList.add(new LatLng(lat, lng));
                        FareLocationList.add(new LatLng(lat4, lng4));

                    } else
                        FareLocationList.add(new LatLng(lat4, lng4));

                    // ////////////

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
           // }

            // /////
            Log.d("Summary", "" + Summary);
            Log.d("startaddress", "" + startaddress);
            Log.d("endaddress", "" + endaddress);
            Log.d("startaddlatlng", "" + startaddlatlng);
            Log.d("endaddlatlng", "" + endaddlatlng);
            Log.d("via_waypoint", "" + via_waypoint);

            for (int i = 0; i < via_waypoint.size(); i++) {
                String asd = MapUtilityMethods.getAddress(
                        XMemberRideFragmentActivity.this,
                        via_waypoint.get(i).latitude,
                        via_waypoint.get(i).longitude);
                via_waypointstrarr.add(asd);
            }
            Log.d("via_waypointstrarr", "" + via_waypointstrarr);
        }
    }

    public void leaveRide(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                XMemberRideFragmentActivity.this);
        builder.setMessage("Are you sure you want to leave this ride?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Leave Ride")
                                .setAction("Leave Ride")
                                .setLabel("Leave Ride").build());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new ConnectionTaskFordroppool()
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new ConnectionTaskFordroppool().execute();
                        }

                    }
                });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView) dialog
                .findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    private class ConnectionTaskFordroppool extends
            AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(
                XMemberRideFragmentActivity.this);
        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectiondroppool mAuth1 = new AuthenticateConnectiondroppool();
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
              if (dialog != null && dialog.isShowing()) {
                  dialog.dismiss();
              }

              if (exceptioncheck) {
                  exceptioncheck = false;
                  Toast.makeText(XMemberRideFragmentActivity.this,
                          getResources().getString(R.string.exceptionstring),
                          Toast.LENGTH_LONG).show();
                  return;
              }

              if (droppoolresp != null && droppoolresp.length() > 0
                      && droppoolresp.contains("Unauthorized Access")) {
                  Log.e("MemberRideFragmentActivity",
                          "droppoolresp Unauthorized Access");
                  Toast.makeText(XMemberRideFragmentActivity.this,
                          getResources().getString(R.string.exceptionstring),
                          Toast.LENGTH_LONG).show();
                  return;
              }

              Intent mainIntent = new Intent(XMemberRideFragmentActivity.this,
                      NewHomeScreen.class);
              mainIntent.putExtra("from", "normal");
              mainIntent.putExtra("message", "null");
              mainIntent.putExtra("CabId", "null");
              mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                      | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(mainIntent);
          }catch (Exception e){
              e.printStackTrace();
          }
        }

    }

    public class AuthenticateConnectiondroppool {

        public AuthenticateConnectiondroppool() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/dropapool.php";
            HttpPost httpPost = new HttpPost(url_select);

            BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                    "CabId", rideDetailsModel.getCabId());
            BasicNameValuePair SentMemberNameBasicNameValuePair = new BasicNameValuePair(
                    "SentMemberName", FullName);
            BasicNameValuePair SentMemberNumberBasicNameValuePair = new BasicNameValuePair(
                    "SentMemberNumber", MemberNumberstr);
            BasicNameValuePair ReceiveMemberNameBasicNameValuePair = new BasicNameValuePair(
                    "ReceiveMemberName", rideDetailsModel.getOwnerName());
            BasicNameValuePair ReceiveMemberNumberBasicNameValuePair = new BasicNameValuePair(
                    "ReceiveMemberNumber", rideDetailsModel.getMobileNumber());
            BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
                    "Message", FullName + " left your ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName());

            String authString = rideDetailsModel.getCabId() + FullName
                    + " left your ride from "
                    + rideDetailsModel.getFromShortName() + " to "
                    + rideDetailsModel.getToShortName()
                    + rideDetailsModel.getOwnerName()
                    + rideDetailsModel.getMobileNumber() + FullName
                    + MemberNumberstr;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(CabIdBasicNameValuePair);
            nameValuePairList.add(SentMemberNameBasicNameValuePair);
            nameValuePairList.add(SentMemberNumberBasicNameValuePair);
            nameValuePairList.add(ReceiveMemberNameBasicNameValuePair);
            nameValuePairList.add(ReceiveMemberNumberBasicNameValuePair);
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
                droppoolresp = stringBuilder.append(bufferedStrChunk)
                        .toString();
            }

            Log.d("droppoolresp", "" + stringBuilder.toString());
        }
    }

    private void checkUserBalance() {
        String msgcode = "501";
        String mobilenumber = MemberNumberstr.substring(4);
        SharedPreferences sharedPreferences = getSharedPreferences(
                "MobikwikToken", 0);
        String token = sharedPreferences.getString("token", "");

        String checksumstring = GlobalMethods.calculateCheckSumForService("'"
                + mobilenumber + "''" + GlobalVariables.Mobikwik_MerchantName
                + "''" + GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
                + token + "'", GlobalVariables.Mobikwik_14SecretKey);
        String endpoint = GlobalVariables.Mobikwik_ServerURL + "/userbalance";
        String params = "cell=" + mobilenumber + "&token=" + token
                + "&msgcode=" + msgcode + "&mid="
                + GlobalVariables.Mobikwik_Mid + "&merchantname="
                + GlobalVariables.Mobikwik_MerchantName + "&checksum="
                + checksumstring;
        Log.d("checkUserBalance", "checkUserBalance endpoint : " + endpoint
                + " params : " + params);
        new GlobalAsyncTask(XMemberRideFragmentActivity.this, endpoint, params,
                null, XMemberRideFragmentActivity.this, true, "userbalance", true);
    }

    private boolean isWalletLinked(){
        SharedPreferences sharedPreferences = getSharedPreferences("MobikwikToken", 0);
        String token = sharedPreferences.getString("token", "");
        if(!TextUtils.isEmpty(token)){
            return true;
        }

        return false;
    }

    private void showWallletDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(
                XMemberRideFragmentActivity.this).create();
        alertDialog.setMessage("We need to link your wallet to process payments.");

        alertDialog.setButton("LINK NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(SPreference.getRideTakerWalletType(XMemberRideFragmentActivity.this) == AppConstants.PAYMENT_TYPE_MOBIKWIK) {

                    Intent intent = new Intent(XMemberRideFragmentActivity.this, FirstLoginWalletsActivity.class);
                    intent.putExtra(AppConstants.ACTIVITYNAME, AppConstants.XMEMBERRIDEFRAGACITIVTY);
                    startActivity(intent);
                }
            }
        });


        // Showing Alert Message
        alertDialog.show();
    }

    private void showLowBalanceDialog(){
        final Dialog dialog = new Dialog(XMemberRideFragmentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_low_balance);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ((TextView)dialog.findViewById(R.id.tvMessage)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)dialog.findViewById(R.id.tvRechargeNow)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)dialog.findViewById(R.id.tvCancel)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));

        dialog.findViewById(R.id.tvRechargeNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               // openAppOrMSite();
                String mSite = "https://m.mobikwik.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mSite));
                startActivity(i);
            }
        });
        dialog.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void openAppOrMSite() {

        String packageName = "com.mobikwik_new";
        String mSite = "https://m.mobikwik.com";

        if (checkIfAppInstalled(packageName)) {

            Intent launchIntent = getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);

        } else {

            Intent intent = new Intent(this, MobileSiteActivity.class);
            intent.putExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL, mSite);
            startActivity(intent);
        }
    }
    private boolean checkIfAppInstalled(String packageName) {

        PackageManager packageManager = getPackageManager();

        try {
            packageManager.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isRefreshNeeded){
            Intent mainIntent = new Intent(XMemberRideFragmentActivity.this,
                    NewHomeScreen.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(mainIntent, 500);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //finish();
        }
    }

    private void showPaymentProgressDialog(){
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
      //  int distance = (int)Double.parseDouble(rideDetailsModel.getDistance().replace("km","").trim());
      //  int distance = (int)Double.parseDouble(getDistance(memberlocationaddressFrom,memberlocationaddressTo).replace("km","").trim());
        if(TextUtils.isEmpty(memberDistance)){
            Toast.makeText(XMemberRideFragmentActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
            return;
        }
        double minFare = Double.parseDouble(rideDetailsModel.getPerKmCharge());
        double distance = Double.parseDouble(memberDistance.trim());
        totalFare = (int)Math.round(distance*minFare);
        paymentDialog = new Dialog(XMemberRideFragmentActivity.this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.dialog_payment_progress);
        paymentDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ((TextView)paymentDialog.findViewById(R.id.tvFareInfo)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvMsg)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.FONT_AWESOME));
        ((TextView)paymentDialog.findViewById(R.id.tvPay)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvCancel)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvPleaseWait)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvPaymentStatus)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvMsg2)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvOK)).setTypeface(FontTypeface.getTypeface(XMemberRideFragmentActivity.this,AppConstants.HELVITICA));
        ((TextView)paymentDialog.findViewById(R.id.tvMsg)).setText("You will be charged" +" "+getResources().getString(R.string.rupee)+totalFare+" "+"for this ride.");

      //  TextView tvPayTypeText = (TextView)paymentDialog.findViewById(R.id.tvCash);
        ImageView ivPayImage = (ImageView)paymentDialog.findViewById(R.id.pTytpeImage);

        if(SPreference.getRideTakerWalletType(XMemberRideFragmentActivity.this) == AppConstants.NULL){
            SPreference.setRideTakerWalletType(XMemberRideFragmentActivity.this,AppConstants.PAYMENT_TYPE_CASH);
            SPreference.getPref(XMemberRideFragmentActivity.this).edit().putInt(SPreference.SELECTED_PAY_TYPE,AppConstants.PAYMENT_TYPE_CASH).commit();
            ivPayImage.setImageResource(R.drawable.popup_cash);
          //  tvPayTypeText.setText("CASH");
        }else {
            if(SPreference.getRideTakerWalletType(XMemberRideFragmentActivity.this) == AppConstants.PAYMENT_TYPE_CASH){
                ivPayImage.setImageResource(R.drawable.popup_cash);
             //   tvPayTypeText.setText("CASH");
            }else if(SPreference.getRideTakerWalletType(XMemberRideFragmentActivity.this) == AppConstants.PAYMENT_TYPE_MOBIKWIK){
                ivPayImage.setImageResource(R.drawable.popup_01);
             //   tvPayTypeText.setText("MOBIKWIK");
            }
        }


        paymentDialog.findViewById(R.id.tvPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPaymentRequest(String.valueOf(totalFare),rideDetailsModel.getCabId(),MemberNumberstr.substring(4), rideDetailsModel.getMobileNumber().substring(4));
                paymentDialog.findViewById(R.id.llFareInfo).setVisibility(View.GONE);
                paymentDialog.findViewById(R.id.llProgressView).setVisibility(View.VISIBLE);
                if(SPreference.getRideTakerWalletType(XMemberRideFragmentActivity.this) != AppConstants.PAYMENT_TYPE_CASH){
                    checkBalanceNew();
                }else {
                    sendCashPayRequest(String.valueOf(totalFare),rideDetailsModel.getCabId(),MemberNumberstr.substring(4), rideDetailsModel.getMobileNumber().substring(4));
                }
                tracker.send(new HitBuilders.EventBuilder().setCategory("Payment done").setAction("OK button pressed on Pay popup").setLabel("Choose payment option").build());


            }
        });
        paymentDialog.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.send(new HitBuilders.EventBuilder().setCategory("Payment cancelled").setAction("Cancel button pressed on Pay popup").setLabel("Cancel button pressed on Pay popup").build());

                paymentDialog.dismiss();
            }
        });
        paymentDialog.show();
        paymentDialog.findViewById(R.id.tvOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentDialog.dismiss();
            }
        });
       paymentDialog.findViewById(R.id.llChooseWallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.send(new HitBuilders.EventBuilder().setCategory("Choose payment option").setAction("Choose payment option Clicked").setLabel("Choose payment option").build());
                Intent intent = new Intent(XMemberRideFragmentActivity.this, ChoosePaymentTypeScreen.class);
                intent.putExtra("fromrideoffer",false);
                startActivityForResult(intent,SELECT_PAYMENT_REQUEST);
            }
        });
        paymentDialog.show();
    }

    private void sendCashPayRequest(String amount,String cabID,String sendercell, String receivercell) {
        String endpoint = GlobalVariables.ServiceUrl + "/payNow.php";
        String authString = amount + cabID+AppConstants.CASH
                + receivercell + sendercell ;
        String params = "amount=" + amount + "&sendercell=" + sendercell + "&receivercell=" + receivercell
                + "&cabId=" + cabID+"&paymentMethod="+AppConstants.CASH + "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);
        Log.d("CheckPoolFragmentActivity", "initiatePeerTransfer endpoint : "
                + endpoint + " params : " + params);
        new GlobalAsyncTask(this, endpoint, params, null, this, false,
                "initiatePeerTransfer", false);

    }

    public ArrayList<ListItem> getAllList() {

        ArrayList<ListItem> allList = new ArrayList<ListItem>();

        ListItem item = new ListItem();
        item.setData("CASH");
        allList.add(item);

        item = new ListItem();
        item.setData("MOBIKWIK");
        allList.add(item);


        return allList;
    }
   /* private void logTransaction(String amount, String fee, String merchantname,
                                String mid, String token, String sendercell, String receivercell,
                                String cabID) {

        String endpoint = GlobalVariables.ServiceUrl + "/logTransaction.php";
        String authString = amount + cabID + fee + merchantname + mid
                + receivercell + sendercell + token;
        String params = "amount=" + amount + "&fee=" + fee + "&merchantname="
                + merchantname + "&mid=" + mid + "&token=" + token
                + "&sendercell=" + sendercell + "&receivercell=" + receivercell
                + "&cabId=" + cabID + "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);
        Log.d("CheckPoolFragmentActivity", "logTransaction endpoint : "
                + endpoint + " params : " + params);
        new GlobalAsyncTask(this, endpoint, params, null, this, false,
                "logTransaction", false);
    }

    private void initiatePeerTransfer(String sendercell, String receivercell,
                                      String amount, String fee, String orderid, String token) {

        String checksumstring = GlobalMethods.calculateCheckSumForService("'"
                        + amount + "''" + fee + "''"
                        + GlobalVariables.Mobikwik_MerchantName + "''"
                        + GlobalVariables.Mobikwik_Mid + "''" + orderid + "''"
                        + receivercell + "''" + sendercell + "''" + token + "'",
                GlobalVariables.Mobikwik_14SecretKey);
        String endpoint = GlobalVariables.ServiceUrl
                + "/mobikwikPayments.php";
        String params = "sendercell=" + sendercell + "&receivercell="
                + receivercell + "&amount=" + amount + "&fee=" + fee
                + "&orderid=" + orderid + "&token=" + token + "&mid="
                + GlobalVariables.Mobikwik_Mid + "&merchantname="
                + GlobalVariables.Mobikwik_MerchantName +"&cabId="+rideDetailsModel.getCabId();
        Log.d("CheckPoolFragmentActivity", "initiatePeerTransfer endpoint : "
                + endpoint + " params : " + params);
        new GlobalAsyncTask(this, endpoint, params, null, this, false,
                "initiatePeerTransfer", false);
    }*/

    private void sendPaymentRequest(String amount,String cabID,String sendercell, String receivercell){

        String endpoint = GlobalVariables.ServiceUrl + "/payNow.php";
        String authString = amount + cabID+SPreference.getWalletType(XMemberRideFragmentActivity.this)
                + receivercell + sendercell ;
        String params = "amount=" + amount + "&sendercell=" + sendercell + "&receivercell=" + receivercell
                + "&cabId=" + cabID+"&paymentMethod="+SPreference.getWalletType(XMemberRideFragmentActivity.this) + "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);
        Log.d("CheckPoolFragmentActivity", "initiatePeerTransfer endpoint : "
                + endpoint + " params : " + params);
        new GlobalAsyncTask(this, endpoint, params, null, this, false,
                "initiatePeerTransfer", false);
    }


    private void tokenRegenerate(String mobilenumber, String token,
                                 boolean attemptReTransfer) {
        String msgcode = "507";

        String checksumstring = GlobalMethods.calculateCheckSumForService("'"
                        + mobilenumber + "''" + GlobalVariables.Mobikwik_MerchantName
                        + "''" + GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
                        + token + "''1'",
                GlobalVariables.Mobikwik_14SecretKey_TokenRegenerate);
        String endpoint = GlobalVariables.Mobikwik_ServerURL
                + "/tokenregenerate";
        String params = "cell=" + mobilenumber + "&token=" + token
                + "&tokentype=1" + "&msgcode=" + msgcode + "&mid="
                + GlobalVariables.Mobikwik_Mid + "&merchantname="
                + GlobalVariables.Mobikwik_MerchantName + "&checksum="
                + checksumstring;
        Log.d("CheckPoolFragmentActivity", "tokenRegenerate endpoint : "
                + endpoint + " params : " + params);
        if (attemptReTransfer) {
            new GlobalAsyncTask(this, endpoint, params, null, this, true,
                    "tokenRegenerateReTransfer", true);
        } else {
            new GlobalAsyncTask(this, endpoint, params, null, this, true,
                    "tokenregenerate", true);
        }
    }

   /* private int getDistance(){
        try{
            String fromlatLong = memberlocationlatlong.latitude+","+memberlocationlatlong.longitude;
            String tolatLong = memberlocationlatlongTo.latitude+","+memberlocationlatlongTo.longitude;
            String source = memberlocationaddressFrom.trim()
                    .replaceAll(" ", "%0A").replaceAll("\n","%0A");

            String dest = memberlocationaddressTo.trim()
                    .replaceAll(" ", "%0A").replaceAll("\n","%0A");;

            String url = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin="
                    + source
                    + "&destination="
                    + dest
                    + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                    + GlobalVariables.GoogleMapsAPIKey;

            Log.d("url", "" + url);

            String CompletePageResponse = new Communicator()
                    .executeHttpGet(url);

            CompletePageResponse = CompletePageResponse
                    .replaceAll("\\\\/", "/");

            JSONObject jsonObject = new JSONObject(CompletePageResponse);

            String name = jsonObject.getString("routes");

            JSONArray subArray = new JSONArray(name);

            String distancevalue = null;
            String distancetext = null;

            String durationvalue = null;
            String durationtext = null;

            for (int i = 0; i < subArray.length(); i++) {

                String name1 = subArray.getJSONObject(i).getString("legs")
                        .toString();

                JSONArray subArray1 = new JSONArray(name1);

                for (int i1 = 0; i1 < subArray1.length(); i1++) {

                    String startadd = subArray1.getJSONObject(i1)
                            .getString("distance").toString();

                    JSONObject jsonObject1 = new JSONObject(startadd);
                    distancevalue = jsonObject1.getString("value");
                    distancetext = jsonObject1.getString("text");

                    String startadd1 = subArray1.getJSONObject(i1)
                            .getString("duration").toString();

                    JSONObject jsonObject11 = new JSONObject(startadd1);
                    durationvalue = jsonObject11.getString("value");
                    durationtext = jsonObject11.getString("text");
                }
            }
            if(TextUtils.isEmpty(distancetext)){
               // exceptioncheck = true;
                return 0;
            }

            Log.d("distancevalue", "" + distancevalue);
            Log.d("distancetext", "" + distancetext);

            Log.d("durationvalue", "" + durationvalue);
            Log.d("durationtext", "" + durationtext);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }*/

    private String getDistance(String fromAddress, String toAddres){
        String distancevalue = null;
        String distancetext = null;
        try{

            String source = fromAddress.trim()
                    .replaceAll(" ", "%0A").replaceAll("\n","%0A");

            String dest = toAddres.trim()
                    .replaceAll(" ", "%0A").replaceAll("\n","%0A");;

            String url = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin="
                    + source
                    + "&destination="
                    + dest
                    + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                    + GlobalVariables.GoogleMapsAPIKey;

            Log.d("url", "" + url);

            String CompletePageResponse = new Communicator()
                    .executeHttpGet(url);

            CompletePageResponse = CompletePageResponse
                    .replaceAll("\\\\/", "/");

            JSONObject jsonObject = new JSONObject(CompletePageResponse);

            String name = jsonObject.getString("routes");

            JSONArray subArray = new JSONArray(name);


            String durationvalue = null;
            String durationtext = null;

            for (int i = 0; i < subArray.length(); i++) {

                String name1 = subArray.getJSONObject(i).getString("legs")
                        .toString();

                JSONArray subArray1 = new JSONArray(name1);

                for (int i1 = 0; i1 < subArray1.length(); i1++) {

                    String startadd = subArray1.getJSONObject(i1)
                            .getString("distance").toString();

                    JSONObject jsonObject1 = new JSONObject(startadd);
                    distancevalue = jsonObject1.getString("value");
                    distancetext = jsonObject1.getString("text");

                    String startadd1 = subArray1.getJSONObject(i1)
                            .getString("duration").toString();

                    JSONObject jsonObject11 = new JSONObject(startadd1);
                    durationvalue = jsonObject11.getString("value");
                    durationtext = jsonObject11.getString("text");
                }
            }
            if(TextUtils.isEmpty(distancetext)){
                // exceptioncheck = true;
                return "";
            }

            Log.d("distancevalue", "" + distancevalue);
            Log.d("distancetext", "" + distancetext);

            Log.d("durationvalue", "" + durationvalue);
            Log.d("durationtext", "" + durationtext);
        }catch (Exception e){
            e.printStackTrace();
        }
        return distancetext;
    }

    private void isValidDistance(){
        String wayPoint = "&waypoints=optimize:true";

        wayPoint += "%7C" + memberlocationlatlong.latitude + "," + memberlocationlatlong.longitude + "%7C"
                + memberlocationlatlongTo.latitude + "," + memberlocationlatlongTo.longitude;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskValidatingDistance().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, wayPoint);
        } else {
            new ConnectionTaskValidatingDistance().execute(wayPoint);
        }
    }

    private class ConnectionTaskValidatingDistance extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;
        private int memberJoinDistance;

        @Override
        protected void onPreExecute() {
            showProgressBar();
            memberJoinDistance = 0;

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateValidatingDistance mAuth1 = new AuthenticateValidatingDistance();
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
                    Toast.makeText(XMemberRideFragmentActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                float totalRideDistance = Float.parseFloat(rideDetailsModel.getDistance().replace("km","").trim())*1000;
                if(memberJoinDistance <= (1.25*totalRideDistance)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new ConnectionTaskForJoiningapool().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new ConnectionTaskForJoiningapool().execute();
                    }
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                    builder.setTitle("Offtrack!");
                    builder.setMessage("Please select pickup and drop closer to route or join another ride.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            isPick = false;
                            locationmarker.setVisibility(View.GONE);
                            memberlocationaddressFrom = null;
                            memberlocationaddressTo = null;
                            memberlocationlatlongTo = null;
                            memberlocationlatlong = null;
                            mylocationmarkerFrom.remove();
                            mylocationmarkerTo.remove();
                            joinPoolClicked();
                        }
                    });
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public class AuthenticateValidatingDistance {
            private String wayPointUrl;

            public AuthenticateValidatingDistance() {

            }

            public void connection() throws Exception {

               String source = rideDetailsModel.getFromLocation();
               String dest = rideDetailsModel.getToLocation();
                Address locationAddressFrom = null, locationAddressTo = null;


                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + rideDetailsModel.getsLatLon()
                        + "&destination="
                        + rideDetailsModel.geteLatLon()
                        + wayPointUrl
                        + "&sensor=false&units=metric&mode=driving&alternatives=false&key="
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
                    memberJoinDistance = 0;
                    for (int i1 = 0; i1 < subArray1.length(); i1++) {

                        memberJoinDistance =  memberJoinDistance + Integer.valueOf(subArray1.getJSONObject(i1).getJSONObject("distance").getString("value"));

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

                        durationList.add(jsonObjectDuraton.getInt("value"));

                        String distance = subArray1.getJSONObject(i1)
                                .getString("distance").toString();
                        JSONObject jsonObjectDistance = new JSONObject(distance);

                        distanceList.add(jsonObjectDistance.getInt("value"));
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
                            XMemberRideFragmentActivity.this,
                            via_waypoint.get(i).latitude,
                            via_waypoint.get(i).longitude);
                    via_waypointstrarr.add(asd);
                }
                Log.d("via_waypointstrarr", "" + via_waypointstrarr);
            }
        }

    }

  /*  private void onConfirmedClicked(){
        memberlocationaddressFrom =  rideDetailsModel.getFromLocation();
        memberlocationaddressTo =  rideDetailsModel.getToLocation();
        memberlocationlatlong = startaddlatlng.get(0);
        memberlocationlatlongTo = endaddlatlng.get(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForJoiningapool().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskForJoiningapool().execute();
        }
    }*/
    private void checkBalanceNew(){
        double minFare = Double.parseDouble(rideDetailsModel.getPerKmCharge());
        double distance = Double.parseDouble(memberDistance.trim());

       // int minFare = Integer.parseInt(rideDetailsModel.getPerKmCharge());
       // int distance = (int)Double.parseDouble(rideDetailsModel.getDistance().replace("km","").trim());
        int totalFare = (int)Math.round(distance*minFare);

            String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
            String authString = "checkBalanceForRide"+totalFare+MemberNumberstr+SPreference.getWalletType(XMemberRideFragmentActivity.this);
            String params = "act=checkBalanceForRide"+"&amount="+totalFare+"&mobileNumber="
                    + MemberNumberstr+"&paymentMethod="+SPreference.getWalletType(XMemberRideFragmentActivity.this)+ "&auth="
                    + GlobalMethods.calculateCMCAuthString(authString);

            new GlobalAsyncTask(XMemberRideFragmentActivity.this, endpoint, params,
                    null, XMemberRideFragmentActivity.this, true, "checkBalance",
                    false);
    }

    private void checkTokenExist(){
        String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
        String authString = "getToken"+MemberNumberstr;
        String params = "act=getToken&mobileNumber="
                + MemberNumberstr+ "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);

        new GlobalAsyncTask(XMemberRideFragmentActivity.this, endpoint, params,
                null, XMemberRideFragmentActivity.this, true, "getToken",
                false);
    }


    //-------------------------------------------------------------------------------->

    private void validatePickupPoint(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskValidatePickupPoint().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskValidatePickupPoint().execute();
        }
    }
    private void validateDropPoint(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskValidateDropPoint().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskValidateDropPoint().execute();
        }
    }

    private class ConnectionTaskValidatePickupPoint extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;
        private double memberJoinDistance;

        @Override
        protected void onPreExecute() {
            showProgressBar();
            memberJoinDistance = 0;

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticatePickupLocation mAuth1 = new AuthenticatePickupLocation();
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
                  Toast.makeText(XMemberRideFragmentActivity.this,
                          getResources().getString(R.string.exceptionstring),
                          Toast.LENGTH_LONG).show();
                  return;
              }
              // float totalRideDistance = Float.parseFloat(rideDetailsModel.getDistance().replace("km", "").trim()) * 1000;
              List<LatLng> list = polyLine;
              if(memberJoinDistance > 2500 && !PolyUtil.isLocationOnPath(new LatLng(memberlocationlatlong.latitude,memberlocationlatlong.longitude),list,true,500)){
                  AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                  builder.setTitle("Offtrack!");
                  builder.setMessage("Please select pickup closer to route or join another ride.");
                  builder.setCancelable(false);
                  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          dialog.dismiss();
                          isPick = false;
                          locationmarker.setVisibility(View.GONE);
                          memberlocationaddressFrom = null;
                          memberlocationaddressTo = null;
                          memberlocationlatlongTo = null;
                          memberlocationlatlong = null;
                          if(mylocationmarkerFrom != null){
                              mylocationmarkerFrom.remove();
                          }

                          // mylocationmarkerTo.remove();
                          joinPoolClicked();
                      }
                  });
                  AlertDialog dialog = builder.show();
                  TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                  messageText.setGravity(Gravity.CENTER);
                  dialog.show();
              }else {
                  isPick = true;
                  mylocationmarkerFrom = joinpoolmap.addMarker(new MarkerOptions().position(memberlocationlatlong).snippet("Pick")
                          .title(usermemlocadd).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                  joinpoolmap.animateCamera(CameraUpdateFactory.newLatLngZoom(toLatLang, 16));
                  buttonLocationMarker.setText("Select Drop Location");
                  joinPoolClicked();
              }
          }catch (Exception e){
              e.printStackTrace();
          }



        }
        public class AuthenticatePickupLocation {

            public AuthenticatePickupLocation() {

            }

            public void connection() throws Exception {

                String source = rideDetailsModel.getFromLocation().replaceAll(" ",
                        "%20");
                String dest = memberlocationaddressFrom.replaceAll(" ",
                        "%20");
                String fromlatLong = rideDetailsModel.getsLatLon();
                String tolatLong = memberlocationlatlong.latitude+","+memberlocationlatlong.longitude;

                // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + fromlatLong
                        + "&destination="
                        + tolatLong
                        + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                        + GlobalVariables.GoogleMapsAPIKey;

                Log.d("url", "" + url);

                CompletePageResponse = new Communicator().executeHttpGet(url);

                CompletePageResponse = CompletePageResponse
                        .replaceAll("\\\\/", "/");

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
                    memberJoinDistance = tempDistance;
                }

            }
        }

    }

    private class ConnectionTaskValidateDropPoint extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;
        private double memberJoinDistance;

        @Override
        protected void onPreExecute() {
            showProgressBar();
            memberJoinDistance = 0;

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateDropLocation mAuth1 = new AuthenticateDropLocation();
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
                   Toast.makeText(XMemberRideFragmentActivity.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }
               // float totalRideDistance = Float.parseFloat(rideDetailsModel.getDistance().replace("km", "").trim()) * 1000;
               List<LatLng> list = polyLine;
               if(memberJoinDistance > 2500 && !PolyUtil.isLocationOnPath(new LatLng(memberlocationlatlongTo.latitude,memberlocationlatlongTo.longitude),list,true,500)) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(XMemberRideFragmentActivity.this);
                   builder.setTitle("Offtrack!");
                   builder.setMessage("Please select drop closer to route or join another ride.");
                   builder.setCancelable(false);
                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                           isPick = true;
                           locationmarker.setVisibility(View.VISIBLE);
                           buttonLocationMarker.setVisibility(View.VISIBLE);
                           //  memberlocationaddressFrom = null;
                           // memberlocationlatlong = null;
                           memberlocationaddressTo = null;
                           memberlocationlatlongTo = null;
                           // mylocationmarkerFrom.remove();
                           if(mylocationmarkerTo != null){
                               mylocationmarkerTo.remove();

                           }
                           joinPoolClicked();
                       }
                   });
                   AlertDialog dialog = builder.show();
                   TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                   messageText.setGravity(Gravity.CENTER);
                   dialog.show();

               }else {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                       new ConnectionTaskForJoiningapool().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                   } else {
                       new ConnectionTaskForJoiningapool().execute();
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
           }




        }
        public class AuthenticateDropLocation {

            public AuthenticateDropLocation() {

            }

            public void connection() throws Exception {

                String source = rideDetailsModel.getToLocation().replaceAll(" ",
                        "%20");
                String dest = memberlocationaddressTo.replaceAll(" ",
                        "%20");

                String fromlatLong = rideDetailsModel.geteLatLon();
                String tolatLong = memberlocationlatlongTo.latitude+","+memberlocationlatlongTo.longitude;


                // http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + fromlatLong
                        + "&destination="
                        + tolatLong
                        + "&sensor=false&units=metric&mode=driving&alternatives=true&key="
                        + GlobalVariables.GoogleMapsAPIKey;

                Log.d("url", "" + url);

                CompletePageResponse = new Communicator().executeHttpGet(url);

                CompletePageResponse = CompletePageResponse
                        .replaceAll("\\\\/", "/");

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
                    memberJoinDistance = tempDistance;
                }

            }
        }

    }
    int paymentType;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode ==  SELECT_PAYMENT_REQUEST && data != null){
                try{
                    paymentType = data.getExtras().getInt("type");
                    SPreference.getPref(XMemberRideFragmentActivity.this).edit().putInt(SPreference.RIDE_TAKER_WALLET_TYPE,paymentType).commit();
                    SPreference.getPref(XMemberRideFragmentActivity.this).edit().putInt(SPreference.SELECTED_PAY_TYPE,paymentType).commit();
                    if(paymentType == AppConstants.PAYMENT_TYPE_CASH){
                        ((ImageView)paymentDialog.findViewById(R.id.pTytpeImage)).setImageResource(R.drawable.popup_cash);
                      //  ((TextView)paymentDialog.findViewById(R.id.tvCash)).setText("CASH");
                    }else if(paymentType == AppConstants.PAYMENT_TYPE_MOBIKWIK){
                        ((ImageView)paymentDialog.findViewById(R.id.pTytpeImage)).setImageResource(R.drawable.popup_01);
                      //  ((TextView)paymentDialog.findViewById(R.id.tvCash)).setText("MOBIKWIK WALLET");
                    }
                    if(paymentType != AppConstants.PAYMENT_TYPE_CASH){
                        SPreference.getPref(XMemberRideFragmentActivity.this).edit().putInt(SPreference.RIDE_OFFER_WALLET_TYPE,paymentType).commit();
                       // checkTokenExist();
                    }


                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }



    public void getCurrentLocation(final LatLng location) {
        new AsyncTask<Void, Void, List<Address>>() {

            @Override
            protected List<Address> doInBackground(Void... voids) {
                Geocoder geo = new Geocoder(XMemberRideFragmentActivity.this);

                List<Address> listAddresses = null;

                try {
                    if (location != null) {
                        listAddresses = geo.getFromLocation(location.latitude,
                                location.longitude,
                                1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(XMemberRideFragmentActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }

                return listAddresses;
            }

            public void onPostExecute(List<Address> listAddresses) {
                Address _address = null;
                StringBuilder result = new StringBuilder();

                if ((listAddresses != null) && (listAddresses.size() > 0)) {
                    _address = listAddresses.get(0);

                    for (int i = 0; i <= _address.getMaxAddressLineIndex(); i++) {
                        result.append(_address.getAddressLine(i) + " ");
                    }
                    joinpoolchangelocationtext.setText(result.toString());
                   /* GeoPoint currentPosition = new GeoPoint(((int)(_address.getLatitude() * 1E6)),
                            ((int)(_address.getLongitude() * 1E6)));*/
                }

            }

        }.execute();
    }

    private void locationHandler(LatLng latLng){
        //if (isOnline()) {
        LocationModel locationModel = new LocationModel();
        locationModel.setLocationByAddress(false);
        locationModel.setLatLng(latLng);
        locationModel.setLocationByAddress(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetLocationTaskHandler(XMemberRideFragmentActivity.this, locationModelListener,locationModel )
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetLocationTaskHandler(XMemberRideFragmentActivity.this, locationModelListener,locationModel).execute();
            }
       // }
    }

    private LocationModelListener locationModelListener = new LocationModelListener() {

        @Override
        public void getAddress(Address address) {

        }

        @Override
        public void getLatLong(LatLng latLng) {

        }

        @Override
        public void getStringAddress(String address) {
            if (!isPick) {
                memberlocationaddressFrom =  address;
            }else {
                memberlocationaddressTo = address;
            }
            joinpoolchangelocationtext.setText(address);
        }

        @Override
        public void getError(String error) {
            joinpoolchangelocationtext.setText(error);
        }

        @Override
        public void isLoading(boolean isLoading) {
            if(isLoading){
                joinpoolchangelocationtext.setVisibility(View.GONE);
                findViewById(R.id.dot_progress_bar).setVisibility(View.VISIBLE);
            }else {
                joinpoolchangelocationtext.setVisibility(View.VISIBLE);
                findViewById(R.id.dot_progress_bar).setVisibility(View.GONE);
            }
        }


    };
}
