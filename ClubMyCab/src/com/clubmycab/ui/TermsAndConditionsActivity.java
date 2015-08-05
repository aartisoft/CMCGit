package com.clubmycab.ui;

import android.app.Activity;
import android.os.Bundle;

import com.clubmycab.R;

public class TermsAndConditionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tnc);
		
	//	setResult(Activity.RESULT_CANCELED);
		
//		TextView textView = (TextView)findViewById(R.id.textViewTNCLink);
//		textView.setMovementMethod(LinkMovementMethod.getInstance());
//		
//		Button button = (Button)findViewById(R.id.buttonTNCAccept);
//		button.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent mainIntent = new Intent(TermsAndConditionsActivity.this,
//						RegistrationActivity.class);
//				mainIntent.putExtra("source", "phonenumber");
//				startActivity(mainIntent);
//				finish();
//			}
//		});
	}
	

}
