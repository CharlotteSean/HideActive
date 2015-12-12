package com.hideactive.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListView;

import com.hideactive.R;
import com.hideactive.util.ViewUtil;
import com.hideactive.widget.RefreshViewHeaderHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class AllPostFragment extends BaseFragment {
	
	private ListView productView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;

    private RefreshViewHeaderHolder refreshViewHeaderHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_product, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//        loadProductList();
	}
	
	private void initView() {
		productView = (ListView) findViewById(R.id.lv_product);
//		productList = new ArrayList<Product>();
//		productListAdapter = new ProductListAdapter(getActivity(), productList);
//		productView.setAdapter(productListAdapter);

		swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.actionbar_bg));
        refreshViewHeaderHolder = new RefreshViewHeaderHolder(getActivity());
		swipeRefreshLayout.setHeaderView(refreshViewHeaderHolder.getHeaderView());
		swipeRefreshLayout.setTargetScrollWithLayout(true);
		swipeRefreshLayout
				.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

					@Override
					public void onRefresh() {
						refreshViewHeaderHolder.textView.setText("正在刷新...");
						refreshViewHeaderHolder.imageView.setVisibility(View.GONE);
						refreshViewHeaderHolder.progressBar.setVisibility(View.VISIBLE);
//                        loadProductList();
                        swipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshViewHeaderHolder.imageView.setRotation(0);
                                refreshViewHeaderHolder.imageView.setVisibility(View.VISIBLE);
                                refreshViewHeaderHolder.progressBar.setVisibility(View.GONE);
                                refreshViewHeaderHolder.imageView.setImageResource(R.mipmap.refresh_successed);
                                swipeRefreshLayout.setRefreshCompleted();
                            }
                        }, 3000);
					}

					@Override
					public void onPullDistance(int distance) {
						// 0 ~ 192
//                        Log.e("", "distance : " + ViewUtil.px2dip(getActivity(), distance));
					}

					@Override
					public void onPullEnable(boolean enable) {
						refreshViewHeaderHolder.textView.setText(enable ? "松开刷新" : "下拉刷新");
                        refreshViewHeaderHolder.imageView.setImageResource(R.mipmap.indicator_arrow);
						refreshViewHeaderHolder.imageView.setVisibility(View.VISIBLE);
						refreshViewHeaderHolder.imageView.setRotation(enable ? 180 : 0);
                        refreshViewHeaderHolder.progressBar.setVisibility(View.GONE);
					}
				});
	}

}
