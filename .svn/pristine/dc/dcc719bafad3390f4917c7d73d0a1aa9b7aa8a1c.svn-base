package com.nyist.vnow.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nyist.vnow.R;
import com.nyist.vnow.utils.ImageLoaderUtils;

/**
 * @author harry
 * @version Creat on 2014-6-27上午11:57:05
 */
public class ConfMemberGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mDatas;
    private LayoutInflater mInflater;

    public ConfMemberGridAdapter(Context context, ArrayList<String> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    public void updateData(ArrayList<String> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public final class ConfMemberHolder {
        public ConfMemberHolder(ImageView imageview) {
            this.imageview = imageview;
        }

        ImageView imageview;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() + 1 : 1;
    }

    @Override
    public Object getItem(int position) {
        return position != mDatas.size() ? mDatas.get(position) : "drawable://" + R.drawable.img_add_member;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConfMemberHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.select_imageview, parent,
                    false);
            holder = new ConfMemberHolder((ImageView) convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ConfMemberHolder) convertView.getTag();
        }
        bindView(position, convertView);
        return convertView;
    }

    /**
     * @param position
     * @param convertView
     */
    private void bindView(int position, View convertView) {
        if (mDatas.size() == 0) {
            ImageLoaderUtils.imageLoader.displayImage("drawable://" + R.drawable.img_add_member, (ImageView) convertView);
        }
        else {
            if (position < mDatas.size()) {
                String string = mDatas.get(position);
                ImageLoaderUtils.imageLoader.displayImage(string, (ImageView) convertView);
            }
            else {
                ImageLoaderUtils.imageLoader.displayImage("drawable://" + R.drawable.img_add_member, (ImageView) convertView);
            }
        }
    }
}
