package com.nyist.vnow.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nyist.vnow.R;
import com.nyist.vnow.fragment.VNowFragmentSynPic;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class SynImageAdapter extends BaseAdapter {
	public static final BaseAdapter Adapter = null;
	private List<String> imageUrls; // 图片地址list
	private Context context;
	private ImageLoader mImageLoader;
	int mGalleryItemBackground;

	public SynImageAdapter(List<String> imageUrls, Context context) {
		this.imageUrls = imageUrls;
		this.context = context;
		// /*
		// * 使用在res/values/attrs.xml中的<declare-styleable>定义 的Gallery属性.
		// */
		TypedArray a = context.obtainStyledAttributes(R.styleable.Gallery);
		/* 取得Gallery属性的Index id */
		mGalleryItemBackground = a.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
		/* 让对象的styleable属性能够反复使用 */
		a.recycle();
	}

	public int getCount() {
		return imageUrls.size();
	}

	public Object getItem(int position) {
		return imageUrls.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = new ImageView(context);
		
		if (mImageLoader == null) {
			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		// 设置所有图片的资源地址
		view.setScaleType(ImageView.ScaleType.CENTER);
		view.setLayoutParams(new Gallery.LayoutParams(180, 240));
		view.setPadding(2, 2, 2, 2);
		view.setImageResource(R.drawable.img_syn_picdefult);
		mImageLoader.displayImage(imageUrls.get(position), view);
		/* 设置Gallery背景图 */
		return view;
	}
}
