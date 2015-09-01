package com.yimi.demo;

import java.io.IOException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AMRPlayer {

	private static MediaPlayer mMediaPlayer;
	private OnCompletionListener mCompletionListener;
	
	public AMRPlayer(OnCompletionListener completionListener){
		mCompletionListener = completionListener;
		buildMediaPlayer();
	}
	
	private void buildMediaPlayer() {
		if(null == mMediaPlayer){
			mMediaPlayer = new MediaPlayer();
		}
		
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mCompletionListener != null) {
					mCompletionListener.onCompletion(null);
				}
			}
		});
	}
	
	public void play(String path) {
		if (mMediaPlayer == null) {
			throw new RuntimeException("MediaPlayer has been released.");
		}

		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}

		mMediaPlayer.reset();
		mMediaPlayer.setLooping(false);

		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setAuxEffectSendLevel(1.0f);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IllegalStateException ioe) {
			mCompletionListener.onCompletion(mMediaPlayer);
			Log.e("dog", ioe.getMessage());
		} catch (IllegalArgumentException e) {
			mCompletionListener.onCompletion(mMediaPlayer);
			Log.e("dog", e.getMessage());
		} catch (IOException e) {
			mCompletionListener.onCompletion(mMediaPlayer);
			Log.e("dog", e.getMessage());
		} 
	}
	
	public void stop(){
		if (mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}
	}
}
