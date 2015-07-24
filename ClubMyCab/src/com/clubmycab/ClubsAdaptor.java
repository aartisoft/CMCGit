package com.clubmycab;

import java.util.List;

import com.clubmycab.ui.ContactsInviteForRideActivity;
import com.clubmycab.utility.StringTags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ClubsAdaptor extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private List<ClubObject> mainDataList = null;
	int selectedIndex=-1;
	private boolean isWarnning;

	public ClubsAdaptor(Context context, List<ClubObject> mainDataList,boolean isWarnning) {
this.isWarnning=isWarnning;
		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
	}

	static class ViewHolder {
		protected TextView name;
		protected TextView nofmem;
		protected TextView clubownername;
		protected RadioButton check;
		public ImageView ivWarnning;
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
	public void setSelectedIndex(int index){
	    selectedIndex = index;
	    
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
			holder.check = (RadioButton) view.findViewById(R.id.myclubcheckBox);
			holder.ivWarnning=(ImageView)view.findViewById(R.id.ivWarnning);
			

			view.setTag(holder);
			view.setTag(R.id.nameofclub, holder.name);
			view.setTag(R.id.noofmembers, holder.nofmem);
			view.setTag(R.id.clubownername, holder.clubownername);
			view.setTag(R.id.myclubcheckBox, holder.check);
			
			//holder.check.setonc
			
			holder.ivWarnning.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Toast.makeText(mContext,StringTags.TAG_LOW_MEMBER, Toast.LENGTH_LONG).show();
					
				}
			});
			
			holder.check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int getPosition = (Integer) v.getTag();
					setSelectedIndex(getPosition);
					notifyDataSetChanged();
					for(int i=0;i<mainDataList.size();i++){
					if(selectedIndex==i)
					mainDataList.get(i).setSelected(true);
					else
						mainDataList.get(i).setSelected(false);
					}

					//Uncheck Member club
					ContactsInviteForRideActivity.adapterClubMember.setSelectedIndex(-1);
					ContactsInviteForRideActivity.adapterClubMember.notifyDataSetChanged();
					
					
				}
			});

//			holder.check
//					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//						@Override
//						public void onCheckedChanged(CompoundButton vw,
//								boolean isChecked) {
//
//							int getPosition = (Integer) vw.getTag();
//							setSelectedIndex(getPosition);
//							notifyDataSetChanged();
////							mainDataList.get(getPosition).setSelected(
////									vw.isChecked());
//
//						}
//					});

		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if(isWarnning){
			
			try{
				int count=Integer.parseInt(mainDataList.get(position).getNoofMembers());
				if(count<=10)
				holder.ivWarnning.setVisibility(View.VISIBLE);
				else
					holder.ivWarnning.setVisibility(View.GONE);

			}
			catch(Exception e){
				holder.ivWarnning.setVisibility(View.GONE);

				
			}
			
		
			
			
		}
		else
			holder.ivWarnning.setVisibility(View.GONE);

		holder.check.setTag(position);

		if(selectedIndex == position){
			holder.check.setChecked(true);
			mainDataList.get(position).setSelected(true);

		    }
		    else{
		    	holder.check.setChecked(false);
				mainDataList.get(position).setSelected(false);

		    }


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

	//	holder.check.setChecked(mainDataList.get(position).isSelected());

		return view;
	}

}
