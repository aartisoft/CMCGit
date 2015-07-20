package com.clubmycab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.clubmycab.FindFavoritesPlaceAdapter;
import com.clubmycab.R;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.gson.Gson;

public class FavoritePlaceFindActivity extends Activity implements
		OnClickListener {

	private ListView lvFavoritePlace;
	private Button btnDone;
	private Boolean flagchk;
	private String fromshortname;
	public static ArrayList<String> resultTag = new ArrayList<String>();

	private AutoCompleteTextView from_places;
	FindFavoritesPlaceAdapter adapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_favoriteplace_find);

		flagchk = true;
		findIds();
		setListenrs();
		setOnitemList();
	}

	// Find all ids of layout here
	private void findIds() {

		lvFavoritePlace = (ListView) findViewById(R.id.lvFavoritePlace);
		from_places = (AutoCompleteTextView) findViewById(R.id.from_places1);

		btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(this);
		FindFavoritesPlaceAdapter.resultList.clear();
		resultTag.clear();
		//Call to get all saved addresses by user
		getWorkHomeAddress();

		adapter= new FindFavoritesPlaceAdapter(this,
				R.layout.list_item_custom);
		
		if(resultTag.size()==0){
		//InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		 //  inputMethodManager.toggleSoftInputFromWindow(from_places.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		}
		lvFavoritePlace.setAdapter(adapter);
		from_places.setDropDownHeight(0);

		from_places.setAdapter(adapter);

		

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
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
					
					getWorkHomeAddress();

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

				from_places.setText(FindFavoritesPlaceAdapter.resultList
						.get(pos));

				dismissKeyboard();
				Intent resultIntent = new Intent();

				resultIntent.putExtra("address", from_places.getText()
						.toString());

				setResult(Activity.RESULT_OK, resultIntent);
				finish();

			}
		});

	}
	
	//For dismiss key board

	private void dismissKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}

	//Get all saved favorite location from shared preference
	private void getWorkHomeAddress() {
	resultTag.clear();
	FindFavoritesPlaceAdapter.resultList.clear();
	
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
				addressModel = (AddressModel) gson.fromJson(hashMap.get(key),
						AddressModel.class);
				FindFavoritesPlaceAdapter.resultList.add(addressModel
						.getLongname());
				}
				catch(Exception e){
				FindFavoritesPlaceAdapter.resultList.add("");
				}
				
				if(adapter!=null)
					adapter.notifyDataSetChanged();
			


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
	

}
