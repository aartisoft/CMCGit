package com.clubmycab.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.ContactObject;
import com.clubmycab.ContactsAdapter;
import com.clubmycab.ContactsListClass;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.model.ContactData;
import com.clubmycab.model.GroupDataModel;
import com.clubmycab.model.MemberModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by newpc on 9/5/16.
 */
public class XMyClubsActivty extends Activity implements
        GlobalAsyncTask.AsyncTaskResultListener {

    private ListView lv, lvmyclub, listMembersclubs;
    private Button newclub;
    private ContactsAdapter objAdapter;
    private String OwnerNumber;
    private String FullName;
    private String Result;
    private String referralResult;
    private ImageView notificationimg;
    private ImageView sidemenu;
    private TextView myprofile;
    private TextView myrides;
    private TextView bookacab;
    private TextView sharemylocation;
    private TextView myclubs;
    private TextView sharethisapp;
    private TextView mypreferences;
    private TextView about;
    private ArrayList<String> ClubName = new ArrayList<String>();
    private ArrayList<String> NewClub = new ArrayList<String>();
    private ArrayList<String> ClubMember = new ArrayList<String>();
    private ArrayList<String> MembersNumber = new ArrayList<String>();
    private ArrayList<String> selectednames = new ArrayList<String>();
    private ArrayList<String> selectednumbers = new ArrayList<String>();
    private ArrayList<String> namearray = new ArrayList<String>();
    private ArrayList<String> phonenoarray = new ArrayList<String>();
    private ArrayList<String> imagearray = new ArrayList<String>();
    private ArrayList<String> namearraynew = new ArrayList<String>();
    private ArrayList<String> phonenoarraynew = new ArrayList<String>();
    private ArrayList<String> imagearraynew = new ArrayList<String>();
    private ArrayList<String> shownames = new ArrayList<String>();
    private ArrayList<String> shownumbers = new ArrayList<String>();
    private ArrayList<String> showimagenames = new ArrayList<String>();
    private ArrayList<String> showpoolid = new ArrayList<String>();
    private ListView listmyclubpopup;
    private Mypopuplistadapter clubadaptor;
    private ListView listmemberclubpopup;
    private Memberpopuplistadapter membersclubadaptor;
    private String strSelected;
    private Dialog meradialog;
    private RelativeLayout unreadnoticountrl;
    private TextView unreadnoticount;
    private String readunreadnotiresp;
    private Bitmap mIcon11;
    private String imagenameresp;
    private String poolresponse;
    private ArrayList<String> MyClubPoolId = new ArrayList<String>();
    private ArrayList<String> MyClubPoolName = new ArrayList<String>();
    private ArrayList<String> MyClubNoofMembers = new ArrayList<String>();
    private ArrayList<String> MyClubOwnerName = new ArrayList<String>();
    private ArrayList<String> MyClubMembers = new ArrayList<String>();
    private ArrayList<String> MemberClubPoolId = new ArrayList<String>();
    private ArrayList<String> MemberClubPoolName = new ArrayList<String>();
    private ArrayList<String> MemberClubNoofMembers = new ArrayList<String>();
    private ArrayList<String> MemberClubOwnerName = new ArrayList<String>();
    private ArrayList<String> MemberClubMembers = new ArrayList<String>();
    private LinearLayout myclubsrl;
    private String comefrom, comefromfirstlogin;
    private Tracker tracker;
    private boolean exceptioncheck = false;
    private static final int CREATE_NEW_GRP_REQUEST = 101;
    private static final int ADD_MORE_MEMBER_REQUEST = 102;
    private static final int REFER_MEMBER_FOR_CLUB_REQUEST = 103;
    private ArrayList<GroupDataModel> groupList = new ArrayList<GroupDataModel>();
    private ListView listMyclubs;
    private MyClubsAdapter adapterClubMy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclubs_x);

        comefrom = null;
        Intent intent = getIntent();
        comefrom = intent.getStringExtra("comefrom");

        // Check if Internet present
        if (!isOnline()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    XMyClubsActivty.this);
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
                .getInstance(XMyClubsActivty.this);
        tracker = analytics
                .newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("MyClubs");
        ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvHeading)).setText("My Groups");
        findViewById(R.id.flBackArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(XMyClubsActivty.this,
                        NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        myclubsrl = (LinearLayout) findViewById(R.id.myclubsrl);
        myclubsrl.setOnClickListener(new View.OnClickListener() {

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
        notificationimg = (ImageView) findViewById(R.id.notificationimg);
        ((TextView)findViewById(R.id.tvMessage)).setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        OwnerNumber = mPrefs.getString("MobileNumber", "");
        findViewById(R.id.flNotifications).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(XMyClubsActivty.this,
                        NotificationListActivity.class);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
        unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

        if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

            unreadnoticountrl.setVisibility(View.GONE);

        } else {

            unreadnoticountrl.setVisibility(View.VISIBLE);
            unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
        }
        newclub = (Button) findViewById(R.id.butmakenewclubs);
        newclub.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
        newclub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showGroupNameDialog();
            }
        });
        comefromfirstlogin = getIntent().getStringExtra("comefromfirstlogin");
        if (comefromfirstlogin != null && comefromfirstlogin.length() > 0) {
            newclub.performClick();
        }
        listMyclubs = (ListView) findViewById(R.id.listMyclubs);
        adapterClubMy = new MyClubsAdapter();
        adapterClubMy.init(XMyClubsActivty.this, groupList, true);
        listMyclubs.setAdapter(adapterClubMy);
        listMyclubs.setOnItemClickListener(onItemClickListener);
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
        final EditText ClubNames = (EditText) dialog.findViewById(R.id.editTextnewclub);
        final EditText searchfromlist = (EditText) dialog.findViewById(R.id.searchfromlist);

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

        objAdapter = new ContactsAdapter(XMyClubsActivty.this,
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        MakeClub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String cname = ClubNames.getText().toString().trim();
                if (cname.isEmpty() || cname == null
                        || cname.equalsIgnoreCase("")) {

                    Toast.makeText(XMyClubsActivty.this,
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
                        Toast.makeText(XMyClubsActivty.this,
                                "Please select contact(s) to create club",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }

        });

        dialog.show();
    }

  /*  public void showclub() throws JSONException {

        SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
        String clubs = mPrefs11111.getString("clubs", "");

        if (clubs.equalsIgnoreCase("No Users of your Club")) {
            Toast.makeText(XMyClubsActivty.this, "No groups created yet!!",
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
                    XMyClubsActivty.this, MyClubPoolId, MyClubPoolName,
                    MyClubNoofMembers, MyClubOwnerName);
            lvmyclub.setAdapter(adapter);
            lvmyclub.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                        // Toast.makeText(XMyClubsActivty.this,
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
                    XMyClubsActivty.this, MemberClubPoolId, MemberClubPoolName,
                    MemberClubNoofMembers, MemberClubOwnerName);
            listMembersclubs.setAdapter(adapter1);

            listMembersclubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
    }*/

    // ////////////////////////////
    public void ShowAlertformembersclubs(ArrayList<String> shownames2,
                                         ArrayList<String> shownum, final String cname,
                                         final ArrayList<String> poolids, ArrayList<String> imagenames) {

        meradialog = new Dialog(this);
        meradialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meradialog.setContentView(R.layout.memberclublistpopup);

        TextView clubnameinpopup = (TextView) meradialog.findViewById(R.id.memberclubnameinpopup);
        clubnameinpopup.setText(cname);
        clubnameinpopup.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));

        listmemberclubpopup = (ListView) meradialog.findViewById(R.id.listmemberclubpopup);

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
        memberrefermorecontacts.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));

        memberrefermorecontacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectedPoolId = poolids.get(0).toString().trim();
                selectedPoolName = cname;
                if(meradialog != null){
                    meradialog.dismiss();
                }
                Intent intent = new Intent(XMyClubsActivty.this, SendInvitesToOtherScreen.class);
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
        clubnameinpopup.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
        listmyclubpopup = (ListView) meradialog
                .findViewById(R.id.listmyclubpopup);

        clubadaptor = new Mypopuplistadapter(shownames2, shownum, poolids,
                imgnames);
        listmyclubpopup.setAdapter(clubadaptor);

        Button addmorecontacts = (Button) meradialog.findViewById(R.id.addmorecontacts);
        addmorecontacts.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));

        addmorecontacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectedPoolId = poolids.get(0).toString().trim();
                selectedPoolName = cname;
                if(meradialog != null){
                    meradialog.dismiss();
                }
                Intent intent = new Intent(XMyClubsActivty.this, SendInvitesToOtherScreen.class);
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

        objAdapter = new ContactsAdapter(XMyClubsActivty.this,
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        done.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(XMyClubsActivty.this, "" + toaststr,
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(XMyClubsActivty.this,
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

        objAdapter = new ContactsAdapter(XMyClubsActivty.this,
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        done.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(XMyClubsActivty.this, "" + toaststr,
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(XMyClubsActivty.this,
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
                Toast.makeText(XMyClubsActivty.this,
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

            Log.d("XMyClubsActivty", "referFriendStepOne : "
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
            membertextmembername_popup.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));


            if (name.get(position) == null
                    || name.get(position).equalsIgnoreCase("null")) {
                // membertextmembername_popup.setText(number.get(position));
                // membertextmembernum_popup.setText(number.get(position));
                memberappuserimg.setVisibility(View.GONE);
            } else {
                membertextmembername_popup.setText(name.get(position));
                // membertextmembernum_popup.setText(number.get(position));
                memberappuserimg.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(imgnames.get(position))){
                    String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                            + imgnames.get(position).toString().trim();
                    Picasso.with(XMyClubsActivty.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(memberappuserimg);

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

            ImageView appuserimg = (ImageView) single_row.findViewById(R.id.appuserimg);
            TextView Mname = (TextView) single_row.findViewById(R.id.textmembername_popup);
            TextView Mnum = (TextView) single_row.findViewById(R.id.textmembernum_popup);
            Mname.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
            Mnum.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
            ImageView removeuser = (ImageView) single_row.findViewById(R.id.removeuser);

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

              if(!TextUtils.isEmpty(imgnames.get(position))){
                  String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                          + imgnames.get(position).toString().trim();
                  Picasso.with(XMyClubsActivty.this).load(url).placeholder(R.drawable.avatar_rides_list).error(R.drawable.avatar_rides_list).into(appuserimg);

              }
/*                if (imgnames.get(position).toString().trim() == null
                        || imgnames.get(position).toString().trim().isEmpty()
                        || imgnames.get(position).toString().trim().equals("")) {

                    appuserimg.setImageDrawable(getResources().getDrawable(
                            R.drawable.cabappicon));
                } else {
                    AQuery aq = new AQuery(XMyClubsActivty.this);
                    String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
                            + imgnames.get(position).toString().trim();

                    aq.id(appuserimg).image(url, true, true);

                }*/
            }

            removeuser.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            XMyClubsActivty.this);
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
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
           try{
               if(dialog != null){
                   dialog.setMessage("Please Wait...");
                   dialog.setCancelable(false);
                   dialog.setCanceledOnTouchOutside(false);
                   dialog.show();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
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
            try{
                if(dialog != null){
                    dialog.dismiss();
                }
                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMyClubsActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                parseGroupData();
            }catch (Exception e){
                e.printStackTrace();
            }



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
                Log.e("XMyClubsActivty", "poolresponse Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
                Log.e("XMyClubsActivty", "myclubsresp Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
          try {
              if(dialog != null){
                  dialog.setMessage("Please Wait...");
                  dialog.setCancelable(false);
                  dialog.setCanceledOnTouchOutside(false);
                  dialog.show();
              }
          }catch (Exception e){
              e.printStackTrace();
          }

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

           try{
               if (dialog != null && dialog.isShowing()) {
                   dialog.dismiss();
               }

               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(XMyClubsActivty.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }
               parseGroupData();

           }catch (Exception e){
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
                Log.e("XMyClubsActivty", "poolresponse Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
                Log.e("XMyClubsActivty", "myclubsresp Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
                    AppConstants.HELVITICA));

            removeclub.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            XMyClubsActivty.this);
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
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
           try{
               if(dialog != null){
                   dialog.setMessage("Please Wait...");
                   dialog.setCancelable(false);
                   dialog.setCanceledOnTouchOutside(false);
                   dialog.show();
               }
           }catch (Exception e){
               e.printStackTrace();
           }

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

            try{
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(XMyClubsActivty.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                parseGroupData();
            }catch (Exception e){
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
                Log.e("XMyClubsActivty", "poolresponse Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
                Log.e("XMyClubsActivty", "myclubsresp Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
            XMyClubsActivty.this.finish();
        } else {
            Intent mainIntent = new Intent(XMyClubsActivty.this,
                    NewHomeScreen.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(mainIntent, 500);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }
    }

    private class ConnectionTaskForcreatingNewClub extends
            AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
         try{
             if(dialog != null){
                 dialog.setMessage("Please Wait...");
                 dialog.setCancelable(false);
                 dialog.setCanceledOnTouchOutside(false);
                 dialog.show();
             }
         }catch (Exception e){
             e.printStackTrace();
         }

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

           try{
               if (dialog != null && dialog.isShowing()) {
                   dialog.dismiss();
               }

               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(XMyClubsActivty.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }
               parseGroupData();
           }catch (Exception e){
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
                Log.e("XMyClubsActivty", "poolresponse Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
                Log.e("XMyClubsActivty", "myclubsresp Unauthorized Access");
                exceptioncheck = true;
                // Toast.makeText(XMyClubsActivty.this,
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
        String myprofileresp = null;
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
          try{
              if(dialog != null){
                  dialog.setMessage("Please Wait...");
                  dialog.setCancelable(false);
                  dialog.setCanceledOnTouchOutside(false);
                  dialog.show();

              }
          }catch (Exception e){
              e.printStackTrace();
          }
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
           try{
               if(dialog != null){
                   dialog.dismiss();
               }
               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(XMyClubsActivty.this,
                           getResources().getString(R.string.exceptionstring),
                           Toast.LENGTH_LONG).show();
                   return;
               }

               try {
                   if(!TextUtils.isEmpty(myprofileresp) && myprofileresp.equalsIgnoreCase("No Users of your Club")){
                       findViewById(R.id.tvMessage).setVisibility(View.VISIBLE);
                       return;
                   }
                   findViewById(R.id.tvMessage).setVisibility(View.GONE);
                   parseGroupData();
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           }catch (Exception e){
               e.printStackTrace();
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

                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    myprofileresp = stringBuilder.append(bufferedStrChunk)
                            .toString();
                }

                Log.d("myclubsresp", "" + myprofileresp);

                if (myprofileresp != null && myprofileresp.length() > 0
                        && myprofileresp.contains("Unauthorized Access")) {
                    Log.e("XMyClubsActivty", "myprofileresp Unauthorized Access");
                    exceptioncheck = true;
                    // Toast.makeText(XMyClubsActivty.this,
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

    }

    /*
     *______________________________________VD______________________________
     */
    private Dialog askGrpDialog;
    private String groupName, selectedPoolId, selectedPoolName;

    private void showGroupNameDialog(){
        if(askGrpDialog == null){
            askGrpDialog = new Dialog(XMyClubsActivty.this);
            askGrpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            askGrpDialog.setContentView(R.layout.dialog_ask_grp_name);
            askGrpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            askGrpDialog.findViewById(R.id.tvOk).setOnClickListener(dialogClickListener);
            askGrpDialog.findViewById(R.id.tvCancel).setOnClickListener(dialogClickListener);
        }
        if(!askGrpDialog.isShowing()){
            ((EditText)askGrpDialog.findViewById(R.id.etGrpName)).setText("");
            askGrpDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.tvOk:
                    if(!TextUtils.isEmpty(((EditText)askGrpDialog.findViewById(R.id.etGrpName)).getText().toString().trim())){
                        askGrpDialog.dismiss();
                        groupName = ((EditText)askGrpDialog.findViewById(R.id.etGrpName)).getText().toString().trim();
                        Intent intent = new Intent(XMyClubsActivty.this, SendInvitesToOtherScreen.class);
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
            Toast.makeText(XMyClubsActivty.this,
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
            Toast.makeText(XMyClubsActivty.this, "" + toaststr,
                    Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(XMyClubsActivty.this,
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
            Toast.makeText(XMyClubsActivty.this, "" + toaststr,
                    Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(XMyClubsActivty.this,
                    "Please select contact(s) to refer",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ----------------------------------------------------------------->
     */
    private void parseGroupData(){
        try{

            SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
            String response = mPrefs11111.getString("clubs", "");
            if (!TextUtils.isEmpty(response) && response.equalsIgnoreCase("No Users of your Club")) {
                findViewById(R.id.tvMessage).setVisibility(View.VISIBLE);
				/*flag = 1;
				gpListener.onEmptyGroup();*/
                groupList.clear();
                notifyAdapter();
            }else{
                findViewById(R.id.tvMessage).setVisibility(View.GONE);

                if(response == null)
                    return;
                groupList.clear();
                JSONArray subArray = new JSONArray(response);
                for (int i = 0; i < subArray.length(); i++) {
                    GroupDataModel ownerModel = new GroupDataModel();
                    if(!subArray.getJSONObject(i).isNull("PoolId")){
                        ownerModel.setPoolId(subArray.getJSONObject(i).getString("PoolId"));
                    }
                    if(!subArray.getJSONObject(i).isNull("PoolName")){
                        ownerModel.setPoolName(subArray.getJSONObject(i).getString("PoolName"));
                    }
                    if(!subArray.getJSONObject(i).isNull("OwnerNumber")){
                        ownerModel.setOwnerNumber(subArray.getJSONObject(i).getString("OwnerNumber"));
                    }
                    if(!subArray.getJSONObject(i).isNull("OwnerName")){
                        ownerModel.setOwnerName(subArray.getJSONObject(i).getString("OwnerName"));
                    }

                    if(!subArray.getJSONObject(i).isNull("NoofMembers")){
                        ownerModel.setNumberOfMembers(subArray.getJSONObject(i).getString("NoofMembers"));
                    }
                    if (!subArray.getJSONObject(i).isNull("NoofMembers")) {
                        if(subArray.getJSONObject(i).getString("IsPoolOwner").toString().trim().equalsIgnoreCase("1")){
                            ownerModel.setPoolOwner(true);
                        }else {
                            ownerModel.setPoolOwner(false);
                        }
                    }
                    if(!subArray.getJSONObject(i).isNull("Members") && subArray.getJSONObject(i).getJSONArray("Members").length()>0){
                        ArrayList<MemberModel> arrayList = new ArrayList<MemberModel>();
                        JSONArray array = subArray.getJSONObject(i).getJSONArray("Members");
                        for (int j = 0; j < array.length(); j++) {
                            MemberModel memberModel = new MemberModel();
                            if(!array.getJSONObject(j).isNull("FullName")){
                                memberModel.setMemberName(array.getJSONObject(j).getString("FullName"));
                            }

                            if(!array.getJSONObject(j).isNull("MemberNumber")){
                                memberModel.setMemberNumber(array.getJSONObject(j).getString("MemberNumber"));

                            }
                            arrayList.add(memberModel);
                        }
                        ownerModel.setMemberList(arrayList);
                        ownerModel.setMemberData(subArray.getJSONObject(i).getString("Members").toString());
                    }
                    groupList.add(ownerModel);

                }
                notifyAdapter();
            }

        }catch(NullPointerException e){
            e.printStackTrace();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void notifyAdapter(){
        if(adapterClubMy != null){
            adapterClubMy.init(
                    XMyClubsActivty.this, groupList,
                    true);
            adapterClubMy.notifyDataSetChanged();
        }
    }

    private class MyClubsAdapter extends BaseAdapter {
        private static final int ANIMATION_DURATION = 200;

        Context mContext;
        LayoutInflater inflater;
        private List<GroupDataModel> mainDataList = null;
        int selectedIndex = -1;
        private boolean isWarnning;

        public MyClubsAdapter(){
        }

        public void init(Context context, ArrayList<GroupDataModel> mainDataList,
                         boolean isWarnning) {
            this.isWarnning = isWarnning;
            mContext = context;
            this.mainDataList = mainDataList;
            inflater = LayoutInflater.from(mContext);

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
                view = inflater.inflate(R.layout.item_mygroups, parent, false);
                setViewHolder(view);
            } else if (((ViewHolder)convertView.getTag()).needInflate) {
                view = inflater.inflate(R.layout.item_mygroups, parent, false);
                setViewHolder(view);
            }else {
                view = convertView;
            }
            holder = (ViewHolder) view.getTag();
            GroupDataModel currentListData = getItem(position);
            holder.name.setText(mainDataList.get(position).getPoolName());
            holder.nofmem.setText( mainDataList.get(position).getNumberOfMembers());
            if (mainDataList.get(position).getOwnerName().toString().trim().equalsIgnoreCase("") || mainDataList.get(position).getOwnerName().toString().trim().length() == 0) {
                holder.clubownername.setVisibility(View.GONE);
            } else {
                holder.clubownername.setText(mainDataList.get(position).getOwnerName());
            }
            holder.flDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(mainDataList.get(position).isPoolOwner()){
                       AlertDialog.Builder builder = new AlertDialog.Builder(XMyClubsActivty.this);
                       builder.setMessage("Are you sure you want to delete this group?");
                       builder.setCancelable(true);
                       builder.setPositiveButton("No",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                   }
                               });
                       builder.setNegativeButton("Yes",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {

                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                           new ConnectionTaskForRemoveclub()
                                                   .executeOnExecutor(
                                                           AsyncTask.THREAD_POOL_EXECUTOR,
                                                           mainDataList.get(position).getPoolId()
                                                                   .toString().trim());
                                       } else {
                                           new ConnectionTaskForRemoveclub()
                                                   .execute(mainDataList.get(position).getPoolId());
                                       }
                                   }
                               });
                       AlertDialog dialog = builder.show();
                       TextView messageText = (TextView) dialog
                               .findViewById(android.R.id.message);
                       messageText.setGravity(Gravity.CENTER);
                       dialog.show();
                   }else {
                       AlertDialog.Builder builder = new AlertDialog.Builder(XMyClubsActivty.this);
                       builder.setMessage("Are you sure you want to leave this group?");
                       builder.setCancelable(true);
                       builder.setPositiveButton("No",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {
                                       dialog.cancel();
                                   }
                               });
                       builder.setNegativeButton("Yes",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int id) {

                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                           new ConnectionTaskForLeaveClub()
                                                   .executeOnExecutor(
                                                           AsyncTask.THREAD_POOL_EXECUTOR,
                                                           mainDataList.get(position).getPoolId());
                                       } else {
                                           new ConnectionTaskForLeaveClub()
                                                   .execute(mainDataList.get(position).getPoolId());
                                       }
                                   }
                               });
                       AlertDialog dialog = builder.show();
                       TextView messageText = (TextView) dialog
                               .findViewById(android.R.id.message);
                       messageText.setGravity(Gravity.CENTER);
                       dialog.show();
                   }
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
                    ((SendInvitesToOtherScreen)mContext).addGroupClicked(getItem(index));
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

        class ViewHolder {
            private boolean needInflate;
            private TextView name;
            private TextView nofmem;
            private TextView clubownername;
            private FrameLayout flDelete;

            public ViewHolder(View view) {
                name =(TextView) view.findViewById(R.id.nameofclub);
                nofmem = (TextView) view.findViewById(R.id.noofmembers);
                clubownername = (TextView) view.findViewById(R.id.clubownername);
                flDelete = (FrameLayout)view.findViewById(R.id.flDelete);
                name.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
                nofmem.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));
                clubownername.setTypeface(FontTypeface.getTypeface(XMyClubsActivty.this, AppConstants.HELVITICA));

            }
        }

    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try{
                shownames.clear();
                shownumbers.clear();
                showimagenames.clear();
                showpoolid.clear();
                JSONArray subArray;

                subArray = new JSONArray(groupList.get(position).getMemberData());
                if (subArray.length() > 0) {
                    for (int i = 0; i < subArray.length(); i++) {
                        shownames.add(subArray.getJSONObject(i).getString("FullName").toString());
                        shownumbers.add(subArray.getJSONObject(i).getString("MemberNumber").toString());
                        showimagenames.add(subArray.getJSONObject(i).getString("ImageName").toString());
                        showpoolid.add(groupList.get(position).getPoolId());
                    }
                } else {
                    showpoolid.add(groupList.get(position).getPoolId());
                }

                Log.d("shownames", "" + shownames);
                Log.d("shownumbers", "" + shownumbers);
                Log.d("showimagenames", "" + showimagenames);
                Log.d("showpoolid", "" + showpoolid);

                if(groupList.get(position).isPoolOwner()){
                    ShowAlert(shownames, shownumbers, groupList.get(position).getPoolName(), showpoolid, showimagenames);
                }else {
                    ShowAlertformembersclubs(shownames, shownumbers, groupList.get(position).getPoolName(), showpoolid, showimagenames);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private class ConnectionTaskForLeaveClub extends
            AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = new ProgressDialog(XMyClubsActivty.this);

        @Override
        protected void onPreExecute() {
          try{
              if(dialog != null){
                  dialog.setMessage("Please Wait...");
                  dialog.setCancelable(false);
                  dialog.setCanceledOnTouchOutside(false);
                  dialog.show();
              }
          }catch (Exception e){
              e.printStackTrace();
          }

        }

        @Override
        protected Void doInBackground(String... args) {
            AuthenticateConnectionLeaveClub mAuth1 = new AuthenticateConnectionLeaveClub();
            try {
                mAuth1.poolid = args[0];
                mAuth1.connection();
            } catch (Exception e) {
                exceptioncheck = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

           try{
               if (dialog != null && dialog.isShowing()) {
                   dialog.dismiss();
               }

               if (exceptioncheck) {
                   exceptioncheck = false;
                   Toast.makeText(XMyClubsActivty.this, getResources().getString(
                           R.string.exceptionstring), Toast.LENGTH_LONG)
                           .show();
                   return;
               }
               parseGroupData();
           }catch (Exception e){
               e.printStackTrace();
           }

        }

    }

    public class AuthenticateConnectionLeaveClub {

        public String poolid;

        public AuthenticateConnectionLeaveClub() {

        }

        public void connection() throws Exception {

            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Leave Club").setAction("Leave Club")
                    .setLabel("Leave Club").build());

            // Connect to google.com
            HttpClient httpClient = new DefaultHttpClient();
            String url_select = GlobalVariables.ServiceUrl + "/Leave_Club.php";
            HttpPost httpPost = new HttpPost(url_select);

            BasicNameValuePair poolidBasicNameValuePair = new BasicNameValuePair(
                    "poolid", poolid.toString().trim());

            SharedPreferences mPrefs = getSharedPreferences(
                    "FacebookData", 0);
            String OwnerNumber = mPrefs.getString("MobileNumber", "");
            BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
                    "MemberNumber", OwnerNumber.toString().trim());

            String authString = OwnerNumber.toString().trim()
                    + poolid.toString().trim();
            BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(poolidBasicNameValuePair);
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

            // Connect to google.com
            HttpClient httpClient1 = new DefaultHttpClient();
            String url_select1 = GlobalVariables.ServiceUrl + "/Fetch_Club.php";
            HttpPost httpPost1 = new HttpPost(url_select1);

            BasicNameValuePair UserNumberBasicNameValuePair1 = new BasicNameValuePair(
                    "OwnerNumber", OwnerNumber);
            authString = OwnerNumber;
            authValuePair = new BasicNameValuePair("auth",
                    GlobalMethods.calculateCMCAuthString(authString));

            List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
            nameValuePairList1.add(UserNumberBasicNameValuePair1);
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

            SharedPreferences sharedPreferences1 = getSharedPreferences("MyClubs", 0);
            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
            editor1.putString("clubs", myclubsresp.toString().trim());
            editor1.commit();

        }
    }

}
