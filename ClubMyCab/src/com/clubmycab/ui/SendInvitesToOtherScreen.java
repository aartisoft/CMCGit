package com.clubmycab.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.fragment.ContactListFragment;
import com.clubmycab.fragment.ContactListFragment.ContactListListener;
import com.clubmycab.fragment.GroupListFragment;
import com.clubmycab.fragment.GroupListFragment.GroplistFragmnetListener;
import com.clubmycab.model.ContactData;
import com.clubmycab.model.OwnerModel;

/**
 * 1) List Contacts 2) Delete Contacts 3) Searching Contacts 4) Dialog to show
 * selected contacts group
 */

public class SendInvitesToOtherScreen extends FragmentActivity implements
		View.OnClickListener, GroplistFragmnetListener, ContactListListener {
	private static final int CONTACTS_FRAGMENT = 0;
	private static final int GROUP_FRAGMENTS = 1;
	private ViewPager viewPager;
	private TextView tabOne;
	private static final int GROUP_REQUEST_RESULT = 101;
	private static final int CONTACT_REQUEST_RESULT = 102;
	private ArrayList<ContactData> contactList =  new ArrayList<ContactData>();
	private ArrayList<OwnerModel> groupList =  new ArrayList<OwnerModel>();

	private boolean isContactSelected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_invites_to_other_screen);
		findViewById(R.id.rlGroupIcon).setOnClickListener(this);
		findViewById(R.id.llContacts).setOnClickListener(this);
		findViewById(R.id.tvGroups).setOnClickListener(this);
		findViewById(R.id.btnSend).setOnClickListener(this);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(2);
		setupViewPager(viewPager);
		viewPager.setCurrentItem(GROUP_FRAGMENTS);
		switchTabs(findViewById(R.id.tvGroups));
		viewPager.setOnPageChangeListener(onPageChangeListener);

	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				switchTabsForPager(findViewById(R.id.llContacts));
				isContactSelected = true;
				findViewById(R.id.rlGroupIcon).setAlpha(1);
			} else {
				switchTabsForPager(findViewById(R.id.tvGroups));
				isContactSelected = false;
				findViewById(R.id.rlGroupIcon).setAlpha(.5f);

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
		ViewPagerAdapter adapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		adapter.addFrag(ContactListFragment.newInstance(null), "Contacts");
		adapter.addFrag(GroupListFragment.newInstance(null), "Groups");
		viewPager.setAdapter(adapter);
	}

	public void removeUserClicked(ContactData item) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(0);
		// Log.d("Count",count+"");
		contactListFragment.removeUserFromGroup(item);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlGroupIcon:
			ViewPagerAdapter adapter = new ViewPagerAdapter(
					getSupportFragmentManager());
			ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
					.getAdapter()).getItem(0);
			contactListFragment.showGrouplistDialog();
			break;

		case R.id.llContacts:
			switchTabs(v);
			break;

		case R.id.tvGroups:
			switchTabs(v);

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

	private void switchTabs(View view) {
		if (view.getId() == R.id.llContacts) {
			viewPager.setCurrentItem(CONTACTS_FRAGMENT);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorNotifCircle));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorTransparent));

		} else {
			viewPager.setCurrentItem(GROUP_FRAGMENTS);
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
							R.color.colorNotifCircle));

		}

	}

	private void switchTabsForPager(View view) {
		if (view.getId() == R.id.llContacts) {
			// viewPager.setCurrentItem(CONTACTS_FRAGMENT);
			((TextView) findViewById(R.id.tvContacts))
					.setTextColor(getResources().getColor(R.color.colorWhite));
			((TextView) findViewById(R.id.tvGroups))
					.setTextColor(getResources().getColor(
							R.color.colorDullWhite));
			((View) findViewById(R.id.leftView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorNotifCircle));
			((View) findViewById(R.id.rightView))
					.setBackgroundColor(getResources().getColor(
							R.color.colorTransparent));

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
							R.color.colorNotifCircle));

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
	 * Adding custom view to tab
	 */
	private void setupTabIcons() {

	}

	/**
	 * Add User from contact list to group - Remove user from main contact list
	 * - Add user to group list - notify list - update groupCount
	 */
	public void addUserClicked(ContactData contactData) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(
				getSupportFragmentManager());
		ContactListFragment contactListFragment = (ContactListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(0);
		// Log.d("Count",count+"");
		contactListFragment.addUserToGroup(contactData);

	}

	

	public void updateCount(int size) {
		if (size > 0) {
			findViewById(R.id.rlGroupIcon).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.rlGroupIcon).setVisibility(View.GONE);
		}
		((TextView) findViewById(R.id.tvCount)).setText(String.valueOf(size));
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
	public void onContactListModified(ArrayList<ContactData> contactList) {
		this.contactList = contactList;
	}

	/*
	 * Intent intent = new Intent(this,DisplayContact.class);
	 * intent.putExtra("Contact_list", ContactLis); startActivity(intent);
	 * 
	 * ArrayList<ContactClass> myList =
	 * getIntent().getParcelableExtra("Contact_list");
	 */
	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("iscontactslected",isContactSelected );
		if(isContactSelected){
			intent.putExtra("Contact_list", contactList); 
		}else {
			intent.putExtra("Group_list", groupList); 

		}
		setResult(RESULT_OK, intent);
		super.onBackPressed();
		
	}*/

	@Override
	public void onGroupSelected(ArrayList<OwnerModel> groupList) {
		this.groupList = groupList;
	}

	public void onGroupChecked(OwnerModel ownerModel) {
		GroupListFragment contactListFragment = (GroupListFragment) ((ViewPagerAdapter) viewPager
				.getAdapter()).getItem(1);
		// Log.d("Count",count+"");
		contactListFragment.addUserToGroup(ownerModel);
	}

}
