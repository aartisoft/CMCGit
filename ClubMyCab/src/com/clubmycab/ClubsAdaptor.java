package com.clubmycab;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ClubsAdaptor extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private List<ClubObject> mainDataList = null;

	public ClubsAdaptor(Context context, List<ClubObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
	}

	static class ViewHolder {
		protected TextView name;
		protected TextView nofmem;
		protected TextView clubownername;
		protected CheckBox check;
	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public ClubObject getItem(int position) {
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
			view = inflater.inflate(R.layout.activity_newclub_item, null);

			holder.name = (TextView) view.findViewById(R.id.nameofclub);
			holder.nofmem = (TextView) view.findViewById(R.id.noofmembers);
			holder.clubownername = (TextView) view
					.findViewById(R.id.clubownername);
			holder.check = (CheckBox) view.findViewById(R.id.myclubcheckBox);

			view.setTag(holder);
			view.setTag(R.id.nameofclub, holder.name);
			view.setTag(R.id.noofmembers, holder.nofmem);
			view.setTag(R.id.clubownername, holder.clubownername);
			view.setTag(R.id.myclubcheckBox, holder.check);

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

		holder.nofmem.setText("(" + mainDataList.get(position).getNoofMembers()
				+ ")");

		if (mainDataList.get(position).getClubOwnerName().toString().trim()
				.equalsIgnoreCase("")
				|| mainDataList.get(position).getClubOwnerName().toString()
						.trim().length() == 0) {

			holder.clubownername.setVisibility(View.GONE);

		} else {
			holder.clubownername.setText("("
					+ mainDataList.get(position).getClubOwnerName() + ")");
		}

		holder.check.setChecked(mainDataList.get(position).isSelected());

		return view;
	}

}
