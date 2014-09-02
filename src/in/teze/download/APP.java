package in.teze.download;

import android.app.Application;

/**功能：
 * APP
 * @author   by fooyou 2014年6月11日   下午3:34:40
 */
public class APP extends Application {

	protected static final String TAG = "APP";
	protected static String APP_NAME = "APP";
	protected static String SCHEMA = "in.teze.downloadlib.";

	private static APP instance;
	private static DatabaseHelper dbHelper;

	public APP() {
		super();
		Loger.i(TAG, "APP is instanced");
		instance = this;
	}

	public static APP getInstance() {
		return instance;
	}
	
	public static DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void init() {
		initDataBase();
	}
	
	
	private void initDataBase(){
		dbHelper=new DatabaseHelper(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Loger.i(TAG, "APP is onCreated");
		init();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

}
