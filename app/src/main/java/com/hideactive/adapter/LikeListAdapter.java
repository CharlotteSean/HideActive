package com.hideactive.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.model.Comment;
import com.hideactive.model.User;
import com.hideactive.util.DateUtil;
import com.hideactive.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class LikeListAdapter extends BaseAdapter {

	private Context context;
	private List<User> list;
	private LayoutInflater inflater;

	public LikeListAdapter(Context context, List<User> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_post_like, null);
		}
		ImageView userLogo = ViewHolder.get(convertView, R.id.user_logo);
		TextView userName = ViewHolder.get(convertView, R.id.user_name);

        if (list.get(position).getLogo() != null) {
            ImageLoader.getInstance().displayImage(list.get(position).getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        } else {
			userLogo.setImageResource(R.mipmap.user_logo_default);
		}
		userName.setText(list.get(position).getNickname());

		return convertView;
	}

}