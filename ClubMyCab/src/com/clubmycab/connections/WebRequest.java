package com.clubmycab.connections;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLException;

import android.util.Log;

public class WebRequest {

	private static final int MAX_REDIRECTS = 5;
	String response;

	// This message only gets visible when the already logged-in user updates
	// the app to v2.5,
	// and HttpsURLConnection fails to establish a trusted connection
	// (SSLException)
	public static final String MESSAGE_SSL_EXCEPTION = "This connection is not trusted. Please logout and retry.";

	private InputStream mInputStream;
	private int mTimesRedirected = 0;

	public void perform(String endpoint, String params)
			throws WebRequestException {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(endpoint);
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					Integer.toString(params.getBytes().length));

			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(params.getBytes().length);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);

			DataOutputStream os = new DataOutputStream(conn.getOutputStream());
			os.writeBytes(params);
			os.flush();
			os.close();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String encoding = conn.getContentEncoding();
				if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
					mInputStream = new GZIPInputStream(conn.getInputStream());
				} else {
					mInputStream = conn.getInputStream();
				}

				try {
					InputStream inputStream = mInputStream;
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);

					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);

					StringBuilder stringBuilder = new StringBuilder();

					String bufferedStrChunk = null;

					while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
						response = stringBuilder.append(bufferedStrChunk)
								.toString();
					}
				} catch (Exception e) {
					Log.e("Error", "InputStream Error");
					e.printStackTrace();
				}

			} else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM // Handle
																			// redirection
					|| responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				String newEndpoint = conn.getHeaderField("Location");
				if (++mTimesRedirected <= MAX_REDIRECTS) {
					perform(newEndpoint, params);
				} else {
					throw new WebRequestException("Too many redirects");
				}
			} else {
				throw new WebRequestException("HTTP Error " + responseCode);
			}
		} catch (MalformedURLException e) {
			throw new WebRequestException(e.getMessage());
		} catch (SSLException e) {
			throw new WebRequestException(MESSAGE_SSL_EXCEPTION);
		} catch (IOException e) {
			throw new WebRequestException(e.getMessage());
		}
	}

	public String getResult() {
		return response;
	}

	@SuppressWarnings("serial")
	public static class WebRequestException extends Exception {

		public WebRequestException(String msg) {
			super(msg);
		}
	}
}
