package in.teze.download;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

/**功能：
 * FileInfo
 * @author   by fooyou 2014年6月12日   下午3:35:34
 */
public class FileInfo  implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int STATE_FINISHED = 1;
	public static final int STATE_RUNNING = 0;
	public static final int STATE_STOPPED = -1;
	
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String filePath;
	@DatabaseField
	public String url;
	@DatabaseField
	public String imgUri;
	@DatabaseField
	public String name;
	@DatabaseField
	public int progress;
	@DatabaseField
	public int fileSize;
	@DatabaseField
	public int state;// 1:finished 0:running -1:stopped
	
	FileInfo(){
	}
	
	
}


