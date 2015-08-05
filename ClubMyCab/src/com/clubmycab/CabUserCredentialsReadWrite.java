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

import com.clubmycab.utility.Log;

public class CabUserCredentialsReadWrite {
	
	private Context mContext;
    private String mFilename;
    
    public static final String KEY_JSON_CAB_NAME_UBER = "KeyJSONCabNameUber";
    public static final String KEY_JSON_CAB_NAME_MEGA = "KeyJSONCabNameMega";
    public static final String KEY_JSON_CAB_NAME_TFS = "KeyJSONCabNameTFS";
    
    public CabUserCredentialsReadWrite(Context context) {
        mContext = context;
        mFilename = "CabUser.Credentials";
    }
    
    public boolean saveToFile(String userCredentials) throws JSONException, IOException {
        
        Writer writer = null;
         
        try {
        	
        	JSONObject jsonObject = new JSONObject(userCredentials);
        	JSONArray jsonArray = readArrayFromFile();
        	JSONArray newJsonArray = new JSONArray();
        	
        	for (int i = 0; i < jsonArray.length(); i++) {
    			if (!jsonArray.getJSONObject(i).get("CabName").toString().equals(jsonObject.get("CabName").toString())) {
    				newJsonArray.put(jsonArray.getJSONObject(i));
    			}
    		}
        	
        	newJsonArray.put(jsonObject);
        	
		    OutputStream outputStream = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
		    writer = new OutputStreamWriter(outputStream);
		    writer.write(newJsonArray.toString());
		    
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
    
    public JSONObject readObjectFromFile(String cabName) throws IOException {
    	
    	try {
			
    		JSONArray jsonArray = readArrayFromFile();  
        	JSONObject jsonObject = new JSONObject();
            
            for (int i = 0; i < jsonArray.length(); i++) {
    			if (jsonArray.getJSONObject(i).get("CabName").toString().equals(cabName)) {
    				jsonObject = jsonArray.getJSONObject(i);
    			}
    		}
            
            return jsonObject;
		} catch (Exception e) {
			Log.e("RateMyClub", "readObjectFromFile Exception : " + e);
			return new JSONObject();
		}
    	
    }
     
    public JSONArray readArrayFromFile() throws IOException {
         
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
                
                JSONArray jsonArray =  new JSONArray(stringBuilder.toString());
                
                return jsonArray;
			} else {
				Log.d("RateMyClub", "readArrayFromFile : file does not exist");
				return new JSONArray();
			}
        	 
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("RateMyClub", "readArrayFromFile Exception : " + e);
             
            return new JSONArray();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

}
