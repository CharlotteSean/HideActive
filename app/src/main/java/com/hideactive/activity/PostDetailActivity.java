package com.hideactive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.adapter.CommentListAdapter;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Comment;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.DateUtil;
import com.hideactive.util.ToastUtil;
import com.hideactive.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostDetailActivity extends BaseActivity {

    private static int PAGE_SIZE = 5;

    private ImageView userLogo;
    private TextView userName;
    private TextView postDate;
    private TextView postContent;
    private ImageView postImage;
    private Button commentNumBtn;
    private Button likeNumBtn;
    private ListView commentListView;
    private Button tipsBtn;

    private EditText commentContentView;
    private Button commentSendBtn;

    private int currentCommentNum;
    private int currentLikeNum;

    private Post mPost;
    private List<Comment> commentList;
    private CommentListAdapter commentListAdapter;

    private int currentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initView();
        initPost();
    }

    /**
     * 提供打开方法，解耦
     * @param context
     * @param postId
     */
    public static void start(Context context, String postId) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.detail));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        topBarLeftBtn.setText(getResources().getString(R.string.back));
        topBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        userLogo = (ImageView) findViewById(R.id.user_logo);
        userName = (TextView) findViewById(R.id.user_name);
        postDate = (TextView) findViewById(R.id.post_date);
        postContent = (TextView) findViewById(R.id.post_content);
        postImage = (ImageView) findViewById(R.id.post_image);

        commentNumBtn = (Button) findViewById(R.id.post_comment_num);
        likeNumBtn = (Button) findViewById(R.id.post_like_num);

        commentListView = (ListView) findViewById(R.id.lv_post_comment);
        commentList = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(this, commentList);
        commentListView.setAdapter(commentListAdapter);

        tipsBtn = (Button) findViewById(R.id.post_detail_tips);
        tipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComments();
            }
        });

        commentContentView = (EditText) findViewById(R.id.post_detail_comment_content);
        commentSendBtn = (Button) findViewById(R.id.post_detail_comment_send);
        commentSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = commentContentView.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort("请先输入内容");
                    return;
                }
                sendComment(content);
            }
        });
    }

    private void refreshView() {
        if (mPost.getAuthor().getLogo() != null) {
            ImageLoader.getInstance().displayImage(mPost.getAuthor().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        }
        userName.setText(mPost.getAuthor().getNickname());
        String createAt = mPost.getCreatedAt();
        postDate.setText(DateUtil.getDiffTime(DateUtil.string2Date(createAt)));
        if (!TextUtils.isEmpty(mPost.getContent())) {
            postContent.setVisibility(View.VISIBLE);
            postContent.setText(mPost.getContent());
        } else {
            postContent.setVisibility(View.GONE);
        }
        if (mPost.getImage() != null) {
            postImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(mPost.getImage().getUrl(),
                    postImage, ImageLoaderOptions.getOptions());
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDetailDialog imageDetailDialog = new ImageDetailDialog(PostDetailActivity.this, mPost.getImage().getUrl());
                    imageDetailDialog.show();
                }
            });
        } else {
            postImage.setVisibility(View.GONE);
        }

        currentCommentNum = mPost.getCommentNum().intValue();
        currentLikeNum = mPost.getLikeNum().intValue();
        commentNumBtn.setText(getString(R.string.comment) + " " + mPost.getCommentNum().toString());
        commentNumBtn.setSelected(true);
        likeNumBtn.setText(getString(R.string.like) + " " + mPost.getLikeNum().toString());
    }

    /***
     * 动态设置listview的高度 item 总布局必须是linearLayout
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 初始化post数据
     */
    private void initPost() {
        BmobQuery<Post> query = new BmobQuery<Post>();
        String postId = getIntent().getStringExtra("postId");
        if (TextUtils.isEmpty(postId)) {
            ToastUtil.showShort("获取数据失败");
            return;
        }
        query.include("author");
        query.getObject(this, postId, new GetListener<Post>() {
            @Override
            public void onSuccess(Post object) {
                mPost = object;
                // 获取到数据后，刷新页面
                refreshView();
                // 加载评论
                currentPageIndex = 0;
                loadComments();
            }

            @Override
            public void onFailure(int code, String arg0) {
                ToastUtil.showShort("获取数据失败");
            }
        });
    }

    /**
     * 加载评论
     */
    private void loadComments() {
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        Post post = new Post();
        post.setObjectId(mPost.getObjectId());
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.include("user");
        query.order("createdAt");
        query.setLimit(PAGE_SIZE);
        query.setSkip(PAGE_SIZE * currentPageIndex);
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> object) {
                // 若是起始页，则删除列表
                if (currentPageIndex == 0) {
                    commentList.clear();
                    if (object != null && object.size() == 0) {
                        tipsBtn.setText("暂无评论");
                        tipsBtn.setClickable(false);
                        return;
                    }
                }
                currentPageIndex++;
                commentList.addAll(object);
                commentListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(commentListView);

                if (commentList.size() < PAGE_SIZE * currentPageIndex) {
                    tipsBtn.setText("无更多评论");
                    tipsBtn.setClickable(false);
                } else {
                    tipsBtn.setText("加载更多");
                    tipsBtn.setClickable(true);
                }
            }
            @Override
            public void onError(int code, String msg) {
                tipsBtn.setText("暂无评论");
            }
        });
    }

    /**
     * 发表评论
     * @param content
     */
    private void sendComment(String content) {
        hideSoftInputView();
        commentContentView.setText("");
        commentContentView.clearFocus();

        User user = application.getCurrentUser();
        Post post = new Post();
        post.setObjectId(mPost.getObjectId());
        final Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                currentCommentNum++;
                Post post = new Post();
                post.setCommentNum(currentCommentNum);
                post.update(PostDetailActivity.this, mPost.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        commentNumBtn.setText(getString(R.string.comment) + " " + currentCommentNum);
                        if (commentList.size() < PAGE_SIZE * currentPageIndex) {
                            commentList.add(comment);
                            commentListAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(commentListView);
                        } else {
                            loadComments();
                        }
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        ToastUtil.showShort("评论失败：" + msg);
                    }
                });
            }
            @Override
            public void onFailure(int code, String msg) {
                ToastUtil.showShort("评论失败：" + msg);
            }
        });
    }

}