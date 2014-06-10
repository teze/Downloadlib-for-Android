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

	public void start(View view){
		FileInfo info=new FileInfo();
		info.name="爱和承诺";
		
		info.url="http://down11.zol.com.cn/liaotian/QQ4.7.0.apk";
		info.progress=0;
		info.filePath=FileOrDir.AppDir+"QQ.apk";
		APP.getDbHelper().getFileDataDao().create(info);
		Intent intent=new Intent(this, DownloadActivity.class);
		startActivity(intent);
	}
	
	public void pause(View view){
		
	}

}