package com.clubmycab.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clubmycab.R;

public class TextPageFragment extends Fragment {

	private String mBitmapDrawable;
public static String TAG="1";

	public static TextPageFragment newInstance(String bitmap) {

		Bundle args = new Bundle();
		args.putString(TAG, bitmap);
		//args.put("1", bitmap);

		TextPageFragment helpPageFragment = new TextPageFragment();
		helpPageFragment.setArguments(args);

		return helpPageFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		
		

		mBitmapDrawable = 
				getArguments().getString(TAG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = (View) inflater.inflate(R.layout.fragment_textpagger_layout,
				container, false);

		TextView tvPaggerItem = (TextView) view
				.findViewById(R.id.tvPaggerItem);

		tvPaggerItem.setText(mBitmapDrawable);

		return view;
	}

}
