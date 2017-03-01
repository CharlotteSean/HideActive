package com.hideactive.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
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
import com.hideactive.util.ViewUtil;
import com.hideactive.widget.EmoticonsTextView;

import java.util.List;

public class HomePageAdapter extends BaseRVAdapter<Post> {

    private Context context;
    private LikesDB likesDB;

    public HomePageAdapter(Context context) {
        super(context);
        this.context = context;
        likesDB = SessionApplication.getInstance().getLikesDB();
    }

    @Override
    protected ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return ViewHolder.create(R.layout.list_item_post, parent);
    }

    @Override
    protected void onBindVH(ViewHolder holder, int position, final Post post) {
        SimpleDraweeView userLogoView = holder.getView(R.id.user_logo);
        TextView userNameView = holder.getView(R.id.user_name);
        TextView postDateView = holder.getView(R.id.post_date);
        EmoticonsTextView postContentView = holder.getView(R.id.post_content);
        SimpleDraweeView postImageView = holder.getView(R.id.post_image);
        ImageButton postCommentView = holder.getView(R.id.post_comment);
        ImageButton postLikeView = holder.getView(R.id.post_like);
        TextView postCommentNumView = holder.getView(R.id.post_comment_num);
        TextView postLikeNumView = holder.getView(R.id.post_like_num);


        if (post.getAuthor().getLogo() != null) {
            Uri uri = Uri.parse(post.getAuthor().getLogo().getUrl());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(60, 60))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(userLogoView.getController())
                    .build();
            userLogoView.setController(controller);
        } else {
            userLogoView.setImageResource(R.mipmap.user_logo_default);
        }
        userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.start(context, post.getAuthor().getObjectId());
            }
        });
        String nickname = TextUtils.isEmpty(post.getAuthor().getNickname())
                ? post.getAuthor().getUsername()
                : post.getAuthor().getNickname();
        userNameView.setText(nickname);
        String createAt = post.getCreatedAt();
        postDateView.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(createAt, TimeUtil.FORMAT_DATE_TIME_SECOND)));
        if (!TextUtils.isEmpty(post.getContent())) {
            postContentView.setVisibility(View.VISIBLE);
            postContentView.setMText(post.getContent());
        } else {
            postContentView.setVisibility(View.GONE);
        }

        if (post.getImage() != null) {
            postImageView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(post.getImage().getUrl());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(postImageView.getController())
                    .build();
            postImageView.setController(controller);

            postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDetailDialog imageDetailDialog = new ImageDetailDialog(context, post.getImage().getUrl());
                    imageDetailDialog.show();
                }
            });
        } else {
            postImageView.setVisibility(View.GONE);
        }
        postCommentNumView.setText(post.getCommentNum().toString());
        postLikeNumView.setText(post.getLikeNum().toString());

        String uId = SessionApplication.getInstance().getCurrentUser().getObjectId();
        Like like = new Like(uId, post.getObjectId());
        postLikeView.setSelected(likesDB.isLike(like));
    }

    @Override
    protected void onStatusChanged(int status) {
        ViewHolder viewHolder = getLoadViewHolder();
        if (viewHolder == null) {
            return;
        }

        ProgressBar progressBar = viewHolder.getView(R.id.progress_loading);
        TextView textView = viewHolder.getView(R.id.tv_loading);
        switch (status) {
            case STATUS_NORMAL:
                textView.setText(R.string.load_completed);
                progressBar.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
                progressBar.setVisibility(View.VISIBLE);
                textView.setText(R.string.load_more);
                break;
            case STATUS_LOAD_NO_MORE:
                textView.setText(R.string.no_more);
                progressBar.setVisibility(View.GONE);
                break;
            case STATUS_LOAD_FAILURE:
                textView.setText(R.string.load_error);
                progressBar.setVisibility(View.GONE);
                break;
            default:
                textView.setText(R.string.load_completed);
                progressBar.setVisibility(View.GONE);
                break;
        }
    }
}
