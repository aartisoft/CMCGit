package com.clubmycab.fragment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.DatabaseHandler;
import com.clubmycab.ListViewAdapter;
import com.clubmycab.MemberRideFragmentActivity;
import com.clubmycab.MyRidesListClass;
import com.clubmycab.MyRidesObject;
import com.clubmycab.PagingListView;
import com.clubmycab.R;
import com.clubmycab.SafeAsyncTask;
import com.clubmycab.ShowHistoryRidesAdaptor;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.MyRidesActivity;
import com.clubmycab.ui.MyRidesActivity.AuthenticateConnectionFetchPool;
import com.clubmycab.ui.MyRidesActivity.AuthenticateConnectionShowRidesHistory;
import com.clubmycab.ui.NotificationListActivity;
import com.clubmycab.ui.UniversalDrawer;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.navdrawer.SimpleSideDrawer;

public class MyRidesFragment extends Fragment implements
		AsyncTaskResultListener {
	String FullName;
	String MobileNumberstr;
	String poolresponse;
	String rideshistoryresponse;
	ListView mypoollist;
	PagingListView listView;
	boolean exceptioncheck = false;
	Tracker tracker;
	ArrayList<RideDetailsModel> arrayRideDetailsModels = new ArrayList<RideDetailsModel>();
	ListViewAdapter adapter;
	ShowHistoryRidesAdaptor showhisadaptor;
	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;
	ImageView sidemenu;
	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;
	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView mypreferences;
	TextView about;
	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;
	String readunreadnotiresp;
	Bitmap mIcon11;
	RelativeLayout myridesrl;
	Button showhistory;
	String latestcabid;
	int poolhistorypagenumber;
	boolean cabchk;
	boolean shouldGoBack = true;
	boolean onCreateCalled;
	boolean showHistoryCalled;

	public static MyRidesFragment newInstance(Bundle args) {
		MyRidesFragment fragment = new MyRidesFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_myrides, container, false);

		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getActivity().getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							getActivity().finish();
							startActivity(intent);

						}
					});

			builder.show();
			return;
		}

		myridesrl = (RelativeLayout) view.findViewById(R.id.myridesrl);
		myridesrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("myridesrl", "myridesrl");
			}
		});

