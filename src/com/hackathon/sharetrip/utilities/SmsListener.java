package com.hackathon.sharetrip.utilities;

import com.hackathon.sharetrip.FeedsListActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * @author selva
 *
 */
public class SmsListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras(); // ---get the SMS message passed
												// in---
			SmsMessage[] msgs = null;
			String msg_from;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						String msgBody = msgs[i].getMessageBody();
						Toast.makeText(context,
								"Message" + msgBody,
								Toast.LENGTH_LONG).show();
						if (msgBody.contains("ShareRide :::")) {
							/*Preferences pref = new Preferences(context);
							String[] ms=msgBody.split(":::");
							pref.setMsg(String.valueOf(msgs.length), ms[1]);*/
							Intent in = new Intent(context,
									FeedsListActivity.class);
							in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(in);
						}
					}
				} catch (Exception e) {
					// Log.d("Exception caught",e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
