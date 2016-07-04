package com.clubmycab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllNotificationListViewAdapter extends BaseAdapter {

	Context context;
	ArrayList<String> SentMemberName;
	ArrayList<String> SentMemberNumber;
	ArrayList<String> ReceiveMemberName;
	ArrayList<String> ReceiveMemberNumber;
	ArrayList<String> Message;
	ArrayList<String> CabId;
	ArrayList<String> DateTime;
	ArrayList<String> Status;
	ArrayList<String> NType;
	LayoutInflater inflater;

	public AllNotificationListViewAdapter(Context context,
			ArrayList<String> smname, ArrayList<String> smnum,
			ArrayList<String> rmname, ArrayList<String> rmnum,
			ArrayList<String> msgs, ArrayList<String> cids,
			ArrayList<String> dtime, ArrayList<String> sts,
			ArrayList<String> ntype) {
		this.context = context;
		this.SentMemberName = smname;
		this.SentMemberNumber = smnum;
		this.ReceiveMemberName = rmname;
		this.ReceiveMemberNumber = rmnum;
		this.Message = msgs;
		this.CabId = cids;
		this.DateTime = dtime;
		this.Status = sts;
		this.NType = ntype;
	}

	@Override
	public int getCount() {
		return CabId.size();
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

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.all_notificationrequest_row_x,
				parent, false);

		// Locate the TextViews in listview_item.xml

		TextView txtNoti = (TextView) itemView.findViewById(R.id.txtNoti);
		ImageView notifyicon = (ImageView) itemView
				.findViewById(R.id.notifyicon);
		TextView datetext = (TextView) itemView.findViewById(R.id.datetext);
		TextView timetext = (TextView) itemView.findViewById(R.id.timetext);
		ImageView readornot = (ImageView) itemView.findViewById(R.id.readornot);
		txtNoti.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		datetext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
		timetext.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));

		txtNoti.setText(Message.get(position));

		/*if (NType.get(position).toString().trim()
				.equalsIgnoreCase("Share_LocationUpdate")) {
			notifyicon.setImageDrawable(context.getResources().getDrawable(
					R.drawable.threedotsformap));
		} else {
			notifyicon.setImageDrawable(context.getResources().getDrawable(
					R.drawable.notifications_joinclub));
		}*/

		String[] arr = DateTime.get(position).split(" ");

		String[] arr1 = arr[0].toString().trim().split("-");

		/*String dt = arr1[2].toString().trim() + "/" + arr1[1].toString().trim()
				+ "/" + arr1[0].toString().trim();*/
		int month = (int)Double.parseDouble(arr1[1].toString().trim());
		String dt = arr1[2].toString().trim()+" "+getMontString(month);

		datetext.setText(dt);

		String _24HourTime = arr[1].toString().trim();
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		Date _24HourDt = null;
		try {
			_24HourDt = _24HourSDF.parse(_24HourTime);
			System.out.println(_24HourDt);
			System.out.println(_12HourSDF.format(_24HourDt));
			timetext.setText(_12HourSDF.format(_24HourDt));
			timetext.setAllCaps(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// final String time = arr[1].toString().trim();
		//
		// try {
		// final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
		// final Date dateObj = sdf.parse(time);
		// System.out.println(dateObj);
		// System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
		// timetext.setText(new SimpleDateFormat("K:mm").format(dateObj));
		// } catch (final ParseException e) {
		// e.printStackTrace();
		// }

		if (Status.get(position).equalsIgnoreCase("U")) {
			readornot.setBackgroundResource(R.drawable.closemail);
		} else {
			readornot.setBackgroundResource(R.drawable.openmail);
		}

		return itemView;
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
}