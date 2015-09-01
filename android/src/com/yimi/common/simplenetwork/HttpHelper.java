package com.yimi.common.simplenetwork;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.yimi.common.simplenetwork.baidupushserver.MessageDigestUtility;

public class HttpHelper {
	public static final String HOST_DEMO= "http://voicedemo.duapp.com";
			//"http://192.168.199.223:18080";
	
	public static void DemoHttp_Get(String url,Map<String, String> param,HttpCallback cb,
			int id,Object cbdata){
		String fullUrl = HOST_DEMO+"/"+ url + "?"+getParamString(param);
		HttpClientService.getInstance().request_get(fullUrl, cb, id, cbdata);
	}
	
	public static void DemoHttp_Post(String url,Map<String, String> param,byte[] data, HttpCallback cb,
			int id,Object cbdata){
		String fullString = HOST_DEMO+"/"+url+"?"+getParamString(param);
		HttpClientService.getInstance().request_post(fullString, null, cb, id, data, cbdata);
		
	}
	
	public static String getSignString(Map<String, String> param,String password){
		if(null==param){
			param = new HashMap<String, String>();
		}
		String curtime = "" + System.currentTimeMillis();
		param.put("timestamp", curtime);
		
		StringBuilder sb = new StringBuilder();
		ArrayList<String> keys = new ArrayList<String>();
		for ( Map.Entry<String, String> entry : param.entrySet() ) {
			String key = entry.getKey();
			keys.add(key);
		}
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = param.get(key);
			sb.append(key).append('=').append(value);
			
		}
		sb.append(password);
		String encodeString = MessageDigestUtility.urlEncode(sb.toString());
		if ( encodeString != null ) {
			encodeString = encodeString.replaceAll("\\*", "%2A");
		}
		
		return getMD5(encodeString);
		
	}
	
	static String getParamString(Map<String, String> param){
		if(null==param){
			param = new HashMap<String, String>();
		}
		
		String paramString = "";
		if(null!=param){
			for (String key : param.keySet()) {
			    String value = param.get(key);
			    if(paramString.equalsIgnoreCase("")){
			    	paramString += key +"=" + value;
			    }
			    else{
			    	paramString +="&"+key+"="+value;
			    }
			}
		}
		return paramString;
	}
	
	public static String getMD5(String param){
		try{
		    MessageDigest md5 = MessageDigest.getInstance("MD5");
		    md5.update(param.getBytes());
		    byte[] m = md5.digest();
		    StringBuilder hex= new StringBuilder();
		    for(int k=0; k < m.length; k++)
		    {
		    	String hexstr = Integer.toHexString(m[k]&0xff);
		    	if(hexstr.length()  == 1){
		    		hex.append("0");
		    	}
		    	hex.append(hexstr);
		    }
		    return hex.toString().toLowerCase();
		}
		catch(NoSuchAlgorithmException e){
			return "";
		}
	}
	
}
