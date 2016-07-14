package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.clubmycab.CabApplication;
import com.clubmycab.CircularImageView;
import com.clubmycab.Communicator;
import com.clubmycab.R;
import com.clubmycab.UpcomingStartTripAlarm;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.model.ContactData;
import com.clubmycab.model.GroupDataModel;
import com.clubmycab.model.MemberModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.LocationAddress;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewRideCreationScreen extends Activity implements GlobalAsyncTask.AsyncTaskResultListener, View.OnClickListener {
    public static final String HOME_ACTIVITY_CAR_POOL = "HOME_ACTIVITY_CAR_POOL";
    public static final String HOME_ACTIVITY_SHARE_CAB = "HOME_ACTIVITY_SHARE_CAB";
    public static final int CONTACTS_GROUP_REQUEST = 500;
    public static final int FROM_LOC_TEXT_PICKER = 501;
    public static final int TO_LOC_TEXT_PIC = 502;
    static final int TIME_DIALOG_ID = 1001;
    static final int DATE_DIALOG_ID = 1002;
    float FromToMinDestance = 2000;
    private static final int MAX_SEAT_COUNT = 20;

    private Tracker tracker;
    private AppEventsLogger logger;
    private CircularImageView profilepic;
    private ImageView notificationimg;
    private CircleImageView drawerprofilepic;
    private String FullName, MobileNumber, myprofileresp, imagenameresp;
    private TextView username, drawerusername, unreadnoticount;
    private RelativeLayout unreadnoticountrl;
    private Bitmap mIcon11;
    private ScreenType screenType;
    private Location mycurrentlocationobject;
    private LocationRequestType locationRequestType;
    private Address fAddress, tAddress;
    private AddressModel addressModelFrom;
    private TextView from_places, to_places;
    private String fromshortname;
    private AddressModel addressModelTo;
    private String toshortname;
    private CheckBox checkPubToPrvt;
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView tvDate;
    private int year, month, day;
    private TextView tvTime, tvSeatCount;
    private int hour, minute;
    private boolean isCallresetIntentParams;
    private String travelDate;
    private String travelTime;
    private String CabId;
    private AlertDialog dialogseats;
    private ArrayList<String> selectednumbers = new ArrayList<String>();
    private ArrayList<String> selectednames = new ArrayList<String>();
    private int seatCount = 3;
    private String publicRideResponse;
    private String distancetext;
    private String privateRideResponse;
    private int pickerHour;
    private Dialog onedialog;

    private enum ScreenType {
        CARPOOL, CABSHARE;
    }

    ;

    private enum LocationRequestType {
        FROMlOCATON, TOLOCATION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ride_creation_screen);
        GoogleAnalytics analytics = GoogleAnalytics
                .getInstance(NewRideCreationScreen.this);
        tracker = analytics
                .newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("Offer Ride Screen");

        //  checkNetworkConnection();
        initViews();
    }
    private void initViews() {
        try {
            findViewById(R.id.flFromLocation).setOnClickListener(this);
            findViewById(R.id.flToLocation).setOnClickListener(this);
            findViewById(R.id.llSendInvite).setOnClickListener(this);
            findViewById(R.id.llShowDate).setOnClickListener(this);
            findViewById(R.id.llShowTime).setOnClickListener(this);
            findViewById(R.id.flMinus).setOnClickListener(this);
            findViewById(R.id.flPlus).setOnClickListener(this);
            findViewById(R.id.from_places).setOnClickListener(this);
            findViewById(R.id.to_places).setOnClickListener(this);
            findViewById(R.id.flMainLayout).setOnClickListener(this);
            findViewById(R.id.flQuestionMark).setOnClickListener(this);
            findViewById(R.id.flInfo).setOnClickListener(this);

            tvDate = (TextView) findViewById(R.id.tvDate);
            tvTime = (TextView) findViewById(R.id.tvTime);
            from_places = (TextView) findViewById(R.id.from_places);
            to_places = (TextView) findViewById(R.id.to_places);
            tvSeatCount = (TextView) findViewById(R.id.tvSeatCount);
            checkPubToPrvt = (CheckBox) findViewById(R.id.checkPubToPrvt);

            tvDate.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            tvTime.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            from_places.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            to_places.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            tvSeatCount.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            checkPubToPrvt.setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.textFrom)).setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.textTo)).setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.tvDeparture)).setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.tvSeats)).setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.tvNext1)).setTypeface(FontTypeface.getTypeface(NewRideCreationScreen.this, AppConstants.HELVITICA));

            checkPubToPrvt.setOnCheckedChangeListener(onCheckedChangeListener);
            initializeDarawer();
            setNotificationAndProfileImage();
            setScreenType();

            calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR,1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            updateTime(hour, minute);
            // set current time into textview
           /* tvTime.setText(
                    new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));*/
            showDate(year, month + 1, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDarawer() {
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
                tracker.send(new HitBuilders.EventBuilder().setCategory("Notification").setAction("Notification icon pressed").setLabel("Notification icon pressed").build());

                Intent mainIntent = new Intent(NewRideCreationScreen.this,
                        NotificationListActivity.class);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PickLocationActivity.PICLOCATION_REQUEST) {
                if (getIntent() == null)
                    return;
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (locationRequestType == LocationRequestType.FROMlOCATON) {
                        String fromlocationname = bundle.getString("address");
                       /* double latitude = bundle.getDouble("latitude");
                        double longitude = bundle.getDouble("longitude");
                        bundle.getParcelable("addressmodel");
                        fromshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, latitude, longitude);
                        Address address = new Address(Locale.getDefault());
                        address.setLatitude(latitude);
                        address.setLongitude(longitude);
                        fAddress = address;*/
                        fAddress =   bundle.getParcelable("addressmodel");
                        from_places.setText("");
                        if(fAddress != null){
                           /* fAddress.setLatitude(latitude);
                            fAddress.setLongitude(longitude);
                         */   from_places.setText(fromlocationname);
                            try {
                                fromshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, fAddress);
                                addressModelFrom = new AddressModel();
                                addressModelFrom.setAddress(fAddress);
                                addressModelFrom.setShortname(fromshortname);
                                addressModelFrom.setLongname(from_places.getText()
                                        .toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (locationRequestType == LocationRequestType.TOLOCATION) {
                        String tolocationname = bundle.getString("address");
                        /* double latitude = bundle.getDouble("latitude");
                        double longitude = bundle.getDouble("longitude");
                        toshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, latitude, longitude);
                        Address address = new Address(Locale.getDefault());
                        address.setLatitude(latitude);
                        address.setLongitude(longitude);
                        tAddress = address;*/
                        to_places.setText("");
                        tAddress =   bundle.getParcelable("addressmodel");
                        if(tAddress != null){
                           /* tAddress.setLatitude(latitude);
                            tAddress.setLongitude(longitude);
                         */   to_places.setText(tolocationname);
                            try {
                                toshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, tAddress);
                                addressModelTo = new AddressModel();
                                addressModelTo.setAddress(tAddress);
                                addressModelTo.setShortname(toshortname);
                                addressModelTo.setLongname(to_places.getText().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            } else if (requestCode == FROM_LOC_TEXT_PICKER) {
                String value = (String) data.getExtras().getString("address");
                double latitude = data.getExtras().getDouble("latitude");
                double longitude =  data.getExtras().getDouble("longitude");

                Log.d("from_place:::", value);

                // from_places.append(value);
                from_places.setText(value);

                if (TextUtils.isEmpty(value)) {
                    return;
                } else {
                    fAddress = null; // reset previous

                    //fAddress = geocodeAddress(from_places.getText().toString());
                    //fAddress = getCodeAddressByLatLong(new LatLng(latitude,longitude));
                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(latitude);
                    address.setLongitude(longitude);
                    fAddress = address;

                    if (fAddress == null) {
                        Toast.makeText(
                                NewRideCreationScreen.this,
                                "Could not locate the address, please try using the map or a different address",
                                Toast.LENGTH_LONG).show();
                    } else {
                        addressModelFrom = new AddressModel();
                        addressModelFrom.setAddress(fAddress);
                       /* fromshortname = MapUtilityMethods.getAddressshort(
                                NewRideCreationScreen.this, fAddress.getLatitude(),
                                fAddress.getLongitude());*/
                        //fromshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, fAddress);
                        fromshortname = MapUtilityMethods.getaddressfromautoplace(NewRideCreationScreen.this, value);
                        addressModelFrom.setShortname(fromshortname);
                        addressModelFrom.setLongname(from_places.getText()
                                .toString());

                        if (fAddress != null && tAddress != null) {
                            // homebtnsll.setVisibility(View.VISIBLE);
                            // showButtonsDialog();

                        }
                    }
                }
            } else if (requestCode == TO_LOC_TEXT_PIC) {
                String value = (String) data.getExtras().getString("address");
                double latitude = data.getExtras().getDouble("latitude");
                double longitude =  data.getExtras().getDouble("longitude");
                // Toast.makeText(mcontext, "call back to_place:"+value,
                // Toast.LENGTH_SHORT).show();
                to_places.setText(value);
                if (TextUtils.isEmpty(value)) {
                    return;
                } else {

                    tAddress = null; // reset previous

                   // tAddress = geocodeAddress(to_places.getText().toString());
                    //tAddress = getCodeAddressByLatLong(new LatLng(latitude,longitude));
                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(latitude);
                    address.setLongitude(longitude);
                    tAddress = address;


                    if (tAddress == null) {
                        Toast.makeText(
                                NewRideCreationScreen.this,
                                "Could not locate the address, please try using the map or a different address",
                                Toast.LENGTH_LONG).show();
                    } else {
                        addressModelTo = new AddressModel();
                        addressModelTo.setAddress(tAddress);
                       /* toshortname = MapUtilityMethods.getAddressshort(
                                NewRideCreationScreen.this, tAddress.getLatitude(),
                                tAddress.getLongitude());*/
                        //toshortname = MapUtilityMethods.getAddressshort(NewRideCreationScreen.this, tAddress);
                        toshortname = MapUtilityMethods.getaddressfromautoplace(NewRideCreationScreen.this, value);

                        addressModelTo.setShortname(toshortname);
                        addressModelTo.setLongname(to_places.getText()
                                .toString());

                        if (fAddress != null && tAddress != null) {
                            //showButtonsDialog();

                        }
                    }
                }
            }else if(requestCode == CONTACTS_GROUP_REQUEST){
                if (data.getExtras().getBoolean("iscontactslected")) {
                    Log.d("", "");
                    ArrayList<ContactData> myList = data.getExtras()
                            .getParcelableArrayList("Contact_list");
                    if (myList != null && myList.size() > 0) {
                        sendInviteRequest(
                                data.getExtras().getBoolean("iscontactslected"),
                                myList, null);
                    }
                } else {
                    L.mesaage("");
                    ArrayList<GroupDataModel> myList = data.getExtras()
                            .getParcelableArrayList("Group_list");
                    if (myList != null && myList.size() > 0) {
                        sendInviteRequest(
                                data.getExtras().getBoolean("iscontactslected"),
                                null, myList);
                    }

                }
            }
        }
    }
    double latitude, longitude;

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.flFromLocation:
                locationRequestType = LocationRequestType.FROMlOCATON;
                 intent = new Intent(NewRideCreationScreen.this, PickLocationActivity.class);
                if(addressModelFrom != null && addressModelFrom.getAddress() != null){
                    latitude = addressModelFrom.getAddress().getLatitude();
                    longitude = addressModelFrom.getAddress().getLongitude();

                }else {
                   if(CabApplication.getInstance().getFirstLocation() != null){
                       latitude = CabApplication.getInstance().getFirstLocation().getLatitude();
                       longitude = CabApplication.getInstance().getFirstLocation().getLongitude();
                   }



                }
                if(!TextUtils.isEmpty(from_places.getText().toString()))
                    intent.putExtra("address",from_places.getText().toString());

                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent, PickLocationActivity.PICLOCATION_REQUEST);
                break;

            case R.id.flToLocation:
                locationRequestType = LocationRequestType.TOLOCATION;
                 intent = new Intent(NewRideCreationScreen.this, PickLocationActivity.class);
                if(addressModelTo != null && addressModelTo.getAddress() != null){
                    latitude = addressModelTo.getAddress().getLatitude();
                    longitude = addressModelTo.getAddress().getLongitude();
                }else {
                    if(CabApplication.getInstance().getFirstLocation() != null){
                        latitude = CabApplication.getInstance().getFirstLocation().getLatitude();
                        longitude = CabApplication.getInstance().getFirstLocation().getLongitude();
                    }else {
                        latitude = 0.0;
                        longitude = 0.0;
                    }


                }
                if(!TextUtils.isEmpty(to_places.getText().toString()))
                    intent.putExtra("address",to_places.getText().toString());

                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent, PickLocationActivity.PICLOCATION_REQUEST);
                break;

            case R.id.llSendInvite:
                if (isValidRequest()) {
                    sendRideCreationRequest();
                }
                break;

            case R.id.llShowDate:
                showDialog(DATE_DIALOG_ID);
                break;

            case R.id.llShowTime:
                showDialog(TIME_DIALOG_ID);
                break;

            case R.id.flMinus:
                if(seatCount > 1){
                    seatCount = seatCount - 1;
                    tvSeatCount.setText(String.valueOf(seatCount));
                }
                break;

            case R.id.flPlus:
                if(seatCount < MAX_SEAT_COUNT){
                    seatCount = seatCount + 1;
                    tvSeatCount.setText(String.valueOf(seatCount));
                }
                break;

            case R.id.from_places:
                // Open get places list activity
                isCallresetIntentParams = true;
                 intent= new Intent(NewRideCreationScreen.this,
                        FavoritePlaceFindActivity.class);

                startActivityForResult(intent, FROM_LOC_TEXT_PICKER);
                findViewById(R.id.from_places).setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.from_places).setClickable(true);

                    }
                },3000);
                // overridePendingTransition(R.anim.slide_in_right,
                // R.anim.slide_out_left);

                break;
            case R.id.to_places:
                isCallresetIntentParams = true;

                intent = new Intent(NewRideCreationScreen.this,
                        FavoritePlaceFindActivity.class);

                startActivityForResult(intent, TO_LOC_TEXT_PIC);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.to_places).setClickable(true);

                    }
                },3000);
                break;
            case R.id.flMainLayout:

                break;

            case R.id.flQuestionMark:
                CustomDialog.showDialog(NewRideCreationScreen.this,"Select this option if you want to offer rides only to your contacts and groups. This may reduce the chances of you finding a co-rider");
                break;

            case R.id.flInfo:
                tracker.send(new HitBuilders.EventBuilder().setCategory("Offer ride info").setAction("i button pressed on offer ride").setLabel("Offer ride info").build());

                CustomDialog.showDialog(NewRideCreationScreen.this,"For non-commercial vehicles, offer only that many seats that help you share costs.\n\nWe recommend maximum 3 seats.\n\nTrying to make a profit on a non-commercial license is not allowed and can invalidate your insurance");
                break;

        }
    }


    private void setNotificationAndProfileImage() {
        String endpoint = GlobalVariables.ServiceUrl + "/FetchUnreadNotificationCount.php";
        String authString = MobileNumber;
        String params = "MobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
        new GlobalAsyncTask(this, endpoint, params, new FetchUnreadNotificationCountHandler(), this, false, "FetchUnreadNotificationCount", false);

        // redundant code
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForFetchClubs()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskForFetchClubs().execute();
        }*/

        // ///////////////
        SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
        String imagestr = mPrefs111.getString("imagestr", "");
        if (imagestr.isEmpty() || imagestr == null || imagestr.equalsIgnoreCase("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForfetchimagename().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        SharedPreferences mPrefs1111 = getSharedPreferences("MyProfile", 0);
        String myprofile = mPrefs1111.getString("myprofile", "");
        if (myprofile.isEmpty() || myprofile == null || myprofile.equalsIgnoreCase("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new ConnectionTaskForFetchMyProfile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new ConnectionTaskForFetchMyProfile().execute();
            }
        }
    }

    private void setScreenType() {
        try {
            if (getIntent() != null) {
                Bundle bundle = getIntent().getExtras();
                if (bundle == null)
                    return;
                if (bundle.containsKey("screentoopen") && bundle.getString("screentoopen").equalsIgnoreCase(HOME_ACTIVITY_CAR_POOL)) {
                    screenType = ScreenType.CARPOOL;
                    mycurrentlocationobject = CabApplication.getInstance().getFirstLocation();
                    LocationAddress.getAddressFromLocation(mycurrentlocationobject.getLatitude(), mycurrentlocationobject.getLongitude(), NewRideCreationScreen.this, new GeocoderHandler());
                } else if (bundle.containsKey("screentoopen") && bundle.getString("screentoopen").equalsIgnoreCase(HOME_ACTIVITY_SHARE_CAB)) {
                    screenType = ScreenType.CABSHARE;
                }

                if (bundle.containsKey("fromlocation")) {
                    from_places.setText(bundle.getString("fromlocation"));
                }

                if (bundle.containsKey("tolocation")) {
                    to_places.setText(bundle.getString("tolocation"));

                }
                String startString = "", endString = "";
                if (bundle.containsKey("StartAddressModel")) {
                    startString = bundle
                            .getString("StartAddressModel");
                }
                if (bundle.containsKey("EndAddressModel")) {
                    endString = bundle
                            .getString("EndAddressModel");
                }

                if (!TextUtils.isEmpty(startString) && !TextUtils.isEmpty(endString)) {

                    Log.d("InviteFragment", "StartAddressModel : " + startString
                            + " EndAddressModel : " + endString);

                    Gson gson = new Gson();
                    AddressModel startAddressModel = (AddressModel) gson.fromJson(
                            startString, AddressModel.class);
                    fAddress = startAddressModel.getAddress();
                    fromshortname = startAddressModel.getShortname();
                    from_places.setText(startAddressModel.getLongname());

                    AddressModel endAddressModel = (AddressModel) gson.fromJson(
                            endString, AddressModel.class);
                    tAddress = endAddressModel.getAddress();
                    addressModelFrom = new AddressModel();
                    addressModelFrom.setAddress(fAddress);
                    fromshortname = MapUtilityMethods.getAddressshort(
                            NewRideCreationScreen.this, fAddress.getLatitude(),
                            fAddress.getLongitude());
                    addressModelFrom.setShortname(fromshortname);
                    addressModelFrom.setLongname(from_places.getText()
                            .toString());
                    addressModelTo = new AddressModel();
                    addressModelTo.setAddress(tAddress);
                    toshortname = MapUtilityMethods.getAddressshort(
                            NewRideCreationScreen.this, tAddress.getLatitude(),
                            tAddress.getLongitude());
                    addressModelTo.setShortname(toshortname);
                    addressModelTo.setLongname(to_places.getText()
                            .toString());
                    if (fAddress != null && tAddress != null) {
                        // showInviteButton();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void showInviteButton(){
        Button button = (Button) findViewById(R.id.buttonNext);
        button.setVisibility(View.VISIBLE);
    }*/

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                ((TextView) findViewById(R.id.tvNext1)).setText("INVITE FRIENDS");
            } else {
                ((TextView) findViewById(R.id.tvNext1)).setText("PUBLISH RIDE");
            }

        }
    };

    /**
     * Utility Methods------------------------------->
     */
    public void checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = false;
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            isConnected = true;
        }
        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    NewRideCreationScreen.this);
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
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewRideCreationScreen.this);
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

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    if (locationAddress != null) {
                        from_places.setText(locationAddress.substring(
                                locationAddress.indexOf("Address:") + 8,
                                locationAddress.length()).trim().replaceAll("\n","%0A"));
                        String value = from_places.getText().toString();
                        if (value.equalsIgnoreCase("") || value.isEmpty()) {

                        } else {
                            fAddress = null; // reset previous

                            fAddress = geocodeAddress(from_places.getText()
                                    .toString());
                            if (fAddress == null) {
                                from_places.setText("");
                                //isLocationNotUpdated = false;
                            /*	Toast.makeText(
                                        getActivity(),
										"Could not locate the address, please try using the map or a different address",
										Toast.LENGTH_LONG).show();
				*/

                            } else {
                                //	isLocationNotUpdated = false;
                                addressModelFrom = new AddressModel();
                                addressModelFrom.setAddress(fAddress);
                                fromshortname = MapUtilityMethods.getAddressshort(
                                        NewRideCreationScreen.this, fAddress.getLatitude(),
                                        fAddress.getLongitude());
                                addressModelFrom.setShortname(fromshortname);
                                addressModelFrom.setLongname(from_places.getText()
                                        .toString());
                            }
                        }
                    }

                    break;
                default:
                    locationAddress = null;
            }

            L.mesaage(locationAddress);
        }
    }

    private Address geocodeAddress(String addressString) {
        Address addressReturn = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            ArrayList<Address> arrayList = (ArrayList<Address>) geocoder
                    .getFromLocationName(addressString, 1);
            Log.d("geocodeAddress", "geocodeAddress : " + arrayList.toString());
            if (arrayList.size() > 0) {
                addressReturn = arrayList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressReturn;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(DATE_DIALOG_ID);
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        switch (id) {
            case TIME_DIALOG_ID:
                // set time picker as current time
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                        timePickerListener, pickerHour, minute, false);
                return timePickerDialog;
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, myDateListener, year, month, day);


        }
        return null;
       /* if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);

        }*/
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    updateTime(selectedHour, selectedMinute);

                }
            };

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {
        pickerHour = hours;
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        travelTime = aTime;
        tvTime.setText(aTime);
        this.hour = hours;
        this.minute = mins;
    }

    private void showDate(int year, int month, int day) {
        travelDate = String.format("%02d",day) + "/" + String.format("%02d",month) + "/" + year;
        tvDate.setText(String.format("%02d",day)+" "+getMontString(month));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
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


    /**
     * ----------------------NetworkCall----------------------->
     */
    private class ConnectionTaskForfetchimagename extends
            AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionfetchimagename mAuth1 = new AuthenticateConnectionfetchimagename();
            try {
                mAuth1.connection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (exceptioncheck) {
                exceptioncheck = false;
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (imagenameresp == null) {
                profilepic.setImageResource(R.drawable.cabappicon);
                drawerprofilepic.setImageResource(R.drawable.cabappicon);
            } else if (imagenameresp.contains("Unauthorized Access")) {
                Log.e("HomeActivity", "imagenameresp Unauthorized Access");
                Toast.makeText(NewRideCreationScreen.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
                return;
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
            HttpClient httpClient11 = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/fetchimagename.php";
            HttpPost httpPost11 = new HttpPost(url_select);
            BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair("MobileNumber", MobileNumber);
            String authString = MobileNumber;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth", GlobalMethods.calculateCMCAuthString(authString));
            List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
            nameValuePairList11.add(MobileNumberBasicNameValuePair11);
            nameValuePairList11.add(authValuePair);
            UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(nameValuePairList11);
            httpPost11.setEntity(urlEncodedFormEntity11);
            HttpResponse httpResponse11 = httpClient11.execute(httpPost11);
            Log.d("httpResponse", "" + httpResponse11);
            InputStream inputStream11 = httpResponse11.getEntity().getContent();
            InputStreamReader inputStreamReader11 = new InputStreamReader(inputStream11);
            BufferedReader bufferedReader11 = new BufferedReader(inputStreamReader11);
            StringBuilder stringBuilder11 = new StringBuilder();
            String bufferedStrChunk11 = null;
            while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
                imagenameresp = stringBuilder11.append(bufferedStrChunk11).toString();
            }
            Log.d("imagenameresp", "" + imagenameresp);
            if (imagenameresp == null) {
            } else if (imagenameresp.contains("Unauthorized Access")) {
            } else {
                String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + imagenameresp;
                String urldisplay = url1.toString().trim();
                mIcon11 = null;
                String imgString = null;
                try {
                    InputStream in = new URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    imgString = Base64.encodeToString(getBytesFromBitmap(mIcon11), Base64.NO_WRAP);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences1 = getSharedPreferences("userimage", 0);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("imgname", imagenameresp.trim());
                editor1.putString("imagestr", imgString);
                editor1.commit();
            }
        }
    }

    private class ConnectionTaskForFetchMyProfile extends
            AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionFetchMyProfile mAuth1 = new AuthenticateConnectionFetchMyProfile();
            try {
                mAuth1.connection();
            } catch (Exception e) {
                exceptioncheck = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (exceptioncheck) {
                exceptioncheck = false;
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (myprofileresp.contains("Unauthorized Access")) {
                Log.e("HomeActivity", "myprofileresp Unauthorized Access");
                Toast.makeText(NewRideCreationScreen.this,
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
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/fetchmyprofile.php";
            HttpPost httpPost = new HttpPost(url_select);
            BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair("UserNumber", MobileNumber);
            String authString = MobileNumber;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth", GlobalMethods.calculateCMCAuthString(authString));
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(UserNumberBasicNameValuePair);
            nameValuePairList.add(authValuePair);
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("httpResponse", "" + httpResponse);
            InputStream inputStream = httpResponse.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk = null;
            myprofileresp = null;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                myprofileresp = stringBuilder.append(bufferedStrChunk).toString();
            }
            Log.d("myprofileresp", "" + myprofileresp);
            if (!myprofileresp.contains("Unauthorized Access")) {
                SharedPreferences sharedPreferences1 = getSharedPreferences(
                        "MyProfile", 0);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("myprofile", myprofileresp.toString().trim());
                editor1.commit();
            }
        }
    }

    /**
     * Request for Public Group--------------->
     */

    private void showProgressBar(){
        try{
            onedialog = new Dialog(NewRideCreationScreen.this);
            onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            onedialog.setCancelable(false);
            onedialog.setContentView(R.layout.dialog_ishare_loader);
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
    private class SendInviteToPublicGrpTask extends AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;


        @Override
        protected void onPreExecute() {
          showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateSendInvitePublicGrp mAuth1 = new AuthenticateSendInvitePublicGrp();
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

            if (exceptioncheck) {
                exceptioncheck = false;
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!TextUtils.isEmpty(publicRideResponse)
                    && publicRideResponse.contains("Unauthorized Access")) {
                Log.e("NewRideCreationScreen",
                        "SendInvite Unauthorized Access");
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    NewRideCreationScreen.this);
            builder.setTitle("Success!");
            builder.setMessage("Sit back and relax. We will inform you when riders join.");
            builder.setCancelable(false);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            scheduleUpcomingTripNotification();
                            //scheduleStartTripNotification();
                            Intent mainIntent = new Intent(
                                    NewRideCreationScreen.this,
                                    NewHomeScreen.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mainIntent.putExtra("comefrom", "newridecreation");
                            mainIntent.putExtra("cabID", "-1");// This is false condition for restrictiong my rides to not open checkpool (Map for owner)
                            startActivity(mainIntent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
							/*Intent mainIntent = new Intent(
									NewRideCreationScreen.this,
									MyRidesActivity.class);

							mainIntent.putExtra("comefrom", "comefrom");
							mainIntent.putExtra("cabID", CabId);

							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivityForResult(mainIntent, 500);
							overridePendingTransition(R.anim.slide_in_right,
									R.anim.slide_out_left);*/
                        }
                    });

            builder.show();

        }
        public class AuthenticateSendInvitePublicGrp {

            public AuthenticateSendInvitePublicGrp() {

            }

            public void connection() throws Exception {
                String fromlatLong = fAddress.getLatitude()+","+fAddress.getLongitude();
                String tolatLong = tAddress.getLatitude()+","+tAddress.getLongitude();
               /* String source = from_places.getText().toString().trim()
                        .replaceAll(" ", "%0A").replaceAll("\n","%0A");

                String dest = to_places.getText().toString().trim()
                        .replaceAll(" ", "%0A").replaceAll("\n","%0A");;
*/
                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + fromlatLong
                        + "&destination="
                        + tolatLong
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
                distancetext = null;

                String durationvalue = null;
                String durationtext = null;
                double tempDistance = 0;

                for (int i = 0; i < subArray.length(); i++) {

                    String name1 = subArray.getJSONObject(i).getString("legs")
                            .toString();

                    JSONArray subArray1 = new JSONArray(name1);

                    for (int i1 = 0; i1 < subArray1.length(); i1++) {

                        String startadd = subArray1.getJSONObject(i1)
                                .getString("distance").toString();
                        String startadd1 = subArray1.getJSONObject(i1)
                                .getString("duration").toString();

                        JSONObject jsonObject1 = new JSONObject(startadd);
                        if(i == 0){
                            tempDistance = Double.parseDouble(jsonObject1.getString("value"));
                            distancevalue = jsonObject1.getString("value");
                            distancetext = jsonObject1.getString("text");
                            JSONObject jsonObject11 = new JSONObject(startadd1);
                            durationvalue = jsonObject11.getString("value");
                            durationtext = jsonObject11.getString("text");
                        }else {
                            if(Double.parseDouble(jsonObject1.getString("value")) < tempDistance ){
                                tempDistance = Double.parseDouble(jsonObject1.getString("value"));
                                distancevalue = jsonObject1.getString("value");
                                distancetext = jsonObject1.getString("text");
                                JSONObject jsonObject11 = new JSONObject(startadd1);
                                durationvalue = jsonObject11.getString("value");
                                durationtext = jsonObject11.getString("text");
                            }
                        }

                    }
                }
                if(TextUtils.isEmpty(distancetext)){
                    exceptioncheck = true;
                    return;
                }

                if(!TextUtils.isEmpty(distancevalue) && Integer.parseInt(distancevalue) == 0){
                    exceptioncheck = true;
                    return;
                }

                Log.d("distancevalue", "" + distancevalue);
                Log.d("distancetext", "" + distancetext);

                Log.d("durationvalue", "" + durationvalue);
                Log.d("durationtext", "" + durationtext);

                String msg = FullName + " invited you to share a cab from "
                        + fromshortname + " to " + toshortname;

                String screentoopen = getIntent().getStringExtra("screentoopen");
                String perKmCharge = "0";
                if (screentoopen
                        .equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {
                /*if (checkBoxForFree.isChecked()) {
                    perKmCharge = "0";
                } else {
                    perKmCharge = textViewPricePerKm.getText().toString();
                }*/
                    msg = FullName + " invited you to join a car pool from "
                            + fromshortname + " to " + toshortname + " at Rs."
                            + perKmCharge + " per Km";
                }

                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/openacabPublic.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                        "CabId", CabId);
                BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
                        "MobileNumber", MobileNumber);
                BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
                        "OwnerName", FullName);
                BasicNameValuePair FromLocationBasicNameValuePair = new BasicNameValuePair(
                        "FromLocation", from_places.getText().toString());
                BasicNameValuePair ToLocationBasicNameValuePair = new BasicNameValuePair(
                        "ToLocation", to_places.getText().toString().trim());

                BasicNameValuePair FromShortNameBasicNameValuePair;
                BasicNameValuePair ToShortNameBasicNameValuePair;

                if (fromshortname == null || fromshortname.equalsIgnoreCase("")
                        || fromshortname.isEmpty()) {

                    FromShortNameBasicNameValuePair = new BasicNameValuePair(
                            "FromShortName", from_places.getText().toString());
                } else {

                    FromShortNameBasicNameValuePair = new BasicNameValuePair(
                            "FromShortName", fromshortname);
                }

                if (toshortname == null || toshortname.equalsIgnoreCase("")
                        || toshortname.isEmpty()) {

                    ToShortNameBasicNameValuePair = new BasicNameValuePair(
                            "ToShortName", to_places.getText().toString());
                } else {

                    ToShortNameBasicNameValuePair = new BasicNameValuePair(
                            "ToShortName", toshortname);
                }
                String rideDistance = String.valueOf(Double.parseDouble(distancevalue)/1000);

                BasicNameValuePair TravelDateBasicNameValuePair = new BasicNameValuePair(
                        "TravelDate", travelDate);
                BasicNameValuePair TravelTimeBasicNameValuePair = new BasicNameValuePair(
                        "TravelTime", travelTime);
                BasicNameValuePair SeatsBasicNameValuePair = new BasicNameValuePair(
                        "Seats", String.valueOf(seatCount));
                BasicNameValuePair RemainingSeatsBasicNameValuePair = new BasicNameValuePair(
                        "RemainingSeats", String.valueOf(seatCount));
                BasicNameValuePair DistanceBasicNameValuePair = new BasicNameValuePair(
                        "Distance", rideDistance);

                BasicNameValuePair durationvalueBasicNameValuePair = new BasicNameValuePair(
                        "ExpTripDuration", durationvalue);

                BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
                        "MembersNumber", selectednumbers.toString());
                BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
                        "MembersName", selectednames.toString());
                BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
                        "Message", msg);

                String rideType ="";
                if (screentoopen
                        .equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {// From Rides Available
                    rideType = "4";
                }else{// Cabs Fragment (Cab Share)
                    rideType = "5";
                }

                BasicNameValuePair RideTypeNameBasicNameValuePair = new BasicNameValuePair(
                        "rideType", rideType);
                BasicNameValuePair PerKmChargeBasicNameValuePair = new BasicNameValuePair(
                        "perKmCharge", perKmCharge);
                BasicNameValuePair fromLatLongPair = new BasicNameValuePair(
                        "sLatLon",fromlatLong);
                BasicNameValuePair toLatLongPair = new BasicNameValuePair(
                        "eLatLon", tolatLong);
			/*if (screentoopen
					.equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {
				rideType = "1";
				RideTypeNameBasicNameValuePair = new BasicNameValuePair(
						"rideType", rideType);// Carpool Public
				PerKmChargeBasicNameValuePair = new BasicNameValuePair(
						"perKmCharge", perKmCharge);
			}*/

                Log.d("ContactsToInviteForRideActivity",
                        "AuthenticateConnectionSendInvite : "
                                + GlobalVariables.ServiceUrl + "/openacab.php"
                                + " CabId : " + CabId + " MobileNumber : "
                                + MobileNumber + " OwnerName : " + FullName
                                + " FromLocation : "
                                + from_places.getText().toString()
                                + " ToLocation : " + to_places.getText().toString()
                                + " FromShortName : " + fromshortname
                                + " ToShortName : " + toshortname
                                + " TravelDate : "
                                + travelDate
                                + " TravelTime : "
                                + travelTime
                                + " Seats : " + seatCount + " RemainingSeats : "
                                + seatCount + " Distance : " + distancetext
                                + " ExpTripDuration : " + durationvalue
                                + " MembersNumber : " + selectednumbers.toString()
                                + "MemberName:" + selectednames.toString()
                                + " screentoopen : "
                                + getIntent().getStringExtra("screentoopen")
                                + " rideType : " + rideType + " perKmCharge : "
                                + perKmCharge);

                String authString = CabId
                        + rideDistance
                        + durationvalue
                        + from_places.getText().toString()
                        + ((fromshortname == null
                        || fromshortname.equalsIgnoreCase("") || fromshortname
                        .isEmpty()) ? from_places.getText().toString()
                        : fromshortname)
                        + selectednames.toString()
                        + selectednumbers.toString()
                        + msg
                        + MobileNumber
                        + FullName
                        + perKmCharge
                        + seatCount
                        + rideType
                        + seatCount
                        + to_places.getText().toString()
                        + ((toshortname == null || toshortname.equalsIgnoreCase("") || toshortname
                        .isEmpty()) ? to_places.getText().toString()
                        : toshortname)
                        + travelDate
                        + travelTime+fromlatLong+tolatLong;
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(CabIdBasicNameValuePair);
                nameValuePairList.add(MobileNumberBasicNameValuePair);
                nameValuePairList.add(OwnerNameBasicNameValuePair);
                nameValuePairList.add(FromLocationBasicNameValuePair);
                nameValuePairList.add(ToLocationBasicNameValuePair);
                nameValuePairList.add(FromShortNameBasicNameValuePair);
                nameValuePairList.add(ToShortNameBasicNameValuePair);
                nameValuePairList.add(TravelDateBasicNameValuePair);
                nameValuePairList.add(TravelTimeBasicNameValuePair);
                nameValuePairList.add(SeatsBasicNameValuePair);
                nameValuePairList.add(RemainingSeatsBasicNameValuePair);
                nameValuePairList.add(DistanceBasicNameValuePair);
                nameValuePairList.add(durationvalueBasicNameValuePair);
                nameValuePairList.add(MembersNumberBasicNameValuePair);
                nameValuePairList.add(MembersNameBasicNameValuePair);
                nameValuePairList.add(MessageBasicNameValuePair);
                nameValuePairList.add(RideTypeNameBasicNameValuePair);
                nameValuePairList.add(PerKmChargeBasicNameValuePair);
                nameValuePairList.add(authValuePair);
                nameValuePairList.add(fromLatLongPair);
                nameValuePairList.add(toLatLongPair);

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
                    publicRideResponse = stringBuilder.append(bufferedStrChunk).toString();
                }

                Log.d("sendres", "" + stringBuilder.toString());
            }
        }

    }



    private class ConnectionTaskForSendInvite extends
            AsyncTask<String, Void, Void> {
        private boolean exceptioncheck;


        @Override
        protected void onPreExecute() {
           showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionSendInvite mAuth1 = new AuthenticateConnectionSendInvite();
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

            if (exceptioncheck) {
                exceptioncheck = false;
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (privateRideResponse != null && privateRideResponse.length() > 0
                    && privateRideResponse.contains("Unauthorized Access")) {
                Log.e("InviteFragmentActivity",
                        "SendInvite Unauthorized Access");
                Toast.makeText(NewRideCreationScreen.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    NewRideCreationScreen.this);
            builder.setMessage("Your friend(s) have been informed about the ride! We will let you know when they join. Sit back & relax!");
            builder.setCancelable(false);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            scheduleUpcomingTripNotification();
                            //scheduleStartTripNotification();

                            Intent mainIntent = new Intent(
                                    NewRideCreationScreen.this,
                                    NewHomeScreen.class);

                            mainIntent.putExtra("comefrom", "comefrom");
                            mainIntent.putExtra("cabID", "-1");// false condition can be improved

                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivityForResult(mainIntent, 500);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                        }
                    });

            builder.show();

        }
        public class AuthenticateConnectionSendInvite {

            public AuthenticateConnectionSendInvite() {

            }

            public void connection() throws Exception {
                String fromlatLong = fAddress.getLatitude()+","+fAddress.getLongitude();
                String tolatLong = tAddress.getLatitude()+","+tAddress.getLongitude();
               /* String source = from_places.getText().toString().trim()
                        .replaceAll(" ", "%20").replaceAll("\n","%0A");
                String dest = to_places.getText().toString().trim()
                        .replaceAll(" ", "%20").replaceAll("\n","%0A");*/

                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + fromlatLong
                        + "&destination="
                        + tolatLong
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
                distancetext = null;

                String durationvalue = null;
                String durationtext = null;
                double tempDistance = 0;
                for (int i = 0; i < subArray.length(); i++) {

                    String name1 = subArray.getJSONObject(i).getString("legs")
                            .toString();

                    JSONArray subArray1 = new JSONArray(name1);

                    for (int i1 = 0; i1 < subArray1.length(); i1++) {

                        String startadd = subArray1.getJSONObject(i1).getString("distance").toString();
                        String startadd1 = subArray1.getJSONObject(i1).getString("duration").toString();

                        JSONObject jsonObject1 = new JSONObject(startadd);
                        if(i == 0){
                            tempDistance = Double.parseDouble(jsonObject1.getString("value"));
                            distancevalue = jsonObject1.getString("value");
                            distancetext = jsonObject1.getString("text");
                            JSONObject jsonObject11 = new JSONObject(startadd1);
                            durationvalue = jsonObject11.getString("value");
                            durationtext = jsonObject11.getString("text");
                        }else {
                            if(Double.parseDouble(jsonObject1.getString("value")) < tempDistance ){
                                tempDistance = Double.parseDouble(jsonObject1.getString("value"));
                                distancevalue = jsonObject1.getString("value");
                                distancetext = jsonObject1.getString("text");
                                JSONObject jsonObject11 = new JSONObject(startadd1);
                                durationvalue = jsonObject11.getString("value");
                                durationtext = jsonObject11.getString("text");
                            }
                        }
                    }
                }

                Log.d("distancevalue", "" + distancevalue);
                Log.d("distancetext", "" + distancetext);

                Log.d("durationvalue", "" + durationvalue);
                Log.d("durationtext", "" + durationtext);
                if(TextUtils.isEmpty(distancevalue) ){
                    exceptioncheck = true;
                    return;
                }

                if(!TextUtils.isEmpty(distancevalue) && Integer.parseInt(distancevalue) == 0){
                    exceptioncheck = true;
                    return;
                }



                String msg = FullName + " invited you to share a cab from "
                        + fromshortname + " to " + toshortname;

                String screentoopen = getIntent().getStringExtra("screentoopen");
                String perKmCharge = "0";
                if (screentoopen
                        .equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {
               /* if (checkBoxForFree.isChecked()) {
                    perKmCharge = "0";
                } else {
                    perKmCharge = textViewPricePerKm.getText().toString();
                }*/
                    msg = FullName + " has invited you to join a car pool from "
                            + fromshortname + " to " + toshortname;
                }

                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/openacab.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
                        "CabId", CabId);
                BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
                        "MobileNumber", MobileNumber);
                BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
                        "OwnerName", FullName);
                BasicNameValuePair FromLocationBasicNameValuePair = new BasicNameValuePair(
                        "FromLocation", from_places.getText().toString());
                BasicNameValuePair ToLocationBasicNameValuePair = new BasicNameValuePair(
                        "ToLocation", to_places.getText().toString().trim());

                BasicNameValuePair FromShortNameBasicNameValuePair;
                BasicNameValuePair ToShortNameBasicNameValuePair;

                if (fromshortname == null || fromshortname.equalsIgnoreCase("")
                        || fromshortname.isEmpty()) {

                    FromShortNameBasicNameValuePair = new BasicNameValuePair(
                            "FromShortName", from_places.getText().toString());
                } else {

                    FromShortNameBasicNameValuePair = new BasicNameValuePair(
                            "FromShortName", fromshortname);
                }

                if (toshortname == null || toshortname.equalsIgnoreCase("")
                        || toshortname.isEmpty()) {

                    ToShortNameBasicNameValuePair = new BasicNameValuePair(
                            "ToShortName", to_places.getText().toString());
                } else {

                    ToShortNameBasicNameValuePair = new BasicNameValuePair(
                            "ToShortName", toshortname);
                }
                String rideDistance = String.valueOf(Double.parseDouble(distancevalue)/1000);

                BasicNameValuePair TravelDateBasicNameValuePair = new BasicNameValuePair(
                        "TravelDate", travelDate);
                BasicNameValuePair TravelTimeBasicNameValuePair = new BasicNameValuePair(
                        "TravelTime", travelTime);
                BasicNameValuePair SeatsBasicNameValuePair = new BasicNameValuePair(
                        "Seats", String.valueOf(seatCount));
                BasicNameValuePair RemainingSeatsBasicNameValuePair = new BasicNameValuePair(
                        "RemainingSeats", String.valueOf(seatCount));
                BasicNameValuePair DistanceBasicNameValuePair = new BasicNameValuePair(
                        "Distance", rideDistance);

                BasicNameValuePair durationvalueBasicNameValuePair = new BasicNameValuePair(
                        "ExpTripDuration", durationvalue);

                BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
                        "MembersNumber", selectednumbers.toString());
                BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
                        "MembersName", selectednames.toString());
                BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
                        "Message", msg);

                String rideType = "";
                if (screentoopen
                        .equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {// From Rides Available
                    rideType = "1";
                }else{// Cabs Fragment (Cab Share)
                    rideType = "2";
                }
                BasicNameValuePair RideTypeNameBasicNameValuePair = new BasicNameValuePair(
                        "rideType", rideType);
                BasicNameValuePair PerKmChargeBasicNameValuePair = new BasicNameValuePair(
                        "perKmCharge", perKmCharge);
                BasicNameValuePair fromLatLongPair = new BasicNameValuePair(
                        "sLatLon",fromlatLong);
                BasicNameValuePair toLatLongPair = new BasicNameValuePair(
                        "eLatLon", tolatLong);
			/*if (screentoopen
					.equalsIgnoreCase(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {
				rideType = "1";
				RideTypeNameBasicNameValuePair = new BasicNameValuePair(
						"rideType", rideType);
				PerKmChargeBasicNameValuePair = new BasicNameValuePair(
						"perKmCharge", perKmCharge);
			}*/

                Log.d("ContactsToInviteForRideActivity",
                        "AuthenticateConnectionSendInvite : "
                                + GlobalVariables.ServiceUrl + "/openacab.php"
                                + " CabId : " + CabId + " MobileNumber : "
                                + MobileNumber + " OwnerName : " + FullName
                                + " FromLocation : "
                                + from_places.getText().toString()
                                + " ToLocation : " + to_places.getText().toString()
                                + " FromShortName : " + fromshortname
                                + " ToShortName : " + toshortname
                                + " TravelDate : "
                                + travelDate
                                + travelTime
                                + " Seats : " + seatCount + " RemainingSeats : "
                                + seatCount + " Distance : " + rideDistance
                                + " ExpTripDuration : " + durationvalue
                                + " MembersNumber : " + selectednumbers.toString()
                                + "MemberName:" + selectednames.toString()
                                + " screentoopen : "
                                + getIntent().getStringExtra("screentoopen")
                                + " rideType : " + rideType + " perKmCharge : "
                                + perKmCharge);

                String authString = CabId
                        + rideDistance
                        +tolatLong
                        + durationvalue
                        + from_places.getText().toString()
                        + ((fromshortname == null
                        || fromshortname.equalsIgnoreCase("") || fromshortname
                        .isEmpty()) ? from_places.getText().toString()
                        : fromshortname)
                        + selectednames.toString()
                        + selectednumbers.toString()
                        + msg
                        + MobileNumber
                        + FullName
                        + perKmCharge
                        + seatCount
                        + rideType
                        + seatCount+fromlatLong
                        + to_places.getText().toString()
                        + ((toshortname == null || toshortname.equalsIgnoreCase("") || toshortname
                        .isEmpty()) ? to_places.getText().toString()
                        : toshortname)
                        + travelDate
                        + travelTime;;
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(CabIdBasicNameValuePair);
                nameValuePairList.add(MobileNumberBasicNameValuePair);
                nameValuePairList.add(OwnerNameBasicNameValuePair);
                nameValuePairList.add(FromLocationBasicNameValuePair);
                nameValuePairList.add(ToLocationBasicNameValuePair);

                nameValuePairList.add(FromShortNameBasicNameValuePair);
                nameValuePairList.add(ToShortNameBasicNameValuePair);

                nameValuePairList.add(TravelDateBasicNameValuePair);
                nameValuePairList.add(TravelTimeBasicNameValuePair);
                nameValuePairList.add(SeatsBasicNameValuePair);
                nameValuePairList.add(RemainingSeatsBasicNameValuePair);
                nameValuePairList.add(DistanceBasicNameValuePair);
                nameValuePairList.add(durationvalueBasicNameValuePair);
                nameValuePairList.add(MembersNumberBasicNameValuePair);
                nameValuePairList.add(MembersNameBasicNameValuePair);
                nameValuePairList.add(MessageBasicNameValuePair);

                nameValuePairList.add(RideTypeNameBasicNameValuePair);
                nameValuePairList.add(PerKmChargeBasicNameValuePair);
                nameValuePairList.add(fromLatLongPair);
                nameValuePairList.add(toLatLongPair);
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
                    privateRideResponse = stringBuilder.append(bufferedStrChunk).toString();
                }

                Log.d("sendres", "" + stringBuilder.toString());
            }
        }

    }



    /**
     * Networ Response-------------------------------------------------->
     */
    @Override
    public void getResult(String response, String uniqueID) {
        if (uniqueID.equals("FetchUnreadNotificationCount")) {
            if (response != null && response.length() > 0
                    && response.contains("Unauthorized Access")) {
                Log.e("HomeActivity",
                        "FetchUnreadNotificationCount Unauthorized Access");
                Toast.makeText(NewRideCreationScreen.this,
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
        }
    }

    private boolean isValidRequest() {
        try{
            if(addressModelTo ==null || addressModelFrom == null){
                Toast.makeText(
                        NewRideCreationScreen.this,
                        "Please enter both from & to locations",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if(addressModelTo.getAddress() == null || addressModelFrom.getAddress() == null){
                Toast.makeText(NewRideCreationScreen.this, "Please select another location", Toast.LENGTH_LONG).show();
                return false;
            }
            Location locationA = new Location("Start point");

            locationA.setLatitude(addressModelFrom.getAddress()
                    .getLatitude());
            locationA.setLongitude(addressModelFrom.getAddress()
                    .getLongitude());

            Location locationB = new Location("End point");

            locationB.setLatitude(addressModelTo.getAddress()
                    .getLatitude());
            locationB.setLongitude(addressModelTo.getAddress()
                    .getLongitude());

            float distance = locationA.distanceTo(locationB);

            if (distance < FromToMinDestance) {
                new AlertDialog.Builder(NewRideCreationScreen.this)
                        .setTitle("")
                        .setMessage(
                                "From and To locations for this trip are too close. Please try with diffrent locations")
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        // continue with delete
                                        dialog.cancel();

                                    }
                                })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            } else {

                String screentoopen = getIntent().getStringExtra(
                        "screentoopen");
                if (screentoopen.equals(HOME_ACTIVITY_SHARE_CAB)
                        || screentoopen.equals(HOME_ACTIVITY_CAR_POOL)) {
                    isCallresetIntentParams = false;
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("ClubMyCab Click")
                            .setAction("ClubMyCab Click")
                            .setLabel("ClubMyCab Click").build());

                    logger.logEvent("HomePage ClubMyCab Click");
                    if (addressModelFrom != null
                            && addressModelTo != null) {

                    } else {
                        Toast.makeText(
                                NewRideCreationScreen.this,
                                "Please enter both from & to locations",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  false;
    }
    
    private void sendRideCreationRequest(){
/*

        Animation animScale = AnimationUtils.loadAnimation(
                NewRideCreationScreen.this,
                R.anim.button_click_anim);
        findViewById(R.id.llSendInvite).startAnimation(animScale);
*/

        Handler mHandler2 = new Handler();
        Runnable mRunnable2 = new Runnable() {
            @Override
            public void run() {

                if (fAddress == null) {

                    from_places.requestFocus();

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            NewRideCreationScreen.this);

                    builder.setMessage("Please Enter From Location. If you have already selected a location on map please try again by selecting a nearby location");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                } else if (tAddress == null) {

                    to_places.requestFocus();

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            NewRideCreationScreen.this);

                    builder.setMessage("Please Enter To Location. If you have already selected a location on map please try again by selecting a nearby location");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                } else if (TextUtils.isEmpty(travelDate)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            NewRideCreationScreen.this);

                    builder.setMessage("Please Enter Date");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                }

                else if (TextUtils.isEmpty(travelTime)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            NewRideCreationScreen.this);

                    builder.setMessage("Please Enter Time");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView) dialog
                            .findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    dialog.show();

                }
                else  {

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd/MM/yyyy hh:mm aa");

                    Date currentTime = new Date();
                    Date rideTime = new Date();
                    try {
                        rideTime = dateFormat.parse(travelDate
                                + " "
                                + travelTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("NewRideCreationScreen",
                            "Invite click currentTime : "
                                    + currentTime + " rideTime : "
                                    + rideTime);

                    if (rideTime.compareTo(currentTime) < 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                NewRideCreationScreen.this);
                        builder.setMessage("The time entered is before the current time");
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog = builder.show();
                        TextView messageText = (TextView) dialog
                                .findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.CENTER);
                        dialog.show();
                    } else {

                        Log.d("fromshortname", "" + fromshortname);
                        Log.d("toshortname", "" + toshortname);

                        CabId = MobileNumber
                                + System.currentTimeMillis();
                        String OwnerName = FullName;
                        // Changedv2
                        if (checkPubToPrvt.isChecked()) {// Publish
                            // Only
                            // to
                            // Private
                            // Groups/Contacts
                            Intent mainIntent = new Intent(
                                    NewRideCreationScreen.this,
                                    SendInvitesToOtherScreen.class);
                            mainIntent
                                    .putExtra(
                                            "activity_id",
                                            SendInvitesToOtherScreen.INVITE_FRAGMENT_ACTIVTY_ID);

                            /*if (getIntent()
                                    .getStringExtra("screentoopen")
                                    .equals(HomeActivity.HOME_ACTIVITY_CAR_POOL)) {
                                if (checkBoxForFree.isChecked()) {
                                    mainIntent.putExtra(
                                            "perKmCharge", "0");
                                } else {
                                    String charge = textViewPricePerKm
                                            .getText().toString();
                                    if (charge.isEmpty()
                                            || charge.length() <= 0
                                            || charge.equals("0")
                                            || Integer
                                            .parseInt(charge) <= 0) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                NewRideCreationScreen.this);
                                        builder.setMessage("Please enter a valid per seat charge");
                                        builder.setCancelable(false);

                                        builder.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {

                                                    }
                                                });

                                        builder.show();
                                        return;
                                    } else {
                                        mainIntent.putExtra(
                                                "perKmCharge",
                                                charge);
                                    }
                                }
                            }*/

                            startActivityForResult(mainIntent, CONTACTS_GROUP_REQUEST);
                            overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                        } else {// Publish To Public Groups
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                new SendInviteToPublicGrpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                new SendInviteToPublicGrpTask().execute();
                            }
                        }

                    }

                    // Log.d("Invite : ", "Date comparison : " +
                    // rideTime.compareTo(currentTime) +
                    // " currentTime : " + currentTime.toString() +
                    // " rideTime : " + rideTime.toString());

                }
            }
        };
        mHandler2.postDelayed(mRunnable2, 500);

    }

    private void sendInviteRequest(final boolean isGrpFrmContact, final ArrayList<ContactData> contactList, final ArrayList<GroupDataModel> groupList) {

        Handler mHandler2 = new Handler();
        Runnable mRunnable2 = new Runnable() {
            @Override
            public void run() {

                selectednames.clear();
                selectednumbers.clear();

                // Retrive Data from list
                if (isGrpFrmContact) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (ContactData bean : contactList) {
                        // duplicacy check, my number check is left currently
                        map.put(bean.getPhoneNumber().replace(" ", ""),
                                bean.getName());
                        L.mesaage(bean.getPhoneNumber().length() + "");
                    }
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        String number = String.valueOf(pair.getKey());
                        int length = number.length();
                        L.mesaage(length + "");
                        it.remove(); // avoids a ConcurrentModificationException
                        selectednames.add((String) pair.getValue());
                        selectednumbers.add("0091"
                                + number.substring(number.length() - 10));

                    }
                    L.mesaage(selectednames.toString() + " , "
                            + selectednumbers.toString());
                } else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (GroupDataModel bean : groupList) {
                        if (!bean.getOwnerNumber().equals(MobileNumber)) {
                            map.put(bean.getOwnerNumber(), bean.getOwnerName());
                        }
                        if (bean.getMemberList() != null) {
                            ArrayList<MemberModel> subArray = bean
                                    .getMemberList();
                            for (int i = 0; i < subArray.size(); i++) {
                                if (!subArray.get(i).getMemberNumber()
                                        .equals(MobileNumber)) {
                                    map.put(subArray.get(i).getMemberNumber(),
                                            subArray.get(i).getMemberName());
                                }
                            }
                        }
                    }
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        System.out.println(pair.getKey() + " = "
                                + pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                        selectednames.add((String) pair.getValue());
                        selectednumbers.add(((String) pair.getKey()));

                    }
                    L.mesaage(selectednames.toString() + " , "
                            + selectednumbers.toString());

                }

                if (selectednames.size() > 0) {

                    Log.d("selectednames", "" + selectednames);
                    Log.d("selectednumbers", "" + selectednumbers);

                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Invite").setAction("Invite")
                            .setLabel("Invite").build());

                    if (selectednames.size() >= seatCount) {
                        // conitnuechk = false;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new ConnectionTaskForSendInvite()
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new ConnectionTaskForSendInvite().execute();
                        }

                    } else {

                        // conitnuechk = true;
                        int remainingSeats = seatCount
                                - selectednames.size();
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                NewRideCreationScreen.this);
                        builder.setMessage("You have " + seatCount
                                + " seats to share and have selected only "
                                + selectednames.size() + " friend(s)");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Continue Anyways",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                            new ConnectionTaskForSendInvite()
                                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        } else {
                                            new ConnectionTaskForSendInvite()
                                                    .execute();
                                        }
                                    }
                                });
                        dialogseats = builder.show();
                        TextView messageText = (TextView) dialogseats
                                .findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.CENTER);
                        dialogseats.show();
                    }

                } else {
                    Toast.makeText(NewRideCreationScreen.this,
                            "Please select Groups/Contacts to invite",
                            Toast.LENGTH_LONG).show();
                }

            }
        };
        mHandler2.postDelayed(mRunnable2, 500);

    }

    private void scheduleUpcomingTripNotification() {
        String tripTime = travelDate + " " + travelTime;

        UpcomingStartTripAlarm upcomingStartTripAlarm = new UpcomingStartTripAlarm();
        upcomingStartTripAlarm.setAlarm(NewRideCreationScreen.this, tripTime,
                UpcomingStartTripAlarm.ALARM_TYPE_UPCOMING, CabId,
                fromshortname, toshortname);

    }

    private Address getCodeAddressByLatLong(LatLng locationModel){
        Geocoder geo = new Geocoder(NewRideCreationScreen.this);
        Address _address = null;
        List<Address> listAddresses = null;
        int count = 0;

        while (listAddresses == null && count <2){
            count++;
            try {
                if (locationModel != null) {
                    listAddresses = geo.getFromLocation(locationModel.latitude,
                            locationModel.longitude, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        if ((listAddresses != null) && (listAddresses.size() > 0)) {
             _address = listAddresses.get(0);

        }
        return  _address;
    }


}
