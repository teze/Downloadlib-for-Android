package com.teze.downloaddemo;

import android.os.Environment;

/**功能：
 * Global
 * @author   by fooyou 2014年6月12日   下午3:35:38
 */
public class Global {

	public static class FileOrDir{
		public static String APP="";
		public static String AppDir=Environment.getExternalStorageDirectory()+"/teze/";
	}
	
	public static class URL{
		public static final String URL_AUTH="http://www.baidu.com/";
	}
}

