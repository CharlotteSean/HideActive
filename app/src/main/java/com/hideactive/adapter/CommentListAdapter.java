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
import com.hideactive.model.Comment;
import com.hideactive.model.Like;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.DateUtil;
import com.hideactive.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

public class CommentListAdapter extends BaseAdapter {

	private Context context;
	private List<Comment> list;
	private LayoutInflater inflater;

	public CommentListAdapter(Context context, List<Comment> list) {
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
			convertView = inflater.inflate(R.layout.list_item_post_comment, null);
		}
		ImageView userLogo = ViewHolder.get(convertView, R.id.user_logo);
		TextView userName = ViewHolder.get(convertView, R.id.user_name);
		TextView postDate = ViewHolder.get(convertView, R.id.comment_date);
		TextView postContent = ViewHolder.get(convertView, R.id.comment_content);

        if (list.get(position).getUser().getLogo() != null) {
            ImageLoader.getInstance().displayImage(list.get(position).getUser().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        } else {
			userLogo.setImageResource(R.mipmap.user_logo_default);
		}
		userName.setText(list.get(position).getUser().getNickname());
		String createAt = list.get(position).getCreatedAt();
		postDate.setText(DateUtil.getDiffTime(DateUtil.string2Date(createAt)));
        if (!TextUtils.isEmpty(list.get(position).getContent())) {
            postContent.setVisibility(View.VISIBLE);
            postContent.setText(list.get(position).getContent());
        } else {
            postContent.setVisibility(View.GONE);
        }

		return convertView;
	}

}