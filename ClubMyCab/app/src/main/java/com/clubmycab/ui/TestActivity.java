package com.clubmycab.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;

public class TestActivity extends AppCompatActivity{
    Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_test_activty);

	}

	private void generatePersistentNotification() {
		int icon = R.drawable.cabappicon;
		// notificationID++;

		String message = "You are sharing your location";

		Intent intent = new Intent();
		intent = new Intent(TestActivity.this,
				ShareLocationFragmentActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(
				TestActivity.this, 101, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent pendingCloseIntent = PendingIntent.getActivity(
				this,
				0,
				new Intent(this, NewRideCreationScreen.class).setFlags(
						Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_SINGLE_TOP).setAction(
						"CLOSE_ACTION"), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				TestActivity.this);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker("iShareRyde")
				.setWhen(System.currentTimeMillis())
				.setOngoing(true)
				.addAction(R.drawable.ride_call,
						"Stop sharing", pendingCloseIntent)
				.setContentTitle("iShareRyde")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentIntent(pIntent)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(message).setAutoCancel(true).build();

		NotificationManager notificationManager = (NotificationManager) TestActivity.this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(101, notification);
	}
}
