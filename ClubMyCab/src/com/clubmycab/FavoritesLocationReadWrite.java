package com.clubmycab;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class FavoritesLocationReadWrite {
	
	private Context mContext;
    private String mFilename;
    
    public FavoritesLocationReadWrite(Context context) {
        mContext = context;
        mFilename = "homeoffice.favorite";
    }
    
    public boolean saveToFile(String address) throws JSONException, IOException {
        
        Writer writer = null;
         
        try {
        	JSONObject jsonObject = new JSONObject(address);
        	JSONArray jsonArray = readFromFile();
        	JSONArray newJSONArray = new JSONArray();
        	
//        	int indexDelete = -1;
        	
        	for (int i = 0; i < jsonArray.length(); i++) {
				if (!jsonArray.getJSONObject(i).get("type").equals(jsonObject.get("type"))) {
					newJSONArray.put(jsonArray.getJSONObject(i));
				}
			}
        	
//        	if (indexDelete != -1) {
//        		jsonArray.remove(indexDelete);
//        		jsonArray.
//			}
        	
        	newJSONArray.put(jsonObject);
        	
            OutputStream outputStream = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(newJSONArray.toString());
            
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("RateMyClub", "saveToFile Exception : " + e);
            return false;
            
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
     
    public JSONArray readFromFile() throws IOException {
         
        BufferedReader bufferedReader = null;
         
        try {
            
        	File file = new File(mContext.getFilesDir().getPath() + "/" + mFilename);
        	if (file.exists()) {
        		InputStream inputStream = mContext.openFileInput(mFilename);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                 
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                 
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                
                return new JSONArray(stringBuilder.toString());
			} else {
				Log.d("RateMyClub", "readFromFile : file does not exist");
				return new JSONArray();
			}
        	 
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("RateMyClub", "readFromFile Exception : " + e);
             
            return new JSONArray();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

}
