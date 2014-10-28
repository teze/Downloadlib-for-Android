package in.teze.download;

import in.teze.download.R;
import in.teze.download.DownloadListAdapter.ClickListener;
import in.teze.download.DownloadService.IDownloadService;
import in.teze.download.DownloadService.State;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**功能：
 * DownloadActivity
 * @author   by fooyou 2014年6月12日   下午3:35:17
 */
public class DownloadActivity extends ActionBarActivity implements OnItemClickListener,ClickListener{

	protected static final String TAG = "DownloadActivity";
	private ListView listView;
	private DownloadListAdapter mAdapter;
	private IDownloadService downloadService;
	private DownloadProcess process;
	private DownloadBroadcast downloadReceiver;

	private ServiceConnection serviceConnection=new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Loger.i(TAG,"onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Loger.i(TAG,"onServiceConnected");
			if (service instanceof IDownloadService) {
				downloadService=(IDownloadService) service;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		initView();
		bindService();
		initData();
		loadAdapter();
	}

	@Override
	protected void onDestroy() {
		Loger.i(TAG, "onDestroy");
		unbindService(serviceConnection);
		unregisterReceiver();
		super.onDestroy();
	}


	private void initView(){
		listView=(ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);
	}


	private void initData(){
		process=new DownloadProcess(getApplicationContext());
		mAdapter=new DownloadListAdapter(this);
		mAdapter.setClickListener(this);
		listView.setAdapter(mAdapter);
		registerReceiver();
	}

	private void registerReceiver(){
		downloadReceiver=new DownloadBroadcast();
		IntentFilter filter=new IntentFilter(DownloadService.ACTION_DOWNLOAD);
		registerReceiver(downloadReceiver, filter);
	}

	private void unregisterReceiver(){
		if (downloadReceiver!=null) {
			unregisterReceiver(downloadReceiver);
		}
	}

	public void loadAdapter(){
		List<FileInfo> list=process.getDownloadList();
		mAdapter.setItems(list);
		mAdapter.notifyDataSetChanged();
	}


	private void bindService(){
		Intent service=new Intent(this, DownloadService.class);
		startService(service);
		bindService(service, serviceConnection, BIND_AUTO_CREATE);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		/*FileInfo item=mAdapter.getItem(position);
		startDownload(view,item);*/
	}


	private void startDownload(FileInfo info){
		if (downloadService!=null && info!=null) {
			downloadService.startDownload(info);
		}
	}

	private boolean pauseDownload(Object obj){
		if (downloadService!=null) {
			return downloadService.stopDownload(obj);
		}
		return false;
	}

	private void updateProgress(final String fileKey,final long progress){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/*Loger.d(TAG, "updateProgress >> "+progress);*/
				if (mAdapter==null||listView==null) {
					return;
				}
				int position=mAdapter.getViewPosition(fileKey);
				View currentView=getViewByPosion(position);
				if (currentView!=null) {
					ProgressBar progressBar=(ProgressBar) currentView.findViewById(R.id.progressBar);
					TextView percent=(TextView) currentView.findViewById(R.id.progressText);
					Button downloadBtn=(Button) currentView.findViewById(R.id.downloadBtn);
					progressBar.setProgress((int) progress);
					percent.setText(progress+"%");
					if (progress==100) {
						downloadBtn.setText(R.string.finished);
					}
				}
			}
		});
	}
	
	private void updateDownloadBtnText(final String fileKey,final State state){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mAdapter==null||listView==null) {
					return;
				}
				int position=mAdapter.getViewPosition(fileKey);
				View currentView=getViewByPosion(position);
				if (currentView!=null) {
					Button downloadBtn=(Button) currentView.findViewById(R.id.downloadBtn);
					switch (state) {
					case RUNNING:
						downloadBtn.setText(R.string.pause);
						break;
					case STOPPED:
						downloadBtn.setText(R.string.start);
						break;
					case ERROR:
						downloadBtn.setText(R.string.reload);
						break;
					case FINISHED:
						downloadBtn.setText(R.string.finished);
						break;
					default:
						break;
					}
				}
			}
		});
	}
	
	public void updateItemCache(String fileKey,long progress,int state){
		int position=mAdapter.getViewPosition(fileKey);
		FileInfo fileInfo=mAdapter.getItem(position);
		if (fileInfo!=null) {
			fileInfo.progress=(int) progress;
			fileInfo.state=state;
		}
	}
	
	public void updateItemCache(String fileKey,int state){
		int position=mAdapter.getViewPosition(fileKey);
		FileInfo fileInfo=mAdapter.getItem(position);
		if (fileInfo!=null) {
			fileInfo.state=state;
		}
	}
	
	public View getViewByPosion(int wantPosition){
		if (listView==null) {
			return null;
		}
		int firstPosition=listView.getFirstVisiblePosition()-listView.getHeaderViewsCount();
		int childPosition=wantPosition-firstPosition;
		if (childPosition<0 ||childPosition >= listView.getChildCount()) {
			return null;
		}else{
			View wantView=listView.getChildAt(childPosition);
			return wantView;
		}
	}

	@Override
	public void onDownloadClick(View button,int position) {//TODO need modify
		FileInfo item=mAdapter.getItem(position);
		String path=item.filePath;
		State key=downloadService.getItemState(path);
		Button btn=(Button) button;
		switch (key) {
		case RUNNING:
			boolean result=pauseDownload(path);
			btn.setText(result?R.string.start:R.string.pause);
			break;
		case STOPPED:
			startDownload(item);
			btn.setText(R.string.pause);
			break;
		case ERROR:
			btn.setText(R.string.reload);
			break;
		case FINISHED:
			btn.setText(R.string.finished);
			break;

		default:
			break;
		}
	}

	class DownloadBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction=intent.getAction();
			if (intentAction.equals(DownloadService.ACTION_DOWNLOAD)) {
				String actionType=intent.getStringExtra(DownloadService.INTENT_ACTION_TYPE);
				Bundle data=intent.getBundleExtra(DownloadService.INTENT_BUNDLE);
				if (data==null) {
					return;
				}
				String fileKey=data.getString(DownloadService.INTENT_FILE_ID);
				if (TextUtils.isEmpty(actionType)) {
					return;
				}
				if (actionType.equals(DownloadService.Action.START)) {
					updateItemCache(fileKey,FileInfo.STATE_RUNNING);
					updateDownloadBtnText(fileKey, State.RUNNING);
				}
				if (actionType.equals(DownloadService.Action.STOP)) {
					updateItemCache(fileKey, FileInfo.STATE_STOPPED);
					updateDownloadBtnText(fileKey, State.STOPPED);
				}
				if (actionType.equals(DownloadService.Action.PROGRESS)) {
					long progress=data.getLong(DownloadService.INTENT_PROGRESS);
					updateProgress(fileKey, progress);
					updateItemCache(fileKey, progress, FileInfo.STATE_RUNNING);
				}
				if (actionType.equals(DownloadService.Action.FAILED)) {
					updateItemCache(fileKey, FileInfo.STATE_STOPPED);
					updateDownloadBtnText(fileKey, State.ERROR);
				}
				if (actionType.equals(DownloadService.Action.SUCCESS)) {
					updateProgress(fileKey, 100);
					updateItemCache(fileKey, FileInfo.STATE_FINISHED);
					updateDownloadBtnText(fileKey, State.FINISHED);
				}
			}
		}
	}
	
}
