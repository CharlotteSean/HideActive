package com.hideactive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.githang.viewpagerindicator.IconPagerAdapter;
import com.githang.viewpagerindicator.IconTabPageIndicator;
import com.hideactive.R;
import com.hideactive.fragment.AllPostFragment;
import com.hideactive.fragment.BaseFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.UserFragment;
import com.hideactive.util.ActivityCollector;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.TabButtonView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseFragmentActivity {

    private ViewPager mViewPager;
    private IconTabPageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.activity_main);
		
		initView();
	}

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (IconTabPageIndicator) findViewById(R.id.indicator);
        List<BaseFragment> fragments = initFragments();
        FragmentAdapter adapter = new FragmentAdapter(fragments, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
    }

    private List<BaseFragment> initFragments() {
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();

        AllPostFragment userFragment = new AllPostFragment();
        userFragment.setTitle(getResources().getString(R.string.home));
        userFragment.setIconId(R.drawable.tab_home_selector);
        fragments.add(userFragment);

        MessageFragment noteFragment = new MessageFragment();
        noteFragment.setTitle(getResources().getString(R.string.message));
        noteFragment.setIconId(R.drawable.tab_message_selector);
        fragments.add(noteFragment);

        UserFragment contactFragment = new UserFragment();
        contactFragment.setTitle(getResources().getString(R.string.me));
        contactFragment.setIconId(R.drawable.tab_me_selector);
        fragments.add(contactFragment);

        return fragments;
    }

    class FragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        private List<BaseFragment> mFragments;

        public FragmentAdapter(List<BaseFragment> fragments, FragmentManager fm) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getIconResId(int index) {
            return mFragments.get(index).getIconId();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getTitle();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                openActivity(new Intent(MainActivity.this, CreatePostActivity.class));
                break;
			case R.id.action_user_info:
				ToastUtil.showShort("action_user_info");
				break;
			case R.id.action_settings:
				ToastUtil.showShort("action_settings");
				break;
            default:
                break;
        }
        return true;
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (firstTime + 2000 > System.currentTimeMillis()) {
            ActivityCollector.finishAll();
            super.onBackPressed();
        } else {
            ToastUtil.showShort("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }
	
}
