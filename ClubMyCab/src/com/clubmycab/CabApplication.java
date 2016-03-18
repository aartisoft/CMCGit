package com.clubmycab;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.affle.affledowloadtracker.AffleAppDownloadTracker;
import com.clubmycab.utility.GlobalVariables;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
@ReportsCrashes(formKey = "", // will not be used
mailTo = "vindhya.singh07@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.app_name)
public class CabApplication extends Application {

	public static GoogleAnalytics analytics;
	public static Tracker tracker;

	@Override
	public void onCreate() {
		super.onCreate();

		analytics = GoogleAnalytics.getInstance(this);
		analytics.setLocalDispatchPeriod(1800);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.enableExceptionReporting(true);
		tracker.enableAdvertisingIdCollection(true);
		tracker.enableAutoActivityTracking(true);
		
		//Added for config Affle sdk
		AffleAppDownloadTracker affledownloadtracker=new AffleAppDownloadTracker();
		affledownloadtracker.trackDownload(this,null);

		FacebookSdk.sdkInitialize(getApplicationContext());
		ACRA.init(CabApplication.this);
	}
}
