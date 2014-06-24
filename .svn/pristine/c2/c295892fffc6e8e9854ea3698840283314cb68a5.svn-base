package com.nyist.vnow.adapter;

import java.util.List;

import com.nyist.vnow.R;
import com.nyist.vnow.struct.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class VNowGroupAdapter extends BaseAdapter {
    private List<Group> mListGroups;
    private Context mContext;
    private boolean mIsDel = false;
    private DelListener mDelListener;

    final static class ViewHolder {
        ImageView imgHead;
        TextView txtName;
        Button mBtnDelGrp;
    }

    public VNowGroupAdapter(Context context, List<Group> list,
            DelListener listener) {
        mContext = context;
        this.mListGroups = list;
        mDelListener = listener;
    }

    public void updateListView(List<Group> list) {
        this.mListGroups = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListGroups.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String nickName = mListGroups.get(position).getName();
        ViewHolder holder = null;
        if (convertView == null || position < mListGroups.size()) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_group, null);
            holder.imgHead = (ImageView) convertView
                    .findViewById(R.id.item_group_logo);
            holder.txtName = (TextView) convertView
                    .findViewById(R.id.item_group_name);
            holder.mBtnDelGrp = (Button) convertView
                    .findViewById(R.id.group_del_btn);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(nickName);
        // if (mListGroups.get(position).getHead() != null) {
        // // holder.imgHead.setImageBitmap(mListGroups.get(position)
        // // .getmPhoto());
        // } else {
        // holder.imgHead.setImageResource(R.drawable.userhead);
        // }
        if (mIsDel) {
            holder.mBtnDelGrp.setVisibility(View.VISIBLE);
            holder.mBtnDelGrp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mDelListener
                            .onDelGroup(mListGroups.get(position).getUuid());
                }
            });
        }
        else {
            holder.mBtnDelGrp.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setDelable(boolean isDel) {
        mIsDel = isDel;
    }

    public interface DelListener {
        public void onDelGroup(String uuid);
    }
}
