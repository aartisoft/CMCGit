package com.clubmycab.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.ArrayList;

public class FAQActivity extends FragmentActivity implements GlobalAsyncTask.AsyncTaskResultListener {
    private ViewPager mViewPagerAns1, mViewPagerAns2, mViewPagerAns3, mViewPagerAns4; //, mViewPagerAns5, mViewPagerAns6;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter1;
    private ArrayList<String> ans1, ans2, ans3, ans4; //, ans5, ans6;
    private Tracker tracker;
    private AppEventsLogger logger;
    private ImageView notificationimg;
    private String FullName, MobileNumber, myprofileresp, imagenameresp;
    private TextView unreadnoticount;
    private RelativeLayout unreadnoticountrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_layout);
        ((TextView) findViewById(R.id.tvFaq)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion1)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer1)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion2)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer2)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion3)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer3)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion4)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer4)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion5)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer5)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion6)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer6)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion7)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer7)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion8)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer8)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion9)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer9)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion10)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer10)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion11)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer11)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvQuestion12)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvAnswer12)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvGTDQ)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvGTDA)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        setNotificationAndProfileImage();
        ((TextView) findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(FAQActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvHeading)).setText("FAQs");
        findViewById(R.id.flBackArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(FAQActivity.this, NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private void setNotificationAndProfileImage() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(FAQActivity.this);
        tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
        tracker.setScreenName("FAQScreen");
        logger = AppEventsLogger.newLogger(this);
        GlobalVariables.ActivityName = "FAQScreen";
        notificationimg = (ImageView) findViewById(R.id.notificationimg);
        SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
        FullName = mPrefs.getString("FullName", "");
        MobileNumber = mPrefs.getString("MobileNumber", "");
        unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
        unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
        findViewById(R.id.flNotifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(FAQActivity.this, NotificationListActivity.class);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        String endpoint = GlobalVariables.ServiceUrl + "/FetchUnreadNotificationCount.php";
        String authString = MobileNumber;
        String params = "MobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
        new GlobalAsyncTask(this, endpoint, params, new FetchUnreadNotificationCountHandler(), this, false, "FetchUnreadNotificationCount", false);

    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FAQActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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

        Intent mainIntent = new Intent(FAQActivity.this, NewHomeScreen.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(mainIntent, 500);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void getResult(String response, String uniqueID) {
        if (uniqueID.equals("FetchUnreadNotificationCount")) {
            if (response != null && response.length() > 0
                    && response.contains("Unauthorized Access")) {
                Log.e("HomeActivity",
                        "FetchUnreadNotificationCount Unauthorized Access");
                Toast.makeText(FAQActivity.this,
                        getResources().getString(R.string.exceptionstring),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

                unreadnoticountrl.setVisibility(View.GONE);

            } else {

                unreadnoticountrl.setVisibility(View.VISIBLE);
                unreadnoticount
                        .setText(GlobalVariables.UnreadNotificationCount);
            }
        }
    }
}
