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
import com.hideactive.adapter.LikeListAdapter;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.db.LikesDB;
import com.hideactive.dialog.ImageDetailDialog;
import com.hideactive.model.Comment;
import com.hideactive.model.Like;
import com.hideactive.model.Message;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.PushUtil;
import com.hideactive.util.TimeUtil;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.EmoticonsTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostDetailActivity extends BaseActivity {

    private static int PAGE_SIZE = 5;

    private ImageView userLogo;
    private TextView userName;
    private TextView postDate;
    private EmoticonsTextView postContent;
    private ImageView postImage;
    private Button commentNumBtn;
    private Button likeNumBtn;
    private ListView commentListView;
    private ListView likeListView;
    private Button tipsBtn;

    private View commentEare;
    private EditText commentContentView;
    private Button commentSendBtn;
    private Button sendLikeBTn;

    private LikesDB likesDB;

    private int currentCommentNum;
    private int currentLikeNum;

    private Post mPost;
    private List<Comment> commentList;
    private CommentListAdapter commentListAdapter;
    private List<User> likeList;
    private LikeListAdapter likeListAdapter;

    private int currentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        likesDB = new LikesDB(this, application.getCurrentUser().getObjectId());
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
        likesDB.closedDB();
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
        postContent = (EmoticonsTextView) findViewById(R.id.post_content);
        postImage = (ImageView) findViewById(R.id.post_image);

        commentNumBtn = (Button) findViewById(R.id.post_comment_num);
        likeNumBtn = (Button) findViewById(R.id.post_like_num);
        commentNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commentNumBtn.isSelected()) {
                    // 切换选中状态
                    commentNumBtn.setSelected(true);
                    likeNumBtn.setSelected(false);
                    // 显示
                    commentListView.setVisibility(View.VISIBLE);
                    likeListView.setVisibility(View.GONE);
                    tipsBtn.setText("正在加载...");
                    commentEare.setVisibility(View.VISIBLE);
                    sendLikeBTn.setVisibility(View.GONE);
                    // 加载数据
                    currentPageIndex = 0;
                    loadComments();
                }
            }
        });
        likeNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!likeNumBtn.isSelected()) {
                    // 切换选中状态
                    commentNumBtn.setSelected(false);
                    likeNumBtn.setSelected(true);
                    // 显示
                    commentListView.setVisibility(View.GONE);
                    likeListView.setVisibility(View.VISIBLE);
                    tipsBtn.setText("正在加载...");
                    commentEare.setVisibility(View.GONE);
                    sendLikeBTn.setVisibility(View.VISIBLE);
                    // 加载数据
                    currentPageIndex = 0;
                    loadLikes();
                }
            }
        });

        commentListView = (ListView) findViewById(R.id.lv_post_comment);
        commentList = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(this, commentList);
        commentListView.setAdapter(commentListAdapter);

        likeListView = (ListView) findViewById(R.id.lv_post_like);
        likeList = new ArrayList<User>();
        likeListAdapter = new LikeListAdapter(this, likeList);
        likeListView.setAdapter(likeListAdapter);

        tipsBtn = (Button) findViewById(R.id.post_detail_tips);
        tipsBtn.setText("正在加载");
        tipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentNumBtn.isSelected()) {
                    loadComments();
                } else {
                    loadLikes();
                }
            }
        });

        commentEare = findViewById(R.id.post_detail_comment);
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
        sendLikeBTn = (Button) findViewById(R.id.post_detail_like);
        sendLikeBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLike();
            }
        });
    }

    /**
     * 刷新界面数据
     */
    private void refreshView() {
        if (mPost.getAuthor().getLogo() != null) {
            ImageLoader.getInstance().displayImage(mPost.getAuthor().getLogo().getUrl(),
                    userLogo, ImageLoaderOptions.getOptions());
        }
        String nickname = TextUtils.isEmpty(mPost.getAuthor().getNickname())
                ? mPost.getAuthor().getUsername()
                : mPost.getAuthor().getNickname();
        userName.setText(nickname);
        String createAt = mPost.getCreatedAt();
        postDate.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(createAt, TimeUtil.FORMAT_DATE_TIME_SECOND)));
        if (!TextUtils.isEmpty(mPost.getContent())) {
            postContent.setVisibility(View.VISIBLE);
            postContent.setMText(mPost.getContent());
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

        Like like = new Like(application.getCurrentUser().getObjectId(), mPost.getObjectId());
        if (likesDB.isLike(like)) {
            sendLikeBTn.setSelected(true);
            sendLikeBTn.setText(getString(R.string.unlike));
        } else {
            sendLikeBTn.setSelected(false);
            sendLikeBTn.setText(getString(R.string.like));
        }
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
                    if (object == null || object.size() == 0) {
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
        // 还原输入栏
        hideSoftInputView();
        commentContentView.setText("");
        commentContentView.clearFocus();
        // 发送
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
                        // 发送消息
                        sendMessage(comment.getContent());
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

    /**
     * 加载点赞
     */
    private void loadLikes() {
        BmobQuery<User> query = new BmobQuery<User>();
        Post post = new Post();
        post.setObjectId(mPost.getObjectId());
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.order("createdAt");
        query.setLimit(PAGE_SIZE);
        query.setSkip(PAGE_SIZE * currentPageIndex);
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                // 若是起始页，则删除列表
                if (currentPageIndex == 0) {
                    likeList.clear();
                    if (object == null || object.size() == 0) {
                        tipsBtn.setText("暂无点赞");
                        tipsBtn.setClickable(false);
                        return;
                    }
                }
                currentPageIndex++;
                likeList.addAll(object);
                likeListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(likeListView);

                if (likeList.size() < PAGE_SIZE * currentPageIndex) {
                    tipsBtn.setText("无更多点赞");
                    tipsBtn.setClickable(false);
                } else {
                    tipsBtn.setText("加载更多");
                    tipsBtn.setClickable(true);
                }
            }

            @Override
            public void onError(int code, String msg) {
                tipsBtn.setText("暂无点赞");
            }
        });
    }

    /**
     * 点赞/取消点赞
     */
    private void sendLike() {
        final User user = application.getCurrentUser();
        // 若是自己发布，则不可点
        if (user.getObjectId().equals(mPost.getAuthor().getObjectId())) {
            return;
        }
        sendLikeBTn.setClickable(false);
        Post post = new Post();
        post.setObjectId(mPost.getObjectId());
        BmobRelation relation = new BmobRelation();
        final Like like = new Like(user.getObjectId(), mPost.getObjectId());
        if (sendLikeBTn.isSelected()) {
            // 取消点赞
            relation.remove(user);
            post.setLikes(relation);
            post.increment("likeNum", -1);
            post.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    currentLikeNum--;
                    sendLikeBTn.setSelected(false);
                    sendLikeBTn.setText(getString(R.string.like));
                    likesDB.delete(like);
                    likeNumBtn.setText(getString(R.string.like) + " " + currentLikeNum);
                    if (likeList.size() < PAGE_SIZE * currentPageIndex) {
                        for (int i = 0; i < likeList.size(); i++) {
                            if (likeList.get(i).getObjectId().equalsIgnoreCase(user.getObjectId())) {
                                likeList.remove(i);
                                break;
                            }
                        }
                        likeListAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(likeListView);
                    }
                    sendLikeBTn.setClickable(true);
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
            post.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    currentLikeNum++;
                    sendLikeBTn.setSelected(true);
                    sendLikeBTn.setText(getString(R.string.unlike));
                    likesDB.addOne(like);
                    likeNumBtn.setText(getString(R.string.like) + " " + currentLikeNum);
                    if (likeList.size() < PAGE_SIZE * currentPageIndex) {
                        likeList.add(user);
                        likeListAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(likeListView);
                    } else {
                        loadLikes();
                    }
                    sendLikeBTn.setClickable(true);
                }
                @Override
                public void onFailure(int i, String s) {
                }
            });
        }
    }

    /**
     * 发送一条消息
     * @param content
     */
    private void sendMessage(final String content) {
        Message message = new Message();
        message.setContent(content);
        message.setFromUser(application.getCurrentUser());
        message.setToUser(mPost.getAuthor());
        message.setPost(mPost);
        message.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // 发送推送
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", application.getCurrentUser().getNickname());
                    jsonObject.put("content", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PushUtil.push2User(PostDetailActivity.this, mPost.getAuthor().getObjectId(), jsonObject.toString());
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

}