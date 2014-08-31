package in.teze.download;

import in.teze.download.R;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**功能：
 * DownloadListAdapter
 * @author   by fooyou 2014年6月12日   下午3:35:21
 */
public  class DownloadListAdapter extends CommonAdapter<FileInfo>{


	protected static final String TAG = "DownloadListAdapter";
	private ClickListener clickListener;
	private Map<String, String> viewMap;

	public DownloadListAdapter(Context context) {
		super(context);
		viewMap=new HashMap<String,String>();
	}

	@Override   

	public View getView(final int position, View convertView, ViewGroup parent) {
		/*Loger.i(TAG, "getView >> "+position);*/
		FileInfo item=getItem(position);
		ViewHolder viewHolder=null;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=View.inflate(mContext, R.layout.listitem_download, null);
			viewHolder.image=(ImageView) convertView.findViewById(R.id.image);
			viewHolder.name=(TextView) convertView.findViewById(R.id.name);
			viewHolder.progressText=(TextView) convertView.findViewById(R.id.progressText);
			viewHolder.progressBar=(ProgressBar) convertView.findViewById(R.id.progressBar);
			viewHolder.downloadBtn=(Button)convertView.findViewById(R.id.downloadBtn);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(item.name+">>"+position);
		int percent=item.progress;
		/*if(item.fileSize!=0){
			percent=item.progress*100/item.fileSize;
		}*/
		viewHolder.position=position;
		invokePosition(item.filePath, position);
		viewHolder.progressText.setText(percent+"%");
		viewHolder.progressBar.setProgress(item.progress);
		setDownloadBtnText(viewHolder.downloadBtn, item.state);
		viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(clickListener!=null){
					clickListener.onDownloadClick(v,position);
				}
			}
		});

		return convertView;
	}


	private void invokePosition(String key ,int position){
		if (!TextUtils.isEmpty(key)) {
			viewMap.put(key, position+"");
		}
	}

	private void setDownloadBtnText(Button button,int state){
		switch (state) {
		case FileInfo.STATE_FINISHED:
			button.setText(R.string.finished);
			break;
		case FileInfo.STATE_RUNNING:
			button.setText(R.string.pause);
			break;
		case FileInfo.STATE_STOPPED:
			button.setText(R.string.start);
			break;

		default:
			break;
		}
	}

	public  int getViewPosition(String key){
		int position=-1;
		try {
			if (!TextUtils.isEmpty(key) && viewMap!=null&& !viewMap.isEmpty()) {
				String value=viewMap.get(key);
				position=Integer.valueOf(value);
				//TODO some bug ,may be can't find the file key  by Fooyou
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return position;
	}

	public ClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;
	}


	static class ViewHolder{
		int position;
		ImageView image;
		TextView name;
		TextView progressText;
		ProgressBar progressBar;
		Button downloadBtn;
	}

	public interface ClickListener{
		public void onDownloadClick(View v,int position);
	}
}