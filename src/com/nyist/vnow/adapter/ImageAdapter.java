package com.nyist.vnow.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowColleageAdapter.ViewHolder;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.UploadCaptrue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private List<UploadCaptrue> captrueList;
    private ImageLoader mImageLoader;
    
    
    class ViewHolder {
		ImageView img;
		ProgressBar loadProgress;
		TextView loadWait;
		TextView fileName;
	}
    
    public ImageAdapter(Context mContext, List<UploadCaptrue> list)
    {
        super();
        this.mContext = mContext;
        this.captrueList = list;
    }
    
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return captrueList.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
    
    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
    	ViewHolder viewHolder = null;
		final UploadCaptrue mContent = captrueList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_upload_photo, null);
			viewHolder.img = (ImageView) convertView
					.findViewById(R.id.photo_img);
			viewHolder.loadProgress = (ProgressBar) convertView
					.findViewById(R.id.upload_progress);
			viewHolder.loadWait = (TextView) convertView
					.findViewById(R.id.upload_progress_wait);
			viewHolder.fileName = (TextView) convertView
					.findViewById(R.id.file_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (mImageLoader == null) {
			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		}
		if(position == 0){
			viewHolder.loadWait.setVisibility(View.GONE);
			viewHolder.loadProgress.setVisibility(View.VISIBLE);
		}else{
			viewHolder.loadWait.setVisibility(View.VISIBLE);
			viewHolder.loadProgress.setVisibility(View.GONE);
		}
		viewHolder.fileName.setText(mContent.getPhotoName());
		viewHolder.img.setImageBitmap(mContent.getBmpPhoto());
        return convertView;
    }
    
}