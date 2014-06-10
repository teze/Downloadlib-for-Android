package com.teze.downloaddemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public  class DownloadListAdapter extends CommonAdapter<FileInfo>{


	private ClickListener clickListener;
	public DownloadListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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

		viewHolder.name.setText(item.name);
		int percent=0;
		if(item.fileSize!=0){
			percent=item.progress*100/item.fileSize;
		}

		viewHolder.progressText.setText(percent+"%");
		viewHolder.progressBar.setProgress(item.progress);
		final ViewHolder holder=viewHolder;
		viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(clickListener!=null){
					clickListener.onDownloadClick(v,holder,position);
				}
			}
		});

		return convertView;
	}


	public ClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;
	}


	static class ViewHolder{
		ImageView image;
		TextView name;
		TextView progressText;
		ProgressBar progressBar;
		Button downloadBtn;
	}

	public interface ClickListener{
		public void onDownloadClick(View v,ViewHolder holder,int position);
	}
}