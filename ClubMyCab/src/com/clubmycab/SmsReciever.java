package com.clubmycab;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.clubmycab.utility.Log;

public class SmsReciever extends BroadcastReceiver {

	private static PhoneListener phoneListener = null;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SMSReceiver", "OnRecieve");
		SharedPreferences spPhone = context.getSharedPreferences("Phone",
				Context.MODE_PRIVATE);
		String cachedOTP = spPhone.getString("cachedOTP", "");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				SmsMessage[] smsMessages = Telephony.Sms.Intents
						.getMessagesFromIntent(intent);
				for (SmsMessage message : smsMessages) {
					Log.d("SMSReceiver",
							"OnRecieve getDisplayOriginatingAddress : "
									+ message.getDisplayOriginatingAddress());
					Log.d("SMSReceiver", "OnRecieve getDisplayMessageBody : "
							+ message.getDisplayMessageBody());
					if (message.getDisplayOriginatingAddress().contains(
							"ISHARE")
							|| message.getDisplayOriginatingAddress().contains(
									"CLUBMY")) {
						String[] msgOTP = message.getDisplayMessageBody()
								.split(" ");

						if (phoneListener != null) {
							phoneListener
									.onOtpConfirmed(msgOTP[msgOTP.length - 1]
											.trim());
						}

						// if (msgOTP[msgOTP.length -
						// 1].trim().equalsIgnoreCase(cachedOTP)) {
						// SharedPreferences.Editor
						// editorSMSVerifier=spPhone.edit();
						// editorSMSVerifier.putBoolean("isConfirmed", true);
						// editorSMSVerifier.commit();
						// if (phoneListener != null)
						// {
						// phoneListener.onOtpConfirmed("confirmed");
						// }
						// }
					}
				}
			} else {

				Object[] data = (Object[]) bundle.get("pdus");
				for (Object pdu : data) {
					Log.d("SMSReceiver",
							"legacy SMS implementation (before KitKat)");
					SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
					if (message == null) {
						Log.e("SMSReceiver", "SMS message is null -- ABORT");
						break;
					}
					Log.d("SMSReceiver",
							"OnRecieve getDisplayOriginatingAddress : "
									+ message.getDisplayOriginatingAddress());
					Log.d("SMSReceiver", "OnRecieve getDisplayMessageBody : "
							+ message.getDisplayMessageBody());

					if (message.getDisplayOriginatingAddress().contains(
							"ISHARE")
							|| message.getDisplayOriginatingAddress().contains(
									"CLUBMY")) {
						String[] msgOTP = message.getDisplayMessageBody()
								.split(" ");
						if (phoneListener != null) {
							phoneListener
									.onOtpConfirmed(msgOTP[msgOTP.length - 1]
											.trim());
						}

						// if (msgOTP[msgOTP.length -
						// 1].trim().equalsIgnoreCase(cachedOTP))
						// {
						// SharedPreferences.Editor
						// editorSMSVerifier=spPhone.edit();
						// editorSMSVerifier.putBoolean("isConfirmed", true);
						// editorSMSVerifier.commit();
						// if (phoneListener != null)
						// {
						// phoneListener.onOtpConfirmed("confirmed");
						// }
						// }
					}

				}
			}

		}
	}

	public void setOnPhoneListener(PhoneListener myListener) {
		phoneListener = myListener;
	}

}
