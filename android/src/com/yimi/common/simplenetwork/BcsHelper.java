package com.yimi.common.simplenetwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.http.HttpMethodName;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BCSServiceException;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.GenerateUrlRequest;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.yimi.demo.ContextUtil;


public class BcsHelper {
	 static final String TAG = "BCS";
	 static final String host = "bcs.duapp.com";
	 static String accessKey = "QjFH8nzVBiGY28ikdU3Upf15";
	 static String secretKey = "2hKaFNH7dmE6gew0fow19h5sEGkIh36r";
	 
	 public interface BcsListener{
		 public void onSuccessed(String ojectName, byte[] data);
		 public void onFialed(String ojectName);
	 }
	 
	 static class BcsRunnable implements Runnable{
		 public static final int BCS_FAILED = 1;
		 public static final int BCS_SUCCESS = 0;
		 BcsListener mListener;
		 String mObjName;
		 byte[] mData;
		 int mType;
		 public BcsRunnable(int type,String obj,BcsListener listener,byte[] data){
			 mData= data;
			 mType = type;
			 mListener = listener;
			 mObjName = obj;
		 }
		@Override
		public void run() {
			if(null==mListener){
				return;
			}
			if(BCS_FAILED == mType){
				mListener.onFialed(mObjName);
			}
			else if(BCS_SUCCESS == mType){
				mListener.onSuccessed(mObjName, mData);
			}
		}
		 
	 }
	 
	 public static void putObjectt(String bucket,final String objname,byte[] data,final BcsListener listener){

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(data.length);
	    	final PutObjectRequest request = new PutObjectRequest(bucket, "/"+objname, new ByteArrayInputStream(data), metadata);
	    	new Thread(new Runnable() {
				
				@Override
				public void run() {
					try{
						BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
					    BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
					    baiduBCS.setDefaultEncoding("UTF-8");
				    	baiduBCS.putObject(request);
				    	ContextUtil.getInstance().runOnUIThread(
				    			new BcsRunnable(BcsRunnable.BCS_SUCCESS, objname, listener, null));
					}
					catch (BCSServiceException e) {
				    	ContextUtil.getInstance().runOnUIThread(
				    			new BcsRunnable(BcsRunnable.BCS_FAILED, objname, listener, null));
					}
					catch(BCSClientException e){
				    	ContextUtil.getInstance().runOnUIThread(
				    			new BcsRunnable(BcsRunnable.BCS_FAILED, objname, listener, null));
					}
				}
			}).start();
	 }
	
	public static void putObjectt(String bucket,File file,final String objname,final BcsListener listener){

    	final PutObjectRequest request = new PutObjectRequest(bucket, "/"+objname,file);

    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
				    BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
				    baiduBCS.setDefaultEncoding("UTF-8");
			    	baiduBCS.putObject(request);
			    	ContextUtil.getInstance().runOnUIThread(
			    			new BcsRunnable(BcsRunnable.BCS_SUCCESS, objname, listener, null));
				}
				catch (BCSServiceException e) {
			    	ContextUtil.getInstance().runOnUIThread(
			    			new BcsRunnable(BcsRunnable.BCS_FAILED, objname, listener, null));
				}
				catch(BCSClientException e){
			    	ContextUtil.getInstance().runOnUIThread(
			    			new BcsRunnable(BcsRunnable.BCS_FAILED, objname, listener, null));
				}
			}
		}).start();
	}
	
	static public void getBCSObject(String bucket, final String object, final BcsListener listener){
		final GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, "/" + object);
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);		
					BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
					baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
					BaiduBCSResponse<DownloadObject> resp = baiduBCS.getObject(getObjectRequest);
					InputStream ins = resp.getResult().getContent();
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					int count = -1;  
					try {
						byte[] data = new byte[4096];
						while((count = ins.read(data,0,4096)) != -1)  
				            	outStream.write(data, 0, count);  
				    	ContextUtil.getInstance().runOnUIThread(
				    			new BcsRunnable(BcsRunnable.BCS_FAILED, object, listener, outStream.toByteArray()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				    	ContextUtil.getInstance().runOnUIThread(
				    			new BcsRunnable(BcsRunnable.BCS_FAILED, object, listener, null));
					}				
				}
				catch (BCSServiceException e) {
			    	ContextUtil.getInstance().runOnUIThread(
			    			new BcsRunnable(BcsRunnable.BCS_FAILED, object, listener, null));
		    		e.printStackTrace();
				}
				catch(BCSClientException e){
			    	ContextUtil.getInstance().runOnUIThread(
			    			new BcsRunnable(BcsRunnable.BCS_FAILED, object, listener, null));
		    		e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public static String getUrl(String bucket,String object){
		BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
	    BaiduBCS baiduBCS = new BaiduBCS(credentials, host);
	    return baiduBCS.generateUrl(new GenerateUrlRequest(HttpMethodName.GET, bucket, object));
	}
}
