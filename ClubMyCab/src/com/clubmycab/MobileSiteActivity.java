package com.clubmycab;

import android.support.v4.app.Fragment;

public class MobileSiteActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return MobileSiteFragment.newInstance(getIntent().getStringExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL), getIntent().getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID), getIntent().getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_USERNAME), getIntent().getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_PASSWORD));
	}

}
