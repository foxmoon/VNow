package com.nyist.vnow.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VNSdFileAdapter extends BaseAdapter {
    Context context;
    File current;
    List<File> mListFiles;
    private FileListener mFileListener;

    public VNSdFileAdapter(Context context, File current, FileListener listener) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.current = current;
        mListFiles = new ArrayList<File>();
        mFileListener = listener;
        setData(current);
    }

    public File getCurrentParent() {
        return current;
    }

    public void setData(File current) {
        File[] files = current.listFiles();
        for (int i = 0; i < files.length; i++) {
            mListFiles.add(files[i]);
        }
        Collections.sort(mListFiles, new FileComparator());
        this.current = current;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListFiles.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mListFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView fileName;
        ImageView fileIcon;
        LinearLayout clickView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_doc, null);
        }
        else {
            view = convertView;
        }
        fileIcon = (ImageView) view.findViewById(R.id.icon);
        fileName = (TextView) view.findViewById(R.id.file_name);
        clickView = (LinearLayout) view.findViewById(R.id.itemView);
        File file = (File) getItem(position);
        if (file.isDirectory()) {
            fileIcon.setBackgroundDrawable(context.getResources().getDrawable(
                    R.drawable.folder));
        }
        else {
            fileIcon.setBackgroundDrawable(context.getResources().getDrawable(
                    R.drawable.file));
        }
        fileName.setText(file.getName());
        clickView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListFiles.get(position).isFile()) {
                    VNowApplication.the().showToast(mListFiles.get(position).getName());
                    mFileListener.onClickFile(mListFiles.get(position));
                }
                else {
                    current = mListFiles.get(position);
                    File[] file = current.listFiles();
                    if (null != file) {
                        mListFiles.clear();
                        for (int i = 0; i < file.length; i++) {
                            mListFiles.add(file[i]);
                        }
                        Collections.sort(mListFiles, new FileComparator());
                        notifyDataSetChanged();
                    }
                }
            }
        });
        return view;
    }

    class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            // TODO Auto-generated method stub
            if (f1 == null || f2 == null) {// 先比较null
                if (f1 == null) {
                    {
                        return -1;
                    }
                }
                else {
                    return 1;
                }
            }
            else {
                if (f1.isDirectory() == true && f2.isDirectory() == true) { // 再比较文件夹
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
                else {
                    if ((f1.isDirectory() && !f2.isDirectory()) == true) {
                        return -1;
                    }
                    else if ((f2.isDirectory() && !f1.isDirectory()) == true) {
                        return 1;
                    }
                    else {
                        return f1.getName().compareToIgnoreCase(f2.getName());// 最后比较文件
                    }
                }
            }
        }
    }

    public interface FileListener {
        public void onClickFile(File file);
    }
}
