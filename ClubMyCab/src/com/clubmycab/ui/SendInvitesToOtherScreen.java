package com.clubmycab.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.clubmycab.R;
import com.clubmycab.fragment.ContactListFragment;
import com.clubmycab.fragment.GroupListFragment;
import com.clubmycab.model.ContactData;

/**
 * 1) List Contacts
 * 2) Delete Contacts
 * 3) Searching Contacts
 * 4) Dialog to show selected contacts group
 */

public class SendInvitesToOtherScreen extends FragmentActivity implements View.OnClickListener{
    private static final int CONTACTS_FRAGMENT = 0;
    private static final int GROUP_FRAGMENTS = 1;
    private ViewPager viewPager;
    private TextView tabOne;
    private int notification_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_invites_to_other_screen);
        findViewById(R.id.rlGroupIcon).setOnClickListener(this);
        findViewById(R.id.llContacts).setOnClickListener(this);
        findViewById(R.id.tvGroups).setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
      

    }
    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ContactListFragment.newInstance(null), "Contacts");
        adapter.addFrag(GroupListFragment.newInstance(null), "Groups");
        viewPager.setAdapter(adapter);
    }

    public void removeUserClicked(ContactData item) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ContactListFragment contactListFragment =   (ContactListFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(0);
        // Log.d("Count",count+"");
        contactListFragment.removeUserFromGroup(item);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlGroupIcon:
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                ContactListFragment contactListFragment =   (ContactListFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(0);
                contactListFragment.showGrouplistDialog();
                break;

            case R.id.llContacts:
                switchTabs(v);
                break;

            case R.id.tvGroups:
                switchTabs(v);

                break;
        }


    }

    private void switchTabs(View view){
        if(view.getId() == R.id.llContacts){
            viewPager.setCurrentItem(CONTACTS_FRAGMENT);
            ((TextView)findViewById(R.id.tvContacts)).setTextColor(getResources().getColor(R.color.colorWhite));
            ((TextView)findViewById(R.id.tvGroups)).setTextColor(getResources().getColor(R.color.colorDullWhite));
            ((View)findViewById(R.id.leftView)).setBackgroundColor(getResources().getColor(R.color.colorNotifCircle));
            ((View)findViewById(R.id.rightView)).setBackgroundColor(getResources().getColor(R.color.colorTransparent));

        }else {
            viewPager.setCurrentItem(GROUP_FRAGMENTS);
            ((TextView)findViewById(R.id.tvContacts)).setTextColor(getResources().getColor(R.color.colorDullWhite));
            ((TextView)findViewById(R.id.tvGroups)).setTextColor(getResources().getColor(R.color.colorWhite));
            ((View)findViewById(R.id.leftView)).setBackgroundColor(getResources().getColor(R.color.colorTransparent));
            ((View)findViewById(R.id.rightView)).setBackgroundColor(getResources().getColor(R.color.colorNotifCircle));

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
     * Add User from contact list to group
     * - Remove user from main contact list
     * - Add user to group list
     * - notify list
     * - update groupCount
     */
    public void addUserClicked(ContactData contactData){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ContactListFragment contactListFragment =   (ContactListFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(0);
        // Log.d("Count",count+"");
        contactListFragment.addUserToGroup(contactData);

    }

    /**
     * Remove user from created group
     * - Remove user from group list
     * - Add user to contact list
     * - notify list
     * - update groupCount
     */
    public void removeUserFromGroup(){


    }

    public void updateCount(int size) {
        if(size>0){
            findViewById(R.id.rlGroupIcon).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.rlGroupIcon).setVisibility(View.GONE);
        }
        ((TextView)findViewById(R.id.tvCount)).setText(String.valueOf(size));
    }

  /*  Intent intent = new Intent(this,DisplayContact.class);
    intent.putExtra("Contact_list", ContactLis);
    startActivity(intent);

    ArrayList<ContactClass> myList = getIntent().getParcelableExtra("Contact_list");*/




}
