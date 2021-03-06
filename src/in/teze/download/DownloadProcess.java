package in.teze.download;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.RuntimeExceptionDao;

/**功能：
 * DownloadProcess
 * @author   by fooyou 2014年6月12日   下午3:35:25
 */
public class DownloadProcess {
	
	private static final String TAG = "DownloadProcess";
	public DatabaseHelper helper;
	private RuntimeExceptionDao<FileInfo, Integer> fileDao;
	
	public DownloadProcess(Context context){
		helper=DatabaseHelper.getHelper(context);
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
	
	public boolean isRecordExist(FileInfo info) {
		if (info == null) {
			return false;
		}
		String fileKey = info.filePath;
		Map<String, Object> map=new HashMap<String, Object>();
	
		map.put("filePath", fileKey);
		try {
			List<FileInfo> fileInfos= fileDao.queryForFieldValuesArgs(map);
			if (fileInfos != null && fileInfos.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Loger.w(TAG, "query DownloadDb error >>" + info.toString());
		}
		return false;
	}
	
}
