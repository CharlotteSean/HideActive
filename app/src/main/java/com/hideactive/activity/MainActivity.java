package com.hideactive.activity;

import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.AllPostFragment;
import com.hideactive.fragment.TodoFragment;
import com.hideactive.util.ActivityCollector;
import com.hideactive.widget.PagerSlidingTabStrip;
import com.hideactive.R;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private AllPostFragment productFragment;
	private TodoFragment todoFragment;
	private MessageFragment messageFragment;
	
	private PagerSlidingTabStrip tabs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setIcon(R.mipmap.actionbar_up);
		
		initView();
	}
	
	private void initView() {
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();
	}
	
	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置tab标题字体粗细
		tabs.setTypeface(null, Typeface.BOLD);
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(getResources().getColor(R.color.red_dark));
		// 设置选中Tab文字的颜色
		tabs.setSelectedTextColor(getResources().getColor(R.color.red_dark));
	}
	
	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "产品", "待办", "消息" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (productFragment == null) {
					productFragment = new AllPostFragment();
				}
				return productFragment;
			case 1:
				if (todoFragment == null) {
					todoFragment = new TodoFragment();
				}
				return todoFragment;
			case 2:
				if (messageFragment == null) {
					messageFragment = new MessageFragment();
				}
				return messageFragment;
			default:
				return null;
			}
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
	
	private void CreateMenu(Menu menu) {
        MenuItem createPostItem = menu.add(0, 0, 0, "创建");
		createPostItem.setIcon(R.mipmap.actionbar_page);
		createPostItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
	
	private boolean MenuChoice(MenuItem item) {
        switch (item.getItemId()) {  
        case 0:  
            startActivity(new Intent(this, CreatePostActivity.class));
            return true;  
        }  
        return false;  
    }
	
}
