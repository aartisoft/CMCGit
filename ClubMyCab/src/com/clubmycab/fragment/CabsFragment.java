package com.clubmycab.fragment;

import com.clubmycab.R;
import com.clubmycab.ui.HomeActivity;
import com.clubmycab.ui.HomeCarPoolActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;



public class CabsFragment extends Fragment implements OnClickListener{
	 
	public static CabsFragment newInstance(Bundle args) {
		CabsFragment fragment = new CabsFragment();
	    fragment.setArguments(args);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cabs, container, false);

		return v;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.sharecabll).setOnClickListener(this);
		view.findViewById(R.id.llbookacab).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sharecabll:
			Intent mainIntent = new Intent(getActivity(),
					HomeActivity.class);
			mainIntent.putExtra("screentoopen",
					HomeActivity.HOME_ACTIVITY_SHARE_CAB);
			startActivityForResult(mainIntent, 500);
			getActivity().overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);

			break;
			
		case R.id.llbookacab:
			Intent mainIntent2 = new Intent(getActivity(),
					HomeActivity.class);
			mainIntent2.putExtra("screentoopen",
					HomeActivity.HOME_ACTIVITY_BOOK_CAB);
			startActivityForResult(mainIntent2, 500);
			getActivity().overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;

		default:
			break;
		}
	}

	
}