/*		GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyRides");

		UniversalDrawer drawer = new UniversalDrawer(getActivity(), tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) view.findViewById(R.id.profilepic);
		notificationimg = (ImageView) view.findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) view
				.findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getActivity().getSharedPreferences(
				"FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

		username = (TextView) view.findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(
				getActivity().getAssets(), "NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) view.findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getActivity()
				.getAssets(), "NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(getActivity(),
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) view
				.findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) view.findViewById(R.id.unreadnoticount);

		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}*/

		// ///////////////
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new ConnectionTaskForreadunreadnotification()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new ConnectionTaskForreadunreadnotification().execute();
		// }

		// ///////////////
		SharedPreferences mPrefs111 = getActivity().getSharedPreferences(
				"userimage", 0);
		String imgname = mPrefs111.getString("imgname", "");
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {
		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

		mypoollist = (ListView) view.findViewById(R.id.mypoollist);

		showHistoryCalled = false;
		onCreateCalled = true;

		String comefrom = getActivity().getIntent().getStringExtra("comefrom");

		Log.d("MyRidesActivity", "comefrom : " + comefrom);

		if (comefrom == null || comefrom.isEmpty()
				|| comefrom.equalsIgnoreCase("null")) {
			String PoolResponseSplash = getActivity().getIntent()
					.getStringExtra("PoolResponseSplash");

			Log.d("MyRidesActivity", "PoolResponseSplash : "
					+ PoolResponseSplash);

			onCreateCalled = false;

			if (PoolResponseSplash == null || PoolResponseSplash.isEmpty()
					|| PoolResponseSplash.equalsIgnoreCase("null")) {

				shouldGoBack = true;

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchPool().execute();
				}

			} else {

				shouldGoBack = false;

				poolresponse = PoolResponseSplash;
				ConnectionTaskForFetchPoolPostExecute();
				Log.d("MyRidesActivity",
						"PoolResponseSplash arrayRideDetailsModels size : "
								+ arrayRideDetailsModels.size());
				if (arrayRideDetailsModels.size() == 1) {
					mypoollist.performItemClick(mypoollist.getAdapter()
							.getView(0, null, null), 0, mypoollist.getAdapter()
							.getItemId(0));
				}
			}
		} else {

			// Mark notificaiton read call
			String nid = getActivity().getIntent().getStringExtra("nid");
			String params = "rnum=" + "&nid=" + nid + "&auth="
					+ GlobalMethods.calculateCMCAuthString(nid);
			String endpoint = GlobalVariables.ServiceUrl
					+ "/UpdateNotificationStatusToRead.php";
			Log.d("MyRidesActivity",
					"UpdateNotificationStatusToRead endpoint : " + endpoint
							+ " params : " + params);
			new GlobalAsyncTask(getActivity(), endpoint, params, null, this,
					false, "UpdateNotificationStatusToRead", false);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchPool().execute();
			}

		}

	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
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

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (poolresponse.contains("Unauthorized Access")) {
				Log.e("MyRidesActivity", "poolresponse Unauthorized Access");
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ConnectionTaskForFetchPoolPostExecute();

			clearBookedOrCarPreference();

			String comefrom = getActivity().getIntent().getStringExtra(
					"comefrom");

			if (onCreateCalled && comefrom != null && !comefrom.isEmpty()
					&& !comefrom.equalsIgnoreCase("null")) {

				onCreateCalled = false;

				String cabID = getActivity().getIntent()
						.getStringExtra("cabID");
				// int index = CabId.indexOf(cabID);
				int index = -1;
				for (int i = 0; i < arrayRideDetailsModels.size(); i++) {
					if (arrayRideDetailsModels.get(i).getCabId().equals(cabID)) {
						index = i;
					}
				}

				Log.d("MyRidesActivity", "onPostExecute comefrom : " + comefrom
						+ " cabID : " + cabID + " index : " + index);

				if (index != -1) {
					mypoollist.performItemClick(mypoollist.getAdapter()
							.getView(index, null, null), index, mypoollist
							.getAdapter().getItemId(index));
				}
				// if
				// (comefrom.equals(UpcomingStartTripAlarm.ALARM_TYPE_UPCOMING))
				// {
				//
				// mypoollist.performItemClick(mypoollist.getAdapter()
				// .getView(index, null, null), index, mypoollist
				// .getAdapter().getItemId(index));
				//
				// } else if (comefrom
				// .equals(UpcomingStartTripAlarm.ALARM_TYPE_START_TRIP)) {
				//
				// mypoollist.performItemClick(mypoollist.getAdapter()
				// .getView(index, null, null), index, mypoollist
				// .getAdapter().getItemId(index));
				// }

			}
		}

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void getResult(String response, String uniqueID) {
		// TODO Auto-generated method stub

	}

	private void ConnectionTaskForFetchPoolPostExecute() {
		if (poolresponse.equalsIgnoreCase("No Pool Created Yet!!")
				|| poolresponse.equalsIgnoreCase("[]")) {
			Toast.makeText(getActivity(), "No active rides!", Toast.LENGTH_LONG)
					.show();
		} else {

			// CabId.clear();
			// MobileNumber.clear();
			// OwnerName.clear();
			// FromLocation.clear();
			// ToLocation.clear();
			//
			// FromShortName.clear();
			// ToShortName.clear();
			//
			// TravelDate.clear();
			// TravelTime.clear();
			// Seats.clear();
			// RemainingSeats.clear();
			// Seat_Status.clear();
			// Distance.clear();
			// OpenTime.clear();
			// CabStatus.clear();
			// imagename.clear();
			//
			// BookingRefNo.clear();
			// DriverName.clear();
			// DriverNumber.clear();
			// CarNumber.clear();
			// CabName.clear();
			//
			// ExpTripDuration.clear();
			// status.clear();

			arrayRideDetailsModels = new ArrayList<RideDetailsModel>();

			try {

				// //////////////////////////////////////////////
				Gson gson = new Gson();
				arrayRideDetailsModels = gson.fromJson(poolresponse,
						new TypeToken<ArrayList<RideDetailsModel>>() {
						}.getType());
				Log.d("MyRidesActivity",
						"GSON pools : "
								+ gson.toJson(arrayRideDetailsModels)
										.toString());

				// //////////////////////////////////////////////

				ArrayList<String> OwnerName = new ArrayList<String>();
				ArrayList<String> FromShortName = new ArrayList<String>();
				ArrayList<String> ToShortName = new ArrayList<String>();
				ArrayList<String> TravelDate = new ArrayList<String>();
				ArrayList<String> TravelTime = new ArrayList<String>();
				ArrayList<String> Seat_Status = new ArrayList<String>();
				ArrayList<String> imagename = new ArrayList<String>();

				// JSONArray subArray = new JSONArray(poolresponse);
				String allcabids = "s";
				for (int i = 0; i < arrayRideDetailsModels.size(); i++) {
					allcabids += "'" + arrayRideDetailsModels.get(i).getCabId()
							+ "',";

					OwnerName.add(arrayRideDetailsModels.get(i).getOwnerName());
					FromShortName.add(arrayRideDetailsModels.get(i)
							.getFromShortName());
					ToShortName.add(arrayRideDetailsModels.get(i)
							.getToShortName());
					TravelDate.add(arrayRideDetailsModels.get(i)
							.getTravelDate());
					TravelTime.add(arrayRideDetailsModels.get(i)
							.getTravelTime());
					Seat_Status.add(arrayRideDetailsModels.get(i)
							.getSeat_Status());
					imagename.add(arrayRideDetailsModels.get(i).getImagename());
				}
				// for (int i = 0; i < subArray.length(); i++) {
				// try {
				// CabId.add(subArray.getJSONObject(i).getString("CabId")
				// .toString());
				//
				// allcabids += "'"
				// + subArray.getJSONObject(i).getString("CabId")
				// .toString().trim() + "',";
				//
				// MobileNumber.add(subArray.getJSONObject(i)
				// .getString("MobileNumber").toString());
				// OwnerName.add(subArray.getJSONObject(i)
				// .getString("OwnerName").toString());
				// FromLocation.add(subArray.getJSONObject(i)
				// .getString("FromLocation").toString());
				// ToLocation.add(subArray.getJSONObject(i)
				// .getString("ToLocation").toString());
				//
				// FromShortName.add(subArray.getJSONObject(i)
				// .getString("FromShortName").toString());
				// ToShortName.add(subArray.getJSONObject(i)
				// .getString("ToShortName").toString());
				//
				// TravelDate.add(subArray.getJSONObject(i)
				// .getString("TravelDate").toString());
				// TravelTime.add(subArray.getJSONObject(i)
				// .getString("TravelTime").toString());
				// Seats.add(subArray.getJSONObject(i).getString("Seats")
				// .toString());
				// RemainingSeats.add(subArray.getJSONObject(i)
				// .getString("RemainingSeats").toString());
				// Seat_Status.add(subArray.getJSONObject(i)
				// .getString("Seat_Status").toString());
				// Distance.add(subArray.getJSONObject(i)
				// .getString("Distance").toString());
				// OpenTime.add(subArray.getJSONObject(i)
				// .getString("OpenTime").toString());
				// CabStatus.add(subArray.getJSONObject(i)
				// .getString("CabStatus").toString());
				// imagename.add(subArray.getJSONObject(i)
				// .getString("imagename").toString());
				//
				// BookingRefNo.add(subArray.getJSONObject(i)
				// .getString("BookingRefNo").toString());
				// DriverName.add(subArray.getJSONObject(i)
				// .getString("DriverName").toString());
				// DriverNumber.add(subArray.getJSONObject(i)
				// .getString("DriverNumber").toString());
				// CarNumber.add(subArray.getJSONObject(i)
				// .getString("CarNumber").toString());
				// CabName.add(subArray.getJSONObject(i)
				// .getString("CabName").toString());
				//
				// ExpTripDuration.add(subArray.getJSONObject(i)
				// .getString("ExpTripDuration").toString());
				// status.add(subArray.getJSONObject(i)
				// .getString("status").toString());
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

				DatabaseHandler db = new DatabaseHandler(getActivity());
				allcabids = allcabids.substring(1, allcabids.length() - 1);
				db.deleteArchieveChats(allcabids);

				adapter = new ListViewAdapter(getActivity(), FromShortName,
						ToShortName, TravelDate, TravelTime, Seat_Status,
						OwnerName, imagename);
				mypoollist.setAdapter(adapter);

				mypoollist.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d("arg2", "" + arg2);

						RideDetailsModel rideDetailsModel = arrayRideDetailsModels
								.get(arg2);
						String mobileNumber = rideDetailsModel
								.getMobileNumber();

						if (mobileNumber.equalsIgnoreCase(MobileNumberstr)) {

							final Intent mainIntent = new Intent(getActivity(),
									CheckPoolFragmentActivity.class);
							mainIntent.putExtra("RideDetailsModel",
									(new Gson()).toJson(rideDetailsModel));

							// mainIntent.putExtra("CabId", CabId.get(arg2));
							// mainIntent.putExtra("MobileNumber",
							// MobileNumber.get(arg2));
							// mainIntent.putExtra("OwnerName",
							// OwnerName.get(arg2));
							// mainIntent.putExtra("OwnerImage",
							// imagename.get(arg2));
							// mainIntent.putExtra("FromLocation",
							// FromLocation.get(arg2));
							// mainIntent.putExtra("ToLocation",
							// ToLocation.get(arg2));
							//
							// mainIntent.putExtra("FromShortName",
							// FromShortName.get(arg2));
							// mainIntent.putExtra("ToShortName",
							// ToShortName.get(arg2));
							//
							// mainIntent.putExtra("TravelDate",
							// TravelDate.get(arg2));
							// mainIntent.putExtra("TravelTime",
							// TravelTime.get(arg2));
							// mainIntent.putExtra("Seats", Seats.get(arg2));
							// mainIntent.putExtra("RemainingSeats",
							// RemainingSeats.get(arg2));
							// mainIntent.putExtra("Seat_Status",
							// Seat_Status.get(arg2));
							// mainIntent.putExtra("Distance",
							// Distance.get(arg2));
							// mainIntent.putExtra("OpenTime",
							// OpenTime.get(arg2));
							//
							// mainIntent.putExtra("CabStatus",
							// CabStatus.get(arg2));
							//
							// mainIntent.putExtra("BookingRefNo",
							// BookingRefNo.get(arg2));
							// mainIntent.putExtra("DriverName",
							// DriverName.get(arg2));
							// mainIntent.putExtra("DriverNumber",
							// DriverNumber.get(arg2));
							// mainIntent.putExtra("CarNumber",
							// CarNumber.get(arg2));
							// mainIntent.putExtra("CabName",
							// CabName.get(arg2));
							//
							// mainIntent.putExtra("ExpTripDuration",
							// ExpTripDuration.get(arg2));
							// mainIntent.putExtra("status", status.get(arg2));

							getActivity().startActivity(mainIntent);

						} else {

							final Intent mainIntent = new Intent(getActivity(),
									MemberRideFragmentActivity.class);
							mainIntent.putExtra("RideDetailsModel",
									(new Gson()).toJson(rideDetailsModel));

							// mainIntent.putExtra("CabId", CabId.get(arg2));
							// mainIntent.putExtra("MobileNumber",
							// MobileNumber.get(arg2));
							// mainIntent.putExtra("OwnerName",
							// OwnerName.get(arg2));
							// mainIntent.putExtra("OwnerImage",
							// imagename.get(arg2));
							// mainIntent.putExtra("FromLocation",
							// FromLocation.get(arg2));
							// mainIntent.putExtra("ToLocation",
							// ToLocation.get(arg2));
							//
							// mainIntent.putExtra("FromShortName",
							// FromShortName.get(arg2));
							// mainIntent.putExtra("ToShortName",
							// ToShortName.get(arg2));
							//
							// mainIntent.putExtra("TravelDate",
							// TravelDate.get(arg2));
							// mainIntent.putExtra("TravelTime",
							// TravelTime.get(arg2));
							// mainIntent.putExtra("Seats", Seats.get(arg2));
							// mainIntent.putExtra("RemainingSeats",
							// RemainingSeats.get(arg2));
							// mainIntent.putExtra("Seat_Status",
							// Seat_Status.get(arg2));
							// mainIntent.putExtra("Distance",
							// Distance.get(arg2));
							// mainIntent.putExtra("OpenTime",
							// OpenTime.get(arg2));
							//
							// mainIntent.putExtra("CabStatus",
							// CabStatus.get(arg2));
							//
							// mainIntent.putExtra("BookingRefNo",
							// BookingRefNo.get(arg2));
							// mainIntent.putExtra("DriverName",
							// DriverName.get(arg2));
							// mainIntent.putExtra("DriverNumber",
							// DriverNumber.get(arg2));
							// mainIntent.putExtra("CarNumber",
							// CarNumber.get(arg2));
							// mainIntent.putExtra("CabName",
							// CabName.get(arg2));
							//
							// mainIntent.putExtra("ExpTripDuration",
							// ExpTripDuration.get(arg2));
							// mainIntent.putExtra("status", status.get(arg2));

							getActivity().startActivity(mainIntent);
						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		showhistory = (Button) getView().findViewById(R.id.showhistory);
		showhistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showHistoryCalled = true;

				showhistory.setVisibility(View.GONE);
				mypoollist.setVisibility(View.GONE);
				listView = (PagingListView) getActivity().findViewById(
						R.id.paging_list_view);
				listView.setVisibility(View.VISIBLE);

				showhisadaptor = new ShowHistoryRidesAdaptor(getActivity());

				latestcabid = "";
				poolhistorypagenumber = -1;
				cabchk = false;

				listView.setAdapter(showhisadaptor);
				listView.setHasMoreItems(true);
				listView.setPagingableListener(new PagingListView.Pagingable() {
					@Override
					public void onLoadMoreItems() {
						if (!cabchk) {
							// new CountryAsyncTask().execute();
							new ConnectionTaskForShowRidesHistory()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											Integer.toString(++poolhistorypagenumber));
						} else {
							listView.onFinishLoading(false, null);
						}
					}
				});

				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d("arg2", "" + arg2);

						RideDetailsModel rideDetailsModel = arrayRideDetailsModels
								.get(arg2);
						String mobileNumber = rideDetailsModel
								.getMobileNumber();

						if (mobileNumber.equalsIgnoreCase(MobileNumberstr)) {

							final Intent mainIntent = new Intent(getActivity(),
									CheckPoolFragmentActivity.class);
							mainIntent.putExtra("RideDetailsModel",
									(new Gson()).toJson(rideDetailsModel));

							getActivity().startActivity(mainIntent);

						} else {

							final Intent mainIntent = new Intent(getActivity(),
									MemberRideFragmentActivity.class);
							mainIntent.putExtra("RideDetailsModel",
									(new Gson()).toJson(rideDetailsModel));

							getActivity().startActivity(mainIntent);
						}
					}
				});

			}
		});
	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/FetchMyPools.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);
			String authString = MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse FetchMyPools", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());
		}
	}

	private void clearBookedOrCarPreference() {

		if (arrayRideDetailsModels.size() > 0) {

			SharedPreferences sharedPreferences = getActivity()
					.getSharedPreferences("AlreadyBookedOrOwnCar", 0);
			String arrayListString = sharedPreferences.getString("arraylist",
					"");

			ArrayList<String> arrayList;
			if (arrayListString == null || arrayListString.isEmpty()) {
				arrayList = null;
			} else {
				Gson gson = new Gson();
				arrayList = gson.fromJson(arrayListString, ArrayList.class);
			}

			if (arrayList != null && arrayList.size() > 0) {
				ArrayList<String> newArrayList = new ArrayList<String>();
				for (String string : arrayList) {
					for (RideDetailsModel rideDetailsModel : arrayRideDetailsModels) {
						if (rideDetailsModel.getCabId().equals(string)) {
							newArrayList.add(string);
						}
					}
					// if (CabId.indexOf(string) != -1) {
					// newArrayList.add(string);
					// }
				}

				Gson gson = new Gson();

				String string = gson.toJson(newArrayList).toString();

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("arraylist", string.trim());
				editor.commit();

			}
		}

	}

	private class ConnectionTaskForShowRidesHistory extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionShowRidesHistory mAuth1 = new AuthenticateConnectionShowRidesHistory();
			try {
				mAuth1.cid = args[0];
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

			if (rideshistoryresponse.contains("Unauthorized Access")) {
				Log.e("MyRidesActivity",
						"rideshistoryresponse Unauthorized Access");
				Toast.makeText(getActivity(),
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideshistoryresponse.equalsIgnoreCase("No Pool Created Yet!!")
					|| rideshistoryresponse.equalsIgnoreCase("[]")) {
				Toast.makeText(getActivity(), "No More history",
						Toast.LENGTH_LONG).show();

				latestcabid = "khatam";
				poolhistorypagenumber = -1;
				cabchk = true;
				listView.onFinishLoading(false, null);

			} else {

				MyRidesListClass.listmyrides.clear();

				// if (latestcabid.equalsIgnoreCase("") ||
				// latestcabid.isEmpty()) {
				if (poolhistorypagenumber == -1) {

					for (int i = 0; i < arrayRideDetailsModels.size(); i++) {

						RideDetailsModel rideDetailsModel = arrayRideDetailsModels
								.get(i);
						MyRidesObject ride = new MyRidesObject();

						ride.setCabId(rideDetailsModel.getCabId());
						ride.setMobileNumber(rideDetailsModel.getMobileNumber());
						ride.setOwnerName(rideDetailsModel.getOwnerName());
						ride.setFromLocation(rideDetailsModel.getFromLocation());
						ride.setToLocation(rideDetailsModel.getToLocation());
						ride.setFromShortName(rideDetailsModel
								.getFromShortName());
						ride.setToShortName(rideDetailsModel.getToShortName());

						ride.setTravelDate(rideDetailsModel.getTravelDate());
						ride.setTravelTime(rideDetailsModel.getTravelTime());
						ride.setSeats(rideDetailsModel.getSeats());
						ride.setRemainingSeats(rideDetailsModel
								.getRemainingSeats());
						ride.setSeat_Status(rideDetailsModel.getSeat_Status());
						ride.setDistance(rideDetailsModel.getDistance());
						ride.setOpenTime(rideDetailsModel.getOpenTime());
						ride.setCabStatus(rideDetailsModel.getCabStatus());
						ride.setImagename(rideDetailsModel.getImagename());

						ride.setBookingRefNo(rideDetailsModel.getBookingRefNo());
						ride.setDriverName(rideDetailsModel.getDriverName());
						ride.setDriverNumber(rideDetailsModel.getDriverNumber());
						ride.setCarNumber(rideDetailsModel.getCarNumber());

						ride.setCabName(rideDetailsModel.getCabName());
						ride.setStatus(rideDetailsModel.getStatus());
						ride.setExpTripDuration(rideDetailsModel
								.getExpTripDuration());

						MyRidesListClass.listmyrides.add(ride);
					}
				}

				try {

					// //////////////////////////////////////////////
					Gson gson = new Gson();
					ArrayList<RideDetailsModel> arrayList = gson.fromJson(
							rideshistoryresponse,
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());
					Log.d("MyRidesActivity", "GSON history pools : "
							+ gson.toJson(arrayList).toString());

					// //////////////////////////////////////////////

					// JSONArray subArray = new JSONArray(rideshistoryresponse);
					for (int i = 0; i < arrayList.size(); i++) {

						RideDetailsModel rideDetailsModel = arrayList.get(i);
						MyRidesObject ride = new MyRidesObject();

						ride.setCabId(rideDetailsModel.getCabId());
						ride.setMobileNumber(rideDetailsModel.getMobileNumber());
						ride.setOwnerName(rideDetailsModel.getOwnerName());
						ride.setFromLocation(rideDetailsModel.getFromLocation());
						ride.setToLocation(rideDetailsModel.getToLocation());
						ride.setFromShortName(rideDetailsModel
								.getFromShortName());
						ride.setToShortName(rideDetailsModel.getToShortName());

						ride.setTravelDate(rideDetailsModel.getTravelDate());
						ride.setTravelTime(rideDetailsModel.getTravelTime());
						ride.setSeats(rideDetailsModel.getSeats());
						ride.setRemainingSeats(rideDetailsModel
								.getRemainingSeats());
						ride.setSeat_Status(rideDetailsModel.getSeat_Status());
						ride.setDistance(rideDetailsModel.getDistance());
						ride.setOpenTime(rideDetailsModel.getOpenTime());

						ride.setCabStatus(rideDetailsModel.getCabStatus());

						ride.setImagename(rideDetailsModel.getImagename());

						ride.setBookingRefNo(rideDetailsModel.getBookingRefNo());
						ride.setDriverName(rideDetailsModel.getDriverName());
						ride.setDriverNumber(rideDetailsModel.getDriverNumber());
						ride.setCarNumber(rideDetailsModel.getCarNumber());

						ride.setCabName(rideDetailsModel.getCabName());
						ride.setStatus(rideDetailsModel.getStatus());
						ride.setExpTripDuration(rideDetailsModel
								.getExpTripDuration());

						MyRidesListClass.listmyrides.add(ride);

						arrayRideDetailsModels.add(rideDetailsModel);

						// CabId.add(subArray.getJSONObject(i).getString("CabId")
						// .toString());
						//
						// MobileNumber.add(subArray.getJSONObject(i)
						// .getString("MobileNumber").toString());
						// OwnerName.add(subArray.getJSONObject(i)
						// .getString("OwnerName").toString());
						// FromLocation.add(subArray.getJSONObject(i)
						// .getString("FromLocation").toString());
						// ToLocation.add(subArray.getJSONObject(i)
						// .getString("ToLocation").toString());
						//
						// FromShortName.add(subArray.getJSONObject(i)
						// .getString("FromShortName").toString());
						// ToShortName.add(subArray.getJSONObject(i)
						// .getString("ToShortName").toString());
						//
						// TravelDate.add(subArray.getJSONObject(i)
						// .getString("TravelDate").toString());
						// TravelTime.add(subArray.getJSONObject(i)
						// .getString("TravelTime").toString());
						// Seats.add(subArray.getJSONObject(i).getString("Seats")
						// .toString());
						// RemainingSeats.add(subArray.getJSONObject(i)
						// .getString("RemainingSeats").toString());
						// Seat_Status.add(subArray.getJSONObject(i)
						// .getString("Seat_Status").toString());
						// Distance.add(subArray.getJSONObject(i)
						// .getString("Distance").toString());
						// OpenTime.add(subArray.getJSONObject(i)
						// .getString("OpenTime").toString());
						// CabStatus.add(subArray.getJSONObject(i)
						// .getString("CabStatus").toString());
						// imagename.add(subArray.getJSONObject(i)
						// .getString("imagename").toString());
						//
						// BookingRefNo.add(subArray.getJSONObject(i)
						// .getString("BookingRefNo").toString());
						// DriverName.add(subArray.getJSONObject(i)
						// .getString("DriverName").toString());
						// DriverNumber.add(subArray.getJSONObject(i)
						// .getString("DriverNumber").toString());
						// CarNumber.add(subArray.getJSONObject(i)
						// .getString("CarNumber").toString());
						//
						// CabName.add(subArray.getJSONObject(i)
						// .getString("CabName").toString());
						// status.add(subArray.getJSONObject(i)
						// .getString("status").toString());
						// ExpTripDuration.add(subArray.getJSONObject(i)
						// .getString("ExpTripDuration").toString());

					}

					Log.d("MyRidesListClass.listmyrides", ""
							+ MyRidesListClass.listmyrides);
					Log.d("MyRidesListClass.listmyrides", ""
							+ MyRidesListClass.listmyrides.size());

					new CountryAsyncTask().execute();

					// ///////////////////

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
	
	public class AuthenticateConnectionShowRidesHistory {

		public String cid;

		public AuthenticateConnectionShowRidesHistory() {

		}

		@SuppressWarnings("deprecation")
		public void connection() throws Exception {

			HttpClient httpclient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchmypoolshistorypagewise.php";
			HttpPost httpPost = new HttpPost(url_select11);

			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"page", cid.toString().trim());

			String authString = cid.toString().trim() + MobileNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);

			Log.d("url_select11", "" + url_select11);
			HttpResponse httpResponse = httpclient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				rideshistoryresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("rideshistoryresponse", "" + stringBuilder.toString());
		}
	}
	
	private class CountryAsyncTask extends SafeAsyncTask<List<MyRidesObject>> {

		@Override
		public List<MyRidesObject> call() throws Exception {
			List<MyRidesObject> result = null;
			result = MyRidesListClass.listmyrides;
			Thread.sleep(3000);
			return result;
		}

		@Override
		protected void onSuccess(List<MyRidesObject> newItems) throws Exception {
			super.onSuccess(newItems);
			if (newItems.size() > 0) {
				latestcabid = newItems.get(newItems.size() - 1).getCabId();
				cabchk = false;
				listView.onFinishLoading(true, newItems);
			} else {
				latestcabid = "khatam";
				poolhistorypagenumber = -1;
				cabchk = true;
				listView.onFinishLoading(false, null);
			}
		}
	}
}