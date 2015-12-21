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
import com.hideactive.SessionApplication;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.db.LikesDB;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Like;
import com.hideactive.model.Message;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.DateUtil;
import com.hideactive.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

public class MessageListAdapter extends BaseAdapter {

	private Context context;
	private List<Message> list;
	private LayoutInflater inflater;

	public MessageListAdapter(Context context, List<Message> list) {
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
			convertView = inflater.inflate(R.layout.list_item_message, null);
		}
		ImageView userLogo = ViewHolder.get(convertView, R.id.user_logo);
		TextView userName = ViewHolder.get(convertView, R.id.user_name);
		TextView messageDate = ViewHolder.get(convertView, R.id.message_date);
		TextView messageContent = ViewHolder.get(convertView, R.id.message_content);
		ImageView postImage = ViewHolder.get(convertView, R.id.post_image);
		TextView postUserName = ViewHolder.get(convertView, R.id.post_user_name);
		TextView postContent = ViewHolder.get(convertView, R.id.post_content);

        if (list.get(position).getFromUser().getLogo() != null) {
            ImageLoader.getInstance().displayImage(list.get(position).getFromUser().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        } else {
			userLogo.setImageResource(R.mipmap.user_logo_default);
		}
		userName.setText(list.get(position).getFromUser().getNickname());
		String createAt = list.get(position).getCreatedAt();
		messageDate.setText(DateUtil.getNormalTime(DateUtil.string2Date(createAt)));
		messageContent.setText(list.get(position).getContent());
		if (list.get(position).getPost().getImage() != null) {
			ImageLoader.getInstance().displayImage(list.get(position).getPost().getImage().getUrl(),
					postImage, ImageLoaderOptions.getOptions());
		} else {
			ImageLoader.getInstance().displayImage(list.get(position).getPost().getAuthor().getLogo().getUrl(),
					postImage, ImageLoaderOptions.getOptions());
		}
		postUserName.setText("@" + list.get(position).getPost().getAuthor().getNickname());
		postContent.setText(list.get(position).getPost().getContent());

		return convertView;
	}

}