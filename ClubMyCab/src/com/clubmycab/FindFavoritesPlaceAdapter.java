package com.clubmycab;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.LinearGradient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clubmycab.ui.FavoritePlaceFindActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class FindFavoritesPlaceAdapter extends BaseAdapter implements
		Filterable {
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	public static ArrayList<String> resultList = new ArrayList<String>();
	private LayoutInflater inflater;
	

	public FindFavoritesPlaceAdapter(Context context, int textViewResourceId) {
		super();

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}

	public ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + GlobalVariables.GoogleMapsAPIKey);
			sb.append("&components=country:ind");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("PlacesAutoCompleteAdapter",
					"Error processing Places API URL" + e);
			return resultList;
		} catch (IOException e) {
			Log.e("PlacesAutoCompleteAdapter", "Error connecting to Places API"
					+ e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
			// Log.d("PlacesAutoCompleteAdapter", "resultList : " + resultList);
		} catch (JSONException e) {
			Log.e("PlacesAutoCompleteAdapter", "Cannot process JSON results"
					+ e);
		}

		return resultList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.adapter_findplace_row,
					parent, false);

			holder.tvTag = (TextView) convertView.findViewById(R.id.tvTags);
			holder.tvAddress = (TextView) convertView
					.findViewById(R.id.tvAddress);

			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		holder.tvAddress.setText(resultList.get(position));
		
		//Check if address is already saved or not. If added its has tag value
		 if(FavoritePlaceFindActivity.resultTag.size()>0){
		 if(FavoritePlaceFindActivity.resultTag.size()>=position)
		 holder.tvTag.setText(FavoritePlaceFindActivity.resultTag.get(position));
		 else{
		 holder.tvTag.setText("");
		 }
		 }
		 else{
			 holder.tvTag.setText(resultList.get(position));
			 holder.tvAddress.setText("");
			 LinearLayout.LayoutParams param=(LinearLayout.LayoutParams)holder.tvTag.getLayoutParams();
			 param.setMargins(15,15,15,-5);
			holder.tvTag.setLayoutParams(param);
			 
			 
		 }

		return convertView;
	}

	public class ViewHolder {

		public TextView tvTag;
		public TextView tvAddress;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}