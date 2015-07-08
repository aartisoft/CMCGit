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
import com.clubmycab.utility.Log;
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

		String email = "testingone@mail.com";
		String mobilenumber = "8200012345";
		String msgcode = "500";
		String action = "existingusercheck";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ action + "''" + mobilenumber + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "'",
				GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL
				+ "/querywallet";
		String params = "cell=" + mobilenumber
				+ "&msgcode=" + msgcode + "&action=" + action + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("WalletsActivity", "endpoint : " + endpoint + " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, new QueryWalletHandler(),
				this, true);

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
		
		Log.d("WalletsActivity", "getResult : " + error);

	}
}
