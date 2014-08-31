
package in.teze.download;

import android.os.StatFs;

/**功能：
 * StorageUtil
 * @author   by fooyou 2014年6月12日   下午3:35:50
 */
public class StorageUtil {
	public static long getFreeSpace(String path){
		long freeSize=0;
		StatFs statFs=new StatFs(path);
		@SuppressWarnings("deprecation")
		long blocks=statFs.getAvailableBlocks();
		@SuppressWarnings("deprecation")
		long size=statFs.getBlockSize();
		freeSize=blocks*size;
		return freeSize;
	}
}
