package com.hideactive.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.fragment.HomeFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.SettingFragment;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseFragmentActivity {

    private CircleImageView userLogoView;
    private DrawerLayout drawer;
    private View[] mTabs;
    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private SettingFragment settingFragment;
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshSlidingMenu();
    }

    private void initView(){
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.app_name));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        Drawable drawableLeft= getResources().getDrawable(R.mipmap.top_bar_menu);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        topBarLeftBtn.setCompoundDrawables(drawableLeft, null, null, null);
        topBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        Button topBarRightBtn = (Button) findViewById(R.id.btn_top_bar_right);
        topBarRightBtn.setVisibility(View.VISIBLE);
        Drawable drawableRight= getResources().getDrawable(R.drawable.top_bar_create_selector);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        topBarRightBtn.setCompoundDrawables(null, null, drawableRight, null);
        topBarRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });

        userLogoView = (CircleImageView) findViewById(R.id.sliding_menu_avatar);
        userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
//                actionBar_img.setImageResource(R.mipmap.actionbar_up);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                actionBar_img.setImageResource(R.mipmap.actionbar_todo);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = drawer.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float leftScale = 1 - 0.3f * scale;
                float rightScale = 0.8f + scale * 0.2f;

                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent,
                        mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            }
        });

        mTabs = new View[3];
        mTabs[0] = findViewById(R.id.sliding_menu_home);
        mTabs[1] = findViewById(R.id.sliding_menu_message);
        mTabs[2] = findViewById(R.id.sliding_menu_setting);
        mTabs[0].setSelected(true);
    }

    private void initTab(){
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        settingFragment = new SettingFragment();
        fragments = new Fragment[] {homeFragment, messageFragment, settingFragment };
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).
                add(R.id.fragment_container, messageFragment).hide(messageFragment).show(homeFragment).commit();
    }

    /**
     * 刷新SlidingMenu
     */
    private void refreshSlidingMenu() {
        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        }
        TextView userNameView = (TextView) findViewById(R.id.sliding_menu_nick);
        userNameView.setText(application.getCurrentUser().getNickname());
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
