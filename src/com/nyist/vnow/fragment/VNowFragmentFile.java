package com.nyist.vnow.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNSdFileAdapter;
import com.nyist.vnow.adapter.VNSdFileAdapter.FileListener;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.FileUpdate;

public class VNowFragmentFile extends Fragment implements FileListener{
	private VNowCore mCore; 
	
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private ListView mFileList;
	private File mCurrentFile;
	private String mSdcardPath;
	private VNSdFileAdapter mFileAdapter;
	private View mFileView;
	private Button mBtnBack;
	
	private View mFileUpdateView;
	private ListView mListViewUpdate;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.vnow_login, container, false);
		mCore = VNowApplication.the().getCore();
		initUI(view);
		return view;
	}

	private void initUI(View view){
		mSdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File root = null;
		if(!TextUtils.isEmpty(mSdcardPath) && new File(mSdcardPath).canRead()){
			root  = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		}
		mFileList = (ListView) view.findViewById(R.id.file_list);
		mBtnBack = (Button) view.findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		mCurrentFile = root;
		mFileAdapter = new VNSdFileAdapter(getActivity(), mCurrentFile, this);
		mFileList.setAdapter(mFileAdapter);
	}
//	private void loadFileUpdateUI() {
//		if (null == mFileUpdateView) {
//			mFileUpdateView = findViewById(R.id.layout_file_update);
//		}
//		mFileUpdateView.setVisibility(View.VISIBLE);
//		mListViewUpdate = (ListView) findViewById(R.id.file_update_list);
//		if (null == mAdapterFileUpdate) {
//			mAdapterFileUpdate = new VNowFileUpdateAdapter(this,
//					mCore.getmListFileUpdate());
//			mListViewUpdate.setAdapter(mAdapterFileUpdate);
//		} else {
//			mAdapterFileUpdate.notifyDataSetChanged();
//		}
//	}
	private void uploadFile(File file) {
		try {
			BufferedInputStream inputStream = new BufferedInputStream(
					new FileInputStream(file));

			byte[] data = new byte[inputStream.available()];
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onClickFile(File file) {
		// TODO Auto-generated method stub
		FileUpdate update = new FileUpdate();
		update.setmFileName(file.getName());
		update.setmFile(file);
//		mCore.getmListFileUpdate().add(update);
		if (mCore.addUploadFile(update)) {
			if (mCore.getmListFileUpdate().size() == 1) {
				uploadFile(file);
			}
//			loadFileUpdateUI();
		} else {
			VNowApplication.the().showToast("该文件已经在上传列表中!");
		}
		
	}
	
}
