package com.clubmycab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

public class PaymentMethodActivity extends AppCompatActivity implements View.OnClickListener,GlobalAsyncTask.AsyncTaskResultListener{
    private boolean isMobikwikTokenExist;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        initViews();
    }

    private void initViews() {
        try{
            findViewById(R.id.card_view_mobikwik).setOnClickListener(this);
            findViewById(R.id.flBackArrow).setOnClickListener(this);
            ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(PaymentMethodActivity.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.tvRupeeIcon)).setTypeface(FontTypeface.getTypeface(PaymentMethodActivity.this, AppConstants.FONT_AWESOME));
            ((TextView)findViewById(R.id.tvRupeeText)).setTypeface(FontTypeface.getTypeface(PaymentMethodActivity.this, AppConstants.HELVITICA));

            ((TextView)findViewById(R.id.tvCash)).setTypeface(FontTypeface.getTypeface(PaymentMethodActivity.this, AppConstants.HELVITICA));
            ((TextView)findViewById(R.id.tvHeading)).setText("Wallet");
            checkTokenExist();
            GoogleAnalytics analytics = GoogleAnalytics
                    .getInstance(PaymentMethodActivity.this);
            tracker = analytics
                    .newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flBackArrow:
                Intent mainIntent = new Intent(PaymentMethodActivity.this,
                        NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.card_view_mobikwik:
               if(!isMobikwikTokenExist){
                   Intent i1 = new Intent(PaymentMethodActivity.this,
                           FirstLoginWalletsActivity.class);
                   i1.putExtra("from", "wallet");
                   startActivity(i1);
                   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   GlobalVariables.ActivityName = "WalletsAcitivity";
                   tracker.send(new HitBuilders.EventBuilder()
                           .setCategory("Mobikwik Wallet")
                           .setAction("Mobikwik Wallet")
                           .setLabel("Mobikwik Wallet").build());
               }else {
                   setDefaultWallet(AppConstants.PAYMENT_TYPE_MOBIKWIK);
               }
                break;
        }
    }

    private void setDefaultWallet(int wallet){
        SPreference.setRideOfferWalletType(PaymentMethodActivity.this, wallet);
        SPreference.setRideTakerWalletType(PaymentMethodActivity.this,wallet);
        SPreference.getPref(PaymentMethodActivity.this).edit().putInt(SPreference.SELECTED_PAY_TYPE,wallet).commit();

        if(wallet == AppConstants.PAYMENT_TYPE_MOBIKWIK){
            findViewById(R.id.ivCheckMobikwik).setVisibility(View.VISIBLE); // other check visibility gone
        }

    }

    private void checkTokenExist(){
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        String MobileNumber = mPrefs.getString("MobileNumber", "");

        String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
        String authString = "getToken"+MobileNumber+ AppConstants.MOBIKWIK;
        String params = "act=getToken&mobileNumber="
                + MobileNumber+"&paymentMethod="+AppConstants.MOBIKWIK+ "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);

        new GlobalAsyncTask(PaymentMethodActivity.this, endpoint, params,
                null, PaymentMethodActivity.this, true, "getToken",
                false);
    }

    private void checkBalanceNew( ){
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        String MobileNumber = mPrefs.getString("MobileNumber", "");

        SharedPreferences sharedPreferences = getSharedPreferences(
                "MobikwikToken", 0);
        String token = sharedPreferences.getString("token", "");

       // if(!TextUtils.isEmpty(token)){
            String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
            String authString = "checkBalance"+MobileNumber + AppConstants.MOBIKWIK+ token;
            String params = "act=checkBalance&mobileNumber="
                    + MobileNumber+"&paymentMethod="+AppConstants.MOBIKWIK+"&token="+token + "&auth="
                    + GlobalMethods.calculateCMCAuthString(authString);

            new GlobalAsyncTask(PaymentMethodActivity.this, endpoint, params,
                    null, PaymentMethodActivity.this, true, "checkBalance",
                    false);
      //  }
    }

    @Override
    public void getResult(String response, String uniqueID) {
      try{
          if(uniqueID.equalsIgnoreCase("getToken")) {
              try {
                  JSONObject jsonObject = new JSONObject(response);
                  if (jsonObject.getString("status").equals("success")) {
                     // findViewById(R.id.card_view_mobikwik).setClickable(false);
                      isMobikwikTokenExist = true;
                      if(SPreference.getRideOfferWalletType(PaymentMethodActivity.this) == AppConstants.PAYMENT_TYPE_MOBIKWIK){
                          findViewById(R.id.ivCheckMobikwik).setVisibility(View.VISIBLE);
                      }

                      checkBalanceNew();
                  }else {

                      isMobikwikTokenExist = false;
                     // findViewById(R.id.card_view_mobikwik).setClickable(true);
                  }

              }catch (Exception e){
                  e.printStackTrace();
              }
          }else if(uniqueID.equalsIgnoreCase("checkBalance")){
              JSONObject jsonObject = new JSONObject(response);
              if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                  Log.d("FirstLoginWalletActivity", "userbalance response : ");
                  ((TextView)findViewById(R.id.tvRupeeIcon)).setText(getResources().getString(R.string.rupee));
                  ((TextView)findViewById(R.id.tvRupeeText)).setText(jsonObject.getString("balance"));
              }else {
              }
          }
      }catch (Exception e){
          e.printStackTrace();
      }
    }
}
