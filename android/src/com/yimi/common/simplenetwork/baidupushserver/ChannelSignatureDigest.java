package com.yimi.common.simplenetwork.baidupushserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChannelSignatureDigest {
	
	public static final String URL_KEY = "url";
	public static final String HTTP_METHOD_KEY = "http_method";
	public static final String CLIENT_SECRET_KEY = "client_secret";
	
//	private static final Set<String> PARAM_SET = new TreeSet<String>();
	
	public static String digest(String accessKey, String secretKey, Map<String, String> params) {
		return null;
	}
	
	public static String digest(String method, String url, String clientSecret, Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append(method).append(url);
		ArrayList<String> keys = new ArrayList<String>();
		for ( Map.Entry<String, String> entry : params.entrySet() ) {
			String key = entry.getKey();
			keys.add(key);
		}
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			sb.append(key).append('=').append(value);
			
		}
		sb.append(clientSecret);
		String encodeString = MessageDigestUtility.urlEncode(sb.toString());
		if ( encodeString != null ) {
			encodeString = encodeString.replaceAll("\\*", "%2A");
		}
 		return MessageDigestUtility.toMD5Hex(encodeString);
	}
	
	
	public static boolean checkParams(Map<String, String> params) {
		return false;
	}
	
}
