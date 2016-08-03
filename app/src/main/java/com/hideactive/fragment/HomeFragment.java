package com.hideactive.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.activity.PostDetailActivity;
import com.hideactive.adapter.BaseLoadMoreAdapter;
import com.hideactive.adapter.HomePageAdapter;
import com.hideactive.model.Post;
import com.hideactive.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class HomeFragment extends BaseFragment {

	private TextView tipsView;
	private RecyclerView postListView;
	private SwipeRefreshLayout swipeRefreshLayout;

	private static final int PAGE_SIZE = 10;

	private List<Post>  postList;
	private HomePageAdapter homePageAdapter;
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

		postListView = (RecyclerView) findViewById(R.id.lv_post);
        postListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        postListView.setHasFixedSize(true);
        postListView.setItemAnimator(new DefaultItemAnimator());

		postList = new ArrayList<>();
		homePageAdapter = new HomePageAdapter(postListView, getActivity(), postList);
		postListView.setAdapter(homePageAdapter);
		homePageAdapter.setOnItemClickListener(new HomePageAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(HomePageAdapter.LoadMoreViewHolder viewHolder, int position) {
				PostDetailActivity.start(getActivity(), postList.get(position).getObjectId());
			}
		});

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.blue_dark);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				currentPageIndex = 0;
				loadPost();
			}
		});

		homePageAdapter.setOnLoadMoreListener(new BaseLoadMoreAdapter.OnLoadMoreListener() {
			@Override
			public void loadMore() {
				loadPost();
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
				if (object != null && object.size() != 0) {
					if (currentPageIndex == 0) {
						postList = homePageAdapter.resetData(object);
					} else {
						postList = homePageAdapter.setLoadMore(object);
					}
                    currentPageIndex++;
					tipsView.setVisibility(View.GONE);
				} else {
					if (currentPageIndex == 0) {
						postList = homePageAdapter.resetData(null);
						tipsView.setText("还没帖子，赶紧发布吧！");
					} else {
						homePageAdapter.setLoadNoMore();
					}
				}
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtil.showShort("查询失败！" + msg);
			}
		});
	}

}
