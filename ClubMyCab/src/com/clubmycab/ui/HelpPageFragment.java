package com.clubmycab.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clubmycab.R;

public class HelpPageFragment extends Fragment {

	private BitmapDrawable mBitmapDrawable;

	public static final String ARGUMENTS_BITMAP = "HelpPageFragment.ArgumentsBitmap";

	public static HelpPageFragment newInstance(Bitmap bitmap) {

		Bundle args = new Bundle();
		args.putParcelable(ARGUMENTS_BITMAP, bitmap);

		HelpPageFragment helpPageFragment = new HelpPageFragment();
		helpPageFragment.setArguments(args);

		return helpPageFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		mBitmapDrawable = new BitmapDrawable(getResources(),
				(Bitmap) getArguments().getParcelable(ARGUMENTS_BITMAP));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = (View) inflater.inflate(R.layout.fragment_help_page,
				container, false);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.imageViewHelpPage);

		imageView.setImageDrawable(mBitmapDrawable);

		return view;
	}

}
