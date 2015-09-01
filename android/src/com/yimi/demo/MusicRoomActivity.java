package com.yimi.demo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MusicRoomActivity extends Activity implements HttpCallback, OnCompletionListener {
	
	String group;
	AMRPlayer mAmrPlayer = new AMRPlayer(this);
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_musicroom);

		
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
		HttpHelper.DemoHttp_Get("user/getplay", null, this, 0, null);
	}

	@Override
	public void onResponse(byte[] result, int status, int id,
			Object callbackData, String responseType, Header[] headers) {
		if(HttpClientService.SuccessID == status){
			try {
				JSONObject object = new JSONObject(new String(result));
				mAmrPlayer.play(object.getString("url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		mAmrPlayer.stop();
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		HttpHelper.DemoHttp_Get("user/getplay", null, this, 0, null);
	}
	
}
