package com.hideactive.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseFragmentActivity {

    private SlidingMenu menu;
    private CircleImageView userLogoView;
    private TextView userNameView;

    private HomeFragment homeFragment;
    private MessageFragment messageFragment;
    private SettingFragment settingFragment;
    private Fragment[] fragments;

    private int currentIndex;
    private int menuIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initSlidingMenu();
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
                menu.toggle();
            }
        });

        Button topBarRightBtn = (Button) findViewById(R.id.btn_top_bar_right);
        topBarRightBtn.setVisibility(View.VISIBLE);
        Drawable drawableRight= getResources().getDrawable(R.mipmap.top_bar_edit);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        topBarRightBtn.setCompoundDrawables(null, null, drawableRight, null);
        topBarRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });

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

    /**
     * 刷新SlidingMenu
     */
    private void refreshSlidingMenu() {
        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        }
        userNameView.setText(user.getNickname());
    }

    /**
     * 初始化侧滑菜单
     */
    private void initSlidingMenu() {
        menu = new SlidingMenu(this);
        // 设置菜单模式左滑
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        // 设置阴影宽度
//        menu.setShadowWidthRes(R.dimen.sliding_shadow);
        // 设置阴影图片
//        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑出时主页面显示的剩余宽度
        menu.setBehindOffsetRes(R.dimen.sliding_offset_res);
        // 设置菜单是否渐变
        menu.setFadeEnabled(false);
        // 设置渐变效果的值
//        menu.setFadeDegree(0.35f);
        // 使SlidingMenu附加在Activity
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 为侧滑菜单设置布局
        menu.setMenu(R.layout.layout_sliding_menu);

        // 用户信息初始化
        userLogoView = (CircleImageView) menu.findViewById(R.id.sliding_menu_avatar);
        userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
            }
        });
        userNameView = (TextView) findViewById(R.id.sliding_menu_nick);

        // tab选项初始化
        View homeMenuTab = menu.findViewById(R.id.sliding_menu_home);
        View messageMenuTab = menu.findViewById(R.id.sliding_menu_message);
        View settingMenuTab = menu.findViewById(R.id.sliding_menu_setting);
        homeMenuTab.setOnClickListener(tabClickListener);
        messageMenuTab.setOnClickListener(tabClickListener);
        settingMenuTab.setOnClickListener(tabClickListener);
    }

    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sliding_menu_home:
                    menuIndex = 0;
                    break;
                case R.id.sliding_menu_message:
                    menuIndex = 1;
                    break;
                case R.id.sliding_menu_setting:
                    menuIndex = 2;
                    break;
            }
            // 关闭菜单
            menu.toggle();
            showFragment(menuIndex);
        }
    };

    /**
     * 显示对应的页面
     * @param index
     */
    private void showFragment(int index) {
        if (index == currentIndex) {
            // 要显示的页面是当前页面，则不做处理
            return;
        }
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.fragment_container, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentIndex = index;
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.toggle();
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
