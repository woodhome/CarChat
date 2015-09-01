package com.yimi.demo;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.android.tpush.XGPushManager;
import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;
import com.yimi.demo.data.GrouperManager;
import com.yimi.demo.data.GrouperManager.Grouper;
import com.yimi.demo.data.GrouperManager.IGrouperListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements IGrouperListener, HttpCallback {	
	
	HttpCallback mNickCallback = new HttpCallback() {
		
		@Override
		public void onResponse(byte[] result, int status, int id,
				Object callbackData, String responseType, Header[] headers) {
			
			if(HttpClientService.SuccessID != status){
				Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
				return;
			}
			
			try {
				JSONObject object = new JSONObject(new String(result));
				String nickname = object.getString("nickname");
				if(nickname.isEmpty()){
						final Dialog dialog = new Dialog(ContextUtil.getInstance(),R.style.dialog);
					
						dialog.setCanceledOnTouchOutside(false);
						dialog.setCancelable(false);
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						final View view = LayoutInflater.from(ContextUtil.getInstance()).inflate(R.layout.dialog_setnick,null);
						dialog.setContentView(view);
						view.findViewById(R.id.btnok).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								EditText nickEditText = (EditText)view.findViewById(R.id.nick);
								String nick = nickEditText.getText().toString();
								if(null == nick || nick.isEmpty()){
									Toast.makeText(ContextUtil.getInstance(), "昵称不能为空", Toast.LENGTH_LONG).show();
								}
								else{
									HashMap<String, String> param = new HashMap<String, String>();
									param.put("nickname", nick);
									param.put("imei", ContextUtil.getInstance().getImei());
									HttpHelper.DemoHttp_Get("user/setnickname", param, new HttpCallback() {
										
										@Override
										public void onResponse(byte[] result, int status, int id,
												Object callbackData, String responseType, Header[] headers) {
											// TODO Auto-generated method stub
											if(HttpClientService.SuccessID != status){
												dialog.show();
												Toast.makeText(ContextUtil.getInstance(), "设置昵称失败", Toast.LENGTH_LONG).show();
											}
											else{
												
											}
										}
									}, 0, null);
									dialog.dismiss();
								}
							}
						});
						dialog.show();
					}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		XGPushManager.registerPush(getApplicationContext());
		
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("imei", ContextUtil.getInstance().getImei());
		HttpHelper.DemoHttp_Get("user/getnickname", param, mNickCallback, 0, null);

		((ListView)findViewById(R.id.groups)).setAdapter(mMyAdapter);
		
		findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), GroupActivity.class));
				
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();

		GrouperManager.getInstance().getMyGroupers(this);
	}
	
	ArrayList<Grouper> mGroupers;
	
	BaseAdapter mMyAdapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			Button btn = (Button)convertView;
			if(null == btn){
				btn = new Button(MainActivity.this);
				btn.setTextColor(Color.BLACK);
				btn.setTextSize(30);
				btn.setBackgroundResource(R.drawable.btn_common);
			}
			
			btn.setText(mGroupers.get(position).name);
			btn.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("imei", ContextUtil.getInstance().getImei());
					param.put("name", mGroupers.get(position).name);
					HttpHelper.DemoHttp_Get("user/quit", param, MainActivity.this, 0, null);
					return false;
				}
			});
			
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = null;
					if(mGroupers.get(position).type == 1){
						intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
					}
					else{
						intent = new Intent(getApplicationContext(), MusicRoomActivity.class);
					}
					intent.putExtra("name", mGroupers.get(position).name);
					startActivity(intent);
				}
			});
			
			return btn;
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
			return mGroupers == null ? 0 : mGroupers.size();
		}
	};
	
	
	@Override
	public void onGotGroupers(ArrayList<Grouper> groupers) {
		mGroupers = groupers;
		mMyAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResponse(byte[] result, int status, int id,
			Object callbackData, String responseType, Header[] headers) {
		GrouperManager.getInstance().getMyGroupers(this);
		
	}
	
	
}
