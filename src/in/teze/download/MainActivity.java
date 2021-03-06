package in.teze.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import in.teze.download.R;
import in.teze.download.DownloadService.Task;
import in.teze.download.Global.FileOrDir;

public class MainActivity extends ActionBarActivity {
   private int index;
   DatabaseHelper dbHelper ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHelper = DatabaseHelper.getHelper(getApplicationContext());
		DownloadTool.getInstance().init(getApplicationContext());
		DownloadTool.getInstance().startDownload(getApplicationContext(), "http://down11.zol.com.cn/liaotian/QQ4.7.0.apk", FileOrDir.AppDir+"AA.apk");
	}

	public void add(View view){
		for (int i = 0; i < 5; i++) {
			FileInfo info=new FileInfo();
			info.url="http://down11.zol.com.cn/liaotian/QQ4.7.0.apk";
			info.name=i+"QQ.apk";
			info.filePath=FileOrDir.AppDir+info.name;
			info.progress=0;
			info.state=FileInfo.STATE_STOPPED;
			dbHelper.getFileDataDao().create(info);
		}
	}
	
	public void list(View view){
		Intent intent=new Intent(this, DownloadActivity.class);
		startActivity(intent);
	}
	
	public void start(View view){
		addDownload();
		Intent intent=new Intent(this, DownloadActivity.class);
		startActivity(intent);
	}
	
	public void addDownload(){
		index++;
		FileInfo info=new FileInfo();
		info.url="http://down11.zol.com.cn/liaotian/QQ4.7.0.apk";
		info.name=index+"qq.apk";
		info.filePath=FileOrDir.AppDir+info.name;
		info.progress=0;
		info.state=FileInfo.STATE_RUNNING;
		Intent service=new Intent(this, DownloadService.class);
		service.putExtra(DownloadService.INTENT_FILE_INFO, info);
		service.putExtra(DownloadService.INTENT_TASK, Task.TASK_ADD_DOWNLOAD);
		startService(service);
		
	}

	
	public void update(){
		FileInfo info=new FileInfo();
		info.id=2;
		info.url="http://down11.zol.com.cn/liaotian/QQ4.7.0.apk";
		info.name="qq.apk";
		info.filePath=FileOrDir.AppDir+info.name;
		info.progress=0;
		info.fileSize=10;
		info.state=FileInfo.STATE_RUNNING;
		/*APP.getDbHelper().getFileDataDao().update(info);*/
		DownloadProcess downloadProcess=new DownloadProcess(getApplicationContext());
		downloadProcess.updateFileSizeDb(info);
	}
}
