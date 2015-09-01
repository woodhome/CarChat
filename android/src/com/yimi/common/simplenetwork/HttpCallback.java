package com.yimi.common.simplenetwork;

import org.apache.http.Header;



public interface HttpCallback {
	public abstract void onResponse(byte[] result ,int status ,int id,Object callbackData,
			String responseType,Header[] headers);
}
