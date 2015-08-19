package com.clubmycab;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.clubmycab.ui.MyRidesActivity;
import com.clubmycab.utility.Log;

public class UpcomingStartTripAlarm extends WakefulBroadcastReceiver {

	public static final String ALARM_TYPE_UPCOMING = "UpcomingTripAlarm";
	public static final String ALARM_TYPE_START_TRIP = "StartTripAlarm";
	private static final String ALARM_TYPE_KEY = "AlarmTypeKey";
	private static final String CAB_ID_KEY = "CabIDKey";
	private static final String MESSAGE_KEY = "MessageKey";

	public static final int UPCOMING_TRIP_NOTIFICATION_TIME = 15;
	public static final int START_TRIP_NOTIFICATION_TIME = 0;

	private static final int ALARM_UPCOMING_ID = 0;
	private static final int ALARM_START_TRIP_ID = 1;

	static int notificationID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {

		String type = intent.getStringExtra(ALARM_TYPE_KEY);
		Log.d("UpcomingStartTripAlarm", "onReceive : " + type);

		// if (type.equals(ALARM_TYPE_UPCOMING)) {
		// setAlarm(
		// context,
		// Long.toString(System.currentTimeMillis() + (1 * 60 * 1000)),
		// UpcomingStartTripAlarm.ALARM_TYPE_START_TRIP,
		// intent.getStringExtra(CAB_ID_KEY), "fromshortname",
		// "toshortname");
		// }

		generateNotification(context, intent);

	}

	public void setAlarm(Context context, String startTime, String alarmType,
			String cabID, String fShortName, String tShortName) {

		Log.d("UpcomingStartTripAlarm", "setAlarm startTime : " + startTime
				+ " alarmType : " + alarmType);

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm aa");
			Date date = simpleDateFormat.parse(startTime);

			Log.d("UpcomingStartTripAlarm",
					"setAlarm startTime : " + date.getTime());

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			if (alarmType.equals(ALARM_TYPE_UPCOMING)) {

				Intent intent = new Intent(context,
						UpcomingStartTripAlarm.class);
				intent.putExtra(ALARM_TYPE_KEY, ALARM_TYPE_UPCOMING);
				intent.putExtra(CAB_ID_KEY, cabID);
				intent.putExtra(MESSAGE_KEY, "You have an upcoming trip from "
						+ fShortName + " to " + tShortName
						+ ". Click here to book a cab");

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, ALARM_UPCOMING_ID, intent, 0);
				alarmManager
						.set(AlarmManager.RTC_WAKEUP,
								(date.getTime() - (UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)),
								pendingIntent);
			} else if (alarmType.equals(ALARM_TYPE_START_TRIP)) {

				Intent intent = new Intent(context,
						UpcomingStartTripAlarm.class);
				intent.putExtra(ALARM_TYPE_KEY, ALARM_TYPE_START_TRIP);
				intent.putExtra(CAB_ID_KEY, cabID);
				intent.putExtra(MESSAGE_KEY, "Your trip from " + fShortName
						+ " to " + tShortName + " is about to start");

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, ALARM_START_TRIP_ID, intent, 0);
				alarmManager
						.set(AlarmManager.RTC_WAKEUP,
								(date.getTime() - (START_TRIP_NOTIFICATION_TIME * 60 * 1000)),
								pendingIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelBothAlarms(Context context) {
		Intent intent = new Intent(context, UpcomingStartTripAlarm.class);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent sender = PendingIntent.getBroadcast(context,
				ALARM_UPCOMING_ID, intent, 0);
		alarmManager.cancel(sender);

		sender = PendingIntent.getBroadcast(context, ALARM_START_TRIP_ID,
				intent, 0);
		alarmManager.cancel(sender);
	}

	private void generateNotification(Context context,
			Intent intentFromNotification) {

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent();

		String type = intentFromNotification.getStringExtra(ALARM_TYPE_KEY);
		if (type.equals(ALARM_TYPE_UPCOMING)) {
			intent = new Intent(context, MyRidesActivity.class);
			intent.putExtra("comefrom", ALARM_TYPE_UPCOMING);
			intent.putExtra("cabID",
					intentFromNotification.getStringExtra(CAB_ID_KEY));
		} else if (type.equals(ALARM_TYPE_START_TRIP)) {
			intent = new Intent(context, MyRidesActivity.class);
			intent.putExtra("comefrom", ALARM_TYPE_START_TRIP);
			intent.putExtra("cabID",
					intentFromNotification.getStringExtra(CAB_ID_KEY));
		}

		String message = intentFromNotification.getStringExtra(MESSAGE_KEY);

		PendingIntent pIntent = PendingIntent.getActivity(context,
				notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker("ClubMyCab")
				.setWhen(System.currentTimeMillis())
				.setAutoCancel(true)
				.setContentTitle("ClubMyCab")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentIntent(pIntent)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(message).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID, notification);

	}

}
