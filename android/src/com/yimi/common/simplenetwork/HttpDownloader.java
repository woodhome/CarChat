package com.yimi.common.simplenetwork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.Handler;
import android.os.Message;

public class HttpDownloader {
	
	public interface DownloadListener{
		public void onDownloadDone(boolean isSuccess);
	}
	
	public interface IDownloadByteListener{
		public void onByteDownloadDone(byte[] data);
	}
	
	static class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			DownloadListener listener = (DownloadListener) msg.obj;
			listener.onDownloadDone(msg.arg1==1 ? true:false);
		}
	}
	

	
	Handler mHandler;
	public HttpDownloader(){
		mHandler = new Handler();
	}
	
	public void download(final String urlString, final String filepath, final DownloadListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean success = false;
				try {
					URL url = new URL(urlString);
					HttpURLConnection connection =(HttpURLConnection)url.openConnection();
					connection.connect();
					if(HttpStatus.SC_OK == connection.getResponseCode() ){
						InputStream inputStream = connection.getInputStream();
						FileOutputStream outputStream = new FileOutputStream(new File(filepath));
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len=inputStream.read(buffer)) != -1) {
							outputStream.write(buffer,0,len);
						}
						outputStream.flush();
						outputStream.close();
						success = true;
					}
					connection.disconnect();
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
				final boolean successed = success;
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						listener.onDownloadDone(successed);
					}
				});
			}
		}).start();
	}
	
	public void download(final String urlString, final IDownloadByteListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ByteArrayOutputStream outputStream = null;
				try {
					URL url = new URL(urlString);
					HttpURLConnection connection =(HttpURLConnection)url.openConnection();
					connection.connect();
					if(HttpStatus.SC_OK == connection.getResponseCode() ){
						InputStream inputStream = connection.getInputStream();
						outputStream = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len=inputStream.read(buffer)) != -1) {
							outputStream.write(buffer,0,len);
						}
						outputStream.flush();
					}
					connection.disconnect();
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
				if(null==outputStream){
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							listener.onByteDownloadDone(null);
						}
					});
				}
				
				else{
					final byte[] data=outputStream.toByteArray();
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							listener.onByteDownloadDone(data);
						}
					});
				}
			}
		}).start();
	}
}
