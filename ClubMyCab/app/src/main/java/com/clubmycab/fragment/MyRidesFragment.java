package com.clubmycab.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.clubmycab.MyRidesListClass;
import com.clubmycab.PagingListView;
import com.clubmycab.R;
import com.clubmycab.SafeAsyncTask;
import com.clubmycab.ShowHistoryRidesAdaptor;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.XCheckPoolFragmentActivty;
import com.clubmycab.ui.XMemberRideFragmentActivity;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.navdrawer.SimpleSideDrawer;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

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
	MyRidesAdapter adapter;
	ShowHistoryRidesAdaptor showhisadaptor;
	ImageView sidemenu;
	private SimpleSideDrawer mNav;
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
	private String _CabId,comefrom;
	public static MyRidesFragment fragment;
	private boolean isFirstLaunch;
    private Activity activity;
	
	public static MyRidesFragment newInstance(Bundle args) {
		if(fragment == null){
			fragment = new MyRidesFragment();
		}
		try{
			fragment.setArguments(args);
		}catch (Exception e){
			e.printStackTrace();
		}
		return fragment;
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = activity;
    }

    public static MyRidesFragment getInstance(){
		return fragment;
	}
	private boolean isFetchPoolRunning;
	private boolean isComingFromSplash;
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("HomeScreen");
		tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Opened").setLabel("MY Rides Tab").build());

		SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_myrides, container, false);

		return v;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//refershRideListData("");
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
							Intent mainIntent = new Intent(getActivity(),
									NewHomeScreen.class);
							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(mainIntent);

						}
					});

			builder.show();
			return;
		}
		showhistory = (Button) getView().findViewById(R.id.showhistory);
		showhistory.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));

		((TextView)view.findViewById(R.id.tvNoRideAvlble)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		myridesrl = (RelativeLayout) view.findViewById(R.id.myridesrl);
		myridesrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("myridesrl", "myridesrl");
			}
		});
		
		mypoollist = (ListView) view.findViewById(R.id.mypoollist);

		showHistoryCalled = false;
		onCreateCalled = true;
		
		if(getArguments() != null){
			Bundle bundle = getArguments();
			if(bundle.containsKey("comefrom") && bundle.containsKey("cabID")){
                _CabId = bundle.getString("cabID");
                comefrom = bundle.getString("comefrom");
				refershRideListData(bundle.getString("cabID"));
			}else if(bundle.containsKey("PoolResponseSplash")){
				String PoolResponseSplash = getArguments().getString("PoolResponseSplash");
				poolresponse = PoolResponseSplash;
				ConnectionTaskForFetchPoolPostExecute();
				Log.d("MyRidesActivity",
						"PoolResponseSplash arrayRideDetailsModels size : "
								+ arrayRideDetailsModels.size());
				if (arrayRideDetailsModels.size() == 1) {
					try{
						if(mypoollist !=null){
							mypoollist.performItemClick(mypoollist.getAdapter()
									.getView(0, null, null), 0, mypoollist.getAdapter()
									.getItemId(0));
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}

			}else {
				refershRideListData("");

			}
			
		}else {
			refershRideListData("");
		}
		

	}
	
	
	public void refershRideListData(String cabId){
		if(getActivity() != null){
			if(!isFetchPoolRunning){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchPool().execute();
				}
			}
		}
	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			try{
				isFetchPoolRunning = true;
				if(getView() != null)
				getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			}catch (Exception e){
				e.printStackTrace();
			}
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
			try{

				isFetchPoolRunning = false;
				if(getView() == null)
					return;
				if(getView() != null)
					getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if(getView().findViewById(R.id.paging_list_view) != null){
					getView().findViewById(R.id.paging_list_view).setVisibility(View.GONE);
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
					Toast.makeText(getActivity(), getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
					return;
				}


				ConnectionTaskForFetchPoolPostExecute();
				clearBookedOrCarPreference();
                if ( comefrom != null && !comefrom.isEmpty()
                        && !comefrom.equalsIgnoreCase("null")) {
                    onCreateCalled = false;
                    // int index = CabId.indexOf(cabID);
                    int index = -1;
                    if(!TextUtils.isEmpty(_CabId)){
                        for (int i = 0; i < arrayRideDetailsModels.size(); i++) {
                            if (arrayRideDetailsModels.get(i).getCabId().equals(_CabId)) {
                                index = i;
                            }
                        }
                        if (index != -1) {
                            mypoollist.performItemClick(mypoollist.getAdapter()
                                    .getView(index, null, null), index, mypoollist
                                    .getAdapter().getItemId(index));
                        }

                    }

                }
			
			}catch(Exception e){
				e.printStackTrace();
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
			/*Toast.makeText(getActivity(), "No active rides! Join a car pool or offer a ride now.", Toast.LENGTH_LONG)
					.show();*/
			getView().findViewById(R.id.llErrorMsg).setVisibility(View.VISIBLE);
		} else {
			getView().findViewById(R.id.llErrorMsg).setVisibility(View.GONE);
			arrayRideDetailsModels = new ArrayList<RideDetailsModel>();

			try {

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
				ArrayList<Boolean> isOwner = new ArrayList<Boolean>();
				ArrayList<RideDetailsModel> arrayListRide = new ArrayList<RideDetailsModel>();


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
					isOwner.add(arrayRideDetailsModels.get(i).getIsOwner().equalsIgnoreCase("Y") ? true:false);

					arrayListRide.add(arrayRideDetailsModels.get(i));
				}
				

			/*	DatabaseHandler db = new DatabaseHandler(getActivity());
				allcabids = allcabids.substring(1, allcabids.length() - 1);
				db.deleteArchieveChats(allcabids);*/

				adapter = new MyRidesAdapter(getActivity(), FromShortName,
						ToShortName, TravelDate, TravelTime, Seat_Status,
						OwnerName, imagename,isOwner,arrayListRide);
				mypoollist.setAdapter(adapter);

				mypoollist.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d("arg2", "" + arg2);

						RideDetailsModel rideDetailsModel = arrayRideDetailsModels
								.get(arg2);
						if(!rideDetailsModel.getRideType().equalsIgnoreCase("3")){
                            String mobileNumber = rideDetailsModel
                                    .getMobileNumber();

                            if (mobileNumber.equalsIgnoreCase(MobileNumberstr)) {

                                final Intent mainIntent = new Intent(getActivity(), XCheckPoolFragmentActivty.class);
                                mainIntent.putExtra("RideDetailsModel", (new Gson()).toJson(rideDetailsModel));
                                getActivity().startActivity(mainIntent);

                            } else {

                                final Intent mainIntent = new Intent(getActivity(),
                                        XMemberRideFragmentActivity.class);
                                mainIntent.putExtra("RideDetailsModel",
                                        (new Gson()).toJson(rideDetailsModel));

                                getActivity().startActivity(mainIntent);
                            }
                        }
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

			showhistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mypoollist != null && mypoollist.getVisibility() == View.VISIBLE){
					GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
					tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
					tracker.setScreenName("HomeScreen");
					tracker.send(new HitBuilders.EventBuilder().setCategory("Click").setAction("Click").setLabel("Show Ride History").build());

					showHistoryCalled = true;
					showhistory.setText("SHOW CURRENT RIDES");
					mypoollist.setVisibility(View.GONE);
					listView = (PagingListView) getActivity().findViewById(
							R.id.paging_list_view);
					listView.setVisibility(View.VISIBLE);
					showhisadaptor = new ShowHistoryRidesAdaptor(getActivity());

					latestcabid = "";
					//poolhistorypagenumber = -1; // earlier -1
                    poolhistorypagenumber = 0;
					cabchk = false;

					listView.setAdapter(showhisadaptor);
					listView.setHasMoreItems(true);
					listView.setPagingableListener(new PagingListView.Pagingable() {
						@Override
						public void onLoadMoreItems() {
							if (!cabchk) {
								// new CountryAsyncTask().execute();
								new ConnectionTaskForShowRidesHistory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
												Integer.toString(++poolhistorypagenumber));
							} else {
								listView.onFinishLoading(false, null);
							}
						}
					});

					/*listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							Log.d("arg2", "" + arg2);

							RideDetailsModel rideDetailsModel = MyRidesListClass.listmyrides
									.get(arg2);
							String mobileNumber = rideDetailsModel
									.getMobileNumber();

							if (mobileNumber.equalsIgnoreCase(MobileNumberstr)) {

								final Intent mainIntent = new Intent(getActivity(),
										XCheckPoolFragmentActivty.class);
								mainIntent.putExtra("RideDetailsModel",
										(new Gson()).toJson(rideDetailsModel));

								getActivity().startActivity(mainIntent);

							} else {

								final Intent mainIntent = new Intent(getActivity(),
										XMemberRideFragmentActivity.class);
								mainIntent.putExtra("RideDetailsModel",
										(new Gson()).toJson(rideDetailsModel));

								getActivity().startActivity(mainIntent);
							}
						}
					});*/

				
				}else {
					//refreshDataOnPageChange();
					if(listView !=null)
						listView.setVisibility(View.GONE);
					if(mypoollist != null)
						mypoollist.setVisibility(View.VISIBLE);
					if(showhistory != null)
						showhistory.setText("SHOW HISTORY");
                    _CabId = "";
                    refershRideListData("");
				}
			}
		});
	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
			String MobileNumberstr = mPrefs.getString("MobileNumber", "");
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
			//getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
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
			try{
				if(getView() == null){
					return;
				}
				getView().findViewById(R.id.progressBar).setVisibility(View.GONE);

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(getActivity(),
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(rideshistoryresponse)) {
					getView().findViewById(R.id.llErrorMsg).setVisibility(View.VISIBLE);
					return;
				}

				if ( rideshistoryresponse.contains("Unauthorized Access")) {
					Log.e("MyRidesActivity",
							"rideshistoryresponse Unauthorized Access");
					Toast.makeText(getActivity(),
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (rideshistoryresponse.equalsIgnoreCase("No Pool Created Yet!!")
						&& rideshistoryresponse.equalsIgnoreCase("[]")) {
					getView().findViewById(R.id.llErrorMsg).setVisibility(View.VISIBLE);

					Toast.makeText(getActivity(), "No More history",
							Toast.LENGTH_LONG).show();

					latestcabid = "khatam";
					poolhistorypagenumber = -1;
					cabchk = true;
					listView.onFinishLoading(false, null);

				} else {
					getView().findViewById(R.id.llErrorMsg).setVisibility(View.GONE);

					MyRidesListClass.listmyrides.clear();

					// if (latestcabid.equalsIgnoreCase("") ||
					// latestcabid.isEmpty()) {
					if (poolhistorypagenumber == -1) {

						for (int i = 0; i < arrayRideDetailsModels.size(); i++) {

							RideDetailsModel rideDetailsModel = arrayRideDetailsModels.get(i);
						/*RideDetailsModel ride = new RideDetailsModel();

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
*/
							MyRidesListClass.listmyrides.add(rideDetailsModel);
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
						/*RideDetailsModel ride = new RideDetailsModel();

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
						ride.setIsOwner(rideDetailsModel.getIsOwner());
						ride.setVehicleModel(rideDetailsModel.getVehicleModel());
						ride.setRegistrationNumber(rideDetailsModel.getRegistrationNumber());
						ride.setIsCommercial(rideDetailsModel.getIsCommercial());

						ride.setExpTripDuration(rideDetailsModel.getExpTripDuration());*/

							MyRidesListClass.listmyrides.add(rideDetailsModel);

							//arrayRideDetailsModels.add(rideDetailsModel);



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
			}catch (Exception e){
				e.printStackTrace();
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
	
	private class CountryAsyncTask extends SafeAsyncTask<List<RideDetailsModel>> {

		@Override
		public List<RideDetailsModel> call() throws Exception {
			List<RideDetailsModel> result = null;
			result = MyRidesListClass.listmyrides;
			Thread.sleep(3000);
			return result;
		}

		@Override
		protected void onSuccess(List<RideDetailsModel> newItems) throws Exception {
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
	
	public void refreshDataOnPageChange(){
		if(isOnline() && isFirstLaunch){
			isFirstLaunch = false;
		}else {
			if(listView !=null)
				listView.setVisibility(View.GONE);
			if(mypoollist != null)
				mypoollist.setVisibility(View.VISIBLE);
			if(showhistory != null)
				showhistory.setText("SHOW HISTORY");
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("LifeCycle", "onDestroy");
		if(fragment != null){
			fragment = null;
		}

	}

	public class MyRidesAdapter extends BaseAdapter {

		// Declare Variables
		Context context;
		ArrayList<String> fromlocation;
		ArrayList<String> tolocation;
		ArrayList<String> traveldate;
		ArrayList<String> traveltime;
		ArrayList<String> seats;
		ArrayList<String> ownername;
		ArrayList<String> ownerimagename;
		LayoutInflater inflater;
		AQuery aq;
		ArrayList<Boolean> isOwner;
		ArrayList<RideDetailsModel> arrayListRide;

		public MyRidesAdapter(Context context, ArrayList<String> floc,
							  ArrayList<String> tloc, ArrayList<String> tdate,
							  ArrayList<String> ttime, ArrayList<String> sets,
							  ArrayList<String> ownnam, ArrayList<String> ownimgnam, ArrayList<Boolean> isOwner,  ArrayList<RideDetailsModel> arrayListRide) {
			this.context = context;
			this.fromlocation = floc;
			this.tolocation = tloc;
			this.traveldate = tdate;
			this.traveltime = ttime;
			this.seats = sets;
			this.ownername = ownnam;
			this.ownerimagename = ownimgnam;
			this.aq = new AQuery(context);
			this.isOwner = isOwner;
			this.arrayListRide = arrayListRide;

		}

		@Override
		public int getCount() {
			return fromlocation.size();
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
			ViewHolder viewholder;

			if (convertView == null) {
				inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.myrides_list_row_x, parent,
						false);
				viewholder = new ViewHolder(convertView);

			/*viewholder.myridesbannerimage = (CircularImageView) convertView
					.findViewById(R.id.myridesbannerimage);
			viewholder.myridesbannerusername = (TextView) convertView
					.findViewById(R.id.myridesbannerusername);
			viewholder.fromtolocationvalue = (TextView) convertView
					.findViewById(R.id.fromtolocationvalue);
			viewholder.datetext = (TextView) convertView
					.findViewById(R.id.datetext);
			viewholder.timetext = (TextView) convertView
					.findViewById(R.id.timetext);
			viewholder.seatstext = (TextView) convertView
					.findViewById(R.id.seatstext);
			viewholder.tvAvSeats = (TextView) convertView
					.findViewById(R.id.tvAvSeats);*/

				convertView.setTag(viewholder);

			} else
				viewholder = (ViewHolder) convertView.getTag();

			// Locate the TextViews in listview_item.xml

		/*if (ownerimagename.get(position).toString().trim().isEmpty()) {

			Log.d("image nahi hai", ""
					+ ownername.get(position).toString().trim());

		} else {
			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ ownerimagename.get(position).toString().trim();
			aq.id(viewholder.myridesbannerimage).image(url, true, true);
		}
		viewholder.myridesbannerusername.setText(ownername.get(position)
				.toString().trim());
		viewholder.fromtolocationvalue.setText(fromlocation.get(position)
				.toString().trim()
				+ " > " + tolocation.get(position).toString().trim());
		viewholder.datetext.setText(traveldate.get(position).toString().trim());
		viewholder.timetext.setText(traveltime.get(position).toString().trim());

		try {
			String[] arr = seats.get(position).toString().trim().split("/");

			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			viewholder.seatstext.setText("Total seats : "
					+ (total + StringTags.TAT_ADD_TOTAL));
			viewholder.tvAvSeats.setText("Available : " + ava);
		} catch (Exception e) {
			viewholder.seatstext.setText("Total seats :");
			viewholder.tvAvSeats.setText("Available :");
		}*/
			//--------------------------------->
			String[] arr = seats.get(position).toString().trim().split("/");

			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			if(isOwner.get(position)){
				viewholder.tvSeatAvLable.setText("Seats Filled");
				viewholder.tvSeatCount .setText(String.valueOf(filled));
			}else {
				viewholder.tvSeatAvLable.setText("Seats Available");
				viewholder.tvSeatCount .setText(String.valueOf(ava));
			}


			viewholder.tvPlaceFrom.setText(fromlocation.get(position).toString().trim());
			//	viewholder.tvDate.setText(traveldate.get(position).toString().trim());
			viewholder.tvTime.setText(traveltime.get(position).toString().trim());
			viewholder.tvPlaceTo .setText(tolocation.get(position).toString().trim());
			String[] arr2 = traveldate.get(position).toString().trim().split("/");
			int month = Integer.parseInt(arr2[1]);
			int date = Integer.parseInt(arr2[0]);
			viewholder.tvDate.setText(String.format("%02d",date)+" "+getMontString(month));

			if(arrayListRide.get(position).getRideType()!=null){

				String rideType = arrayListRide.get(position).getRideType();
				if(rideType.equalsIgnoreCase("1") || rideType.equalsIgnoreCase("4")){
					viewholder.flCall.setVisibility(View.GONE);
					viewholder.llSeatsAv.setVisibility(View.VISIBLE);
					viewholder.ivUserImage.setVisibility(View.VISIBLE);
					viewholder.ivCabTypeIcon.setVisibility(View.GONE);
					viewholder.tvCancel.setVisibility(View.GONE);

					viewholder.tvUserName.setText(ownername.get(position).toString().trim());
					if (arrayListRide.get(position).getVehicleModel() != null){
						viewholder.tvModelNumber.setText(arrayListRide.get(position).getVehicleModel());
					}
					if (arrayListRide.get(position).getRegistrationNumber() != null){
						viewholder.tvRegNo.setText(arrayListRide.get(position).getRegistrationNumber().toUpperCase(Locale.getDefault()));
					}
					if (arrayListRide.get(position).getIsCommercial() != null){
						if(arrayListRide.get(position).getIsCommercial().equalsIgnoreCase(AppConstants.COMMERCIAL)){
							viewholder.ivCabImage.setImageResource(R.drawable.car_taxi);
						}else {
							viewholder.ivCabImage.setImageResource(R.drawable.car);
						}
					}
					if(!TextUtils.isEmpty(ownerimagename.get(position).toString())){
						String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
								+ ownerimagename.get(position).toString().trim();
						//  Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
						Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
					}

				}else if(rideType.equalsIgnoreCase("2") || rideType.equalsIgnoreCase("5")){
					// builder.append("(Cab Share)");
					viewholder.tvCancel.setVisibility(View.GONE);

					viewholder.flCall.setVisibility(View.GONE);
					viewholder.llSeatsAv.setVisibility(View.VISIBLE);
					viewholder.ivUserImage.setVisibility(View.VISIBLE);
					viewholder.ivCabTypeIcon.setVisibility(View.GONE);
					viewholder.tvUserName.setText(ownername.get(position).toString().trim());
					if (arrayListRide.get(position).getVehicleModel() != null){
						viewholder.tvModelNumber.setText("CAB SHARE");
					}
					if (arrayListRide.get(position).getRegistrationNumber() != null){
						viewholder.tvRegNo.setText("");
					}
					viewholder.ivCabImage.setImageResource(R.drawable.car_taxi);
					if(!TextUtils.isEmpty(ownerimagename.get(position).toString())){
						String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
								+ ownerimagename.get(position).toString().trim();
						//  Glide.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
						Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
					}


				}else if(rideType.equalsIgnoreCase("3")){
					viewholder.flCall.setVisibility(View.VISIBLE);
					viewholder.llSeatsAv.setVisibility(View.GONE);
					viewholder.ivUserImage.setVisibility(View.GONE);
					viewholder.ivCabTypeIcon.setVisibility(View.VISIBLE);
					viewholder.tvCancel.setVisibility(View.VISIBLE);
					viewholder.ivCabImage.setImageResource(R.drawable.car_taxi);
					if (arrayListRide.get(position).getCarNumber() != null){
						String str[] = arrayListRide.get(position).getCarNumber().split("\\(");
						viewholder.tvModelNumber.setText(str[1].trim().replace(")","").toUpperCase(Locale.getDefault()));
						viewholder.tvRegNo.setText(str[0].trim().toUpperCase(Locale.getDefault()));

					}
					if(!TextUtils.isEmpty(arrayListRide.get(position).getDriverName())){
						viewholder.tvUserName.setText(arrayListRide.get(position).getDriverName());
					}
					if(!TextUtils.isEmpty(arrayListRide.get(position).getCabName())){
                   /* String url = GlobalVariables.ServiceUrl + "/ProfileImages/" + ownerimagename.get(position).toString().trim();
                    Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(viewholder.ivUserImage);
    */              String cabName = arrayListRide.get(position).getCabName();
						if(!TextUtils.isEmpty(cabName)){
							if(cabName.equalsIgnoreCase(AppConstants.UBER)){
								viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_uber_icon);
							}else if(cabName.equalsIgnoreCase(AppConstants.OLA)){
								viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_ola_icon);

							}else if(cabName.equalsIgnoreCase(AppConstants.MERU)){
								viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_meru_icon);

							}else if(cabName.equalsIgnoreCase(AppConstants.MEGA)){
								viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_mega_icon);

							}else if(cabName.equalsIgnoreCase(AppConstants.TAXIFORSURE)){
								viewholder.ivCabTypeIcon.setImageResource(R.drawable.cab_tfs_icon);
							}
						}
					}
					viewholder.flCall.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(!TextUtils.isEmpty(arrayListRide.get(position).getDriverNumber())){
								Intent intent = new Intent(Intent.ACTION_DIAL);
								intent.setData(Uri.parse("tel:" + arrayListRide.get(position).getDriverNumber().trim()));
								context.startActivity(intent);
							}
						}
					});
					viewholder.tvCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							cancelCabDialog(arrayListRide.get(position));
						}
					});
				}
			}



			return convertView;
		}

		public class ViewHolder {

			/*public CircularImageView myridesbannerimage;
            public TextView myridesbannerusername;
            public TextView fromtolocationvalue;
            public TextView datetext;
            public TextView timetext;
            public TextView seatstext, tvAvSeats;*/
			private TextView tvCancel, tvUserName, tvDate,tvTime,tvPlaceFrom,tvPlaceTo,tvSeatAvLable,tvSeatCount,tvModelNumber,tvRegNo ;
			private ImageView ivUserImage,ivCabImage, ivCabTypeIcon;
			private FrameLayout flCall;
			private LinearLayout llSeatsAv;
			public ViewHolder(View view){
				tvUserName = (TextView)view.findViewById(R.id.tvUserName);
				tvDate = (TextView)view.findViewById(R.id.tvDate);
				tvTime = (TextView)view.findViewById(R.id.tvTime);
				tvPlaceFrom = (TextView)view.findViewById(R.id.tvPlaceFrom);
				tvPlaceTo = (TextView)view.findViewById(R.id.tvPlaceTo);
				tvSeatAvLable = (TextView)view.findViewById(R.id.tvSeatAvLable);
				tvSeatCount = (TextView)view.findViewById(R.id.tvSeatCount);
				tvModelNumber = (TextView)view.findViewById(R.id.tvModelNumber);
				tvRegNo = (TextView)view.findViewById(R.id.tvRegNo);
				ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
				ivCabImage = (ImageView) view.findViewById(R.id.ivCabImage);
				flCall = (FrameLayout)view.findViewById(R.id.flCall);
				llSeatsAv = (LinearLayout) view.findViewById(R.id.llSeatsAv);
				ivCabTypeIcon = (ImageView)view.findViewById(R.id.ivCabIcon);
				tvCancel = (TextView)view.findViewById(R.id.tvCancel);
				tvUserName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvDate.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvTime.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvPlaceFrom.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvPlaceTo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvSeatAvLable.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvSeatCount.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvModelNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
				tvRegNo.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));



			}
		}

		private String getMontString(int month) {
			String monthString = "";
			switch (month) {
				case 1:
					monthString = "JAN";
					break;
				case 2:
					monthString = "FEB";
					break;
				case 3:
					monthString = "MAR";
					break;
				case 4:
					monthString = "APR";
					break;
				case 5:
					monthString = "MAY";
					break;
				case 6:
					monthString = "JUN";
					break;
				case 7:
					monthString = "JUL";
					break;
				case 8:
					monthString = "AUG";
					break;
				case 9:
					monthString = "SEP";
					break;
				case 10:
					monthString = "OCT";

					break;
				case 11:
					monthString = "NOV";
					break;
				case 12:
					monthString = "DEC";
					break;


			}

			return monthString;
		}

		private void cancelCabDialog(final RideDetailsModel rideDetailsModel){
			AlertDialog.Builder builder = new AlertDialog.Builder(
					context);
			builder.setMessage("Are you sure you want to cancel the booking?");

			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
											int which) {

							if (rideDetailsModel.getCabName()
									.equalsIgnoreCase("Uber")) {
								cancelUberCab(rideDetailsModel);
							} else if (rideDetailsModel.getCabName()
									.equalsIgnoreCase("Mega")) {
								cancelMegaCab(rideDetailsModel);
							} else if (rideDetailsModel.getCabName()
									.equalsIgnoreCase("TaxiForSure")) {
								cancelTFSCab(rideDetailsModel);
							} else if (rideDetailsModel.getCabName()
									.equalsIgnoreCase("ola")) {
								cancelOlaCab(rideDetailsModel);
							}
						}
					});

			builder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
											int which) {

						}
					});

			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();
		}

		private void cancelUberCab(RideDetailsModel rideDetailsModel) {

			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("iShareRyde requires Internet connection");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

				return;
			}

			CancelUberCabAsync cancelUberCabAsync = new CancelUberCabAsync();
			String param = ("bookingRefNo=" + rideDetailsModel.getBookingRefNo());

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				cancelUberCabAsync.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, param);
			} else {
				cancelUberCabAsync.execute(param);
			}
		}

		public class CancelUberCabAsync extends AsyncTask<String, Void, String> {

			String result;

			private ProgressDialog dialog = new ProgressDialog(
					context);

			@Override
			protected void onPreExecute() {
				try{
					if(dialog != null){
						dialog.setMessage("Please Wait...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(String... args) {
				Log.d("CancelUberCabAsync",
						"CancelUberCabAsync : " + args[0].toString());

				try {
					URL url = new URL(GlobalVariables.ServiceUrl
							+ "/cancelUberBooking.php");
					String response = "";

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setReadTimeout(30000);
					urlConnection.setConnectTimeout(30000);
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					OutputStream outputStream = urlConnection.getOutputStream();
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(outputStream, "UTF-8"));
					bufferedWriter.write(args[0].toString());
					bufferedWriter.flush();
					bufferedWriter.close();
					outputStream.close();

					int responseCode = urlConnection.getResponseCode();

					if (responseCode == HttpsURLConnection.HTTP_OK) {

						String line = "";
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(
										urlConnection.getInputStream()));
						while ((line = bufferedReader.readLine()) != null) {
							response += line;
						}

					} else {
						response = "";
						Log.d("CancelUberCabAsync",
								"responseCode != HttpsURLConnection.HTTP_OK : "
										+ responseCode);
						result = response;
					}

					Log.d("CancelUberCabAsync", "CancelUberCabAsync response : "
							+ response);
					result = response;
				} catch (Exception e) {
					e.printStackTrace();
					result = "";
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				if (!result.isEmpty()) {

					try {
						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.get("status").toString();
						if (status.equalsIgnoreCase("SUCCESS")) {

							// JSONObject jsonObjectData = new JSONObject(jsonObject
							// .get("data").toString());

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setMessage("Booking cancelled!");
									builder.setPositiveButton("OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											});
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();

								}
							});

						} else {
							final String reason = jsonObject.get("message")
									.toString();

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setTitle("Cab could not be cancelled");
									builder.setMessage(reason);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {

				try{
					if (dialog  != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					refershRideListData("");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}

		private void cancelTFSCab(RideDetailsModel rideDetailsModel) {

			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("iShareRyde requires Internet connection");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

				return;
			}

			CancelTFSCabAsync cancelTFSCabAsync = new CancelTFSCabAsync();
			String authString = rideDetailsModel.getBookingRefNo() + "cancellation";
			String param = "type=cancellation" + "&booking_id="
					+ rideDetailsModel.getBookingRefNo()
					+ "&cancellation_reason=&auth="
					+ GlobalMethods.calculateCMCAuthString(authString);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				cancelTFSCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						param);
			} else {
				cancelTFSCabAsync.execute(param);
			}
		}

		public class CancelTFSCabAsync extends AsyncTask<String, Void, String> {

			String result;

			private ProgressDialog dialog = new ProgressDialog(
					context);

			@Override
			protected void onPreExecute() {
				try{
					if(dialog != null){
						dialog.setMessage("Please Wait...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(String... args) {

				try {

					URL url = new URL(GlobalVariables.ServiceUrl + "/tfs.php");
					String response = "";

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setReadTimeout(30000);
					urlConnection.setConnectTimeout(30000);
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					OutputStream outputStream = urlConnection.getOutputStream();
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(outputStream, "UTF-8"));
					bufferedWriter.write(args[0].toString());
					bufferedWriter.flush();
					bufferedWriter.close();
					outputStream.close();

					int responseCode = urlConnection.getResponseCode();

					if (responseCode == HttpsURLConnection.HTTP_OK) {

						String line = "";
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(
										urlConnection.getInputStream()));
						while ((line = bufferedReader.readLine()) != null) {
							response += line;
						}

					} else {
						response = "";
						Log.d("CancelTFSCabAsync",
								"responseCode != HttpsURLConnection.HTTP_OK : "
										+ responseCode);
						result = response;
					}

					Log.d("CancelTFSCabAsync", "CancelTFSCabAsync response : "
							+ response);
					result = response;
				} catch (Exception e) {
					e.printStackTrace();
					result = "";
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				if (result != null && result.length() > 0
						&& result.contains("Unauthorized Access")) {

					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Log.e("CheckPoolFragmentActivity",
									"CancelTFSCabAsync Unauthorized Access");
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
						}
					});

					return "";
				}

				if (!result.isEmpty()) {

					try {
						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.get("status").toString();
						if (status.equalsIgnoreCase("success")) {

							// JSONObject jsonObjectData = new JSONObject(jsonObject
							// .get("data").toString());

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setMessage("Booking cancelled!");
									builder.setPositiveButton("OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											});
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();

								}
							});

						} else {
							final String reason = jsonObject.get("error_desc")
									.toString();

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setTitle("Cab could not be cancelled");
									builder.setMessage(reason);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try{
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					refershRideListData("");
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		}

		private void cancelOlaCab(RideDetailsModel rideDetailsModel) {

			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("iShareRyde requires Internet connection");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

				return;
			}

			CancelOlaCabAsync cancelOlaCabAsync = new CancelOlaCabAsync();
			String authString = rideDetailsModel.getBookingRefNo() + "cancellation";
			String param = "type=cancellation" + "&booking_id="
					+ rideDetailsModel.getBookingRefNo() + "&auth="
					+ GlobalMethods.calculateCMCAuthString(authString);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				cancelOlaCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						param);
			} else {
				cancelOlaCabAsync.execute(param);
			}
		}

		public class CancelOlaCabAsync extends AsyncTask<String, Void, String> {

			String result;

			private ProgressDialog dialog = new ProgressDialog(
					context);

			@Override
			protected void onPreExecute() {
				try{
					if(dialog != null){
						dialog.setMessage("Please Wait...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(String... args) {

				try {
					URL url = new URL(GlobalVariables.ServiceUrl + "/olaApi.php");
					String response = "";

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setReadTimeout(30000);
					urlConnection.setConnectTimeout(30000);
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					OutputStream outputStream = urlConnection.getOutputStream();
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(outputStream, "UTF-8"));
					bufferedWriter.write(args[0].toString());
					bufferedWriter.flush();
					bufferedWriter.close();
					outputStream.close();

					int responseCode = urlConnection.getResponseCode();

					if (responseCode == HttpsURLConnection.HTTP_OK) {

						String line = "";
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(
										urlConnection.getInputStream()));
						while ((line = bufferedReader.readLine()) != null) {
							response += line;
						}

					} else {
						response = "";
						Log.d("CancelOlaCabAsync",
								"responseCode != HttpsURLConnection.HTTP_OK : "
										+ responseCode);
						result = response;
					}

					Log.d("CancelOlaCabAsync", "CancelOlaCabAsync response : "
							+ response);
					result = response;
				} catch (Exception e) {
					e.printStackTrace();
					result = "";
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				if (result.contains("Unauthorized Access")) {

					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Log.e("MemberRideFragmentActivity",
									"CancelOlaCabAsync Unauthorized Access");
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
						}
					});

					return "";
				}

				if (!result.isEmpty()) {

					try {
						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.get("status").toString();
						if (status.equalsIgnoreCase("success")) {

							// JSONObject jsonObjectData = new JSONObject(jsonObject
							// .get("data").toString());

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setMessage("Booking cancelled!");
									builder.setPositiveButton("OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											});
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();

								}
							});

						} else {
							final String reason = jsonObject.get("reason")
									.toString();

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setTitle("Cab could not be cancelled");
									builder.setMessage(reason);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {

				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				refershRideListData("");
			}
		}

		private void cancelMegaCab(RideDetailsModel rideDetailsModel) {

			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("iShareRyde requires Internet connection");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

				return;
			}

			CancelMegaCabAsync cancelMegaCabAsync = new CancelMegaCabAsync();
			String authString = rideDetailsModel.getBookingRefNo()
					+ rideDetailsModel.getMobileNumber() + "CancelBooking";
			String param = "type=CancelBooking" + "&mobile="
					+ rideDetailsModel.getMobileNumber() + "&bookingNo="
					+ rideDetailsModel.getBookingRefNo() + "&auth="
					+ GlobalMethods.calculateCMCAuthString(authString);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				cancelMegaCabAsync.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, param);
			} else {
				cancelMegaCabAsync.execute(param);
			}
		}

		public class CancelMegaCabAsync extends AsyncTask<String, Void, String> {

			String result;

			private ProgressDialog dialog = new ProgressDialog(
					context);

			@Override
			protected void onPreExecute() {
				try{
					if(dialog != null){
						dialog.setMessage("Please Wait...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			protected String doInBackground(String... args) {

				try {
					URL url = new URL(GlobalVariables.ServiceUrl + "/MegaApi.php");
					String response = "";

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setReadTimeout(30000);
					urlConnection.setConnectTimeout(30000);
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					OutputStream outputStream = urlConnection.getOutputStream();
					BufferedWriter bufferedWriter = new BufferedWriter(
							new OutputStreamWriter(outputStream, "UTF-8"));
					bufferedWriter.write(args[0].toString());
					bufferedWriter.flush();
					bufferedWriter.close();
					outputStream.close();

					int responseCode = urlConnection.getResponseCode();

					if (responseCode == HttpsURLConnection.HTTP_OK) {

						String line = "";
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(
										urlConnection.getInputStream()));
						while ((line = bufferedReader.readLine()) != null) {
							response += line;
						}

					} else {
						response = "";
						Log.d("CancelMegaCabAsync",
								"responseCode != HttpsURLConnection.HTTP_OK : "
										+ responseCode);
						result = response;
					}

					Log.d("CancelMegaCabAsync", "CancelMegaCabAsync response : "
							+ response);
					result = response;
				} catch (Exception e) {
					e.printStackTrace();
					result = "";
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				if (result != null && result.length() > 0
						&& result.contains("Unauthorized Access")) {

					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Log.e("CheckPoolFragmentActivity",
									"CancelMegaCabAsync Unauthorized Access");
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
						}
					});

					return "";
				}

				if (!result.isEmpty()) {

					try {
						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.get("status").toString();
						if (status.equalsIgnoreCase("SUCCESS")) {

							// JSONObject jsonObjectData = new JSONObject(jsonObject
							// .get("data").toString());

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setMessage("Booking cancelled!");
									builder.setPositiveButton("OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											});
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();

								}
							});

						} else {
							final String reason = jsonObject.get("data").toString();

							((NewHomeScreen)context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											context);
									builder.setTitle("Cab could not be cancelled");
									builder.setMessage(reason);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					((NewHomeScreen)context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				return result;
			}

			@Override
			protected void onPostExecute(String result) {

				try{
					if(dialog != null){
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						refershRideListData("");
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}


		public boolean isOnline() {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}
	}
}