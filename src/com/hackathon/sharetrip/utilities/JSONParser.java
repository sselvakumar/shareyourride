package com.hackathon.sharetrip.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * @author selva
 *
 */
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	Context mContext;

	public JSONParser(Context mContext) {
		super();
		this.mContext = mContext;
	}

	// constructor
	public JSONParser() {

	}

	public String getJSONFromUrl(String url) {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpPost = new HttpGet(url);
			// HttpPost httpPost = new HttpPost(url);
			System.out.println(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
		}
		System.out.println(json);
		// return JSON String
		return json;

	}

	public static final String KEY_USER = "userId";
	public static final String KEY_LOC = "loc";
	public static final String KEY_NAME_S = "sname";
	public static final String KEY_LAT_S = "slat";
	public static final String KEY_LON_s = "slon";

	public static final String KEY_NAME_D = "dname";
	public static final String KEY_LAT_D = "dlat";
	public static final String KEY_LON_D = "dlon";

	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LON = "lon";

	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_IS_SRC_EVENT = "isSrcEvent";
	public static final String KEY_IS_DES_EVENT = "isDestEvent";
	public static final String KEY_ENDPONT = "";
	public static final String KEY_SRC_OBJ = "source";
	public static final String KEY_DES_OBJ = "dest";
	public static final String KEY_EVENT = "event";
	public static final String KEY_ROOT = "helperObject";
	public static final String KEY_TYPE = "type";
	public static final String KEY_PHONE_NUMBER = "phoneNumber";
	public static final String KEY_NO_OF_SEATS = "rseats";
	public static final String KEY_SEATS = "seats";

	public static final String KEY_ID = "_id";
	public static final String KEY_SRC_LAT_LON = "srclatlon";
	public static final String KEY_DES_LAT_LON = "deslatlon";
	public static final String KEY_GENDER = "gender";

	/*
	 * shareparameters:{ "userId": id, "source": { "name": "Murg", "loc": [ 1, 1
	 * ] }, "isSrcEvent": false, "dest": { "name": "Murg", "loc": [ 1, 1 ] },
	 * "isDestEvent": false, "type": "car", "rseats": 3, "date": "27/03/2013",
	 * "time": "17:30" }
	 */
	public String postRegisterUrl(String userName, String phoneNumber,
			String gender, JSONObject srcLoc, JSONObject desLoc, String date,
			String time, String type, String noOfSeats, Boolean isSrcEvent,
			Boolean isDesEvent, String endpoint) throws JSONException {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL + endpoint);
			httpPost.setHeader("Content-Type", "application/json");
			JSONObject helperObject = new JSONObject();
			JSONObject root = new JSONObject();
			try {
				root.put(KEY_USER, userName);

				root.put(KEY_NAME_S, srcLoc.get(KEY_NAME));
				root.put(KEY_LAT_S, srcLoc.get(KEY_LAT));
				root.put(KEY_LON_s, srcLoc.get(KEY_LON));
				root.put(KEY_IS_SRC_EVENT, isSrcEvent);

				root.put(KEY_NAME_D, desLoc.get(KEY_NAME));
				root.put(KEY_LAT_D, desLoc.get(KEY_LAT));
				root.put(KEY_LON_D, desLoc.get(KEY_LON));
				root.put(KEY_IS_DES_EVENT, isDesEvent);

				root.put(KEY_TYPE, type);
				root.put(KEY_NO_OF_SEATS, noOfSeats);
				root.put(KEY_DATE, date);
				root.put(KEY_TIME, time);
				root.put(KEY_PHONE_NUMBER, phoneNumber);
				root.put(KEY_GENDER, gender);

				helperObject.put(KEY_ROOT, root);
				System.out.println(helperObject);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			StringEntity se = new StringEntity(helperObject.toString());
			/*
			 * se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
			 * "application/x-www-form-urlencoded"));
			 */
			try {
				httpPost.setEntity(se);
			} catch (Exception e) {
				e.printStackTrace();
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
		}
		System.out.println(json);
		// return JSON String
		return json;

	}

	private JSONObject getLocObject(String src, JSONArray loc)
			throws JSONException {
		return new JSONObject().put(KEY_NAME, src).put(KEY_LOC, loc);
		// TODO Auto-generated method stub
	}

	private JSONArray getLocAry(Double lat, Double lon) throws JSONException {
		JSONArray ary = new JSONArray();
		ary.put(lat);
		ary.put(lon);
		return ary;
	}
}
