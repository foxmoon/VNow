package com.nyist.vnow.adapter;

import java.util.List;

import com.nyist.vnow.R;
import com.nyist.vnow.struct.FileUpdate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VNowFileUpdateAdapter extends BaseAdapter {
    private List<FileUpdate> mListFiles;
    private LayoutInflater mLayoutInflater;

    class ViewHolder {
        ImageView imgFile;
        TextView txtName;
        TextView txtUpdate;
        TextView txtWait;
        ProgressBar pbUpdate;
    }

    public VNowFileUpdateAdapter(Context context, List<FileUpdate> list) {
        super();
        // TODO Auto-generated constructor stub
        this.mListFiles = list;
        this.mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_file_update,
                    null);
            holder.imgFile = (ImageView) convertView
                    .findViewById(R.id.img_file);
            holder.txtName = (TextView) convertView
                    .findViewById(R.id.file_name);
            holder.txtUpdate = (TextView) convertView
                    .findViewById(R.id.file_update);
            holder.txtWait = (TextView) convertView
                    .findViewById(R.id.upload_progress_wait);
            holder.pbUpdate = (ProgressBar) convertView
                    .findViewById(R.id.pb_file_update);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileUpdate item = mListFiles.get(position);
        if (null != item.getmFileImg()) {
            holder.imgFile.setImageBitmap(item.getmFileImg());
        }
        holder.txtName.setText(item.getmFileName());
        // if (item.isSuccess()) {
        // holder.pbUpdate.setVisibility(View.GONE);
        // } else {
        // holder.pbUpdate.setVisibility(View.VISIBLE);
        // }
        if (position == 0) {
            holder.pbUpdate.setVisibility(View.VISIBLE);
            holder.txtWait.setVisibility(View.GONE);
        }
        else {
            holder.pbUpdate.setVisibility(View.GONE);
            holder.txtWait.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
