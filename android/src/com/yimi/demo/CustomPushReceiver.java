package com.yimi.demo;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;

public class CustomPushReceiver extends XGPushBaseReceiver {
	
	static final String mAllActions[] = {"com.yimi.newvoice"
							};	

	public static final int MESSAGE_NEWVOICE = 0;
	public static final String ACTION__NEWVOICE = mAllActions[MESSAGE_NEWVOICE]; 
	
	String mUserID;
	
	class RegisteRunnable implements Runnable{

		@Override
		public void run() {
			String imeiString = ContextUtil.getInstance().getImei();
			if("".equals(imeiString)){
				ContextUtil.getInstance().runOnUIThread(this, 10 * 1000);
			}
			else{
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("pushid", (String)mUserID);
				param.put("imei", imeiString);
				HttpHelper.DemoHttp_Get("user/register", param, new HttpCallback() {
					
					@Override
					public void onResponse(byte[] result, int status, int id,
							Object callbackData, String responseType, Header[] headers) {

						if(HttpClientService.SuccessID != status){
							ContextUtil.getInstance().runOnUIThread(new RegisteRunnable(), 5 * 60 * 1000);
						}
						
					}
				}, 0, null);
			}
		}
		
	}
	
	RegisteRunnable mRegisteRunnable = new RegisteRunnable();
	
	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegisterResult(Context arg0, int errorcode,
			XGPushRegisterResult result) {
		if(errorcode == XGPushBaseReceiver.SUCCESS){

			mUserID = result.getToken();
			ContextUtil.getInstance().removeCall(mRegisteRunnable);
			ContextUtil.getInstance().runOnUIThread(mRegisteRunnable,1000 * 3);
		}
		else{
			ContextUtil.getInstance().runOnUIThread(new Runnable() {
				
				@Override
				public void run() {
					XGPushManager.registerPush(ContextUtil.getInstance());
				}
			}, 10 * 1000);
		}

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage message) {
		try {
			JSONObject messageJson = new JSONObject(message.getContent());
			int messagetype = messageJson.getInt("type"); 
			
			if(messagetype < mAllActions.length){
				Log.d("dog", "Receive push message : " + mAllActions[messagetype]);
				Intent intent = new Intent(mAllActions[messagetype]);
				intent.putExtra("data", message.getContent());
				ContextUtil.getInstance().sendBroadcast(intent);
			}
			else{
				Log.e("dog", "push message not defined!");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
