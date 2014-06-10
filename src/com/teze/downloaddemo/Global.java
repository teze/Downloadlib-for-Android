package com.teze.downloaddemo;

import android.os.Environment;

public class Global {

	public static class FileOrDir{
		public static String APP="";
		public static String AppDir=Environment.getExternalStorageDirectory()+"/teze/";
	}
	
	public static class URL{
		public static final String URL_AUTH="http://www.baidu.com/";
	}
}

