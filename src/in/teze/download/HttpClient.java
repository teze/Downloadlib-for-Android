package in.teze.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.widget.Toast;

/**功能：
 * HttpClientTool
 * @author   by fooyou 2014年6月12日   下午3:35:42
 */
public class HttpClient {

	private static final int TIMEOUT_READ = 20 * 1000;
	private static final int TIMEOUT_CONNECT = 10 * 1000;
	protected static final String DAT = ".Dat";
	protected static final String TMP = ".tmp";
	protected static final String TAG = "HttpClientTool";


	public static RandomAccessFile continuousDownload(String urlString,String filePath,DownloadCallback callback) {
		Response response=new Response(urlString,filePath);
		InputStream downInputStream = null;
		RandomAccessFile fileTarget = null;
		File temp=null;
		long fileSize = -1;
		long progress = 0;
		try {
			if (callback==null) {
				callback=new DefulatDownloadCallback();
			}
			temp=new File(filePath);
			if (temp.exists()&& callback!=null) {
				callback.onSuccess(filePath,response);
				return null;
			}else{
				temp.getParentFile().mkdirs();
			}

			fileTarget = new RandomAccessFile(filePath + TMP, "rw");
			long startPosition = fileTarget.length();

			HttpURLConnection connection;
			connection = getConnection(urlString);
			if (connection == null) {
				callback.onFailed(filePath, response);
				return null;
			}
			connection.setConnectTimeout(TIMEOUT_CONNECT);
			connection.setReadTimeout(TIMEOUT_READ);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Range", "bytes=" + startPosition+ "-");
			connection.addRequestProperty("Accept-Encoding", "gzip");// EOFException
			connection.addRequestProperty("Accept-Encoding", "zip");// EOFException
			connection.setRequestProperty("Connection", "close");// EOFException
			connection.connect();
			downInputStream = connection.getInputStream();

			//1.Database record mode

			/*fileSize = connection.getContentLength();
			if (callback !=null) {
				response.fileSize=fileSize;
				callback.onStart(filePath, response);
			}*/

			//2. File record mode
			boolean isFirst = false;
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
				if (fileSize < 0) {
					callback.onFailed(filePath, response);
				}
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
			if (callback !=null) {
				response.fileSize=fileSize;
				callback.onStart(filePath, response);
			}
			if (StorageUtil.getFreeSpace(temp.getParent())< fileSize) {
				response.msg="存储卡空间不足";
				response.progress=progress;
				callback.onFailed(filePath, response);
				Toast.makeText(APP.getInstance(), "存储卡空间不足", Toast.LENGTH_SHORT).show();
				return null;
			}

			byte[] buffer = new byte[1024];
			int length = 0;
			fileTarget.seek(startPosition);
			while (!Thread.currentThread().isInterrupted()&& (length = downInputStream.read(buffer)) != -1) {
				fileTarget.write(buffer, 0, length);
				progress = fileTarget.length() * 100 / fileSize;
				callback.onProgress(filePath, progress);
				/*Loger.i(TAG, "progress >> "+progress);*/
			}
			if (Thread.currentThread().isInterrupted()) {
				response.progress=progress;
				callback.onStop(filePath,response);
			}
			downInputStream.close();
			fileTarget.close();
			if (progress == 100) {
				File tempFile = new File(filePath + TMP);
				File file = new File(filePath);
				tempFile.renameTo(file);
				callback.onSuccess(filePath,response);
				if (recordText.exists()) {
					recordText.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.exception=e;
			response.progress=progress;
			callback.onFailed(filePath, response);
			return null;
		} finally {
			//TODO some stuff
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
		public void onProgress(String fileKey,long progress);

		public void onSuccess(String fileKey,Response obj);

		public void onFailed(String fileKey,Response obj);

		public void onStop(String fileKey,Response obj);

		public void onStart(String fileKey,Response obj);
	}
	
	public static class DefulatDownloadCallback implements DownloadCallback{

		@Override
		public void onProgress(String fileKey, long progress) {
			Loger.i(TAG, "onProgress");
		}

		@Override
		public void onSuccess(String fileKey, Response obj) {
			Loger.i(TAG, "onSuccess");
			
		}

		@Override
		public void onFailed(String fileKey, Response obj) {
			Loger.i(TAG, "onFailed");
		}

		@Override
		public void onStop(String fileKey, Response obj) {
			Loger.i(TAG, "onStop");
		}

		@Override
		public void onStart(String fileKey, Response obj) {
			Loger.i(TAG, "onStart");
		}
	}

	public static class Response implements Serializable{
		private static final long serialVersionUID = 1L;
		String url;
		String filePath;
		long fileSize;
		long progress;
		String msg;
		Exception exception;

		public Response(String url,String filePath){
			this.url=url;
			this.filePath=filePath;
		}
	}
}
