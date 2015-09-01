package com.yimi.common.simplenetwork;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class HttpClientService {
	
	class RequestTrafficItem{
		long sendlen = 0;
		long receivelen = 0;
		int requestCount = 0;
	}
	
	public static final int SuccessID = 1;
	public static final int TimeoutID = 2;
	public static final int FailID = -1;
	
	boolean mIsRunning;
	List<MyRequest> mRequestList;
	Handler mhandler;
	Object lock;
	
	HashMap<String, RequestTrafficItem> mTrafficItems = new HashMap<String,RequestTrafficItem>();
	int mUploadDay = 0;
	
	private static HttpClientService mInstance = null;
	private HttpClientService(){
		init();
		mhandler = HttpHandler.getInstance();
	}
	
	static public HttpClientService getInstance(){
		if(null == mInstance){
			mInstance = new HttpClientService();
		}
		return mInstance;
	}
	public void setHandler(Handler handler){
		mhandler = handler;
	}
	public void request_get(String req , HttpCallback cb ,int id,Object cbdata){
		String specialString = "~!@#$%^&*()_+:|\\=-,./?><;'][";
		String uri = Uri.encode(req, specialString);
		HttpGet request = new HttpGet(uri);
		Log.v("dog", "Url : " + uri);
		add(new MyRequest(request,cb ,id,cbdata));
	}
	
	public void request_post(String req , String[]headers, HttpCallback cb ,int id,byte[] data,Object cbdata){
		String specialString = "~!@#$%^&*()_+:|\\=-,./?><;'][{}\"";
		String uri = Uri.encode(req, specialString);
		HttpPost request = new HttpPost(uri);
		
		if(null!=data)
			request.setEntity(new ByteArrayEntity(data));
		
		if (headers != null){
			for (int index = 0; index < headers.length;){
				request.setHeader(headers[index++], headers[index++]);
			}
		}
		add(new MyRequest(request,cb ,id,cbdata));
	}
	
	public void request_post(String req, HttpCallback cb ,int id,Map<String, String> params,Object cbdata){
		String specialString = "~!@#$%^&*()_+:|\\=-,./?><;'][{}\"";
		String uri = Uri.encode(req, specialString);
		HttpPost request = new HttpPost(uri);
	    Iterator<Entry<String, String>> iter = params.entrySet().iterator();
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
    	    postparams.add(new BasicNameValuePair(key, val));
        }
		try {
			request.setEntity(new UrlEncodedFormEntity(postparams, HTTP.UTF_8));	
			add(new MyRequest(request,cb ,id,cbdata));	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void add(MyRequest request){
		synchronized (lock) {
			mRequestList.add(request);
			long curtime = System.currentTimeMillis();
			int index = 0;
			for (int i = 0; i < mRequestList.size(); i++) {
				if(mRequestList.get(i).expire < curtime){
					index = i + 1;
				}
			}
			for (int i = 0; i < index; i++) {
				MyRequest req = mRequestList.get(0);
                mhandler.sendMessage(mhandler.obtainMessage(HttpHandler.HttpMessageID,TimeoutID,0 , req));
				mRequestList.remove(0);
			}
		}
	}
	
	public void request_post(String req,HttpCallback cb , int id,HttpEntity entity,Object cbdata){

		String specialString = "~!@#$%^&*()_+:|\\=-,./?><;'][{}\"";
		String uri = Uri.encode(req, specialString);
		HttpPost request = new HttpPost(uri);
		
		request.setEntity(entity);
		add(new MyRequest(request,cb ,id,cbdata));
		
	}
	
	public void stop(){
		mIsRunning = false;
	}
	
	public void start(){
		mIsRunning = true;
	}
	
	private void init(){
		lock = new Object();
		mIsRunning = true;
		mRequestList = new ArrayList<MyRequest>();
		final Thread th = new Thread(new Runnable(){
			@Override
			public void run() { 
				Thread.currentThread().setName("HTTPThread");
				HttpParams httpParameters = new BasicHttpParams();
				
				// Set the timeout in milliseconds until a connection is established.  
			    HttpConnectionParams.setConnectionTimeout(httpParameters, 10 * 1000);
			    
			    // Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
			    HttpConnectionParams.setSoTimeout(httpParameters, 10 * 1000);
			    
				HttpClient htc = new DefaultHttpClient(httpParameters);
				while(mIsRunning){	
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(!mRequestList.isEmpty()){
						MyRequest req = null;
					synchronized(lock){
						req = mRequestList.get(0);
						mRequestList.remove(0);
					}
					try {
							HttpResponse response = htc.execute(req.mRequest);
							req.headers = response.getAllHeaders();
				            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				            	   if(null != response.getEntity().getContentType()){
				            		   req.mResponseContentType = response.getEntity().getContentType().getValue();
				            	   }
					               req.mResult = EntityUtils.toByteArray(response.getEntity());
					               if(null == mhandler){
					            	   Log.e("HTTPERROR", "NULL Pointer");
					            	   
					               }
					               else{
						               mhandler.sendMessage(mhandler.obtainMessage(HttpHandler.HttpMessageID, SuccessID,0 , req));
					               }
				            }
				            else{
				            	String eror= new String(EntityUtils.toByteArray(response.getEntity()));
				                mhandler.sendMessage(mhandler.obtainMessage(HttpHandler.HttpMessageID,FailID,0 , req));
				                Log.e("dog", "ErrorCocde:" + response.getStatusLine().getStatusCode() + eror);
				            }
				           
					} catch (ClientProtocolException e) {
		                mhandler.sendMessage(mhandler.obtainMessage(HttpHandler.HttpMessageID,TimeoutID,0 , req));
						e.printStackTrace();
					} catch (IOException e) {
		                mhandler.sendMessage(mhandler.obtainMessage(HttpHandler.HttpMessageID,TimeoutID,0 , req));
						e.printStackTrace();
					}
				}
				}
			}});
		th.start();
	}
}
