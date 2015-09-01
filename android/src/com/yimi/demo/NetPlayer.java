package com.yimi.demo;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.widget.Toast;

import com.yimi.common.simplenetwork.HttpDownloader;
import com.yimi.common.simplenetwork.HttpDownloader.DownloadListener;
import com.yimi.common.simplenetwork.baidupushserver.MessageDigestUtility;

public class NetPlayer {
	
	static NetPlayer mInstance;
	
	public static NetPlayer getInstance(){
		if(null == mInstance){
			mInstance = new NetPlayer();
		}
		return mInstance;
	}
	
	public void play(String url){
		final String path = getCachePath() + MessageDigestUtility.toMD5Hex(url);
		
		if(new File(path).exists()){
			new AMRPlayer(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					
				}
			}).play(path);		
			return;
		}
		else{
			
			new HttpDownloader().download(url, path, new DownloadListener() {
				
				@Override
				public void onDownloadDone(boolean isSuccess) {
					if(isSuccess){
						new AMRPlayer(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub
								
							}
						}).play(path);		
					}
					else{
						new File(path).delete();
						Toast.makeText(ContextUtil.getInstance(),"下载声音失败",Toast.LENGTH_SHORT).show();
					}
					
				}
			});
		}
	}
	
	private String getCachePath(){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/demovoices/";
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;
	}
}
