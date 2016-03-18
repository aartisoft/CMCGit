package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.clubmycab.CircularImageView;
import com.clubmycab.ContactObject;
import com.clubmycab.ContactsAdapter;
import com.clubmycab.ContactsListClass;
import com.clubmycab.MembersClubsShowAdaptor;
import com.clubmycab.MyClubsShowAdaptor;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.model.ContactData;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class MyClubsActivity extends Activity implements
		AsyncTaskResultListener {

	ListView lv, lvmyclub, listMembersclubs;
	Button newclub;
	ContactsAdapter objAdapter;

	String OwnerNumber;
	String FullName;

	String Result;
	String referralResult;

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	ImageView sidemenu;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;

	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView mypreferences;
	TextView about;

	private ArrayList<String> ClubName = new ArrayList<String>();
	private ArrayList<String> NewClub = new ArrayList<String>();
	private ArrayList<String> ClubMember = new ArrayList<String>();
	private ArrayList<String> MembersNumber = new ArrayList<String>();

	ArrayList<String> selectednames = new ArrayList<String>();
	ArrayList<String> selectednumbers = new ArrayList<String>();

	ArrayList<String> namearray = new ArrayList<String>();
	ArrayList<String> phonenoarray = new ArrayList<String>();
	ArrayList<String> imagearray = new ArrayList<String>();

	ArrayList<String> namearraynew = new ArrayList<String>();
	ArrayList<String> phonenoarraynew = new ArrayList<String>();
	ArrayList<String> imagearraynew = new ArrayList<String>();

	ArrayList<String> shownames = new ArrayList<String>();
	ArrayList<String> shownumbers = new ArrayList<String>();
	ArrayList<String> showimagenames = new ArrayList<String>();
	ArrayList<String> showpoolid = new ArrayList<String>();

	ListView listmyclubpopup;
	Mypopuplistadapter clubadaptor;

	ListView listmemberclubpopup;
	Memberpopuplistadapter membersclubadaptor;

	String strSelected;
	Dialog meradialog;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	String readunreadnotiresp;
	Bitmap mIcon11;
	String imagenameresp;
	String poolresponse;

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

	RelativeLayout myclubsrl;

	String comefrom, comefromfirstlogin;

	Tracker tracker;
	boolean exceptioncheck = false;
	private static final int CREATE_NEW_GRP_REQUEST = 101;
	private static final int ADD_MORE_MEMBER_REQUEST = 102;
	private static final int REFER_MEMBER_FOR_CLUB_REQUEST = 103;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myclubs);

		comefrom = null;
		Intent intent = getIntent();
		comefrom = intent.getStringExtra("comefrom");

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MyClubsActivity.this);
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							finish();

							startActivity(intent);

						}
					});

			builder.show();
			return;
		}

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(MyClubsActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyClubs");

		myclubsrl = (RelativeLayout) findViewById(R.id.myclubsrl);
		myclubsrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("myclubsrl", "myclubsrl");
			}
		});

		if (comefrom != null) {

			if (comefrom.equalsIgnoreCase("GCM")) {

				String nid = intent.getStringExtra("nid");
				String params = "rnum=" + "&nid=" + nid + "&auth="
						+ GlobalMethods.calculateCMCAuthString(nid);
				String endpoint = GlobalVariables.ServiceUrl
						+ "/UpdateNotificationStatusToRead.php";
				Log.d("MyClubMyActivity",
						"UpdateNotificationStatusToRead endpoint : " + endpoint
								+ " params : " + params);
				new GlobalAsyncTask(this, endpoint, params, null, this, false,
						"UpdateNotificationStatusToRead", false);

			}

		}

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		OwnerNumber = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(MyClubsActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		lvmyclub = (ListView) findViewById(R.id.listMyclubs);
		listMembersclubs = (ListView) findViewById(R.id.listMembersclubs);

		lv = (ListView) findViewById(R.id.listMyclubs);
		newclub = (Button) findViewById(R.id.butmakenewclubs);

		newclub.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		Cursor cursor = null;
		try {
			cursor = MyClubsActivity.this.getContentResolver().query(
					Phone.CONTENT_URI, null, null, null, null);
			int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
			int imageIdx = cursor.getColumnIndex(Phone.CONTACT_ID);

			cursor.moveToFirst();
			do {

				if (cursor.getString(phoneNumberIdx).length() > 10) {
					namearray.add(cursor.getString(nameIdx));
					String phonenumbercapt = cursor.getString(phoneNumberIdx);

					String phoneStr = phonenumbercapt.replaceAll("\\D+", "");

					String phonesub = null;
					if (phoneStr.length() > 10) {
						phonesub = phoneStr.substring(phoneStr.length() - 10,
								phoneStr.length());
					} else {
						phonesub = phoneStr;
					}

					phonenoarray.add(phonesub);
					imagearray.add(cursor.getString(imageIdx));
				}

			} while (cursor.moveToNext());

			Log.d("name", "" + namearray);
			Log.d("phoneNumber", "" + phonenoarray);
			Log.d("imagearray", "" + imagearray);

			Log.d("name count", "" + namearray.size());
			Log.d("phoneNumber count", "" + phonenoarray.size());
			Log.d("imagearray count", "" + imagearray.size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		HashSet set = new HashSet();
		for (int i = 0; i < phonenoarray.size(); i++) {
			boolean val = set.add(phonenoarray.get(i));
			if (val == false) {

			} else {
				namearraynew.add(namearray.get(i));
				phonenoarraynew.add(phonenoarray.get(i));
				imagearraynew.add(imagearray.get(i));
			}
		}

		Log.d("namearraynew", "" + namearraynew);
		Log.d("phonenoarraynew", "" + phonenoarraynew);
		Log.d("imagearraynew", "" + imagearraynew);

		Log.d("namearraynew count", "" + namearraynew.size());
		Log.d("phonenoarraynew count", "" + phonenoarraynew.size());
		Log.d("imagearraynew count", "" + imagearraynew.size());

		newclub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	showAlertDialog();
					showGroupNameDialog();
			}
		});
		
		comefromfirstlogin = getIntent().getStringExtra("comefromfirstlogin");
		if (comefromfirstlogin != null && comefromfirstlogin.length() > 0) {
			newclub.performClick();
		}

		// ///////////////
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new ConnectionTaskForreadunreadnotification()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new ConnectionTaskForreadunreadnotification().execute();
		// }

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imgname = mPrefs111.getString("imgname", "");
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

			// new ConnectionTaskForfetchimagename().execute();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForfetchimagename()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForfetchimagename().execute();
			}

		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

		// try {
		// showclub();
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchClubs().execute();
		}
	}

	private void showAlertDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.activity_newclub);

		ListView lv = (ListView) dialog.findViewById(R.id.listView_newclub);
		Button MakeClub = (Button) dialog.findViewById(R.id.but_newclub);
		final EditText ClubNames = (EditText) dialog
				.findViewById(R.id.editTextnewclub);
		final EditText searchfromlist = (EditText) dialog
				.findViewById(R.id.searchfromlist);

		ContactsListClass.phoneList.clear();

		for (int i = 0; i < namearraynew.size(); i++) {

			ContactObject cp = new ContactObject();

			cp.setName(namearraynew.get(i));
			cp.setNumber(phonenoarraynew.get(i));
			cp.setImage(imagearraynew.get(i));
			cp.setAppUserimagename("contacticon.png");

			ContactsListClass.phoneList.add(cp);
		}

		Collections.sort(ContactsListClass.phoneList,
				new Comparator<ContactObject>() {
					@Override
					public int compare(ContactObject lhs, ContactObject rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

		objAdapter = new ContactsAdapter(MyClubsActivity.this,
				ContactsListClass.phoneList);
		lv.setAdapter(objAdapter);

		searchfromlist.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				String text = searchfromlist.getText().toString()
						.toLowerCase(Locale.getDefault());
				objAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox chk = (CheckBox) view.findViewById(R.id.contactcheck);
				ContactObject bean = ContactsListClass.phoneList.get(position);
				if (bean.isSelected()) {
					bean.setSelected(false);
					chk.setChecked(false);
				} else {
					bean.setSelected(true);
					chk.setChecked(true);
				}

			}
		});

		MakeClub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cname = ClubNames.getText().toString().trim();
				if (cname.isEmpty() || cname == null
						|| cname.equalsIgnoreCase("")) {

					Toast.makeText(MyClubsActivity.this,
							"Please enter the group name", Toast.LENGTH_LONG)
							.show();
				} else {

					// TODO Auto-generated method stub
					selectednames.clear();
					selectednumbers.clear();

					searchfromlist.setText("");

					// Retrive Data from list
					for (ContactObject bean : ContactsListClass.phoneList) {

						if (bean.isSelected()) {
							selectednames.add(bean.getName());
							selectednumbers.add("0091" + bean.getNumber());
						}

					}

					if (selectednames.size() > 0) {

						Log.d("selectednames", "" + selectednames);
						Log.d("selectednumbers", "" + selectednumbers);

						dialog.dismiss();

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForcreatingNewClub()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											FullName, OwnerNumber, ClubNames
													.getText().toString(),
											selectednames.toString(),
											selectednumbers.toString());
						} else {
							new ConnectionTaskForcreatingNewClub().execute(
									FullName, OwnerNumber, ClubNames.getText()
											.toString(), selectednames
											.toString(), selectednumbers
											.toString());
						}
					} else {
						Toast.makeText(MyClubsActivity.this,
								"Please select contact(s) to create club",
								Toast.LENGTH_LONG).show();
					}

				}
			}

		});

		dialog.show();
	}

	public void showclub() throws JSONException {

		SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
		String clubs = mPrefs11111.getString("clubs", "");

		if (clubs.equalsIgnoreCase("No Users of your Club")) {
			Toast.makeText(MyClubsActivity.this, "No groups created yet!!",
					Toast.LENGTH_LONG).show();
		} else {

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

			JSONArray subArray = new JSONArray(clubs);

			for (int i = 0; i < subArray.length(); i++) {

				if (subArray.getJSONObject(i).getString("IsPoolOwner")
						.toString().trim().equalsIgnoreCase("1")) {
					MyClubPoolId.add(subArray.getJSONObject(i)
							.getString("PoolId").toString());
					MyClubPoolName.add(subArray.getJSONObject(i)
							.getString("PoolName").toString());
					// Pawan cheks NoofMember value for null
					if (subArray.getJSONObject(i).getString("NoofMembers")
							.toString().equalsIgnoreCase("null"))
						MyClubNoofMembers.add("1");

					else
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

					// Pawan cheks NoofMember value for null
					if (subArray.getJSONObject(i).getString("NoofMembers")
							.toString().equalsIgnoreCase("null"))
						MemberClubNoofMembers.add("1");

					else
						MemberClubNoofMembers.add(subArray.getJSONObject(i)
								.getString("NoofMembers").toString());

					MemberClubOwnerName.add(subArray.getJSONObject(i)
							.getString("OwnerName").toString());
					MemberClubMembers.add(subArray.getJSONObject(i)
							.getString("Members").toString());
				}
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

			MyClubsShowAdaptor adapter = new MyClubsShowAdaptor(
					MyClubsActivity.this, MyClubPoolId, MyClubPoolName,
					MyClubNoofMembers, MyClubOwnerName);
			lvmyclub.setAdapter(adapter);
			lvmyclub.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					shownames.clear();
					shownumbers.clear();
					showimagenames.clear();
					showpoolid.clear();

					JSONArray subArray;
					try {
						subArray = new JSONArray(MyClubMembers.get(position)
								.toString().trim());
						if (subArray.length() > 0) {
							for (int i = 0; i < subArray.length(); i++) {
								shownames.add(subArray.getJSONObject(i)
										.getString("FullName").toString());
								shownumbers.add(subArray.getJSONObject(i)
										.getString("MemberNumber").toString());
								showimagenames.add(subArray.getJSONObject(i)
										.getString("ImageName").toString());
								showpoolid.add(MyClubPoolId.get(position)
										.toString().trim());
							}
						} else {
							showpoolid.add(MyClubPoolId.get(position)
									.toString());
						}

						Log.d("shownames", "" + shownames);
						Log.d("shownumbers", "" + shownumbers);
						Log.d("showimagenames", "" + showimagenames);
						Log.d("showpoolid", "" + showpoolid);
						// if (shownames.size() < 1) {
						//
						// Toast.makeText(MyClubsActivity.this,
						// StringTags.TAG_DOSE_NOT_HAVE_MEMBER,
						// Toast.LENGTH_SHORT).show();
						// }
						//
						// else
						ShowAlert(shownames, shownumbers,
								MyClubPoolName.get(position), showpoolid,
								showimagenames);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			MembersClubsShowAdaptor adapter1 = new MembersClubsShowAdaptor(
					MyClubsActivity.this, MemberClubPoolId, MemberClubPoolName,
					MemberClubNoofMembers, MemberClubOwnerName);
			listMembersclubs.setAdapter(adapter1);

			listMembersclubs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					shownames.clear();
					shownumbers.clear();
					showimagenames.clear();
					showpoolid.clear();

					JSONArray subArray;
					try {
						subArray = new JSONArray(MemberClubMembers
								.get(position).toString().trim());

						if (subArray.length() > 0) {
							for (int i = 0; i < subArray.length(); i++) {
								shownames.add(subArray.getJSONObject(i)
										.getString("FullName").toString());
								shownumbers.add(subArray.getJSONObject(i)
										.getString("MemberNumber").toString());
								showimagenames.add(subArray.getJSONObject(i)
										.getString("ImageName").toString());
								showpoolid.add(MemberClubPoolId.get(position)
										.toString().trim());
							}
						} else {
							showpoolid.add(MemberClubPoolId.get(position)
									.toString());
						}

						Log.d("shownames", "" + shownames);
						Log.d("shownumbers", "" + shownumbers);
						Log.d("showpoolid", "" + showpoolid);

						ShowAlertformembersclubs(shownames, shownumbers,
								MemberClubPoolName.get(position), showpoolid,
								showimagenames);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		}
	}

	// ////////////////////////////
	public void ShowAlertformembersclubs(ArrayList<String> shownames2,
			ArrayList<String> shownum, final String cname,
			final ArrayList<String> poolids, ArrayList<String> imagenames) {
		// TODO Auto-generated method stub

		meradialog = new Dialog(this);
		meradialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		meradialog.setContentView(R.layout.memberclublistpopup);

		TextView clubnameinpopup = (TextView) meradialog
				.findViewById(R.id.memberclubnameinpopup);
		clubnameinpopup.setText(cname);

		listmemberclubpopup = (ListView) meradialog
				.findViewById(R.id.listmemberclubpopup);

		ArrayList<String> arrayList1 = new ArrayList<String>();
		ArrayList<String> arrayList2 = new ArrayList<String>();
		ArrayList<String> arrayList3 = new ArrayList<String>();
		ArrayList<String> arrayList4 = new ArrayList<String>();
		for (int i = 0; i < shownames2.size(); i++) {
			if (shownames2.get(i) != null
					&& !shownames2.get(i).equalsIgnoreCase("null")) {
				arrayList1.add(shownames2.get(i));
				arrayList2.add(shownum.get(i));
				arrayList3.add(poolids.get(i));
				arrayList4.add(imagenames.get(i));
			}
		}

		membersclubadaptor = new Memberpopuplistadapter(arrayList1, arrayList2,
				arrayList3, arrayList4);
		// membersclubadaptor = new Memberpopuplistadapter(shownames2, shownum,
		// poolids);
		listmemberclubpopup.setAdapter(membersclubadaptor);

		Button memberrefermorecontacts = (Button) meradialog
				.findViewById(R.id.memberrefermorecontacts);
		memberrefermorecontacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedPoolId = poolids.get(0).toString().trim();
				selectedPoolName = cname;
				if(meradialog != null){
					meradialog.dismiss();
				}
				Intent intent = new Intent(MyClubsActivity.this, SendInvitesToOtherScreen.class);
				intent.putExtra("activity_id", SendInvitesToOtherScreen.MY_CLUB_ACTIVITY_ID);
				startActivityForResult(intent, REFER_MEMBER_FOR_CLUB_REQUEST);
				//ReferMembersForClub(poolids.get(0).toString().trim(), cname);
			}
		});

		meradialog.show();

	}

	// //////////////////////////

	public void ShowAlert(ArrayList<String> shownames2,
			ArrayList<String> shownum, final String cname,
			final ArrayList<String> poolids, ArrayList<String> imgnames) {
		// TODO Auto-generated method stub

		meradialog = new Dialog(this);
		meradialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		meradialog.setContentView(R.layout.myclublistpopup);

		TextView clubnameinpopup = (TextView) meradialog
				.findViewById(R.id.clubnameinpopup);
		clubnameinpopup.setText(cname);

		listmyclubpopup = (ListView) meradialog
				.findViewById(R.id.listmyclubpopup);

		clubadaptor = new Mypopuplistadapter(shownames2, shownum, poolids,
				imgnames);
		listmyclubpopup.setAdapter(clubadaptor);

		Button addmorecontacts = (Button) meradialog
				.findViewById(R.id.addmorecontacts);
		addmorecontacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedPoolId = poolids.get(0).toString().trim();
				selectedPoolName = cname;
				if(meradialog != null){
					meradialog.dismiss();
				}
				Intent intent = new Intent(MyClubsActivity.this, SendInvitesToOtherScreen.class);
				intent.putExtra("activity_id", SendInvitesToOtherScreen.MY_CLUB_ACTIVITY_ID);
				startActivityForResult(intent, ADD_MORE_MEMBER_REQUEST);
			//	Addmorememberstoclub(, clubname)

			}
		});

		meradialog.show();

	}

	private void Addmorememberstoclub(final String poolid, final String clubname) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.selectrecipientspopup);

		ListView lv = (ListView) dialog.findViewById(R.id.listView_selectrecp);
		Button done = (Button) dialog.findViewById(R.id.selectrecpdonebtn);
		final EditText searchfromlist = (EditText) dialog
				.findViewById(R.id.searchfromlist);

		ContactsListClass.phoneList.clear();

		for (int i = 0; i < namearraynew.size(); i++) {

			ContactObject cp = new ContactObject();

			cp.setName(namearraynew.get(i));
			cp.setNumber(phonenoarraynew.get(i));
			cp.setImage(imagearraynew.get(i));
			cp.setAppUserimagename("contacticon.png");

			ContactsListClass.phoneList.add(cp);
		}

		Collections.sort(ContactsListClass.phoneList,
				new Comparator<ContactObject>() {
					@Override
					public int compare(ContactObject lhs, ContactObject rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

		objAdapter = new ContactsAdapter(MyClubsActivity.this,
				ContactsListClass.phoneList);
		lv.setAdapter(objAdapter);

		searchfromlist.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				String text = searchfromlist.getText().toString()
						.toLowerCase(Locale.getDefault());
				objAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox chk = (CheckBox) view.findViewById(R.id.contactcheck);
				ContactObject bean = ContactsListClass.phoneList.get(position);
				if (bean.isSelected()) {
					bean.setSelected(false);
					chk.setChecked(false);
				} else {
					bean.setSelected(true);
					chk.setChecked(true);
				}

			}
		});

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectednames.clear();
				selectednumbers.clear();

				searchfromlist.setText("");

				// Retrive Data from list
				for (ContactObject bean : ContactsListClass.phoneList) {

					if (bean.isSelected()) {
						selectednames.add(bean.getName());
						selectednumbers.add("0091" + bean.getNumber());
					}

				}

				if (selectednames.size() > 0) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForAddmoreuserstoclub()
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, poolid,
										selectednames.toString().trim(),
										selectednumbers.toString().trim());
					} else {
						new ConnectionTaskForAddmoreuserstoclub().execute(
								poolid, selectednames.toString().trim(),
								selectednumbers.toString().trim());
					}

					dialog.dismiss();
					meradialog.dismiss();

					String toaststr = selectednumbers.size()
							+ " friend(s) added to " + clubname + " group";
					Toast.makeText(MyClubsActivity.this, "" + toaststr,
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(MyClubsActivity.this,
							"Please select contact(s) to create group",
							Toast.LENGTH_LONG).show();
				}

			}

		});

		dialog.show();
	}

	// //////////////////////////////

	private void ReferMembersForClub(final String poolid, final String clubname) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.selectrecipientspopup);

		ListView lv = (ListView) dialog.findViewById(R.id.listView_selectrecp);
		Button done = (Button) dialog.findViewById(R.id.selectrecpdonebtn);
		final EditText searchfromlist = (EditText) dialog
				.findViewById(R.id.searchfromlist);

		ContactsListClass.phoneList.clear();

		for (int i = 0; i < namearraynew.size(); i++) {

			ContactObject cp = new ContactObject();

			cp.setName(namearraynew.get(i));
			cp.setNumber(phonenoarraynew.get(i));
			cp.setImage(imagearraynew.get(i));
			cp.setAppUserimagename("contacticon.png");

			ContactsListClass.phoneList.add(cp);
		}

		Collections.sort(ContactsListClass.phoneList,
				new Comparator<ContactObject>() {
					@Override
					public int compare(ContactObject lhs, ContactObject rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

		objAdapter = new ContactsAdapter(MyClubsActivity.this,
				ContactsListClass.phoneList);
		lv.setAdapter(objAdapter);

		searchfromlist.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				String text = searchfromlist.getText().toString()
						.toLowerCase(Locale.getDefault());
				objAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox chk = (CheckBox) view.findViewById(R.id.contactcheck);
				ContactObject bean = ContactsListClass.phoneList.get(position);
				if (bean.isSelected()) {
					bean.setSelected(false);
					chk.setChecked(false);
				} else {
					bean.setSelected(true);
					chk.setChecked(true);
				}

			}
		});

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectednames.clear();
				selectednumbers.clear();

				searchfromlist.setText("");

				// Retrive Data from list
				for (ContactObject bean : ContactsListClass.phoneList) {

					if (bean.isSelected()) {
						selectednames.add(bean.getName());
						selectednumbers.add("0091" + bean.getNumber());
					}

				}

				if (selectednames.size() > 0) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForReferfriends().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, poolid);
					} else {
						new ConnectionTaskForReferfriends().execute(poolid);
					}

					dialog.dismiss();
					meradialog.dismiss();

					String toaststr = selectednumbers.size()
							+ " friend(s) refered to " + clubname + " group";
					Toast.makeText(MyClubsActivity.this, "" + toaststr,
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(MyClubsActivity.this,
							"Please select contact(s) to refer",
							Toast.LENGTH_LONG).show();
				}

			}

		});

		dialog.show();
	}

	// //////////////////////////////
	private class ConnectionTaskForReferfriends extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionReferfriends mAuth1 = new AuthenticateConnectionReferfriends();
			try {
				mAuth1.ClubId = args[0];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionReferfriends {

		public String ClubId;

		public AuthenticateConnectionReferfriends() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Refer Friend Club")
					.setAction("Refer Friend Club")
					.setLabel("Refer Friend Club").build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/referFriendStepOne.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"ClubId", ClubId);
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", FullName);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", OwnerNumber);
			BasicNameValuePair ReferedUserNameBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserName", selectednames.toString());
			BasicNameValuePair ReferedUserNumberBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserNumber", selectednumbers.toString());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(ReferedUserNameBasicNameValuePair);
			nameValuePairList.add(ReferedUserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String referfriendresponse = "";
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				referfriendresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("MyClubsActivity", "referFriendStepOne : "
					+ referfriendresponse);
		}
	}

	// //////////////////////////////
	public class Memberpopuplistadapter extends BaseAdapter {

		// Declare Variables

		ArrayList<String> name;
		ArrayList<String> number;
		ArrayList<String> poolid;
		ArrayList<String> imgnames;

		public Memberpopuplistadapter(ArrayList<String> Name,
				ArrayList<String> Number, ArrayList<String> pids,
				ArrayList<String> imnms) {
			this.name = Name;
			this.number = Number;
			this.poolid = pids;
			this.imgnames = imnms;
		}

		@Override
		public int getCount() {
			return name.size();
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

			// Declare Variables
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View single_row = inflater.inflate(
					R.layout.memberclublistpopup_item, null, true);

			ImageView memberappuserimg = (ImageView) single_row
					.findViewById(R.id.memberappuserimg);
			TextView membertextmembername_popup = (TextView) single_row
					.findViewById(R.id.membertextmembername_popup);
			TextView membertextmembernum_popup = (TextView) single_row
					.findViewById(R.id.membertextmembernum_popup);

			if (name.get(position) == null
					|| name.get(position).equalsIgnoreCase("null")) {
				// membertextmembername_popup.setText(number.get(position));
				// membertextmembernum_popup.setText(number.get(position));
				memberappuserimg.setVisibility(View.GONE);
			} else {
				membertextmembername_popup.setText(name.get(position));
				// membertextmembernum_popup.setText(number.get(position));
				memberappuserimg.setVisibility(View.VISIBLE);
				if (imgnames.get(position).toString().trim() == null
						|| imgnames.get(position).toString().trim().isEmpty()
						|| imgnames.get(position).toString().trim().equals("")) {

					memberappuserimg.setImageDrawable(getResources()
							.getDrawable(R.drawable.cabappicon));
				} else {
					AQuery aq = new AQuery(MyClubsActivity.this);
					String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
							+ imgnames.get(position).toString().trim();

					aq.id(memberappuserimg).image(url, true, true);
				}
			}

			return single_row;
		}
	}

	// ///////
	// ////////////////////////////

	public class Mypopuplistadapter extends BaseAdapter {

		// Declare Variables

		ArrayList<String> name;
		ArrayList<String> number;
		ArrayList<String> poolid;
		ArrayList<String> imgnames;

		public Mypopuplistadapter(ArrayList<String> Name,
				ArrayList<String> Number, ArrayList<String> pids,
				ArrayList<String> imgnm) {
			this.name = Name;
			this.number = Number;
			this.poolid = pids;
			this.imgnames = imgnm;

		}

		@Override
		public int getCount() {
			return name.size();
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

			// Declare Variables
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View single_row = inflater.inflate(R.layout.myclublistpopup_item,
					null, true);

			ImageView appuserimg = (ImageView) single_row
					.findViewById(R.id.appuserimg);
			TextView Mname = (TextView) single_row
					.findViewById(R.id.textmembername_popup);
			TextView Mnum = (TextView) single_row
					.findViewById(R.id.textmembernum_popup);
			ImageView removeuser = (ImageView) single_row
					.findViewById(R.id.removeuser);

			Mnum.setVisibility(View.GONE);

			if (name.get(position) == null
					|| name.get(position).equalsIgnoreCase("null")) {

				if (phonenoarraynew.indexOf(number.get(position).toString()
						.trim().substring(4)) != -1) {

					Mname.setText(namearraynew.get(phonenoarraynew
							.indexOf(number.get(position).toString().trim()
									.substring(4))));
				} else {
					Mname.setText(number.get(position));
				}

				// Mnum.setText(number.get(position));
				appuserimg.setVisibility(View.INVISIBLE);
			} else {
				Mname.setText(name.get(position));
				// Mnum.setText(number.get(position));
				appuserimg.setVisibility(View.VISIBLE);

				if (imgnames.get(position).toString().trim() == null
						|| imgnames.get(position).toString().trim().isEmpty()
						|| imgnames.get(position).toString().trim().equals("")) {

					appuserimg.setImageDrawable(getResources().getDrawable(
							R.drawable.cabappicon));
				} else {
					AQuery aq = new AQuery(MyClubsActivity.this);
					String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
							+ imgnames.get(position).toString().trim();

					aq.id(appuserimg).image(url, true, true);
				}
			}

			removeuser.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MyClubsActivity.this);
					String str;
					if (name.get(position) == null
							|| name.get(position).equalsIgnoreCase("null")) {

						str = "Are you sure you Want to delete "
								+ number.get(position) + " from this group?";
					} else {

						str = "Are you sure you Want to delete "
								+ name.get(position) + " from this group?";
					}

					builder.setMessage(str);
					builder.setCancelable(true);
					builder.setPositiveButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
					builder.setNegativeButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										new ConnectionTaskForRemoveuserfromclub()
												.executeOnExecutor(
														AsyncTask.THREAD_POOL_EXECUTOR,
														poolid.get(position),
														number.get(position));
									} else {
										new ConnectionTaskForRemoveuserfromclub()
												.execute(poolid.get(position),
														number.get(position));
									}

									shownames.remove(position);
									shownumbers.remove(position);
									showimagenames.remove(position);
									showpoolid.remove(position);

									clubadaptor = new Mypopuplistadapter(
											shownames, shownumbers, showpoolid,
											showimagenames);
									listmyclubpopup.setAdapter(clubadaptor);

									if (shownumbers.size() > 0) {

									} else {
										meradialog.dismiss();
									}
								}
							});
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				}
			});

			return single_row;
		}
	}

	// ///////

	private class ConnectionTaskForRemoveuserfromclub extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionRemoveuserfromclub mAuth1 = new AuthenticateConnectionRemoveuserfromclub();
			try {
				mAuth1.poolid = args[0];
				mAuth1.usernumber = args[1];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// shownames.clear();
			// shownumbers.clear();
			// showpoolid.clear();
			//
			// for (int i = 0; i < ClubMember.size(); i++) {
			// String cname = ClubMember.get(i).toString().split("\\|")[0]
			// .trim();
			// String mnum = ClubMember.get(i).toString().split("\\|")[1]
			// .trim();
			// String fname = ClubMember.get(i).toString().split("\\|")[2]
			// .trim();
			// String poolid = ClubMember.get(i).toString().split("\\|")[4]
			// .trim();
			// if (cname.equalsIgnoreCase(strSelected)) {
			//
			// shownames.add(fname);
			// shownumbers.add(mnum);
			// showpoolid.add(poolid);
			//
			// }
			// }
			//

		}

	}

	public class AuthenticateConnectionRemoveuserfromclub {

		public String poolid;
		public String usernumber;

		public AuthenticateConnectionRemoveuserfromclub() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Remove Member Club")
					.setAction("Remove Member Club")
					.setLabel("Remove Member Club").build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/removeuserfromclub.php";

			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
					"poolid", poolid.toString().trim());
			BasicNameValuePair usernumberBasicNameValuePair = new BasicNameValuePair(
					"usernumber", usernumber.toString().trim());

			String authString = poolid.toString().trim()
					+ usernumber.toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(poolidBasicNameValuePair);
			nameValuePairList.add(usernumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String poolresponse = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());

			if (poolresponse != null && poolresponse.length() > 0
					&& poolresponse.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "poolresponse Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			// /////////////
			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select1 = GlobalVariables.ServiceUrl + "/Fetch_Club.php";

			HttpPost httpPost1 = new HttpPost(url_select1);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);
			authString = OwnerNumber;
			authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));
			nameValuePairList.add(authValuePair);

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair);
			nameValuePairList1.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;
			String myclubsresp = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myclubsresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			if (myclubsresp != null && myclubsresp.length() > 0
					&& myclubsresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myclubsresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// /////////////////////////////////
	// ///////

	private class ConnectionTaskForAddmoreuserstoclub extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyClubsActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionAddmoreuserstoclub mAuth1 = new AuthenticateConnectionAddmoreuserstoclub();
			try {
				mAuth1.poolid = args[0];
				mAuth1.names = args[1];
				mAuth1.numbers = args[2];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionAddmoreuserstoclub {

		public String poolid;
		public String names;
		public String numbers;

		public AuthenticateConnectionAddmoreuserstoclub() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Add Member Club")
					.setAction("Add Member Club").setLabel("Add Member Club")
					.build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();

			String url_select1 = GlobalVariables.ServiceUrl
					+ "/addmoreuserstoclub.php";

			HttpPost httpPost = new HttpPost(url_select1);

			BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
					"poolid", poolid);
			BasicNameValuePair namesBasicNameValuePair = new BasicNameValuePair(
					"ClubMembersName", names);
			BasicNameValuePair numbersBasicNameValuePair = new BasicNameValuePair(
					"ClubMembersNumber", numbers);

			String authString = names + numbers + poolid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(poolidBasicNameValuePair);
			nameValuePairList.add(namesBasicNameValuePair);
			nameValuePairList.add(numbersBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String poolresponse = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());

			if (poolresponse != null && poolresponse.length() > 0
					&& poolresponse.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "poolresponse Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			// /////////////
			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/Fetch_Club.php";

			HttpPost httpPost1 = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);
			authString = OwnerNumber;
			authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair);
			nameValuePairList1.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;
			String myclubsresp = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myclubsresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			if (myclubsresp != null && myclubsresp.length() > 0
					&& myclubsresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myclubsresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// //////////////
	public class ClubDeleteAdaptor extends BaseAdapter {

		// Declare Variables

		ArrayList<String> cname;

		public ClubDeleteAdaptor(ArrayList<String> Name) {
			this.cname = Name;

		}

		@Override
		public int getCount() {
			return cname.size();
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

			// Declare Variables
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View single_row = inflater.inflate(R.layout.myclubs_listrow, null,
					true);

			TextView Mname = (TextView) single_row
					.findViewById(R.id.nameofclub);
			ImageView removeclub = (ImageView) single_row
					.findViewById(R.id.removeclub);

			Mname.setText(cname.get(position));
			Mname.setTypeface(Typeface.createFromAsset(getAssets(),
					"NeutraText-Light.ttf"));

			removeclub.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MyClubsActivity.this);
					builder.setMessage("Want to delete this group?");
					builder.setCancelable(true);
					builder.setPositiveButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
					builder.setNegativeButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									ArrayList<String> poolidselected = new ArrayList<String>();
									String selectclubname = NewClub
											.get(position).toString().trim();

									for (int i = 0; i < ClubMember.size(); i++) {
										String cname = ClubMember.get(i)
												.toString().split("\\|")[0]
												.trim();
										String mnum = ClubMember.get(i)
												.toString().split("\\|")[1]
												.trim();
										String fname = ClubMember.get(i)
												.toString().split("\\|")[2]
												.trim();
										String poolid = ClubMember.get(i)
												.toString().split("\\|")[4]
												.trim();
										if (cname
												.equalsIgnoreCase(selectclubname)) {

											poolidselected.add(poolid);

										}
									}

									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										new ConnectionTaskForRemoveclub()
												.executeOnExecutor(
														AsyncTask.THREAD_POOL_EXECUTOR,
														poolidselected.get(0));
									} else {
										new ConnectionTaskForRemoveclub()
												.execute(poolidselected.get(0));
									}
								}
							});
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				}
			});

			return single_row;
		}
	}

	// ///////

	// ///////

	private class ConnectionTaskForRemoveclub extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyClubsActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionRemoveclub mAuth1 = new AuthenticateConnectionRemoveclub();
			try {
				mAuth1.poolid = args[0];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionRemoveclub {

		public String poolid;

		public AuthenticateConnectionRemoveclub() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Delete Club").setAction("Delete Club")
					.setLabel("Delete Club").build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select1 = GlobalVariables.ServiceUrl + "/removeclub.php";

			HttpPost httpPost = new HttpPost(url_select1);

			BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
					"poolid", poolid);

			String authString = poolid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(poolidBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String poolresponse = "";

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());

			if (poolresponse != null && poolresponse.length() > 0
					&& poolresponse.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "poolresponse Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			// /////////////
			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/Fetch_Club.php";

			HttpPost httpPost1 = new HttpPost(url_select11);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);
			authString = OwnerNumber;
			authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair);
			nameValuePairList1.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;
			String myclubsresp = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myclubsresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			if (myclubsresp != null && myclubsresp.length() > 0
					&& myclubsresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myclubsresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// /////////////////////////////////
	// ///////
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (comefrom != null) {
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			MyClubsActivity.this.finish();
		} else {
			Intent mainIntent = new Intent(MyClubsActivity.this,
					HomeCarPoolActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

	// ///////
	// private class ConnectionTaskForreadunreadnotification extends
	// AsyncTask<String, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	//
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionreadunreadnotification mAuth1 = new
	// AuthenticateConnectionreadunreadnotification();
	// try {
	// mAuth1.connection();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// exceptioncheck = true;
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void v) {
	//
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(MyClubsActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// if (readunreadnotiresp.equalsIgnoreCase("0")) {
	//
	// unreadnoticountrl.setVisibility(View.GONE);
	//
	// } else {
	//
	// unreadnoticountrl.setVisibility(View.VISIBLE);
	// unreadnoticount.setText(readunreadnotiresp);
	// }
	// }
	//
	// }
	//
	// public class AuthenticateConnectionreadunreadnotification {
	//
	// public AuthenticateConnectionreadunreadnotification() {
	//
	// }
	//
	// public void connection() throws Exception {
	//
	// // Connect to google.com
	// HttpClient httpClient = new DefaultHttpClient();
	// String url_select11 = GlobalVariables.ServiceUrl
	// + "/FetchUnreadNotificationCount.php";
	//
	// HttpPost httpPost = new HttpPost(url_select11);
	// BasicNameValuePair MobileNumberBasicNameValuePair = new
	// BasicNameValuePair(
	// "MobileNumber", OwnerNumber);
	//
	// List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	// nameValuePairList.add(MobileNumberBasicNameValuePair);
	//
	// UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
	// nameValuePairList);
	// httpPost.setEntity(urlEncodedFormEntity);
	// HttpResponse httpResponse = httpClient.execute(httpPost);
	//
	// Log.d("httpResponse", "" + httpResponse);
	//
	// InputStream inputStream = httpResponse.getEntity().getContent();
	// InputStreamReader inputStreamReader = new InputStreamReader(
	// inputStream);
	//
	// BufferedReader bufferedReader = new BufferedReader(
	// inputStreamReader);
	//
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// String bufferedStrChunk = null;
	//
	// while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
	// readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
	// .toString();
	// }
	//
	// Log.d("readunreadnotiresp", "" + readunreadnotiresp);
	//
	// }
	// }

	// ////////////////////////
	// ///////
	private class ConnectionTaskForfetchimagename extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionfetchimagename mAuth1 = new AuthenticateConnectionfetchimagename();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (imagenameresp == null) {

				profilepic.setImageResource(R.drawable.cabappicon);
				drawerprofilepic.setImageResource(R.drawable.cabappicon);

			} else if (imagenameresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "imagenameresp Unauthorized Access");
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);
			}

		}
	}

	public class AuthenticateConnectionfetchimagename {

		public AuthenticateConnectionfetchimagename() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/fetchimagename.php";

			HttpPost httpPost11 = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair(
					"MobileNumber", OwnerNumber);

			String authString = OwnerNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);
			nameValuePairList11.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.d("httpResponse", "" + httpResponse11);

			InputStream inputStream11 = httpResponse11.getEntity().getContent();
			InputStreamReader inputStreamReader11 = new InputStreamReader(
					inputStream11);

			BufferedReader bufferedReader11 = new BufferedReader(
					inputStreamReader11);

			StringBuilder stringBuilder11 = new StringBuilder();

			String bufferedStrChunk11 = null;

			while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
				imagenameresp = stringBuilder11.append(bufferedStrChunk11)
						.toString();
			}

			Log.d("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else if (imagenameresp.contains("Unauthorized Access")) {

			} else {

				String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ imagenameresp;
				String urldisplay = url1.toString().trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imagenameresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}

		}
	}

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// ////////////
	// /////////////////////////////////
	// ///////

	private class ConnectionTaskForcreatingNewClub extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyClubsActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectioncreatingNewClub mAuth1 = new AuthenticateConnectioncreatingNewClub();
			try {
				mAuth1.ownname = args[0];
				mAuth1.ownnum = args[1];
				mAuth1.cname = args[2];
				mAuth1.cmemnames = args[3];
				mAuth1.cmemnums = args[4];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			Toast.makeText(getApplicationContext(), "" + poolresponse,
					Toast.LENGTH_LONG).show();

			try {
				showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectioncreatingNewClub {

		public String ownname;
		public String ownnum;
		public String cname;
		public String cmemnames;
		public String cmemnums;

		public AuthenticateConnectioncreatingNewClub() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Create Club").setAction("Create Club")
					.setLabel("Create Club").build());

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/Store_Club.php";
			HttpPost httpPost = new HttpPost(url_select11);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair OwnerNameValuePair = new BasicNameValuePair(
					"OwnerName", ownname);
			nameValuePairList.add(OwnerNameValuePair);

			BasicNameValuePair OwnerNumberValuePair = new BasicNameValuePair(
					"OwnerNumber", ownnum);
			nameValuePairList.add(OwnerNumberValuePair);

			BasicNameValuePair ClubnameValuePair = new BasicNameValuePair(
					"ClubName", cname);
			nameValuePairList.add(ClubnameValuePair);

			BasicNameValuePair ClubMembersNameValuePair = new BasicNameValuePair(
					"ClubMembersName", cmemnames);
			nameValuePairList.add(ClubMembersNameValuePair);

			BasicNameValuePair ClubMembersNumberValuePair = new BasicNameValuePair(
					"ClubMembersNumber", cmemnums);
			nameValuePairList.add(ClubMembersNumberValuePair);

			String authString = cmemnames + cmemnums + cname + ownname + ownnum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + poolresponse);

			if (poolresponse != null && poolresponse.length() > 0
					&& poolresponse.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "poolresponse Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			// /////////////
			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select111 = GlobalVariables.ServiceUrl
					+ "/Fetch_Club.php";

			HttpPost httpPost1 = new HttpPost(url_select111);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);
			authString = OwnerNumber;
			authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(UserNumberBasicNameValuePair);
			nameValuePairList1.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.d("httpResponse", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;
			String myclubsresp = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				myclubsresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			if (myclubsresp != null && myclubsresp.length() > 0
					&& myclubsresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myclubsresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	private class ConnectionTaskForFetchClubs extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyClubs mAuth1 = new AuthenticateConnectionFetchMyClubs();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(MyClubsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				showclub();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class AuthenticateConnectionFetchMyClubs {

		public AuthenticateConnectionFetchMyClubs() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/Fetch_Club.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerNumber);

			String authString = OwnerNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String myprofileresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myclubsresp", "" + myprofileresp);

			if (myprofileresp != null && myprofileresp.length() > 0
					&& myprofileresp.contains("Unauthorized Access")) {
				Log.e("MyClubsActivity", "myprofileresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(MyClubsActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myprofileresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void getResult(String response, String uniqueID) {
		// TODO Auto-generated method stub

	}
	
	/*
	 *______________________________________VD______________________________
	 */
	private Dialog askGrpDialog;
	private String groupName, selectedPoolId, selectedPoolName;

	private void showGroupNameDialog(){
		if(askGrpDialog == null){
			askGrpDialog = new Dialog(MyClubsActivity.this);
			askGrpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			askGrpDialog.setContentView(R.layout.dialog_ask_grp_name);
			askGrpDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			askGrpDialog.findViewById(R.id.tvOk).setOnClickListener(dialogClickListener);
			askGrpDialog.findViewById(R.id.tvCancel).setOnClickListener(dialogClickListener);
		}
		if(!askGrpDialog.isShowing()){
			((EditText)askGrpDialog.findViewById(R.id.etGrpName)).setText("");
			askGrpDialog.show();
		}
	}
	
	private OnClickListener dialogClickListener = new OnClickListener() {
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tvOk:
				if(!TextUtils.isEmpty(((EditText)askGrpDialog.findViewById(R.id.etGrpName)).getText().toString().trim())){
					askGrpDialog.dismiss();
					groupName = ((EditText)askGrpDialog.findViewById(R.id.etGrpName)).getText().toString().trim();
					Intent intent = new Intent(MyClubsActivity.this, SendInvitesToOtherScreen.class);
					intent.putExtra("activity_id", SendInvitesToOtherScreen.MY_CLUB_ACTIVITY_ID);
					startActivityForResult(intent, CREATE_NEW_GRP_REQUEST);
				}else {
					((EditText)askGrpDialog.findViewById(R.id.etGrpName)).setError("Please enter group name");
				}
				break;
				
			case R.id.tvCancel:
				askGrpDialog.dismiss();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CREATE_NEW_GRP_REQUEST){
			if(resultCode == RESULT_OK){
				if (data.getExtras().getBoolean("iscontactslected")) {
					Log.d("", "");
					ArrayList<ContactData> myList = data.getExtras()
							.getParcelableArrayList("Contact_list");
					if (myList != null && myList.size() > 0) {
						sendCreateGrpRequest(
							myList);
					}
				} 
			}
		}else if(requestCode == ADD_MORE_MEMBER_REQUEST){
			if(resultCode == RESULT_OK){
				if (data.getExtras().getBoolean("iscontactslected")) {
					Log.d("", "");
					ArrayList<ContactData> myList = data.getExtras()
							.getParcelableArrayList("Contact_list");
					if (myList != null && myList.size() > 0) {
						sendAddMoreMemeberReq(
							myList);
					}
				} 
			}
		}else if(requestCode == REFER_MEMBER_FOR_CLUB_REQUEST){
			if(resultCode == RESULT_OK){
				if (data.getExtras().getBoolean("iscontactslected")) {
					Log.d("", "");
					ArrayList<ContactData> myList = data.getExtras()
							.getParcelableArrayList("Contact_list");
					if (myList != null && myList.size() > 0) {
						sendReferMemberClubReq(
							myList);
					}
				} 
			}
		}
		
	};
	
	private void sendCreateGrpRequest(ArrayList<ContactData> contactList){
			selectednames.clear();
			selectednumbers.clear();
			HashMap<String, String> map = new HashMap<String, String>();
			for (ContactData bean : contactList) {
				// duplicacy check, my number check is left currently
				map.put(bean.getPhoneNumber().replace(" ", ""), bean.getName());
				L.mesaage(bean.getPhoneNumber().length()+"");
			}
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String number = String.valueOf(pair.getKey());
				int length = number.length();
				L.mesaage(length+"");
					it.remove(); // avoids a ConcurrentModificationException
				selectednames.add((String) pair.getValue());
				selectednumbers.add("0091"
						+ number.substring(number.length()-10));

			}
			L.mesaage(selectednames.toString() + " , "
					+ selectednumbers.toString());
			if (selectednames != null && selectednames.size() > 0) {

				Log.d("selectednames", "" + selectednames);
				Log.d("selectednumbers", "" + selectednumbers);


				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForcreatingNewClub()
							.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									FullName, OwnerNumber, groupName
											,
									selectednames.toString(),
									selectednumbers.toString());
				} else {
					new ConnectionTaskForcreatingNewClub().execute(
							FullName, OwnerNumber, groupName, selectednames
									.toString(), selectednumbers
									.toString());
				}
			} else {
				Toast.makeText(MyClubsActivity.this,
						"Please select contact(s) to create club",
						Toast.LENGTH_LONG).show();
			}

		
	}
	
	private void sendAddMoreMemeberReq(ArrayList<ContactData> contactList){
		selectednames.clear();
		selectednumbers.clear();
		HashMap<String, String> map = new HashMap<String, String>();
		for (ContactData bean : contactList) {
			// duplicacy check, my number check is left currently
			map.put(bean.getPhoneNumber().replace(" ", ""), bean.getName());
			L.mesaage(bean.getPhoneNumber().length()+"");
		}
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String number = String.valueOf(pair.getKey());
			int length = number.length();
			L.mesaage(length+"");
				it.remove(); // avoids a ConcurrentModificationException
			selectednames.add((String) pair.getValue());
			selectednumbers.add("0091"
					+ number.substring(number.length()-10));

		}
		L.mesaage(selectednames.toString() + " , "
				+ selectednumbers.toString());

		if (selectednames != null && selectednames.size() > 0) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForAddmoreuserstoclub()
						.executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, selectedPoolId,
								selectednames.toString().trim(),
								selectednumbers.toString().trim());
			} else {
				new ConnectionTaskForAddmoreuserstoclub().execute(
						selectedPoolId, selectednames.toString().trim(),
						selectednumbers.toString().trim());
			}


			String toaststr = selectednumbers.size()
					+ " friend(s) added to " + selectedPoolName + " group";
			Toast.makeText(MyClubsActivity.this, "" + toaststr,
					Toast.LENGTH_LONG).show();

		} else {
			Toast.makeText(MyClubsActivity.this,
					"Please select contact(s) to create group",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private void sendReferMemberClubReq(ArrayList<ContactData> contactList){
		selectednames.clear();
		selectednumbers.clear();
		HashMap<String, String> map = new HashMap<String, String>();
		for (ContactData bean : contactList) {
			// duplicacy check, my number check is left currently
			map.put(bean.getPhoneNumber().replace(" ", ""), bean.getName());
			L.mesaage(bean.getPhoneNumber().length()+"");
		}
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String number = String.valueOf(pair.getKey());
			int length = number.length();
			L.mesaage(length+"");
				it.remove(); // avoids a ConcurrentModificationException
			selectednames.add((String) pair.getValue());
			selectednumbers.add("0091"
					+ number.substring(number.length()-10));

		}
		L.mesaage(selectednames.toString() + " , "
				+ selectednumbers.toString());
		if (selectednames.size() > 0) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForReferfriends().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, selectedPoolId);
			} else {
				new ConnectionTaskForReferfriends().execute(selectedPoolId);
			}

		

			String toaststr = selectednumbers.size()
					+ " friend(s) refered to " + selectedPoolName + " group";
			Toast.makeText(MyClubsActivity.this, "" + toaststr,
					Toast.LENGTH_LONG).show();

		} else {
			Toast.makeText(MyClubsActivity.this,
					"Please select contact(s) to refer",
					Toast.LENGTH_LONG).show();
		}
	}




}
