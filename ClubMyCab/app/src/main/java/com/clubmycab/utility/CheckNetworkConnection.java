package com.clubmycab.utility;


/** 
 * @author Set permission Access Network State in Manifest.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkConnection {

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

	public static void showConnectionErrorDialog(final Activity context){
		AlertDialog.Builder builder = new AlertDialog.Builder(
			context);
		builder.setMessage("No Internet Connection. Please check and try again!");
		builder.setCancelable(false);

		builder.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = context.getIntent();
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						context.finish();
						context.startActivity(intent);

					}
				});

		builder.show();
	}

}
