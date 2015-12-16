package com.hideactive.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hideactive.R;
import com.hideactive.util.ToastUtil;

public class MessageFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	/**
	 * 监听当前fragment是否可见，第一次创建时不调用
	 * @param hidden
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getActivity().getActionBar().setTitle(R.string.message);
		}
	}

	public void initView() {
		getActivity().getActionBar().setTitle(R.string.message);

	}

}
