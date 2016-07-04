package com.clubmycab.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.model.JoinedMemberModel;
import com.clubmycab.ui.ImageScreen;
import com.clubmycab.ui.XCheckPoolFragmentActivty;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by newpc on 29/4/16.
 */
public class JoinedMemberAdapter extends BaseAdapter {
    private ArrayList<JoinedMemberModel> arrayList;
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private boolean isComeFromHistory;
    private Tracker tracker;

    public void init(Context context, ArrayList<JoinedMemberModel> arrayList, boolean isComeFromHistory, Tracker tracker) {
        this.arrayList = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.isComeFromHistory = isComeFromHistory;
        this.tracker = tracker;
    }

    @Override
    public int getCount() {
        if (arrayList != null && arrayList.size() > 0) {
            return arrayList.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
       // return arrayList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        //return arrayList.indexOf(getItem(position));
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_joined_memberlist, parent, false);
            holder = new ViewHolder(convertView);
            // holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(arrayList.get(position).getMemeberName() != null){
            holder.tvMemberName.setText(arrayList.get(position).getMemeberName());
        }
        if(!TextUtils.isEmpty(arrayList.get(position).getHasBoarded())){
            if(arrayList.get(position).getHasBoarded().equalsIgnoreCase("0")){
                holder.tvStatus.setText("Awaiting");

            }else if(arrayList.get(position).getHasBoarded().equalsIgnoreCase("1")){
                holder.tvStatus.setText("Paid");

            }else if(arrayList.get(position).getHasBoarded().equalsIgnoreCase("2")){
                holder.tvStatus.setText("Take cash");

            }
        }
        if(arrayList.get(position).getMemberImageName() != null){
            String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                    +arrayList.get(position).getMemberImageName().toString().trim();
            Picasso.with(context).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(holder.ivMemberImage);
        }
        holder.ivMemberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                        +arrayList.get(position).getMemberImageName().toString().trim();
                Intent intent = new Intent(context, ImageScreen.class);
                intent.putExtra("url",url);
                context.startActivity(intent);
            }
        });
        holder.ivCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(arrayList.get(position).getMemberNumber() != null){
                   Intent intent = new Intent(Intent.ACTION_DIAL);
                   intent.setData(Uri.parse("tel:" + arrayList.get(position).getMemberNumber().substring(4).trim()));
                   context.startActivity(intent);
               }
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Call Member").setAction("Call Member")
                        .setLabel("Call Member").build());
            }
        });

        holder.ivCrossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.get(position).getMemberNumber() != null)
                    ((XCheckPoolFragmentActivty)context).sendRemoveMemberRequest(arrayList.get(position).getMemberNumber(),arrayList.get(position).getMemeberName());

            }
        });
        if(isComeFromHistory){
            holder.ivCrossBtn.setVisibility(View.INVISIBLE);
            holder.ivCallBtn.setVisibility(View.INVISIBLE);
            holder.ivCallBtn.setOnClickListener(null);
            holder.ivCrossBtn.setOnClickListener(null);

        }else {
            holder.ivCrossBtn.setVisibility(View.VISIBLE);
            holder.ivCallBtn.setVisibility(View.VISIBLE);

        }
        return convertView;
    }

    /* private view holder class */
    private class ViewHolder {
        private TextView tvMemberName, tvStatus;
        private ImageView ivCallBtn, ivCrossBtn,ivMemberImage;
        public ViewHolder(View view) {
            tvMemberName = (TextView)view.findViewById(R.id.tvMemberName);
            tvStatus = (TextView)view.findViewById(R.id.tvStatus);
            ivCallBtn = (ImageView) view.findViewById(R.id.ivCallBtn);
            ivCrossBtn = (ImageView) view.findViewById(R.id.ivCrossBtn);
            ivMemberImage = (ImageView) view.findViewById(R.id.ivMemberImage);

            tvMemberName.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));
            tvStatus.setTypeface(FontTypeface.getTypeface(context, AppConstants.HELVITICA));


        }
    }

}