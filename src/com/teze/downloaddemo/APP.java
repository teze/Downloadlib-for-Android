package com.teze.downloaddemo;

import android.app.Application;

public class APP extends Application {

	protected static final String TAG = "APP";
	protected static String APP_NAME = "APP";

	private static APP instance;
	private static DatabaseHelper dbHelper;

	public APP() {
		super();
		Loger.i(TAG, "APP is instanced");
		instance = this;
	}

	public static APP getInstance() {
		return instance;
	}
	
	public static DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void init() {
		initDataBase();
	}
	
	
	private void initDataBase(){
		dbHelper=new DatabaseHelper(this);
		/*for (int i = 0; i < 10; i++) { Temp for text 
			FileInfo info=new FileInfo();
			info.name="爱和承诺"+i;
			info.url="http://img04.tooopen.com/images/20130315/tooopen_23024520.jpg";
			info.fileSize=100;
			info.progress=0;
			info.filePath=FileOrDir.AppDir+i+"1.jpg";
			dbHelper.getFileDataDao().create(info);
		}*/
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Loger.i(TAG, "APP is onCreated");
		init();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

}
