package com.hideactive.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.activity.PostDetailActivity;
import com.hideactive.adapter.PostListAdapter;
import com.hideactive.model.Post;
import com.hideactive.util.ToastUtil;
import com.hideactive.util.ViewUtil;
import com.hideactive.widget.RefreshViewHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class HomeFragment extends BaseFragment {

	private TextView tipsView;
	private ListView postListView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;
	private RefreshViewHolder refreshViewHolder;

	private static final int PAGE_SIZE = 10;

	private List<Post>  postList;
	private PostListAdapter postListAdapter;
	private int currentPageIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		// 初始化数据
		currentPageIndex = 0;
		loadPost();
	}

	private void initView() {
		tipsView = (TextView) findViewById(R.id.tv_tips);

		postListView = (ListView) findViewById(R.id.lv_post);
		postList = new ArrayList<Post>();
		postListAdapter = new PostListAdapter(getActivity(), postList);
		postListView.setAdapter(postListAdapter);
		postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PostDetailActivity.start(getActivity(), postList.get(position).getObjectId());
			}
		});

		swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//		swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.refresh_header_bg));
		refreshViewHolder = new RefreshViewHolder(getActivity());
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
//							Log.e("", "enable : " + enable);
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
		query.order("-createdAt");
		query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
		query.setLimit(PAGE_SIZE);
		query.setSkip(PAGE_SIZE * currentPageIndex);
		query.findObjects(getActivity(), new FindListener<Post>() {
			@Override
			public void onSuccess(List<Post> object) {
				// 若是起始页，则删除列表
				if (currentPageIndex == 0) {
					postList.clear();
					if (object == null || object.size() == 0) {
						tipsView.setText("还没帖子，赶紧发布吧！");
						return;
					} else {
						tipsView.setVisibility(View.GONE);
					}
				}
				if (object != null && object.size() != 0) {
					currentPageIndex++;
					postList.addAll(object);
					postListAdapter.notifyDataSetChanged();
				}
				refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_FINISHED);
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_FINISHED);
				swipeRefreshLayout.setRefreshCompleted();
				swipeRefreshLayout.setLoadMoreCompleted();
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtil.showShort("查询失败！" + msg);
				tipsView.setText("还没帖子，赶紧发布吧！");
			}
		});
	}

}
