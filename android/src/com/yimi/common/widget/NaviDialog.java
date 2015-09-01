package com.yimi.common.widget;

import com.yimi.demo.ContextUtil;
import com.yimi.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NaviDialog implements Runnable {
	private Dialog mDialog;
	private View mView;
	private Context mContext;
	NaviDialog.OnNaviClickListener mFirstButtonClickListener = null;
	NaviDialog.OnNaviClickListener mSecondButtonClickListener = null;
	int mSecondsOfDismiss = 0;
	String mTitleString = "";
	
	public NaviDialog(){
		mContext = ContextUtil.getInstance();
		init();
	}
	
	public NaviDialog(Context context){
		mContext = context;
		init();
	}
	
	public NaviDialog(Context context,int dismissAfterSeconds){
		mContext = context;
		init();
		mSecondsOfDismiss = dismissAfterSeconds;
		ContextUtil.getInstance().runOnUIThread(this, 1000);
	}
	
	public NaviDialog(int dismissAfterSeconds){
		mContext = ContextUtil.getInstance();
		init();
		mSecondsOfDismiss = dismissAfterSeconds;
		ContextUtil.getInstance().runOnUIThread(this, 1000);
	}
	
	private void init(){
		mDialog = new Dialog(mContext,R.style.dialog);
		
		      
		
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mView = LayoutInflater.from(ContextUtil.getInstance()).inflate(R.layout.setaddrdialog, null);
		mDialog.setContentView(mView);
		mDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				ContextUtil.getInstance().removeCall(NaviDialog.this);
			}
		});
		
		((Button)mView.findViewById(R.id.btnfirst)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mFirstButtonClickListener != null){
					mFirstButtonClickListener.onClick();
				}
				mDialog.dismiss();
				ContextUtil.getInstance().removeCall(NaviDialog.this);
			}
		});
		
		((Button)mView.findViewById(R.id.btnsecond)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mSecondButtonClickListener != null){
					mSecondButtonClickListener.onClick();
				}
				mDialog.dismiss();
				ContextUtil.getInstance().removeCall(NaviDialog.this);
			}
		});
	}
	
	public void setContentText(CharSequence content){
		((TextView)mView.findViewById(R.id.content)).setText(content);
	}
	
	public void setContentText(CharSequence content, int size){
		((TextView)mView.findViewById(R.id.content)).setText(content);
		((TextView)mView.findViewById(R.id.content)).setTextSize(size);
	}
	
	public void setTitleText(CharSequence title){
		mTitleString = title.toString();
		((TextView)mView.findViewById(R.id.title)).setText(title);
	}
	
	public void setTitleText(CharSequence title, int size){
		mTitleString = title.toString();
		((TextView)mView.findViewById(R.id.title)).setText(title);
		((TextView)mView.findViewById(R.id.title)).setTextSize(size);
	}
	
	public void show(){
		mDialog.show();
	}
	
	public void hide(){
		mDialog.hide();
	}
	
	public void setFirstButtonText(CharSequence text){
		((Button)mView.findViewById(R.id.btnfirst)).setText(text);
	}
	
	public void setSecondButtonText(CharSequence text){
		((Button)mView.findViewById(R.id.btnsecond)).setText(text);
	}
	
	public void setOnFirstButtonClickListener(NaviDialog.OnNaviClickListener listener){
		mFirstButtonClickListener = listener;
	}
	
	public void setOnSecondButtonClickListener(NaviDialog.OnNaviClickListener listener){
		mSecondButtonClickListener = listener;
	}
	
	public static abstract interface OnNaviClickListener{
		public void onClick();
	}
	
	public void setDialogWindowType(int type){
		mDialog.getWindow().setType(type);
	}
	
	public void setCanceledOnTouchOutside(boolean mCancle){
		mDialog.setCancelable(mCancle);
		mDialog.setCanceledOnTouchOutside(mCancle);
	}
	public void enableFirstButton(boolean enable){
		mView.findViewById(R.id.btnfirst).setVisibility(
				enable?View.VISIBLE:View.GONE);
	}
	
	public void enableSecondButton(boolean enable){
		mView.findViewById(R.id.btnsecond).setVisibility(
				enable?View.VISIBLE:View.GONE);
	}

	@Override
	public void run() {
		mSecondsOfDismiss--;
		((TextView)mView.findViewById(R.id.title)).setText(mTitleString + "(" + mSecondsOfDismiss + ")");
		if(0 >= mSecondsOfDismiss){
			if(null!= mDialog){
				mDialog.dismiss();
			}
		}
		else{
			ContextUtil.getInstance().runOnUIThread(this, 1000);
		}
	}
}
