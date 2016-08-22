package com.hideactive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hideactive.R;
import com.hideactive.fragment.HomeFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.SettingFragment;
import com.hideactive.util.ToastUtil;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;

    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private SettingFragment settingFragment;
    private Fragment[] fragments;

    private int currentPageIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

        View homeMenuTab = findViewById(R.id.sliding_menu_home);
        View messageMenuTab = findViewById(R.id.sliding_menu_message);
        View settingMenuTab = findViewById(R.id.sliding_menu_setting);
        homeMenuTab.setOnClickListener(tabClickListener);
        messageMenuTab.setOnClickListener(tabClickListener);
        settingMenuTab.setOnClickListener(tabClickListener);

        // 展现首页
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        settingFragment = new SettingFragment();
        fragments = new Fragment[] {homeFragment, messageFragment, settingFragment };
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .show(homeFragment)
                .commit();
    }

    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sliding_menu_home:
                    showFragment(0);
                    break;
                case R.id.sliding_menu_message:
                    showFragment(1);
                    break;
                case R.id.sliding_menu_setting:
                    showFragment(2);
                    break;
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    };

    /**
     * 显示对应的页面
     * @param index
     */
    private void showFragment(int index) {
        if (index == currentPageIndex) {
            return;
        }
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentPageIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.fragment_container, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentPageIndex = index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_post) {
            openActivity(new Intent(MainActivity.this, CreatePostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            application.finishAll();
            super.onBackPressed();
        } else {
            ToastUtil.showShort("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }

}
