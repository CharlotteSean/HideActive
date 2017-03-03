package com.hideactive.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.activity.PostDetailActivity;
import com.hideactive.adapter.BaseRVAdapter;
import com.hideactive.adapter.HomePageAdapter;
import com.hideactive.adapter.ViewHolder;
import com.hideactive.model.Post;
import com.hideactive.util.ToastUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HomeFragment extends BaseFragment {

	private TextView tipsView;
	private RecyclerView postListView;
	private SwipeRefreshLayout swipeRefreshLayout;

	private static final int PAGE_SIZE = 10;

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
	}

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh(100);
    }

    private void initView() {
		tipsView = (TextView) findViewById(R.id.tv_tips);

		postListView = (RecyclerView) findViewById(R.id.lv_post);
        postListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        postListView.setHasFixedSize(true);
        postListView.setItemAnimator(new DefaultItemAnimator());

		homePageAdapter = new HomePageAdapter(getActivity());
        homePageAdapter.setLMOpened(R.layout.layout_load_more, new BaseRVAdapter.OnLoadMoreListener() {
            @Override
            public boolean onLoadMore() {
                if (swipeRefreshLayout.isRefreshing()) {
                    return false;
                }
                loadPost();
                return true;
            }
        });
        homePageAdapter.setOnItemClickListener(new BaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder viewHolder, int position) {
                PostDetailActivity.start(getActivity(), homePageAdapter.getData().get(position).getObjectId());
            }
        });
		postListView.setAdapter(homePageAdapter);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
                if (homePageAdapter.isLoading()) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
				currentPageIndex = 0;
				loadPost();
			}
		});
	}

    /**
     * 刷新
     *
     * @param delayMillis
     */
	private void autoRefresh(long delayMillis) {
		swipeRefreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
                currentPageIndex = 0;
                loadPost();
			}
		}, delayMillis);
	}

	/**
	 * 分页加载数据
	 */
	private void loadPost() {
		BmobQuery<Post> query = new BmobQuery<>();
		query.order("-createdAt");
		query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
		query.setLimit(PAGE_SIZE);
		query.setSkip(PAGE_SIZE * currentPageIndex);
		query.findObjects(new FindListener<Post>() {
			@Override
			public void done(List<Post> list, BmobException e) {
				if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
					swipeRefreshLayout.setRefreshing(false);
				}
				if (e == null) {
					if (list != null && list.size() != 0) {
						if (currentPageIndex == 0) {
							homePageAdapter.resetData(list);
						} else {
                            homePageAdapter.addData2Last(list);
						}
                        homePageAdapter.setLMNormal();
						currentPageIndex++;
						tipsView.setVisibility(View.GONE);
					} else {
						homePageAdapter.setLMNoMore();
						tipsView.setText("还没帖子，赶紧发布吧！");
					}
				} else {
					ToastUtil.showShort("查询失败！" + e.getMessage());
                    homePageAdapter.setLMFailure();
				}
			}
		});
	}

}
