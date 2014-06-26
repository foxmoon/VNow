package com.nyist.vnow.utils;

import com.nyist.vnow.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 对话框帮助类
 * 
 * @author harry
 * @version Creat on 2014-6-25上午10:37:45
 */
public class DialogUtil {
    /**
     * ListDialog的回调接口
     */
    public interface OnListDialogItemClickListener {
        public void onListDialogItemClick(int which);
    }

    public static final int TYPE_BLUE = 1;
    public static final int TYPE_RED = 2;
    public static final int TYPE_CANCEL = 3;
    public static final int TYPE_WHITE = 4;

    /**
     * 创建ListDialog
     * 
     * @param Context
     *            context
     * @param Integer
     *            itemsId
     * @param int[] types
     * @param Boolean
     *            cancelable
     * @param OnListDialogItemClickListener
     *            listener
     * @author chenjh
     */
    public static Dialog createListDialog(final Activity context,
            final String title,
            final int itemsId, final int[] types, final boolean cancelable,
            final OnListDialogItemClickListener listener) {
        if (context.isFinishing()) {
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        final Dialog dlg = new Dialog(context, R.style.Theme_Dialog);
        Window win = dlg.getWindow();
        win.setGravity(Gravity.CENTER);
        final View view = inflater.inflate(R.layout.dialog_list_layout, null);
        final int cFullFillWidth = (int) DensityUtil.dip2px(context, 300);
        view.setMinimumWidth(cFullFillWidth);
        final ListView listView = (ListView) view
                .findViewById(R.id.lv_dialog_content);
        String[] itemsArray = context.getResources().getStringArray(itemsId);
        final ListDialogAdapter adapter = new ListDialogAdapter(context,
                itemsArray, types);
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                if (listener != null) {
                    listener.onListDialogItemClick(position);
                }
                dlg.dismiss();
                listView.requestFocus();
            }
        });
        dlg.setCanceledOnTouchOutside(cancelable);
        dlg.setCancelable(cancelable);
        dlg.setContentView(view);
        dlg.show();
        return dlg;
    }

    /**
     * @param String
     *            [] items
     * @param int[] types
     * @author chenjh
     * 
     */
    static class ListDialogAdapter extends BaseAdapter {
        private Activity context;
        private String[] items;
        private int[] types;
        private LayoutInflater inflater;

        public ListDialogAdapter(final Activity cxt, String[] items, int[] types) {
            this.context = cxt;
            this.items = items;
            this.types = types;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (items == null) {
                return 0;
            }
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            if (items != null && position < getCount()) {
                return items[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.dialog_layout_list_item, null);
                holder.tvItem = (TextView) convertView
                        .findViewById(R.id.tv_dialog_item);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            StringUtil.setViewText(holder.tvItem, items[position]);
            switch (types[position]) {
                case TYPE_BLUE:
                    holder.tvItem
                            .setBackgroundResource(R.drawable.btn_dialog_blue_selector);
                    holder.tvItem.setTextColor(Color.WHITE);
                    break;
                case TYPE_CANCEL:
                case TYPE_WHITE:
                    holder.tvItem
                            .setBackgroundResource(R.drawable.btn_dialog_white_selector);
                    holder.tvItem.setTextColor(Color.BLACK);
                    break;
                default:
                    break;
            }
            return convertView;
        }

        static class ViewHolder {
            TextView tvItem;
        }
    }

    /**
     * dialog 左边的按钮的监听
     */
    public interface OnLeftClickListener {
        public void onLeftClick();
    }

    /**
     * dialog 右边的按钮的监听
     */
    public interface OnRightClickListener {
        public void onRightClick();
    }

    /**
     * dialog 中间的按钮的监听
     */
    public interface OnCenterClickListener {
        public void onCenterClick();
    }

    /**
     * 得到自定义的progressDialog
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Dialog createLoadingDialog(Context context,
            String loadingText, boolean cancelable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.avc_dlg_progress, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        TextView loadingTextView = (TextView) v.findViewById(R.id.txt_progress_msg);
        loadingTextView.setText("" + loadingText);
        Dialog loadingDialog = new Dialog(context, R.style.navSettingDialogTheme);// 创建自定义样式dialog
        loadingDialog.setCancelable(cancelable);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }
}
