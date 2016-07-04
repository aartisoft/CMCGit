package com.clubmycab.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.fragment.ContactListFragment;
import com.clubmycab.fragment.ContactListFragment.ContactListListener;
import com.clubmycab.fragment.GroupListFragment;
import com.clubmycab.fragment.GroupListFragment.GroplistFragmnetListener;
import com.clubmycab.model.ContactData;
import com.clubmycab.model.GroupDataModel;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;

import java.util.ArrayList;
import java.util.List;

/**
 * 1) List Contacts 2) Delete Contacts 3) Searching Contacts 4) Dialog to show
 * selected contacts group
 */

public class SendInvitesToOtherScreen extends FragmentActivity implements
		View.OnClickListener, GroplistFragmnetListener, ContactListListener {
	public static final int MY_CLUB_ACTIVITY_ID = 101;
	public static final int INVITE_FRAGMENT_ACTIVTY_ID = 102;
	public static final int CHECK_POOL_FRAGMENT_ID = 103;
	public static final int MEMBER_RIDE_ACTIVITY_ID =  104;
	private static final int CONTACTS_FRAGMENT = 0;
	private static final int GROUP_FRAGMENTS = 1;
	private ViewPager viewPager;
	private TextView tabOne;
	private static final int GROUP_REQUEST_RESULT = 101;
	private static final int CONTACT_REQUEST_RESULT = 102;
	private ArrayList<ContactData> contactList =  new ArrayList<ContactData>();
	private ArrayList<GroupDataModel> groupList =  new ArrayList<GroupDataModel>();
	private boolean isContactSelected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_invites_to_other_screen);
		findViewById(R.id.rlGroupIcon).setOnClickListener(this);
		findViewById(R.id.rlGroupIconGroups).setOnClickListener(this);
		findViewById(R.id.llContacts).setOnClickListener(this);
		findViewById(R.id.tvGroups).setOnClickListener(this);
		findViewById(R.id.btnSend).setOnClickListener(this);

		((TextView) findViewById(R.id.tvContacts)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));
		((TextView) findViewById(R.id.tvGroups)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));
		((Button) findViewById(R.id.btnSend)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		initializeView();

	}
	
	private void initializeView(){
		if(getIntent().getExtras() != null){
			Bundle bundle = getIntent().getExtras();
			if(bundle.containsKey("activity_id")){
				if(bundle.getInt("activity_id") == INVITE_FRAGMENT_ACTIVTY_ID){
					isContactSelected = false;
					viewPager.setOffscreenPageLimit(2);
					ViewPagerAdapter adapter = new ViewPagerAdapter(
							getSupportFragmentManager());
					adapter.addFrag(ContactListFragment.newInstance(null), "Contacts");
					adapter.addFrag(GroupListFragment.newInstance(null), "Groups");
					viewPager.setAdapter(adapter);
					viewPager.setCurrentItem(GROUP_FRAGMENTS);
					switchTabsFromTabs(findViewById(R.id.tvGroups));
					viewPager.setOnPageChangeListener(onPageChangeListener);
				}else {
					isContactSelected = true;
					viewPager.setOffscreenPageLimit(1);
					ViewPagerAdapter adapter = new ViewPagerAdapter(
							getSupportFragmentManager());
					adapter.addFrag(ContactListFragment.newInstance(null), "Contacts");
					viewPager.setAdapter(adapter);
					switchTabsFromTabs(findViewById(R.id.tvContacts));
					
					//Hide View--------------------------->
					findViewById(R.id.tab_switcher).setVisibility(View.GONE);
					findViewById(R.id.llGroups).setVisibility(View.GONE);
					((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(R.color.colorWhite));
					//viewPager.setOnPageChangeListener(onPageChangeListener);
				}
			}
		}
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				switchTabsFromPager(findViewById(R.id.llContacts));
				isContactSelected = true;
				findViewById(R.id.rlGroupIcon).setAlpha(1);
				findViewById(R.id.rlGroupIconGroups).setAlpha(.5f);

			} else {
				switchTabsFromPager(findViewById(R.id.tvGroups));
				isContactSelected = false;
				findViewById(R.id.rlGroupIcon).setAlpha(.5f);
				findViewById(R.id.rlGroupIconGroups).setAlpha(1);
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	/**
	 * Adding fragments to ViewPager
	 * 
	 * @param viewPager
	 */
	private void setupViewPager(ViewPager viewPager) {
		
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlGroupIcon:
			ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
					.getAdapter()).getItem(0);
			contactListFragment.showGrouplistDialog();
			break;
		case R.id.rlGroupIconGroups:
			GroupListFragment groupListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
					.getAdapter()).getItem(1);
			groupListFragment.showGrouplistDialog();
			break;
		case R.id.llContacts:
			switchTabsFromTabs(v);
			break;

		case R.id.tvGroups:
			switchTabsFromTabs(v);

			break;
		case R.id.btnSend:
			Intent intent = new Intent();
			intent.putExtra("iscontactslected",isContactSelected );
			if(isContactSelected){
				intent.putExtra("Contact_list", contactList); 
			}else {
				intent.putExtra("Group_list", groupList); 

			}
			setResult(RESULT_OK, intent);
			finish();
			break;
		}

	}

	private void switchTabsFromTabs(View view) {
		if (view.getId() == R.id.llContacts) {
			viewPager.setCurrentItem(CONTACTS_FRAGMENT);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorWhite));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.color_app_blue));
			((TextView) findViewById(R.id.tvContacts)).setTypeface(Typeface.DEFAULT_BOLD);
			((TextView) findViewById(R.id.tvGroups)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));


		} else {
			viewPager.setCurrentItem(GROUP_FRAGMENTS);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.color_app_blue));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorWhite));
			((TextView) findViewById(R.id.tvContacts)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));
			((TextView) findViewById(R.id.tvGroups)).setTypeface(Typeface.DEFAULT_BOLD);

		}

	}

	private void switchTabsFromPager(View view) {
		if (view.getId() == R.id.llContacts) {
			// viewPager.setCurrentItem(CONTACTS_FRAGMENT);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorWhite));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorTransparent));
			((TextView) findViewById(R.id.tvContacts)).setTypeface(Typeface.DEFAULT_BOLD);
			((TextView) findViewById(R.id.tvGroups)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));

		} else {
			// viewPager.setCurrentItem(GROUP_FRAGMENTS);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorTransparent));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorWhite));
			((TextView) findViewById(R.id.tvContacts)).setTypeface(FontTypeface.getTypeface(SendInvitesToOtherScreen.this, AppConstants.HELVITICA));
			((TextView) findViewById(R.id.tvGroups)).setTypeface(Typeface.DEFAULT_BOLD);

		}

	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
		private final List<String> mFragmentTitleList = new ArrayList<String>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFrag(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

	

	/**
	 * Add User from contact list to group - Remove user from main contact list
	 * - Add user to group list - notify list - update groupCount
	 */
	public void addUserClicked(ContactData contactData) {
		ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(0);
		contactListFragment.addUserToGroup(contactData);
		if(((ViewPagerAdapter) viewPager
				.getAdapter()).getCount() ==2){
			GroupListFragment groupListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
					.getAdapter()).getItem(1);
			groupListFragment.clearSelectGroups();
		}
		
	}
	
	/**
	 * Remove user from selected contact list in Contact tab
	 * @param item
	 */
	public void removeUserClicked(ContactData item) {
		ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(0);
		// Log.d("Count",count+"");
		contactListFragment.removeUserFromGroup(item);

	}

	
	/**
	 * Update counting in contacts tab
	 * @param size
	 */
	public void updateCount(int size) {
		if (size > 0) {
			findViewById(R.id.rlGroupIcon).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.rlGroupIcon).setVisibility(View.GONE);
		}
		((TextView) findViewById(R.id.tvCount)).setText(String.valueOf(size));
	}
	
	/**
	 * Add new group from group list to selected groups and remove from main group list
	 * @param item
	 */
	public void addGroupClicked(GroupDataModel item) {
		GroupListFragment groupListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(1);
		// Log.d("Count",count+"");
		groupListFragment.addNewGroup(item);
		ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(0);
		contactListFragment.clearSectedContacts();
		
	}

	/**
	 * Remove group from selected grouplist and add it again to maingrouplist 
	 * @param item
	 */
	public void removeGroupClicked(GroupDataModel item) {
		GroupListFragment contactListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(1);
		// Log.d("Count",count+"");
		contactListFragment.removeGroupFromSelection(item);
		
	}
	
	/**
	 * Update counting in groups tab
	 * @param size
	 */
	public void updateGroupCount(int size) {
		if (size > 0) {
			findViewById(R.id.rlGroupIconGroups).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.rlGroupIconGroups).setVisibility(View.GONE);
		}
		((TextView) findViewById(R.id.tvCountGroups)).setText(String.valueOf(size));
	}

	/*
	 * @Override public void onGroupEmpty() {
	 * viewPager.setCurrentItem(CONTACTS_FRAGMENT); }
	 */

	@Override
	public void onEmptyGroup() {
		viewPager.setCurrentItem(CONTACTS_FRAGMENT);

	}

	
	@Override
	public void onGroupListModified(ArrayList<GroupDataModel> groupList) {
		this.groupList = groupList;
		if(groupList.size() > 0){
			findViewById(R.id.rlGroupIcon).setVisibility(View.GONE);
		}
			
	}
	
	@Override
	public void onContactListModified(ArrayList<ContactData> contactList) {
		this.contactList = contactList;
		if(contactList.size() > 0){
			findViewById(R.id.rlGroupIconGroups).setVisibility(View.GONE);
		}
	}

	public void onGroupChecked(GroupDataModel ownerModel) {
		GroupListFragment contactListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(1);
		// Log.d("Count",count+"");
		contactListFragment.addUserToGroup(ownerModel);
	}

	

}
