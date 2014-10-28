package in.teze.download;

import android.os.Environment;

/**功能：
 * Global
 * @author   by fooyou 2014年6月12日   下午3:35:38
 */
public class Global {
	
	public static String SCHEMA = "in.teze.downloadlib.";

	public static class FileOrDir{
		public static String APP="";
		public static String AppDir=Environment.getExternalStorageDirectory()+"/download/";
	}
	
	public static class URL{
		public static final String URL_AUTH="http://www.baidu.com/";
	}
	
	
}

