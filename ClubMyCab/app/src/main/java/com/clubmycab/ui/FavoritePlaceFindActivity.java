package com.clubmycab.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.clubmycab.FindFavoritesPlaceAdapter;
import com.clubmycab.R;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.model.SavedSearchedModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.clubmycab.utility.Utility;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class FavoritePlaceFindActivity extends Activity implements OnClickListener, FindFavoritesPlaceAdapter.FavouriteAdapterDataChangeListner {

	private ListView lvFavoritePlace;
	private Button btnDone;
	private Boolean flagchk;
	private String fromshortname;
	public static ArrayList<String> resultTag = new ArrayList<String>();

	private static final int MAX_RECENT_SEARCH_COUNT = 5;
	private static final int TAB_VIEW_TEXT_SIZE_SP = 14;

	private AutoCompleteTextView from_places;
	FindFavoritesPlaceAdapter adapter ;
	private boolean isAlreadyClicked;
    public  ArrayList<String> resultList = new ArrayList<String>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "TAG";
    private ArrayList<LatLng> arrayListLatLong = new ArrayList<LatLng>();

    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_favoriteplace_find);
        findViewById(R.id.llTopLayout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(FavoritePlaceFindActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    findViewById(R.id.llTopLayout).setClickable(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.llTopLayout).setClickable(true);

                        }
                    },3000);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });

      /*  PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                dismissKeyboard();
                StringBuilder buider = new StringBuilder();
                if(!TextUtils.isEmpty(place.getName())){
                    buider.append(place.getName()+", ");
                }
                if(!TextUtils.isEmpty(place.getAddress())){
                    buider.append(place.getAddress());
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("address", buider.toString());
                resultIntent.putExtra("latitude", place.getLatLng().latitude);
                resultIntent.putExtra("longitude", place.getLatLng().longitude);
                resultIntent.putExtra("sortname", place.getName());
                setResult(Activity.RESULT_OK, resultIntent);
                if(!TextUtils.isEmpty(buider.toString())){
                    saveRecentSearchedPlace(buider.toString(),String.valueOf(place.getLatLng().latitude),String.valueOf(place.getLatLng().longitude));
                }
                finish();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
            }
        });*/


        flagchk = true;
		try{
			findIds();
			//setListenrs();
			setOnitemList();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	// Find all ids of layout here
	private void findIds() {
		try{
			lvFavoritePlace = (ListView) findViewById(R.id.lvFavoritePlace);
			from_places = (AutoCompleteTextView) findViewById(R.id.from_places1);
			btnDone = (Button) findViewById(R.id.btnDone);
			btnDone.setOnClickListener(this);
			if(resultList != null)
		    resultList.clear();
			resultTag.clear();
			getWorkHomeAddress();
            showRecentSearches();
			adapter= new FindFavoritesPlaceAdapter(this,
					R.layout.list_item_custom);
            adapter.init(resultList);
            final SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
            int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
			if(resultTag.size()==0 && count == 0){
			   // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(FavoritePlaceFindActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
			}
			lvFavoritePlace.setAdapter(adapter);
			from_places.setDropDownHeight(0);
			from_places.setAdapter(adapter);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//Set ontextchange listener for autoCompletetextview from_place

	private void setListenrs() {
		from_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				return false;
			}

		});

		from_places
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							// dismissKeyboard();
						}

						return false;
					}

				});

		from_places
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

					}
				});

		from_places.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence cs, int arg1,
					int arg2, int arg3) {
				// When user changed the Text

				String text = from_places.getText().toString().trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					
					//getWorkHomeAddress();

				} else {
					resultTag.clear();
				}
				//resultTag.clear();

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					fromshortname = MapUtilityMethods.getaddressfromautoplace(
							FavoritePlaceFindActivity.this, from_places
									.getText().toString().trim());

				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

	}

	//Set listview onitemclick listener 
	private void setOnitemList() {

		lvFavoritePlace.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {


				try{
					if(isAlreadyClicked){
						return;
					}else {
						isAlreadyClicked = true;
					}
					if(resultList != null || resultList.size()>0){
						from_places.setText(resultList.get(pos));
						dismissKeyboard();
						Intent resultIntent = new Intent();
						resultIntent.putExtra("address", resultList.get(pos));
                        resultIntent.putExtra("latitude", arrayListLatLong.get(pos).latitude);
                        resultIntent.putExtra("longitude", arrayListLatLong.get(pos).longitude);

                        setResult(Activity.RESULT_OK, resultIntent);
						if(!TextUtils.isEmpty(from_places.getText().toString().trim())){
							//saveRecentSearchedPlace(from_places.getText().toString().trim());
						}
						finish();
					}

				}catch (Exception e){
					e.printStackTrace();
				}

			}
		});

	}

	public void findPlace(View view) {
		try {
			Intent intent =
					new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
							.build(this);
			startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
		} catch (GooglePlayServicesRepairableException e) {
			// TODO: Handle the error.
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO: Handle the error.
		}
	}


	/*private void saveRecentSearchedPlace(String address,String latitude, String longitude) {
		if(!isAddrresExists(address)){
            SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
            if(count == MAX_RECENT_SEARCH_COUNT){
                count = 0;
            }

            editor.putString(AppConstants.SEARCH_INDEX_+count,address);
            editor.putString(AppConstants.SEARCH_INDEX_LATITUDE_+count,latitude );
            editor.putString(AppConstants.SEARCH_INDEX_LONGITUDE_+count,longitude );
            editor.putInt(AppConstants.SEARCH_COUNT, count+1);
            editor.commit();
        }
	}*/
    private void saveRecentSearchedPlace(String address,String latitude, String longitude) {
        if(!isAddrresExists(address)){
            SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ArrayList<SavedSearchedModel> arrayList = new ArrayList<SavedSearchedModel>();
            SavedSearchedModel savedSearchedModel = new SavedSearchedModel();
            savedSearchedModel.address = address;
            savedSearchedModel.latitude = latitude;
            savedSearchedModel.longitude = longitude;
            arrayList.add(savedSearchedModel);

            int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
            ArrayList<SavedSearchedModel> arrayList2 = new ArrayList<SavedSearchedModel>();
            for (int i = 0; i < count ; i++) {
                SavedSearchedModel savedSearchedModel1 = new SavedSearchedModel();
                savedSearchedModel1.address = sharedPreferences.getString(AppConstants.SEARCH_INDEX_+i,"");
                savedSearchedModel1.latitude = sharedPreferences.getString(AppConstants.SEARCH_INDEX_LATITUDE_+i,"");
                savedSearchedModel1.longitude = sharedPreferences.getString(AppConstants.SEARCH_INDEX_LONGITUDE_+i,"");
                arrayList2.add(savedSearchedModel1);
            }


            arrayList.addAll(arrayList2);
            if(arrayList.size() > MAX_RECENT_SEARCH_COUNT){
                arrayList.remove(MAX_RECENT_SEARCH_COUNT);
            }

            for (int i = 0; i < arrayList.size() ; i++) {
                SavedSearchedModel searchModel = arrayList.get(i);
                editor.putString(AppConstants.SEARCH_INDEX_+i,searchModel.address);
                editor.putString(AppConstants.SEARCH_INDEX_LATITUDE_+i,searchModel.latitude );
                editor.putString(AppConstants.SEARCH_INDEX_LONGITUDE_+i,searchModel.longitude );

            }
            editor.putInt(AppConstants.SEARCH_COUNT, arrayList.size());
            editor.commit();

           /* if(count == MAX_RECENT_SEARCH_COUNT){
                count = 0;
            }*/


        }
    }
    private boolean isAddrresExists(String address){
        SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
        for (int i = 0; i < count; i++) {
            if(sharedPreferences.getString(AppConstants.SEARCH_INDEX_+i,"").equalsIgnoreCase(address)){
                return true;
            }
        }

        return false;
    }

	private void showRecentSearches(){
		final SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
        int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
        if(count == 0){
            return;
        }

        int size = 0;
        for (int i = 0; i < MAX_RECENT_SEARCH_COUNT ; i++) {
            if(!TextUtils.isEmpty(sharedPreferences.getString(AppConstants.SEARCH_INDEX_+i,""))){
                size = size+1;
            }
        }
        if(size != 0){
            findViewById(R.id.llRecentSearch).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.llRecentSearch).setVisibility(View.GONE);

        }
        //size = size+1;
		LinearLayout llSearcedAddress = (LinearLayout)findViewById(R.id.llRecentSearchAddress);
		for (int i = 0; i < size; i++) {
			final TextView texView = new TextView(FavoritePlaceFindActivity.this);
			texView.setId(i);
            texView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
            texView.setTextColor(Color.parseColor("#000000"));
            texView.setSingleLine(true);
            texView.setEllipsize(TextUtils.TruncateAt.END);
			texView.setMinLines(2);
            texView.setText(sharedPreferences.getString(AppConstants.SEARCH_INDEX_+i,""));
            texView.setTypeface(FontTypeface.getTypeface(FavoritePlaceFindActivity.this,AppConstants.HELVITICA));
            texView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
					Utility.hideSoftKeyboard(FavoritePlaceFindActivity.this);
                    if(TextUtils.isEmpty(sharedPreferences.getString(AppConstants.SEARCH_INDEX_LATITUDE_+texView.getId(),"")))
                        return;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("address", sharedPreferences.getString(AppConstants.SEARCH_INDEX_+texView.getId(),""));
					resultIntent.putExtra("latitude", Double.parseDouble(sharedPreferences.getString(AppConstants.SEARCH_INDEX_LATITUDE_+texView.getId(),"")));
					resultIntent.putExtra("longitude", Double.parseDouble(sharedPreferences.getString(AppConstants.SEARCH_INDEX_LONGITUDE_+texView.getId(),"")));
					setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });
            llSearcedAddress.addView(texView);
            if(i < size-1){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
                View view = new View(FavoritePlaceFindActivity.this);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.parseColor("#e8e8e8"));
                llSearcedAddress.addView(view);
            }
        }

	}

	private void hideRecentSearches(){
        findViewById(R.id.llRecentSearch).setVisibility(View.GONE);
	}

	//For dismiss key board

	private void dismissKeyboard() {
		try{
           if(this != null){
               InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
               inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
                       .getWindowToken(), 0);
           }
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	//Get all saved favorite location from shared preference
	private void getWorkHomeAddress() {
        resultTag.clear();
        resultList.clear();

        SharedPreferences mPrefs11111 = getSharedPreferences(
                "FavoriteLocations", 0);
        final String favoritelocation = mPrefs11111.getString(
                "favoritelocation", "");
        Log.d("FavoritePlaceFindActivity::::::", "favoritelocation : "
                + favoritelocation);

        AddressModel addressModel = null, workAddressModel = null;

        if (!favoritelocation.isEmpty()) {

            Gson gson = new Gson();
            @SuppressWarnings("unchecked")
            HashMap<String, String> hashMap = gson.fromJson(favoritelocation,
                    HashMap.class);

            //Set keys = hashMap.keySet();
            SortedSet<String> keys = new TreeSet<String>(hashMap.keySet());


            for (Iterator i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();

                if(hashMap.get(key).isEmpty()||hashMap.get(key).equalsIgnoreCase(""))
                    continue;

                if (key.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY)){
                    resultTag.add("Home");


                }
                else if (key.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY)){
                    resultTag.add("Office");
                }


                else{
                    resultTag.add(key);
                }

                Log.d("Key value::", key);
                // Get long address from location

                try{
                    addressModel = (AddressModel) gson.fromJson(hashMap.get(key), AddressModel.class);
                    resultList.add(addressModel.getLongname());
                    arrayListLatLong.add(new LatLng(addressModel.getAddress().getLatitude(), addressModel.getAddress().getLongitude()));
                }
                catch(Exception e){
                 /*   resultList.add("");
                    arrayListLatLong.add(null);*/
                }

                if(adapter!=null){
					adapter.init(resultList);
					adapter.notifyDataSetChanged();
				}




            }

        }

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnDone) {

			Intent resultIntent = new Intent();

			resultIntent.putExtra("address", from_places.getText().toString());

			setResult(Activity.RESULT_OK, resultIntent);
			finish();

		}

	}


	@Override
	public void onDataChanged( ArrayList<String> resultList) {
        try{
            hideRecentSearches();
            this.resultList = resultList;
          /*  if(adapter != null){
                adapter.notifyDataSetChanged();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utility.hideSoftKeyboard(FavoritePlaceFindActivity.this);

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
              //  dismissKeyboard();
                StringBuilder buider = new StringBuilder();
                if(!TextUtils.isEmpty(place.getName())){
                    buider.append(place.getName()+", ");
                }
                if(!TextUtils.isEmpty(place.getAddress())){
                    buider.append(place.getAddress());
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("address", buider.toString());
                resultIntent.putExtra("latitude", place.getLatLng().latitude);
                resultIntent.putExtra("longitude", place.getLatLng().longitude);
                resultIntent.putExtra("sortname", place.getName());
                setResult(Activity.RESULT_OK, resultIntent);
                if(!TextUtils.isEmpty(buider.toString())){
                    saveRecentSearchedPlace(buider.toString(),String.valueOf(place.getLatLng().latitude),String.valueOf(place.getLatLng().longitude));
                }
                finish();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.d("TAG", status.getStatusMessage());
                final SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
                int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
                if(resultTag.size()==0 && count == 0){
                    finish();
                }

            } else if (resultCode == RESULT_CANCELED) {
                final SharedPreferences sharedPreferences = getSharedPreferences("FacebookData", 0);
                int count = sharedPreferences.getInt(AppConstants.SEARCH_COUNT,0);
                if(resultTag.size()==0 && count == 0){
                    finish();
                }
            }
        }
    }

}
