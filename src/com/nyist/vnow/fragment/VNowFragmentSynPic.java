package com.nyist.vnow.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.SynImageAdapter;
import com.nyist.vnow.struct.Files;
import com.nyist.vnow.utils.Net;
import com.nyist.vnow.view.AlignLeftGallery;
public class VNowFragmentSynPic extends Fragment {
	public static HashMap<String, Bitmap> imagesCache = new HashMap<String, Bitmap>(); // 图片缓存
	private final int UPDATE_PICTURE = 0x01;
	private final int LOAD_PICTURE = 0x02;
	private final int LOAD_CURRENT = 0x03;
	private AlignLeftGallery mGalleryView;
	private View mMyView;
	private ImageButton mBtnClose;
	private boolean mHasMeasured = false;
	public SynImageAdapter mImageAdapter;
	private List<String> mUrls;
	private List<String> url = new ArrayList<String>(); // 需要下载图片的url地址
	private LinearLayout mLayout;
	private int num = 0;
	private Files mFileUtil;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int code = msg.what;
			switch (code) {
			case UPDATE_PICTURE:{
					LoadImageTask task = new LoadImageTask();// 异步加载图片
					task.execute(mUrls.get(mUrls.size()-1));
				if(null!=mImageAdapter){
					mImageAdapter.notifyDataSetChanged();
				}
			}
				break;
//			case LOAD_PICTURE:{
//				mImageAdapter.notifyDataSetChanged();
//			}
//				break;
			case LOAD_CURRENT:{
				for (int i = 0; i < url.size(); i++) {
					LoadImageTask task = new LoadImageTask();// 异步加载图片
					task.execute(url.get(i));
				}
				if(null!=mImageAdapter){
					mImageAdapter.notifyDataSetChanged();
				}
				url.clear();
			}
				break;
			}
		}
		
	};
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mMyView = inflater.inflate(R.layout.p2p_synpic_layout, container, false);
		mUrls = new ArrayList<String>(); 
		initUI();
		loadData();
		return mMyView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mFileUtil.delFile();
		imagesCache.clear();
		mUrls.clear();
		mUrls = null;
		super.onDestroy();
	}
	private void initUI(){
//		mGalleryView = (AlignLeftGallery) mMyView.findViewById(R.id.syn_gallery);
		mGalleryView = new AlignLeftGallery(getActivity());
		mLayout =  (LinearLayout) mMyView.findViewById(R.id.test_layout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getActivity().getWindowManager().getDefaultDisplay().getWidth()*2,
		LinearLayout.LayoutParams.WRAP_CONTENT);
		mGalleryView.setSpacing(10);
		lp.leftMargin = 6;
		lp.topMargin = 6;
		lp.rightMargin = 6;
		lp.bottomMargin = 6;
		mLayout.addView(mGalleryView,lp);
		mBtnClose = (ImageButton) mMyView.findViewById(R.id.syn_close_btn);
		mBtnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMyView.setVisibility(View.GONE);
			}
		});
//		mGalleryView.setOnItemClickListener(new AlignLeftGallery.IOnItemClickListener() {
//			
//			@Override
//			public void onItemClick(int position) {
//				Toast.makeText(getActivity(), position + " click!", Toast.LENGTH_SHORT).show();
//			}
//		});
	}

	public void setUIVisibility(int visiable){
		mMyView.setVisibility(visiable);
	}
	
	public void setUIVisibility(){
		if(mMyView.getVisibility() == View.GONE){
			mMyView.setVisibility(View.VISIBLE);
		}else{
			mMyView.setVisibility(View.GONE);
		}
	}
	
	
	/**
	 * 判断Gallery滚动是否停止,如果停止则加载当前页面的图片
	 */
	private void GalleryWhetherStop() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					int index = 0;
					index = num;
					Thread.sleep(1000);
					if (index == num) {
						url.add(mUrls.get(num));
						if (num != 0 && mUrls.get(num - 1) != null) {
							url.add(mUrls.get(num - 1));
						}
						if (num != mUrls.size() - 1 && mUrls.get(num + 1) != null) {
							url.add(mUrls.get(num + 1));
						}
						mHandler.sendEmptyMessage(LOAD_CURRENT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable).start();
	}
	public void updateImgList(String url){
		mUrls.add(url);
		mHandler.sendEmptyMessage(UPDATE_PICTURE);
	}
	private void loadData(){
		mUrls = new ArrayList<String>();
		mFileUtil = new Files(getActivity());
		Bitmap image = BitmapFactory.decodeResource(getResources(),
				R.drawable.img_syn_picdefult);
		imagesCache.put("background_non_load", image); 
		mImageAdapter = new SynImageAdapter(mUrls, getActivity());
		mGalleryView.setAdapter(mImageAdapter);
		mGalleryView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				num = arg2;
				GalleryWhetherStop();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	// 加载图片的异步任务
	class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			
			try {
				String url = params[0];
				if(null == url)
					return null;
				String imgName = url.substring(url.lastIndexOf("/")+1);
				boolean isExists = mFileUtil.compare(imgName);
				if (isExists) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 8; 
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					Bitmap bm = BitmapFactory.decodeFile(Files.sd_card+imgName,options);
					bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
					imagesCache.put(url, bm); // 把下载好的图片保存到缓存中
					if(imagesCache.size()>15){
						deleteFirstItem();
					}
					mHandler.sendEmptyMessage(LOAD_PICTURE);
				}else{
					Net net = new Net();
					byte[] data = net.downloadResource(getActivity(), url);
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
					mFileUtil.saveImage(imgName, data);
					mHandler.sendEmptyMessage(UPDATE_PICTURE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return bitmap;
		}
	}
	
	private void deleteFirstItem(){
		Iterator iter = imagesCache.entrySet().iterator();
		Object delUrl = null;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			delUrl = entry.getKey();
		}
		imagesCache.remove(delUrl);
		System.out.println(delUrl.toString());
	}
	
	private Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
		return null;
		}
	}
	
	private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		 int w = bitmap.getWidth();
		 int h = bitmap.getHeight();
		 Matrix matrix = new Matrix();
		 float scaleWidth = ((float) width / w);
		 float scaleHeight = ((float) height / h);
		 matrix.postScale(scaleWidth, scaleHeight);
		 Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		 return newbmp;
	}
	
}
