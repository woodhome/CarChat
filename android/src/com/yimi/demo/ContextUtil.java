package com.yimi.demo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.yimi.demo.data.ChatMessageManager;

public class ContextUtil extends Application {

	static ContextUtil mInstance = null;
	String mImei;
	Handler mHandler = new Handler();
	
	@Override
	public void onCreate(){
		super.onCreate();
		mInstance = this;
		ChatMessageManager.getInstance().start();
	}
	
	public static ContextUtil getInstance(){
		return mInstance;
	}
	
	public void runOnUIThread(Runnable r){
		mHandler.post(r);
	}
	
	public void runOnUIThread(Runnable r,long delay){
		mHandler.postDelayed(r, delay);
	}
	
	public void removeCall(Runnable r){
		mHandler.removeCallbacks(r);
	}
	
	  public String getImei(){
		  if(mImei!=null){
			  return mImei;
		  }
	      TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	      mImei = telephonyManager.getDeviceId();
	      if(null == mImei){
	    	  return "";
	      }
		  return mImei;
	  }
}
