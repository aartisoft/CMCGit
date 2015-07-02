package com.clubmycab.utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GlobalMethods {

	public static String calculateCheckSumString(String... checksumstring) {
		StringBuilder stb = new StringBuilder();
		for (String checkSumInput : checksumstring) {
			if (checkSumInput != null && !checkSumInput.equals("")) {
				stb.append("'" + checkSumInput + "'");
			}
		}
		return stb.toString();
	}

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
			//String enc = new String(digest);
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

}
