package com.clubmycab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.clubmycab.utility.StringTags;

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
		ViewHolder viewholder;
		if(convertView==null){
			
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.myrides_list_row, parent,
					false);
		viewholder=new ViewHolder();
			// Locate the TextViews in listview_item.xml

		viewholder.myridesbannerimage = (CircularImageView) convertView
					.findViewById(R.id.myridesbannerimage);
		viewholder.myridesbannerusername = (TextView) convertView
					.findViewById(R.id.myridesbannerusername);
		viewholder.	fromtolocationvalue = (TextView) convertView
					.findViewById(R.id.fromtolocationvalue);
		viewholder.	datetext = (TextView) convertView.findViewById(R.id.datetext);
		viewholder.	timetext = (TextView) convertView.findViewById(R.id.timetext);
		viewholder.	seatstext = (TextView) convertView.findViewById(R.id.seatstext);
		viewholder.	tvAvSeats=(TextView)convertView.findViewById(R.id.tvAvSeats);
		
		convertView.setTag(viewholder);
		}
		else
			viewholder=(ViewHolder)convertView.getTag();
			
	

		

		if (items.get(position).getImagename().toString().trim().isEmpty()) {

		} else {
			String url = "http://180.179.208.23/cmc/cmcservice"
					+ "/ProfileImages/"
					+ items.get(position).getImagename().toString().trim();
			aq.id(viewholder.myridesbannerimage).image(url, true, true);
		}
		viewholder.myridesbannerusername.setText(items.get(position).getOwnerName()
				.toString().trim());
		viewholder.fromtolocationvalue.setText(items.get(position).getFromShortName()
				.toString().trim()
				+ " > "
				+ items.get(position).getToShortName().toString().trim());
		viewholder.	datetext.setText(items.get(position).getTravelDate().toString().trim());
		viewholder.timetext.setText(items.get(position).getTravelTime().toString().trim());
//		viewholder.seatstext.setText(items.get(position).getSeat_Status().toString()
//				.trim()
//				+ " Seats");
		
		
		try{
			String []arr=items.get(position).getSeat_Status().toString().trim().split("/");
			
			int total=Integer.parseInt(arr[1]);
			int filled=Integer.parseInt(arr[0]);
			int ava=total-filled;
			viewholder.seatstext.setText("Total seats : "+(total+StringTags.TAT_ADD_TOTAL));
			viewholder.tvAvSeats.setText("Available : "+ava);
			}catch(Exception e){
				viewholder.seatstext.setText("Total seats :");
				viewholder.tvAvSeats.setText("Available :");
			}

		return convertView;
	}
	
	public class ViewHolder{
	public 	CircularImageView myridesbannerimage;
	public 	TextView myridesbannerusername;
	public	TextView fromtolocationvalue;
	public	TextView datetext;
	public	TextView timetext;
	public	TextView seatstext;
	public	TextView tvAvSeats;
	}
}