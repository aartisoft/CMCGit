package com.clubmycab.asynctasks;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import FetchClubHandler.FetchClubHandler;
import android.R.bool;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.clubmycab.connections.WebRequest;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;

public class GlobalAsyncTask {

	Context context;
	String endPoint;
	String params;
	Object handler;
	boolean showdialog;

	private AsyncTaskResultListener mListener;

	public GlobalAsyncTask(Context context, String endPoint, String params,
			Object handler, AsyncTaskResultListener listener, boolean showdialog) {
		this.showdialog = showdialog;
		this.handler = handler;
		this.context = context;
		this.endPoint = endPoint;
		this.params = params;
		this.mListener = listener;
		// TODO Auto-generated constructor stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new GlobalRequestTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new GlobalRequestTask().execute();
		}
	}

	public interface AsyncTaskResultListener {
		public void getResult(int result, String response);
	}

	public class GlobalRequestTask extends AsyncTask<String, Void, Void> {

		WebRequest wr = new WebRequest();
		private ProgressDialog dialog = new ProgressDialog(context);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (showdialog) {
				dialog.setMessage("Please Wait...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		}

		@Override
		protected void onPostExecute(Void v) {
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
			} else if (handler instanceof FetchClubHandler) {
				
			}

			if (showdialog && dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				mListener.getResult(0, response);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("Exception", e.toString());
				e.printStackTrace();
			}

		};

		@Override
		protected Void doInBackground(String... args) {

			try {
				wr.perform(endPoint, params);
			} catch (Exception e) {
				Log.d("Request Error", e.toString());
			}

			return null;
		}

	}
}
