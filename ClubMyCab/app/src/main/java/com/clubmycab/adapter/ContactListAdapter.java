package com.clubmycab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.ContactData;
import com.clubmycab.ui.SendInvitesToOtherScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;

import java.util.ArrayList;

public class ContactListAdapter extends BaseAdapter {
    private static final int ANIMATION_DURATION = 200;
    private ArrayList<ContactData> contactList = new ArrayList<ContactData> ();

    private LayoutInflater inflater;
    private Context context;

    public ContactListAdapter(){

    }

    public void init(Context context, ArrayList contactList) {
        this.contactList = contactList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public ContactData getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        final View view;

        if (convertView == null) {
            view = inflater.inflate(R.layout.component_contact_list, parent, false);
            setViewHolder(view);
        } else if (((ViewHolder)convertView.getTag()).needInflate) {
            view = inflater.inflate(R.layout.component_contact_list, parent, false);
            setViewHolder(view);
        }else {
            view = convertView;
        }
        mViewHolder = (ViewHolder) view.getTag();
        ContactData currentListData = getItem(position);
        mViewHolder.tvName.setText(currentListData.getName());
        mViewHolder.tvNumber.setText(currentListData.getPhoneNumber());
        mViewHolder.tvTitle.setText(String.valueOf(currentListData.getName().charAt(0)).toUpperCase());
        mViewHolder.flAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCell(view, position);
            }
        });


        return view;
    }

    private class ViewHolder {
        TextView tvTitle, tvName,tvNumber;
        FrameLayout flAddGroup;
        public boolean needInflate;

        public ViewHolder(View item) {
            tvTitle = (TextView) item.findViewById(R.id.tvFirstLetter);
            tvNumber = (TextView) item.findViewById(R.id.tvNumber);
            tvName = (TextView) item.findViewById(R.id.tvName);
            flAddGroup = (FrameLayout)item.findViewById(R.id.flAddGroup);
            tvTitle.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
            tvNumber.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
            tvName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));

        }
    }

    private void deleteCell(final View v, final int index) {
      //  selectedContact.add(getItem(index));
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {

                ViewHolder vh = (ViewHolder)v.getTag();
                vh.needInflate = true;
                ((SendInvitesToOtherScreen)context).addUserClicked(getItem(index));
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