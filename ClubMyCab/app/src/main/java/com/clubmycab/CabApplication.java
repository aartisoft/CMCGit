package com.clubmycab;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDex;

import com.clubmycab.utility.GlobalVariables;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.fabric.sdk.android.Fabric;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "vindhya.singh07@gmail.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.app_name)
public class CabApplication extends Application {
	
	public static GoogleAnalytics analytics;
	public static Tracker tracker;
	private static CabApplication cabApplication;
	public Location getFirstLocation() {
		return firstLocation;
	}

	public void setFirstLocation(Location firstLocation) {
		this.firstLocation = firstLocation;
	}

	private Location firstLocation;
	
	public static CabApplication getInstance(){
		if (null == cabApplication) {
            return cabApplication;
        }
        return cabApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		cabApplication = (CabApplication) getApplicationContext();
		analytics = GoogleAnalytics.getInstance(this);
		analytics.setLocalDispatchPeriod(1800);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.enableExceptionReporting(true);
		tracker.enableAdvertisingIdCollection(true);
		tracker.enableAutoActivityTracking(true);
		
		//Added for config Affle sdk


		FacebookSdk.sdkInitialize(getApplicationContext());
	//	ACRA.init(CabApplication.this);
	}
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
	
	private CurrentLocation currentLocation;
	
	public class CurrentLocation{
		public String getCurrentLat() {
			return currentLat;
		}
		public void setCurrentLat(String currentLat) {
			this.currentLat = currentLat;
		}
		public String getCurrentLong() {
			return currentLong;
		}
		public void setCurrentLong(String currentLong) {
			this.currentLong = currentLong;
		}
		public String getCurrentAddress() {
			return currentAddress;
		}
		public void setCurrentAddress(String currentAddress) {
			this.currentAddress = currentAddress;
		}
		private String currentLat;
		private String currentLong;
		private String currentAddress;
	}
	
	public void setCurrentLocation(CurrentLocation  currentLocation){
		this.currentLocation = currentLocation;
	}
	
	public CurrentLocation getCurretLocation(){
		return currentLocation;
	}
	
	
}
