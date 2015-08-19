package com.clubmycab.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.clubmycab.R;
import com.viewpagerindicator.CirclePageIndicator;

public class AboutPagerFragmentActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private FragmentStatePagerAdapter mFragmentStatePagerAdapter;
	private ArrayList<Drawable> mHelpPageDrawables;

	private String mStartedFrom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mStartedFrom = null;
		try {
			if (!getIntent().getStringExtra("mStartedFrom").isEmpty()) {
				mStartedFrom = getIntent().getStringExtra("mStartedFrom");
			}
		} catch (Exception e) {
			mStartedFrom = null;
		}

		mHelpPageDrawables = new ArrayList<Drawable>();
		mHelpPageDrawables.add(getResources().getDrawable(R.drawable.screen1));
		mHelpPageDrawables.add(getResources().getDrawable(R.drawable.screen2));
		mHelpPageDrawables.add(getResources().getDrawable(R.drawable.screen3));
		mHelpPageDrawables.add(getResources().getDrawable(R.drawable.screen4));

		setContentView(R.layout.activity_help_pager);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);

		mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mHelpPageDrawables.size();
			}

			@Override
			public Fragment getItem(int position) {
				Bitmap bitmap = ((BitmapDrawable) (mHelpPageDrawables
						.get(position))).getBitmap();

				return HelpPageFragment.newInstance(bitmap);
			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}
		};

		mViewPager.setAdapter(mFragmentStatePagerAdapter);

		// TitlePageIndicator titleIndicator =
		// (TitlePageIndicator)findViewById(R.id.titles);
		// titleIndicator.setViewPager(mViewPager);

		// LinePageIndicator linePageIndicator =
		// (LinePageIndicator)findViewById(R.id.indicator);
		// linePageIndicator.setViewPager(mViewPager, mCurrentIndex);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		circlePageIndicator.setViewPager(mViewPager, 0);
		circlePageIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						// Log.d(TAG, "circlePageIndicator onPageSelected : " +
						// position);
						// mCurrentIndex = position;
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int state) {
						// TODO Auto-generated method stub

					}
				});

		mViewPager.setCurrentItem(0);

		Button button = (Button) findViewById(R.id.buttonGetStarted);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mStartedFrom != null) {
					Intent mainIntent = new Intent(
							AboutPagerFragmentActivity.this,
							LoginActivity.class);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
					finish();
				} else {

//					Intent mainIntent = new Intent(
//							AboutPagerFragmentActivity.this, HomeActivity.class);
//					mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
//					startActivityForResult(mainIntent, 500);
//					overridePendingTransition(R.anim.slide_in_right,
//							R.anim.slide_out_left);

					finish();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

//		Intent mainIntent = new Intent(AboutPagerFragmentActivity.this,
//				HomeActivity.class);
//		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		startActivityForResult(mainIntent, 500);
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
