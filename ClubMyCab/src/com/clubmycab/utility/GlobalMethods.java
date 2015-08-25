package com.clubmycab.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

public class GlobalMethods {

	// public static String calculateCheckSumString(String... checksumstring) {
	// StringBuilder stb = new StringBuilder();
	// for (String checkSumInput : checksumstring) {
	// if (checkSumInput != null && !checkSumInput.equals("")) {
	// stb.append("'" + checkSumInput + "'");
	// }
	// }
	// return stb.toString();
	// }

	public static String calculateCheckSumForService(String input,
			String secretkey) {
		String mykey = secretkey;
		String test = input;
		System.out.println("Received String :- " + test + "\t Key " + mykey);

		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret = new SecretKeySpec(mykey.getBytes(),
					"HmacSHA256");
			mac.init(secret);
			byte[] digest = mac.doFinal(test.getBytes());
			// String enc = new String(digest);
			String ret = "";
			for (byte b : digest) {
				ret = ret + String.format("%02x", b);
			}
			return ret;
		} catch (Exception e) {
			Log.e("", "Error");
			return null;
		}
	}

	public static boolean checkResponseChecksum(String response) {

		try {
			JSONObject jsonObject = new JSONObject(response);

			Iterator<String> iterator = jsonObject.keys();
			String responseValues = "";

			HashMap<String, String> hashMap = new HashMap<String, String>();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = jsonObject.get(key).toString();

				if (!value.isEmpty() && value.length() > 0
						&& !key.equalsIgnoreCase("checksum")) {
					hashMap.put(key, value);
				}

				// if (!value.isEmpty() && value.length() > 0 &&
				// !key.equalsIgnoreCase("checksum")) {
				// responseValues += (value + "''");
				// }
			}
			// Log.d("checkResponseChecksum", "hashMap : " + hashMap);
			Map<String, String> map = new TreeMap<String, String>(hashMap);
			List<String> list = new ArrayList<String>(map.keySet());
			Log.d("checkResponseChecksum",
					"map : " + map + " keySet : " + map.keySet() + " list : "
							+ list);

			for (int i = 0; i < list.size(); i++) {
				responseValues += (map.get(list.get(i)) + "''");
			}

			responseValues = responseValues.substring(0,
					responseValues.length() - 2);
			String responseValuesFinal = "'" + responseValues + "'";

			// Log.d("checkResponseChecksum", "responseValuesFinal : "
			// + responseValuesFinal);

			String checkSumGenerated = GlobalMethods
					.calculateCheckSumForService(responseValuesFinal,
							GlobalVariables.Mobikwik_14SecretKey);
			Log.d("checkResponseChecksum", checkSumGenerated);

			if (checkSumGenerated.equals(jsonObject.get("checksum").toString())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// public static String getEmail(Context context) {
	// AccountManager accountManager = AccountManager.get(context);
	// Account account = getAccount(accountManager);
	//
	// if (account == null) {
	// return null;
	// } else {
	// return account.name;
	// }
	// }
	//
	// private static Account getAccount(AccountManager accountManager) {
	// Account[] accounts = accountManager.getAccountsByType("com.google");
	// Account account;
	// if (accounts.length > 0) {
	// account = accounts[0];
	// } else {
	// account = null;
	// }
	// return account;
	// }

}
