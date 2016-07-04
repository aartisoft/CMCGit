package com.clubmycab.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListview extends ListView{
	
	public CustomListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	   if(ev.getAction()==MotionEvent.ACTION_MOVE)
	      return true;
	   return super.dispatchTouchEvent(ev);
	}

	

}
