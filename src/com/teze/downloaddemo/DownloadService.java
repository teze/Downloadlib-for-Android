package com.teze.downloaddemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.teze.downloaddemo.HttpClientTool.DownloadCallback;
import com.teze.downloaddemo.HttpClientTool.Response;

public class DownloadService extends Service {

	private static final String TAG = "DownloadService";
	public  static final String ACTION_DOWNLOAD = APP.SCHEMA+"action_download";
	public  static final String INTENT_ACTION_TYPE = "actionType";
	public  static final String INTENT_BUNDLE = "bundle";
	public  static final String INTENT_FILE_ID = "fileId";
	public  static final String INTENT_PROGRESS = "progress";
	public  static final String INTENT_RESPONSE = "response";
	public  static final String INTENT_FILE_INFO = "fileInfo";
	
	private DownloadProcess process;
	private Map<String, Thread> threadMap=new HashMap<String, Thread>();
	
	private IDownloadService binder=new IDownloadService() {
		
		@Override
		void startDownload(Object obj) {
			Loger.i(TAG,"startDownload");
			if (obj instanceof FileInfo) {
				start((FileInfo) obj);
			}
		}
		
		@Override
		boolean stopDownload(Object obj) {
			Loger.i(TAG,"stopDownload");
			if (obj instanceof String) {
				return pause(obj+"");
			}
			return false;
		}
		
		@Override
		boolean removeDownload(Object obj) {
			Loger.i(TAG,"removeDownload");
			return false;
		}
		
		@Override
		boolean addDownload(Object obj) {
			boolean reuslt=false;
			if (obj instanceof FileInfo) {
				FileInfo info=(FileInfo) obj;
				reuslt=addRecordDb(info);
				if (reuslt)start(info);
			}
			return reuslt;
		}

		@Override
		State getItemState(Object key) {
			try {
				return isRuning((String) key)?State.RUNNING:State.STOPPED;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return State.ERROR;
		}

	}; 

	private boolean addRecordDb(FileInfo info){
		int rowID=process.addDownloadDb(info);
		return rowID==-1?false:true;
	}
	
	private boolean updateRecordDb(FileInfo info){
		int rowID=process.updateDownloadDb(info);
		return rowID==-1?false:true;
	}
	
	
	
	private void sendBroadCast(String type,Bundle bundle){
		Intent intent=new Intent(ACTION_DOWNLOAD);
		intent.putExtra(INTENT_ACTION_TYPE, type);
		intent.putExtra(INTENT_BUNDLE, bundle);
		sendBroadcast(intent);
	}
	
	private void start(final FileInfo info){
		final DownloadCallback callback=new DownloadCallback() {

			@Override
			public void onProgress(String fileKey, long progress) {
				Bundle bundle=new Bundle();
				bundle.putString(INTENT_FILE_ID, fileKey);
				bundle.putLong(INTENT_PROGRESS, progress);
				sendBroadCast(Action.PROGRESS, bundle);
			}

			@Override
			public void onSuccess(String fileKey, Response obj) {
				Bundle bundle=new Bundle();
				bundle.putString(INTENT_FILE_ID, fileKey);
				bundle.putSerializable(INTENT_RESPONSE, obj);
				sendBroadCast(Action.SUCCESS, bundle);
				FileInfo temp=new FileInfo();
				temp.filePath=fileKey;
				temp.progress=100;
				temp.state=FileInfo.STATE_FINISHED;
				updateRecordDb(temp); 
			}

			@Override
			public void onFailed(String fileKey, Response obj) {
				Bundle bundle=new Bundle();
				bundle.putString(INTENT_FILE_ID, fileKey);
				bundle.putSerializable(INTENT_RESPONSE, obj);
				sendBroadCast(Action.FAILED, bundle);
				FileInfo temp=new FileInfo();
				temp.filePath=fileKey;
				temp.progress=(int) obj.progress;
				temp.state=FileInfo.STATE_STOPPED;
				updateRecordDb(temp); 
			}

			@Override
			public void onStop(String fileKey, Response obj) {
				Bundle bundle=new Bundle();
				bundle.putString(INTENT_FILE_ID, fileKey);
				bundle.putSerializable(INTENT_RESPONSE, obj);
				sendBroadCast(Action.STOP, bundle);
				FileInfo temp=new FileInfo();
				temp.filePath=fileKey;
				temp.progress=(int) obj.progress;
				temp.state=FileInfo.STATE_STOPPED;
				updateRecordDb(temp); 
			}

			@Override
			public void onStart(String fileKey, Response obj) {
				Bundle bundle=new Bundle();
				bundle.putString(INTENT_FILE_ID, fileKey);
				bundle.putSerializable(INTENT_RESPONSE, obj);
				sendBroadCast(Action.START, bundle);
				FileInfo temp=new FileInfo();
				temp.filePath=fileKey;
				temp.fileSize=(int) obj.fileSize;
				temp.state=FileInfo.STATE_RUNNING;
				updateRecordDb(temp); 
			}
			
		};
		
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClientTool.continuousDownload(info.url, info.filePath, callback);
			}
		});
		thread.start();// TODO thread pool manager
		keepThread(info.filePath, thread);
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
	
	private void keepThread(String key ,Thread thread ){
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
		init();
		super.onCreate();
	}
	
	private void init(){
		process=new DownloadProcess();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Loger.i(TAG,"onStartCommand");
		FileInfo info=(FileInfo) intent.getSerializableExtra(INTENT_FILE_INFO);
		if (info!=null) {
			binder.addDownload(info);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Loger.i(TAG,"onDestroy");
		super.onDestroy();
	}
	
	abstract class IDownloadService extends Binder {
		abstract void startDownload(Object obj );
		abstract boolean stopDownload(Object obj);
		abstract boolean addDownload(Object obj);
		abstract boolean removeDownload(Object obj);
		abstract State getItemState(Object obj);
	}
	
	public enum State{
		RUNNING,STOPPED,FINISHED,ERROR;
	}
	
	public static class Action{
		static String START="start";
		static String STOP="stop";
		static String PROGRESS="progress";
		static String SUCCESS="success";
		static String FAILED="failed";
	}
}
