package com.clubmycab.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class OffersListActivity extends Activity implements
		AsyncTaskResultListener {

	private TextView textViewCredits;
	private ListView listViewOffers;

	private String mobileNumber, userDataResponse;

	private JSONArray offersJSONArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_offers_list);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					OffersListActivity.this);
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							finish();

							startActivity(intent);

						}
					});

			builder.show();
			return;
		}

		textViewCredits = (TextView) findViewById(R.id.textViewOffersCredits);

		listViewOffers = (ListView) findViewById(R.id.listViewOffers);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		mobileNumber = mPrefs.getString("MobileNumber", "");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		userData();
	}

	private void userData() {

		String endpoint = GlobalVariables.ServiceUrl + "/userData.php";
		String authString = mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("userData", "userData endpoint : " + endpoint + " params : "
				+ params);
		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
				OffersListActivity.this, false, "userData", false);
	}

	private void getOffers() {

		String endpoint = GlobalVariables.ServiceUrl + "/getOffers.php";
		String authString = mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("getOffers", "getOffers endpoint : " + endpoint + " params : "
				+ params);
		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
				OffersListActivity.this, true, "getOffers", false);
	}

//	private void claimOffers(String offerID) {
//
//		String endpoint = GlobalVariables.ServiceUrl + "/claimOffer.php";
//		String authString = offerID + mobileNumber;
//		String params = "mobileNumber=" + mobileNumber + "&id=" + offerID
//				+ "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
//		Log.d("claimOffers", "claimOffers endpoint : " + endpoint
//				+ " params : " + params);
//		new GlobalAsyncTask(OffersListActivity.this, endpoint, params, null,
//				OffersListActivity.this, true, "claimOffers", false);
//	}

	@Override
	public void getResult(String response, String uniqueID) {

		if (uniqueID.equals("getOffers")) {
			Log.d("OffersListActivity", "getOffers response : " + response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("OffersListActivity", "getOffers Unauthorized Access");
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {

					offersJSONArray = new JSONArray(jsonObject.get("data")
							.toString());

					if (offersJSONArray.length() > 0) {
						listViewOffers.setAdapter(new ListViewAdapter());

						listViewOffers
								.setOnItemClickListener(new AdapterView.OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										try {
											Intent intent = new Intent(
													OffersListActivity.this,
													OffersDetailsActivity.class);
											intent.putExtra(
													"OfferJSONObjectString",
													offersJSONArray.get(
															position)
															.toString());
											intent.putExtra(
													"UserDataJSONObjectString",
													userDataResponse);
											startActivity(intent);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

								});
					}
				} else {
					Toast.makeText(OffersListActivity.this,
							jsonObject.get("message").toString(),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			}
		} else if (uniqueID.equals("userData")) {
			Log.d("OffersListActivity", "userData response : " + response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("OffersListActivity", "userData Unauthorized Access");
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					JSONObject jsonObject2 = new JSONObject(jsonObject.get(
							"data").toString());
					textViewCredits.setText("Your Club Points : "
							+ jsonObject2.get("totalCredits").toString());
				}

				userDataResponse = response;

				getOffers();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(OffersListActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			}
		} 
//		else if (uniqueID.equals("claimOffers")) {
//			Log.d("OffersListActivity", "claimOffers response : " + response);
//
//			if (response != null && response.length() > 0
//					&& response.contains("Unauthorized Access")) {
//				Log.e("OffersListActivity", "claimOffers Unauthorized Access");
//				Toast.makeText(OffersListActivity.this,
//						getResources().getString(R.string.exceptionstring),
//						Toast.LENGTH_LONG).show();
//				return;
//			}
//
//			try {
//				JSONObject jsonObject = new JSONObject(response);
//
//				Toast.makeText(OffersListActivity.this,
//						jsonObject.get("message").toString(), Toast.LENGTH_LONG)
//						.show();
//
//				if (jsonObject.get("status").toString().equals("success")) {
//					userData();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Toast.makeText(OffersListActivity.this,
//						getResources().getString(R.string.exceptionstring),
//						Toast.LENGTH_LONG).show();
//			}
//		}
	}

	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return offersJSONArray.length();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = (View) getLayoutInflater().inflate(
						R.layout.list_item_offers, parent, false);
			}

			try {
				JSONObject jsonObject = offersJSONArray.getJSONObject(position);

				TextView textView = (TextView) convertView
						.findViewById(R.id.textViewOfferDescription);
				textView.setText(jsonObject.get("title").toString());

				textView = (TextView) convertView
						.findViewById(R.id.textViewOfferUserStatus);
				textView.setText(jsonObject.get("UserOfferStatus").toString());

				// Button button = (Button) convertView
				// .findViewById(R.id.buttonOfferClaim);
				// button.setTag("ButtonOfferClaim" + position);
				//
				// if (jsonObject.get("credits") != null
				// && !jsonObject.get("credits").toString()
				// .equalsIgnoreCase("null")
				// && !jsonObject.get("credits").toString()
				// .equalsIgnoreCase("0")) {
				//
				// button.setVisibility(View.VISIBLE);
				//
				// button.setOnClickListener(new View.OnClickListener() {
				//
				// @Override
				// public void onClick(View view) {
				//
				// int pos = Integer.parseInt(view.getTag().toString()
				// .replace("ButtonOfferClaim", ""));
				// Log.d("ButtonOfferClaim",
				// "ButtonOfferClaim onClick pos : " + pos);
				//
				// try {
				// JSONObject jsonObject = offersJSONArray
				// .getJSONObject(pos);
				// claimOffers(jsonObject.get("id").toString());
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// }
				// });
				//
				// } else {
				// button.setVisibility(View.GONE);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(OffersListActivity.this,
				HomeActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
