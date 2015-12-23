package com.hideactive.adapter;

import java.util.List;

import com.hideactive.SessionApplication;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.db.LikesDB;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Like;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.DateUtil;
import com.hideactive.util.ToastUtil;
import com.hideactive.util.ViewHolder;
import com.hideactive.R;
import com.hideactive.widget.EmoticonsTextView;
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
		TextView postDate = ViewHolder.get(convertView, R.id.post_date);
		EmoticonsTextView postContent = ViewHolder.get(convertView, R.id.post_content);
		ImageView postImage = ViewHolder.get(convertView, R.id.post_image);
		ImageView postComment = ViewHolder.get(convertView, R.id.post_comment);
		final ImageView postLike = ViewHolder.get(convertView, R.id.post_like);
		TextView postCommentNum = ViewHolder.get(convertView, R.id.post_comment_num);
		final TextView postLikeNum = ViewHolder.get(convertView, R.id.post_like_num);

        if (list.get(position).getAuthor().getLogo() != null) {
            ImageLoader.getInstance().displayImage(list.get(position).getAuthor().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        } else {
			userLogo.setImageResource(R.mipmap.user_logo_default);
		}
		userName.setText(list.get(position).getAuthor().getNickname());
		String createAt = list.get(position).getCreatedAt();
		postDate.setText(DateUtil.getDiffTime(DateUtil.string2Date(createAt)));
        if (!TextUtils.isEmpty(list.get(position).getContent())) {
            postContent.setVisibility(View.VISIBLE);
//            postContent.setText(list.get(position).getContent());
			postContent.setMText(list.get(position).getContent());
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
		postCommentNum.setText(list.get(position).getCommentNum().toString());
		postLikeNum.setText(list.get(position).getLikeNum().toString());

		String uId = SessionApplication.getInstance().getCurrentUser().getObjectId();
		Like like = new Like(uId, list.get(position).getObjectId());
		LikesDB likesDB = new LikesDB(context, uId);
		postLike.setSelected(likesDB.isLike(like));
		likesDB.closedDB();
		postLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = BmobUser.getCurrentUser(context, User.class);
				// 若是自己发布，则不可点
				if (user.getObjectId().equals(list.get(position).getAuthor().getObjectId())) {
					return;
				}
				postLike.setClickable(false);
				Post post = new Post();
				post.setObjectId(list.get(position).getObjectId());
				BmobRelation relation = new BmobRelation();
				final Like like = new Like(user.getObjectId(), list.get(position).getObjectId());
				final LikesDB likesDB = new LikesDB(context, user.getObjectId());
				if (postLike.isSelected()) {
					// 取消点赞
					relation.remove(user);
					post.setLikes(relation);
					post.increment("likeNum", -1);
					post.update(context, new UpdateListener() {
						@Override
						public void onSuccess() {
							postLike.setSelected(false);
							postLikeNum.setText(String.valueOf(Integer.parseInt(postLikeNum.getText().toString()) - 1));
							likesDB.delete(like);
							postLike.setClickable(true);
						}

						@Override
						public void onFailure(int arg0, String arg1) {
						}
					});
					return;
				} else {
					// 点赞
					relation.add(user);
					post.setLikes(relation);
					post.increment("likeNum", 1);
					post.update(context, new UpdateListener() {
						@Override
						public void onSuccess() {
							postLike.setSelected(true);
							postLikeNum.setText(String.valueOf(Integer.parseInt(postLikeNum.getText().toString()) + 1));
							likesDB.addOne(like);
							postLike.setClickable(true);
						}

						@Override
						public void onFailure(int i, String s) {
						}
					});
					likesDB.closedDB();
				}
			}
		});

		return convertView;
	}

}