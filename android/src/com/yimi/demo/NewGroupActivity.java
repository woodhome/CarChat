package com.yimi.demo;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.yimi.common.simplenetwork.HttpCallback;
import com.yimi.common.simplenetwork.HttpClientService;
import com.yimi.common.simplenetwork.HttpHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class NewGroupActivity extends Activity implements HttpCallback {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);
		
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		
		findViewById(R.id.register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = ((EditText)findViewById(R.id.username)).getText().toString();
				String comment = ((EditText)findViewById(R.id.comment)).getText().toString();
				if(null == name || name.isEmpty() || null == comment || comment.isEmpty()){
					Toast.makeText(NewGroupActivity.this, "名称和描述不能为空", Toast.LENGTH_SHORT).show();
				}
				else{
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("name", name);
					param.put("comment", comment);
					HttpHelper.DemoHttp_Get("user/creategroup", param, NewGroupActivity.this, 0, null);
				}
			}
		});
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
				else if(1 == object.getInt("error")){
					Toast.makeText(NewGroupActivity.this, "频道名称已经存在", Toast.LENGTH_SHORT).show();
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
