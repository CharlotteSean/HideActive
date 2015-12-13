package com.hideactive.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.adapter.PostListAdapter;
import com.hideactive.model.Post;
import com.hideactive.util.ActivityCollector;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.RefreshViewHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends BaseActivity {

	private ListView postListView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;
	private RefreshViewHolder refreshViewHolder;

	private static final int PAGE_SIZE = 10;

	private List<Post>  postList;
	private PostListAdapter postListAdapter;
	private int currentPageIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.activity_main);
		
		initView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// 初始化数据
		currentPageIndex = 0;
		loadPost();
	}

	private void initView() {
		Button actionBarLeftBtn = (Button) findViewById(R.id.btn_action_bar_left);
		Button actionBarRightBtn = (Button) findViewById(R.id.btn_action_bar_right);
		TextView actionBarTitle = (TextView) findViewById(R.id.tv_action_bar_title);
		actionBarLeftBtn.setVisibility(View.GONE);
		Drawable img_left = getResources().getDrawable(R.mipmap.actionbar_page);
		img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
		actionBarRightBtn.setCompoundDrawables(img_left, null, null, null);
		actionBarRightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
			}
		});
		actionBarTitle.setText(getResources().getString(R.string.app_name));

		postListView = (ListView) findViewById(R.id.lv_post);
		postList = new ArrayList<Post>();
		postListAdapter = new PostListAdapter(this, postList);
		postListView.setAdapter(postListAdapter);

		swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.actionbar_bg));
		refreshViewHolder = new RefreshViewHolder(this);
		swipeRefreshLayout.setHeaderView(refreshViewHolder.getHeaderView());
		swipeRefreshLayout.setFooterView(refreshViewHolder.getFooterView());
		swipeRefreshLayout.setTargetScrollWithLayout(true);
		swipeRefreshLayout
				.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

					@Override
					public void onRefresh() {
						refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_ING);
						currentPageIndex = 0;
						loadPost();
					}

					@Override
					public void onPullDistance(int distance) {
						// 0 ~ 192PX, 0 ~ 64DP
//                        Log.e("", "distance : " + ViewUtil.px2dip(getActivity(), distance));
					}

					@Override
					public void onPullEnable(boolean enable) {
						if (enable) {
							refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_CAN);
						} else {
							refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_CAN_NOT);
						}
                    }
                });

        swipeRefreshLayout.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_ING);
				loadPost();
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {
				if (enable) {
					refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_CAN);
				} else {
					refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_CAN_NOT);
				}
            }
        });
	}

	/**
	 * 分页加载数据
	 */
	private void loadPost() {
		BmobQuery<Post> query = new BmobQuery<Post>();
		query.order("-updatedAt");
		query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
		query.setLimit(PAGE_SIZE);
		query.setSkip(PAGE_SIZE * currentPageIndex);
        // 设置先从网络获取，若没则取缓存
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findObjects(this, new FindListener<Post>() {
			@Override
			public void onSuccess(List<Post> object) {
				// 若是起始页，则删除列表
				if (currentPageIndex == 0) {
					postList.clear();
				}
				currentPageIndex ++;
				postList.addAll(object);
				postListAdapter.notifyDataSetChanged();

				refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_FINISHED);
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_FINISHED);
				swipeRefreshLayout.setRefreshCompleted();
				swipeRefreshLayout.setLoadMoreCompleted();
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtil.showShort("查询失败！");
			}
		});
	}
	
}
