package com.hideactive.adapter;

import java.util.List;

import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.ViewHolder;
import com.hideactive.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

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
		ImageButton postComment = ViewHolder.get(convertView, R.id.post_comment);
		final ImageButton postLike = ViewHolder.get(convertView, R.id.post_like);
		TextView postCommentNum = ViewHolder.get(convertView, R.id.post_comment_num);
		final TextView postLikeNum = ViewHolder.get(convertView, R.id.post_like_num);

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
		postCommentNum.setText(String.valueOf(list.get(position).getCommentNum()));
		postLikeNum.setText(String.valueOf(list.get(position).getLikeNum()));

		postLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = BmobUser.getCurrentUser(context, User.class);
				// 若是自己发布，则不可点赞
				if (user.getObjectId().equals(list.get(position).getAuthor().getObjectId())) {
					return;
				}
				Post post = new Post();
				post.setObjectId(list.get(position).getObjectId());
				// 将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
				BmobRelation relation = new BmobRelation();
				// 将当前用户添加到多对多关联中
				relation.add(user);
				// 多对多关联指向`post`的`likes`字段
				post.setLikes(relation);
				// 同时将喜欢人数+1
				post.increment("likeNum", 1);
				post.update(context, new UpdateListener() {
					@Override
					public void onSuccess() {
						Log.e("postLike", "onSuccess");
						postLike.setImageResource(R.mipmap.like_selected);
						postLikeNum.setText(String.valueOf(list.get(position).getLikeNum() + 1));
					}

					@Override
					public void onFailure(int i, String s) {

					}
				});
			}
		});

		return convertView;
	}
	
}