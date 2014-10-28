package in.teze.download;

import android.content.Context;
import android.content.Intent;

public class DownloadTool {

	private static final String ACTION_DOWNLOAD_SERVICE = "android.intent.action.DownloadService";
	protected  DatabaseHelper dbHelper;
	private static  DownloadTool sInstance;

	private DownloadTool (){
	}

	public static DownloadTool getInstance(){
		if (sInstance == null) {
			synchronized (DownloadTool.class) {
				sInstance = new DownloadTool();
			}
		}
		return sInstance;
	}
	
	
	public void init(Context context){
		initDataBase(context);
	}
	
	
	private void initDataBase(Context context){
		dbHelper= DatabaseHelper.getHelper(context);
	}
	
	public  void startDownload(Context context,String uri,String filePath){
		Intent service = new Intent(ACTION_DOWNLOAD_SERVICE);
		FileInfo fileInfo = new FileInfo();
		fileInfo.url = uri;
		fileInfo.filePath = filePath;
		service.putExtra(DownloadService.INTENT_FILE_INFO, fileInfo);
		service.putExtra(DownloadService.INTENT_TASK, DownloadService.Task.TASK_ADD_DOWNLOAD);
		context.startService(service);
	}
	
	public  void stopDownload(Context context,String filePath){
		Intent service = new Intent(ACTION_DOWNLOAD_SERVICE);
		FileInfo fileInfo = new FileInfo();
		fileInfo.filePath = filePath;
		service.putExtra(DownloadService.INTENT_FILE_INFO, fileInfo);
		service.putExtra(DownloadService.INTENT_TASK, DownloadService.Task.TASK_STOP_DOWNLOAD);
		context.startService(service);
	}
	
	
}
