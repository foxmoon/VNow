package com.nyist.vnow.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.DES;

public class VNowRctContactAdapter extends BaseAdapter {
    private List<VNowRctContact> list = null;
    private Context mContext;
    private long mPriorTime;
    private String filterNum;

    public VNowRctContactAdapter(Context mContext, List<VNowRctContact> list) {
        this.mContext = mContext;
        this.list = list;
    }

    final static class ViewHolder {
        ImageView imgHead;
        TextView txtName;
        TextView txtPhone;
        TextView txtCatalog;
        TextView txtMsg;
        LinearLayout llayoutContent;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * 
     * @param list
     */
    public void updateListView(List<VNowRctContact> list, String filter) {
        this.list = list;
        this.filterNum = filter;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final VNowRctContact rctItem = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.avc_list_contacts_item, null);
            viewHolder.imgHead = (ImageView) convertView
                    .findViewById(R.id.img_contact_head);
            viewHolder.txtName = (TextView) convertView
                    .findViewById(R.id.txt_contact_name);
            viewHolder.txtPhone = (TextView) convertView
                    .findViewById(R.id.txt_contact_phone);
            viewHolder.txtCatalog = (TextView) convertView
                    .findViewById(R.id.txt_contact_catalog);
            viewHolder.txtMsg = (TextView) convertView
                    .findViewById(R.id.txt_contact_msg);
            viewHolder.llayoutContent = (LinearLayout) convertView
                    .findViewById(R.id.llayout_item_content);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtCatalog.setVisibility(View.GONE);
        if (null != filterNum) {
            viewHolder.txtName.setText(Html.fromHtml(rctItem.getmStrContactName()
                    .replace(filterNum, "<FONT COLOR='#237ED3'>" + filterNum + "</FONT>")));
        }
        else {
            viewHolder.txtName.setText(rctItem.getmStrContactName());
        }
        DES des = new DES();
        viewHolder.txtPhone.setText(des.decrypt(rctItem.getmStrConPhone()));
        if (rctItem.ismIsCallIn()) {
            viewHolder.txtMsg.setBackgroundResource(R.drawable.img_imcoming);
        }
        else {
            viewHolder.txtMsg.setBackgroundResource(R.drawable.img_outgoing);
        }
        if (position == 0) {
            mPriorTime = 0;
        }
        else {
            mPriorTime = list.get(position - 1).getmCallTime();
        }
        // format message time
        String result = "";
        int diff = CommonUtil.daysDiffCompareToToday(rctItem.getmCallTime());
        if (diff == 0) {
            if (CommonUtil.isSameTimeInterval(mPriorTime, rctItem.getmCallTime())) {
                viewHolder.txtCatalog.setVisibility(View.GONE);
            }
            else {
                viewHolder.txtCatalog.setVisibility(View.VISIBLE);
                viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
                String region = CommonUtil.currentDayTimeRegionFormat(rctItem.getmCallTime());
                result = region + " "
                        + CommonUtil.currentDayTimeFormat(rctItem.getmCallTime());
            }
        }
        else if (diff == 1) {
            if (CommonUtil.isSameTimeInterval(mPriorTime, rctItem.getmCallTime())) {
                viewHolder.txtCatalog.setVisibility(View.GONE);
            }
            else {
                viewHolder.txtCatalog.setVisibility(View.VISIBLE);
                viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
                String region = CommonUtil.currentDayTimeRegionFormat(rctItem.getmCallTime());
                result = "昨天  "
                        + region + " "
                        + CommonUtil.currentDayTimeFormat(rctItem.getmCallTime());
            }
        }
        else {
            if (CommonUtil.isSameWeekCompareToToday(rctItem.getmCallTime())) {
                if (CommonUtil
                        .isSameTimeInterval(mPriorTime, rctItem.getmCallTime())) {
                    viewHolder.txtCatalog.setVisibility(View.GONE);
                }
                else {
                    viewHolder.txtCatalog.setVisibility(View.VISIBLE);
                    viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
                    result = CommonUtil.thisWeekTimeFormat(rctItem.getmCallTime());
                }
            }
            else {
                if (CommonUtil
                        .isSameTimeInterval(mPriorTime, rctItem.getmCallTime())) {
                    viewHolder.txtCatalog.setVisibility(View.GONE);
                }
                else {
                    viewHolder.txtCatalog.setVisibility(View.VISIBLE);
                    viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
                    result = CommonUtil.monthTimeFormat(rctItem.getmCallTime());
                }
            }
        }
        viewHolder.txtCatalog.setText(result);
        return convertView;
    }
}
