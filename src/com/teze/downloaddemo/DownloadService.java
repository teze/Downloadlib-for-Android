package com.teze.downloaddemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.teze.downloaddemo.HttpClientTool.DownloadCallback;

public class DownloadService extends Service {

	private static final String TAG = "DownloadService";
	private Map<String, Thread> threadMap=new HashMap<String, Thread>();
	
	private IDownloadService binder=new IDownloadService() {
		
		@Override
		void startDownload(Object obj,DownloadCallback callback) {
			Loger.i(TAG,"startDownload");
			if (obj instanceof FileInfo) {
				start((FileInfo) obj, callback);
			}
			
		}
		
		@Override
		boolean pauseDownload(Object obj) {
			Loger.i(TAG,"pauseDownload");
			if (obj instanceof String) {
				return pause(obj+"");
			}
			return false;
		}
		
		@Override
		void StopDownload() {
			Loger.i(TAG,"StopDownload");
		}

		@Override
		State getItemState(Object key) {
			try {
				return isRuning((String) key)?State.RUNNING:State.PAUSE;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return State.ERROR;
		}
		
	}; 

	
	private void start(final FileInfo info,final DownloadCallback callback){
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClientTool.continuousDownload(info.url, info.filePath, callback);
			}
		});
		thread.start();
		addThread(info.filePath, thread);
	}
	
	
	private boolean pause(String key){
		Thread current =threadMap.get(key);
		if (current!=null) {
			current.interrupt();
			threadMap.remove(key);
			return true;
		}else {
			return false;
		}
	}
	
	private void addThread(String key ,Thread thread ){
		if(threadMap!=null){
			threadMap.put(key, thread);
		}
	}
	
	private boolean isRuning(String key ){
		if(threadMap!=null){
			return threadMap.containsKey(key);
		}
		return false;
		
	}
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		Loger.i(TAG,"onBind");
		return binder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Loger.i(TAG,"onRebind");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Loger.i(TAG,"onUnbind");
		return true;
	}

	@Override
	public void onCreate() {
		Loger.i(TAG,"onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Loger.i(TAG,"onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Loger.i(TAG,"onDestroy");
		super.onDestroy();
	}
	
	abstract class IDownloadService extends Binder {
		abstract void startDownload(Object obj,DownloadCallback callback );
		abstract boolean pauseDownload(Object obj);
		abstract void StopDownload();
		abstract State getItemState(Object obj);
	}
	
	public enum State{
		RUNNING,PAUSE,ERROR,FINISHED;
	}
}
