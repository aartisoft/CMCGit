package com.clubmycab.utility;

public class L {
	
	public static void mesaage(String msg){
		try{
			Log.d("ishare", msg);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
