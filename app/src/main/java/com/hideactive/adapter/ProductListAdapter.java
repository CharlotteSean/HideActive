package com.hideactive.adapter;

import java.util.List;

import com.hideactive.model.User;
import com.hideactive.util.ViewHolder;
import com.hideactive.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductListAdapter extends BaseAdapter {

	private Context context;
	private List<User> list;
	private LayoutInflater inflater;

	public ProductListAdapter(Context context, List<User> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_product, null);
		} 
		TextView title = ViewHolder.get(convertView, R.id.title);
//		title.setText(list.get(position).getName());

		return convertView;
	}
	
}
