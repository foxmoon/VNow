package com.nyist.vnow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.struct.Colleage;

public class VNowColleageAdapter extends BaseAdapter implements SectionIndexer{
	private List<Colleage> mListColleages;
	private Context mContext;

	final static class ViewHolder {
		ImageView imgHead;
		TextView txtName;
		TextView txtCatalog;
		TextView txtMsg;
		LinearLayout llayoutContent;
	}

	public VNowColleageAdapter(Context context, List<Colleage> list) {
		mContext = context;
		this.mListColleages = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<Colleage> list){
		this.mListColleages = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListColleages.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		final Colleage mContent = mListColleages.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.avc_list_contacts_item, null);
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

			convertView.setTag(viewHolder);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.txtCatalog.setVisibility(View.VISIBLE);
			viewHolder.llayoutContent.setPadding(0, 5, 0, 5);
			viewHolder.txtCatalog.setText(mContent.getmSortLetters());
		}else{
			viewHolder.txtCatalog.setVisibility(View.GONE);
		}
	
		viewHolder.txtName.setText(mListColleages.get(position).getG_name());

//		if (mListColleages.get(position).getG_head() != null) {
////			viewHolder.imgHead.setImageBitmap(mListColleages.get(position)
////					.getG_head());
//		} else {
//			viewHolder.imgHead.setImageResource(R.drawable.userhead);
//		}
		return convertView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return mListColleages.get(position).getmSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
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
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
