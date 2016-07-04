package com.clubmycab.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CabApplication;
import com.clubmycab.R;
import com.clubmycab.adapter.PlaceArrayAdapter;
import com.clubmycab.adapter.PrivateRidesAdapter;
import com.clubmycab.adapter.PublicRidesAdapter;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.interfaces.CurrentLocatListenerRidesAvail;
import com.clubmycab.model.PrivateRide;
import com.clubmycab.model.PublicRide;
import com.clubmycab.model.Ride;
import com.clubmycab.ui.ChoosePaymentTypeScreen;
import com.clubmycab.ui.FirstLoginWalletsActivity;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.NewRideCreationScreen;
import com.clubmycab.ui.VehicleDetailScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.clubmycab.utility.Utility;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RidesAvail extends Fragment implements OnClickListener, LocationListener,CurrentLocatListenerRidesAvail, GlobalAsyncTask.AsyncTaskResultListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks  {
	private static RidesAvail fragment;
	private ListView lvPrivateGrpRides, lvPublicGrpRides, lvAllRides;
	private RidesAvailAdapter ridesAdapter;
	private PrivateRidesAdapter privateRidesAdapter;
	private PublicRidesAdapter publicRidesAdapter;
	private ArrayList<PublicRide> ridesList = new ArrayList<PublicRide>();
	private ArrayList<Integer> ridesFareList = new ArrayList<Integer>();

	private ArrayList<Ride> ridesListPublic = new ArrayList<Ride>();
	private ArrayList<PrivateRide> ridesListPrivate = new ArrayList<PrivateRide>();
	private String MobileNumber;
	private TextView tvFromTo;
	private String sLocation;
	private LocationManager locationManager;
	private static final int LOCATION_REQUEST = 101;
	private boolean isFirstLaunchDone;
	private String allRidesResponse;
	private Tracker tracker;
	public  static final int SELECT_PAYMENT_REQUEST =  1001;
	private Dialog searchRideDialog;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter1, mPlaceArrayAdapter2;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final String LOG_TAG = "MainActivity";
    private Address fromSearchAddress;
	private String shortName;
    private Address toSearchAddress;
    private String fromSLocation;
    private String toELocation;
	private Dialog onedialog;
    private AutoCompleteTextView mFromLocation, mToLocation;
    private ViewGroup header;
    private String startSearchAddress, endSearchAddress;

    public static RidesAvail newInstance(Bundle args) {
		//if(fragment == null){
			fragment = new RidesAvail();
		//}
		fragment.setArguments(args);
		return fragment;
	}
	
	public static RidesAvail getInstance(){
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
		MobileNumber = mPrefs.getString("MobileNumber", "");
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("HomeScreen");
		tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Opened").setLabel("Rides Available Screen").build());

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		 ((NewHomeScreen)getActivity()).listenerRideAvailLoc = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.rides_avail2, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		// view.findViewById(R.id.flExpandPri).setOnClickListener(this);
		isFirstLaunchDone = false;
		view.findViewById(R.id.llCollapsePriRide).setOnClickListener(this);
		view.findViewById(R.id.llCollapsePubRide).setOnClickListener(this);
        view.findViewById(R.id.cardSerchView).setOnClickListener(this);

        view.findViewById(R.id.carpoolll).setOnClickListener(this);
		((TextView)view.findViewById(R.id.tvNoRideAvlble)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		((TextView)view.findViewById(R.id.tvOfferARide)).setTypeface(FontTypeface.getTypeface(getActivity(),AppConstants.HELVITICA));
		tvFromTo = (TextView) view.findViewById(R.id.tvFromToRides);
		tvFromTo.setTypeface(FontTypeface.getTypeface(getActivity(),
				AppConstants.HELVITICA));
		((TextView) view.findViewById(R.id.tvPriRideLable))
				.setTypeface(FontTypeface.getTypeface(getActivity(),
						AppConstants.HELVITICA));

		lvPrivateGrpRides = (ListView) view
				.findViewById(R.id.lvPrivateGrpRides);
		lvPublicGrpRides = (ListView) view.findViewById(R.id.lvPublicGrpRides);
		lvAllRides = (ListView) view.findViewById(R.id.lvAllRides);
		getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		publicRidesAdapter = new PublicRidesAdapter();
		publicRidesAdapter.init(getActivity(), ridesListPublic, tracker);
		lvPublicGrpRides.setAdapter(publicRidesAdapter);

		privateRidesAdapter = new PrivateRidesAdapter();
		privateRidesAdapter.init(getActivity(), ridesListPrivate);
		lvPrivateGrpRides.setAdapter(privateRidesAdapter);

        ridesAdapter = new RidesAvailAdapter();
        ridesAdapter.init(getActivity(), ridesList,ridesFareList, ridesListPrivate);
       /* LayoutInflater inflater = getActivity().getLayoutInflater();
        header = (ViewGroup) inflater.inflate(
                R.layout.header_private_groups_x, lvAllRides, false);
        header.setVisibility(View.GONE);
        lvAllRides.addHeaderView(header);
   */     lvAllRides.setAdapter(ridesAdapter);
		if(CabApplication.getInstance().getFirstLocation() != null){
			getLatLong(CabApplication.getInstance().getFirstLocation());
		}
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
		mGoogleApiClient.connect();
		mPlaceArrayAdapter1 = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
				null, null);
		mPlaceArrayAdapter2 = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
				null, null);


	}



	private void notifyAdpater(ArrayList<PublicRide> arrayList) {
		if (ridesAdapter != null) {
			ridesAdapter.init(getActivity(), arrayList,ridesFareList, ridesListPrivate);
			ridesAdapter.notifyDataSetChanged();
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
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.llCollapsePriRide:
			collapseRideList();
			break;

		case R.id.llCollapsePubRide:
			collapseRideList();
			break;

		case R.id.flExpandPri:
			expandPriGrpRides();
			break;
			
		case R.id.cardPrivate:
			expandPriGrpRides();
			break;
        case R.id.cardSerchView:
			collapseRideList();
            fetchAllRides();
            getView().findViewById(R.id.cardSerchView).setVisibility(View.GONE);
            break;


		case R.id.carpoolll:
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Offer a ride")
                    .setAction("Offer a ride pressed")
                    .setLabel("Offer a ride").build());
           /* if(!isWalletLinked()){
                showWallletDialog();
                return;
            }
            SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
            if(mPrefs.getBoolean(AppConstants.IS_VEHICLE_ADDED, false)){
                Intent mainIntent = new Intent(getActivity(), NewRideCreationScreen.class);
                mainIntent.putExtra("screentoopen",
						NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
                startActivity(mainIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }else {
                Intent mainIntent = new Intent(getActivity(), VehicleDetailScreen.class);
                mainIntent.putExtra("screentoopen",
						NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
                startActivity(mainIntent);
           }*/

			// Second Phase
			//checkTokenExist();

			//Third Phase
			if(SPreference.getPref(getActivity()).getInt(SPreference.USER_TYPE, AppConstants.USER_TYPE_NORMAL) == AppConstants.USER_TYPE_NORMAL){
				if(SPreference.getRideOfferWalletType(getActivity()) == AppConstants.NULL){
                   // showChooseWalletForPayment();
					Intent intent = new Intent(getActivity(), ChoosePaymentTypeScreen.class);
					intent.putExtra("fromrideoffer",true);
					startActivityForResult(intent,SELECT_PAYMENT_REQUEST);
				}else {
					checkTokenExist();
				}
			}else {// for user type system
				if(SPreference.getPref(getActivity()).getBoolean(AppConstants.IS_VEHICLE_ADDED, false)){
					Intent mainIntent = new Intent(getActivity(), NewRideCreationScreen.class);
					mainIntent.putExtra("screentoopen", NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
					//   startActivityForResult(mainIntent, NewHomeScreen.OFFER_RIDE_REQUEST);
					startActivity(mainIntent);
					getActivity().overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}else {
					Intent mainIntent = new Intent(getActivity(), VehicleDetailScreen.class);
					mainIntent.putExtra("screentoopen", NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
					startActivity(mainIntent);
				}
			}

			break;

		default:
			break;
		}
	}

	private void expandPubGrpRides(int index) {
       // ((NewHomeScreen)getActivity()).hideSearch();

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Open public rides available card")
                .setAction("Open public rides available card")
                .setLabel("Open public rides available card").build());
		getView().findViewById(R.id.llRideTypes).setVisibility(View.GONE);
		getView().findViewById(R.id.llPrivateGrpRides).setVisibility(View.GONE);
		getView().findViewById(R.id.llPublicGrpRides).setVisibility(
				View.VISIBLE);
		if(ridesList.get(index).getName() != null)
			tvFromTo.setVisibility(View.VISIBLE);
			tvFromTo.setText(ridesList.get(index).getName());

		if(publicRidesAdapter != null){
			publicRidesAdapter.init(getActivity(), (ArrayList<Ride>) ridesList.get(index).getRides(), tracker);
			publicRidesAdapter.notifyDataSetChanged();
		}

	}

	private void expandPriGrpRides() {
       // ((NewHomeScreen)getActivity()).hideSearch();

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Open private rides available card")
                .setAction("Open private rides available card")
                .setLabel("Open private rides available card").build());

        getView().findViewById(R.id.llRideTypes).setVisibility(View.GONE);
		getView().findViewById(R.id.llPrivateGrpRides).setVisibility(
				View.VISIBLE);
		getView().findViewById(R.id.llPublicGrpRides).setVisibility(View.GONE);
		if(privateRidesAdapter != null){
			privateRidesAdapter.init(getActivity(), ridesListPrivate);
			privateRidesAdapter.notifyDataSetChanged();
		}
	}

	public void collapseRideList() {
       // ((NewHomeScreen)getActivity()).showSearch();
        getView().findViewById(R.id.llRideTypes).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.llPrivateGrpRides).setVisibility(View.GONE);
		getView().findViewById(R.id.llPublicGrpRides).setVisibility(View.GONE);
	}

	@Override
	public void getResult(String response, String uniqueID) {
		if(uniqueID.equalsIgnoreCase("getToken")){
			try{
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("success")) {
					if(!TextUtils.isEmpty(jsonObject.getString("token"))){
						SharedPreferences mPrefs = getActivity().getSharedPreferences("FacebookData", 0);
						if(mPrefs.getBoolean(AppConstants.IS_VEHICLE_ADDED, false)){
							Intent mainIntent = new Intent(getActivity(), NewRideCreationScreen.class);
							mainIntent.putExtra("screentoopen",
									NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
							//   startActivityForResult(mainIntent, NewHomeScreen.OFFER_RIDE_REQUEST);
							startActivity(mainIntent);
							getActivity().overridePendingTransition(R.anim.slide_in_right,
									R.anim.slide_out_left);
						}else {
							Intent mainIntent = new Intent(getActivity(), VehicleDetailScreen.class);
							mainIntent.putExtra("screentoopen", NewRideCreationScreen.HOME_ACTIVITY_CAR_POOL);
							startActivity(mainIntent);
						}
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mPlaceArrayAdapter1 != null)
		    mPlaceArrayAdapter1.setGoogleApiClient(mGoogleApiClient);
        if(mPlaceArrayAdapter2 != null)
		    mPlaceArrayAdapter2.setGoogleApiClient(mGoogleApiClient);

	}

    @Override
    public void onConnectionSuspended(int i) {
        if(mPlaceArrayAdapter1 != null)
            mPlaceArrayAdapter1.setGoogleApiClient(null);
        if(mPlaceArrayAdapter2 != null)
            mPlaceArrayAdapter2.setGoogleApiClient(null);

	}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showSearchDialog() {
        if(searchRideDialog != null && searchRideDialog.isShowing()){
            searchRideDialog.hide();
        }else {
            showSearchRideDialog();
        }
    }

    public class RidesAvailAdapter extends BaseAdapter {
		private ArrayList<PublicRide> arrayList, arrayListPrivate;
		private Context context;
		private LayoutInflater inflater;
		private ViewHolder holder;
		private ArrayList<Integer> rFareList;
		private ArrayList<PrivateRide> rPtivateList;
        private int offset = 0;

		public void init(Context context, ArrayList<PublicRide> arrayList, ArrayList<Integer> rFareList, ArrayList<PrivateRide> rPtivateList) {
			this.arrayList = arrayList;
			this.rFareList = rFareList;
			this.context = context;
			inflater = LayoutInflater.from(this.context);
			this.rPtivateList = rPtivateList;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (arrayList != null && arrayList.size() > 0){
				count = arrayList.size();
			}
			if (rPtivateList != null && rPtivateList.size() > 0){
                offset = 1;
				count = count+1;
			}
			return count;
		}

		public Object getItem(int position) {
			// return arrayList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return arrayList.indexOf(getItem(position));
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_rides_avail_x,
						parent, false);
				holder = new ViewHolder(convertView);
				// holder.txtDesc = (TextView)
				// convertView.findViewById(R.id.desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if(position == 0 && rPtivateList != null && rPtivateList.size()>0){
                holder.cardPrivate.setVisibility(View.VISIBLE);
                holder.cardPublic.setVisibility(View.GONE);

                holder.tvRideCountPrivate.setText(String.valueOf(rPtivateList.size()));
                holder.cardPrivate.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expandPriGrpRides();
                    }
                });


            }else {
                holder.cardPrivate.setVisibility(View.GONE);
                holder.cardPublic.setVisibility(View.VISIBLE);
                convertView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        expandPubGrpRides(position-offset);
                        tracker.send(new HitBuilders.EventBuilder().setCategory("Screen").setAction("Click").setLabel("Ride Group Details").build());


                    }
                });


                if(arrayList.get(position-offset).getRides() != null && arrayList.get(position-offset).getRides().size()>0){
                    holder.tvTotalRideCount.setText(String.valueOf(arrayList.get(position-offset).getRides().size()));
                    String arr[] = arrayList.get(position-offset).getName().trim().split(" to ");
                    String from = arr[0].trim();
                    String to = arr[1].trim();

                    holder.tvFromPlace.setText(from);
                    holder.tvToPlace.setText(to);
                }
                holder.tvFareCount.setText(String.valueOf("Rs."+rFareList.get(position-offset)));
            }
			return convertView;
		}

		/* private view holder class */
		/*private class ViewHolder {
			private TextView tvRideOwnerName, tvFromToLocation, seatstext,
					tvRemainingSeats, datetext, timetext;
			private FrameLayout flHideGrp, flExpandGrp;

			public ViewHolder(View view) {
				tvRideOwnerName = (TextView) view
						.findViewById(R.id.myridesbannerusername);
				tvFromToLocation = (TextView) view
						.findViewById(R.id.tvFromToLocation);
				seatstext = (TextView) view.findViewById(R.id.seatstext);
				tvRemainingSeats = (TextView) view.findViewById(R.id.tvAvSeats);
				datetext = (TextView) view.findViewById(R.id.datetext);
				timetext = (TextView) view.findViewById(R.id.timetext);
				flHideGrp = (FrameLayout) view.findViewById(R.id.flHide);
				flExpandGrp = (FrameLayout) view.findViewById(R.id.flExpand);

				tvRideOwnerName.setTypeface(FontTypeface.getTypeface(context,
						AppConstants.HELVITICA));
				timetext.setTypeface(FontTypeface.getTypeface(context,
						AppConstants.HELVITICA));
				datetext.setTypeface(FontTypeface.getTypeface(context,
								AppConstants.HELVITICA));
				tvFromToLocation.setTypeface(FontTypeface.getTypeface(context,
						AppConstants.HELVITICA));
				seatstext.setTypeface(FontTypeface.getTypeface(context,
						AppConstants.HELVITICA));
				tvRemainingSeats.setTypeface(FontTypeface.getTypeface(context,
						AppConstants.HELVITICA));

			}
		}*/
		private class ViewHolder {
			private TextView tvRideCountPrivate, tvFromPlace, tvFromCity, tvToPlace, tvToCity, tvTotalRide, tvTotalRideCount,tvFare, tvFareCount;
			private CardView cardPrivate, cardPublic;
			public ViewHolder(View view){
				tvFromPlace = (TextView)view.findViewById(R.id.tvPlaceFrom);
				tvFromCity = (TextView)view.findViewById(R.id.tvCityFrom);
				tvToPlace = (TextView)view.findViewById(R.id.tvPlaceTo);
				tvToCity = (TextView)view.findViewById(R.id.tvCityTo);
				tvTotalRide = (TextView)view.findViewById(R.id.tvTotalRideLable);
				tvTotalRideCount = (TextView)view.findViewById(R.id.tvTotalCount);
				tvFare = (TextView)view.findViewById(R.id.tvFare);
				tvFareCount = (TextView)view.findViewById(R.id.tvFareCount);
				cardPrivate = (CardView)view.findViewById(R.id.card_viewPrivate);
                cardPublic = (CardView)view.findViewById(R.id.card_view2);

				tvFromPlace.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvFromCity.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvToPlace.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvToCity.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvTotalRide.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvTotalRideCount.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvFare.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
				tvFareCount.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));

                ((TextView) view.findViewById(R.id.tvTotalRideLablePrivate)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
                ((TextView) view.findViewById(R.id.tvTotalCountPrivate)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
                ((TextView) view.findViewById(R.id.tvHeadingRideGrp)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));

                tvRideCountPrivate = (TextView) view.findViewById(R.id.tvTotalCountPrivate);
               /* header.findViewById(R.id.card_view2).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/


            }
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == LOCATION_REQUEST){
				getLocation();
		}if(resultCode == getActivity().RESULT_OK){
			if(requestCode ==  SELECT_PAYMENT_REQUEST && data != null){
				try{
					int paymentType = data.getExtras().getInt("type");
					SPreference.getPref(getActivity()).edit().putInt(SPreference.RIDE_OFFER_WALLET_TYPE,paymentType).commit();
                    SPreference.getPref(getActivity()).edit().putInt(SPreference.RIDE_TAKER_WALLET_TYPE,paymentType).commit();
                    SPreference.getPref(getActivity()).edit().putInt(SPreference.SELECTED_PAY_TYPE,paymentType).commit();

                    checkTokenExist();
				}catch (NullPointerException e){

				}
			}
		}

	}

	/***
	 * ------------------------Fetch Ride
	 * List----------------------------------> Fetch All Rides Parse Data For
	 * all rides Put Data for All Rides in allRides List Put data of Private
	 * Rides in Private Ride Group Put Public Rides in Public Rides Group Notify
	 * All Rides List getRidesForGroup- When clicked on expand of particular
	 * public group
	 * 
	 */
	/**
	 * This will fetch all public and private group rides
	 */
	private void fetchAllRides() {
        //((NewHomeScreen)getActivity()).showSearch();
        if(getView() == null)
			return;
		ridesList.clear();
		ridesListPrivate.clear();
		ridesListPublic.clear();
        allRidesResponse = "";
        if(ridesAdapter != null){
            ridesAdapter.init(getActivity(),ridesList, ridesFareList, ridesListPrivate);
            ridesAdapter.notifyDataSetChanged();
        }
		getView().findViewById(R.id.llErrorMsg).setVisibility(View.GONE);
		getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

		if (isOnline()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskAllRidesRequest()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskAllRidesRequest().execute();
			}
		}
	}

	private void fetchSearchedRides() {
		if(getView() == null)
			return;


		if (isOnline()) {
            ridesList.clear();
            ridesListPrivate.clear();
            ridesListPublic.clear();
            allRidesResponse = "";
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskAllRidesFromToRequest()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskAllRidesFromToRequest().execute();
			}
		}else {
            CustomDialog.showDialog(getActivity(), "Please check internet connection");
        }
	}

	private void showProgressBar(){
		try{
			onedialog = new Dialog(getActivity());
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

	private class ConnectionTaskAllRidesRequest extends
			AsyncTask<String, Void, Void> {

		private boolean exceptioncheck;

		@Override
		protected void onPreExecute() {
			Log.d("", "");
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateAllRidesRequest mAuth1 = new AuthenticateAllRidesRequest();
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
				if(getView() != null)
					getView().findViewById(R.id.progressBar).setVisibility(View.GONE);

				if(TextUtils.isEmpty(allRidesResponse)){
					return;
				}

				try{
					collapseRideList();
					if((ridesList != null && ridesList.size() > 0) || (ridesListPrivate != null && ridesListPrivate.size()>0)){
						getView().findViewById(R.id.llErrorMsg).setVisibility(View.GONE);
						populateDataInList();
					}else{
						getView().findViewById(R.id.llErrorMsg).setVisibility(View.VISIBLE);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateAllRidesRequest {


		public AuthenticateAllRidesRequest() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitationsNew.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);
			BasicNameValuePair sLocationNameValuePair = new BasicNameValuePair(
					"sLatLon", sLocation);
			String authString = MobileNumber+sLocation;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(sLocationNameValuePair);
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
				allRidesResponse = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("allRidesResponse",
					"" + stringBuilder.toString() + " mobileNumber : "
							+ MobileNumber);

			try {

				if(TextUtils.isEmpty(allRidesResponse)){
					return;
				}
				Log.d("AllRides", allRidesResponse);
				JSONObject obj = new JSONObject(allRidesResponse);
				if(obj == null)
					return;
				if (!obj.isNull("status") && obj.optString("status").equalsIgnoreCase("success")) {
					if(obj.isNull("data")){
						return;
					}
					JSONObject obj2 = obj.getJSONObject("data");
					Gson gson = new Gson();

					ridesList = gson.fromJson(
							obj2.getJSONArray("publicRides").toString(),
							new TypeToken<ArrayList<PublicRide>>() {
							}.getType());
					calculateMinimumFare(ridesList);
					Log.d("public rides", ridesList.toString());

					/*if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							//ridesList.add(arrayRideLocal.get(i));
						}
					}*/
				}

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			try {
				JSONObject obj = new JSONObject(allRidesResponse);
				if(obj == null)
					return;
				if (!obj.isNull("status") && obj.optString("status").equalsIgnoreCase("success")) {
					if(obj.isNull("data")){
						return;
					}
					JSONObject obj2 = obj.getJSONObject("data");
					Gson gson = new Gson();
					ridesListPrivate = gson.fromJson(
							obj2.getJSONArray("privateRides").toString(),
							new TypeToken<ArrayList<PrivateRide>>() {
							}.getType());
					Log.d("private rides", ridesListPrivate.toString());


				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

    private class ConnectionTaskAllRidesFromToRequest extends
            AsyncTask<String, Void, Void> {

        private boolean exceptioncheck;

        @Override
        protected void onPreExecute() {
            showProgressBar();
			Log.d("", "");
        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateAllRidesRequestFromTo mAuth1 = new AuthenticateAllRidesRequestFromTo();
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
               hideProgressBar();

                if(TextUtils.isEmpty(allRidesResponse)){
                    return;
                }

                try{
					if((ridesList != null && ridesList.size() > 0) || (ridesListPrivate != null && ridesListPrivate.size()>0)){
						getView().findViewById(R.id.llErrorMsg).setVisibility(View.GONE);
                        getView().findViewById(R.id.cardSerchView).setVisibility(View.VISIBLE);
                        populateDataInList();
                    }else{
                        getView().findViewById(R.id.llErrorMsg).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.cardSerchView).setVisibility(View.GONE);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public class AuthenticateAllRidesRequestFromTo {


        public AuthenticateAllRidesRequestFromTo() {

        }

        public void connection() throws Exception {

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select11 = GlobalVariables.ServiceUrl
                    + "/rideInvitationFromTo.php";
            HttpPost httpPost = new HttpPost(url_select11);
            BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
                    "mobileNumber", MobileNumber);
            BasicNameValuePair sLocationNameValuePair = new BasicNameValuePair(
                    "sLatLon", fromSLocation);
             BasicNameValuePair eLocationNameValuePair = new BasicNameValuePair(
                    "eLatLon", toELocation);
            BasicNameValuePair startAddressNameValuePair = new BasicNameValuePair(
                    "startLocation", startSearchAddress);
            BasicNameValuePair endAddressNameValuePair = new BasicNameValuePair(
                    "endLocation", endSearchAddress);

            String authString = toELocation+endSearchAddress+MobileNumber+sLocation+startSearchAddress;
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(MobileNumberBasicNameValuePair);
            nameValuePairList.add(sLocationNameValuePair);
			nameValuePairList.add(eLocationNameValuePair);
            nameValuePairList.add(startAddressNameValuePair);
            nameValuePairList.add(endAddressNameValuePair);

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
                allRidesResponse = stringBuilder.append(
                        bufferedStrChunk).toString();
            }

            Log.d("allRidesResponse",
                    "" + stringBuilder.toString() + " mobileNumber : "
                            + MobileNumber);

            try {

                if(TextUtils.isEmpty(allRidesResponse)){
                    return;
                }
                Log.d("AllRides", allRidesResponse);
                JSONObject obj = new JSONObject(allRidesResponse);
                if(obj == null)
                    return;
                if (!obj.isNull("status") && obj.optString("status").equalsIgnoreCase("success")) {
                    if(obj.isNull("data")){
                        return;
                    }
                    JSONObject obj2 = obj.getJSONObject("data");
                    Gson gson = new Gson();

                    ridesList = gson.fromJson(
                            obj2.getJSONArray("publicRides").toString(),
                            new TypeToken<ArrayList<PublicRide>>() {
                            }.getType());
                    calculateMinimumFare(ridesList);
                    Log.d("public rides", ridesList.toString());

					/*if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							//ridesList.add(arrayRideLocal.get(i));
						}endSearchAddress
					}*/
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                JSONObject obj = new JSONObject(allRidesResponse);
                if(obj == null)
                    return;
                if (!obj.isNull("status") && obj.optString("status").equalsIgnoreCase("success")) {
                    if(obj.isNull("data")){
                        return;
                    }
                    JSONObject obj2 = obj.getJSONObject("data");
                    Gson gson = new Gson();
                    ridesListPrivate = gson.fromJson(
                            obj2.getJSONArray("privateRides").toString(),
                            new TypeToken<ArrayList<PrivateRide>>() {
                            }.getType());
                    Log.d("private rides", ridesListPrivate.toString());


                }

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
	
	private void populateDataInList(){
		/*try{
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewGroup header = (ViewGroup) inflater.inflate(
					R.layout.header_private_groups, lvAllRides, false);
            if(ridesAdapter!=null)
			lvAllRides.removeHeaderView(header);
		}catch(Exception e){
			e.printStackTrace();
		}*/

		if(ridesListPrivate != null && ridesListPrivate.size()>0 ){

            //header.setVisibility(View.VISIBLE);
			/*((TextView) header.findViewById(R.id.tvTotalRideLable)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.tvTotalCount)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.tvHeadingRideGrp)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));

			((TextView) header.findViewById(R.id.tvTotalCount)).setText(String.valueOf(ridesListPrivate.size()));
			header.findViewById(R.id.card_view2).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					expandPriGrpRides();

				}
			});
			*//*header.findViewById(R.id.flExpandPri).setOnClickListener(this);
			((TextView) header.findViewById(R.id.tvRideInPriGrp))
					.setTypeface(FontTypeface.getTypeface(getActivity(),
							AppConstants.HELVITICA));
			header.findViewById(R.id.cardPrivate).setOnClickListener(this);
			((TextView) header.findViewById(R.id.tvFromToLocation)).setTypeface(FontTypeface.getTypeface(getActivity(),
					AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.seatstext)).setTypeface(FontTypeface.getTypeface(getActivity(),
					AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.tvAvSeats)).setTypeface(FontTypeface.getTypeface(getActivity(),
					AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.datetext)).setTypeface(FontTypeface.getTypeface(getActivity(),
					AppConstants.HELVITICA));
			((TextView) header.findViewById(R.id.timetext)).setTypeface(FontTypeface.getTypeface(getActivity(),
					AppConstants.HELVITICA));
			
			if(ridesListPrivate.get(0).getOwnerName() != null){
					StringBuilder builder = new StringBuilder(ridesListPrivate.get(0).getOwnerName()) ;
					if(ridesListPrivate.get(0).getRideType()!=null){
						String rideType = ridesListPrivate.get(0).getRideType();
						if(rideType.equalsIgnoreCase("1") || rideType.equalsIgnoreCase("4")){
							builder.append("(Car Pool)");
						}else if(rideType.equalsIgnoreCase("2") || rideType.equalsIgnoreCase("5")){
							builder.append("(Cab Share)");
						}
					}
				
				((TextView) header.findViewById(R.id.myridesbannerusername)).setText(builder.toString().toUpperCase());
			}
			if(ridesListPrivate.get(0).getFromShortName() != null && ridesListPrivate.get(0).getToShortName() != null)
				((TextView) header.findViewById(R.id.tvFromToLocation)).setText(ridesListPrivate.get(0).getFromShortName()+" - \n"+ridesListPrivate.get(0).getToShortName());
			if(ridesListPrivate.get(0).getTravelDate() != null)
				((TextView) header.findViewById(R.id.datetext)).setText(ridesListPrivate.get(0).getTravelDate());
			if(ridesListPrivate.get(0).getTravelTime() != null)
				((TextView) header.findViewById(R.id.timetext)).setText(ridesListPrivate.get(0).getTravelTime());
			if(ridesListPrivate.get(0).getSeats() != null)
				((TextView) header.findViewById(R.id.seatstext)).setText("Total seats : "+ridesListPrivate.get(0).getSeats());
			if(ridesListPrivate.get(0).getRemainingSeats() != null)
				((TextView) header.findViewById(R.id.tvAvSeats)).setText("Remaining seats : "+ridesListPrivate.get(0).getRemainingSeats());
	*/
			//lvAllRides.addHeaderView(header);
		}else {
          //  header.setVisibility(View.GONE);
        }
		//lvAllRides.setAdapter(ridesAdapter);
        if(ridesAdapter != null ){
            ridesAdapter.init(getActivity(),ridesList, ridesFareList, ridesListPrivate);
            ridesAdapter.notifyDataSetChanged();
        }
	}
	
	/**
	 * Utiliy Method---------------->
	 */
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
		if(location != null){
			if(isFirstLaunchDone){
				sLocation = location.getLatitude()+","+location.getLongitude();
				isFirstLaunchDone = false;
			}
		
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getLatLong(Location location) {
		if(location == null)
			return;
		if(!isFirstLaunchDone){
			sLocation = location.getLatitude()+","+location.getLongitude();
			isFirstLaunchDone = true;
			fetchAllRides();
			Log.d("LatLong", location.getLatitude()+","+location.getLatitude());
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
                    intent.putExtra(AppConstants.ACTIVITYNAME,AppConstants.RIDES_AVAILABLE);
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



	/*
	*Get Ride List
	* loop across ride lis
	* create a dummy list of diatance from ride list
	*
	*
	*
	 */
	private void calculateMinimumFare(ArrayList<PublicRide> arrayList){
		try{
			int minRs = 4;
			int minFare = 10;
			if(arrayList == null)
				return;
			ridesFareList.clear();
			for (int i = 0; i < arrayList.size(); i++) {
				int fare = 0;
				ArrayList<Ride> arralistride = (ArrayList<Ride>) ridesList.get(i).getRides();
				if(arralistride != null && arralistride.size()>0){
					minRs = Integer.parseInt(arralistride.get(0).getPerKmCharge());
					int min = !TextUtils.isEmpty(arralistride.get(0).getDistance()) ?(int)Math.round(Double.parseDouble(arralistride.get(0).getDistance().replace("km","").trim())) : 0;
					for(Ride j: arralistride) {
						if(!TextUtils.isEmpty(j.getDistance()) && (int)Math.round(Double.parseDouble(j.getDistance().replace("km","").trim())) < min){
                            min = (int)Math.round(Double.parseDouble(j.getDistance().replace("km","").trim()));
                        }
					}
					fare = min*minRs;
					fare= (int)(10*(Math.round(fare/10.0))); // Round figure for 10 as suggested
					ridesFareList.add(fare);
				}else {
					ridesFareList.add(minFare);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private void checkTokenExist(){
		String endpoint = GlobalVariables.ServiceUrl + "/walletApis.php";
		String authString = "getToken"+MobileNumber+SPreference.getWalletType(getActivity());
		String params = "act=getToken&mobileNumber="
				+ MobileNumber+"&paymentMethod="+SPreference.getWalletType(getActivity())+ "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		new GlobalAsyncTask(getActivity(), endpoint, params,
				null, RidesAvail.this, true, "getToken",
				false);
	}

	private void openUserProfile(){
		Intent intent = getOpenFacebookIntent(getActivity());
		startActivity(intent);
	}
	public static Intent getOpenFacebookIntent(Context context) {

		try {
			context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/973186792779568"));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/app_scoped_user_id/973186792779568/"));
		}
	}
	private void showSearchRideDialog(){
		searchRideDialog = new Dialog(getActivity());
		searchRideDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		searchRideDialog.setContentView(R.layout.dialog_search_rides);
		searchRideDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
         mFromLocation = (AutoCompleteTextView) searchRideDialog.findViewById(R.id.from_places);
         mToLocation = (AutoCompleteTextView) searchRideDialog.findViewById(R.id.to_places);
        ((TextView)searchRideDialog.findViewById(R.id.tvSearchCabs)).setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
        mFromLocation.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
        mToLocation.setTypeface(FontTypeface.getTypeface(getActivity(), AppConstants.HELVITICA));
        searchRideDialog.findViewById(R.id.tvSubmit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mFromLocation.getText().toString().trim())){
					mFromLocation.setError("Please enter from address");
					return;
				}
				if(TextUtils.isEmpty(mToLocation.getText().toString().trim())){
					mToLocation.setError("Please enter to address");
					return;
				}
				collapseRideList();
				fetchSearchedRides();
                searchRideDialog.dismiss();
                Utility.hideSoftKeyboard(getActivity());

            }
        });
		mFromLocation.setThreshold(2);
		mFromLocation.setOnItemClickListener(mAutocompleteClickListener1);

        mFromLocation.setAdapter(mPlaceArrayAdapter1);

        mToLocation.setThreshold(2);
        mToLocation.setOnItemClickListener(mAutocompleteClickListener2);
           mToLocation.setAdapter(mPlaceArrayAdapter2);
        

       /* mFromLocation.setAdapter(new PlacesAutoCompleteAdapter(
                getActivity(), R.layout.item_spin_support));


        mFromLocation
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int p, long id) {

                        //index = 0;

                        String jnd = mFromLocation.getText()
                                .toString().trim();
                        startSearchAddress = mFromLocation.getText()
                                .toString().trim();
                        fromSearchAddress = MapUtilityMethods.geocodeAddress(jnd,
                                getActivity());

                        if (fromSearchAddress != null) {
                            fromSLocation = fromSearchAddress.getLatitude()+","+fromSearchAddress.getLongitude();



                        } else {
                            fromSLocation="";
                            Toast.makeText(
                                    getActivity(),
                                    "Sorry, we could not find the location you entered, please try again",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });*/



     /*   mToLocation.setAdapter(new PlacesAutoCompleteAdapter(
                getActivity(), R.layout.item_spin_support));*/
       /* mToLocation
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int p, long id) {

                        //index = 0;

                        String jnd = mToLocation.getText()
                                .toString().trim();
                        endSearchAddress = mToLocation.getText()
                                .toString().trim();

                        toSearchAddress = MapUtilityMethods.geocodeAddress(jnd,
                                getActivity());

                        if (toSearchAddress != null) {
                            toELocation = toSearchAddress.getLatitude()+","+toSearchAddress.getLongitude();

                          *//*  AddressModel addressModel = new AddressModel();
                            addressModel.setAddress(toSearchAddress);
                            addressModel.setShortname(MapUtilityMethods
                                    .getAddressshort(
                                            getActivity(),
                                            toSearchAddress.getLatitude(),
                                            toSearchAddress.getLongitude()));
                            addressModel.setLongname(jnd);

                            Gson gson = new Gson();
                            String json = gson.toJson(addressModel);*//*

                            // Log.d("position::::::", "" + pos);

							Utility.hideSoftKeyboard(getActivity());
                        } else {
                            toELocation = "";
                            Toast.makeText(
                                    getActivity(),
                                    "Sorry, we could not find the location you entered, please try again",
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                });*/


        searchRideDialog.show();

	}

	private AdapterView.OnItemClickListener mAutocompleteClickListener1
			= new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter1.getItem(position);
			final String placeId = String.valueOf(item.placeId);
			Log.d(LOG_TAG, "Selected: " + item.description);
			PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
					.getPlaceById(mGoogleApiClient, placeId);
			placeResult.setResultCallback(mUpdatePlaceDetailsCallback1);
			Log.d(LOG_TAG, "Fetching details for ID: " + item.placeId);
		}
	};

	private AdapterView.OnItemClickListener mAutocompleteClickListener2
			= new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter2.getItem(position);
			final String placeId = String.valueOf(item.placeId);
			Log.d(LOG_TAG, "Selected: " + item.description);
			PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
					.getPlaceById(mGoogleApiClient, placeId);
			placeResult.setResultCallback(mUpdatePlaceDetailsCallback2);
			Log.d(LOG_TAG, "Fetching details for ID: " + item.placeId);
		}
	};

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback1
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            final Place place = places.get(0);
            startSearchAddress = mFromLocation.getText().toString().trim();
            if (place.getLatLng() != null) {
                fromSLocation = place.getLatLng().latitude+","+place.getLatLng().longitude;
            } else {
                fromSLocation="";
                Toast.makeText(
                        getActivity(),
                        "Sorry, we could not find the location you entered, please try again",
                        Toast.LENGTH_LONG).show();
            }


        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback2
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            endSearchAddress = mToLocation.getText().toString().trim();
            if (place.getLatLng() != null) {
                toELocation = place.getLatLng().latitude+","+place.getLatLng().longitude;
                Utility.hideSoftKeyboard(getActivity());
            } else {
                toELocation = "";
                Toast.makeText(
                        getActivity(),
                        "Sorry, we could not find the location you entered, please try again",
                        Toast.LENGTH_LONG).show();
            }


        }
    };

}
