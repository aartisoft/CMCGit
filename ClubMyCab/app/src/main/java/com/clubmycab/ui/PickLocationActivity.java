package com.clubmycab.ui;

import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.LocationModel;
import com.clubmycab.model.LocationModelListener;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class PickLocationActivity extends FragmentActivity implements View.OnClickListener{
    public static final int PICLOCATION_REQUEST =  1001;
	private GoogleMap myMap;
	private LatLng invitemapcenter;
	private double latitude = 28.619570;
	private double longitude = 77.088104;
    public String address;
    private Address addressModel;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy_xml);
        findViewById(R.id.cardBack).setOnClickListener(this);
        findViewById(R.id.tvDone).setOnClickListener(this);
        ((Button)findViewById(R.id.tvDone)).setTypeface(FontTypeface.getTypeface(PickLocationActivity.this, AppConstants.HELVITICA));
        if(getIntent() != null){
			Bundle bundle = getIntent().getExtras();
			if(bundle != null && bundle.containsKey("latitude")){
				latitude =  bundle.getDouble("latitude");
			}
			if(bundle != null && bundle.containsKey("longitude")){
				longitude =  bundle.getDouble("longitude");
			}
            if(bundle != null && bundle.containsKey("longitude")){
                address =  bundle.getString("address");
                ((TextView)findViewById(R.id.tvLocationAddress)).setText(address);

            }
		}
		myMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frommap)).getMap();

		myMap.setMyLocationEnabled(true);

		myMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				invitemapcenter = cameraPosition.target;
                latitude = invitemapcenter.latitude;
                longitude = invitemapcenter.longitude;
                locationHandler(invitemapcenter);
            }
		});
		if(latitude !=0 || longitude !=0){
            LatLng currentlatLng = new LatLng(latitude, longitude);
            myMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
            myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }


       /* Intent intent = new Intent();
        intent.putE*/
		/*String address = MapUtilityMethods.getAddress(PickLocationActivity.this, latitude, longitude);
		//fromlocation.setText(address);
		fromrelative.setVisibility(View.VISIBLE);
		contentrelativehomepage.setVisibility(View.GONE);*/
	}

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.cardBack:
               finish();
               break;

           case R.id.tvDone:
               Intent intent = new Intent();
               Bundle bundle = new Bundle();
               bundle.putParcelable("addressmodel", addressModel);
               bundle.putDouble("latitude", invitemapcenter.latitude);
               bundle.putDouble("longitude", invitemapcenter.longitude);
               bundle.putString("address", address);

               intent.putExtras(bundle);
               setResult(RESULT_OK, intent);
               finish();
               break;
       }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((com.github.silvestrpredko.dotprogressbar.DotProgressBar)findViewById(R.id.dot_progress_bar)).setVisibility(View.GONE);

    }

    private void locationHandler(LatLng latLng){
        //if (isOnline()) {
        LocationModel locationModel = new LocationModel();
        locationModel.setLocationByAddress(false);
        locationModel.setLatLng(latLng);
        locationModel.setLocationByAddress(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetLocationTaskHandler(PickLocationActivity.this, locationModelListener,locationModel )
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetLocationTaskHandler(PickLocationActivity.this, locationModelListener,locationModel).execute();
        }
        // }
    }

    private LocationModelListener locationModelListener = new LocationModelListener() {

        @Override
        public void getAddress(Address address) {
            addressModel =  address;

        }

        @Override
        public void getLatLong(LatLng latLng) {

        }

        @Override
        public void getStringAddress(String addres) {
            address = addres;
            if(!TextUtils.isEmpty(address)){
                ((TextView)findViewById(R.id.tvLocationAddress)).setText(address);
            }else {
                ((TextView)findViewById(R.id.tvLocationAddress)).setText("Please move map to other loc");
            }

        }

        @Override
        public void getError(String error) {
            ((TextView)findViewById(R.id.tvLocationAddress)).setText(error);
        }

        @Override
        public void isLoading(boolean isLoading) {
            if(isLoading){
                ((com.github.silvestrpredko.dotprogressbar.DotProgressBar)findViewById(R.id.dot_progress_bar)).setVisibility(View.VISIBLE);
                findViewById(R.id.tvLocationAddress).setVisibility(View.GONE);
            }else {
                findViewById(R.id.tvLocationAddress).setVisibility(View.VISIBLE);
                ((com.github.silvestrpredko.dotprogressbar.DotProgressBar)findViewById(R.id.dot_progress_bar)).setVisibility(View.GONE);
            }
        }


    };
}
