package com.teze.downloaddemo;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**功能：
 * DatabaseHelper
 * @author   by fooyou 2014年6月12日   下午3:35:03
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public  static final String TABLE_NAME = "fileinfo";
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "download_record.db";
	private static final int DATABASE_VERSION = 1;

	private Dao<FileInfo, Integer> fileDao = null;
	private RuntimeExceptionDao<FileInfo, Integer> fileRuntimeDao = null;
	private static DatabaseHelper instance;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper getHelper(){
		if (instance==null) {
			instance=new DatabaseHelper(APP.getInstance());
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Loger.i(TAG, "onCreate");
			TableUtils.createTable(connectionSource, FileInfo.class);
		} catch (SQLException e) {
			Loger.i(TAG, "Can't create database");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Loger.i(TAG, "onUpgrade");
			TableUtils.dropTable(connectionSource, FileInfo.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Loger.i(TAG, "Can't drop databases");
			throw new RuntimeException(e);
		}
	}

	public Dao<FileInfo, Integer> getDao() throws SQLException {
		if (fileDao == null) {
			fileDao = getDao(FileInfo.class);
		}
		return fileDao;
	}

	public RuntimeExceptionDao<FileInfo, Integer> getFileDataDao() {
		if (fileRuntimeDao == null) {
			fileRuntimeDao = getRuntimeExceptionDao(FileInfo.class);
		}
		return fileRuntimeDao;
	}

	@Override
	public void close() {
		super.close();
		fileDao = null;
		fileRuntimeDao = null;
	}
	
	
}
