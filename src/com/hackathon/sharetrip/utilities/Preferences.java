package com.hackathon.sharetrip.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author selva
 *
 */
public class Preferences {
	Context mContext;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	public Preferences(Context mContext) {
		super();
		this.mContext = mContext;
		prefs = mContext.getSharedPreferences("shareride", 0);
		editor = prefs.edit();
	}

	public String getPhoneNumber() {
		return prefs.getString("phone", null);
	}

	public void setPhoneNumber(String phNo) {
		editor.putString("phone", phNo);
		editor.commit();
	}

	public String getName() {
		return prefs.getString("name", null);
	}
	

	public void setGender(String name) {
		editor.putString("gender", name);
		editor.commit();
	}
	
	public String getGender() {
		return prefs.getString("gender", null);
	}
	

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	public Boolean getRegStatus() {
		return prefs.getBoolean("register", false);
	}

	public void setRegStatus(Boolean name) {
		editor.putBoolean("register", name);
		editor.commit();
	}

	public ArrayList<HashMap<String, String>> getAllMsg() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		SharedPreferences p = mContext
				.getSharedPreferences("shareride_msgs", 0);
		editor = prefs.edit();
		Map<String, ?> keys = p.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			list.add(parseSearchResults(entry.getValue().toString()));
		}
		return list;
	}

	public HashMap<String, String> parseSearchResults(String json) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = null;
		try {
			JSONArray result = new JSONArray(json);
			for (int i = 0; i < result.length(); i++) {
				JSONObject user = result.getJSONObject(i);
				map = new HashMap<String, String>();
				map.put(JSONParser.KEY_USER,
						user.getString(JSONParser.KEY_USER));
				map.put(JSONParser.KEY_TYPE,
						user.getString(JSONParser.KEY_TYPE));
				map.put(JSONParser.KEY_TIME,
						user.getString(JSONParser.KEY_TIME));
				map.put(JSONParser.KEY_DATE,
						user.getString(JSONParser.KEY_DATE));
				map.put(JSONParser.KEY_ID, user.getString(JSONParser.KEY_ID));
				map.put(JSONParser.KEY_SEATS,
						user.getString(JSONParser.KEY_SEATS));
				JSONObject src = user.getJSONObject(JSONParser.KEY_SRC_OBJ);
				map.put(JSONParser.KEY_NAME_S,
						src.getString(JSONParser.KEY_NAME));
				JSONArray sloc = src.getJSONArray(JSONParser.KEY_LOC);
				map.put(JSONParser.KEY_SRC_LAT_LON, sloc.getDouble(0) + ","
						+ sloc.getDouble(1));

				JSONObject des = user.getJSONObject(JSONParser.KEY_DES_OBJ);
				map.put(JSONParser.KEY_NAME_D,
						des.getString(JSONParser.KEY_NAME));
				JSONArray dloc = des.getJSONArray(JSONParser.KEY_LOC);
				map.put(JSONParser.KEY_DES_LAT_LON, dloc.getDouble(0) + ","
						+ dloc.getDouble(1));
				// map.put(JSONParser.KEY_PHONE_NUMBER,
				// user.getString(JSONParser.KEY_PHONE_NUMBER));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public void setMsg(String key, String value) {
		SharedPreferences p = mContext
				.getSharedPreferences("shareride_msgs", 0);
		SharedPreferences.Editor er = p.edit();
		er.putString(key, value);
		er.commit();
	}
}
