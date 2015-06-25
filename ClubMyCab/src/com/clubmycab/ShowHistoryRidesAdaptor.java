package com.clubmycab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;

public class ShowHistoryRidesAdaptor extends PagingBaseAdapter<String> {

	Context context;
	LayoutInflater inflater;
	AQuery aq;

	public ShowHistoryRidesAdaptor(Context context) {
		this.context = context;
		this.aq = new AQuery(context);
	}

	@Override
	public int getCount() {
		return items.size();
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

		CircularImageView myridesbannerimage;
		TextView myridesbannerusername;
		TextView fromtolocationvalue;
		TextView datetext;
		TextView timetext;
		TextView seatstext;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.myrides_list_row, parent,
				false);

		// Locate the TextViews in listview_item.xml

		myridesbannerimage = (CircularImageView) itemView
				.findViewById(R.id.myridesbannerimage);
		myridesbannerusername = (TextView) itemView
				.findViewById(R.id.myridesbannerusername);
		fromtolocationvalue = (TextView) itemView
				.findViewById(R.id.fromtolocationvalue);
		datetext = (TextView) itemView.findViewById(R.id.datetext);
		timetext = (TextView) itemView.findViewById(R.id.timetext);
		seatstext = (TextView) itemView.findViewById(R.id.seatstext);

		if (items.get(position).getImagename().toString().trim().isEmpty()) {

		} else {
			String url = "http://180.179.208.23/cmc/cmcservice"
					+ "/ProfileImages/"
					+ items.get(position).getImagename().toString().trim();
			aq.id(myridesbannerimage).image(url, true, true);
		}
		myridesbannerusername.setText(items.get(position).getOwnerName()
				.toString().trim());
		fromtolocationvalue.setText(items.get(position).getFromShortName()
				.toString().trim()
				+ " > "
				+ items.get(position).getToShortName().toString().trim());
		datetext.setText(items.get(position).getTravelDate().toString().trim());
		timetext.setText(items.get(position).getTravelTime().toString().trim());
		seatstext.setText(items.get(position).getSeat_Status().toString()
				.trim()
				+ " Seats");

		return itemView;
	}
}