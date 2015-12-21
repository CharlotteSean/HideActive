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
import com.hideactive.adapter.MessageListAdapter;
import com.hideactive.model.Message;
import com.hideactive.model.User;
import com.hideactive.widget.RefreshViewHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class MessageFragment extends BaseFragment {

	private TextView tipsView;
	private ListView postListView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;
	private RefreshViewHolder refreshViewHolder;

	private static final int PAGE_SIZE = 10;

	private List<Message> messageList;
	private MessageListAdapter messageListAdapter;
	private int currentPageIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		// 初始化数据
		currentPageIndex = 0;
		loadMessage();
	}

	private void initView() {
		tipsView = (TextView) findViewById(R.id.tv_tips);

		postListView = (ListView) findViewById(R.id.lv_message);
		messageList = new ArrayList<Message>();
		messageListAdapter = new MessageListAdapter(getActivity(), messageList);
		postListView.setAdapter(messageListAdapter);
		postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PostDetailActivity.start(getActivity(), messageList.get(position).getPost().getObjectId());
			}
		});

		swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.refresh_header_bg));
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
						loadMessage();
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
				loadMessage();
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
	private void loadMessage() {
		BmobQuery<Message> query = new BmobQuery<Message>();
		User user = new User();
		user.setObjectId(application.getCurrentUser().getObjectId());
		query.addWhereEqualTo("toUser", new BmobPointer(user));
		query.include("fromUser,post.author");
		query.order("createdAt");
		query.setLimit(PAGE_SIZE);
		query.setSkip(PAGE_SIZE * currentPageIndex);
		query.findObjects(getActivity(), new FindListener<Message>() {
			@Override
			public void onSuccess(List<Message> object) {
				Log.e("loadMessage", "object: " + object.size());
				// 若是起始页，则删除列表
				if (currentPageIndex == 0) {
					messageList.clear();
					if (object == null || object.size() == 0) {
						tipsView.setText("暂无评论");
						return;
					} else {
						tipsView.setVisibility(View.GONE);
					}
				}
				if (object != null && object.size() != 0) {
					currentPageIndex++;
					messageList.addAll(object);
					messageListAdapter.notifyDataSetChanged();
				}
				refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_FINISHED);
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_FINISHED);
				swipeRefreshLayout.setRefreshCompleted();
				swipeRefreshLayout.setLoadMoreCompleted();
			}

			@Override
			public void onError(int code, String msg) {
				tipsView.setText("暂无评论");
			}
		});
	}

}
