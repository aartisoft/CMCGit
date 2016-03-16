package com.clubmycab.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.clubmycab.R;
import com.viewpagerindicator.CirclePageIndicator;

public class FAQActivity extends FragmentActivity {
	private ViewPager mViewPagerAns1, mViewPagerAns2, mViewPagerAns3,
			mViewPagerAns4; //, mViewPagerAns5, mViewPagerAns6;
	private FragmentStatePagerAdapter mFragmentStatePagerAdapter1;
	private ArrayList<String> ans1, ans2, ans3, ans4; //, ans5, ans6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq_layout);
		mViewPagerAns1 = (ViewPager) findViewById(R.id.viewPagerans1);
		mViewPagerAns2 = (ViewPager) findViewById(R.id.viewPagerans2);
		mViewPagerAns3 = (ViewPager) findViewById(R.id.viewPagerans3);
		mViewPagerAns4 = (ViewPager) findViewById(R.id.viewPagerans4);
//		mViewPagerAns5 = (ViewPager) findViewById(R.id.viewPagerans5);
//		mViewPagerAns6 = (ViewPager) findViewById(R.id.viewPagerans6);

		ans1 = new ArrayList<String>();
		ans1.add("iShareRyde makes ride sharing safe and convenient");
		ans1.add("You can carpool or share a cab – allows you to share ride even if you don’t have a car");
		ans1.add("Sharing a ride can reduce journey cost to as low as Rs. 2/km");
		ans1.add("Sharing works best with people you trust – your colleagues, friends and friends of friends");

		ans2 = new ArrayList<String>();
		ans2.add("Sharing with a group gives you flexibility to leave at different times and not have to wait.");
		ans2.add("Bigger groups increase chances of ride sharing. Someone or other will be ready to leave at same time as you");
		ans2.add("Keep groups focused to friends who travel same route as you");
		ans2.add("Name your groups to show destinations. Or just choose a fun name");
		ans2.add("Add a profile image - helps your friends identify you");

		ans3 = new ArrayList<String>();
		ans3.add("For your safety, we show only rides open in your groups");
		ans3.add("If there is no ride in your groups that you can join, you can start one");

		ans4 = new ArrayList<String>();
		ans4.add("If it is a carpool, the person offering the ride will charge Rs.3/km per seat");
		ans4.add("If you are sharing a cab, the cost will depend on bill");
		ans4.add("Wallet to wallet transfer in the app is most convenient for paying");

//		ans5 = new ArrayList<String>();
//		ans5.add("We show you choice of cabs with estimated costs and arrival time");
//		ans5.add("You can sort the cabs on Book a Cab page by nearest and cheapest. Click the icons that appear on top");
//		ans5.add("Book the cab when you are ready to leave - iShareRyde is about sharing on-the-go");
//		ans5.add("You can book a cab even if you are riding alone");
//
//		ans6 = new ArrayList<String>();
//		ans6.add("We do not share phone numbers of group members till they join a ride with you to protect privacy");
//		ans6.add("Instant Messenger history is deleted on ride completion");
//		ans6.add("\"Here, I am\" can be used to let your loved ones know where you are through the journey. Activate this when you want");

		setAdapter1();
		setAdapter2();
		setAdapter3();
		setAdapter4();
//		setAdapter5();
//		setAdapter6();

	}

	public void setAdapter1() {

		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return ans1.size();
			}

			@Override
			public Fragment getItem(int position) {

				return TextPageFragment.newInstance(ans1.get(position));
			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}

		};

		mViewPagerAns1.setAdapter(mFragmentStatePagerAdapter1);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator1);
		circlePageIndicator.setViewPager(mViewPagerAns1, 0);
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

		mViewPagerAns1.setCurrentItem(0);

	}

	public void setAdapter2() {

		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return ans2.size();
			}

			@Override
			public Fragment getItem(int position) {

				return TextPageFragment.newInstance(ans2.get(position));
			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}

		};

		mViewPagerAns2.setAdapter(mFragmentStatePagerAdapter1);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator2);
		circlePageIndicator.setViewPager(mViewPagerAns2, 0);
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

		mViewPagerAns2.setCurrentItem(0);

	}

	public void setAdapter3() {

		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return ans3.size();
			}

			@Override
			public Fragment getItem(int position) {

				return TextPageFragment.newInstance(ans3.get(position));
			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}

		};

		mViewPagerAns3.setAdapter(mFragmentStatePagerAdapter1);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator3);
		circlePageIndicator.setViewPager(mViewPagerAns3, 0);
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

		mViewPagerAns3.setCurrentItem(0);

	}

	public void setAdapter4() {

		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return ans4.size();
			}

			@Override
			public Fragment getItem(int position) {

				return TextPageFragment.newInstance(ans4.get(position));
			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}

		};

		mViewPagerAns4.setAdapter(mFragmentStatePagerAdapter1);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator4);
		circlePageIndicator.setViewPager(mViewPagerAns4, 0);
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

		mViewPagerAns4.setCurrentItem(0);

	}

//	public void setAdapter5() {
//
//		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
//				getSupportFragmentManager()) {
//
//			@Override
//			public int getCount() {
//				return ans5.size();
//			}
//
//			@Override
//			public Fragment getItem(int position) {
//
//				return TextPageFragment.newInstance(ans5.get(position));
//			}
//
//			@Override
//			public int getItemPosition(Object object) {
//				return PagerAdapter.POSITION_NONE;
//			}
//
//		};
//
//		mViewPagerAns5.setAdapter(mFragmentStatePagerAdapter1);
//
//		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator5);
//		circlePageIndicator.setViewPager(mViewPagerAns5, 0);
//		circlePageIndicator
//				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//					@Override
//					public void onPageSelected(int position) {
//						// Log.d(TAG, "circlePageIndicator onPageSelected : " +
//						// position);
//						// mCurrentIndex = position;
//					}
//
//					@Override
//					public void onPageScrolled(int position,
//							float positionOffset, int positionOffsetPixels) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onPageScrollStateChanged(int state) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//
//		mViewPagerAns5.setCurrentItem(0);
//
//	}
//
//	public void setAdapter6() {
//
//		mFragmentStatePagerAdapter1 = new FragmentStatePagerAdapter(
//				getSupportFragmentManager()) {
//
//			@Override
//			public int getCount() {
//				return ans6.size();
//			}
//
//			@Override
//			public Fragment getItem(int position) {
//
//				return TextPageFragment.newInstance(ans6.get(position));
//			}
//
//			@Override
//			public int getItemPosition(Object object) {
//				return PagerAdapter.POSITION_NONE;
//			}
//
//		};
//
//		mViewPagerAns6.setAdapter(mFragmentStatePagerAdapter1);
//
//		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator6);
//		circlePageIndicator.setViewPager(mViewPagerAns6, 0);
//		circlePageIndicator
//				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//					@Override
//					public void onPageSelected(int position) {
//						// Log.d(TAG, "circlePageIndicator onPageSelected : " +
//						// position);
//						// mCurrentIndex = position;
//					}
//
//					@Override
//					public void onPageScrolled(int position,
//							float positionOffset, int positionOffsetPixels) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onPageScrollStateChanged(int state) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//
//		mViewPagerAns6.setCurrentItem(0);
//
//	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(FAQActivity.this, HomeCarPoolActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
