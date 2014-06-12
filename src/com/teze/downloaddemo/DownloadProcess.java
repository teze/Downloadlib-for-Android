package com.teze.downloaddemo;

import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.RuntimeExceptionDao;

/**功能：
 * DownloadProcess
 * @author   by fooyou 2014年6月12日   下午3:35:25
 */
public class DownloadProcess {
	
	private static final String TAG = "DownloadProcess";
	public DatabaseHelper helper=APP.getDbHelper();
	private RuntimeExceptionDao<FileInfo, Integer> fileDao;
	
	public DownloadProcess(){
		helper=APP.getDbHelper();
		fileDao=helper.getFileDataDao();
	}
	
	public List<FileInfo> getDownloadList(){
		List<FileInfo> list=fileDao.queryForAll();
		return list;
	}
	
	public int addDownloadDb(FileInfo info){
		return fileDao.create(info);
	}
	
	public int updateFileSizeDb(FileInfo info){
		if (info==null) {
			return -1;
		}
		int rowID=-1;
		String fileKey=info.filePath;
		String fileSize=info.fileSize+"";
		String state=info.state+"";
		String sql="UPDATE '"+DatabaseHelper.TABLE_NAME+"' SET fileSize = ?,state=? WHERE filePath =?";
		String[] args={fileSize,state,fileKey};
		try {
			rowID=fileDao.updateRaw(sql, args);
		} catch (Exception e) {
			e.printStackTrace();
			Loger.w(TAG, "updateDownloadDb error >>"+info.toString());
		}
		return rowID;
	}
	
	public int updateProgressDb(FileInfo info){
		SQLiteDatabase db =null;
		int rows=-1;
		try {
			db=helper.getWritableDatabase();
			ContentValues values=new ContentValues();
			String fileKey=info.filePath;
			String progress=info.progress+"";
			String state=info.state+"";
			values.put("progress", progress);
			values.put("state", state);
			
			String whereClause="filePath =?";
			String whereArgs[]={fileKey};
			rows=db.update(DatabaseHelper.TABLE_NAME,values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			Loger.w(TAG, "updateFileRecord error >>"+info.toString());
		}
		return rows;
	}
	
}
