package com.yimi.common.simplenetwork.baidupushserver;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;



public class BaiduPushServer {
	public interface IPushListener{
		public void onPushSuccess();
		public void onPushFailed(String result);
	}
	public static final String PUSH_URL = "http://channel.api.duapp.com/rest/2.0/channel/channel";
	public static final String METHOD_PUSHMSG = "push_msg";
	public static final String API_KEY = "QjFH8nzVBiGY28ikdU3Upf15";
	public static final String SECRET_KEY = "2hKaFNH7dmE6gew0fow19h5sEGkIh36r";
	
	public static void pushMsg(String userid,String msg, final IPushListener listener,int expire){
		
		String fullurl = PUSH_URL + "?" + concatHttpParams(getMsgParams(userid,msg,expire));
		HttpClientService.getInstance().request_post(fullurl, null, new HttpCallback() {
			
			@Override
			public void onResponse(byte[] result, int status, int id,
					Object callbackData,String responsetype,Header[] headers) {
				if(HttpClientService.SuccessID == status){
					listener.onPushSuccess();
				}
				else{
					listener.onPushFailed("");
				}
				
			}
		}, 0, null, null);
	}
	
	public static void pushTagMsg(String tag,String msg,final IPushListener listener,int expire){
		String fullurl = PUSH_URL + "?" + concatHttpParams(getTagMsgParams(tag, msg,expire));
		HttpClientService.getInstance().request_post(fullurl, null, new HttpCallback() {
			
			@Override
			public void onResponse(byte[] result, int status, int id,
					Object callbackData,String responsetype,Header[] headers) {
				if(HttpClientService.SuccessID == status){
					listener.onPushSuccess();
				}
				else{
					listener.onPushFailed("");
				}
				
			}
		}, 0, null, null);
	}
	
	private static String concatHttpParams(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = false;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (isFirst == false) {
				isFirst = true;
			} else {
				sb.append('&');
			}
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(MessageDigestUtility.urlEncode(entry.getValue()));
		}
		return sb.toString();
	}
	
	private static Map<String, String> getMsgParams(String userid, String msg,int expire){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", METHOD_PUSHMSG);
		params.put("apikey", API_KEY);
		params.put("user_id", userid);
		params.put("push_type", "1");
		params.put("message_type", "0");
		params.put("messages", msg);
		params.put("message_expires", ""+expire);
		params.put("msg_keys", userid + System.currentTimeMillis());
		params.put("timestamp", ""+System.currentTimeMillis());
		params.put("sign", ChannelSignatureDigest.digest("POST", PUSH_URL, SECRET_KEY, params));
		return params;
	}
	
	private static Map<String, String> getTagMsgParams(String tag, String msg,int expire){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", METHOD_PUSHMSG);
		params.put("apikey", API_KEY);
		params.put("tag", tag);
		params.put("push_type", "2");
		params.put("message_type", "0");
		params.put("message_expires", ""+expire);
		params.put("messages", msg);
		params.put("msg_keys", tag + System.currentTimeMillis());
		params.put("timestamp", ""+System.currentTimeMillis());
		params.put("sign", ChannelSignatureDigest.digest("POST", PUSH_URL, SECRET_KEY, params));
		return params;
	}
}
