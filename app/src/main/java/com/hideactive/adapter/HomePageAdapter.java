package com.hideactive.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hideactive.R;
import com.hideactive.SessionApplication;
import com.hideactive.activity.UserInfoActivity;
import com.hideactive.db.LikesDB;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Like;
import com.hideactive.model.Post;
import com.hideactive.util.TimeUtil;
import com.hideactive.widget.EmoticonsTextView;

import java.util.List;

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
    private LikesDB likesDB;

    public HomePageAdapter(RecyclerView recyclerView, Context context, List<Post> list) {
        super(recyclerView);
        this.context = context;
        this.list = list;
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
            Uri uri = Uri.parse(post.getAuthor().getLogo().getUrl());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolder.userLogoView.getController())
                    .build();
            viewHolder.userLogoView.setController(controller);
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
            viewHolder.postContentView.setText(post.getContent());
        } else {
            viewHolder.postContentView.setVisibility(View.GONE);
        }

        if (post.getImage() != null) {
            viewHolder.postImageView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(post.getImage().getUrl());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolder.userLogoView.getController())
                    .build();
            viewHolder.userLogoView.setController(controller);

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
    }

    public interface OnItemClickListener {
        void onItemClick(LoadMoreViewHolder viewHolder, int position);
    }

    /**
     * 设置点击事件
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 普通布局
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public SimpleDraweeView userLogoView;
        public TextView userNameView;
        public TextView postDateView;
        public TextView postContentView;
        public ImageView postImageView;
        public ImageButton postCommentView;
        public ImageButton postLikeView;
        public TextView postCommentNumView;
        public TextView postLikeNumView;

        public LoadMoreViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);

            userLogoView = (SimpleDraweeView) view.findViewById(R.id.user_logo);
            userNameView = (TextView) view.findViewById(R.id.user_name);
            postDateView = (TextView) view.findViewById(R.id.post_date);
            postContentView = (TextView) view.findViewById(R.id.post_content);
            postImageView = (ImageView) view.findViewById(R.id.post_image);
            postCommentView = (ImageButton) view.findViewById(R.id.post_comment);
            postLikeView = (ImageButton) view.findViewById(R.id.post_like);
            postCommentNumView = (TextView) view.findViewById(R.id.post_comment_num);
            postLikeNumView = (TextView) view.findViewById(R.id.post_like_num);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, getLayoutPosition());
            }
        }
    }

}
