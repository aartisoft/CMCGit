package com.clubmycab.utility;

public class GlobalVariables {

	// chat IpAddress is same for both production & UAT

	public static final String ServiceUrl = "http://180.179.207.159/cmc/cmcservice"; // Production
	public static final String IpAddress = "180.179.208.23"; // Production
	public static final String ServerNameForChat = "@p2p-vm1"; // Production

	// Mobikwik
	public static final String Mobikwik_ServerURL = "https://walletapi.mobikwik.com"; // Production
	public static final String Mobikwik_Mid = "MBK9572"; // Production
	public static final String Mobikwik_MerchantName = "ClubMyCab"; // Production
	public static final String Mobikwik_14SecretKey = "bToO0UJ7OaijZ9g7a1Axr3J5OpEF"; // Production
	public static final String Mobikwik_14SecretKey_TokenRegenerate = "xXgeDsLo1nliS32dlkb21JpC05"; // Production

	// public static final String ServiceUrl =
	// "http://180.179.208.23/cmc/cmcservice"; // UAT
	// public static final String IpAddress = "180.179.208.23"; // UAT
	// public static final String ServerNameForChat = "@p2p-vm2"; // UAT
	
	// Mobikwik
	// public static final String Mobikwik_ServerURL =
	// "https://test.mobikwik.com/mobikwik"; // UAT
	// public static final String Mobikwik_Mid = "MBK9005"; // UAT
	// public static final String Mobikwik_MerchantName = "MyMerchantName"; //
	// UAT
	// public static final String Mobikwik_14SecretKey =
	// "ju6tygh7u7tdg554k098ujd5468o"; // UAT
	// public static final String Mobikwik_14SecretKey_TokenRegenerate =
	// "lu6tygh7u7tdg554k098ujd5468o"; // UAT

	// public static final String ServiceUrl = "http://104.155.216.171/cmc";
	// // GoogleCloudUAT
	// public static final String IpAddress = "104.155.216.171";
	// // GoogleCloudUAT
	// public static final String ServerNameForChat = "@p2p-vm2";
	// // GoogleCloudUAT

	public static final boolean LOGGING_ENABLED = true;

	public static final double GEOFENCING_RADIUS = 0.003;

	public static String ActivityName = "";

	public static final String GoogleAnalyticsTrackerId = "UA-63477985-1";

	public static final String GoogleMapsAPIKey = "AIzaSyBqd05mV8c2VTIAKhYP1mFKF7TRueU2-Z0";

	public static final String GCMProjectKey = "145246375713";

	public static String UnreadNotificationCount = "0";

}
