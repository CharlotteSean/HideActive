package com.hideactive.adapter;

import java.util.List;

import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Post;
import com.hideactive.util.ViewHolder;
import com.hideactive.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PostListAdapter extends BaseAdapter {

	private Context context;
	private List<Post> list;
	private LayoutInflater inflater;

	public PostListAdapter(Context context, List<Post> list) {
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
			convertView = inflater.inflate(R.layout.list_item_post, null);
		}
		ImageView userLogo = ViewHolder.get(convertView, R.id.user_logo);
		TextView userName = ViewHolder.get(convertView, R.id.user_name);
		TextView postContent = ViewHolder.get(convertView, R.id.post_content);
		ImageView postImage = ViewHolder.get(convertView, R.id.post_image);
		Button postComment = ViewHolder.get(convertView, R.id.post_comment);
		Button postLike = ViewHolder.get(convertView, R.id.post_like);

        if (list.get(position).getAuthor().getLogo() != null) {
            ImageLoader.getInstance().displayImage(list.get(position).getAuthor().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        }
		userName.setText(list.get(position).getAuthor().getNickname());
        if (!TextUtils.isEmpty(list.get(position).getContent())) {
            postContent.setVisibility(View.VISIBLE);
            postContent.setText(list.get(position).getContent());
        } else {
            postContent.setVisibility(View.GONE);
        }

		if (list.get(position).getImage() != null) {
			postImage.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(list.get(position).getImage().getUrl(),
                    postImage, ImageLoaderOptions.getOptions());
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDetailDialog imageDetailDialog = new ImageDetailDialog(context, list.get(position).getImage().getUrl());
                    imageDetailDialog.show();
                }
            });
		} else {
			postImage.setVisibility(View.GONE);
		}
		postComment.setText(list.get(position).getCommentNum() + "");
		postLike.setText(list.get(position).getLikeNum() + "");

		return convertView;
	}
	
}