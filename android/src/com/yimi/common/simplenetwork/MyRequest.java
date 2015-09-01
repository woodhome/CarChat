package com.yimi.common.simplenetwork;


import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;


public class MyRequest {
		public HttpUriRequest mRequest;
		public HttpCallback mCb;
		public byte[] mResult;
		public Header[] headers;
		public int mid;
		public Object mCallbackData;
		public String mResponseContentType;
		public long expire;
		public MyRequest(HttpUriRequest req , HttpCallback callback ,int id,Object cbData){
			mCallbackData = cbData;
			mRequest = req;
			mCb = callback;
			mid =id;
			expire=System.currentTimeMillis() + 60 * 1000;
		}
}
