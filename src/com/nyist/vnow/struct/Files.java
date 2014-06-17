package com.nyist.vnow.struct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

public class Files {

	public static String sd_card = "/sdcard/vn_temp/";

	/**
	 * �����ļ���
	 * 
	 * @param context
	 */
	
	public Files(Context context){
		mkdir(context);
	}
	private void mkdir(Context context) {
		System.out.println(13123321);
		File file;
		file = new File(sd_card);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	

	/**
	 * ����ͼƬ��SD��
	 * 
	 * @param URL
	 * @param data
	 * @throws IOException
	 */
	public void saveImage(String URL, byte[] data) throws IOException {
		saveData(sd_card, URL, data);
	}

	/**
	 * ��ȡͼƬ
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public byte[] readImage(String filename) throws IOException {
		byte[] tmp = readData(sd_card, filename);
		return tmp;
	}

	/**
	 * ��ȡͼƬ����
	 * 
	 * @param path
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private byte[] readData(String path, String name) throws IOException {
		// String name = MyHash.mixHashStr(url);
		ByteArrayBuffer buffer = null;
		String paths = path + name;
		File file = new File(paths);
		if (!file.exists()) {
			return null;
		}
		InputStream inputstream = new FileInputStream(file);
		buffer = new ByteArrayBuffer(1024);
		byte[] tmp = new byte[1024];
		int len;
		while (((len = inputstream.read(tmp)) != -1)) {
			buffer.append(tmp, 0, len);
		}
		inputstream.close();
		return buffer.toByteArray();
	}

	/**
	 * ͼƬ���湤����
	 * 
	 * @param path
	 * @param fileName
	 * @param data
	 * @throws IOException
	 */
	private void saveData(String path, String fileName, byte[] data)
			throws IOException {
		// String name = MyHash.mixHashStr(AdName);
		File file = new File(path + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(data);
		outStream.close();
	}

	/**
	 * �ж��ļ��Ƿ���� true���� false������
	 * 
	 * @param url
	 * @return
	 */
	public boolean compare(String url) {
		String paths = sd_card + url;
		File file = new File(paths);
		if (!file.exists()) {
			return false;
		}
		return true;
	}
	
	public void delFile(){
		File file;
		file = new File(sd_card);
		file.delete();
	}

}
