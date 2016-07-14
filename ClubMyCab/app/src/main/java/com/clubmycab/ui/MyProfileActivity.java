package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.clubmycab.utility.Utility;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends Activity {

	private EditText fullnameedittext;
	private EditText emailedittext;
	private TextView dobedittext;
	private EditText mobileedittext;
	private TextView genderedittext;
	private ImageView notificationimg;
	private ImageView sidemenu;
	private CircleImageView profilebannerimage;
	private TextView profilebannerusername;
    private TextView myprofile;
	private TextView myrides;
	private TextView bookacab;
	private TextView sharemylocation;
	private TextView myclubs;
	private TextView sharethisapp;
	private TextView mypreferences;
	private TextView about;
	private String FullName;
	private String MobileNumberstr;
	private RelativeLayout unreadnoticountrl;
	private TextView unreadnoticount;
    private Button updateprofile;
	private String imagenameresp;
	private String myprofileresp;
	private String updateprofileresp;
	private Calendar myCalendar = Calendar.getInstance();
	private String readunreadnotiresp;
	private Bitmap mIcon11;
	private String imageuploadchkstr;
	private Bitmap mainbmp = null;
	private String imageuploadresp;
	private Tracker tracker;
	private boolean exceptioncheck = false;
	private static final int DATE_DIALOG_ID = 1002;
	private int year, month, day;
	private Calendar calendar;
    private String searchString = "";
    private EditText etRegistrationNo;
    private AutoCompleteTextView etModelName;
	private Dialog onedialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR,1);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MyProfileActivity.this);
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
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        MobileNumberstr = mPrefs.getString("MobileNumber", "");
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(MyProfileActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("MyProfile");
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		profilebannerimage = (CircleImageView) findViewById(R.id.profilebannerimage);
		profilebannerusername = (TextView) findViewById(R.id.profilebannerusername);
        profilebannerusername.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        profilebannerusername.setText(FullName);
        ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvHeading)).setText("My Profile");
        findViewById(R.id.flBackArrow).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MyProfileActivity.this,
                        NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        findViewById(R.id.flNotifications).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(MyProfileActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}



		fullnameedittext = (EditText) findViewById(R.id.fullnameedittext);
		emailedittext = (EditText) findViewById(R.id.emailedittext);
		dobedittext = (TextView) findViewById(R.id.dobedittext);
		mobileedittext = (EditText) findViewById(R.id.mobileedittext);
		genderedittext = (TextView) findViewById(R.id.genderedittext);

		mobileedittext.setEnabled(false);

		updateprofile = (Button) findViewById(R.id.updateprofile);


		fullnameedittext.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));


		emailedittext.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));


		dobedittext.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));


		mobileedittext.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));

		genderedittext.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));

		updateprofile.setTypeface(FontTypeface.getTypeface(MyProfileActivity.this,AppConstants.HELVITICA));

		findViewById(R.id.lldobedittext).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*new DatePickerDialog(MyProfileActivity.this, date1, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
				showDialog(DATE_DIALOG_ID);


			}
		});
        findViewById(R.id.dobedittext).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				/*new DatePickerDialog(MyProfileActivity.this, date1, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
                showDialog(DATE_DIALOG_ID);


            }
        });
		findViewById(R.id.myprofilerl).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			}
		});


		// /////////////

		findViewById(R.id.llgenderedittext).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MyProfileActivity.this);
				builder.setTitle("Select Gender");

				final CharSequence str[] = { "Male", "Female", "Other" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub
						genderedittext.setText(str[position]);
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});
        findViewById(R.id.genderedittext).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MyProfileActivity.this);
                builder.setTitle("Select Gender");

                final CharSequence str[] = { "Male", "Female", "Other" };

                builder.setItems(str, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        // TODO Auto-generated method stub
                        genderedittext.setText(str[position]);
                    }
                });

                dialog = builder.create();
                dialog.show();

            }
        });




        updateprofile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method

				if (emailedittext.getText().toString().trim().isEmpty()
						&& genderedittext.getText().toString().trim().isEmpty()
						&& dobedittext.getText().toString().trim().isEmpty()) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MyProfileActivity.this);

					builder.setMessage("Please enter the details");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else {

					String eml = emailedittext.getText().toString().trim();
					String gnd = genderedittext.getText().toString().trim();
					String dob = dobedittext.getText().toString().trim();
					String fullname = fullnameedittext.getText().toString()
							.trim();

					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MyProfileActivity.this);
						builder.setTitle("Internet Connection Error");
						builder.setMessage("iShareRyde requires Internet connection");
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();

						return;
					} else {

						Log.d("all set", "all set");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForUpdateMyProfile()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											eml, dob, gnd, fullname);
						} else {
							new ConnectionTaskForUpdateMyProfile().execute(eml,
									dob, gnd, fullname);
						}
					}

				}

			}
		});

        SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imagestr = mPrefs111.getString("imagestr", "");
		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

			// new ConnectionTaskForfetchimagename().execute();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForfetchimagename()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForfetchimagename().execute();
			}

		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);
    		profilebannerimage.setImageBitmap(yourSelectedImage);
		}

		profilebannerimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				selectImage();
			}
		});

		SharedPreferences mPrefs1111 = getSharedPreferences("MyProfile", 0);
		String myprofile = mPrefs1111.getString("myprofile", "");
		if (myprofile.isEmpty() || myprofile == null
				|| myprofile.equalsIgnoreCase("")) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchMyProfile()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchMyProfile().execute();
			}

		} else {

			if (myprofile.equalsIgnoreCase("No Data")) {
				Toast.makeText(MyProfileActivity.this, "" + myprofile,
						Toast.LENGTH_LONG).show();
			} else {
				JSONArray subArray = null;
				try {
					subArray = new JSONArray(myprofile);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < subArray.length(); i++) {
					try {

						fullnameedittext.setText(subArray.getJSONObject(i)
								.getString("FullName").toString().trim());
						mobileedittext.setText(subArray.getJSONObject(i)
								.getString("MobileNumber").toString().trim()
								.substring(4));
						emailedittext.setText(subArray.getJSONObject(i)
								.getString("Email").toString().trim());
						genderedittext.setText(subArray.getJSONObject(i)
								.getString("Gender").toString().trim());
						dobedittext.setText(subArray.getJSONObject(i)
								.getString("DOB").toString().trim());
                        if(!subArray.getJSONObject(i).isNull("DOB") && !TextUtils.isEmpty(subArray.getJSONObject(i).optString("DOB"))){
                            String[] arr2 = subArray.getJSONObject(i).getString("DOB").toString().trim().split("/");
                            day = Integer.parseInt(arr2[0]);
                            month = Integer.parseInt(arr2[1])-1;
                            year = Integer.parseInt(arr2[2]);
                        }

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

        ((AutoCompleteTextView) findViewById(R.id.etModel)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((EditText) findViewById(R.id.etRegistrationNo)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.textView8)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvRegno)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.textView9)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAddDetail)).setTypeface(FontTypeface.getTypeface(MyProfileActivity.this, AppConstants.HELVITICA));


        etModelName = (AutoCompleteTextView) findViewById(R.id.etModel);
        etRegistrationNo = (EditText) findViewById(R.id.etRegistrationNo);
        etModelName.setText(SPreference.getPref(MyProfileActivity.this).getString(SPreference.MODEL_NO,""));
        etRegistrationNo.setText(SPreference.getPref(MyProfileActivity.this).getString(SPreference.REGISTRATION_NO,""));
        etModelName.setThreshold(1);
        etModelName.addTextChangedListener(textWatcher);
        if(SPreference.getPref(MyProfileActivity.this).getBoolean(SPreference.IS_COMMERCIAL,false)){
            ((CheckBox) findViewById(R.id.ch1)).setChecked(true);
        }else {
            ((CheckBox) findViewById(R.id.ch1)).setChecked(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //resetRequest();
            new ConnectionTaskGetModel()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new ConnectionTaskGetModel().execute();
        }
		/*if(TextUtils.isEmpty(SPreference.getPref(MyProfileActivity.this).getString(SPreference.MODEL_NO,""))){
			findViewById(R.id.llVehicleDetail).setVisibility(View.GONE);
            findViewById(R.id.llDrivingLicence).setVisibility(View.GONE);
            //((TextView)findViewById(R.id.tvAddDetail)).setText("Add Vehicle Detail");

        }else {
			findViewById(R.id.llVehicleDetail).setVisibility(View.VISIBLE);
            findViewById(R.id.llDrivingLicence).setVisibility(View.VISIBLE);
            //((TextView)findViewById(R.id.tvAddDetail)).setText("Vehicle Detail");

        }*/
        etModelName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                selectedPosition = vehicleNameList.indexOf(selection);
                Utility.hideSoftKeyboard(MyProfileActivity.this);
            }
        });

		CheckBox ch1 = (CheckBox) findViewById(R.id.ch2);
		ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					findViewById(R.id.updateprofile).setEnabled(true);
					findViewById(R.id.updateprofile).setBackgroundResource(R.drawable.bg_btn_background);

				} else {
					findViewById(R.id.updateprofile).setEnabled(false);
					findViewById(R.id.updateprofile).setBackgroundColor(getResources().getColor(R.color.color_app_text_light));

				}
			}
		});
        findViewById(R.id.tvAddDetail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.llVehicleDetail).setVisibility(View.VISIBLE);
                findViewById(R.id.llDrivingLicence).setVisibility(View.VISIBLE);
                findViewById(R.id.flCross).setVisibility(View.VISIBLE);


            }
        });
        findViewById(R.id.flCross).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.llVehicleDetail).setVisibility(View.GONE);
                findViewById(R.id.llDrivingLicence).setVisibility(View.GONE);
                findViewById(R.id.flCross).setVisibility(View.GONE);
				findViewById(R.id.updateprofile).setEnabled(true);
				findViewById(R.id.updateprofile).setBackgroundResource(R.drawable.bg_btn_background);



			}
        });
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, myDateListener, year, month, day);


		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			// arg1 = year
			// arg2 = month
			// arg3 = day
			showDate(arg1, arg2 + 1, arg3);
		}
	};
	private void showDate(int year, int month, int day) {
		String travelDate = String.format("%02d",day) + "/" + String.format("%02d",month) + "/" + year;
		Calendar userAge = Calendar.getInstance();
		userAge.set(year,month,day);
		Calendar minAdultAge = Calendar.getInstance();
		minAdultAge.add(Calendar.YEAR, -18);
		if (minAdultAge.before(userAge))
		{
			CustomDialog.showDialog(MyProfileActivity.this,"You need to be 18+ to use this app");
			calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR,1);
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			return;
		}
		myCalendar.set(Calendar.YEAR, year);
		myCalendar.set(Calendar.MONTH, month);
		myCalendar.set(Calendar.DAY_OF_MONTH, day);

	///	String myFormat = "dd/MM/yyyy"; // In which you need put here
	//	SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		dobedittext.setText(travelDate);
	}



	// ////////////////////////
	// ///////
	private class ConnectionTaskForfetchimagename extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionfetchimagename mAuth1 = new AuthenticateConnectionfetchimagename();
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
			try{
				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (imagenameresp == null) {
					profilebannerimage.setImageResource(R.drawable.avatar_drawer);

				} else if (imagenameresp.contains("Unauthorized Access")) {
					Log.e("MyProfileActivity", "imagenameresp Unauthorized Access");
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				} else {
					profilebannerimage.setImageBitmap(mIcon11);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public class AuthenticateConnectionfetchimagename {

		public AuthenticateConnectionfetchimagename() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchimagename.php";
			HttpPost httpPost11 = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);

			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);
			nameValuePairList11.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.d("httpResponse", "" + httpResponse11);

			InputStream inputStream11 = httpResponse11.getEntity().getContent();
			InputStreamReader inputStreamReader11 = new InputStreamReader(
					inputStream11);

			BufferedReader bufferedReader11 = new BufferedReader(
					inputStreamReader11);

			StringBuilder stringBuilder11 = new StringBuilder();

			String bufferedStrChunk11 = null;

			while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
				imagenameresp = stringBuilder11.append(bufferedStrChunk11)
						.toString();
			}

			Log.d("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else if (imagenameresp.contains("Unauthorized Access")) {

			} else {
				String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ imagenameresp;
				String urldisplay = url1.toString().trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imagenameresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}

		}
	}

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// ///////
	// private class ConnectionTaskForreadunreadnotification extends
	// AsyncTask<String, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	//
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionreadunreadnotification mAuth1 = new
	// AuthenticateConnectionreadunreadnotification();
	// try {
	// mAuth1.connection();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// exceptioncheck = true;
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void v) {
	//
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(MyProfileActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// if (readunreadnotiresp.equalsIgnoreCase("0")) {
	//
	// unreadnoticountrl.setVisibility(View.GONE);
	//
	// } else {
	//
	// unreadnoticountrl.setVisibility(View.VISIBLE);
	// unreadnoticount.setText(readunreadnotiresp);
	// }
	// }
	//
	// }
	//
	// public class AuthenticateConnectionreadunreadnotification {
	//
	// public AuthenticateConnectionreadunreadnotification() {
	//
	// }
	//
	// public void connection() throws Exception {
	//
	// // Connect to google.com
	// HttpClient httpClient = new DefaultHttpClient();
	// String url_select11 = GlobalVariables.ServiceUrl
	// + "/FetchUnreadNotificationCount.php";
	// HttpPost httpPost = new HttpPost(url_select11);
	// BasicNameValuePair MobileNumberBasicNameValuePair = new
	// BasicNameValuePair(
	// "MobileNumber", MobileNumberstr);
	//
	// List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	// nameValuePairList.add(MobileNumberBasicNameValuePair);
	//
	// UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
	// nameValuePairList);
	// httpPost.setEntity(urlEncodedFormEntity);
	// HttpResponse httpResponse = httpClient.execute(httpPost);
	//
	// Log.d("httpResponse", "" + httpResponse);
	//
	// InputStream inputStream = httpResponse.getEntity().getContent();
	// InputStreamReader inputStreamReader = new InputStreamReader(
	// inputStream);
	//
	// BufferedReader bufferedReader = new BufferedReader(
	// inputStreamReader);
	//
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// String bufferedStrChunk = null;
	//
	// while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
	// readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
	// .toString();
	// }
	//
	// Log.d("readunreadnotiresp", "" + readunreadnotiresp);
	//
	// }
	// }

	// ///////

	private class ConnectionTaskForFetchMyProfile extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				MyProfileActivity.this);

		@Override
		protected void onPreExecute() {
			try{

			}catch (Exception e){
				e.printStackTrace();
			}

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyProfile mAuth1 = new AuthenticateConnectionFetchMyProfile();
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

			try{
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (myprofileresp.contains("Unauthorized Access")) {
					Log.e("MyProfileActivity", "myprofileresp Unauthorized Access");
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (myprofileresp.equalsIgnoreCase("No Data")) {
					Toast.makeText(MyProfileActivity.this, "" + myprofileresp,
							Toast.LENGTH_LONG).show();
				} else {
					JSONArray subArray = null;
					try {
						subArray = new JSONArray(myprofileresp);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (int i = 0; i < subArray.length(); i++) {
						try {

							fullnameedittext.setText(subArray.getJSONObject(i)
									.getString("FullName").toString().trim());
							mobileedittext.setText(subArray.getJSONObject(i)
									.getString("MobileNumber").toString().trim()
									.substring(4));
							emailedittext.setText(subArray.getJSONObject(i)
									.getString("Email").toString().trim());
							genderedittext.setText(subArray.getJSONObject(i)
									.getString("Gender").toString().trim());
							dobedittext.setText(subArray.getJSONObject(i)
									.getString("DOB").toString().trim());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionFetchMyProfile {

		public AuthenticateConnectionFetchMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchmyprofile.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);
			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myprofileresp", "" + stringBuilder.toString());

			if (!myprofileresp.contains("Unauthorized Access")) {
				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"MyProfile", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("myprofile", myprofileresp.toString().trim());
				editor1.commit();
			}
			// ///////////////
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	// ///////

	private class ConnectionTaskForUpdateMyProfile extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionUpdateMyProfile mAuth1 = new AuthenticateConnectionUpdateMyProfile();
			try {
				mAuth1.email = args[0];
				mAuth1.dob = args[1];
				mAuth1.gender = args[2];
				mAuth1.fullName = args[3];
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
			try{
				hideProgressBar();

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Update Profile").setAction("Update Profile")
						.setLabel("Update Profile").build());

				if (updateprofileresp.equalsIgnoreCase("update success")) {

					/*Toast.makeText(MyProfileActivity.this,
							"Profile updated successfully", Toast.LENGTH_LONG)
							.show();*/
					SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
					FullName = mPrefs.getString("FullName", "");
					MobileNumberstr = mPrefs.getString("MobileNumber", "");
					profilebannerusername.setText(FullName);
				} else if (updateprofileresp.contains("Unauthorized Access")) {
					Log.e("MyProfileActivity",
							"updateprofileresp Unauthorized Access");
					Toast.makeText(MyProfileActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				} else {

					Toast.makeText(MyProfileActivity.this, "" + updateprofileresp,
							Toast.LENGTH_LONG).show();


				}
                if (!isOnline()) {
                    Toast.makeText(MyProfileActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
                if(isVehicleDetailChanged()){
                    if (isValidRequest()) {

                        Utility.hideSoftKeyboard(MyProfileActivity.this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            //resetRequest();
                            new SaveVehicleDetailTask()
                                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new SaveVehicleDetailTask().execute();
                        }
                    }
                }else {
                    Toast.makeText(MyProfileActivity.this,"Profile updated successfully" , Toast.LENGTH_SHORT).show();

                }
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
    private class SaveVehicleDetailTask extends AsyncTask<String, Void, Void> {
        private String result;
        private String addVehicalResul;

        @Override
        protected void onPreExecute() {
           showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("", "");
            AuthenSaveVehicleDetail mAuth1 = new AuthenSaveVehicleDetail();
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
        protected void onPostExecute(Void aVoid) {
            try {
                hideProgressBar();
                if (!TextUtils.isEmpty(addVehicalResul)) {
                    if (exceptioncheck) {
                        exceptioncheck = false;
                        Toast.makeText(MyProfileActivity.this,
                                getResources().getString(R.string.exceptionstring),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(addVehicalResul);
                    if (!jsonObject.isNull("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(MyProfileActivity.this,"Profile updated successfully" , Toast.LENGTH_SHORT).show();
                        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean(AppConstants.IS_VEHICLE_ADDED, true);
                        editor.putString(SPreference.REGISTRATION_NO,etRegistrationNo.getText().toString().trim());
                        editor.putString(SPreference.MODEL_NO, vehicleNameList.get(selectedPosition));
                        editor.putString(SPreference.MODEL_ID, vehicleIDList.get(selectedPosition));
                        editor.putBoolean(SPreference.IS_COMMERCIAL, ((CheckBox) findViewById(R.id.ch1)).isChecked());
                        editor.commit();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class AuthenSaveVehicleDetail {

            public AuthenSaveVehicleDetail() {

            }

            public void connection() throws Exception {
                SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
                String FullName = mPrefs.getString("FullName", "");
                String MobileNumber = mPrefs.getString("MobileNumber", "");
                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/saveUserVehicleDetails.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair mobilePair = new BasicNameValuePair(
                        "mobileNumber", MobileNumber);
                BasicNameValuePair vehicleIdPair = new BasicNameValuePair(
                        "vehicleId", vehicleIDList.get(selectedPosition));
                int isCommercial = 0;
                if (((CheckBox) findViewById(R.id.ch1)).isChecked()) {
                    isCommercial = 1;
                }
                BasicNameValuePair isCommercialPair = new BasicNameValuePair(
                        "isCommercial", String.valueOf(isCommercial));
                BasicNameValuePair registrationNumberPair = new BasicNameValuePair(
                        "registrationNumber", etRegistrationNo.getText().toString().trim());


                String authString = isCommercial + MobileNumber + etRegistrationNo.getText().toString().trim()
                        + vehicleIDList.get(selectedPosition);
                android.util.Log.d("authString", authString);
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(mobilePair);
                nameValuePairList.add(vehicleIdPair);
                nameValuePairList.add(isCommercialPair);
                nameValuePairList.add(registrationNumberPair);
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

                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    addVehicalResul = stringBuilder.append(bufferedStrChunk).toString();
                }


                Log.d("result", "" + stringBuilder.toString());
            }
        }

    }

    private boolean isValidRequest() {
		if (TextUtils.isEmpty(etModelName.getText().toString().trim()) || !vehicleNameList.contains(etModelName.getText().toString().trim())) {
            //etModelName.setError("Please enter vehicle detail
			CustomDialog.showDialog(MyProfileActivity.this,"Please enter a valid vehicle model");
            return false;
        }
        if (TextUtils.isEmpty(etRegistrationNo.getText().toString().trim())) {
           // etRegistrationNo.setError("Please enter registration number");
            CustomDialog.showDialog(MyProfileActivity.this,"Please enter registration number");

            return false;
        }

        return true;
    }
	public class AuthenticateConnectionUpdateMyProfile {

		public String email;
		public String dob;
		public String gender;
		public String fullName;

		public AuthenticateConnectionUpdateMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/updatemyprofile.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);
			BasicNameValuePair EmailBasicNameValuePair = new BasicNameValuePair(
					"Email", email);
			BasicNameValuePair GenderBasicNameValuePair = new BasicNameValuePair(
					"Gender", gender);
			BasicNameValuePair DOBBasicNameValuePair = new BasicNameValuePair(
					"DOB", dob);
			BasicNameValuePair NameBasicNameValuePair = new BasicNameValuePair(
					"fullName", fullName);

			String authString = dob + email + fullName + gender + MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);
			nameValuePairList.add(EmailBasicNameValuePair);
			nameValuePairList.add(GenderBasicNameValuePair);
			nameValuePairList.add(DOBBasicNameValuePair);
			nameValuePairList.add(NameBasicNameValuePair);
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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				updateprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("updateprofileresp", "" + stringBuilder.toString());

			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select111 = GlobalVariables.ServiceUrl
					+ "/fetchmyprofile.php";
			HttpPost httpPost1 = new HttpPost(url_select111);
			BasicNameValuePair UserNumberBasicNameValuePair1 = new BasicNameValuePair(
					"UserNumber", MobileNumberstr);
			authString = MobileNumberstr;
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

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myprofileresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myprofileresp", "" + stringBuilder1.toString());

			if (!myprofileresp.contains("Unauthorized Access")) {
				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"MyProfile", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("myprofile", myprofileresp.toString().trim());
				editor1.commit();

				try{
					JSONArray jsonArray = new JSONArray(myprofileresp);

					if(jsonArray != null && jsonArray.length()>0){
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
						mPrefs.edit().putString("FullName", jsonObject.optString("FullName")).commit();
						mPrefs.edit().putString("MobileNumber", jsonObject.optString("MobileNumber")).commit();
						mPrefs.edit().putString("Email", jsonObject.optString("Email")).commit();
						//MobileNumberstr = mPrefs.getString("MobileNumber", "");


					}

				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent mainIntent = new Intent(MyProfileActivity.this,
				NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MyProfileActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			mainbmp = null;
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {

					Log.d("f.getAbsolutePath()", "" + f.getAbsolutePath());
					imageuploadchkstr = "fromcamera";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								f.getAbsolutePath());
					} else {
						new ConnectionTaskForImageUpload().execute(f
								.getAbsolutePath());
					}

					/*
					 * Bitmap bitmapOrg; BitmapFactory.Options bitmapOptions =
					 * new BitmapFactory.Options();
					 * 
					 * bitmapOrg = BitmapFactory.decodeFile(f.getAbsolutePath(),
					 * bitmapOptions);
					 * 
					 * int width = bitmapOrg.getWidth(); int height =
					 * bitmapOrg.getHeight(); int newWidth = 200; int newHeight
					 * = 200;
					 * 
					 * // calculate the scale - in this case = 0.4f float
					 * scaleWidth = ((float) newWidth) / width; float
					 * scaleHeight = ((float) newHeight) / height;
					 * 
					 * // createa matrix for the manipulation Matrix matrix =
					 * new Matrix(); // resize the bit map
					 * matrix.postScale(scaleWidth, scaleHeight); // rotate the
					 * Bitmap // matrix.postRotate(45);
					 * 
					 * // recreate the new Bitmap Bitmap resizedBitmap =
					 * Bitmap.createBitmap(bitmapOrg, 0, 0, width, height,
					 * matrix, true);
					 * 
					 * mainbmp = resizedBitmap;
					 * 
					 * profilepic.setImageBitmap(mainbmp);
					 * 
					 * String imgString = Base64.encodeToString(
					 * getBytesFromBitmap(mainbmp), Base64.NO_WRAP);
					 * 
					 * if (Build.VERSION.SDK_INT >=
					 * Build.VERSION_CODES.HONEYCOMB) { new
					 * ConnectionTaskForImageUpload().executeOnExecutor(
					 * AsyncTask.THREAD_POOL_EXECUTOR, imgString); } else { new
					 * ConnectionTaskForImageUpload().execute(imgString); }
					 */} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();

				if (picturePath.toString().trim().contains("http")) {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromurl";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				} else {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromgallerynormal";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				}
			}
		} else {
			mainbmp = null;
			Log.d("Result not ok", "Result not ok");
		}

	}

	// ///////

	private class ConnectionTaskForImageUpload extends
			AsyncTask<String, Void, Void> {


		@Override
		protected void onPreExecute() {
			try{
               showProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionImageUpload mAuth1 = new AuthenticateConnectionImageUpload();
			try {
				mAuth1.picturePath = args[0];
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
			try{
              hideProgressBar();

                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(MyProfileActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Change Profile Image")
                        .setAction("Change Profile Image")
                        .setLabel("Change Profile Image").build());

                if (imageuploadresp.equalsIgnoreCase("Error")) {

                    Toast.makeText(
                            MyProfileActivity.this,
                            "Error uploading Image, Please try again or use a different image",
                            Toast.LENGTH_SHORT).show();
                } else if (imageuploadresp.contains("Unauthorized Access")) {
                    Log.e("MyProfileActivity",
                            "imageuploadresp Unauthorized Access");
                    Toast.makeText(MyProfileActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                } else {

                    profilebannerimage.setImageBitmap(mIcon11);

                    Toast.makeText(MyProfileActivity.this, "Image Uploaded",
                            Toast.LENGTH_SHORT).show();

                    if (mIcon11 != null) {
                        mIcon11 = null;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
		}

	}

	public class AuthenticateConnectionImageUpload {

		String picturePath;

		public AuthenticateConnectionImageUpload() {

		}

		public void connection() throws Exception {

			String imagestr = null;

			if (imageuploadchkstr.toString().trim().equalsIgnoreCase("fromurl")) {
				URL url = new URL(picturePath);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				Log.d("img width", "" + myBitmap.getWidth());
				Log.d("img height", "" + myBitmap.getHeight());

				int width = 0;
				int height = 0;

				if (myBitmap.getWidth() <= myBitmap.getHeight()) {

					width = 200;
					height = (myBitmap.getHeight() * 200) / myBitmap.getWidth();
				} else {
					width = (myBitmap.getWidth() * 200) / myBitmap.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				myBitmap = scaleBitmap(myBitmap, width, height);

				Log.d("resize width", "" + myBitmap.getWidth());
				Log.d("resize height", "" + myBitmap.getHeight());

				mainbmp = myBitmap;
				if (myBitmap != null) {
					myBitmap = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			} else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromgallerynormal")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromcamera")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			if (mainbmp != null) {
				mainbmp = null;
			}

			String url_select = GlobalVariables.ServiceUrl + "/imageupload.php";

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);
			BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
					"imagestr", imagestr);

			String authString = imagestr + MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(ImageBasicNameValuePair);
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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				imageuploadresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("imageuploadresp", "" + stringBuilder.toString());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

			} else if (imageuploadresp.contains("Unauthorized Access")) {

			} else {

				String urldisplay = GlobalVariables.ServiceUrl
						+ "/ProfileImages/" + imageuploadresp.trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imageuploadresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}
		}
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);

		float scaleX = newWidth / (float) bitmap.getWidth();
		float scaleY = newHeight / (float) bitmap.getHeight();
		float pivotX = 0;
		float pivotY = 0;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

    private int selectedPosition;
    private ArrayList<String> vehicleNameList = new ArrayList<String>();
    private ArrayList<String> vehicleIDList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    /**
	 * Vehicle Operation=============================>
	 */
	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			searchString = charSequence.toString();
			selectedPosition = -1;
			if (vehicleIDList != null && vehicleIDList.size() == 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					//resetRequest();
					new ConnectionTaskGetModel()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskGetModel().execute();
				}
			}

		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};

    private class ConnectionTaskGetModel extends AsyncTask<String, Void, Void> {
        private String result;

        @Override
        protected void onPreExecute() {
            vehicleNameList.clear();
            vehicleIDList.clear();
           showProgressBar();

        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("", "");
            AuthenticateConnectionModel mAuth1 = new AuthenticateConnectionModel();
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
        protected void onPostExecute(Void aVoid) {
            try {
                hideProgressBar();
                if (!TextUtils.isEmpty(result)) {
                    if (exceptioncheck) {
                        exceptioncheck = false;
                        Toast.makeText(MyProfileActivity.this,
                                getResources().getString(R.string.exceptionstring),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.isNull("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {
                        if (!jsonObject.isNull("data")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                vehicleNameList.add(jObj.optString("vehicleModel"));
                                vehicleIDList.add(jObj.optString("id"));

                            }
                            if (vehicleIDList.size() > 0) {
                                adapter = new ArrayAdapter<String>
                                        (MyProfileActivity.this, android.R.layout.select_dialog_item, vehicleNameList);
                                etModelName.setThreshold(1);
                                etModelName.setAdapter(adapter);//set
                            }
                            if(vehicleNameList != null && vehicleNameList.size() >0 && !TextUtils.isEmpty(SPreference.getPref(MyProfileActivity.this).getString(SPreference.MODEL_NO,""))){
                                selectedPosition = vehicleNameList.indexOf(SPreference.getPref(MyProfileActivity.this).getString(SPreference.MODEL_NO,""));
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class AuthenticateConnectionModel {

            public AuthenticateConnectionModel() {

            }

            public void connection() throws Exception {

                // Connect to google.com
                HttpClient httpClient = new DefaultHttpClient();
                String url_select = GlobalVariables.ServiceUrl + "/getVehicle.php";
                HttpPost httpPost = new HttpPost(url_select);
                BasicNameValuePair searchBasicNameValuePair = new BasicNameValuePair(
                        "q", searchString);

                String authString = searchString;
                BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                        GlobalMethods.calculateCMCAuthString(authString));
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(searchBasicNameValuePair);
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

                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    result = stringBuilder.append(bufferedStrChunk).toString();
                }


                Log.d("result", "" + stringBuilder.toString());
            }
        }

    }

    private boolean isVehicleDetailChanged(){
        if(!SPreference.getPref(MyProfileActivity.this).getString(SPreference.MODEL_NO,"").equalsIgnoreCase(etModelName.getText().toString().trim())){
            return  true;
        }else if(!SPreference.getPref(MyProfileActivity.this).getString(SPreference.REGISTRATION_NO,"").equalsIgnoreCase(etRegistrationNo.getText().toString().trim())){
            return  true;
        }else if(!SPreference.getPref(MyProfileActivity.this).getBoolean(SPreference.IS_COMMERCIAL,false) != ((CheckBox) findViewById(R.id.ch2)).isChecked()){
            return  true;
        }
        return false;
    }

	private void showProgressBar(){
		try{
			onedialog = new Dialog(MyProfileActivity.this);
			onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onedialog.setContentView(R.layout.dialog_ishare_loader);
			onedialog.setCancelable(false);
			onedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			// onedialog.getWindow().setB(getResources().getColor(R.color.colorTransparent));
			onedialog.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void hideProgressBar(){
		try{
			if(onedialog != null)
				onedialog.dismiss();
			onedialog = null;
		}catch (Exception e){
			e.printStackTrace();
		}

	}
}
