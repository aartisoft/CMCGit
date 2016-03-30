package com.clubmycab.fragment;

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
import org.json.JSONObject;

import com.clubmycab.R;
import com.clubmycab.adapter.RidesAvailableAdapter;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.HomeActivity;
import com.clubmycab.ui.HomeCarPoolActivity;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class RidesAvailableFragment extends Fragment implements OnClickListener{
	private String rideInvitationsResponseCarPool;
	private String rideInvitationsResponseCabShare;
	private String MobileNumber;
	private boolean isRunning;
	private ListView ridesListView;
	private RidesAvailableAdapter adapter;
	private ArrayList<RideDetailsModel> ridesList = new ArrayList<RideDetailsModel>();
	
	public static RidesAvailableFragment newInstance(Bundle args) {
		RidesAvailableFragment fragment = new RidesAvailableFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rides_available, container,
				false);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.carpoolll).setOnClickListener(this);
		SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
		MobileNumber = mPrefs.getString("MobileNumber", "");
		ridesListView = (ListView)view.findViewById(R.id.lvRidesList);
		adapter = new RidesAvailableAdapter();
		adapter.init(getActivity(), ridesList);
		ridesListView.setAdapter(adapter);
		fetchInvitations();
	}
	
	private void notifyAdapter(ArrayList<RideDetailsModel> arrayList){
		if(adapter != null){
			adapter.init(getActivity(), arrayList);
			adapter.notifyDataSetChanged();
		}
	}

	private class ConnectionTaskForFetchCarPool extends
			AsyncTask<String, Void, Void> {

		private boolean exceptioncheck;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchCarPool mAuth1 = new AuthenticateConnectionFetchCarPool();
			try {
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

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponseCarPool.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchPool().execute();
			}

		}

	}

	public class AuthenticateConnectionFetchCarPool {

		public AuthenticateConnectionFetchCarPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitationsCarpool.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);
			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse rideInvitations", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				rideInvitationsResponseCarPool = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponseCarPool",
					"" + stringBuilder.toString() + " mobileNumber : "
							+ MobileNumber);
		}
	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {

		private boolean exceptioncheck;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchPool mAuth1 = new AuthenticateConnectionFetchPool();
			try {
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

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponseCabShare.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject obj = new JSONObject(rideInvitationsResponseCarPool);
				String response = obj.getString("status");

				if (!response.equalsIgnoreCase("fail")) {
					Gson gson = new Gson();

					ArrayList<RideDetailsModel> arrayRideLocal = gson.fromJson(
							obj.getJSONArray("data").toString(),
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());

					if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							ridesList.add(arrayRideLocal.get(i));
						}
					}
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				JSONObject obj = new JSONObject(rideInvitationsResponseCabShare);
				String response = obj.getString("status");

				if (!response.equalsIgnoreCase("fail")) {
					Gson gson = new Gson();

					ArrayList<RideDetailsModel> arrayRideLocal = gson.fromJson(
							obj.getJSONArray("data").toString(),
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());

					if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							ridesList.add(arrayRideLocal.get(i));
						}
					}
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			getView().findViewById(R.id.pBarLoader).setVisibility(View.GONE);
			if (ridesList.size() > 0) {
				notifyAdapter(ridesList);

				/*
				 * textView.setVisibility(View.VISIBLE);
				 * 
				 * arrayListInvitations = arrayList;
				 * 
				 * isRunning = false;
				 * 
				 * circlePageIndicator.setVisibility(View.VISIBLE);
				 * 
				 * mFragmentStatePagerAdapter.notifyDataSetChanged();
				 * 
				 * viewPagerHome.setCurrentItem(0);
				 * 
				 * if (arrayListInvitations.size() > 1) {
				 * 
				 * swipeAutomaticViewPagger();
				 * 
				 * }
				 */
			} else {

				/*
				 * textView.setVisibility(View.INVISIBLE);
				 * circlePageIndicator.setVisibility(View.INVISIBLE);
				 * mFragmentStatePagerAdapter.notifyDataSetChanged();
				 */

			}

		}

	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitations.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);
			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse rideInvitations", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				rideInvitationsResponseCabShare = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponseCabShare",
					"" + stringBuilder.toString() + " mobileNumber : "
							+ MobileNumber);
		}
	}
	
	private void fetchInvitations() {
		if(isOnline()){
			ridesList.clear();
			getView().findViewById(R.id.pBarLoader).setVisibility(View.VISIBLE);
			if (!isRunning) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchCarPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchCarPool().execute();
				}

			}
		}else {
			getView().findViewById(R.id.pBarLoader).setVisibility(View.GONE);

		}

	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager)getActivity(). getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.carpoolll:
			Intent mainIntent = new Intent(getActivity(),
					HomeActivity.class);
			mainIntent.putExtra("screentoopen",
					HomeActivity.HOME_ACTIVITY_CAR_POOL);
			startActivityForResult(mainIntent, 500);
			getActivity().overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;

		default:
			break;
		}
		
	}


}