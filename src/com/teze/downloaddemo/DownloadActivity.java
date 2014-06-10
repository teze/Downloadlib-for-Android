package com.teze.downloaddemo;

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teze.downloaddemo.DownloadListAdapter.ClickListener;
import com.teze.downloaddemo.DownloadListAdapter.ViewHolder;
import com.teze.downloaddemo.DownloadService.IDownloadService;
import com.teze.downloaddemo.DownloadService.State;
import com.teze.downloaddemo.HttpClientTool.DownloadCallback;

public class DownloadActivity extends ActionBarActivity implements OnItemClickListener,ClickListener{

	protected static final String TAG = "DownloadActivity";
	private ListView listView;
	private DownloadListAdapter mAdapter;
	private IDownloadService downloadService;
	private DownloadProcess process;

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
		initData();
		loadAdapter();
		bindService();
	}

	@Override
	protected void onDestroy() {
		unbindService(serviceConnection);
		super.onDestroy();
	}


	private void initView(){
		listView=(ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);
	}


	private void initData(){
		process=new DownloadProcess();
		mAdapter=new DownloadListAdapter(this);
		mAdapter.setClickListener(this);
		listView.setAdapter(mAdapter);
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

	
	private void startDownload(final View view, final ViewHolder holder,FileInfo info){
		downloadService.startDownload(info,new DownloadCallback() {

			@Override
			public void onSuccess() {
				updateProgress(view,holder,100);
			}

			@Override
			public void onStop() {

			}

			@Override
			public void onProgress(long progress) {
				updateProgress(view, holder,(int) progress);
			}

			@Override
			public void onFailed() {

			}
			@Override
			public void onStart(Object obj) {
				
			}
		});
	}
	
	private boolean pauseDownload(Object obj){
		return downloadService.pauseDownload(obj);
	}
	
	private void updateProgress(final View view,final ViewHolder holder,final int progress){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Loger.d(TAG, "updateProgress >> "+progress);
				ProgressBar progressBar=holder.progressBar;
				TextView percent=holder.progressText;
				progressBar.setProgress(progress);
				percent.setText(progress+"%");
				if (progress==100) {
					holder.downloadBtn.setText(R.string.finished);
				}
			}
		});
	}

	@Override
	public void onDownloadClick(View button, ViewHolder holder,int position) {
		FileInfo item=mAdapter.getItem(position);
		String path=item.filePath;
		State key=downloadService.getItemState(path);
		Button btn=(Button) button;
		switch (key) {
		case RUNNING:
			boolean result=pauseDownload(path);
			btn.setText(result?R.string.running:R.string.pause);
			break;
		case PAUSE:
			startDownload(button,holder,item);
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
	
}
