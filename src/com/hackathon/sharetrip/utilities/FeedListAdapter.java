package com.hackathon.sharetrip.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hackathon.sharetrip.R;

/**
 * @author selva
 *
 */
public class FeedListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public FeedListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView) vi.findViewById(R.id.title); // title
		TextView desc = (TextView) vi.findViewById(R.id.desc); // title

		// Setting all values in listview
		HashMap<String, String> map = data.get(position);

		String tit = map.get(JSONParser.KEY_NAME_S) + "   to   "
				+ map.get(JSONParser.KEY_NAME_D);
		String des = map.get(JSONParser.KEY_DATE) + " - "
				+ map.get(JSONParser.KEY_TIME) + " - "
				+ map.get(JSONParser.KEY_TYPE) + " - Available Seats: "
				+ map.get(JSONParser.KEY_SEATS);
		title.setText(tit);
		desc.setText(des);

		return vi;
	}
}