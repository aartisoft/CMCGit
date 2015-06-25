package com.clubmycab;

public class UrlConstant {

	String URL;

	public String GetServiceUrl() {

		URL = "http://180.179.207.159/cmc/cmcservice"; // production
		// URL = "http://180.179.208.23/cmc/cmcservice"; // UAT

		// URL = "http://122.160.103.25/php/ClubMyCab"; //Shree
		return URL;

	}

	public String GetIPAddress() {

		URL = "180.179.207.159"; // production
		// URL = "180.179.208.23"; // UAT

		// URL = "122.160.103.25"; //Shree
		return URL;

	}

	public String GetServerNameForChat() {

		URL = "@p2p-vm1"; // production
		// URL = "@p2p-vm2"; // UAT

		return URL;

	}

}
