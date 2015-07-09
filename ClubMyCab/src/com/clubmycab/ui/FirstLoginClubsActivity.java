package com.clubmycab.ui;

import java.util.ArrayList;

import org.json.JSONArray;

import FetchClubHandler.FetchClubHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class FirstLoginClubsActivity extends Activity implements
		AsyncTaskResultListener {

	String FullName;
	String MobileNumber;

	TextView textViewMessage;
	ListView listViewClubs;
	Button buttonCreateClub, buttonContinue;

	ArrayList<String> MyClubPoolId = new ArrayList<String>();
	ArrayList<String> MyClubPoolName = new ArrayList<String>();
	ArrayList<String> MyClubNoofMembers = new ArrayList<String>();
	ArrayList<String> MyClubOwnerName = new ArrayList<String>();
	ArrayList<String> MyClubMembers = new ArrayList<String>();

	ArrayList<String> MemberClubPoolId = new ArrayList<String>();
	ArrayList<String> MemberClubPoolName = new ArrayList<String>();
	ArrayList<String> MemberClubNoofMembers = new ArrayList<String>();
	ArrayList<String> MemberClubOwnerName = new ArrayList<String>();
	ArrayList<String> MemberClubMembers = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_login_club);

		textViewMessage = (TextView) findViewById(R.id.textMessageFirstLoginClub);

		listViewClubs = (ListView) findViewById(R.id.listViewFirstLoginClubs);

		buttonCreateClub = (Button) findViewById(R.id.buttonFLCCreateClub);
		buttonContinue = (Button) findViewById(R.id.buttonFLCContinue);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		String endpoint = GlobalVariables.ServiceUrl + "/Fetch_Club.php";
		String params = "OwnerNumber=" + MobileNumber;

		// move this call to wallets page
		new GlobalAsyncTask(this, endpoint, params, new FetchClubHandler(),
				this, true);
	}

	@Override
	public void getResult(int result, String response) {

		Log.d("FirstLoginClubsActivity", "getResult : " + response);
		
		SharedPreferences sharedPreferences1 = getSharedPreferences(
				"MyClubs", 0);
		SharedPreferences.Editor editor1 = sharedPreferences1.edit();
		editor1.putString("clubs", response.toString().trim());
		editor1.commit();

		if (response.equalsIgnoreCase("No Users of your Club")) {
			textViewMessage.setText(getResources().getString(
					R.string.text_firstloginclubs_notmember));
			buttonCreateClub.setText(getResources().getString(
					R.string.button_flc_create_club_notmember));
			buttonContinue.setText(getResources().getString(
					R.string.button_flc_continue_notmember));
		} else {
			textViewMessage.setText(getResources().getString(
					R.string.text_firstloginclubs_member));
			buttonCreateClub.setText(getResources().getString(
					R.string.button_flc_create_club));
			buttonContinue.setText(getResources().getString(
					R.string.button_flc_continue));

			MyClubPoolId.clear();
			MyClubPoolName.clear();
			MyClubNoofMembers.clear();
			MyClubOwnerName.clear();
			MyClubMembers.clear();

			MemberClubPoolId.clear();
			MemberClubPoolName.clear();
			MemberClubNoofMembers.clear();
			MemberClubOwnerName.clear();
			MemberClubMembers.clear();

			try {
				JSONArray subArray = new JSONArray(response);

				for (int i = 0; i < subArray.length(); i++) {

					if (subArray.getJSONObject(i).getString("IsPoolOwner")
							.toString().trim().equalsIgnoreCase("1")) {
						MyClubPoolId.add(subArray.getJSONObject(i)
								.getString("PoolId").toString());
						MyClubPoolName.add(subArray.getJSONObject(i)
								.getString("PoolName").toString());
						MyClubNoofMembers.add(subArray.getJSONObject(i)
								.getString("NoofMembers").toString());
						MyClubOwnerName.add(subArray.getJSONObject(i)
								.getString("OwnerName").toString());
						MyClubMembers.add(subArray.getJSONObject(i)
								.getString("Members").toString());
					} else {
						MemberClubPoolId.add(subArray.getJSONObject(i)
								.getString("PoolId").toString());
						MemberClubPoolName.add(subArray.getJSONObject(i)
								.getString("PoolName").toString());
						MemberClubNoofMembers.add(subArray.getJSONObject(i)
								.getString("NoofMembers").toString());
						MemberClubOwnerName.add(subArray.getJSONObject(i)
								.getString("OwnerName").toString());
						MemberClubMembers.add(subArray.getJSONObject(i)
								.getString("Members").toString());
					}
				}

				MembersClubsAdaptor adapter = new MembersClubsAdaptor(
						FirstLoginClubsActivity.this, MemberClubPoolId,
						MemberClubPoolName, MemberClubNoofMembers,
						MemberClubOwnerName);
				listViewClubs.setAdapter(adapter);

			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("MyClubPoolId", "" + MyClubPoolId);
			Log.d("MyClubPoolName", "" + MyClubPoolName);
			Log.d("MyClubNoofMembers", "" + MyClubNoofMembers);
			Log.d("MyClubOwnerName", "" + MyClubOwnerName);
			Log.d("MyClubMembers", "" + MyClubMembers);

			Log.d("MemberClubPoolId", "" + MemberClubPoolId);
			Log.d("MemberClubPoolName", "" + MemberClubPoolName);
			Log.d("MemberClubNoofMembers", "" + MemberClubNoofMembers);
			Log.d("MemberClubOwnerName", "" + MemberClubOwnerName);
			Log.d("MemberClubMembers", "" + MemberClubMembers);

		}

		buttonCreateClub.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(FirstLoginClubsActivity.this,
						MyClubsActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		buttonContinue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(FirstLoginClubsActivity.this,
						HomeActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

	}

	private class MembersClubsAdaptor extends BaseAdapter {

		// Declare Variables
		Context context;
		ArrayList<String> MemberClubPoolId;
		ArrayList<String> MemberClubPoolName;
		ArrayList<String> MemberClubNoofMembers;
		ArrayList<String> MemberClubOwnerName;
		LayoutInflater inflater;

		public MembersClubsAdaptor(Context context,
				ArrayList<String> memclubpoolid,
				ArrayList<String> memclubpoolname,
				ArrayList<String> memclubnoofmembers,
				ArrayList<String> memclubownername) {
			this.context = context;
			this.MemberClubPoolId = memclubpoolid;
			this.MemberClubPoolName = memclubpoolname;
			this.MemberClubNoofMembers = memclubnoofmembers;
			this.MemberClubOwnerName = memclubownername;
		}

		@Override
		public int getCount() {
			return MemberClubPoolId.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View itemView = inflater.inflate(R.layout.membersclubs_listrow,
					parent, false);

			// Locate the TextViews in listview_item.xml

			TextView Mname = (TextView) itemView.findViewById(R.id.nameofclub);
			TextView noofmembers = (TextView) itemView
					.findViewById(R.id.noofmembers);
			TextView clubownername = (TextView) itemView
					.findViewById(R.id.clubownername);
			ImageView removeclub = (ImageView) itemView
					.findViewById(R.id.removeclub);

			Mname.setText(MemberClubPoolName.get(position).toString().trim());
			clubownername
					.setText("("
							+ MemberClubOwnerName.get(position).toString()
									.trim() + ")");
			noofmembers.setText("("
					+ MemberClubNoofMembers.get(position).toString().trim()
					+ ")");

			removeclub.setVisibility(View.GONE);

			return itemView;
		}
	}

}
