package com.clubmycab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.model.GroupDataModel;
import com.clubmycab.ui.SendInvitesToOtherScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.StringTags;

import java.util.List;

public class SelectedGroupAdapter extends BaseAdapter {
    private static final int ANIMATION_DURATION = 200;

	Context mContext;
	LayoutInflater inflater;
	private List<GroupDataModel> mainDataList = null;
	int selectedIndex = -1;
	private boolean isWarnning;
	
	public SelectedGroupAdapter(){
	}

	public void init(Context context, List<GroupDataModel> mainDataList,
			boolean isWarnning) {
		this.isWarnning = isWarnning;
		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);

	}

	 class ViewHolder {
		private boolean needInflate;
        private TextView name;
		private TextView nofmem;
		private TextView clubownername;
		private RadioButton check;
        private FrameLayout flAddGroup;

		 public ViewHolder(View view) {
			 	name =(TextView) view.findViewById(R.id.nameofclub);
			 	nofmem = (TextView) view.findViewById(R.id.noofmembers);
			 	clubownername = (TextView) view
						.findViewById(R.id.clubownername);
			 	check = (RadioButton) view.findViewById(R.id.myclubcheckBox);
	            flAddGroup = (FrameLayout)view.findViewById(R.id.flAddGroup);
			 name.setTypeface(FontTypeface.getTypeface(mContext, AppConstants.HELVITICA));
			 nofmem.setTypeface(FontTypeface.getTypeface(mContext, AppConstants.HELVITICA));
			 clubownername.setTypeface(FontTypeface.getTypeface(mContext, AppConstants.HELVITICA));

	        }
	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public GroupDataModel getItem(int position) {
		return mainDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        final View view;

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_remove_group, parent, false);
            setViewHolder(view);
        } else if (((ViewHolder)convertView.getTag()).needInflate) {
            view = inflater.inflate(R.layout.item_remove_group, parent, false);
            setViewHolder(view);
        }else {
            view = convertView;
        }
        holder = (ViewHolder) view.getTag();
        GroupDataModel currentListData = getItem(position);
			/*holder.ivWarnning.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Toast.makeText(mContext, StringTags.TAG_LOW_MEMBER,
							Toast.LENGTH_LONG).show();

				}
			});*/

			holder.check.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int getPosition = (Integer) v.getTag();

					int count = Integer.parseInt(mainDataList.get(getPosition)
							.getNumberOfMembers());

					if (count <= 1) {

						Toast.makeText(mContext,
								StringTags.TAG_DOES_NOT_HAVE_MEMBER,
								Toast.LENGTH_SHORT).show();
					//	holder.check.setSelected(false);
						notifyDataSetChanged();

					} else {
						
						/*if (mainDataList.get(getPosition).isSelected()) {
							mainDataList.get(getPosition).setSelected(false);
						} else {
							mainDataList.get(getPosition).setSelected(true);
						}*/
						((SendInvitesToOtherScreen)mContext).onGroupChecked(mainDataList.get(getPosition));
						notifyDataSetChanged();

					}

				}
			});

		

		/*if (isWarnning) {

			try {
				int count = Integer.parseInt(mainDataList.get(position)
						.getNumberOfMembers());
				if (count <= 10)
					holder.ivWarnning.setVisibility(View.VISIBLE);
				else
					holder.ivWarnning.setVisibility(View.GONE);

			} catch (Exception e) {
				holder.ivWarnning.setVisibility(View.GONE);

			}

		} else
			holder.ivWarnning.setVisibility(View.GONE);*/

		holder.check.setTag(position);
		
		/*if (mainDataList.get(position).isSelected()) {
			holder.check.setChecked(true);
		} else {
			holder.check.setChecked(false);
		}*/
		
		holder.name.setText(mainDataList.get(position).getPoolName());

		holder.nofmem.setText("(" + mainDataList.get(position).getNumberOfMembers()
				+ ")");

		if (mainDataList.get(position).getOwnerName().toString().trim()
				.equalsIgnoreCase("")
				|| mainDataList.get(position).getOwnerName().toString()
						.trim().length() == 0) {

			holder.clubownername.setVisibility(View.GONE);

		} else {
			holder.clubownername.setText(mainDataList.get(position).getOwnerName());
		}
		 holder.flAddGroup.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                deleteCell(view, position);
	            }
	        });

		return view;
	}
	
	  private void deleteCell(final View v, final int index) {
	        Animation.AnimationListener al = new Animation.AnimationListener() {
	            @Override
	            public void onAnimationEnd(Animation arg0) {

	                ViewHolder vh = (ViewHolder)v.getTag();
	                vh.needInflate = true;
	                ((SendInvitesToOtherScreen)mContext).removeGroupClicked(getItem(index));
	            }
	            @Override public void onAnimationRepeat(Animation animation) {}
	            @Override public void onAnimationStart(Animation animation) {}
	        };

	        collapse(v, al);
	    }

	    private void setViewHolder(View view) {
	        ViewHolder vh = new ViewHolder(view);
	        vh.needInflate = false;
	        view.setTag(vh);
	    }

	    private void collapse(final View v, Animation.AnimationListener al) {
	        final int initialHeight = v.getMeasuredHeight();

	        Animation anim = new Animation() {
	            @Override
	            protected void applyTransformation(float interpolatedTime, Transformation t) {
	                if (interpolatedTime == 1) {
	                    v.setVisibility(View.GONE);
	                }
	                else {
	                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                    v.requestLayout();
	                }
	            }

	            @Override
	            public boolean willChangeBounds() {
	                return true;
	            }
	        };

	        if (al!=null) {
	            anim.setAnimationListener(al);
	        }
	        anim.setDuration(ANIMATION_DURATION);
	        v.startAnimation(anim);

	    }

}

