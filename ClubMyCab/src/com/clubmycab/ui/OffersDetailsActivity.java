package com.clubmycab.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;

public class OffersDetailsActivity extends Activity {

	private JSONObject offerJSONObject, userDataJSONObject;

	// private String mobileNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_offers_details);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					OffersDetailsActivity.this);
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

		// SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		// mobileNumber = mPrefs.getString("MobileNumber", "");

		try {
			offerJSONObject = new JSONObject(getIntent().getStringExtra(
					"OfferJSONObjectString"));

			TextView textView = (TextView) findViewById(R.id.textViewOfferDetailsTitle);
			textView.setText(offerJSONObject.get("title").toString());

			textView = (TextView) findViewById(R.id.textViewOfferDetailsDescription);
			textView.setText(offerJSONObject.get("description").toString());

			textView = (TextView) findViewById(R.id.textViewOfferDetailsStatus);
			textView.setText(offerJSONObject.get("UserOfferStatus").toString());

			Button button = (Button) findViewById(R.id.buttonReferral);
			if (offerJSONObject.get("type").toString().contains("referral")) {

				userDataJSONObject = new JSONObject(getIntent().getStringExtra(
						"UserDataJSONObjectString"));

				textView = (TextView) findViewById(R.id.textViewOfferDetailsDescription);
				if (userDataJSONObject.get("status").toString()
						.equals("success")) {
					JSONObject jsonObject = new JSONObject(userDataJSONObject
							.get("data").toString());

					textView.setText(offerJSONObject.get("description")
							.toString()
							+ " Your referral code : "
							+ jsonObject.get("referralCode").toString());
				} else {
					textView.setText(offerJSONObject.get("description")
							.toString());
				}

				button.setVisibility(View.VISIBLE);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						try {
							if (userDataJSONObject.get("status").toString()
									.equals("success")) {
								JSONObject jsonObject = new JSONObject(
										userDataJSONObject.get("data")
												.toString());
								Intent sendIntent = new Intent();
								sendIntent.setAction(Intent.ACTION_SEND);

								if (Double.parseDouble(offerJSONObject.get(
										"useCount").toString()) >= Double
										.parseDouble(offerJSONObject.get(
												"maxUsePerUser").toString())) {
									sendIntent
											.putExtra(
													Intent.EXTRA_TEXT,
													"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ https://play.google.com/store/apps/details?id=com.clubmycab");
								} else {
									sendIntent
											.putExtra(
													Intent.EXTRA_TEXT,
													"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ https://play.google.com/store/apps/details?id=com.clubmycab. Use my referral code "
															+ jsonObject
																	.get("referralCode")
																	.toString()
															+ " and earn credits worth Rs."
															+ offerJSONObject
																	.get("amount")
																	.toString());
								}
								sendIntent.setType("text/plain");
								startActivity(Intent.createChooser(sendIntent,
										"Share Via"));

							}
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(
									OffersDetailsActivity.this,
									getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
						}
					}
				});
			} else {
				button.setVisibility(View.GONE);
			}

			// button = (Button) findViewById(R.id.buttonClaimOffer);
			// if (offerJSONObject.get("credits") != null
			// && !offerJSONObject.get("credits").toString()
			// .equalsIgnoreCase("null")
			// && !offerJSONObject.get("credits").toString()
			// .equalsIgnoreCase("0")) {
			//
			// button.setVisibility(View.VISIBLE);
			//
			// button.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// try {
			// claimOffers(offerJSONObject.get("id").toString());
			// } catch (Exception e) {
			// e.printStackTrace();
			// Toast.makeText(
			// OffersDetailsActivity.this,
			// getResources().getString(
			// R.string.exceptionstring),
			// Toast.LENGTH_LONG).show();
			// }
			// }
			// });
			// } else {
			// button.setVisibility(View.GONE);
			// }

			// textView = (TextView) findViewById(R.id.textViewOfferDetailsTNC);
			// textView.setText(offerJSONObject.get("terms").toString());

			textView = (TextView) findViewById(R.id.textViewOffersTNC);
			textView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								OffersDetailsActivity.this);
						builder.setMessage(offerJSONObject.get("terms")
								.toString());
						builder.setCancelable(false);
						builder.setPositiveButton("OK", null);
						// builder.show();

						AlertDialog alert = builder.create();
						alert.show();

						TextView textView = (TextView) alert
								.findViewById(android.R.id.message);
						textView.setTextSize(13);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(OffersDetailsActivity.this,
					getResources().getString(R.string.exceptionstring),
					Toast.LENGTH_LONG).show();
		}

	}

	// private void claimOffers(String offerID) {
	//
	// String endpoint = GlobalVariables.ServiceUrl + "/claimOffer.php";
	// String authString = offerID + mobileNumber;
	// String params = "mobileNumber=" + mobileNumber + "&id=" + offerID
	// + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
	// Log.d("claimOffers", "claimOffers endpoint : " + endpoint
	// + " params : " + params);
	// new GlobalAsyncTask(OffersDetailsActivity.this, endpoint, params, null,
	// OffersDetailsActivity.this, true, "claimOffers", false);
	// }
	//
	// @Override
	// public void getResult(String response, String uniqueID) {
	//
	// if (uniqueID.equals("claimOffers")) {
	// Log.d("OffersDetailsActivity", "claimOffers response : " + response);
	//
	// if (response != null && response.length() > 0
	// && response.contains("Unauthorized Access")) {
	// Log.e("OffersDetailsActivity",
	// "claimOffers Unauthorized Access");
	// Toast.makeText(OffersDetailsActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// try {
	// JSONObject jsonObject = new JSONObject(response);
	//
	// Toast.makeText(OffersDetailsActivity.this,
	// jsonObject.get("message").toString(), Toast.LENGTH_LONG)
	// .show();
	// } catch (Exception e) {
	// e.printStackTrace();
	// Toast.makeText(OffersDetailsActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// }
	// }
	// }

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
