package com.yimi.demo;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;
import com.yimi.demo.data.GrouperManager;
import com.yimi.demo.data.GrouperManager.Grouper;
import com.yimi.demo.data.GrouperManager.IGrouperListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupActivity extends Activity implements IGrouperListener, HttpCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);

		
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		((ListView)findViewById(R.id.groups)).setAdapter(mGrouperAdapter);
		
		findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), NewGroupActivity.class));
				
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		GrouperManager.getInstance().getGroupers(this);
	}

	ArrayList<Grouper> mGroupers;
	
	BaseAdapter mGrouperAdapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(null == convertView){
				convertView = View.inflate(GroupActivity.this, R.layout.item_grouper, null);
			}
			((TextView)convertView.findViewById(R.id.name)).setText(mGroupers.get(position).name);
			((TextView)convertView.findViewById(R.id.comment)).setText(mGroupers.get(position).comment);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("name", mGroupers.get(position).name);
					param.put("imei", ContextUtil.getInstance().getImei());
					HttpHelper.DemoHttp_Get("user/join", param, GroupActivity.this, 0, null);
					
				}
			});
			
			return convertView;
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
		mGrouperAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onResponse(byte[] result, int status, int id,
			Object callbackData, String responseType, Header[] headers) {
		if(HttpClientService.SuccessID == status){
			try {
				JSONObject object = new JSONObject(new String(result));
				if(0 == object.getInt("error")){
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
		}
		
	}
}
