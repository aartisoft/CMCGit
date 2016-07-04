package com.clubmycab.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CabUserCredentialsReadWrite;
import com.clubmycab.DistanceTimeEstimateHandler;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.interfaces.CurrentLocationListener;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.model.LocationModel;
import com.clubmycab.model.LocationModelListener;
import com.clubmycab.ui.ChoosePaymentTypeScreen;
import com.clubmycab.ui.FavoritePlaceFindActivity;
import com.clubmycab.ui.FirstLoginWalletsActivity;
import com.clubmycab.ui.GetLocationTaskHandler;
import com.clubmycab.ui.MobileSiteActivity;
import com.clubmycab.ui.MobileSiteFragment;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.NewRideCreationScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.LocationAddress;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParserFactory;

public class CabsFragment extends Fragment implements OnClickListener,
		LocationListener , CurrentLocationListener, GlobalAsyncTask.AsyncTaskResultListener{
	private LocationManager locationManager;
	private Location mycurrentlocationobject;
	private static final int LOCATION_REQUEST = 101;
	private boolean isLocationNotUpdated = true;
	private TextView etFromPlace, etToPlace;
	private Address fAddress, toAddress;
	private int PLACE_PICKER_REQUEST = 102;
	private String whichdotclick;
	private GoogleMap myMap;
	private Address tAddress;
	private TextView fromlocation;
	private LatLng invitemapcenter;
	private boolean flagchk;
	private String fromshortname;
	private AddressModel addressModelFrom;
	private String toshortname;
	private AddressModel addressModelTo;
	public JSONArray mCabSearchArray;
	private ListView lvSearchedCabs;
	protected boolean togglePriceTimeSort = true;
	private int cabBookingPosition;
	private String FullName;
	private String MobileNumberstr;
	private String mUberBookingInputParams, mUberUsername, mUberPassword;
	public ProgressDialog dialog12;
	float FromToMinDestance = 2000;
	private boolean isRefresingCabList;
	private boolean isFirstLaunch = true;
	public boolean isMapVisible;
	private Tracker tracker;
	public  static final int SELECT_PAYMENT_REQUEST =  1001;
    private Address mapAddress;
	private Dialog onedialog;


	@Override
    public void getResult(String response, String uniqueID) {
        if(uniqueID.equalsIgnoreCase("getToken")){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("success")) {
                    if(!TextUtils.isEmpty(jsonObject.getString("token"))){
                            Gson gson = new Gson();
                            Intent mainIntent = new Intent(getActivity(), NewRideCreationScreen.class);
                            mainIntent.putExtra("screentoopen", NewRideCreationScreen.HOME_ACTIVITY_SHARE_CAB);

                            if(addressModelFrom != null & addressModelTo != null){
                                mainIntent.putExtra("fromlocation", etFromPlace.getText().toString());
                                mainIntent.putExtra("tolocation", etToPlace.getText().toString());
                                mainIntent.putExtra("StartAddressModel", gson
                                        .toJson(addressModelFrom).toString());
                                mainIntent.putExtra("EndAddressModel", gson
                                        .toJson(addressModelTo).toString());

                            }
                            startActivityForResult(mainIntent, 500);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);
                            /*else {
                            Toast.makeText(getActivity(), "Please select another location", Toast.LENGTH_LONG).show();
                        }*/
                    }
                }else {
                    showWallletDialog();
                }
                L.mesaage("");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class RideObject extends Object {

		public String cabID = "", travelDate = "", travelTime = "",
				fromShortName = "", toShortName = "", fromLongName = "",
				toLongName = "", estDistance = "", estDuration = "",
				driverName = "", driverPhone = "", vehicle = "";

		public RideObject() {
			super();
		}

		public RideObject(String cab, String tDate, String tTime,
				String fShort, String tShort, String fLong, String tLong) {

			super();

			this.cabID = cab;
			this.travelDate = tDate;
			this.travelTime = tTime;
			this.fromShortName = fShort;
			this.toShortName = tShort;
			this.fromLongName = fLong;
			this.toLongName = tLong;

		}
	}

	RideObject rideObject;
	private CustomGridViewCabSearchAdapter cabAdapter;
	private static  CabsFragment fragment;
	private SupportMapFragment mapFragment;

	public static CabsFragment newInstance(Bundle args) {
		if(fragment == null){
			 fragment = new CabsFragment();
		}
		try{
			fragment.setArguments(args);
		}catch (Exception e){
			e.printStackTrace();
		}		return fragment;
	}
	
	public static CabsFragment getInstance(){
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("HomeScreen");
		tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Opened").setLabel("Cabs Search Screen").build());

		SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");
		Log.d("LifeCycle", "onCreate");


	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		 ((NewHomeScreen)getActivity()).fragmentCommunicator = this;
			Log.d("LifeCycle", "onAttach");

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.frommap);

	}

	@Override
	public void onResume() {
		super.onResume();
		//validateSearchCabRequest();
		Log.d("LifeCycle", "onResume");
		initMap();


	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cabs, container, false);
		Log.d("LifeCycle", "onCreateView");

		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d("LifeCycle", "onViewCreated");

		((Button)view.findViewById(R.id.cancel)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		((Button)view.findViewById(R.id.fromdone)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		((TextView)view.findViewById(R.id.fromlocation)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		view.findViewById(R.id.ivHelp).setOnClickListener(this);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_serach_location_x, lvSearchedCabs,false);
		header.findViewById(R.id.flFromLocation).setOnClickListener(this);
		header.findViewById(R.id.flToLocation).setOnClickListener(this);
		view.findViewById(R.id.cancel).setOnClickListener(this);
		view.findViewById(R.id.fromdone).setOnClickListener(this);
		view.findViewById(R.id.sharecabll).setOnClickListener(this);
		fromlocation = (TextView) view.findViewById(R.id.fromlocation);
		lvSearchedCabs = (ListView) view.findViewById(R.id.gridViewBookCab);
		lvSearchedCabs.addHeaderView(header, null, false);
		cabAdapter = new CustomGridViewCabSearchAdapter();
		cabAdapter.init(getActivity(), mCabSearchArray);
		lvSearchedCabs.setAdapter(cabAdapter);
		etFromPlace = (TextView) header.findViewById(R.id.from_places);
		etToPlace = (TextView) header.findViewById(R.id.to_places);
		etFromPlace.setOnClickListener(this);
		etToPlace.setOnClickListener(this);
		togglePriceTimeSort = true;
		etFromPlace.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
		etToPlace.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
		((TextView)header.findViewById(R.id.textFrom)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
		((TextView)header.findViewById(R.id.textTo)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
        ((TextView)view.findViewById(R.id.tvCarPoolCab)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));

		//((NewHomeScreen)getActivity()).setTimeIcon();
		//initMap();

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.d("LifeCycle", "onDetach");

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
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		/*case R.id.llSearchacab:
			isRefresingCabList = false;
			validateSearchCabRequest();
			break;*/

		case R.id.flFromLocation:
			 //myMap.getUiSettings().setAllGesturesEnabled(true);
			isMapVisible = true;
			myMap.getUiSettings().setAllGesturesEnabled(true);
			picFromLocation();
			break;

		case R.id.flToLocation:
			isMapVisible = true;
			 myMap.getUiSettings().setAllGesturesEnabled(true);
			picToLocation();
			break;

		case R.id.cancel:
			isMapVisible = false;
			myMap.getUiSettings().setAllGesturesEnabled(false);
			getView().findViewById(R.id.rlMapView).setVisibility(View.GONE);
			getView().findViewById(R.id.llMainView).setVisibility(View.VISIBLE);
			break;

		case R.id.fromdone:
			isMapVisible = false;
			doneClicked();
			break;
		case R.id.from_places:
			Intent intent = new Intent(getActivity(), FavoritePlaceFindActivity.class);
			startActivityForResult(intent, 3);


			break;
		case R.id.to_places:
			// isCallresetIntentParams = true;

			intent = new Intent(getActivity(), FavoritePlaceFindActivity.class);

			startActivityForResult(intent, 4);

			break;
			
		case R.id.sharecabll:

            //checkTokenExist();

			if(SPreference.getPref(getActivity()).getInt(SPreference.USER_TYPE, AppConstants.USER_TYPE_NORMAL) == AppConstants.USER_TYPE_NORMAL){
				if(SPreference.getRideOfferWalletType(getActivity()) == AppConstants.NULL){
					//showChooseWalletForPayment();
					Intent intent1 = new Intent(getActivity(), ChoosePaymentTypeScreen.class);
					intent1.putExtra("fromrideoffer",true);
					startActivityForResult(intent1,SELECT_PAYMENT_REQUEST);
				}else {
					checkTokenExist();
				}
			}else {// for user type system
				tracker.send(new HitBuilders.EventBuilder().setCategory("Carpool in a cab").setAction("Carpool in a cab").setLabel("Carpool in a cab").build());

				Intent mainIntent = new Intent(getActivity(), NewRideCreationScreen.class);
					mainIntent.putExtra("screentoopen", NewRideCreationScreen.HOME_ACTIVITY_SHARE_CAB);
					Gson gson = new Gson();
					if(addressModelFrom != null & addressModelTo != null){
						mainIntent.putExtra("fromlocation", etFromPlace.getText().toString());
						mainIntent.putExtra("tolocation", etToPlace.getText().toString());
						mainIntent.putExtra("StartAddressModel", gson
								.toJson(addressModelFrom).toString());
						mainIntent.putExtra("EndAddressModel", gson
								.toJson(addressModelTo).toString());

					}
					startActivityForResult(mainIntent, 500);
					getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

			}
			break;

			case R.id.ivHelp:
				CustomDialog.showDialog(getActivity(),"Invite people to share a cab\n" +
						"\n" +
						"You book the cab and pay for it\n" +
						"\n" +
						"Your co-riders pay you Rs. 4/km per seat");
				tracker.send(new HitBuilders.EventBuilder().setCategory("Carpool in a cab question").setAction("Carpool in a cab question").setLabel("Carpool in a cab question").build());

				break;
			
		default:
			break;
		}
	}

	public void hideMapView(){
		isMapVisible = false;
		myMap.getUiSettings().setAllGesturesEnabled(false);
		getView().findViewById(R.id.rlMapView).setVisibility(View.GONE);
		getView().findViewById(R.id.llMainView).setVisibility(View.VISIBLE);
	}
	
	public void refershCabList(){
		isRefresingCabList = true;
		validateSearchCabRequest();
	}
	
	public void validateSearchCabRequest() {
		try{
			if(TextUtils.isEmpty(getFromPlace())){
				return;
			}else if(TextUtils.isEmpty(getToPlace())){
				return;

			}
			if(addressModelFrom == null){
				etFromPlace.setText("");
				return;

			}else if(addressModelTo == null){
				return;
			}else if(addressModelFrom.getAddress() == null){
                return;
            }else if(addressModelTo.getAddress() == null){
                return;
            }
			tracker.send(new HitBuilders.EventBuilder().setCategory("Search Cabs").setAction("Search Cabs").setLabel("Search Cabs").build());

			isLocationNotUpdated = false;
			Location locationA = new Location("Start point");

			locationA.setLatitude(addressModelFrom.getAddress()
					.getLatitude());
			locationA.setLongitude(addressModelFrom.getAddress()
					.getLongitude());

			Location locationB = new Location("End point");

			locationB.setLatitude(addressModelTo.getAddress()
					.getLatitude());
			locationB.setLongitude(addressModelTo.getAddress()
					.getLongitude());

			float distance = locationA.distanceTo(locationB);

			if (distance < FromToMinDestance) {
				new AlertDialog.Builder(getActivity())
						.setTitle("")
						.setMessage(
								"From and To locations are too close. Please try with different locations")
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int which) {
										// continue with delete
										dialog.cancel();

									}
								})

						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}else{

				rideObject = new RideObject("", "", "",
						addressModelFrom.getShortname(), addressModelTo.getShortname(),
						addressModelFrom.getLongname(), addressModelTo.getLongname());
				performCabSearch();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public  void sortCabSerachList(){

		if (togglePriceTimeSort) {
			togglePriceTimeSort = false;
			((NewHomeScreen)getActivity()).setTimeIcon();
			JSONArray jsonArray = performSortOnResults(mCabSearchArray,
					"low_estimate");
			mCabSearchArray = jsonArray;
		} else {
			togglePriceTimeSort = true;
			((NewHomeScreen)getActivity()).setRupeeIcon();
			JSONArray jsonArray = performSortOnResults(mCabSearchArray,
					"timeEstimate");
			mCabSearchArray = jsonArray;
		}

		updateGridView();

	}

	private void performCabSearch() {

		if (fAddress == null) {

			// from_places.requestFocus();

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage("Please Enter From Location. If you have already selected a location on map please try again by selecting a nearby location");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

		} else {
			// successfull for all
			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
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
				Log.d("BookaCab", "performCabSearch");
				if (tAddress == null) {
					PerformCabSearchTimeAsync performCabSearchTimeAsync = new PerformCabSearchTimeAsync();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						performCabSearchTimeAsync
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						performCabSearchTimeAsync.execute();
					}
				} else {
					DistanceTimeEstimateAsync distanceTimeEstimateAsync = new DistanceTimeEstimateAsync();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						distanceTimeEstimateAsync
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						distanceTimeEstimateAsync.execute();
					}
				}

			}
		}

	}

	public class DistanceTimeEstimateAsync extends
			AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... args) {


			try {
				Log.d("DistanceTimeEstimateAsync",
						"fAddress : " + fAddress.toString() + " tAddress : "
								+ tAddress.toString());
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/directions/xml?origin="
								+ Double.toString(fAddress.getLatitude())
								+ ","
								+ Double.toString(fAddress.getLongitude())
								+ "&destination="
								+ Double.toString(tAddress.getLatitude())
								+ ","
								+ Double.toString(tAddress.getLongitude())
								+ "&sensor=false&units=metric&alternatives=false&mode=driving&key="
								+ GlobalVariables.GoogleMapsAPIKey);
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

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
					Log.d("DistanceTimeEstimateAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("DistanceTimeEstimateAsync",
						"DistanceTimeEstimateAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {

				try {
					DistanceTimeEstimateHandler distanceTimeEstimateHandler = new DistanceTimeEstimateHandler();
					XMLReader xmlReader = SAXParserFactory.newInstance()
							.newSAXParser().getXMLReader();
					xmlReader.setContentHandler(distanceTimeEstimateHandler);
					xmlReader.parse(new InputSource(new StringReader(result)));

					ArrayList<Double> distance = distanceTimeEstimateHandler.distanceArray;
					ArrayList<Double> duration = distanceTimeEstimateHandler.durationArray;

					performCabSearchPrice(fAddress, tAddress,
							(distance.get(distance.size() - 1) / 1000),
							(duration.get(duration.size() - 1) / 60));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				hideProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void performCabSearchPrice(Address fromAddress, Address toAddress,
			Double estDistance, Double estDuration) {

		try {
			URL url = new URL(GlobalVariables.ServiceUrl
					+ "/fetchCabDetailsNew.php");
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
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy kk:mm:ss");

			String authString = estDistance.toString()
					+ String.valueOf(toAddress.getLatitude())
					+ String.valueOf(toAddress.getLongitude())
					+ estDuration.toString()
					+ fromAddress.getLocality().toString()
					+ String.valueOf(fromAddress.getLatitude())
					+ String.valueOf(fromAddress.getLongitude())
					+ simpleDateFormat.format(new Date())
					+ toAddress.getLocality().toString();

			bufferedWriter.write("FromCity="
					+ fromAddress.getLocality().toString() + "&ToCity="
					+ toAddress.getLocality().toString() + "&slat="
					+ String.valueOf(fromAddress.getLatitude()) + "&slon="
					+ String.valueOf(fromAddress.getLongitude()) + "&elat="
					+ String.valueOf(toAddress.getLatitude()) + "&elon="
					+ String.valueOf(toAddress.getLongitude()) + "&dist="
					+ estDistance.toString() + "&stime="
					+ simpleDateFormat.format(new Date()) + "&etime="
					+ estDuration.toString() + "&auth="
					+ GlobalMethods.calculateCMCAuthString(authString));
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStream.close();

			Log.d("performCabSearchPrice", "performCabSearchPrice query : "
					+ GlobalVariables.ServiceUrl + "/fetchCabDetailsNew.php?"
					+ "FromCity=" + fromAddress.getLocality().toString()
					+ "&ToCity=" + toAddress.getLocality().toString()
					+ "&slat=" + String.valueOf(fromAddress.getLatitude())
					+ "&slon=" + String.valueOf(fromAddress.getLongitude())
					+ "&elat=" + String.valueOf(toAddress.getLatitude())
					+ "&elon=" + String.valueOf(toAddress.getLongitude())
					+ "&dist=" + estDistance.toString() + "&stime="
					+ simpleDateFormat.format(new Date()) + "&etime="
					+ estDuration.toString());

			int responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				String line = "";
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					response += line;
				}

			} else {
				response = "";
				Log.d("performCabSearchPrice",
						"responseCode != HttpsURLConnection.HTTP_OK : "
								+ responseCode);
				return;
			}

			Log.d("performCabSearchPrice", "performCabSearchPrice response : "
					+ response);

			if (response.contains("Unauthorized Access")) {

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("BookaCabFragmentActivity",
								"performCabSearchPrice Unauthorized Access");
						Toast.makeText(
								getActivity(),
								getResources().getString(
										R.string.exceptionstring),
								Toast.LENGTH_LONG).show();
					}
				});

				return;
			}

			if (!response.toLowerCase().contains("error")) {

				try {
					JSONArray responseArray = new JSONArray(response);
					if (responseArray.length() > 0) {

						if (rideObject != null) {
							rideObject.estDistance = estDistance.toString();
							rideObject.estDuration = estDuration.toString();
						} else {
							rideObject = new RideObject();
							rideObject.estDistance = estDistance.toString();
							rideObject.estDuration = estDuration.toString();
						}

						mCabSearchArray = new JSONArray();
						for (int i = 0; i < responseArray.length(); i++) {
							mCabSearchArray.put(responseArray.getJSONObject(i));
							// JSONArray temp = responseArray.getJSONArray(i);
							// for (int j = 0; j < temp.length(); j++) {
							// mCabSearchArray.put(temp.getJSONObject(j));
							// }
						}

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if (mCabSearchArray.length() > 0) {
									// Default sort by time
									togglePriceTimeSort = true;
									((NewHomeScreen) getActivity())
											.setRupeeIcon();
									JSONArray jsonArray = performSortOnResults(
											mCabSearchArray, "timeEstimate");
									mCabSearchArray = jsonArray;
								}

								updateGridView();
							}
						});
					} else {

					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getActivity(),
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								getActivity(),
								"No cabs found!!!",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getActivity(),
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			});
		}

	}

	private void doneClicked() {

		if (locationManager != null)
			locationManager.removeUpdates(CabsFragment.this);
		getView().findViewById(R.id.rlMapView).setVisibility(View.GONE);
		getView().findViewById(R.id.llMainView).setVisibility(View.VISIBLE);
		 myMap.getUiSettings().setAllGesturesEnabled(false);
		 String fromlocationname = fromlocation.getText().toString().trim();
		flagchk = true;
		if(invitemapcenter == null){
			return;
		}
		if (whichdotclick.equalsIgnoreCase("fromdot")) {
            try {
                fAddress = null; // reset previous
			if(mapAddress != null){
                fAddress = mapAddress;
                etFromPlace.setText("");
                fromshortname = MapUtilityMethods.getAddressshort(getActivity(),fAddress);
                etFromPlace.setText(fromlocationname);
                String jnd = etFromPlace.getText().toString().trim();
                addressModelFrom = new AddressModel();
                    addressModelFrom.setAddress(fAddress);
                    addressModelFrom.setShortname(fromshortname);
                    addressModelFrom.setLongname(etFromPlace.getText().toString());


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

		} else if (whichdotclick.equalsIgnoreCase("todot")) {
            tAddress = null; // reset previous
            etToPlace.setText("");
            if (mapAddress != null) {
                try {
                    tAddress = mapAddress;
                    toshortname = MapUtilityMethods.getAddressshort(getActivity(), tAddress);
                    etToPlace.setText(fromlocationname);
                    addressModelTo = new AddressModel();
                    addressModelTo.setAddress(tAddress);
                    addressModelTo.setShortname(toshortname);
                    addressModelTo.setLongname(etToPlace.getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        isRefresingCabList = false;
		validateSearchCabRequest();

	}

	private void initMap() {
		getView().findViewById(R.id.dot_progress_bar).setVisibility(View.GONE);
		getView().findViewById(R.id.fromlocation).setVisibility(View.VISIBLE);

		myMap = mapFragment.getMap();

		if (myMap != null) {
			/*myMap = ((SupportMapFragment) getChildFragmentManager()
					.findFragmentById(R.id.frommap)).getMap();
*/
          //  myMap = ((SupportMapFragment) getMapFragment()).getMap();
			myMap.setMyLocationEnabled(true);

			myMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition cameraPosition) {

					invitemapcenter = cameraPosition.target;
                    locationHandler(invitemapcenter, locationModelListener1);

				/*	String address = MapUtilityMethods.getAddress(
							getActivity(), invitemapcenter.latitude,
							invitemapcenter.longitude);
					Log.d("address", "" + address);

					((TextView) getView().findViewById(R.id.fromlocation))
							.setText(address);*/

				}
			});
		}
	}

    private SupportMapFragment getMapFragment() {
        FragmentManager fm = null;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          //  fm = getFragmentManager();
			fm = getActivity().getSupportFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }

        return (SupportMapFragment) fm.findFragmentById(R.id.frommap);
    }

	private void picFromLocation() {
		//initMap();

		if (mycurrentlocationobject == null)
			mycurrentlocationobject = getLocation();

		if (mycurrentlocationobject != null) {
			whichdotclick = "fromdot";

			double latitude, longitude;
			if (!etFromPlace.getText().toString().trim().isEmpty()
					&& fAddress != null) {
				// Getting latitude of the current location
				latitude = fAddress.getLatitude();

				// Getting longitude of the current location
				longitude = fAddress.getLongitude();
			} else {
				// Getting latitude of the current location
				latitude = mycurrentlocationobject.getLatitude();

				// Getting longitude of the current location
				longitude = mycurrentlocationobject.getLongitude();
			}

			// Creating a LatLng object for the current location
			LatLng currentlatLng = new LatLng(latitude, longitude);

			// Showing the current location in Google Map
			myMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));

			// Zoom in the Google Map
			myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		/*	String address = MapUtilityMethods.getAddress(getActivity(), latitude, longitude);

			((TextView) getView().findViewById(R.id.fromlocation)).setText(address);*/

			getView().findViewById(R.id.rlMapView).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.llMainView).setVisibility(View.GONE);
		}
	}

	private void picToLocation() {
		//initMap();
		if (mycurrentlocationobject == null)
			mycurrentlocationobject = getLocation();

		// TODO Auto-generated method stub
		// dismissKeyboard();

		if (mycurrentlocationobject != null) {
			whichdotclick = "todot";

			double latitude, longitude;
			if (!etToPlace.getText().toString().trim().isEmpty()
					&& tAddress != null) {
				// Getting latitude of the current location
				latitude = tAddress.getLatitude();

				// Getting longitude of the current location
				longitude = tAddress.getLongitude();
			} else {
				// Getting latitude of the current location
				latitude = mycurrentlocationobject.getLatitude();

				// Getting longitude of the current location
				longitude = mycurrentlocationobject.getLongitude();
			}

			// Creating a LatLng object for the current location
			LatLng currentlatLng = new LatLng(latitude, longitude);

			// Showing the current location in Google Map
			myMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));

			// Zoom in the Google Map
			myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

			/*String address = MapUtilityMethods.getAddress(getActivity(),
					latitude, longitude);

			((TextView) getView().findViewById(R.id.fromlocation))
					.setText(address);*/

			getView().findViewById(R.id.rlMapView).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.llMainView).setVisibility(View.GONE);

		}
	}

	public Location getLocation() {
		Location location = null;
		try {
			locationManager = (LocationManager) getActivity().getSystemService(
					getActivity().LOCATION_SERVICE);

			// getting GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setMessage("Please check your location services");
				dialog.setCancelable(false);
				/*dialog.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								
								 * Intent intent = getActivity().getIntent();
								 * intent
								 * .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								 * getActivity().finish();
								 * startActivity(intent);
								 

							}
						});*/
				dialog.setNegativeButton("Settings",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								paramDialogInterface.dismiss();
								Intent myIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivityForResult(myIntent,
										LOCATION_REQUEST);
								// get gps

							}
						});
				dialog.show();
				return null;
			} else {

				double lat = 0;
				double lng = 0;
				// get the location by gps
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 20000, 1, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lng = location.getLongitude();
							}
						}
					}
				}

				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 1, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							lat = location.getLatitude();
							lng = location.getLongitude();
						}
					}
				}
				/*if (mycurrentlocationobject != null)
					LocationAddress.getAddressFromLocation(
							mycurrentlocationobject.getLatitude(),
							mycurrentlocationobject.getLongitude(),
							getActivity(), new GeocoderHandler());*/

				Log.d("lat", "" + lat);
				Log.d("lng", "" + lng);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		mycurrentlocationobject = location;
		if (mycurrentlocationobject != null && isLocationNotUpdated) {
			LocationAddress.getAddressFromLocation(
					mycurrentlocationobject.getLatitude(),
					mycurrentlocationobject.getLongitude(), getActivity(),
					new GeocoderHandler());
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
			Log.d("onActivityResult",
					"onActivityResult requestCode : "
							+ requestCode
							+ " resultCode : "
							+ resultCode
							+ " data : "
							+ data.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			GetUberAccessTokenAsync getUberAccessTokenAsync = new GetUberAccessTokenAsync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getUberAccessTokenAsync
						.executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			} else {
				getUberAccessTokenAsync
						.execute(data
								.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			}
		} else if (requestCode == 2 && resultCode == getActivity().RESULT_OK) {
			Log.d("onActivityResult",
					"onActivityResult requestCode : "
							+ requestCode
							+ " resultCode : "
							+ resultCode
							+ " data : "
							+ data.getStringExtra(MobileSiteFragment.ARGUMENTS_OLA_REQUEST_URL));

			GetOlaBookingStatusAsync getOlaBookingStatusAsync = new GetOlaBookingStatusAsync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getOlaBookingStatusAsync
						.executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_OLA_REQUEST_URL),
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_OLA_REQUEST_ID));
			} else {
				getOlaBookingStatusAsync
						.execute(
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_OLA_REQUEST_URL),
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_OLA_REQUEST_ID));
			}
		}else if (requestCode == 3 && resultCode == getActivity().RESULT_OK) {

				String value = (String) data.getExtras().getString("address");
				double latitude = data.getExtras().getDouble("latitude");
				double longitude =  data.getExtras().getDouble("longitude");

				Log.d("from_place:::", value);

				// from_places.append(value);
				etFromPlace.setText(value);

				if (value.equalsIgnoreCase("") || value.isEmpty()) {

				} else {
					fAddress = null; // reset previous

					//fAddress = geocodeAddress(etFromPlace.getText().toString());
                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(latitude);
                    address.setLongitude(longitude);
                    if(getCodeAddressByLatLong(new LatLng(latitude,longitude)) != null){
                        address.setLocality(getCodeAddressByLatLong(new LatLng(latitude, longitude)).getLocality());
                    }else {
                        etToPlace.setText("");
                        return;

                    }
                    fAddress = address;
					if (fAddress == null) {
						/*Toast.makeText(
								getActivity(),
								"Could not locate the address, please try using the map or a different address",
								Toast.LENGTH_LONG).show();
				*/	} else {
						addressModelFrom = new AddressModel();
						addressModelFrom.setAddress(fAddress);
						/*fromshortname = MapUtilityMethods.getAddressshort(
								getActivity(), fAddress.getLatitude(),
								fAddress.getLongitude());*/
                        fromshortname = MapUtilityMethods.getaddressfromautoplace(getActivity(), value);

                        addressModelFrom.setShortname(fromshortname);
						addressModelFrom.setLongname(etFromPlace.getText().toString());
						isRefresingCabList = false;
						validateSearchCabRequest();

					}
				}
				


		}else if (requestCode == 4 && resultCode == getActivity().RESULT_OK) {
				String value = (String) data.getExtras().getString("address");
				double latitude = data.getExtras().getDouble("latitude");
				double longitude =  data.getExtras().getDouble("longitude");

				etToPlace.setText(value);
				if (value.equalsIgnoreCase("") || value.isEmpty()) {

				} else {

					tAddress = null; // reset previous

					//tAddress = geocodeAddress(etToPlace.getText().toString());
                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(latitude);
                    address.setLongitude(longitude);
					if(getCodeAddressByLatLong(new LatLng(latitude,longitude)) != null){
						address.setLocality(getCodeAddressByLatLong(new LatLng(latitude, longitude)).getLocality());
					}else {
						etToPlace.setText("");
						return;

					}

                    tAddress = address;
					if (tAddress == null) {
						/*Toast.makeText(
								getActivity(),
								"Could not locate the address, please try using the map or a different address",
								Toast.LENGTH_LONG).show();
			*/		} else {
						addressModelTo = new AddressModel();
						addressModelTo.setAddress(tAddress);
						/*toshortname = MapUtilityMethods.getAddressshort(
								getActivity(), tAddress.getLatitude(),
								tAddress.getLongitude());*/
						toshortname = MapUtilityMethods.getaddressfromautoplace(getActivity(), value);

						addressModelTo.setShortname(toshortname);
						addressModelTo.setLongname(etToPlace.getText()
								.toString());
						isRefresingCabList = false;
						validateSearchCabRequest();
					}
				}

			}else if(resultCode == getActivity().RESULT_OK && requestCode ==  SELECT_PAYMENT_REQUEST && data != null){
				try{
					int paymentType = data.getExtras().getInt("type");
					SPreference.getPref(getActivity()).edit().putInt(SPreference.RIDE_OFFER_WALLET_TYPE,paymentType).commit();
					SPreference.getPref(getActivity()).edit().putInt(SPreference.RIDE_TAKER_WALLET_TYPE,paymentType).commit();
					SPreference.getPref(getActivity()).edit().putInt(SPreference.SELECTED_PAY_TYPE,paymentType).commit();

					checkTokenExist();
				}catch (NullPointerException e){
					e.printStackTrace();
				}

		}

	}

	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String locationAddress;
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				locationAddress = bundle.getString("address");
				if (locationAddress != null) {
					etFromPlace.setText(locationAddress.substring(
							locationAddress.indexOf("Address:") + 8,
							locationAddress.length()).trim());
					String value = etFromPlace.getText().toString();
					if (value.equalsIgnoreCase("") || value.isEmpty()) {

					} else {
						fAddress = null; // reset previous

						fAddress = geocodeAddress(etFromPlace.getText()
								.toString());
						if (fAddress == null) {
							etFromPlace.setText("");
								isLocationNotUpdated = false;
							/*	Toast.makeText(
										getActivity(),
										"Could not locate the address, please try using the map or a different address",
										Toast.LENGTH_LONG).show();
				*/			
							
						} else {
							isLocationNotUpdated = false;
							addressModelFrom = new AddressModel();
							addressModelFrom.setAddress(fAddress);
							fromshortname = MapUtilityMethods.getAddressshort(
									getActivity(), fAddress.getLatitude(),
									fAddress.getLongitude());
							addressModelFrom.setShortname(fromshortname);
							addressModelFrom.setLongname(etFromPlace.getText()
									.toString());
						}
					}
				}

				break;
			default:
				locationAddress = null;
			}

			L.mesaage(locationAddress);
		}
	}

	private String getFromPlace() {
		return etFromPlace.getText().toString().trim();
	}

	private String getToPlace() {
		return etToPlace.getText().toString();
	}

	private void dismissKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getActivity()
				.getCurrentFocus().getWindowToken(), 0);
	}

	private Address geocodeAddress(String addressString) {
		Address addressReturn = null;
		try {
			if(!TextUtils.isEmpty(addressString)){
				Geocoder geocoder = new Geocoder(getActivity());
				try {
					ArrayList<Address> arrayList = (ArrayList<Address>) geocoder
							.getFromLocationName(addressString, 1);
					Log.d("geocodeAddress", "geocodeAddress : " + arrayList.toString());
					if (arrayList.size() > 0) {
						addressReturn = arrayList.get(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}


		return addressReturn;
	}

	private class CustomGridViewCabSearchAdapter extends BaseAdapter {

		private Activity mContext;
		private JSONArray mEntries;

		public void init(Activity context,
				JSONArray entries){
			this.mContext = context;
			this.mEntries = entries;
		}

		@Override
		public int getCount() {
			if(mEntries !=null && mEntries.length()>0){
				return mEntries.length();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Log.d(TAG, "getView : " + position);
			// if (convertView == null) {
			// convertView =
			// (View)mContext.getLayoutInflater().inflate(R.layout.cell_grid_view_cabsearch,
			// parent, false);
			// }
			//if(convertView == null){
				convertView = (View) mContext.getLayoutInflater().inflate(
						R.layout.cell_grid_view_cabsearch, parent, false);
			//}
			String jsonString = "";
			try {
				TextView textView = (TextView) convertView
						.findViewById(R.id.textViewCabName);
				textView.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.ROBOTO_REGULAR));
				jsonString = mEntries.getJSONObject(position).get("CabName")
						.toString();
				textView.setText((jsonString.isEmpty() || jsonString
						.equalsIgnoreCase("null")) ? "-" : jsonString.toUpperCase());

				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.imageViewCabCoIcon);
				textView.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);

				if (jsonString.equalsIgnoreCase("Ola")) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.cab_ola_icon));
				} else if (jsonString.equalsIgnoreCase("Uber")) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.cab_uber_icon));
				} else if (jsonString.equalsIgnoreCase("Meru")) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.cab_meru_icon));
				} else if (jsonString.equalsIgnoreCase("TaxiForSure")) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.cab_tfs_icon));
				} else if (jsonString.equalsIgnoreCase("Mega")) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.cab_mega_icon));
				} else {
					textView.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.GONE);
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewCabType);
				try {
					jsonString = mEntries.getJSONObject(position)
							.get("CarType").toString();
					textView.setText((jsonString.isEmpty() || jsonString
							.equalsIgnoreCase("null")) ? "-" : jsonString);
				} catch (Exception e) {
					textView.setText("-");
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewEstTime);
				try {
					textView.setText("Est. time: "
							+ String.format("%d%n",
									Math.round(Double
											.parseDouble(mCabSearchArray
													.getJSONObject(position)
													.get("timeEstimate")
													.toString()) / 60))
							+ "mins");
				} catch (Exception e) {
					textView.setText("Est. time: -");
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewEstPrice);
				try {
					String lowString = mEntries.getJSONObject(position)
							.get("low_estimate").toString();
					String highString = mEntries.getJSONObject(position)
							.get("high_estimate").toString();
					if (lowString.isEmpty() || highString.isEmpty()
							|| lowString.equalsIgnoreCase("na")
							|| lowString.equalsIgnoreCase("null")
							|| highString.equalsIgnoreCase("na")
							|| highString.equalsIgnoreCase("null")) {
						textView.setText("Est. price: " + "-");
					} else {
						textView.setText("Est. price: \u20B9" + mEntries.getJSONObject(position)
										.get("low_estimate").toString() + "-" + mEntries.getJSONObject(position).get("high_estimate").toString());
					}

				} catch (Exception e) {
					textView.setText("Est. price: " + "-");
				}

				RatingBar ratingBar = (RatingBar) convertView
						.findViewById(R.id.ratingBarBookCab);
				try {
					if(!TextUtils.isEmpty(mEntries.getJSONObject(position).get("Rating").toString())){
						ratingBar.setRating(Float.parseFloat(mEntries.getJSONObject(position).get("Rating").toString()));

					}

				} catch (Exception e) {
					// e.printStackTrace();
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewNumberOfRatings);

				try {
					if (mEntries.getJSONObject(position).get("NoofReviews")
							.toString().equals("0")) {
						textView.setText("");
					} else {
						textView.setText("("
								+ mEntries.getJSONObject(position)
										.get("NoofReviews").toString() + ")");
					}
				} catch (Exception e) {
					textView.setText("");
				}

				Button button = (Button) convertView
						.findViewById(R.id.buttonBookNow);
				button.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.ROBOTO_LIGHT));
				button.setTag("BookNowButton" + position);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						int pos = Integer.parseInt(view.getTag().toString()
								.replace("BookNowButton", ""));
						Log.d("BookaCab", "buttonBookNow onClick pos : " + pos);

						try {
							if (mCabSearchArray.getJSONObject(pos)
									.get("CabName").toString().toLowerCase()
									.contains("taxiforsure")) {
								gridviewItemClick(pos);
							} else {
								bookNowButtonPress(pos, "", "");
							}
							tracker.send(new HitBuilders.EventBuilder().setCategory("Book Cab").setAction("Book Cab pressed").setLabel("Book Cab pressed").build());

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}

	private void gridviewItemClick(final int position) {
		// Log.d("BookaCab",
		// "mGridViewCabSearch onItemClick position : " +
		// position);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View builderView = (View) getActivity().getLayoutInflater().inflate(
				R.layout.book_cab_detail_dialog, null);

		final EditText editTextUserName = (EditText) builderView
				.findViewById(R.id.editTextBookCabUserName);
		final EditText editTextPassword = (EditText) builderView
				.findViewById(R.id.editTextBookCabPassword);
		final TextView messageTextView = (TextView) builderView
				.findViewById(R.id.messageUserCredentials);

		String jsonString = "";
		TextView textView = (TextView) builderView
				.findViewById(R.id.textViewCabName);
		try {
			jsonString = mCabSearchArray.getJSONObject(position).get("CabName")
					.toString();
			textView.setText((jsonString.isEmpty() || jsonString
					.equalsIgnoreCase("null")) ? "-" : jsonString);
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView.findViewById(R.id.textViewCabType);
		try {
			jsonString = mCabSearchArray.getJSONObject(position).get("CarType")
					.toString();
			textView.setText((jsonString.isEmpty() || jsonString
					.equalsIgnoreCase("null")) ? "-" : jsonString);
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView.findViewById(R.id.textViewEstTime);
		try {
			textView.setText("Est. time: "
					+ String.format(
							"%d%n",
							Math.round(Double.parseDouble(mCabSearchArray
									.getJSONObject(position)
									.get("timeEstimate").toString()) / 60))
					+ "mins");
		} catch (Exception e) {
			textView.setText("Est. time: -");
		}
		textView = (TextView) builderView.findViewById(R.id.textViewEstPrice);
		try {
			String lowString = mCabSearchArray.getJSONObject(position)
					.get("low_estimate").toString();
			String highString = mCabSearchArray.getJSONObject(position)
					.get("high_estimate").toString();
			if (lowString.isEmpty() || highString.isEmpty()
					|| lowString.equalsIgnoreCase("na")
					|| lowString.equalsIgnoreCase("null")
					|| highString.equalsIgnoreCase("na")
					|| highString.equalsIgnoreCase("null")) {
				textView.setText("Est. price: " + "-");
			} else {
				textView.setText("Est. price: \u20B9"
						+ mCabSearchArray.getJSONObject(position)
								.get("low_estimate").toString()
						+ "-"
						+ mCabSearchArray.getJSONObject(position)
								.get("high_estimate").toString());
			}

		} catch (Exception e) {
			textView.setText("Est. price: " + "-");
		}
		textView = (TextView) builderView
				.findViewById(R.id.textViewBookCabBaseFare);
		try {
			String baseFareString = mCabSearchArray.getJSONObject(position)
					.get("BaseFare").toString();
			String baseKmString = mCabSearchArray.getJSONObject(position)
					.get("BaseFareKM").toString();
			if (baseFareString.isEmpty()
					|| baseFareString.equalsIgnoreCase("na")
					|| baseFareString.equalsIgnoreCase("null")
					|| baseKmString.isEmpty()
					|| baseKmString.equalsIgnoreCase("na")
					|| baseKmString.equalsIgnoreCase("null")) {
				textView.setText("-");
			} else {
				textView.setText("\u20B9"
						+ mCabSearchArray.getJSONObject(position)
								.get("BaseFare").toString()
						+ " for first "
						+ mCabSearchArray.getJSONObject(position)
								.get("BaseFareKM").toString() + "Kms");
			}
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView
				.findViewById(R.id.textViewBookCabRatePerKM);
		try {
			jsonString = mCabSearchArray.getJSONObject(position)
					.get("RatePerKMAfterBaseFare").toString();
			if (jsonString.isEmpty() || jsonString.equalsIgnoreCase("na")
					|| jsonString.equalsIgnoreCase("null")) {
				textView.setText("-");
			} else {
				textView.setText("\u20B9" + jsonString + " per Km");
			}
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView
				.findViewById(R.id.textViewBookCabNightBase);
		try {
			Double multiplier = Double.parseDouble(mCabSearchArray
					.getJSONObject(position).get("NightTimeRateMultiplier")
					.toString());
			Long nightRate = Math
					.round(multiplier
							* Double.parseDouble(mCabSearchArray
									.getJSONObject(position).get("BaseFare")
									.toString()));

			textView.setText("\u20B9"
					+ Long.toString(nightRate)
					+ " for first "
					+ mCabSearchArray.getJSONObject(position).get("BaseFareKM")
							.toString() + "Kms");
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView
				.findViewById(R.id.textViewBookCabNightPer);
		try {
			Double multiplier = Double.parseDouble(mCabSearchArray
					.getJSONObject(position).get("NightTimeRateMultiplier")
					.toString());
			Long nightRate = Math.round(multiplier
					* Double.parseDouble(mCabSearchArray
							.getJSONObject(position)
							.get("RatePerKMAfterBaseFare").toString()));

			textView.setText("\u20B9" + Long.toString(nightRate) + " per Km");
		} catch (Exception e) {
			textView.setText("-");
		}
		textView = (TextView) builderView
				.findViewById(R.id.textViewBookCabNightTime);
		try {
			String startString = mCabSearchArray.getJSONObject(position)
					.get("NightTimeStartHours").toString();
			String endString = mCabSearchArray.getJSONObject(position)
					.get("NightTimeEndHours").toString();
			if (startString.isEmpty() || startString.equalsIgnoreCase("na")
					|| startString.equalsIgnoreCase("null")
					|| endString.isEmpty() || endString.equalsIgnoreCase("na")
					|| endString.equalsIgnoreCase("null")) {
				textView.setText("-");
			} else {
				textView.setText(mCabSearchArray.getJSONObject(position)
						.get("NightTimeStartHours").toString()
						+ " hrs - "
						+ mCabSearchArray.getJSONObject(position)
								.get("NightTimeEndHours").toString() + " hrs");
			}
		} catch (Exception e) {
			textView.setText("-");
		}

		RatingBar ratingBar = (RatingBar) builderView
				.findViewById(R.id.ratingBarBookCab);
		try {

			ratingBar.setRating(Float.parseFloat(mCabSearchArray
					.getJSONObject(position).get("Rating").toString()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		textView = (TextView) builderView
				.findViewById(R.id.textViewNumberOfRatings);

		try {
			if (mCabSearchArray.getJSONObject(position).get("NoofReviews")
					.toString().equals("0")) {
				textView.setText("");
			} else {
				textView.setText("("
						+ mCabSearchArray.getJSONObject(position)
								.get("NoofReviews").toString() + ")");
			}
		} catch (Exception e) {
			textView.setText("");
		}

		ImageView imageView = (ImageView) builderView
				.findViewById(R.id.imageButtonCallNow);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					String phoneString = mCabSearchArray
							.getJSONObject(position).get("CabContactNo")
							.toString();
					if (phoneString.isEmpty()
							|| phoneString.equalsIgnoreCase("null")
							|| phoneString.equalsIgnoreCase("na")) {
					/*	Toast.makeText(getActivity(),
								"Phone number could not be retrieved",
								Toast.LENGTH_LONG).show();
			*/		} else {
						updateCMCRecords(
								mCabSearchArray.getJSONObject(position)
										.get("CabNameID").toString(),
								mCabSearchArray.getJSONObject(position)
										.get("CarType").toString(), "3",
								fAddress, tAddress, "", "", true, false,
								phoneString, "", "", false);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				/*	Toast.makeText(getActivity(),
							"Phone number could not be retrieved",
							Toast.LENGTH_LONG).show();*/
				}
			}
		});

		try {
			CabUserCredentialsReadWrite cabUserCredentialsReadWrite = new CabUserCredentialsReadWrite(
					getActivity());
			JSONArray jsonArray = cabUserCredentialsReadWrite
					.readArrayFromFile();
			JSONObject jsonObject = new JSONObject();

			if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("uber")) {

			} else if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("mega")) {

			} else if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("taxiforsure")) {
				editTextUserName.setVisibility(View.VISIBLE);
				editTextPassword.setVisibility(View.VISIBLE);
				messageTextView.setVisibility(View.VISIBLE);

				editTextUserName.setHint("Taxi For Sure username");
				editTextPassword.setHint("Taxi For Sure password");

				Log.d("BookaCab", "contains(taxiforsure) : "
						+ cabUserCredentialsReadWrite.readArrayFromFile());

				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						if (jsonArray
								.getJSONObject(i)
								.get("CabName")
								.toString()
								.equals(CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_TFS)) {
							jsonObject = jsonArray.getJSONObject(i);
							editTextUserName.setText(jsonObject.get("Username")
									.toString());
							editTextPassword.setText(jsonObject.get("Password")
									.toString());
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (Exception e) {

		}
		Button button = (Button) builderView.findViewById(R.id.buttonBookNow);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				dismissKeyboard();

				bookNowButtonPress(position, editTextUserName.getText()
						.toString(), editTextPassword.getText().toString());
			}
		});

		builder.setView(builderView);
		AlertDialog dialog = builder.create();

		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Log.d("BookaCab", "dialog onDismiss");
				lvSearchedCabs.setFocusableInTouchMode(true);
				lvSearchedCabs.requestFocus();
				dismissKeyboard();
			}
		});

		dialog.show();
	}

	public class PerformCabSearchTimeAsync extends
			AsyncTask<String, Void, String> {

		String result;
		private ProgressDialog dialog12;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
				try {
                    Log.d("PerformCabSearchTimeAsync", "doInBackground : "
                            + GlobalVariables.ServiceUrl + "/fetchCabDetailsNew.php?"
                            + "FromCity=" + fAddress.getLocality().toString()
                            + "&slat=" + String.valueOf(fAddress.getLatitude())
                            + "&slon=" + String.valueOf(fAddress.getLongitude()));


                    URL url = new URL(GlobalVariables.ServiceUrl
						+ "/fetchCabDetailsNew.php");
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				String authString = fAddress.getLocality().toString()
						+ String.valueOf(fAddress.getLatitude())
						+ String.valueOf(fAddress.getLongitude());

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write("FromCity="
						+ fAddress.getLocality().toString() + "&slat="
						+ String.valueOf(fAddress.getLatitude()) + "&slon="
						+ String.valueOf(fAddress.getLongitude()) + "&auth="
						+ GlobalMethods.calculateCMCAuthString(authString));
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
					Log.d("PerformCabSearchTimeAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("PerformCabSearchTimeAsync",
						"performCabSearchTime response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				if (result.contains("Unauthorized Access")) {
					Log.e("BookaCabFragmentActivity",
							"PerformCabSearchTimeAsync Unauthorized Access");
					Toast.makeText(getActivity(),
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (!result.toLowerCase().contains("error")) {

					try {
						JSONArray responseArray = new JSONArray(result);
						if (responseArray.length() > 0) {
							mCabSearchArray = new JSONArray();
							for (int i = 0; i < responseArray.length(); i++) {

								mCabSearchArray.put(responseArray.getJSONObject(i));
								// JSONArray temp = responseArray.getJSONArray(i);
								// for (int j = 0; j < temp.length(); j++) {
								// mCabSearchArray.put(temp.getJSONObject(j));
								// }
							}

							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {

									if (mCabSearchArray.length() > 0) {
										// Default sort by time
										togglePriceTimeSort = true;
										((NewHomeScreen) getActivity())
												.setRupeeIcon();
										JSONArray jsonArray = performSortOnResults(
												mCabSearchArray, "timeEstimate");
										mCabSearchArray = jsonArray;
									}

									updateGridView();
								}
							});

						} else {

						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getActivity(),
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} else {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getActivity(),
									"No cabs found!!!",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				hideProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void bookNowButtonPress(int position, String userName,
			String password) {
		try {
            tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Click").setLabel("Book Cab"+mCabSearchArray.getJSONObject(position).get("CabName").toString()).build());

			if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("uber")) {

				cabBookingPosition = position;
				bookUberCab(
						mCabSearchArray.getJSONObject(position).get("CabName")
								.toString(),
						mCabSearchArray.getJSONObject(position)
								.get("productId").toString(), fAddress,
						tAddress);
			} else if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("ola")) {
				cabBookingPosition = position;
				bookOlaCab(
						mCabSearchArray.getJSONObject(position).get("CabName")
								.toString(), fAddress, tAddress,
						mCabSearchArray.getJSONObject(position).get("CarType")
								.toString());
			} else if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("mega")) {

				cabBookingPosition = position;
				bookMegaCab(fAddress, tAddress);

			} else if (mCabSearchArray.getJSONObject(position).get("CabName")
					.toString().toLowerCase().contains("taxiforsure")) {

				if (userName.isEmpty() || password.isEmpty()) {
					Toast.makeText(getActivity(),
							"Please enter Username/Password", Toast.LENGTH_LONG)
							.show();
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("CabName",
							CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_TFS);
					jsonObject.put("Username", userName);
					jsonObject.put("Password", password);

					CabUserCredentialsReadWrite cabUserCredentialsReadWrite = new CabUserCredentialsReadWrite(
							getActivity());
					cabUserCredentialsReadWrite.saveToFile(jsonObject
							.toString());

					cabBookingPosition = position;

					String carType = mCabSearchArray.getJSONObject(position)
							.get("carType").toString().trim();
					String etaApp = Long.toString(Math.round(Double
							.valueOf(mCabSearchArray.getJSONObject(position)
									.get("timeEstimate").toString()) / 60));

					bookTFSCab(fAddress, userName, password, carType, etaApp);
				}

			} else {

				updateCMCRecords(
						mCabSearchArray.getJSONObject(position)
								.get("CabNameID").toString(),
						mCabSearchArray.getJSONObject(position).get("CarType")
								.toString(),
						"2",
						fAddress,
						tAddress,
						"",
						"",
						false,
						true,
						"",
						"http://"
								+ mCabSearchArray.getJSONObject(position)
										.get("CabMobileSite").toString(),
						mCabSearchArray.getJSONObject(position)
								.get("CabPackageName").toString(), false);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void updateCMCRecords(String cabName, String cabType,
			String modeID, Address from, Address to, String cabCoUsername,
			String bookingRef, boolean shouldCall, boolean shouldOpenSite,
			String phone, String mSite, String packName,
			boolean shouldOpenBookedCab) {

		if (!isOnline()) {
			return;
		}

		UpdateCMCRecordsAsync updateCMCRecordsAsync = new UpdateCMCRecordsAsync(
				shouldCall, shouldOpenSite, phone, mSite, packName,
				shouldOpenBookedCab);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa");
		Date date = Calendar.getInstance().getTime();
		String dateString, timeString;
		try {
			dateString = simpleDateFormat.format(date);
			timeString = simpleTimeFormat.format(date);
			// Log.d("BookaCab", "updateCMCRecords dateString : " + dateString
			// + " timeString : " + timeString);
		} catch (Exception e) {
			e.printStackTrace();
			dateString = "";
			timeString = "";
		}
		String param = "CabNameID="
				+ cabName
				+ "&CabType="
				+ cabType
				+ "&ModeID="
				+ modeID
                +"&rideType=3"
				+ "&FromLat="
				+ String.valueOf(from.getLatitude())
				+ ","
				+ String.valueOf(from.getLongitude())
				+ "&ToLat="
				+ String.valueOf((to == null) ? "" : to.getLatitude())
				+ ","
				+ String.valueOf((to == null) ? "" : to.getLongitude())
				+ "&MobileNumber="
				+ MobileNumberstr
				+ "&CabUserName="
				+ cabCoUsername
				+ "&BookingRefNo="
				+ bookingRef
				+ "&FromLocation="
				+ ((rideObject != null) ? ((rideObject.fromLongName == null || rideObject.fromLongName
						.isEmpty()) ? etFromPlace.getText().toString()
						: rideObject.fromLongName) : "")
				+ "&ToLocation="
				+ ((rideObject != null) ? ((rideObject.toLongName == null || rideObject.toLongName
						.isEmpty()) ? etToPlace.getText().toString()
						: rideObject.toLongName) : "")
				+ "&FromShortName="
				+ ((rideObject != null) ? ((rideObject.fromShortName == null || rideObject.fromShortName
						.isEmpty()) ? fromshortname : rideObject.fromShortName)
						: "")
				+ "&ToShortName="
				+ ((rideObject != null) ? ((rideObject.toShortName == null || rideObject.toShortName
						.isEmpty()) ? toshortname : rideObject.toShortName)
						: "")
				+ "&TravelDate="
				+ ((rideObject != null) ? ((rideObject.travelDate == null || rideObject.travelDate
						.isEmpty()) ? dateString : rideObject.travelDate) : "")
				+ "&TravelTime="
				+ ((rideObject != null) ? ((rideObject.travelTime == null || rideObject.travelTime
						.isEmpty()) ? timeString : rideObject.travelTime) : "")
				+ "&Distance="
				+ ((rideObject != null) ? rideObject.estDistance : "")
				+ "&ExpTripDuration="
				+ ((rideObject != null) ? rideObject.estDuration : "")
				+ "&CabId=" + ((rideObject != null) ? rideObject.cabID : "")
				+ "&DriverName="
				+ ((rideObject != null) ? rideObject.driverName : "")
				+ "&DriverNumber="
				+ ((rideObject != null) ? rideObject.driverPhone : "")
				+ "&CarNumber="
				+ ((rideObject != null) ? rideObject.vehicle : "")
				+ "&CarType=";

		Log.d("BookaCab", "updateCMCRecords param : " + param);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			updateCMCRecordsAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			updateCMCRecordsAsync.execute(param);
		}
	}

	public class UpdateCMCRecordsAsync extends AsyncTask<String, Void, String> {

		String result;
		boolean shouldCall = false, shouldOpenSite = false,
				shouldOpenBookedCabPage = false;
		String phoneNumber = "", mSiteAddress = "", packageName = "";

		public UpdateCMCRecordsAsync(boolean call, boolean site, String phone,
				String mSite, String packName, boolean shouldOpenBookedCab) {
			shouldCall = call;
			shouldOpenSite = site;
			phoneNumber = phone;
			mSiteAddress = mSite;
			packageName = packName;
			shouldOpenBookedCabPage = shouldOpenBookedCab;
		}

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("UpdateCMCRecordsAsync",
					"UpdateCMCRecordsAsync : " + args[0].toString()
							+ " boolean call : " + shouldCall
							+ " boolean site : " + shouldOpenSite);

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cmcRecords.php");
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
					Log.d("UpdateCMCRecordsAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("UpdateCMCRecordsAsync",
						"UpdateCMCRecordsAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// Toast.makeText(BookaCab.this,
				// "Something went wrong, please try again", Toast.LENGTH_LONG)
				// .show();
				// }
				// });
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try{
				hideProgressBar();

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (shouldCall) {
							Intent intent = new Intent(Intent.ACTION_DIAL, Uri
									.parse("tel:" + phoneNumber));
							startActivity(intent);
						}

						if (shouldOpenSite) {
							openAppOrMSite(packageName, mSiteAddress);
						}

						if (shouldOpenBookedCabPage) {
							/*Intent intent = new Intent(getActivity(),
									NewHomeScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
							getActivity().finish();*/
							((NewHomeScreen)getActivity()).refreshMyRideData();

						}
					}
				});
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void openAppOrMSite(final String packageName, String mSite) {

		if (checkIfAppInstalled(packageName)) {

			Intent launchIntent = getActivity().getPackageManager()
					.getLaunchIntentForPackage(packageName);
			startActivity(launchIntent);

		} else {

			if (mSite.isEmpty() || mSite.equalsIgnoreCase("null")
					|| mSite.equalsIgnoreCase("na")) {
				Toast.makeText(getActivity(),
						"Sorry, no booking information available",
						Toast.LENGTH_LONG).show();
			} else {
				Intent intent = new Intent(getActivity(),
						MobileSiteActivity.class);
				intent.putExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL,
						mSite);
				startActivity(intent);
			}
		}
	}

	private JSONArray performSortOnResults(JSONArray arrayToSort,
			String sortString) {

		JSONArray arraySorted = new JSONArray();

		HashMap<Double, JSONObject> hashMap = new HashMap<Double, JSONObject>();

		ArrayList<Integer> arrayListNoValues = new ArrayList<Integer>();

		JSONObject jsonObject = new JSONObject();

		// String logString = "", logString2 = "";

		for (int i = 0; i < arrayToSort.length(); i++) {

			try {
				jsonObject = arrayToSort.getJSONObject(i);
				// Log.d("BookaCab", "performSortOnResults low_estimate : " +
				// jsonObject.get("low_estimate").toString() +
				// " timeEstimate : " +
				// jsonObject.get("timeEstimate").toString());
				String string = jsonObject.get(sortString).toString();
				if (string.isEmpty() || string.equalsIgnoreCase("null")
						|| string.equalsIgnoreCase("na")) {
					arrayListNoValues.add(Integer.valueOf(i));
					// logString2 += (jsonObject.get("CabName").toString() +
					// " ");
				} else {
					Double key = Double.parseDouble(jsonObject.get(sortString)
							.toString());
					if (hashMap.get(key) != null) {
						// in case of same timeEstimate values for 2 cabs, one
						// value gets over-written, adding small constant to
						// differentiate keys
						int count = 0;
						while (hashMap.get(key) != null){
							count++;
                            key = key + count;

						}
                        hashMap.put((key), jsonObject);
					} else {
						hashMap.put(key, jsonObject);
					}

					// logString += (jsonObject.get("CabName").toString() +
					// " ");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				arrayListNoValues.add(Integer.valueOf(i));
				try {
					jsonObject = arrayToSort.getJSONObject(i);
					// logString2 += (jsonObject.get("CabName").toString() +
					// " ");
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}

		// Log.d("BookaCab",
		// "performSortOnResults hashMap keySet : " + hashMap.keySet());

		Map<Double, JSONObject> map = new TreeMap<Double, JSONObject>(hashMap);

		// Log.d("BookaCab", "performSortOnResults map : " + map);

		Object[] array = map.keySet().toArray(new Object[map.keySet().size()]);

		for (int i = 0; i < array.length; i++) {
			Log.d("BookaCab", "performSortOnResults array : " + array[i]);
			arraySorted.put(hashMap.get(array[i]));
		}

		for (int i = 0; i < arrayListNoValues.size(); i++) {
			try {
				JSONObject jsonObject2 = arrayToSort
						.getJSONObject(arrayListNoValues.get(i).intValue());
				arraySorted.put(jsonObject2);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		Log.d("BookaCab",
				"performSortOnResults arraySorted : " + arraySorted.length()
						+ " arrayToSort : " + arrayToSort.length());
		// Log.d("BookaCab",
		// "performSortOnResults logString : " + logString);
		// Log.d("BookaCab",
		// "performSortOnResults logString2 : " + logString2);
		// Log.d("BookaCab", "performSortOnResults arrayToSort : " +
		// arrayToSort);
		// Log.d("BookaCab", "performSortOnResults arraySorted : " +
		// arraySorted);

		return arraySorted;
	}

	private void updateGridView() {

		if (mCabSearchArray.length() > 0
				&& etToPlace.getText().toString().length() > 0) {
			((NewHomeScreen) getActivity()).setSortIconVisible();
			if(getView() != null){
				getView().findViewById(R.id.llButtons).setVisibility(View.VISIBLE);		
			}
			notifyCabAdpater();
			/*mGridViewCabSearch
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							gridviewItemClick(position);
						}
			});*/

			lvSearchedCabs.setFocusableInTouchMode(true);
			lvSearchedCabs.requestFocus();
			
		}else{
			if(getView() != null){
				getView().findViewById(R.id.llButtons).setVisibility(View.GONE);		
			}
		}

	
	}
	
	private void notifyCabAdpater(){
		if(cabAdapter != null){
			cabAdapter.init(getActivity(), mCabSearchArray);
			cabAdapter.notifyDataSetChanged();
		}else {
			cabAdapter = new CustomGridViewCabSearchAdapter();
			cabAdapter.init(getActivity(), mCabSearchArray);
			cabAdapter.notifyDataSetChanged();
		}
		
	}
	private void notifyCabAdpater(JsonArray jsonArray){
		if(cabAdapter != null){
			cabAdapter.init(getActivity(), mCabSearchArray);
			cabAdapter.notifyDataSetChanged();
		}else {
			cabAdapter = new CustomGridViewCabSearchAdapter();
			cabAdapter.init(getActivity(), mCabSearchArray);
			cabAdapter.notifyDataSetChanged();
		}
		
	}

	private void bookUberCab(final String cabType, final String productID,
			final Address startAddress, final Address endAddress) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null || endAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("");
			builder.setMessage("Please provide both From & To locations to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Please provide us with your Uber account information on the next page, you need to do this only once");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String authString = cabType
						+ String.valueOf(endAddress.getLatitude())
						+ String.valueOf(endAddress.getLongitude())
						+ String.valueOf(startAddress.getLatitude())
						+ String.valueOf(startAddress.getLongitude())
						+ productID;

				GetUberRequestIDAsync getUberRequestIDAsync = new GetUberRequestIDAsync();
				String param = "cabType=" + cabType + "&productid=" + productID
						+ "&lat=" + String.valueOf(startAddress.getLatitude())
						+ "&lon=" + String.valueOf(startAddress.getLongitude())
						+ "&elat=" + String.valueOf(endAddress.getLatitude())
						+ "&elon=" + String.valueOf(endAddress.getLongitude())
						+ "&cabID=&auth="
						+ GlobalMethods.calculateCMCAuthString(authString);
				mUberBookingInputParams = "&productid=" + productID + "&lat="
						+ String.valueOf(startAddress.getLatitude()) + "&lon="
						+ String.valueOf(startAddress.getLongitude())
						+ "&elat=" + String.valueOf(endAddress.getLatitude())
						+ "&elon=" + String.valueOf(endAddress.getLongitude());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					getUberRequestIDAsync.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, param);
				} else {
					getUberRequestIDAsync.execute(param);
				}
			}
		});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();*/
		String authString = cabType
				+ String.valueOf(endAddress.getLatitude())
				+ String.valueOf(endAddress.getLongitude())
				+ String.valueOf(startAddress.getLatitude())
				+ String.valueOf(startAddress.getLongitude())
				+ productID;

		GetUberRequestIDAsync getUberRequestIDAsync = new GetUberRequestIDAsync();
		String param = "cabType=" + cabType + "&productid=" + productID
				+ "&lat=" + String.valueOf(startAddress.getLatitude())
				+ "&lon=" + String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude())
				+ "&cabID=&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		mUberBookingInputParams = "&productid=" + productID + "&lat="
				+ String.valueOf(startAddress.getLatitude()) + "&lon="
				+ String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getUberRequestIDAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			getUberRequestIDAsync.execute(param);
		}
	}

	private void bookOlaCab(final String cabType, final Address startAddress,
			final Address endAddress, final String productID) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null || endAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("");
			builder.setMessage("Please provide both From & To locations to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Please provide us with your Ola account information on the next page");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String authString = cabType
						+ String.valueOf(endAddress.getLatitude())
						+ String.valueOf(endAddress.getLongitude())
						+ String.valueOf(startAddress.getLatitude())
						+ String.valueOf(startAddress.getLongitude())
						+ productID;

				GetOlaRequestIDAsync getOlaRequestIDAsync = new GetOlaRequestIDAsync();
				String param = "cabType=" + cabType + "&productid=" + productID
						+ "&lat=" + String.valueOf(startAddress.getLatitude())
						+ "&lon=" + String.valueOf(startAddress.getLongitude())
						+ "&elat=" + String.valueOf(endAddress.getLatitude())
						+ "&elon=" + String.valueOf(endAddress.getLongitude())
						+ "&cabID=&auth="
						+ GlobalMethods.calculateCMCAuthString(authString);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					getOlaRequestIDAsync.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, param);
				} else {
					getOlaRequestIDAsync.execute(param);
				}
			}
		});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();*/
		String authString = cabType
				+ String.valueOf(endAddress.getLatitude())
				+ String.valueOf(endAddress.getLongitude())
				+ String.valueOf(startAddress.getLatitude())
				+ String.valueOf(startAddress.getLongitude())
				+ productID;

		GetOlaRequestIDAsync getOlaRequestIDAsync = new GetOlaRequestIDAsync();
		String param = "cabType=" + cabType + "&productid=" + productID
				+ "&lat=" + String.valueOf(startAddress.getLatitude())
				+ "&lon=" + String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude())
				+ "&cabID=&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getOlaRequestIDAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			getOlaRequestIDAsync.execute(param);
		}
	}

	private void bookMegaCab(final Address startAddress,
			final Address endAddress) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null || endAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("");
			builder.setMessage("Please provide both From & To locations to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		String authString = String.valueOf(endAddress.getLatitude())
				+ String.valueOf(endAddress.getLongitude()) + MobileNumberstr
				+ String.valueOf(startAddress.getLatitude())
				+ String.valueOf(startAddress.getLongitude()) + "CreateBooking";

		BookMegaCabAsync bookMegaCabAsync = new BookMegaCabAsync();
		String param = "type=CreateBooking" + "&mobile=" + MobileNumberstr
				+ "&slat=" + String.valueOf(startAddress.getLatitude())
				+ "&slon=" + String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude())
				+ "&stime=&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			bookMegaCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					param);
		} else {
			bookMegaCabAsync.execute(param);
		}
	}

	/**
	 * Currently Editing this code
	 * 
	 * @param startAddress
	 * @param username
	 * @param password
	 * @param carType
	 * @param etaApp
	 */
	private void bookTFSCab(final Address startAddress, String username,
			String password, String carType, String etaApp) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("");
			builder.setMessage("Please provide From location to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		BookTFSAsync bookTFSAsync = new BookTFSAsync();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
		Date date = Calendar.getInstance().getTime();
		String dateString, timeString;
		try {
			dateString = simpleDateFormat.format(date);
			timeString = simpleTimeFormat.format(date);
			// Log.d("BookaCab", "updateCMCRecords dateString : " + dateString
			// + " timeString : " + timeString);
		} catch (Exception e) {
			e.printStackTrace();
			dateString = "";
			timeString = "";
		}

		String authString = carType + startAddress.getLocality() + etaApp
				+ etFromPlace.getText().toString().trim() + password
				+ etFromPlace.getText().toString().trim() + dateString
				+ String.valueOf(startAddress.getLatitude())
				+ String.valueOf(startAddress.getLongitude()) + timeString
				+ "appbooking" + username;

		String param = "type=booking" + "&username=" + username + "&password="
				+ password + "&car_type=" + carType + "&source=app"
				+ "&pickup_time=" + timeString + "&pickup_date=" + dateString
				+ "&city=" + startAddress.getLocality() + "&pickup_area="
				+ etFromPlace.getText().toString().trim() + "&landmark="
				+ etFromPlace.getText().toString().trim() + "&pickup_latitude="
				+ String.valueOf(startAddress.getLatitude())
				+ "&pickup_longitude="
				+ String.valueOf(startAddress.getLongitude())
				+ "&eta_from_app=" + etaApp + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			bookTFSAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					param);
		} else {
			bookTFSAsync.execute(param);
		}
	}

	public class GetUberRequestIDAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetUberRequestIDAsync",
					"GetUberRequestIDAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cabbookrequest.php");
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
					Log.d("GetUberRequestIDAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("GetUberRequestIDAsync",
						"GetUberRequestIDAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (result.contains("Unauthorized Access")) {
							Log.e("BookaCabFragmentActivity",
									"GetUberRequestIDAsync Unauthorized Access");
							Toast.makeText(
									getActivity(),
									getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
							return;
						}

						Intent intent = new Intent(getActivity(),
								MobileSiteActivity.class);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL,
								GlobalVariables.ServiceUrl
										+ "/uberapi.php?type=oauth");
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID,
								result);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_USERNAME,
								mUberUsername);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_PASSWORD,
								mUberPassword);

						// startActivity(intent);
						startActivityForResult(intent, 1);
					}
				});

			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				hideProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public class GetOlaRequestIDAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetOlaRequestIDAsync",
					"GetOlaRequestIDAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cabbookrequest.php");
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
					Log.d("GetOlaRequestIDAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("GetOlaRequestIDAsync",
						"GetOlaRequestIDAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						if (result.contains("Unauthorized Access")) {
							Log.e("BookaCabFragmentActivity",
									"GetOlaRequestIDAsync Unauthorized Access");
							Toast.makeText(
									getActivity(),
									getResources().getString(
											R.string.exceptionstring),
									Toast.LENGTH_LONG).show();
							return;
						}

						Intent intent = new Intent(getActivity(),
								MobileSiteActivity.class);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL,
								GlobalVariables.ServiceUrl
										+ "/olaApi.php?type=oauth");
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_OLA_REQUEST_ID,
								result);

						startActivityForResult(intent, 2);
					}
				});

			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				hideProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public class BookTFSAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("BookTFSAsync", "BookTFSAsync : " + args[0].toString());

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
					Log.d("BookTFSAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("BookTFSAsync", "BookTFSAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (result.contains("Unauthorized Access")) {

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("BookaCabFragmentActivity",
								"BookTFSAsync Unauthorized Access");
						Toast.makeText(
								getActivity(),
								getResources().getString(
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

						JSONObject jsonObjectData = new JSONObject(jsonObject
								.get("response_data").toString());
						String driverName = "", driverPhone = "", vehicleLicense = "", requestID = "";
						try {
							driverName = jsonObjectData.get("driver_name")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							driverPhone = jsonObjectData.get("driver_number")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							vehicleLicense = jsonObjectData.get(
									"vehicle_number").toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							requestID = jsonObjectData.get("booking_id")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (rideObject != null) {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						} else {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						}

						final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								builder.setTitle("Success");
								builder.setCancelable(false);
								builder.setMessage("Cab booked succesfully!\r\n"
										+ "Driver : "
										+ driverNameFinal
										+ " ("
										+ driverPhoneFinal
										+ ")\r\n"
										+ "Vehicle : " + vehicleLicenseFinal);
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												try {
													updateCMCRecords(
															mCabSearchArray
																	.getJSONObject(
																			cabBookingPosition)
																	.get("CabNameID")
																	.toString(),
															mCabSearchArray
																	.getJSONObject(
																			cabBookingPosition)
																	.get("CarType")
																	.toString(),
															"1", fAddress,
															tAddress, "",
															requestIDFinal,
															false, false, "",
															"", "", true);
												} catch (Exception e) {
													e.printStackTrace();
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

					} else {
						final String reason = jsonObject.get("error_desc")
								.toString();

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								builder.setTitle("Cab could not be booked");
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
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				hideProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public class BookMegaCabAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("BookMegaCabAsync",
					"BookMegaCabAsync : " + args[0].toString());

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
					Log.d("BookMegaCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("BookMegaCabAsync", "BookMegaCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (result.contains("Unauthorized Access")) {

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("BookaCabFragmentActivity",
								"BookMegaCabAsync Unauthorized Access");
						Toast.makeText(
								getActivity(),
								getResources().getString(
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

						JSONObject jsonObjectData = new JSONObject(jsonObject
								.get("data").toString());
						String driverName = "", driverPhone = "", vehicleLicense = "", requestID = "";
						try {
							driverName = jsonObjectData.get("DriverName")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							driverPhone = jsonObjectData.get("DriverNumber")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							vehicleLicense = jsonObjectData.get("VehicleNo")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							requestID = jsonObject.get("Jobno").toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (rideObject != null) {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						} else {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						}

						final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								builder.setTitle("Success");
								builder.setCancelable(false);
								builder.setMessage("Cab booked succesfully!\r\n"
										+ "Driver : "
										+ driverNameFinal
										+ " ("
										+ driverPhoneFinal
										+ ")\r\n"
										+ "Vehicle : " + vehicleLicenseFinal);
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												try {
													updateCMCRecords(
															mCabSearchArray
																	.getJSONObject(
																			cabBookingPosition)
																	.get("CabNameID")
																	.toString(),
															mCabSearchArray
																	.getJSONObject(
																			cabBookingPosition)
																	.get("CarType")
																	.toString(),
															"1", fAddress,
															tAddress, "",
															requestIDFinal,
															false, false, "",
															"", "", true);
												} catch (Exception e) {
													e.printStackTrace();
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

					} else {
						final String reason = jsonObject.get("data").toString();

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								builder.setTitle("Cab could not be booked");
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
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
				hideProgressBar();
			}catch (Exception e){
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

	private boolean checkIfAppInstalled(String packageName) {

		PackageManager packageManager = getActivity().getPackageManager();

		try {
			packageManager.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isListVisible(){
		if(mCabSearchArray != null && mCabSearchArray.length()>0){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void getLatLong(Location location) {
		mycurrentlocationobject = location;
		if (mycurrentlocationobject != null && isLocationNotUpdated) {
			/*LocationAddress.getAddressFromLocation(
					mycurrentlocationobject.getLatitude(),
					mycurrentlocationobject.getLongitude(), getActivity(),
					new GeocoderHandler());*/
            locationHandler(new LatLng(mycurrentlocationobject.getLatitude(),
                    mycurrentlocationobject.getLongitude()),locationModelListener2);
		}
		
	}
	
	public void refreshDataOnPageChange(){
		try{
			refershCabList();
			/*if(etFromPlace != null){
				etFromPlace.setText("");
				addressModelFrom = null;
			}
			
			if(etToPlace != null){
				etToPlace.setText("");
				addressModelTo = null;
			}
			if(lvSearchedCabs != null){
				//notifyCabAdpater();
				if(cabAdapter != null){
					cabAdapter.init(getActivity(), null);
					cabAdapter.notifyDataSetChanged();
				}
			}
			
			if(getView() != null){
				getView().findViewById(R.id.sharecabll).setVisibility(View.GONE);
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

    private boolean isWalletLinked(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MobikwikToken", 0);
        String token = sharedPreferences.getString("token", "");
        if(!TextUtils.isEmpty(token)){
            return true;
        }

        return false;
    }

    private void showWallletDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(
                getActivity()).create();
        alertDialog.setMessage("We need to link your wallet to process payments.");

        alertDialog.setButton("LINK NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
				Intent intent = null;
				if(SPreference.getRideOfferWalletType(getActivity()) == AppConstants.PAYMENT_TYPE_MOBIKWIK){
					intent = new Intent(getActivity(), FirstLoginWalletsActivity.class);
					intent.putExtra(AppConstants.ACTIVITYNAME,AppConstants.CABS_FRAGMENT);
					startActivity(intent);
				}

            }
        });


        // Showing Alert Message
        alertDialog.show();
    }

	private void showChooseWalletForPayment(){
		AlertDialog alertDialog = new AlertDialog.Builder(
				getActivity()).create();
		alertDialog.setMessage("Please select wallet for receiving payment from riders");

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(getActivity(), ChoosePaymentTypeScreen.class);
				intent.putExtra("fromrideoffer",true);
				startActivityForResult(intent,SELECT_PAYMENT_REQUEST);


			}
		});


		// Showing Alert Message
		alertDialog.show();
	}


	private void checkTokenExist(){
        String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
        String authString = "getToken"+MobileNumberstr+SPreference.getWalletType(getActivity());;
        String params = "act=getToken&mobileNumber="
                + MobileNumberstr+"&paymentMethod="+SPreference.getWalletType(getActivity())+ "&auth="
                + GlobalMethods.calculateCMCAuthString(authString);

        new GlobalAsyncTask(getActivity(), endpoint, params,
                null, CabsFragment.this, true, "getToken",
                false);
    }

	public class GetOlaBookingStatusAsync extends
			AsyncTask<String, Void, String> {

		String response = "";

		@Override
		protected void onPreExecute() {
			try{
              showProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetOlaBookingStatusAsync", "GetOlaBookingStatusAsync : "
					+ args[0].toString());

			try {
				URL url = new URL(args[0].toString());

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				// OutputStream outputStream = urlConnection.getOutputStream();
				// BufferedWriter bufferedWriter = new BufferedWriter(new
				// OutputStreamWriter(outputStream, "UTF-8"));
				// bufferedWriter.write(params);
				// bufferedWriter.flush();
				// bufferedWriter.close();
				// outputStream.close();

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
					Log.d("getOlaBookingStatus",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					return response;
				}

				Log.d("getOlaBookingStatus", "getOlaBookingStatus response : "
						+ response);

				if (response.toLowerCase().contains("crn")) {

					try {
						JSONObject responseObject = new JSONObject(response);
						if (responseObject.length() > 0) {

							String driverName = "", driverPhone = "", eta = "", vehicleMake = "", vehicleLicense = "", requestID = "";

							driverName = responseObject.get("driver_name")
									.toString();
							driverPhone = responseObject.get("driver_number")
									.toString();
							eta = responseObject.get("eta").toString();
							vehicleMake = responseObject.get("car_model")
									.toString();
							vehicleLicense = responseObject.get("cab_number")
									.toString();
							requestID = responseObject.get("crn").toString();

							if (rideObject != null) {
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							} else {
								rideObject = new RideObject();
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							}

							final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, etaFinal = eta, vehicleMakeFinal = vehicleMake, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;
							final String requestIDInTable = args[1].toString();
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("Success");
									builder.setCancelable(false);
									builder.setMessage("Cab booked succesfully!\r\n"
											+ "Driver : "
											+ driverNameFinal
											+ " ("
											+ driverPhoneFinal
											+ ")\r\n"
											+ "Vehicle : "
											+ vehicleMakeFinal
											+ " ("
											+ vehicleLicenseFinal
											+ ")\r\n"
											+ "Est. Time : "
											+ etaFinal + " mins");
									builder.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													try {
														updateCMCRecords(
																mCabSearchArray
																		.getJSONObject(
																				cabBookingPosition)
																		.get("CabNameID")
																		.toString()
																		+ "~"
																		+ requestIDInTable,
																mCabSearchArray
																		.getJSONObject(
																				cabBookingPosition)
																		.get("CarType")
																		.toString(),
																"1", fAddress,
																tAddress, "",
																requestIDFinal,
																false, false,
																"", "", "",
																true);
													} catch (Exception e) {
														e.printStackTrace();
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

						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(
											getActivity(),
											"Something went wrong, please try again",
											Toast.LENGTH_LONG).show();
								}
							});
						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										getActivity(),
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} else {

					try {
						JSONObject responseObject = new JSONObject(response);
						Log.d("getOlaBookingStatus", "responseObject : "
								+ responseObject.toString());
						if (responseObject.length() > 0) {
							String dialogTitle = "", dialogMessage = "";

							// Log.d("getOlaBookingStatus", "errors : "
							// + (new
							// JSONArray(responseObject.get("errors").toString())));

							dialogTitle = responseObject.get("message")
									.toString();
							dialogMessage = responseObject.get("code")
									.toString();

							final String finalDialogTitle = dialogTitle;
							final String finalDialogMessage = dialogMessage;
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle(finalDialogTitle);
									builder.setMessage("Cab could not be booked because : "
											+ finalDialogMessage);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});

						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(
											getActivity(),
											"Something went wrong, please try again",
											Toast.LENGTH_LONG).show();
								}
							});
						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										getActivity(),
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try{
               hideProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }
		}
	}

	public class GetUberAccessTokenAsync extends
			AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			try{
				showProgressBar();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetUberAccessTokenAsync", "GetUberAccessTokenAsync : "
					+ args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cabbookrequeststatus.php?requestid="
						+ args[0].toString());
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				// OutputStream outputStream = urlConnection.getOutputStream();
				// BufferedWriter bufferedWriter = new BufferedWriter(new
				// OutputStreamWriter(outputStream, "UTF-8"));
				// bufferedWriter.write("requestid=" + args[0].toString());
				// bufferedWriter.flush();
				// bufferedWriter.close();
				// outputStream.close();

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
					Log.d("GetUberAccessTokenAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("GetUberAccessTokenAsync",
						"GetUberAccessTokenAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {
				try {
					//JSONArray jsonArray = new JSONArray(result);
					//for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = new JSONObject(result);
						Log.d("GetUberAccessTokenAsync",
								"GetUberAccessTokenAsync jsonArray : "
										+ jsonObject.get("access_token"));
						String paramString = "type=bookuber"
								+ mUberBookingInputParams + "&accesstoken="
								+ jsonObject.get("access_token").toString();
						getUberBookingStatus(paramString, args[0].toString());
					//}
					// Log.d("GetUberAccessTokenAsync",
					// "GetUberAccessTokenAsync access_token : " + jsonArray);

				} catch (Exception e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getActivity(),
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
                hideProgressBar();
            }catch (Exception e){
                e.printStackTrace();
            }

		}
	}

	private void getUberBookingStatus(String params,
									  final String requestIDInTable) {

		try {
			URL url = new URL(GlobalVariables.ServiceUrl + "/uberConnect.php?"
					+ params);
			String response = "";
			Log.d("BookaCab", "getUberBookingStatus : "
					+ GlobalVariables.ServiceUrl + "/uberConnect.php?" + params);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setReadTimeout(30000);
			urlConnection.setConnectTimeout(30000);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			// OutputStream outputStream = urlConnection.getOutputStream();
			// BufferedWriter bufferedWriter = new BufferedWriter(new
			// OutputStreamWriter(outputStream, "UTF-8"));
			// bufferedWriter.write(params);
			// bufferedWriter.flush();
			// bufferedWriter.close();
			// outputStream.close();

			int responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				String line = "";
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					response += line;
				}

			} else {
				response = "";
				Log.d("getUberBookingStatus",
						"responseCode != HttpsURLConnection.HTTP_OK : "
								+ responseCode);
				return;
			}

			Log.d("getUberBookingStatus", "getUberBookingStatus response : "
					+ response);

			if (!response.toLowerCase().contains("error")) {

				try {
					JSONObject responseObject = new JSONObject(response);
					if (responseObject.length() > 0) {

						final String status = responseObject.get("status")
								.toString();
						if (status.equalsIgnoreCase("accepted")) {
							String driverName = "", driverPhone = "", eta = "", vehicleMake = "", vehicleLicense = "", requestID = "";
							try {
								JSONObject jsonObject2 = new JSONObject(
										responseObject.get("driver").toString());
								driverName = jsonObject2.get("name").toString();
								driverPhone = jsonObject2.get("phone_number")
										.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								JSONObject jsonObject2 = new JSONObject(
										responseObject.get("vehicle")
												.toString());
								vehicleMake = jsonObject2.get("make")
										.toString();
								vehicleLicense = jsonObject2.get(
										"license_plate").toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								eta = responseObject.get("eta").toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								requestID = responseObject.get("request_id")
										.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (rideObject != null) {
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							} else {
								rideObject = new RideObject();
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							}

							final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, etaFinal = eta, vehicleMakeFinal = vehicleMake, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
                                            getActivity());
									builder.setTitle("Success");
									builder.setCancelable(false);
									builder.setMessage("Cab booked succesfully!\r\n"
											+ "Driver : "
											+ driverNameFinal
											+ " ("
											+ driverPhoneFinal
											+ ")\r\n"
											+ "Vehicle : "
											+ vehicleMakeFinal
											+ " ("
											+ vehicleLicenseFinal
											+ ")\r\n"
											+ "Est. Time : "
											+ etaFinal + " mins");
									builder.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													try {
														updateCMCRecords(
																"1"
																		+ "~"
																		+ requestIDInTable,
																mCabSearchArray
																		.getJSONObject(
																				cabBookingPosition)
																		.get("CarType")
																		.toString(),
																"1", fAddress,
																tAddress, "",
																requestIDFinal,
																false, false,
																"", "", "",
																true);
													} catch (Exception e) {
														e.printStackTrace();
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
						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("Cab could not be booked");
									builder.setMessage(status);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}

					} else {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										getActivity(),
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getActivity(),
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {

				try {
					JSONObject responseObject = new JSONObject(response);
					Log.d("getUberBookingStatus", "responseObject : "
							+ responseObject.toString());
					if (responseObject.length() > 0) {
						String dialogTitle = "", dialogMessage = "";

						// Log.d("getUberBookingStatus", "errors : "
						// + (new
						// JSONArray(responseObject.get("errors").toString())));
						JSONArray jsonArray = new JSONArray(responseObject.get(
								"errors").toString());
						JSONObject jsonObject = new JSONObject(jsonArray.get(0)
								.toString());

						dialogTitle = jsonObject.get("code").toString();
						dialogMessage = jsonObject.get("title").toString();

						// Iterator<String> iter = responseObject.keys();
						// while (iter.hasNext()) {
						// String key = iter.next();
						// Log.d("getUberBookingStatus", "values : "
						// + responseObject.get(key));
						//
						// String[] strings = responseObject.get("errors")
						// .toString().split(",");
						//
						// for (int i = 0; i < strings.length; i++) {
						// Log.d("getUberBookingStatus", strings[i]);
						// if (strings[i].contains("code")) {
						// String[] tempStrings = strings[i]
						// .split(":");
						// dialogTitle = tempStrings[1]
						// .replaceAll("\"", "")
						// .replaceAll("\\}", "")
						// .replaceAll("]", "");
						// }
						// if (strings[i].contains("title")) {
						// String[] tempStrings = strings[i]
						// .split(":");
						// dialogMessage = tempStrings[1]
						// .replaceAll("\"", "")
						// .replaceAll("\\}", "")
						// .replaceAll("]", "");
						// }
						// }
						// }

						final String finalDialogTitle = dialogTitle;
						final String finalDialogMessage = dialogMessage;
                        getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
                                        getActivity());
								builder.setTitle(finalDialogTitle);
								builder.setMessage(finalDialogMessage);
								builder.setPositiveButton("OK", null);
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();
							}
						});

					} else {
                        getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
                                        getActivity(),
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getActivity(),
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getActivity(),
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			});
		}

	}

	private void locationHandler(LatLng latLng, LocationModelListener listener){
		//if (isOnline()) {
		LocationModel locationModel = new LocationModel();
		locationModel.setLocationByAddress(false);
		locationModel.setLatLng(latLng);
		locationModel.setLocationByAddress(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new GetLocationTaskHandler(getActivity(), listener,locationModel )
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new GetLocationTaskHandler(getActivity(), listener,locationModel).execute();
		}
		// }
	}

	private LocationModelListener locationModelListener1 = new LocationModelListener() {

		@Override
		public void getAddress(Address address) {
            mapAddress = address;

		}

		@Override
		public void getLatLong(LatLng latLng) {

		}

		@Override
		public void getStringAddress(String address) {
            fromlocation.setText(address);
		}

		@Override
		public void getError(String error) {
            fromlocation.setText(error);		}

		@Override
		public void isLoading(boolean isLoading) {
			if(isLoading){
				fromlocation.setVisibility(View.GONE);
				getView().findViewById(R.id.dot_progress_bar).setVisibility(View.VISIBLE);
			}else {
                fromlocation.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.dot_progress_bar).setVisibility(View.GONE);
			}
		}


	};

    private LocationModelListener locationModelListener2 = new LocationModelListener() {

        @Override
        public void getAddress(Address address) {
            fAddress = null; // reset previous

            fAddress = address;
            if (fAddress == null) {
                etFromPlace.setText("");
                isLocationNotUpdated = false;
							/*	Toast.makeText(
										getActivity(),
										"Could not locate the address, please try using the map or a different address",
										Toast.LENGTH_LONG).show();
				*/

            } else {
                isLocationNotUpdated = false;
                addressModelFrom = new AddressModel();
                addressModelFrom.setAddress(fAddress);
                fromshortname = MapUtilityMethods.getAddressshort(
                        getActivity(), fAddress.getLatitude(),
                        fAddress.getLongitude());
                addressModelFrom.setShortname(fromshortname);
                addressModelFrom.setLongname(etFromPlace.getText()
                        .toString());
            }
        }

        @Override
        public void getLatLong(LatLng latLng) {

        }

        @Override
        public void getStringAddress(String address) {

            etFromPlace.setText(address);
        }

        @Override
        public void getError(String error) {
        }

        @Override
        public void isLoading(boolean isLoading) {

        }


    };

	private void showProgressBar(){
		try{
			onedialog = new Dialog(getActivity());
			onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onedialog.setContentView(R.layout.dialog_ishare_loader);
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
    private Address getCodeAddressByLatLong(LatLng locationModel){
        Geocoder geo = new Geocoder(getActivity());
        Address _address = null;
        List<Address> listAddresses = null;
        int count = 0;

        while (listAddresses == null && count <2){
            count++;
            try {
                if (locationModel != null) {
                    listAddresses = geo.getFromLocation(locationModel.latitude,
                            locationModel.longitude, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        if ((listAddresses != null) && (listAddresses.size() > 0)) {
            _address = listAddresses.get(0);

        }
        return  _address;
    }
}