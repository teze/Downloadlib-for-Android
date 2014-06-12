package com.teze.downloaddemo;

import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class DownloadProcess {
	
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
	
	public int updateDownloadDb(FileInfo info){
		if (info==null) {
			return -1;
		}
		String fileKey=info.filePath;
		String fileSize=info.fileSize+"";
		String sql="UPDATE '"+DatabaseHelper.TABLE_NAME+"' SET fileSize = ? WHERE filePath =?";
		String[] args={fileSize,fileKey};
		int rowID=fileDao.updateRaw(sql, args);
		return rowID;
	}
}
