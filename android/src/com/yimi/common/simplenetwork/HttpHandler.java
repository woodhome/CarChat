package com.yimi.common.simplenetwork;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class HttpHandler extends Handler {
	static HttpHandler mInstance = null;
	public static final int HttpMessageID = 0;
	Activity mCurActivity;
	@Override
	public void handleMessage(Message msg) {
		if(msg.what != HttpMessageID)
			return;
		else{
			MyRequest req = (MyRequest)msg.obj;
			if(req.mCb !=null){
				req.mCb.onResponse(req.mResult,msg.arg1 ,req.mid,req.mCallbackData,req.mResponseContentType,req.headers);
			}
		}
	}
	
	private HttpHandler(){
		super();
		mCurActivity = null;
	}
	public static HttpHandler getInstance(){
		if(null == mInstance){
			mInstance = new HttpHandler();
		}
		return mInstance;
	}
	
	public void setCurActivity(Activity activity){
		mCurActivity = activity;
	}
}
