package com.nyist.vnow.struct;

import java.io.File;

import android.graphics.Bitmap;

public class FileUpdate {
	private String mFileName;
	private int mFileUpdate;
	private Bitmap mFileImg;
	private boolean isSuccess;
	private File mFile;
	public File getmFile() {
		return mFile;
	}
	public void setmFile(File mFile) {
		this.mFile = mFile;
	}
	public String getmFileName() {
		return mFileName;
	}
	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}
	public int getmFileUpdate() {
		return mFileUpdate;
	}
	public void setmFileUpdate(int mFileUpdate) {
		this.mFileUpdate = mFileUpdate;
	}
	public Bitmap getmFileImg() {
		return mFileImg;
	}
	public void setmFileImg(Bitmap mFileImg) {
		this.mFileImg = mFileImg;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
}
