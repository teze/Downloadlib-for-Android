package com.teze.downloaddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.teze.downloaddemo.Global.FileOrDir;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void add(View view){
		for (int i = 0; i < 15; i++) {
			FileInfo info=new FileInfo();
			info.url="http://down11.zol.com.cn/liaotian/QQ4.7.0.apk";
			info.name=i+"QQ.apk";
			info.filePath=FileOrDir.AppDir+info.name;
			info.progress=0;
			info.state=FileInfo.STATE_RUNNING;
			APP.getDbHelper().getFileDataDao().create(info);
		}
	}
	
	public void start(View view){
		Intent intent=new Intent(this, DownloadActivity.class);
		startActivity(intent);
	}

}
