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
import com.hideactive.activity.UserInfoActivity;
import com.hideactive.model.Comment;
import com.hideactive.util.TimeUtil;
import com.hideactive.util.ViewHolder;

import java.util.List;

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
//            ImageLoader.getInstance().displayImage(list.get(position).getUser().getLogo().getUrl(),
//                    userLogo, ImageLoaderOptions.getOptions());
        } else {
			userLogo.setImageResource(R.mipmap.user_logo_default);
		}
		userLogo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserInfoActivity.start(context, list.get(position).getUser().getObjectId());
			}
		});
		String nickname = TextUtils.isEmpty(list.get(position).getUser().getNickname())
				? list.get(position).getUser().getUsername()
				: list.get(position).getUser().getNickname();
		userName.setText(nickname);
		String createAt = list.get(position).getCreatedAt();
		postDate.setText(TimeUtil.getMessageTime(TimeUtil.stringToLong(createAt, TimeUtil.FORMAT_DATE_TIME_SECOND)));
		if (!TextUtils.isEmpty(list.get(position).getContent())) {
            postContent.setVisibility(View.VISIBLE);
            postContent.setText(list.get(position).getContent());
        } else {
            postContent.setVisibility(View.GONE);
        }

		return convertView;
	}

}