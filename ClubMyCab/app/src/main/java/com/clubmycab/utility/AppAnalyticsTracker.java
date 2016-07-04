package com.clubmycab.utility;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by newpc on 28/4/16.
 */
public class AppAnalyticsTracker {

    private static Tracker tracker;

    public static void setScreenName(Context context, String stringName){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName(stringName);
    }
}
