
package com.teze.downloaddemo;

import android.os.StatFs;

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
