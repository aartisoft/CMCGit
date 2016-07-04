package com.clubmycab.asynctasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Window;

import com.clubmycab.R;
import com.clubmycab.connections.WebRequest;
import com.clubmycab.connections.WebRequestJSON;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

public class GlobalAsyncTask {

	Context context;
	String endPoint;
	String params;
	Object handler;
	boolean showdialog;
	String uniqueIdentifier;
	boolean jsonWebRequest;

	private AsyncTaskResultListener mListener;
	private Dialog onedialog;

	public GlobalAsyncTask(Context context, String endPoint, String params,
			Object handler, AsyncTaskResultListener listener,
			boolean showdialog, String uniqueID, boolean useJSONWebRequest) {
		this.showdialog = showdialog;
		this.handler = handler;
		this.context = context;
		this.endPoint = endPoint;
		this.params = params;
		this.mListener = listener;
		this.uniqueIdentifier = uniqueID;
		this.jsonWebRequest = useJSONWebRequest;
		// TODO Auto-generated constructor stub

		if (useJSONWebRequest) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new GlobalRequestTaskJSON()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new GlobalRequestTaskJSON().execute();
			}
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new GlobalRequestTask()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new GlobalRequestTask().execute();
			}
		}
	}

	public interface AsyncTaskResultListener {
		public void getResult(String response, String uniqueID);
	}

	public class GlobalRequestTask extends AsyncTask<String, Void, Void> {

		WebRequest wr = new WebRequest();
		private ProgressDialog dialog = new ProgressDialog(context);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			try{
				if(dialog != null){
					if (showdialog) {
						showProgressBar();
						/*dialog.setMessage("Please Wait...");
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();*/
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Void v) {
			try{
				String response = wr.getResult();
				if (handler != null) {
					if (handler instanceof DefaultHandler) {
						XMLReader xmlReader;
						try {
							xmlReader = SAXParserFactory.newInstance()
									.newSAXParser().getXMLReader();
							xmlReader.setContentHandler((DefaultHandler) handler);
							xmlReader.parse(new InputSource(new StringReader(wr
									.getResult())));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (handler instanceof FetchUnreadNotificationCountHandler) {
						((FetchUnreadNotificationCountHandler) handler)
								.setCount(response);
					}
				}

				if (showdialog ) {
					/*dialog.dismiss();*/
					hideProgressBar();
				}
				mListener.getResult(response, uniqueIdentifier);
			}catch (Exception e){
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(String... args) {

			try {
				wr.perform(endPoint, params);
			} catch (Exception e) {
				Log.d("Request Error", e.toString());
				e.printStackTrace();
			}

			return null;
		}

	}

	public class GlobalRequestTaskJSON extends AsyncTask<String, Void, Void> {

		WebRequestJSON wr = new WebRequestJSON();
		private ProgressDialog dialog = new ProgressDialog(context);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			try{
				if (showdialog) {
                    showProgressBar();
				/*if(dialog != null){
					dialog.setMessage("Please Wait...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}*/
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Void v) {
			try{
				String response = wr.getResult();
				if (handler instanceof DefaultHandler) {
					XMLReader xmlReader;
					try {
						xmlReader = SAXParserFactory.newInstance().newSAXParser()
								.getXMLReader();
						xmlReader.setContentHandler((DefaultHandler) handler);
						xmlReader.parse(new InputSource(new StringReader(wr
								.getResult())));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (handler instanceof FetchUnreadNotificationCountHandler) {
					((FetchUnreadNotificationCountHandler) handler)
							.setCount(response);
				}

				if (showdialog ) {
					//dialog.dismiss();
                    hideProgressBar();
				}
				try {
					mListener.getResult(response, uniqueIdentifier);
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("Exception", e.toString());
					e.printStackTrace();
				}

			}catch (Exception e){
				e.printStackTrace();
			}
		};

		@Override
		protected Void doInBackground(String... args) {

			try {
				wr.perform(endPoint, params);
			} catch (Exception e) {
				Log.d("Request Error", e.toString());
				e.printStackTrace();
			}

			return null;
		}

	}

	private void showProgressBar(){
		try{
			onedialog = new Dialog(context);
			onedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onedialog.setContentView(R.layout.dialog_ishare_loader);
			onedialog.setCancelable(false);
			onedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			// onedialog.getWindow().setB(getResources().getColor(R.color.colorTransparent));
			onedialog.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void hideProgressBar(){
		try{
			if(onedialog != null)
				onedialog.dismiss();
			onedialog = null;
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
