package com.nyist.vnow.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.nyist.vnow.R;
import com.nyist.vnow.struct.Colleage;

public class ConfMemberSelectAdapter extends BaseAdapter implements SectionIndexer {
    private List<Colleage> mListColleages;
    private Context mContext;
//    private ArrayList<Integer> mSelectedMembers;

    final static class ViewHolder {
        ImageView imgHead;
        TextView txtName;
        TextView txtCatalog;
        TextView txtMsg;
        LinearLayout llayoutContent;
        CheckBox mMemberCheckBox;
    }

    public ConfMemberSelectAdapter(Context context, List<Colleage> list) {
        mContext = context;
        this.mListColleages = list;
//        mSelectedMembers = selectedMembers;
    }

    public void updateListView(List<Colleage> list) {
        this.mListColleages = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListColleages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        final Colleage mContent = mListColleages.get(position);
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
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.txtCatalog.setVisibility(View.VISIBLE);
            viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
            viewHolder.txtCatalog.setText(mContent.getmSortLetters());
        }
        else {
            viewHolder.txtCatalog.setVisibility(View.GONE);
        }
        viewHolder.txtName.setText(mListColleages.get(position).getG_name());
        viewHolder.mMemberCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mOnCheckedChangeListener.OnCheckedChange(buttonView, position, isChecked);
            }
        });
        return convertView;
    }

    public interface OnItemCheckedChangeListener
    {
        public void OnCheckedChange(CompoundButton buttonView, int position, boolean isChecked);
    }

    private OnItemCheckedChangeListener mOnCheckedChangeListener;

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onCheckedChangeListener)
    {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

//    private boolean isInSelectedDataList(
//            int position)
//    {
//        for (int i = 0; i < mSelectedMembers.size(); i++)
//        {
//            if (mSelectedMembers.get(i).equals(
//                    position))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

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
}
