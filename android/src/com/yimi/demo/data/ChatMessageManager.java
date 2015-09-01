package com.yimi.demo.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.yimi.demo.ContextUtil;
import com.yimi.demo.CustomPushReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ChatMessageManager {
	
	public interface IMessageListener{
		public void onNewMessage(ChatMessage message);
	}
	class ChatReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				JSONObject object = new JSONObject(intent.getStringExtra("data"));
				ChatMessage message = new ChatMessage();
				message.group = object.getString("group");
				message.user = object.getString("user");
				message.voiceurl = object.getString("voice");
				message.text = object.getString("voicetext");
				message.imei = object.getString("imei");
				ArrayList<ChatMessage> list = mChatMessages.get(message.group);
				if(null == list){
					list = new ArrayList<ChatMessageManager.ChatMessage>();
					list.add(message);
					mChatMessages.put(message.group, list);
				}else{
					list.add(message);
				}
				
				if(null != mMessageListener){
					mMessageListener.onNewMessage(message);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static class ChatMessage{
		public String voiceurl;
		public String text;
		public String group;
		public String user;
		public String imei;
	}
	
	static ChatMessageManager mInstance;
	
	ChatReceiver mChatReceiver;
	IMessageListener mMessageListener;
	
	private ChatMessageManager() {
		
		// TODO Auto-generated constructor stub
	}
	
	HashMap<String, ArrayList<ChatMessage> > mChatMessages = new HashMap<String, ArrayList<ChatMessage>>();
	
	public static ChatMessageManager getInstance(){
		if(null == mInstance){
			mInstance = new ChatMessageManager();
		}
		return mInstance;
	}
	
	public void start(){
		if(null == mChatReceiver){
			mChatReceiver = new ChatReceiver();
			ContextUtil.getInstance().registerReceiver(mChatReceiver, new IntentFilter(CustomPushReceiver.ACTION__NEWVOICE));
		}
	}
	
	public void setMessageListener(IMessageListener listener){
		mMessageListener = listener;
	}

	public ArrayList<ChatMessage> getMessages(String group){
		return mChatMessages.get(group);
	}
}
