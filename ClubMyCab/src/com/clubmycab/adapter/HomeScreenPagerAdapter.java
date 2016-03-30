package com.clubmycab.adapter;

import com.clubmycab.fragment.CabsFragment;
import com.clubmycab.fragment.MyRidesFragment;
import com.clubmycab.fragment.RidesAvailableFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class HomeScreenPagerAdapter extends FragmentStatePagerAdapter {

	CharSequence Titles[];
	int NumbOfTabs;

	public HomeScreenPagerAdapter(FragmentManager fm, CharSequence mTitles[],
			int mNumbOfTabsumb) {
		super(fm);
		this.Titles = mTitles;
		this.NumbOfTabs = mNumbOfTabsumb;
	}
	// This method return the fragment for the every position in the View Pager
	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return RidesAvailableFragment.newInstance(null);

		case 1:
			return MyRidesFragment.newInstance(null);
		case 2:
			return CabsFragment.newInstance(null);
		}
		return null;
	}
	// This method return the titles for the Tabs in the Tab Strip
	@Override
	public CharSequence getPageTitle(int position) {
		return Titles[position];
	}
	// This method return the Number of tabs for the tabs Strip
	@Override
	public int getCount() {
		return NumbOfTabs;
	}
}