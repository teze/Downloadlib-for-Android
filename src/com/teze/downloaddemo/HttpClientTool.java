package com.teze.downloaddemo;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.widget.Toast;

public class HttpClientTool {

	private static final String DAT = ".Dat";
	private static final String TMP = ".tmp";
	private static final int TIMEOUT_READ = 20 * 1000;
	private static final int TIMEOUT_CONNECT = 10 * 1000;
	private static final String TAG = "HttpClientTool";


	public static RandomAccessFile continuousDownload(String urlString,String filePath,DownloadCallback callback) {
		InputStream downInputStream = null;
		RandomAccessFile fileTarget = null;
		long fileSize = 0;
		long progress = 0;
		boolean isFirst = false;
		try {
			File temp=new File(filePath);
			if (temp.exists()) {
				callback.onSuccess();
				return null;
			}else{
				temp.getParentFile().mkdirs();
			}

			fileTarget = new RandomAccessFile(filePath + TMP, "rw");
			long startPosition = fileTarget.length();
			
			HttpURLConnection connection;
			connection = getConnection(urlString);
			if (connection == null) {
				return null;
			}
			connection.setConnectTimeout(TIMEOUT_CONNECT);
			connection.setReadTimeout(TIMEOUT_READ);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Range", "bytes=" + startPosition+ "-");
			connection.connect();
			downInputStream = connection.getInputStream();

			
			File recordText = new File(filePath + DAT);
			if (!recordText.exists()) {
				recordText.createNewFile();
			}
			if (recordText.length() > 0) {
				isFirst = false;
			} else {
				isFirst = true;
			}
			if (isFirst) {
				fileSize = connection.getContentLength();
				FileOutputStream os = new FileOutputStream(recordText);
				os.write((fileSize + "").getBytes());
				os.flush();
				os.close();
			} else {
				FileInputStream is = new FileInputStream(recordText);
				byte[] tempBuffer = new byte[is.available()];
				is.read(tempBuffer);
				fileSize = Integer.valueOf(new String(tempBuffer));
				is.close();
			}
			if (StorageUtil.getFreeSpace(recordText.getParent()) < connection.getContentLength()) {
				callback.onFailed();
				Toast.makeText(APP.getInstance(), "存储卡空间不足", Toast.LENGTH_SHORT).show();
				return null;
			}
			
			byte[] buffer = new byte[1024];
			int length = 0;
			fileTarget.seek(startPosition);
			while (!Thread.currentThread().isInterrupted()&& (length = downInputStream.read(buffer)) != -1) {
				fileTarget.write(buffer, 0, length);
				progress = fileTarget.length() * 100 / fileSize;
				callback.onProgress(progress);
				Loger.i(TAG, "progress >> "+progress);
			}
			if (Thread.currentThread().isInterrupted()) {
				callback.onStop();
			}
			downInputStream.close();
			fileTarget.close();
			if (progress == 100) {
				File tempFile = new File(filePath + TMP);
				File file = new File(filePath);
				tempFile.renameTo(file);
				callback.onSuccess();
				if (recordText.exists()) {
					recordText.delete();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			callback.onFailed();
			return null;
		} catch (EOFException e) {
			e.printStackTrace();
			callback.onFailed();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			callback.onFailed();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			callback.onFailed();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			callback.onFailed();
		} finally {
			//TODO
		}
		return fileTarget;
	}



	public static HttpURLConnection getConnection(String urlString){
		HttpURLConnection connection = null ;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	} 


	public interface DownloadCallback {
		public void onProgress(long progress);

		public void onSuccess();

		public void onFailed();

		public void onStop();
		
		public void onStart(Object obj);
	}

	public class Response{
		String url;
		String filePath;
		Exception exception;
		
	}
}
