package com.yimi.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;

import com.yimi.common.simplenetwork.BcsHelper;
import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpHelper;
import com.yimi.common.simplenetwork.BcsHelper.BcsListener;
import com.yimi.demo.data.ChatMessageManager;
import com.yimi.demo.data.ChatMessageManager.ChatMessage;
import com.yimi.demo.data.ChatMessageManager.IMessageListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity implements IMessageListener, HttpCallback {
	
	ArrayList<ChatMessage> mMessages;
	String group;

	MediaRecorder mMediaRecorder;
	String mFileName;
	long mStartTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chatroom);
		mFileName = this.getExternalFilesDir(null) +  "/myaudio.amr";
		
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		Intent intent = getIntent();
		if(null != intent){
			group = intent.getStringExtra("name");
			((TextView)findViewById(R.id.title)).setText(group);
		}
		
		ChatMessageManager.getInstance().setMessageListener(this);
		
		((ListView)findViewById(R.id.messages)).setAdapter(mMessageAdapter);
		
		findViewById(R.id.speak).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					startRecording();
					mStartTime = System.currentTimeMillis();
				}
				if(event.getAction() == MotionEvent.ACTION_CANCEL ||
						event.getAction() == MotionEvent.ACTION_UP){
					stopRecording();
					if(System.currentTimeMillis() - mStartTime > 1000){
						upLoadVoice();
					}
					else{
						Toast.makeText(ChatRoomActivity.this, "说话时间太短", Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});
	}

	@Override
	protected void onResume(){
		super.onResume();
		mMessages = ChatMessageManager.getInstance().getMessages(group);
		mMessageAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		ChatMessageManager.getInstance().setMessageListener(null);
		
	}
	
	BaseAdapter mMessageAdapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			if(mMessages.get(position).imei.equals(ContextUtil.getInstance().getImei())){
				view = View.inflate(getApplicationContext(), R.layout.item_myvoice, null);
			}
			else{
				view = View.inflate(getApplicationContext(), R.layout.item_voice, null);
			}
			((TextView)view.findViewById(R.id.user)).setText(mMessages.get(position).user);
			
			view.findViewById(R.id.play).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					NetPlayer.getInstance().play(mMessages.get(position).voiceurl);
				}
			});
			
			return view;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mMessages == null ? 0 : mMessages.size();
		}
	};
	
	@Override
	public void onNewMessage(ChatMessage message) {
		if(!message.imei.equals(ContextUtil.getInstance().getImei()) 
			&& message.group.equals(group)){
			NetPlayer.getInstance().play(message.voiceurl);
		}
		
		if(message.group.equals(group)){
			mMessages = ChatMessageManager.getInstance().getMessages(group);
			mMessageAdapter.notifyDataSetChanged();
		}
		
	}
	
	
	private void startRecording() {
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mMediaRecorder.setAudioChannels(1);
		mMediaRecorder.setOutputFile(mFileName);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setAudioSamplingRate(8);
		mMediaRecorder.setAudioEncodingBitRate(8);
        try {
        	mMediaRecorder.prepare();
            mMediaRecorder.start();
            findViewById(R.id.anim_voice).setVisibility(View.VISIBLE);    		
            AnimationDrawable animation = (AnimationDrawable)findViewById(R.id.anim_voice).getBackground();
            animation.setOneShot(false);
            animation.stop();
            animation.start();
        } catch (Exception e) {
            Log.e("dog", "Audio prepare() failed");
        }
    }
	
	
    private void stopRecording() {
    	if(null!=mMediaRecorder){
	    	mMediaRecorder.stop();
	    	mMediaRecorder.release();
	    	mMediaRecorder = null;
    	}
		AnimationDrawable animation = (AnimationDrawable)findViewById(R.id.anim_voice).getBackground();
        animation.stop();
        findViewById(R.id.anim_voice).setVisibility(View.GONE);
    }
    
    private void upLoadVoice(){
    	String objname = ContextUtil.getInstance().getImei() + System.currentTimeMillis() + ".amr";
    	BcsHelper.putObjectt("demovoice", new File(mFileName), "/" + objname, new BcsListener() {
			
			@Override
			public void onSuccessed(String ojectName, byte[] data) {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("imei", ContextUtil.getInstance().getImei());
				param.put("name", group);
				param.put("voiceurl", BcsHelper.getUrl("demovoice", "/" + ojectName));
				param.put("text", "nothing");
				HttpHelper.DemoHttp_Get("user/speak", param, ChatRoomActivity.this, 0, null);
			}
			
			@Override
			public void onFialed(String ojectName) {
				Toast.makeText(getApplicationContext(), "上传语音失败", Toast.LENGTH_SHORT).show();
				
			}
		});
    }

	@Override
	public void onResponse(byte[] result, int status, int id,
			Object callbackData, String responseType, Header[] headers) {
		// TODO Auto-generated method stub
		
	}
}
