package com.clubmycab.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.xmlhandler.QueryWalletHandler;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class WalletsAcitivity extends Activity implements
		AsyncTaskResultListener {

	Tracker tracker;
	TextView response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallets);

		response = (TextView) findViewById(R.id.textView1);

		String email = "divey@mobikwik.com";
		String mobilenumber = "9999900000";
		String msgcode = "500";
		String action = "existingusercheck";

		String checksumstring = GlobalMethods.calculateCheckSumForService(
				"'existingusercheck''9999900000'MyMerchantName''MBK9005''500'",
				GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = "https://" + GlobalVariables.Mobikwik_ServerURL
				+ "/querywallet";
		String params = "email=" + email + "&cell=" + mobilenumber
				+ "&msgcode=" + msgcode + "&action=" + action + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring + "";
		new GlobalAsyncTask(this, endpoint, params, new QueryWalletHandler(),
				this);

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(WalletsAcitivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Wallets");

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wallets_acitivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void getResult(int result, String error) {
		// TODO Auto-generated method stub

		response.setText(error);

	}
}
