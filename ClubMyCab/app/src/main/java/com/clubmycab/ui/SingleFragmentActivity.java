package com.clubmycab.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.clubmycab.R;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment();
	
	protected int getLayoutResId() {
		return R.layout.activity_fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_fragment);
		setContentView(getLayoutResId());
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
		
		if (fragment == null) {
			fragment = createFragment();
			fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}

}