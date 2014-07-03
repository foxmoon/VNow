package com.nyist.vnow.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nyist.vnow.R;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.utils.LogTag;

public class ConfColleagueSelectAdapter extends BaseAdapter implements SectionIndexer, OnClickListener {
    private List<Colleage> mListColleages;
    private Context mContext;
    private ArrayList<String> mSelectedMembers;

    final static class ViewHolder {
        ImageView imgHead;
        TextView txtName;
        TextView txtCatalog;
        TextView txtMsg;
        LinearLayout llayoutContent;
        CheckBox mMemberCheckBox;
    }

    public ConfColleagueSelectAdapter(Context context, List<Colleage> list, ArrayList<String> selectedPhotos) {
        mContext = context;
        mListColleages = list;
        mSelectedMembers = selectedPhotos;
    }

    public void updateListView(List<Colleage> list) {
        this.mListColleages = list;
        notifyDataSetChanged();
    }

    // public void updateSelectedMembers(ArrayList<String> selectedPhotos) {
    // mSelectedMembers = selectedPhotos;
    // notifyDataSetChanged();
    // }
    @Override
    public int getCount() {
        return mListColleages != null ? mListColleages.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mListColleages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.avc_list_member_item, null);
            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.img_contact_head);
            viewHolder.txtName = (TextView) convertView
                    .findViewById(R.id.txt_contact_name);
            viewHolder.txtCatalog = (TextView) convertView
                    .findViewById(R.id.txt_contact_catalog);
            viewHolder.txtMsg = (TextView) convertView
                    .findViewById(R.id.txt_contact_msg);
            viewHolder.llayoutContent = (LinearLayout) convertView
                    .findViewById(R.id.llayout_item_content);
            viewHolder.mMemberCheckBox = (CheckBox) convertView.findViewById(R.id.mMemberCheckbox);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindView(position, viewHolder);
        return convertView;
    }

    /**
     * @param position
     * @param viewHolder
     */
    private void bindView(final int position, ViewHolder viewHolder) {
        final Colleage colleage = mListColleages.get(position);
        viewHolder.txtName.setText(colleage.getG_name());
        viewHolder.mMemberCheckBox.setTag(position);
        // 注意这里是设置点击事件而不是check事件
        viewHolder.mMemberCheckBox.setOnClickListener(this);
        if (isInSelectedDataList(colleage.getG_phone()))
        {
            viewHolder.mMemberCheckBox.setChecked(true);
        }
        else
        {
            viewHolder.mMemberCheckBox.setChecked(false);
        }
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.txtCatalog.setVisibility(View.VISIBLE);
            viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
            viewHolder.txtCatalog.setText(colleage.getmSortLetters());
        }
        else {
            viewHolder.txtCatalog.setVisibility(View.GONE);
        }
    }

    private boolean isInSelectedDataList(
            String selectedString)
    {
        for (int i = 0; i < mSelectedMembers.size(); i++)
        {
            if (mSelectedMembers.get(i).equals(
                    selectedString))
            {
                return true;
            }
        }
        return false;
    }

    public interface OnItemCheckedChangeListener
    {
        public void OnCheckedChange(CompoundButton buttonView, int position, String path, boolean isChecked);
    }

    private OnItemCheckedChangeListener mOnCheckedChangeListener;

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onCheckedChangeListener)
    {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public int getSectionForPosition(int position) {
        return mListColleages.get(position).getmSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mListColleages.get(i).getmSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        }
        else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof CheckBox)
        {
            CheckBox checkBox = (CheckBox) view;
            int position = (Integer) checkBox.getTag();
            if (mListColleages != null
                    && mOnCheckedChangeListener != null
                    && position < mListColleages.size())
            {
                Colleage colleage = mListColleages.get(position);
                mOnCheckedChangeListener.OnCheckedChange(checkBox, position, colleage.getG_phone(), checkBox.isChecked());
            }
        }
    }
}
