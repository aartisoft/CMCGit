
 package com.clubmycab;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 *  @Method       AffleInstallReceiver
 *  @Description  This Class extends the SDK's (AffleAppDownloadTracker) class file
 */
public class AffleInstallReceiver extends BroadcastReceiver {
	
	
	 @Override
	    public void onReceive(Context context, Intent intent) {
	        // call Analytics tracker
		 com.google.android.gms.analytics.CampaignTrackingReceiver ir = new com.google.android.gms.analytics.CampaignTrackingReceiver();
	        ir.onReceive(context, intent);

	        // call Affle tracker
	        
	        com.affle.affledowloadtracker.AffleAppDownloadTracker ar = new com.affle.affledowloadtracker.AffleAppDownloadTracker();
	        ar.onReceive(context, intent);
	    }
	
}

