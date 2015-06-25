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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyClubsShowAdaptor extends BaseAdapter {

	// Declare Variables
	Context context;
	ArrayList<String> MyClubPoolId;
	ArrayList<String> MyClubPoolName;
	ArrayList<String> MyClubNoofMembers;
	ArrayList<String> MyClubOwnerName;
	LayoutInflater inflater;

	UrlConstant checkurl;
	boolean exceptioncheck = false;

	public MyClubsShowAdaptor(Context context, ArrayList<String> myclubpoolid,
			ArrayList<String> myclubpoolname,
			ArrayList<String> myclubnoofmembers,
			ArrayList<String> myclubownername) {
		this.context = context;
		this.MyClubPoolId = myclubpoolid;
		this.MyClubPoolName = myclubpoolname;
		this.MyClubNoofMembers = myclubnoofmembers;
		this.MyClubOwnerName = myclubownername;

		checkurl = new UrlConstant();
	}

	@Override
	public int getCount() {
		return MyClubPoolId.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.myclubs_listrow, parent,
				false);

		// Locate the TextViews in listview_item.xml

		TextView Mname = (TextView) itemView.findViewById(R.id.nameofclub);
		TextView noofmembers = (TextView) itemView
				.findViewById(R.id.noofmembers);
		ImageView removeclub = (ImageView) itemView
				.findViewById(R.id.removeclub);

		Mname.setText(MyClubPoolName.get(position).toString().trim());
		noofmembers.setText("("
				+ MyClubNoofMembers.get(position).toString().trim() + ")");

		removeclub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Are you sure you want to delete this club?");
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
													MyClubPoolId.get(position)
															.toString().trim());
								} else {
									new ConnectionTaskForRemoveclub()
											.execute(MyClubPoolId.get(position));
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
				// TODO Auto-generated catch block
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
				Toast.makeText(context,
						context.getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				((MyClubs) context).showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionRemoveclub {

		public String poolid;

		public AuthenticateConnectionRemoveclub() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = checkurl.GetServiceUrl() + "/removeclub.php";

			HttpPost httpPost = new HttpPost(url_select11);

			BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
					"poolid", poolid);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(poolidBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				String poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.e("poolresponse", "" + stringBuilder.toString());

			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select111 = checkurl.GetServiceUrl() + "/Fetch_Club.php";

			HttpPost httpPost1 = new HttpPost(url_select111);

			SharedPreferences mPrefs = context.getSharedPreferences(
					"FacebookData", 0);
			String OwnerNumber = mPrefs.getString("MobileNumber", "");
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.e("httpResponse", "" + httpResponse1);

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

			Log.e("myclubsresp", "" + myclubsresp);

			SharedPreferences sharedPreferences1 = context
					.getSharedPreferences("MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}
}