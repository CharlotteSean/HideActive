package com.hideactive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hideactive.R;
import com.hideactive.model.Message;

import java.util.List;

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
//		ImageView userLogo = ViewHolder.get(convertView, R.id.user_logo);
//		TextView userName = ViewHolder.get(convertView, R.id.user_name);
//		TextView messageDate = ViewHolder.get(convertView, R.id.message_date);
//		TextView messageContent = ViewHolder.get(convertView, R.id.message_content);
//		ImageView postImage = ViewHolder.get(convertView, R.id.post_image);
//		TextView postUserName = ViewHolder.get(convertView, R.id.post_user_name);
//		EmoticonsTextView postContent = ViewHolder.get(convertView, R.id.post_content);
//
//        if (list.get(position).getFromUser().getLogo() != null) {
//            ImageLoader.getInstance().displayImage(list.get(position).getFromUser().getLogo().getUrl(),
//                    userLogo, ImageLoaderOptions.getOptions());
//        } else {
//			userLogo.setImageResource(R.mipmap.user_logo_default);
//		}
//		userLogo.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				UserInfoActivity.start(context, list.get(position).getFromUser().getObjectId());
//			}
//		});
//		String nickname = TextUtils.isEmpty(list.get(position).getFromUser().getNickname())
//				? list.get(position).getFromUser().getUsername()
//				: list.get(position).getFromUser().getNickname();
//		userName.setText(nickname);
//		String createAt = list.get(position).getCreatedAt();
//		messageDate.setText(TimeUtil.getMessageTime(TimeUtil.stringToLong(createAt, TimeUtil.FORMAT_DATE_TIME_SECOND)));
//		messageContent.setText(list.get(position).getContent());
//		if (list.get(position).getPost().getImage() != null) {
//			ImageLoader.getInstance().displayImage(list.get(position).getPost().getImage().getUrl(),
//					postImage, ImageLoaderOptions.getOptions());
//		} else {
//			ImageLoader.getInstance().displayImage(list.get(position).getPost().getAuthor().getLogo().getUrl(),
//					postImage, ImageLoaderOptions.getOptions());
//		}
//		postUserName.setText("@" + list.get(position).getPost().getAuthor().getNickname());
//		postContent.setMText(list.get(position).getPost().getContent());

		return convertView;
	}

}