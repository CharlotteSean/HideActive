package com.hideactive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hideactive.R;
import com.hideactive.adapter.PostListAdapter;
import com.hideactive.model.Post;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.RefreshViewHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class AllPostFragment extends BaseFragment {
	
	private ListView postListView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;

    private RefreshViewHolder refreshViewHeaderHolder;
    private static final int PAGE_SIZE = 10;

    private List<Post>  postList;
    private PostListAdapter postListAdapter;
    private int currentPageIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_all_post, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
        // 初始化数据
        currentPageIndex = 0;
	}
	
	private void initView() {
        postListView = (ListView) findViewById(R.id.lv_post);
        postList = new ArrayList<Post>();
		postListAdapter = new PostListAdapter(getActivity(), postList);
        postListView.setAdapter(postListAdapter);

	}


}
