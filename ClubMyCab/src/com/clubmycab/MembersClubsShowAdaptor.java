package com.clubmycab;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.ui.MyClubsActivity;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MembersClubsShowAdaptor extends BaseAdapter {

	// Declare Variables
	private Context context;
	private ArrayList<String> MemberClubPoolId;
	private ArrayList<String> MemberClubPoolName;
	private ArrayList<String> MemberClubNoofMembers;
	private ArrayList<String> MemberClubOwnerName;
	private LayoutInflater inflater;

	Tracker tracker;
	boolean exceptioncheck = false;

	public MembersClubsShowAdaptor(Context context,
			ArrayList<String> memclubpoolid, ArrayList<String> memclubpoolname,
			ArrayList<String> memclubnoofmembers,
			ArrayList<String> memclubownername) {
		this.context = context;
		this.MemberClubPoolId = memclubpoolid;
		this.MemberClubPoolName = memclubpoolname;
		this.MemberClubNoofMembers = memclubnoofmembers;
		this.MemberClubOwnerName = memclubownername;

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyClubs");
	}

	@Override
	public int getCount() {
		return MemberClubPoolId.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View itemView, ViewGroup parent) {

		ViewHolder viewholder;
		if (itemView == null) {

			viewholder = new ViewHolder();

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			itemView = inflater.inflate(R.layout.membersclubs_listrow, parent,
					false);

			// Locate the TextViews in listview_item.xml

			viewholder.Mname = (TextView) itemView
					.findViewById(R.id.nameofclub);
			viewholder.noofmembers = (TextView) itemView
					.findViewById(R.id.noofmembers);
			viewholder.clubownername = (TextView) itemView
					.findViewById(R.id.clubownername);
			viewholder.removeclub = (ImageView) itemView
					.findViewById(R.id.removeclub);
			viewholder.ivWarnning1 = (ImageView) itemView
					.findViewById(R.id.ivWarnning2);
			itemView.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) itemView.getTag();
		}

		viewholder.Mname.setText(MemberClubPoolName.get(position).toString()
				.trim());
		viewholder.clubownername.setText("("
				+ MemberClubOwnerName.get(position).toString().trim() + ")");
		viewholder.noofmembers.setText("("
				+ MemberClubNoofMembers.get(position).toString().trim() + ")");

		viewholder.ivWarnning1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(context, StringTags.TAG_LOW_MEMBER,
						Toast.LENGTH_LONG).show();

			}
		});
		try {
			int count = Integer.parseInt(MemberClubNoofMembers.get(position)
					.toString().trim());
			if (count <= 10)
				viewholder.ivWarnning1.setVisibility(View.VISIBLE);
			else
				viewholder.ivWarnning1.setVisibility(View.GONE);

		} catch (Exception e) {
			viewholder.ivWarnning1.setVisibility(View.GONE);

		}

		viewholder.removeclub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Are you sure you want to leave this group?");
				builder.setCancelable(true);
				builder.setPositiveButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskForRemoveclub()
											.executeOnExecutor(
													AsyncTask.THREAD_POOL_EXECUTOR,
													MemberClubPoolId
															.get(position)
															.toString().trim());
								} else {
									new ConnectionTaskForRemoveclub()
											.execute(MemberClubPoolId
													.get(position));
								}
							}
						});
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		});

		return itemView;
	}

	public class ViewHolder {
		public ImageView ivWarnning1;
		public TextView Mname, clubownername;
		public TextView noofmembers;
		public ImageView removeclub;

	}

	// ///////

	private class ConnectionTaskForRemoveclub extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(context);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionRemoveclub mAuth1 = new AuthenticateConnectionRemoveclub();
			try {
				mAuth1.poolid = args[0];
				mAuth1.connection();
			} catch (Exception e) {
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(
						context,
						context.getResources().getString(
								R.string.exceptionstring), Toast.LENGTH_LONG)
						.show();
				return;
			}

			try {
				((MyClubsActivity) context).showclub();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionRemoveclub {

		public String poolid;

		public AuthenticateConnectionRemoveclub() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Leave Club").setAction("Leave Club")
					.setLabel("Leave Club").build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/Leave_Club.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
					"poolid", poolid.toString().trim());

			SharedPreferences mPrefs = context.getSharedPreferences(
					"FacebookData", 0);
			String OwnerNumber = mPrefs.getString("MobileNumber", "");
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", OwnerNumber.toString().trim());

			String authString = OwnerNumber.toString().trim()
					+ poolid.toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(poolidBasicNameValuePair);
			nameValuePairList.add(UserNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String poolresponse = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());

			if (poolresponse != null && poolresponse.length() > 0
					&& poolresponse.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "poolresponse Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select1 = GlobalVariables.ServiceUrl + "/Fetch_Club.php";
			HttpPost httpPost1 = new HttpPost(url_select1);

			BasicNameValuePair UserNumberBasicNameValuePair1 = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);
			authString = OwnerNumber;
			authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair1);
			nameValuePairList1.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;
			String myclubsresp = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myclubsresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			if (myclubsresp != null && myclubsresp.length() > 0
					&& myclubsresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myclubsresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = context
					.getSharedPreferences("MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

		}
	}
}