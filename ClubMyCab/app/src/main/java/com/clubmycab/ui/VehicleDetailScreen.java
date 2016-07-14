package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.clubmycab.utility.Utility;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

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
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleDetailScreen extends Activity implements View.OnClickListener, GlobalAsyncTask.AsyncTaskResultListener {
    private AutoCompleteTextView etModelName;
    private boolean exceptioncheck;
    private ArrayList<String> vehicleNameList = new ArrayList<String>();
    private ArrayList<String> vehicleIDList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String searchString = "";
    private int selectedPosition = -1;
    private EditText etRegistrationNo;
    private String addVehicalResul;
    private String screenOption;
    private Tracker tracker;
    private AppEventsLogger logger;
    private CircularImageView profilepic;
    private ImageView notificationimg;
    private CircleImageView drawerprofilepic;
    private String FullName, MobileNumber, myprofileresp, imagenameresp;
    private TextView username, drawerusername, unreadnoticount;
    private RelativeLayout unreadnoticountrl;


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            searchString = charSequence.toString();
            selectedPosition = -1;
            if (vehicleIDList != null && vehicleIDList.size() == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    //resetRequest();
                    new ConnectionTaskGetModel()
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new ConnectionTaskGetModel().execute();
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private Dialog onedialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_detail_screen);
        findViewById(R.id.mainLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        if (!isOnline()) {
            return;
        }
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("screentoopen")) {
                screenOption = bundle.getString("screentoopen");
            }
        }
        ((TextView) findViewById(R.id.textView7)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.textView8)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvRegno)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.textView9)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.textView2)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.btnSubmit)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((AutoCompleteTextView) findViewById(R.id.etModel)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));
        ((EditText) findViewById(R.id.etRegistrationNo)).setTypeface(FontTypeface.getTypeface(VehicleDetailScreen.this, AppConstants.HELVITICA));

        etModelName = (AutoCompleteTextView) findViewById(R.id.etModel);
        etRegistrationNo = (EditText) findViewById(R.id.etRegistrationNo);
        etModelName.setThreshold(1);
        etModelName.addTextChangedListener(textWatcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //resetRequest();
            new ConnectionTaskGetModel()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskGetModel().execute();
        }
        etModelName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                selectedPosition = vehicleNameList.indexOf(selection);
                Utility.hideSoftKeyboard(VehicleDetailScreen.this);

            }
        });
        CheckBox ch1 = (CheckBox) findViewById(R.id.ch2);
        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    findViewById(R.id.btnSubmit).setBackgroundResource(R.drawable.bg_btn_background);

                } else {
                    findViewById(R.id.btnSubmit).setEnabled(false);
                    findViewById(R.id.btnSubmit).setBackgroundColor(getResources().getColor(R.color.color_app_text_light));

                }
            }
        });
        initializeDarawer();
        setNotificationAndProfileImage();


    }

    private void initializeDarawer() {
        GoogleAnalytics analytics = GoogleAnalytics
                .getInstance(VehicleDetailScreen.this);
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

                Intent mainIntent = new Intent(VehicleDetailScreen.this,
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
        AlertDialog.Builder builder = new AlertDialog.Builder(VehicleDetailScreen.this);
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
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (!isOnline()) {
                    Toast.makeText(VehicleDetailScreen.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
                if (isValidRequest()) {

                    Utility.hideSoftKeyboard(VehicleDetailScreen.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        //resetRequest();
                        new SaveVehicleDetailTask()
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new SaveVehicleDetailTask().execute();
                    }
                }
                break;
        }
    }

    private boolean isValidRequest() {
        if (TextUtils.isEmpty(etModelName.getText().toString().trim()) || !vehicleNameList.contains(etModelName.getText().toString().trim())) {
            etModelName.setError("Please enter a valid vehicle model");
            return false;
        }
        if (TextUtils.isEmpty(etRegistrationNo.getText().toString().trim())) {
            etRegistrationNo.setError("Please enter registration number");
            return false;
        }

        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Wallet Check
     */
    public void getResult(String response, String uniqueID) {
        if (uniqueID.equals("FetchUnreadNotificationCount")) {
            if (response != null && response.length() > 0
                    && response.contains("Unauthorized Access")) {
                Log.e("HomeActivity",
                        "FetchUnreadNotificationCount Unauthorized Access");
                Toast.makeText(VehicleDetailScreen.this,
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

    private class ConnectionTaskGetModel extends AsyncTask<String, Void, Void> {
        private String result;

        @Override
        protected void onPreExecute() {
            vehicleNameList.clear();
            vehicleIDList.clear();
           showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("", "");
            AuthenticateConnectionModel mAuth1 = new AuthenticateConnectionModel();
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
        protected void onPostExecute(Void aVoid) {
            try {
              hideProgressBar();
                if (!TextUtils.isEmpty(result)) {
                    if (exceptioncheck) {
                        exceptioncheck = false;
                        Toast.makeText(VehicleDetailScreen.this,
                                getResources().getString(R.string.exceptionstring),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.isNull("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                        if (!jsonObject.isNull("data")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                vehicleNameList.add(jObj.optString("vehicleModel"));
                                vehicleIDList.add(jObj.optString("id"));

                            }
                            if (vehicleIDList.size() > 0) {
                                adapter = new ArrayAdapter<String>
                                        (VehicleDetailScreen.this, android.R.layout.select_dialog_item, vehicleNameList);
                                etModelName.setThreshold(1);
                                etModelName.setAdapter(adapter);//set
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class AuthenticateConnectionModel {

            public AuthenticateConnectionModel() {

            }

            public void connection() throws Exception {

                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/getVehicle.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair searchBasicNameValuePair = new BasicNameValuePair(
                        "q", searchString);

                String authString = searchString;
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(searchBasicNameValuePair);
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
                    result = stringBuilder.append(bufferedStrChunk).toString();
                }


                Log.d("result", "" + stringBuilder.toString());
            }
        }

    }

    private class SaveVehicleDetailTask extends AsyncTask<String, Void, Void> {
        private String result;

        @Override
        protected void onPreExecute() {
            showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("", "");
            AuthenSaveVehicleDetail mAuth1 = new AuthenSaveVehicleDetail();
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
        protected void onPostExecute(Void aVoid) {
            try {
                hideProgressBar();
                if (!TextUtils.isEmpty(addVehicalResul)) {
                    if (exceptioncheck) {
                        exceptioncheck = false;
                        Toast.makeText(VehicleDetailScreen.this,
                                getResources().getString(R.string.exceptionstring),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(addVehicalResul);
                    if (!jsonObject.isNull("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                       // Toast.makeText(VehicleDetailScreen.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean(AppConstants.IS_VEHICLE_ADDED, true);
                        editor.putString(SPreference.REGISTRATION_NO,etRegistrationNo.getText().toString().trim());
                        editor.putString(SPreference.MODEL_NO, vehicleNameList.get(selectedPosition));
                        editor.putString(SPreference.MODEL_ID, vehicleIDList.get(selectedPosition));
                        editor.putBoolean(SPreference.IS_COMMERCIAL, ((CheckBox) findViewById(R.id.ch1)).isChecked());
                        editor.commit();
                        Intent mainIntent = new Intent(VehicleDetailScreen.this, NewRideCreationScreen.class);
                        mainIntent.putExtra("screentoopen",
                                screenOption);
                        //   startActivityForResult(mainIntent, NewHomeScreen.OFFER_RIDE_REQUEST);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class AuthenSaveVehicleDetail {

            public AuthenSaveVehicleDetail() {

            }

            public void connection() throws Exception {
                SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
                String FullName = mPrefs.getString("FullName", "");
                String MobileNumber = mPrefs.getString("MobileNumber", "");
                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/saveUserVehicleDetails.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair mobilePair = new BasicNameValuePair(
                        "mobileNumber", MobileNumber);
                BasicNameValuePair vehicleIdPair = new BasicNameValuePair(
                        "vehicleId", vehicleIDList.get(selectedPosition));
                int isCommercial = 0;
                if (((CheckBox) findViewById(R.id.ch1)).isChecked()) {
                    isCommercial = 1;
                }
                BasicNameValuePair isCommercialPair = new BasicNameValuePair(
                        "isCommercial", String.valueOf(isCommercial));
                BasicNameValuePair registrationNumberPair = new BasicNameValuePair(
                        "registrationNumber", etRegistrationNo.getText().toString().trim());


                String authString = isCommercial + MobileNumber + etRegistrationNo.getText().toString().trim()
                        + vehicleIDList.get(selectedPosition);
                android.util.Log.d("authString", authString);
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(mobilePair);
                nameValuePairList.add(vehicleIdPair);
                nameValuePairList.add(isCommercialPair);
                nameValuePairList.add(registrationNumberPair);
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
                    addVehicalResul = stringBuilder.append(bufferedStrChunk).toString();
                }


                Log.d("result", "" + stringBuilder.toString());
            }
        }

    }

    private void showProgressBar(){
        try{
            onedialog = new Dialog(VehicleDetailScreen.this);
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
