package com.clubmycab;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract.Contacts;
import com.clubmycab.utility.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.clubmycab.utility.GlobalVariables;

public class ContactsAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private List<ContactObject> mainDataList = null;
	private ArrayList<ContactObject> arraylist;
	private AQuery aq;
	
	
	private String filterString1, filterString2;
	private boolean isThreadOn;

	public ContactsAdapter(Context context, List<ContactObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<ContactObject>();
		this.arraylist.addAll(mainDataList);
		aq = new AQuery(mContext);
		
		
		filterString1 = "";
		filterString2 = "";
	}

	static class ViewHolder {
		protected TextView name;
		protected TextView number;
		protected CheckBox check;
		protected ImageView image;
	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public ContactObject getItem(int position) {
		return mainDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.contactslistview, null);

			holder.name = (TextView) view.findViewById(R.id.contactname);
			holder.number = (TextView) view.findViewById(R.id.contactno);

			holder.check = (CheckBox) view.findViewById(R.id.contactcheck);

			holder.image = (ImageView) view.findViewById(R.id.contactimage);

			view.setTag(holder);
			view.setTag(R.id.contactname, holder.name);
			view.setTag(R.id.contactno, holder.number);
			view.setTag(R.id.contactcheck, holder.check);

			holder.check
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton vw,
								boolean isChecked) {

							int getPosition = (Integer) vw.getTag();
							mainDataList.get(getPosition).setSelected(
									vw.isChecked());

						}
					});

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.check.setTag(position);

		holder.name.setText(mainDataList.get(position).getName());
		holder.number.setText(mainDataList.get(position).getNumber());

		if (getByteContactPhoto(mainDataList.get(position).getImage()) == null) {

			if (mainDataList.get(position).getImage()
					.equalsIgnoreCase("91089108")) {

				Log.d("Appuserimage", "Appuserimage");
				String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ mainDataList.get(position).getAppUserimagename();
				aq.id(holder.image).image(url, true, true);
			} else {
				holder.image.setImageResource(R.drawable.contacticon);
			}

		} else {
			holder.image.setImageBitmap(getByteContactPhoto(mainDataList.get(
					position).getImage()));
		}

		holder.check.setChecked(mainDataList.get(position).isSelected());

		return view;
	}

	public void filter(String charText) {
		
		if (filterString1.isEmpty() && filterString2.isEmpty()) {
			filterString1 = charText.toLowerCase(Locale.getDefault());
			filterString2 = charText.toLowerCase(Locale.getDefault());
			
			isThreadOn = true;
			FilterAsync filterAsync = new FilterAsync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				filterAsync.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, filterString1);
			} else {
				filterAsync.execute(filterString1);
			}
		} else {
			if (isThreadOn) {
				filterString2 = charText.toLowerCase(Locale.getDefault());
			} else {
				filterString1 = charText.toLowerCase(Locale.getDefault());
				filterString2 = charText.toLowerCase(Locale.getDefault());
				isThreadOn = true;
				FilterAsync filterAsync = new FilterAsync();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					filterAsync.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, filterString1);
				} else {
					filterAsync.execute(filterString1);
				}
			}
			
		}
		
//		charText = charText.toLowerCase(Locale.getDefault());
//		mainDataList.clear();
//		if (charText.length() == 0) {
//			mainDataList.addAll(arraylist);
//		} else {
//			for (ContactObject wp : arraylist) {
//				if (wp.getName().toLowerCase(Locale.getDefault())
//						.contains(charText)) {
//					mainDataList.add(wp);
//				}
//			}
//		}
//		notifyDataSetChanged();
	}
	
	private class FilterAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(String... args) {
			
			String charText = args[0].toString();
			mainDataList.clear();
			if (charText.length() == 0) {
				mainDataList.addAll(arraylist);
			} else {
				for (ContactObject wp : arraylist) {
					if (wp.getName().toLowerCase(Locale.getDefault())
							.contains(charText)) {
						mainDataList.add(wp);
					}
				}
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			notifyDataSetChanged();
			
			isThreadOn = false;
			
			if (!filterString1.equals(filterString2)) {
				filter(filterString2);
			}
		}
	}

	public Bitmap getByteContactPhoto(String contactId) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				Long.parseLong(contactId));
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = mContext.getContentResolver().query(photoUri,
				new String[] { Contacts.Photo.DATA15 }, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					return BitmapFactory.decodeStream(new ByteArrayInputStream(
							data));
				}
			}
		} finally {
			cursor.close();
		}

		return null;
	}

}
