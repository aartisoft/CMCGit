package com.clubmycab.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CabApplication;
import com.clubmycab.R;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by newpc on 18/4/16.
 */
public class AppLoginScreen extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    private static final String FACEBOOK_LOGIN = "facebook";
    public static final String LINKEDiN_LOGIN = "linkedin";
    public static final String GOOGLE_LOGIN = "google";

    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(id,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    private String PACKAGE ="com.clubmycab";
    private CallbackManager mCallbackManager;
    private boolean exceptioncheck;
    private String result;
    private ProgressDialog dialog12;
    private String userName, userEmail, loginType, profilePicUrl, socialId, socialType;
    private String imageuploadresp;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private TextView textViewTNCLink;
    private String profilePickLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login_screen);
        textViewTNCLink = (TextView) findViewById(R.id.textViewTNCLink);
        textViewTNCLink.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTNCLink.setTextColor(Color.parseColor("#ffffff"));

        if (!isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    AppLoginScreen.this);
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
        initFacebook();
        findViewById(R.id.ivFaceBookLogin).setOnClickListener(this);
        findViewById(R.id.ivLinkedInLogin).setOnClickListener(this);
        findViewById(R.id.ivGoogleLogin).setOnClickListener(this);
        findViewById(R.id.ivWitPhoneLogin).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return;
        }else if(mCallbackManager !=null && mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // -------------linked IN>
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);


    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("f", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("f", "handleSignInResult:" + result.isSuccess());
            userName = acct.getDisplayName();
            userEmail = acct.getEmail();
            socialId = acct.getId();
            profilePicUrl = String.valueOf(acct.getPhotoUrl());

            sendUserData();
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }

    }


    /**
     * Linked In Integration
     */
    private void linkedInIntegration(){
        CabApplication mainApplication = CabApplication.getInstance();
        LISessionManager.getInstance(mainApplication).init(AppLoginScreen.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Log.d("Success", "onAuthSuccess");
                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(AppLoginScreen.this, topCardUrl, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse result) {
                        try {
                            String resultstr = result.getResponseDataAsJson().toString();
                            JSONObject jsonObject = result.getResponseDataAsJson();

                            if(jsonObject.has("emailAddress")){
                                userEmail = jsonObject.getString("emailAddress");
                            }
                            if(jsonObject.has("formattedName")){
                                userName = jsonObject.getString("formattedName");
                            }
                            if(jsonObject.has("id")){
                                socialId = jsonObject.getString("id");
                            }
                            if(jsonObject.has("pictureUrl")){
                                profilePicUrl = jsonObject.getString("pictureUrl");
                            }
                            sendUserData();
                            Log.e("result", resultstr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        Log.d("error", "error");
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.d("Success", "onAuthSuccess" + error.toString());
            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    /**
     * Facebook Integration
     */
    public void initFacebook() {

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Bundle params = new Bundle();
                        params.putString("fields", "id,email,link,name,gender,cover,picture.type(large)");
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        if (response != null) {
                                            try {
                                                JSONObject data = response.getJSONObject();
                                                if(data.has("email")){
                                                    userEmail = data.getString("email");
                                                }
                                                if(data.has("name")){
                                                    userName = data.getString("name");
                                                }
                                                if(data.has("id")){
                                                    socialId = data.getString("id");
                                                }
                                                if (data.has("picture")) {
                                                    profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                                }
                                                if (data.has("link")) {
                                                    profilePickLink = data.getString("link");
                                                }
                                                sendUserData();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(AppLoginScreen.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(AppLoginScreen.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


       /* ImageView btn_fb_login = (ImageView)findViewById(R.id.ivFacebook);

        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(AppLoginScreen.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });*/

    }



    /**
     * Send User Data ==================>
     */
    private void sendUserData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ConnectionTaskForRegister()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskForRegister().execute();
        }
    }

    /**
     * Utility Methods-------------------------------------->
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE ,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("tag", Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP))  ;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("tag", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("tag", e.getMessage(), e);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivFaceBookLogin:
                socialType = FACEBOOK_LOGIN;
                LoginManager.getInstance().logInWithReadPermissions(AppLoginScreen.this, Arrays.asList("public_profile", "user_friends", "email"));
                break;

            case R.id.ivGoogleLogin:
                socialType = GOOGLE_LOGIN;
                signInGoogle();
                break;

            case R.id.ivLinkedInLogin:
                socialType = LINKEDiN_LOGIN;
                linkedInIntegration();
                break;

            case R.id.ivWitPhoneLogin:
                Intent mainIntent = new Intent(
                        AppLoginScreen.this,
                        RegistrationActivity.class);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                finish();
                break;
        }

    }

    private void showProgressDialog(){
        dialog12 = new ProgressDialog(AppLoginScreen.this);
        if(dialog12 == null){
            dialog12.setCancelable(false);
            dialog12.setCanceledOnTouchOutside(false);
            dialog12.setMessage("loginin...");
            dialog12.show();
        }else {
            if(!dialog12.isShowing()){
                dialog12.show();
            }
        }
    }

    private void hideProgressDialog(){
        if(dialog12 != null){
            dialog12.dismiss();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Network Operation----------------------------------------------->
     */
    private class ConnectionTaskForRegister extends
            AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(
                AppLoginScreen.this);

        @Override
        protected void onPreExecute() {
           try{
               if(dialog != null){
                   dialog.setMessage("Please Wait...");
                   dialog.setCancelable(false);
                   dialog.setCanceledOnTouchOutside(false);
                   dialog.show();
               }
           }catch (Exception e){
               e.printStackTrace();
           }

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionRegister mAuth1 = new AuthenticateConnectionRegister();
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
                    Toast.makeText(AppLoginScreen.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (result != null && result.length() > 0
                        && result.contains("Unauthorized Access")) {
                    com.clubmycab.utility.Log.e("AppLoginScreen", "result Unauthorized Access");
                    Toast.makeText(AppLoginScreen.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    if (result != null && !result.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.get("status").toString()
                                .equalsIgnoreCase("SUCCESS")) {
                            if(!jsonObject.isNull("mobileNumber") && !TextUtils.isEmpty(jsonObject.getString("mobileNumber"))){

                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        "FacebookData", 0);
                                SharedPreferences.Editor editor = sharedPreferences
                                        .edit();
                                editor.putString("FullName", userName.toString().trim());
                                editor.putString("Email", userEmail.toString().trim());
                                editor.putString("MobileNumber", jsonObject.getString("mobileNumber").toString().trim());
                                editor.putString("ProfilePicFb", profilePicUrl);
                                editor.commit();

                                if(!TextUtils.isEmpty(profilePicUrl)){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        new ConnectionTaskForImageUpload().executeOnExecutor(
                                                AsyncTask.THREAD_POOL_EXECUTOR, profilePicUrl);
                                    } else {
                                        new ConnectionTaskForImageUpload().execute(profilePicUrl);
                                    }
                                }else {
                                    Intent mainIntent = new Intent(AppLoginScreen.this, NewHomeScreen.class);
                                    mainIntent.putExtra("from", "normal");
                                    mainIntent.putExtra("message", "null");
                                    startActivityForResult(mainIntent, 500);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }
                            }else{
                                PackageInfo pInfo = null;
                                try {
                                    pInfo = getPackageManager().getPackageInfo(
                                            getPackageName(), 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String version = pInfo.versionName;

                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        "FacebookData", 0);
                                SharedPreferences.Editor editor = sharedPreferences
                                        .edit();
                                editor.putString("FullName", userName
                                        .toString().trim());
                                editor.putString("SocialId",socialId);
                      /*  editor.putString("MobileNumber", "0091"
                                + mobileedittext.getText().toString().trim());*/
                                editor.putString("Email", userEmail
                                        .toString().trim());
                                editor.putString("ProfilePicFb", profilePicUrl);
                                editor.putString("verifyotp", "false");
                                editor.putString("LastRegisteredAppVersion", version);
                                editor.commit();

                                Intent i = new Intent(AppLoginScreen.this,
                                        OTPActivity.class);
                                i.putExtra("from", "reg");
                      /*  i.putExtra("mobnum", "0091"
                                + mobileedittext.getText().toString().trim());*/

                                i.putExtra("fullname", userName);
                                i.putExtra("regid", "");
                                i.putExtra("socialid",socialId);
                                i.putExtra("profilepic",profilePicUrl);

                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                finish();
                            }



                        } else {

                            Toast.makeText(AppLoginScreen.this,
                                    jsonObject.get("message").toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public class AuthenticateConnectionRegister {

        public AuthenticateConnectionRegister() {

        }

        public void connection() throws Exception {

            GoogleCloudMessaging gcm = null;
            String regid = null;
            String PROJECT_NUMBER = GlobalVariables.GCMProjectKey;

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging
                            .getInstance(getApplicationContext());
                }
                regid = gcm.register(PROJECT_NUMBER);
                com.clubmycab.utility.Log.d("GCM", "Device registered, ID is " + regid);
            } catch (Exception e) {
                com.clubmycab.utility.Log.e(" registerDevice()", e.getMessage());
            }

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl
                    + "/userregister.php";

            HttpPost httpPost = new HttpPost(url_select);

            BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair("DeviceToken", regid);
            BasicNameValuePair EmailBasicNameValuePair = new BasicNameValuePair("Email",userEmail);
            BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair("FullName", userName);
            BasicNameValuePair platformBasicNameValuePair = new BasicNameValuePair("Platform", "A");
            BasicNameValuePair socialIDPair = new BasicNameValuePair("socialId", socialId);
            BasicNameValuePair socialTypePair = new BasicNameValuePair("socialType", socialType);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            nameValuePairList.add(DeviceTokenBasicNameValuePair);
            nameValuePairList.add(EmailBasicNameValuePair);
            nameValuePairList.add(FullNameBasicNameValuePair);
            nameValuePairList.add(platformBasicNameValuePair);
            nameValuePairList.add(socialIDPair);
            nameValuePairList.add(socialTypePair);



            String authString = regid
                    + userEmail
                    + userName
                    + "A"
                    + socialId+socialType;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            nameValuePairList.add(authValuePair);

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                    nameValuePairList);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            com.clubmycab.utility.Log.d("httpResponse", "" + httpResponse);

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

            com.clubmycab.utility.Log.d("result", "" + stringBuilder.toString());
        }
    }

    private class ConnectionTaskForImageUpload extends
            AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(
                AppLoginScreen.this);

        @Override
        protected void onPreExecute() {
           try{
               if(dialog != null){
                   dialog.setMessage("Please Wait...");
                   dialog.setCancelable(false);
                   dialog.setCanceledOnTouchOutside(false);
                   dialog.show();
               }
           }catch (Exception e){
               e.printStackTrace();
           }

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
           try{
               if (dialog != null && dialog.isShowing()) {
                   dialog.dismiss();
               }

               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(AppLoginScreen.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }



               if (!TextUtils.isEmpty(imageuploadresp) && imageuploadresp.equalsIgnoreCase("Error")) {

                   Toast.makeText(
                           AppLoginScreen.this,
                           "Error uploading Image, Please try again or use a different image",
                           Toast.LENGTH_SHORT).show();
               } else if (imageuploadresp.contains("Unauthorized Access")) {
                   com.clubmycab.utility.Log.e("AppLoginScreen",
                           "imageuploadresp Unauthorized Access");
                   Toast.makeText(AppLoginScreen.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               } else {
                   // Call New Home Screen

                   Intent mainIntent = new Intent(AppLoginScreen.this, NewHomeScreen.class);
                   mainIntent.putExtra("from", "normal");
                   mainIntent.putExtra("message", "null");
                   startActivityForResult(mainIntent, 500);
                   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   finish();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }

    }

    public class AuthenticateConnectionImageUpload {

        String picturePath;

        public AuthenticateConnectionImageUpload() {

        }

        public void connection() throws Exception {

            String imagestr = null;
            SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
            String mobNum = mPrefs.getString("MobileNumber", "");

            URL url = new URL(picturePath);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            com.clubmycab.utility.Log.d("img width", "" + myBitmap.getWidth());
            com.clubmycab.utility.Log.d("img height", "" + myBitmap.getHeight());

            int width = 0;
            int height = 0;

            if (myBitmap.getWidth() <= myBitmap.getHeight()) {

                width = 200;
                height = (myBitmap.getHeight() * 200) / myBitmap.getWidth();
            } else {
                width = (myBitmap.getWidth() * 200) / myBitmap.getHeight();
                height = 200;
            }

            com.clubmycab.utility.Log.d("width", "" + width);
            com.clubmycab.utility.Log.d("height", "" + height);

            myBitmap = scaleBitmap(myBitmap, width, height);

            com.clubmycab.utility.Log.d("resize width", "" + myBitmap.getWidth());
            com.clubmycab.utility.Log.d("resize height", "" + myBitmap.getHeight());
            Bitmap mainbmp = myBitmap;
            if (myBitmap != null) {
                myBitmap = null;
            }
            imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
                    Base64.NO_WRAP);



            String url_select = GlobalVariables.ServiceUrl + "/imageupload.php";

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_select);

            BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
                    "MobileNumber", mobNum);
            BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
                    "imagestr", imagestr);

            String authString = imagestr + mobNum;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(MobileNumberBasicNameValuePair);
            nameValuePairList.add(ImageBasicNameValuePair);
            nameValuePairList.add(authValuePair);

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                    nameValuePairList);
            httpPost.setEntity(urlEncodedFormEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            com.clubmycab.utility.Log.d("httpResponse", "" + httpResponse);

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

            com.clubmycab.utility.Log.d("imageuploadresp", "" + stringBuilder.toString());

            if (imageuploadresp.equalsIgnoreCase("Error")) {

            } else if (imageuploadresp.contains("Unauthorized Access")) {

            } else {

                String urldisplay = GlobalVariables.ServiceUrl
                        + "/ProfileImages/" + imageuploadresp.trim();
                Bitmap mIcon11 = null;
                String imgString = null;
                try {
                    InputStream in = new URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);

                    imgString = Base64.encodeToString(
                            getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

                } catch (Exception e) {
                    com.clubmycab.utility.Log.e("Error", e.getMessage());
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
                Bitmap.Config.ARGB_8888);

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
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
