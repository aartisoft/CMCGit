package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CircularImageView;
import com.clubmycab.ClubListClass;
import com.clubmycab.ClubMemberAdapter;
import com.clubmycab.ClubObject;
import com.clubmycab.ClubsAdaptor;
import com.clubmycab.Communicator;
import com.clubmycab.ContactObject;
import com.clubmycab.ContactsAdapter;
import com.clubmycab.ContactsListClass;
import com.clubmycab.R;
import com.clubmycab.UpcomingStartTripAlarm;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class ContactsInviteForRideActivity extends Activity {

	LinearLayout clubcontactslistll;
	ListView contactslist;

	LinearLayout mainclublistll;
	private ListView listMyclubs;
	ListView listMembersclubs;

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;

	// Button contactsbtn;
	// Button myclubbtn;
	TextView validmobiletxt;

	Button sendtocontacts;

	String FullName;
	String MobileNumberstr;

	ArrayList<String> namearray = new ArrayList<String>();
	ArrayList<String> phonenoarray = new ArrayList<String>();
	ArrayList<String> imagearray = new ArrayList<String>();

	ArrayList<String> namearraynew = new ArrayList<String>();
	ArrayList<String> phonenoarraynew = new ArrayList<String>();
	ArrayList<String> imagearraynew = new ArrayList<String>();

	ArrayList<String> AppUsersfullnamearr = new ArrayList<String>();
	ArrayList<String> AppUsersmobilenumberarr = new ArrayList<String>();
	ArrayList<String> AppUsersimagearr = new ArrayList<String>();

	EditText searchfromlist;
	ContactsAdapter objAdapter;

	String fromcome;
	String CabId;
	String OwnerMobileNumber;
	String OwnerName;
	String FromLocation;
	String ToLocation;
	String TravelDate;
	String TravelTime;
	String Seats;
	String fromshortname;
	String toshortname;

	int flag = 0;

	String sendres;
	String appusers;

	ArrayList<String> selectednames = new ArrayList<String>();
	ArrayList<String> selectednumbers = new ArrayList<String>();

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Boolean clubcreated;
	Boolean appusersavailable;

	String readunreadnotiresp;
	Bitmap mIcon11;
	String fetchclubresp;
	String fetchappusersresp;

	Boolean conitnuechk = false;

	AlertDialog dialogseats;

	RelativeLayout contexthelpcontacts;
	String distancetext;

	String imagenameresp;

	RelativeLayout contactsmyclubrl;
	Tracker tracker;

	boolean exceptioncheck = false;

	// Pawan
	public static ClubsAdaptor adapterClubMy;
	public static ClubMemberAdapter adapterClubMember;
	public static boolean selction = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		adapterClubMy = null;
		adapterClubMember = null;

		setContentView(R.layout.activity_contacts_invite_ride);

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ContactsInviteForRideActivity.this);
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
				.getInstance(ContactsInviteForRideActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Create Invitation for Ride");

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		contactsmyclubrl = (RelativeLayout) findViewById(R.id.contactsmyclubrl);
		contactsmyclubrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("contactsmyclubrl", "contactsmyclubrl");
			}
		});

		Bundle extras = getIntent().getExtras();
		fromcome = extras.getString("fromcome");
		CabId = extras.getString("CabId");
		OwnerName = extras.getString("OwnerName");
		OwnerMobileNumber = extras.getString("MobileNumber");
		FromLocation = extras.getString("FromLocation");
		ToLocation = extras.getString("ToLocation");
		TravelDate = extras.getString("TravelDate");
		TravelTime = extras.getString("TravelTime");
		Seats = extras.getString("Seats");
		fromshortname = extras.getString("fromshortname");
		toshortname = extras.getString("toshortname");

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

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

				Intent mainIntent = new Intent(
						ContactsInviteForRideActivity.this,
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

		// //////////////////

		// contactsbtn = (Button) findViewById(R.id.contactsbtn);
		// myclubbtn = (Button) findViewById(R.id.myclubbtn);
		sendtocontacts = (Button) findViewById(R.id.sendbtn);
		validmobiletxt = (TextView) findViewById(R.id.validmobiletxt);

		clubcontactslistll = (LinearLayout) findViewById(R.id.clubcontactslistll);
		contactslist = (ListView) findViewById(R.id.contactslist);

		mainclublistll = (LinearLayout) findViewById(R.id.mainclublistll);
		listMyclubs = (ListView) findViewById(R.id.listMyclubs);
		listMembersclubs = (ListView) findViewById(R.id.listMembersclubs);

		searchfromlist = (EditText) findViewById(R.id.searchfromlist);

		// contactsbtn.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// myclubbtn.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		sendtocontacts.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		clubcontactslistll.setVisibility(View.GONE);
		searchfromlist.setVisibility(View.GONE);
		validmobiletxt.setVisibility(View.GONE);
		mainclublistll.setVisibility(View.VISIBLE);
		// contactsbtn.setVisibility(View.GONE);

		sendtocontacts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						ContactsInviteForRideActivity.this,
						R.anim.button_click_anim);
				sendtocontacts.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						searchfromlist.setText("");

						selectednames.clear();
						selectednumbers.clear();

						// Retrive Data from list
						if (flag == 1) {
							for (ContactObject bean : ContactsListClass.phoneList) {

								if (bean.isSelected()) {
									selectednames.add(bean.getName());
									selectednumbers.add("0091"
											+ bean.getNumber());
								}
							}
						}

						if (flag == 0) {

							for (ClubObject bean : ClubListClass.ClubList) {

								if (bean.isSelected()) {

									JSONArray subArray;
									try {
										subArray = new JSONArray(bean
												.getClubmembers().toString()
												.trim());
										for (int i = 0; i < subArray.length(); i++) {
											selectednames.add(subArray
													.getJSONObject(i)
													.getString("FullName")
													.toString().trim());
											selectednumbers.add(subArray
													.getJSONObject(i)
													.getString("MemberNumber")
													.toString().trim());
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}

							for (ClubObject bean1 : ClubListClass.MemberClubList) {

								if (bean1.isSelected()) {

									JSONArray subArray;
									try {
										subArray = new JSONArray(bean1
												.getClubmembers().toString()
												.trim());
										for (int i = 0; i < subArray.length(); i++) {

											if (subArray.getJSONObject(i)
													.getString("FullName")
													.toString().trim() == null
													|| subArray
															.getJSONObject(i)
															.getString(
																	"FullName")
															.toString()
															.trim()
															.equalsIgnoreCase(
																	"null")) {

											} else {
												selectednames.add(subArray
														.getJSONObject(i)
														.getString("FullName")
														.toString().trim());
												selectednumbers.add(subArray
														.getJSONObject(i)
														.getString(
																"MemberNumber")
														.toString().trim());
											}
										}

										selectednames.add(subArray
												.getJSONObject(0)
												.getString("OwnerName")
												.toString().trim());
										selectednumbers.add(subArray
												.getJSONObject(0)
												.getString("OwnerNumber")
												.toString().trim());

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}

							Object[] st = selectednumbers.toArray();
							for (Object s : st) {

								if (selectednumbers.indexOf(s) != selectednumbers
										.lastIndexOf(s)) {
									selectednames.remove(selectednumbers
											.lastIndexOf(s));
									selectednumbers.remove(selectednumbers
											.lastIndexOf(s));
								}
							}

							if (selectednumbers.indexOf(MobileNumberstr) != -1) {
								selectednames.remove(selectednumbers
										.indexOf(MobileNumberstr));
								selectednumbers.remove(selectednumbers
										.indexOf(MobileNumberstr));
							}

						}

						if (selectednames.size() > 0) {

							Log.d("selectednames", "" + selectednames);
							Log.d("selectednumbers", "" + selectednumbers);

							tracker.send(new HitBuilders.EventBuilder()
									.setCategory("Invite").setAction("Invite")
									.setLabel("Invite").build());

							if (selectednames.size() >= Integer.parseInt(Seats)) {
								conitnuechk = false;

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskForSendInvite()
											.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								} else {
									new ConnectionTaskForSendInvite().execute();
								}

							} else {

								conitnuechk = true;

								AlertDialog.Builder builder = new AlertDialog.Builder(
										ContactsInviteForRideActivity.this);
								builder.setMessage("You have "
										+ Seats
										+ " seats to share and have selected only "
										+ selectednames.size() + " friend(s)");
								builder.setCancelable(true);
								builder.setPositiveButton("Continue Anyways",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
													new ConnectionTaskForSendInvite()
															.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
												} else {
													new ConnectionTaskForSendInvite()
															.execute();
												}
											}
										});
								dialogseats = builder.show();
								TextView messageText = (TextView) dialogseats
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialogseats.show();
							}

						} else {
							Toast.makeText(ContactsInviteForRideActivity.this,
									"Please select Clubs/Contacts to invite",
									Toast.LENGTH_LONG).show();
						}

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		// Pawan
		adapterClubMy = new ClubsAdaptor(ContactsInviteForRideActivity.this,
				ClubListClass.ClubList, true);
		listMyclubs.setAdapter(adapterClubMy);
		// listMyclubs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listMyclubs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub

				ClubObject bean = ClubListClass.ClubList.get(position);
				bean.setSelected(true);

				int count = Integer.parseInt(bean.getNoofMembers());

				if (count <= 1) {

					Toast.makeText(ContactsInviteForRideActivity.this,
							StringTags.TAG_DOSE_NOT_HAVE_MEMBER,
							Toast.LENGTH_SHORT).show();
				} else {

					for (int i = 0; i < ClubListClass.ClubList.size(); i++) {

						if (i == position)
							continue;

						bean = ClubListClass.ClubList.get(i);
						bean.setSelected(false);
					}
					// Remove all memberclib list selection
					for (int i = 0; i < ClubListClass.MemberClubList.size(); i++) {

						bean = ClubListClass.MemberClubList.get(i);
						bean.setSelected(false);
					}

					adapterClubMember.setSelectedIndex(-1);
					adapterClubMember.notifyDataSetChanged();

					// if (bean.isSelected()) {
					// bean.setSelected(false);
					// chk.setChecked(false);
					// } else {
					// bean.setSelected(true);
					// chk.setChecked(true);
					// }
					adapterClubMy.setSelectedIndex(position);
					adapterClubMy.notifyDataSetChanged();
				}

			}
		});

		adapterClubMember = new ClubMemberAdapter(
				ContactsInviteForRideActivity.this,
				ClubListClass.MemberClubList, true);

		listMembersclubs.setAdapter(adapterClubMember);
		listMembersclubs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub

				ClubObject bean = ClubListClass.MemberClubList.get(position);
				bean.setSelected(true);

				int count = Integer.parseInt(bean.getNoofMembers());

				if (count <= 1) {

					Toast.makeText(ContactsInviteForRideActivity.this,
							StringTags.TAG_DOSE_NOT_HAVE_MEMBER,
							Toast.LENGTH_SHORT).show();
				} else {

					for (int i = 0; i < ClubListClass.MemberClubList.size(); i++) {
						bean = ClubListClass.MemberClubList.get(i);

						if (i == position) {
							bean.setSelected(true);
						}

						else
							bean.setSelected(false);
					}

					// Unselect all MyClub
					for (int i = 0; i < ClubListClass.ClubList.size(); i++) {

						bean = ClubListClass.ClubList.get(i);
						bean.setSelected(false);

					}

					adapterClubMy.setSelectedIndex(-1);
					adapterClubMy.notifyDataSetChanged();
					// if (bean.isSelected()) {
					// bean.setSelected(false);
					// chk.setChecked(false);
					// } else {
					// bean.setSelected(true);
					// chk.setChecked(true);
					// }

					adapterClubMember.setSelectedIndex(position);
					adapterClubMember.notifyDataSetChanged();
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("ContactsInviteForRideActivity", "onResume");

		getClubs();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (conitnuechk) {

			dialogseats.dismiss();
		} else {

			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			ContactsInviteForRideActivity.this.finish();
		}
	}

	private void getClubs() {

		ClubListClass.ClubList.clear();
		ClubListClass.MemberClubList.clear();

		SharedPreferences mPrefs111111 = getSharedPreferences("MyClubs", 0);
		String clubs1 = mPrefs111111.getString("clubs", "");

		Log.d("ContactsInviteForRideActivity", "getClubs : " + clubs1);

		if (clubs1.equalsIgnoreCase("No Users of your Club")) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ContactsInviteForRideActivity.this);
			builder.setMessage("You are not a member of any club yet. Would you like to create one now?");
			builder.setCancelable(false);

			builder.setPositiveButton("Yes, Create club",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(
									ContactsInviteForRideActivity.this,
									MyClubsActivity.class);
							intent.putExtra("comefrom", "comefrom");
							startActivity(intent);
						}
					});

			builder.setNegativeButton("No, Invite contacts",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							flag = 1;

							// myclubbtn.setVisibility(View.GONE);
							// contactsbtn.setVisibility(View.VISIBLE);

							clubcontactslistll.setVisibility(View.VISIBLE);
							searchfromlist.setVisibility(View.VISIBLE);
							validmobiletxt.setVisibility(View.VISIBLE);
							mainclublistll.setVisibility(View.GONE);

							getContacts();

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
										public int compare(ContactObject lhs,
												ContactObject rhs) {
											return lhs.getName().compareTo(
													rhs.getName());
										}
									});

							objAdapter = new ContactsAdapter(
									ContactsInviteForRideActivity.this,
									ContactsListClass.phoneList);
							contactslist.setAdapter(objAdapter);
							contactslist
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent,
												View view, int position, long id) {

											CheckBox chk = (CheckBox) view
													.findViewById(R.id.contactcheck);
											ContactObject bean = ContactsListClass.phoneList
													.get(position);
											if (bean.isSelected()) {
												bean.setSelected(false);
												chk.setChecked(false);
											} else {
												bean.setSelected(true);
												chk.setChecked(true);
											}

										}
									});

							searchfromlist
									.addTextChangedListener(new TextWatcher() {

										@Override
										public void onTextChanged(
												CharSequence cs, int arg1,
												int arg2, int arg3) {
											// When user changed the Text
											String text = searchfromlist
													.getText()
													.toString()
													.toLowerCase(
															Locale.getDefault());
											objAdapter.filter(text);
										}

										@Override
										public void beforeTextChanged(
												CharSequence arg0, int arg1,
												int arg2, int arg3) {
											// TODO Auto-generated method stub

										}

										@Override
										public void afterTextChanged(
												Editable arg0) {
											// TODO Auto-generated method stub
										}
									});

						}
					});

			builder.show();
		} else {

			try {

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

				JSONArray subArray = new JSONArray(clubs1);

				for (int i = 0; i < subArray.length(); i++) {

					if (subArray.getJSONObject(i).getString("IsPoolOwner")
							.toString().trim().equalsIgnoreCase("1")) {
						MyClubPoolId.add(subArray.getJSONObject(i)
								.getString("PoolId").toString());
						MyClubPoolName.add(subArray.getJSONObject(i)
								.getString("PoolName").toString());

						// Pawan cheks for null
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

				if (MyClubPoolName.size() > 0) {

					flag = 0;

					// contactsbtn.setVisibility(View.GONE);

					for (int i = 0; i < MyClubPoolName.size(); i++) {

						ClubObject cp = new ClubObject();

						cp.setName(MyClubPoolName.get(i).toString().trim());
						cp.setClubmembers(MyClubMembers.get(i).toString()
								.trim());

						cp.setNoofMembers(MyClubNoofMembers.get(i).toString()
								.trim());

						cp.setClubOwnerName("");

						ClubListClass.ClubList.add(cp);
					}

					adapterClubMy.notifyDataSetChanged();
					// make adapter only ont time then refressh on in onresume

					// final ClubsAdaptor adapter = new ClubsAdaptor(
					// ContactsInviteForRideActivity.this,
					// ClubListClass.ClubList);
					// listMyclubs.setAdapter(adapter);
					// listMyclubs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					// listMyclubs
					// .setOnItemClickListener(new OnItemClickListener() {
					//
					// @Override
					// public void onItemClick(AdapterView<?> parent,
					// View v, int position, long id) {
					// // TODO Auto-generated method stub
					// // CheckBox chk = (CheckBox) v
					// // .findViewById(R.id.myclubcheckBox);
					// // ClubObject bean = ClubListClass.ClubList
					// // .get(position);
					//
					// // if (bean.isSelected()) {
					// // bean.setSelected(false);
					// // chk.setChecked(false);
					// // } else {
					// // bean.setSelected(true);
					// // chk.setChecked(true);
					// // }
					// adapter.setSelectedIndex(position);
					// adapter.notifyDataSetChanged();
					//
					// }
					// });
				}

				if (MemberClubPoolName.size() > 0) {

					for (int i = 0; i < MemberClubPoolName.size(); i++) {

						ClubObject cp = new ClubObject();

						cp.setName(MemberClubPoolName.get(i).toString().trim());
						cp.setClubmembers(MemberClubMembers.get(i).toString()
								.trim());

						cp.setNoofMembers(MemberClubNoofMembers.get(i)
								.toString().trim());

						cp.setClubOwnerName(MemberClubOwnerName.get(i)
								.toString().trim());

						ClubListClass.MemberClubList.add(cp);
					}

					// Make adapter only one time then refresh only when item
					// value updated
					adapterClubMember.notifyDataSetChanged();

					// ClubsAdaptor adapter = new ClubsAdaptor(
					// ContactsInviteForRideActivity.this,
					// ClubListClass.MemberClubList);
					//
					// listMembersclubs.setAdapter(adapter);
					// listMembersclubs
					// .setOnItemClickListener(new OnItemClickListener() {
					//
					// @Override
					// public void onItemClick(AdapterView<?> parent,
					// View v, int position, long id) {
					// // TODO Auto-generated method stub
					// CheckBox chk = (CheckBox) v
					// .findViewById(R.id.myclubcheckBox);
					// ClubObject bean = ClubListClass.MemberClubList
					// .get(position);
					//
					// if (bean.isSelected()) {
					// bean.setSelected(false);
					// chk.setChecked(false);
					// } else {
					// bean.setSelected(true);
					// chk.setChecked(true);
					// }
					//
					// }
					// });
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void getContacts() {
		// /// For contacts list

		Cursor cursor = null;
		try {
			cursor = ContactsInviteForRideActivity.this.getContentResolver()
					.query(Phone.CONTENT_URI, null, null, null, null);
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
	}

	private class ConnectionTaskForSendInvite extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				ContactsInviteForRideActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSendInvite mAuth1 = new AuthenticateConnectionSendInvite();
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

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(ContactsInviteForRideActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ContactsInviteForRideActivity.this);
			builder.setMessage("Your friend(s) have been informed about the ride! We will let you know when they join. Sit back & relax!");
			builder.setCancelable(false);

			builder.setPositiveButton("I'm done here",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							scheduleUpcomingTripNotification();
							scheduleStartTripNotification();

							SharedPreferences sharedPreferences1 = getSharedPreferences(
									"QuitApplication", 0);
							SharedPreferences.Editor editor1 = sharedPreferences1
									.edit();
							editor1.putBoolean("quitapplication", true);
							editor1.commit();

							Intent mainIntent = new Intent(
									ContactsInviteForRideActivity.this,
									HomeActivity.class);
							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(mainIntent);
						}
					});

			builder.setNegativeButton("Start over again",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							scheduleUpcomingTripNotification();
							scheduleStartTripNotification();

							Intent mainIntent = new Intent(
									ContactsInviteForRideActivity.this,
									HomeActivity.class);
							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivityForResult(mainIntent, 500);
							overridePendingTransition(R.anim.slide_in_right,
									R.anim.slide_out_left);
						}
					});

			builder.show();
		}

	}

	public class AuthenticateConnectionSendInvite {

		public AuthenticateConnectionSendInvite() {

		}

		public void connection() throws Exception {

			String source = FromLocation.replaceAll(" ", "%20");
			String dest = ToLocation.replaceAll(" ", "%20");

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ source
					+ "&destination="
					+ dest
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="
					+ GlobalVariables.GoogleMapsAPIKey;

			Log.d("url", "" + url);

			String CompletePageResponse = new Communicator()
					.executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			String distancevalue = null;
			distancetext = null;

			String durationvalue = null;
			String durationtext = null;

			for (int i = 0; i < subArray.length(); i++) {

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					String startadd = subArray1.getJSONObject(i1)
							.getString("distance").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					distancevalue = jsonObject1.getString("value");
					distancetext = jsonObject1.getString("text");

					String startadd1 = subArray1.getJSONObject(i1)
							.getString("duration").toString();

					JSONObject jsonObject11 = new JSONObject(startadd1);
					durationvalue = jsonObject11.getString("value");
					durationtext = jsonObject11.getString("text");
				}
			}

			Log.d("distancevalue", "" + distancevalue);
			Log.d("distancetext", "" + distancetext);

			Log.d("durationvalue", "" + durationvalue);
			Log.d("durationtext", "" + durationtext);

			String msg = FullName + " invited you to share a cab from "
					+ fromshortname + " to " + toshortname;

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/openacab.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair FromLocationBasicNameValuePair = new BasicNameValuePair(
					"FromLocation", FromLocation);
			BasicNameValuePair ToLocationBasicNameValuePair = new BasicNameValuePair(
					"ToLocation", ToLocation);

			BasicNameValuePair FromShortNameBasicNameValuePair;
			BasicNameValuePair ToShortNameBasicNameValuePair;

			if (fromshortname == null || fromshortname.equalsIgnoreCase("")
					|| fromshortname.isEmpty()) {

				FromShortNameBasicNameValuePair = new BasicNameValuePair(
						"FromShortName", FromLocation);
			} else {

				FromShortNameBasicNameValuePair = new BasicNameValuePair(
						"FromShortName", fromshortname);
			}

			if (toshortname == null || toshortname.equalsIgnoreCase("")
					|| toshortname.isEmpty()) {

				ToShortNameBasicNameValuePair = new BasicNameValuePair(
						"ToShortName", ToLocation);
			} else {

				ToShortNameBasicNameValuePair = new BasicNameValuePair(
						"ToShortName", toshortname);
			}

			BasicNameValuePair TravelDateBasicNameValuePair = new BasicNameValuePair(
					"TravelDate", TravelDate);
			BasicNameValuePair TravelTimeBasicNameValuePair = new BasicNameValuePair(
					"TravelTime", TravelTime);
			BasicNameValuePair SeatsBasicNameValuePair = new BasicNameValuePair(
					"Seats", Seats);
			BasicNameValuePair RemainingSeatsBasicNameValuePair = new BasicNameValuePair(
					"RemainingSeats", Seats);
			BasicNameValuePair DistanceBasicNameValuePair = new BasicNameValuePair(
					"Distance", distancetext);

			BasicNameValuePair durationvalueBasicNameValuePair = new BasicNameValuePair(
					"ExpTripDuration", durationvalue);

			BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
					"MembersNumber", selectednumbers.toString());
			BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
					"MembersName", selectednames.toString());
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", msg);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(FromLocationBasicNameValuePair);
			nameValuePairList.add(ToLocationBasicNameValuePair);

			nameValuePairList.add(FromShortNameBasicNameValuePair);
			nameValuePairList.add(ToShortNameBasicNameValuePair);

			nameValuePairList.add(TravelDateBasicNameValuePair);
			nameValuePairList.add(TravelTimeBasicNameValuePair);
			nameValuePairList.add(SeatsBasicNameValuePair);
			nameValuePairList.add(RemainingSeatsBasicNameValuePair);
			nameValuePairList.add(DistanceBasicNameValuePair);
			nameValuePairList.add(durationvalueBasicNameValuePair);
			nameValuePairList.add(MembersNumberBasicNameValuePair);
			nameValuePairList.add(MembersNameBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);

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
				sendres = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("sendres", "" + stringBuilder.toString());
		}
	}

	private void scheduleUpcomingTripNotification() {
		String tripTime = TravelDate + " " + TravelTime;

		UpcomingStartTripAlarm upcomingStartTripAlarm = new UpcomingStartTripAlarm();
		upcomingStartTripAlarm.setAlarm(ContactsInviteForRideActivity.this,
				tripTime, UpcomingStartTripAlarm.ALARM_TYPE_UPCOMING, CabId,
				fromshortname, toshortname);

	}

	private void scheduleStartTripNotification() {
		String tripTime = TravelDate + " " + TravelTime;

		UpcomingStartTripAlarm upcomingStartTripAlarm = new UpcomingStartTripAlarm();
		upcomingStartTripAlarm.setAlarm(ContactsInviteForRideActivity.this,
				tripTime, UpcomingStartTripAlarm.ALARM_TYPE_START_TRIP, CabId,
				fromshortname, toshortname);

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
