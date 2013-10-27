package com.hackathon.sharetrip;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hackathon.sharetrip.utilities.Constants;
import com.hackathon.sharetrip.utilities.DateTimePicker;
import com.hackathon.sharetrip.utilities.DateTimePicker.ICustomDateTimeListener;
import com.hackathon.sharetrip.utilities.GMapV2GetRouteDirection;
import com.hackathon.sharetrip.utilities.JSONParser;
import com.hackathon.sharetrip.utilities.Preferences;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * @author selva
 *
 */
public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener, ICustomDateTimeListener, ValidationListener {

	@Required(order = 1)
	AutoCompleteTextView source;

	@Required(order = 2)
	AutoCompleteTextView destination;
	Button search, share;
	@Required(order = 3)
	EditText noOfSeats;
	@Required(order = 4)
	EditText ed_date;
	// @Required(order = 5)
	Spinner type;
	String date, time;
	private DateTimePicker dateTimePicker;
	Boolean isShare = true;
	Preferences prefs;
	Validator validator;
	public static ArrayList<HashMap<String, String>> nearByPos = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = new Preferences(this);
		validator = new Validator(this);
		validator.setValidationListener(this);
		source = (AutoCompleteTextView) findViewById(R.id.source);
		destination = (AutoCompleteTextView) findViewById(R.id.destination);
		source.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));
		source.setOnItemClickListener(this);
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(this);
		share = (Button) findViewById(R.id.share);
		share.setOnClickListener(this);
		noOfSeats = (EditText) findViewById(R.id.editText1);
		ed_date = (EditText) findViewById(R.id.editText2);
		ed_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dateTimePicker.showDialog();
				}
			}
		});

		dateTimePicker = new DateTimePicker(MainActivity.this, this);
		dateTimePicker.set24HourFormat(true);
		// dateTimePicker.showDialog();
		type = (Spinner) findViewById(R.id.spinner1);
		type.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
		destination.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));
		destination.setOnItemClickListener(this);
		destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					v2GetRouteDirection = new GMapV2GetRouteDirection();
					try {
						getFromAddress(source.getText().toString());
						getToAddress(destination.getText().toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							toPosition, 11));
					GetRouteTask getRoute = new GetRouteTask();
					getRoute.execute();
				}
			}
		});
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search:

			isShare = false;
			validator.validateAsync();

			break;
		case R.id.share:
			isShare = true;
			validator.validateAsync();

			break;

		default:
			break;
		}

	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
	}

	/***
	 * function to load map. If map is not created it will create it for you
	 * */
	// Google Map
	private GoogleMap googleMap;

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			v2GetRouteDirection = new GMapV2GetRouteDirection();
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			// googleMap.getUiSettings().setAllGesturesEnabled(true);
			// googleMap.setTrafficEnabled(true);
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(googleMap.getMyLocation().getLatitude(),
							googleMap.getMyLocation().getLongitude()), 14));
			// googleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
			markerOptions = new MarkerOptions();
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LON = "lon";

	JSONObject getFromAddress(String address) throws JSONException {
		Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
		JSONObject root = new JSONObject();

		try {
			List<Address> addresses = geoCoder.getFromLocationName(address, 1);
			if (addresses.size() > 0) {
				double latitude = addresses.get(0).getLatitude();
				double longtitude = addresses.get(0).getLongitude();
				fromPosition = new LatLng(latitude, longtitude);
				root.put(KEY_NAME, address);
				root.put(KEY_LAT, latitude);
				root.put(KEY_LON, longtitude);
			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	JSONObject getToAddress(String address) throws JSONException {
		Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
		JSONObject root = new JSONObject();

		try {
			List<Address> addresses = geoCoder.getFromLocationName(address, 1);
			if (addresses.size() > 0) {
				double latitude = addresses.get(0).getLatitude();
				double longtitude = addresses.get(0).getLongitude();
				toPosition = new LatLng(latitude, longtitude);
				root.put(KEY_NAME, address);
				root.put(KEY_LAT, latitude);
				root.put(KEY_LON, longtitude);
			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	Document document;
	GMapV2GetRouteDirection v2GetRouteDirection;
	LatLng fromPosition;
	LatLng toPosition;
	MarkerOptions markerOptions;

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		String response = "";

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MainActivity.this);
			Dialog.setMessage("Loading route...");
			// Dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			// Get All Route values
			document = v2GetRouteDirection.getDocument(fromPosition,
					toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
			response = "Success";
			return response;

		}

		@Override
		protected void onPostExecute(String result) {
			googleMap.clear();
			if (response.equalsIgnoreCase("Success")) {
				ArrayList<LatLng> directionPoint = v2GetRouteDirection
						.getDirection(document);
				PolylineOptions rectLine = new PolylineOptions().width(10)
						.color(Color.GREEN);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}
				// Adding route on the map
				googleMap.addPolyline(rectLine);
				googleMap.addMarker(new MarkerOptions().position(fromPosition));
				googleMap.addMarker(new MarkerOptions().position(toPosition));

			}

		}
	}

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(Constants.PLACES_API_BASE
					+ Constants.TYPE_AUTOCOMPLETE + Constants.OUT_JSON);
			sb.append("?sensor=false&key=" + Constants.API_KEY);
			sb.append("&components=country:in");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(Constants.LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	class PostShareDetails extends AsyncTask<String, String, Boolean> {
		String json = null;
		private ProgressDialog Dialog;

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MainActivity.this);
			Dialog.setMessage("Loading...");
			Dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... f_url) {
			Boolean res = true;
			try {
				JSONParser parser = new JSONParser();
				String src = source.getText().toString();
				String des = destination.getText().toString();
				if (isShare) {
					json = parser.postRegisterUrl(prefs.getName(), prefs
							.getPhoneNumber(), prefs.getGender(),
							getFromAddress(src), getToAddress(des), date, time,
							type.getSelectedItem().toString(), noOfSeats
									.getText().toString(), false, false,
							"share");
				} else {
					json = parser.postRegisterUrl(prefs.getName(), prefs
							.getPhoneNumber(), prefs.getGender(),
							getFromAddress(src), getToAddress(des), date, time,
							type.getSelectedItem().toString(), noOfSeats
									.getText().toString(), false, false,
							"search");
				}

				// json = parser.getJSONFromUrl(f_url[0]);
			} catch (Exception e) {
				e.printStackTrace();
				res = false;
			}

			return res;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			System.out.println(result);
			Dialog.dismiss();
			if (result) {
				try {
					if (isShare) {
						JSONObject res = new JSONObject(json);
						if (res.getBoolean("success")) {
							showToast("Share success");
						} else {
							showToast("Share falied");
						}
					} else {
						parseSearchResults(json);
					}
				} catch (JSONException je) {
					// je.printStackTrace();
					// showToast(getString(R.string.failed));
				} catch (Exception e2) {
					// e2.printStackTrace();
					// showToast(getString(R.string.failed));
				}
			} else {
				showToast(getString(R.string.failed));
			}
		}

	}

	public void showToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
	}

	public void parseSearchResults(String json) {
		// TODO Auto-generated method stub
		try {
			JSONArray result = new JSONArray(json);
			for (int i = 0; i < result.length(); i++) {
				JSONObject user = result.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
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
				map.put(JSONParser.KEY_PHONE_NUMBER,
						user.getString(JSONParser.KEY_PHONE_NUMBER));
				map.put(JSONParser.KEY_GENDER,
						user.getString(JSONParser.KEY_GENDER));

				nearByPos.add(map);

			}
			Intent in = new Intent(MainActivity.this, FeedsListActivity.class);
			startActivity(in);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSet(Calendar calendarSelected, Date dateSelected, int year,
			String monthFullName, String monthShortName, int monthNumber,
			int date, String weekDayFullName, String weekDayShortName,
			int hour24, int hour12, int min, int sec, String AM_PM) {
		this.date = date + ":" + monthNumber + ":" + year;
		this.time = hour24 + ":" + min;
		ed_date.setText(dateSelected.toLocaleString());
	}

	@Override
	public void onCancel() {
		Log.d("datetimepickerdialog", "canceled");
	}

	@Override
	public void preValidation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		new PostShareDetails().execute("");
	}

	@Override
	public void onFailure(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onValidationCancelled() {
		// TODO Auto-generated method stub

	}
}
