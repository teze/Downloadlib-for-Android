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
	
	public void addDownload(FileInfo info){
		fileDao.create(info);
	}
	
	public void updateDownloadState(){
		
	}
}
