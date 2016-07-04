package com.clubmycab.utility;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Sachin on 6/19/2015.
 */
public class Utility {

    public static void hideSoftKeyboard(Activity activity) {
       try{
           if (activity != null && activity.getCurrentFocus() != null) {
               InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
               inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
           }
       }catch (Exception e){
           e.printStackTrace();
       }

    }
}
