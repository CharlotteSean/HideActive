package com.hideactive.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hideactive.R;
import com.hideactive.fragment.HomeFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.UserFragment;
import com.hideactive.util.ActivityCollector;
import com.hideactive.util.ToastUtil;

public class MainActivity extends BaseFragmentActivity {

    private ImageButton[] mTabs;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private UserFragment userFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTab();
    }


    private void initView(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);

        mTabs = new ImageButton[3];
        mTabs[0] = (ImageButton) findViewById(R.id.tab_home);
        mTabs[1] = (ImageButton) findViewById(R.id.tab_message);
        mTabs[2] = (ImageButton) findViewById(R.id.tab_user);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
    }

    private void initTab(){
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        userFragment = new UserFragment();
        fragments = new Fragment[] {homeFragment, messageFragment, userFragment };
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).
                add(R.id.fragment_container, messageFragment).hide(messageFragment).show(homeFragment).commit();
    }

    /**
     * tab点击事件
     * @param view
     */
    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.tab_home:
                index = 0;
                break;
            case R.id.tab_message:
                index = 1;
                break;
            case R.id.tab_user:
                index = 2;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        //把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
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
