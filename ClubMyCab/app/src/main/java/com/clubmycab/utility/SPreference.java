package com.clubmycab.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by newpc on 7/6/16.
 */
public class SPreference {

    public static final String USER_TYPE = "USER_TYPE";
    public static final String SELECTED_PAY_TYPE = "SELECTED_PAY_TYPE";
    public static final String REGISTRATION_NO = "REGISTRATION_NO";
    public static final String MODEL_NO = "MODEL_NO";
    public static final String MODEL_ID = "MODEL_ID";

    public static final String IS_COMMERCIAL = "IS_COMMERCIAL";
    public static final String RIDE_OFFER_WALLET_TYPE = "RIDE_OFFER_WALLET_TYPE";
    public static final String RIDE_TAKER_WALLET_TYPE = "RIDE_TAKER_WALLET_TYPE";
    public static final String IS_SEARCH_RESET = "IS_SEARCH_RESET";


    public static SharedPreferences getPref(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        return mPrefs;
    }

    public static int getRideOfferWalletType(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        return   mPrefs.getInt(SPreference.RIDE_OFFER_WALLET_TYPE,AppConstants.NULL);
    }

    public static int getRideTakerWalletType(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        return   mPrefs.getInt(SPreference.RIDE_TAKER_WALLET_TYPE, AppConstants.PAYMENT_TYPE_CASH);
    }

    public static void setRideOfferWalletType(Context context, int wallettype){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        mPrefs.edit().putInt(SPreference.RIDE_OFFER_WALLET_TYPE,wallettype).commit();
    }

    public static void setRideTakerWalletType(Context context, int wallettype){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        mPrefs.edit().putInt(SPreference.RIDE_TAKER_WALLET_TYPE,wallettype).commit();
    }

    public static String getWalletType(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("FacebookData", 0);
        if(mPrefs.getInt(SPreference.RIDE_OFFER_WALLET_TYPE, AppConstants.NULL) == AppConstants.PAYMENT_TYPE_MOBIKWIK){
            return AppConstants.MOBIKWIK;
        }
        return "";

    }
}
