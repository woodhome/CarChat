package com.yimi.demo.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;
import com.yimi.demo.ContextUtil;

public class GrouperManager {
	
	public static class Grouper{
		public String name;
		public String comment;
		public String tag;
		public int type;
	}
	
	public interface IGrouperListener{
		public void onGotGroupers(ArrayList<Grouper> groupers);
	}
	
	static GrouperManager mInstance;
	
	ArrayList<Grouper> mAllGroupers = new ArrayList<GrouperManager.Grouper>();
	ArrayList<Grouper> mMyGroupers = new ArrayList<GrouperManager.Grouper>();
	
	public static GrouperManager getInstance(){
		if(null == mInstance){
			mInstance = new GrouperManager();
		}
		return mInstance;
	}
	
	public void getGroupers(final IGrouperListener listener){
		HttpHelper.DemoHttp_Get("user/allgroup", null, new HttpCallback() {
			
			@Override
			public void onResponse(byte[] result, int status, int id,
					Object callbackData, String responseType, Header[] headers) {
				if(HttpClientService.SuccessID == status){
					try {
						mAllGroupers.clear();
						JSONObject object = new JSONObject(new String(result));
						JSONArray groupArray = object.getJSONArray("groups");
						for (int i = 0; i < groupArray.length(); i++) {
							JSONObject groupObject = groupArray.getJSONObject(i);
							Grouper grouper = new Grouper();
							grouper.comment = groupObject.getString("comment");
							grouper.name = groupObject.getString("name");
							grouper.tag = groupObject.getString("tag");
							grouper.type = groupObject.getInt("chattype");
							mAllGroupers.add(grouper);
						}
						listener.onGotGroupers(mAllGroupers);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{

					Toast.makeText(ContextUtil.getInstance(), "网络连接失败", Toast.LENGTH_SHORT).show();
				}
				
			}
		}, 0, null);
	}
	
	public void getMyGroupers(final IGrouperListener listener){

		HashMap<String, String> param = new HashMap<String, String>();
		param.put("imei", ContextUtil.getInstance().getImei());
		
		HttpHelper.DemoHttp_Get("user/getmygroup", param, new HttpCallback() {
			
			@Override
			public void onResponse(byte[] result, int status, int id,
					Object callbackData, String responseType, Header[] headers) {
				if(HttpClientService.SuccessID == status){
					try {
						mMyGroupers.clear();
						JSONObject object = new JSONObject(new String(result));
						JSONArray groupArray = object.getJSONArray("groups");
						for (int i = 0; i < groupArray.length(); i++) {
							JSONObject groupObject = groupArray.getJSONObject(i);
							Grouper grouper = new Grouper();
							grouper.comment = groupObject.getString("comment");
							grouper.name = groupObject.getString("name");
							grouper.tag = groupObject.getString("tag");
							grouper.type = groupObject.getInt("chattype");
							mMyGroupers.add(grouper);
						}
						listener.onGotGroupers(mMyGroupers);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}, 0, null);
	}
}
