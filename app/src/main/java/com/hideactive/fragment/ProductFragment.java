package com.hideactive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hideactive.R;
import com.hideactive.widget.RefreshViewHeaderHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends BaseFragment {
	
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
		swipeRefreshLayout.setHeaderViewBackgroundColor(0xff888888);
        refreshViewHeaderHolder = new RefreshViewHeaderHolder(getActivity());
		swipeRefreshLayout.setHeaderView(refreshViewHeaderHolder.getHeaderView());
		swipeRefreshLayout.setTargetScrollWithLayout(true);
		swipeRefreshLayout
				.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

					@Override
					public void onRefresh() {
						refreshViewHeaderHolder.textView.setText("正在刷新");
						refreshViewHeaderHolder.imageView.setVisibility(View.GONE);
						refreshViewHeaderHolder.progressBar.setVisibility(View.VISIBLE);
//                        loadProductList();
					}

					@Override
					public void onPullDistance(int distance) {
						if (distance > 0 && distance < 50) {
							swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.blue));
						}
						if (distance > 50 && distance < 100) {
							swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.blue_dark));
						}
						if (distance > 100 && distance < 150) {
							swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.grey_light));
						}
						if (distance > 150 && distance < 200) {
							swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.grey));
						}
						if (distance > 200) {
							swipeRefreshLayout.setHeaderViewBackgroundColor(0xff888888);
						}
					}

					@Override
					public void onPullEnable(boolean enable) {
						refreshViewHeaderHolder.textView.setText(enable ? "松开刷新" : "下拉刷新");
						refreshViewHeaderHolder.imageView.setVisibility(View.VISIBLE);
						refreshViewHeaderHolder.imageView.setRotation(enable ? 180 : 0);
						refreshViewHeaderHolder.progressBar.setVisibility(View.GONE);
					}
				});
	}

}
