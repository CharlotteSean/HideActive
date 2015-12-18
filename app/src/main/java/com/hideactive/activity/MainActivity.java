package com.hideactive.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.fragment.HomeFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.UserFragment;
import com.hideactive.model.User;
import com.hideactive.util.ActivityCollector;
import com.hideactive.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseFragmentActivity {

    private ImageButton actionBar_img;
    private DrawerLayout drawer;
    private View[] mTabs;
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
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        actionBar_img = (ImageButton) actionBar.getCustomView().findViewById(R.id.custom_actionbar_img);
        actionBar_img.setImageResource(R.mipmap.actionbar_todo);
        actionBar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        TextView actionBar_text = (TextView) actionBar.getCustomView().findViewById(R.id.custom_actionbar_text);
        actionBar_text.setText(getResources().getString(R.string.home));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar_img.setImageResource(R.mipmap.actionbar_up);
                CircleImageView userLogoView = (CircleImageView) findViewById(R.id.sliding_menu_avatar);
                TextView userNameView = (TextView) findViewById(R.id.sliding_menu_nick);
                User user = application.getCurrentUser();
                if (user.getLogo() != null) {
                    ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                            userLogoView, ImageLoaderOptions.getOptions());
                }
                userNameView.setText(user.getNickname());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionBar_img.setImageResource(R.mipmap.actionbar_todo);
            }
        });

        Button logoutBtn = (Button) findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.logout();
            }
        });

        mTabs = new View[3];
        mTabs[0] = findViewById(R.id.sliding_menu_home);
        mTabs[1] = findViewById(R.id.sliding_menu_message);
        mTabs[2] = findViewById(R.id.sliding_menu_setting);
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
            case R.id.sliding_menu_home:
                index = 0;
                break;
            case R.id.sliding_menu_message:
                index = 1;
                break;
            case R.id.sliding_menu_setting:
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
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
        // 关闭菜单
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (firstTime + 2000 > System.currentTimeMillis()) {
            application.finishAll();
            super.onBackPressed();
        } else {
            ToastUtil.showShort("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }
	
}
