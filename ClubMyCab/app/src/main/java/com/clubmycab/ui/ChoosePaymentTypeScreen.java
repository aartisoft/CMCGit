package com.clubmycab.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;

public class ChoosePaymentTypeScreen extends AppCompatActivity implements View.OnClickListener, GlobalAsyncTask.AsyncTaskResultListener{
    RadioButton rb1, rb2;
    private boolean isFirstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment_type_screen);
        findViewById(R.id.flBackArrow).setOnClickListener(this);
        findViewById(R.id.card_view2).setOnClickListener(this);
        findViewById(R.id.card_view1).setOnClickListener(this);

        ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(ChoosePaymentTypeScreen.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvCash)).setTypeface(FontTypeface.getTypeface(ChoosePaymentTypeScreen.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvHeading)).setText("Select Payment");
        findViewById(R.id.flNotifications).setVisibility(View.GONE);
        rb1 =  (RadioButton)findViewById(R.id.radioButtonCash);
        rb2 =  (RadioButton)findViewById(R.id.radioButtonMobikwik);
        rb1.setOnCheckedChangeListener(onCheckedChangeListener);
        rb2.setOnCheckedChangeListener(onCheckedChangeListener);

        if(getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            if(bundle.getBoolean("fromrideoffer",false)){
                findViewById(R.id.card_view1).setVisibility(View.GONE);
                findViewById(R.id.tvMessage).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.card_view1).setVisibility(View.VISIBLE);
                findViewById(R.id.tvMessage).setVisibility(View.GONE);

               /* if(SPreference.getPref(ChoosePaymentTypeScreen.this).getInt(SPreference.SELECTED_PAY_TYPE, AppConstants.PAYMENT_TYPE_CASH) == AppConstants.PAYMENT_TYPE_CASH){
                    rb1.setChecked(true);
                }else if(SPreference.getPref(ChoosePaymentTypeScreen.this).getInt(SPreference.SELECTED_PAY_TYPE, AppConstants.PAYMENT_TYPE_CASH) == AppConstants.PAYMENT_TYPE_MOBIKWIK){
                    rb2.setChecked(true);
                }*/
            }
        }


    }
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Intent intent  = new Intent();
                int defaultPay = AppConstants.NULL;
                if(buttonView == rb1){
                    intent.putExtra("type", AppConstants.PAYMENT_TYPE_CASH);
                    defaultPay = AppConstants.PAYMENT_TYPE_CASH;
                }else if(buttonView == rb2){
                    intent.putExtra("type", AppConstants.PAYMENT_TYPE_MOBIKWIK);
                    defaultPay = AppConstants.PAYMENT_TYPE_MOBIKWIK;

                }
                setResult(RESULT_OK,intent);
                if(defaultPay == AppConstants.PAYMENT_TYPE_CASH){
                    finish();
                }else {
                    setDefaultPay(defaultPay);
                }
               /* if(isFirstTime){
                    if(defaultPay == AppConstants.PAYMENT_TYPE_CASH){
                        finish();
                    }else {
                        setDefaultPay(defaultPay);
                    }
                }
                if(!isFirstTime){
                   isFirstTime = true;
                }*/
            }
        }
    };

    private void setDefaultPay(int defaultPay){
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        String MobileNumber = mPrefs.getString("MobileNumber", "");

        String endpoint = GlobalVariables.ServiceUrl + "/setDefaultPayment.php";
        String authString = MobileNumber+defaultPay;
        String params = "mobileNumber=" + MobileNumber+"&payDefault="+defaultPay+ "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);

        new GlobalAsyncTask(ChoosePaymentTypeScreen.this, endpoint, params,
                null, ChoosePaymentTypeScreen.this, true, "defaultPay",
                false);
    }

    @Override
    public void getResult(String response, String uniqueID) {
        try{
            if(!TextUtils.isEmpty(response)){
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flBackArrow:
             /*   Intent mainIntent = new Intent(ChoosePaymentTypeScreen.this,
                        NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                finish();
                break;
            case R.id.card_view1:
                rb1.setChecked(true);
                break;

            case R.id.card_view2:
                rb2.setChecked(true);
                break;

        }
    }
}
