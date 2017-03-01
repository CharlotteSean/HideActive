package com.hideactive.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hideactive.R;
import com.hideactive.fragment.HomeFragment;
import com.hideactive.fragment.MessageFragment;
import com.hideactive.fragment.SettingFragment;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
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

        fragments = new Fragment[] {new HomeFragment(), new MessageFragment(), new SettingFragment() };
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .show(fragments[0])
                .commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ColorStateList csl = ContextCompat.getColorStateList(this, R.drawable.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        navigationView.getMenu().getItem(0).setChecked(true);

        SimpleDraweeView logoView = (SimpleDraweeView) navigationView.getHeaderView(0).findViewById(R.id.iv_logo);
        TextView sickNameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_sick_name);

        User user = application.getCurrentUser();
        Uri uri;
        if (user.getLogo() != null) {
            uri = Uri.parse(user.getLogo().getUrl());
        } else {
            uri = Uri.parse("res://" + getPackageName() + "/" + R.mipmap.user_logo_default);
        }
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(100, 100))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(logoView.getController())
                .build();
        logoView.setController(controller);

        String nickname = TextUtils.isEmpty(user.getNickname())
                ? user.getUsername()
                : user.getNickname();
        sickNameView.setText(nickname);
    }

    /**
     * 显示对应的页面
     *
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            showFragment(0);
        } else if (id == R.id.nav_message) {
            showFragment(1);
        } else if (id == R.id.nav_settings) {
            showFragment(2);
        } else {
            showFragment(0);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
