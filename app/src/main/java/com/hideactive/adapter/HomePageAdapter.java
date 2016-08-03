package com.hideactive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.SessionApplication;
import com.hideactive.activity.UserInfoActivity;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.db.LikesDB;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Like;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.TimeUtil;
import com.hideactive.widget.EmoticonsTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zhouchunjie on 2016/07/08.
 */
public class HomePageAdapter extends BaseLoadMoreAdapter<Post> {

    // 单击事件
    private OnItemClickListener onItemClickListener;
    // 长按事件
    private OnItemLongClickListener onItemLongClickListener;

    private Context context;
    private List<Post> list;
    private LayoutInflater inflater;
    private LikesDB likesDB;

    public HomePageAdapter(RecyclerView recyclerView, Context context, List<Post> list) {
        super(recyclerView);
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        likesDB = SessionApplication.getInstance().getLikesDB();
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_post, parent, false);
        return new LoadMoreViewHolder(view);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, final int position, final Post post) {
        final LoadMoreViewHolder viewHolder = (LoadMoreViewHolder)holder;

        if (post.getAuthor().getLogo() != null) {
            ImageLoader.getInstance().displayImage(post.getAuthor().getLogo().getUrl(),
                    viewHolder.userLogoView, ImageLoaderOptions.getOptions());
        } else {
            viewHolder.userLogoView.setImageResource(R.mipmap.user_logo_default);
        }
        viewHolder.userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.start(context, post.getAuthor().getObjectId());
            }
        });
        String nickname = TextUtils.isEmpty(post.getAuthor().getNickname())
                ? post.getAuthor().getUsername()
                : post.getAuthor().getNickname();
        viewHolder.userNameView.setText(nickname);
        String createAt = post.getCreatedAt();
        viewHolder.postDateView.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(createAt, TimeUtil.FORMAT_DATE_TIME_SECOND)));
        if (!TextUtils.isEmpty(post.getContent())) {
            viewHolder.postContentView.setVisibility(View.VISIBLE);
            viewHolder.postContentView.setMText(post.getContent());
        } else {
            viewHolder.postContentView.setVisibility(View.GONE);
        }

        if (post.getImage() != null) {
            viewHolder.postImageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(post.getImage().getUrl(),
                    viewHolder.postImageView, ImageLoaderOptions.getOptions());
            viewHolder.postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDetailDialog imageDetailDialog = new ImageDetailDialog(context, post.getImage().getUrl());
                    imageDetailDialog.show();
                }
            });
        } else {
            viewHolder.postImageView.setVisibility(View.GONE);
        }
        viewHolder.postCommentNumView.setText(post.getCommentNum().toString());
        viewHolder.postLikeNumView.setText(post.getLikeNum().toString());

        String uId = SessionApplication.getInstance().getCurrentUser().getObjectId();
        Like like = new Like(uId, post.getObjectId());
        viewHolder.postLikeView.setSelected(likesDB.isLike(like));
        viewHolder.postLikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(context, User.class);
                // 若是自己发布，则不可点
                if (user.getObjectId().equals(post.getAuthor().getObjectId())) {
                    return;
                }
                viewHolder.postLikeView.setClickable(false);
                Post post = new Post();
                post.setObjectId(post.getObjectId());
                BmobRelation relation = new BmobRelation();
                final Like like = new Like(user.getObjectId(), post.getObjectId());
                if (viewHolder.postLikeView.isSelected()) {
                    // 取消点赞
                    relation.remove(user);
                    post.setLikes(relation);
                    post.increment("likeNum", -1);
                    post.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            viewHolder.postLikeView.setSelected(false);
                            viewHolder.postLikeNumView.setText(String.valueOf(Integer.parseInt(viewHolder.postLikeNumView.getText().toString()) - 1));
                            likesDB.delete(like);
                            viewHolder.postLikeView.setClickable(true);
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
                            viewHolder.postLikeView.setSelected(true);
                            viewHolder.postLikeNumView.setText(String.valueOf(Integer.parseInt(viewHolder.postLikeNumView.getText().toString()) + 1));
                            likesDB.addOne(like);
                            viewHolder.postLikeView.setClickable(true);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                        }
                    });
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(LoadMoreViewHolder viewHolder, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(LoadMoreViewHolder viewHolder, int position);
    }

    /**
     * 设置点击事件
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置长按点击事件
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 普通布局
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView userLogoView;
        public TextView userNameView;
        public TextView postDateView;
        public EmoticonsTextView postContentView;
        public ImageView postImageView;
        public ImageView postCommentView;
        public ImageView postLikeView;
        public TextView postCommentNumView;
        public TextView postLikeNumView;

        public LoadMoreViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            userLogoView = (ImageView) view.findViewById(R.id.user_logo);
            userNameView = (TextView) view.findViewById(R.id.user_name);
            postDateView = (TextView) view.findViewById(R.id.post_date);
            postContentView = (EmoticonsTextView) view.findViewById(R.id.post_content);
            postImageView = (ImageView) view.findViewById(R.id.post_image);
            postCommentView = (ImageView) view.findViewById(R.id.post_comment);
            postLikeView = (ImageView) view.findViewById(R.id.post_like);
            postCommentNumView = (TextView) view.findViewById(R.id.post_comment_num);
            postLikeNumView = (TextView) view.findViewById(R.id.post_like_num);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(this, getLayoutPosition());
                return true;
            }
            return false;
        }
    }

}
