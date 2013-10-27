package com.hackathon.sharetrip;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hackathon.sharetrip.utilities.FeedListAdapter;
import com.hackathon.sharetrip.utilities.JSONParser;
import com.hackathon.sharetrip.utilities.Preferences;

/**
 * @author selva
 *
 */
public class FeedsListActivity extends Activity implements OnClickListener {
	ListView list;
	FeedListAdapter adapter;
	AlertDialog alert;
	static int pos = -1;
	Preferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeds_list);
		prefs = new Preferences(this);
		list = (ListView) findViewById(R.id.list);
		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					pos = position;
					showAlert(position);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
		initUI();
	}

	void showAlert(final int p) {
		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			// set title
			alertDialogBuilder.setTitle(getString(R.string.app_name));

			// set dialog message
			alertDialogBuilder
					.setMessage(
							"Would you like to share your Ride with this person?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									HashMap<String, String> map = MainActivity.nearByPos
											.get(pos);
									String uID = map.get(JSONParser.KEY_ID);
									String phNo = map
											.get(JSONParser.KEY_PHONE_NUMBER);
									SmsManager smsManager = SmsManager
											.getDefault();
									String msg = "Hi,this is "
											+ prefs.getName()
											+ ". I like to share the ("
											+ map.get(JSONParser.KEY_NAME_D)
											+ ") ride with you. If you are interested please give call back."
											+ ". Thanks";
									smsManager.sendTextMessage(phNo, null, msg,
											null, null);
									String wtsUpId = phNo + "@s.whatsapp.net";
									// openWhatsApp(wtsUpId, msg);

								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void openWhatsApp(String id, String msg) {

		Cursor c = this.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				new String[] { ContactsContract.Contacts.Data._ID },
				ContactsContract.Data.DATA1 + "=?", new String[] { id }, null);
		c.moveToFirst();
		Intent i = new Intent(Intent.ACTION_VIEW,
				Uri.parse("content://com.android.contacts/data/"
						+ c.getString(0)));
		i.putExtra(Intent.EXTRA_TEXT, msg);

		startActivity(i);
		c.close();
	}

	public void initUI() {
		// TODO Auto-generated method stub
		// Getting adapter by passing xml data ArrayList
		try {
			adapter = new FeedListAdapter(this, MainActivity.nearByPos);
			list.setAdapter(adapter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
