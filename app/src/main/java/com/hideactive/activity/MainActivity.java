package com.hideactive.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.adapter.PostListAdapter;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.ActivityCollector;
import com.hideactive.util.Blur;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.RefreshViewHolder;
import com.hideactive.widget.SuperSwipeRefreshLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends BaseActivity {

    private SlidingMenu menu;
	private ListView postListView;
	private SuperSwipeRefreshLayout swipeRefreshLayout;
	private RefreshViewHolder refreshViewHolder;

	private static final int PAGE_SIZE = 10;

	private List<Post>  postList;
	private PostListAdapter postListAdapter;
	private int currentPageIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.activity_main);
		
		initView();
        initSlidingMenu();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// 初始化数据
		currentPageIndex = 0;
		loadPost();
	}

	private void initView() {
		Button actionBarLeftBtn = (Button) findViewById(R.id.btn_action_bar_left);
		Button actionBarRightBtn = (Button) findViewById(R.id.btn_action_bar_right);
		TextView actionBarTitle = (TextView) findViewById(R.id.tv_action_bar_title);
        Drawable img_left = getResources().getDrawable(R.mipmap.actionbar_todo);
        img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
        actionBarLeftBtn.setCompoundDrawables(img_left, null, null, null);
        actionBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });

		Drawable img_right = getResources().getDrawable(R.mipmap.actionbar_page);
        img_right.setBounds(0, 0, img_right.getMinimumWidth(), img_right.getMinimumHeight());
		actionBarRightBtn.setCompoundDrawables(img_right, null, null, null);
		actionBarRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });
		actionBarTitle.setText(getResources().getString(R.string.app_name));

		postListView = (ListView) findViewById(R.id.lv_post);
		postList = new ArrayList<Post>();
		postListAdapter = new PostListAdapter(this, postList);
		postListView.setAdapter(postListAdapter);

		swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setHeaderViewBackgroundColor(getResources().getColor(R.color.refresh_header_bg));
		refreshViewHolder = new RefreshViewHolder(this);
		swipeRefreshLayout.setHeaderView(refreshViewHolder.getHeaderView());
		swipeRefreshLayout.setFooterView(refreshViewHolder.getFooterView());
		swipeRefreshLayout.setTargetScrollWithLayout(true);
		swipeRefreshLayout
				.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

					@Override
					public void onRefresh() {
						refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_ING);
						currentPageIndex = 0;
						loadPost();
					}

					@Override
					public void onPullDistance(int distance) {
						// 0 ~ 192PX, 0 ~ 64DP
//                        Log.e("", "distance : " + ViewUtil.px2dip(getActivity(), distance));
					}

					@Override
					public void onPullEnable(boolean enable) {
						if (enable) {
							refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_CAN);
						} else {
							refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_CAN_NOT);
						}
                    }
                });

        swipeRefreshLayout.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_ING);
				loadPost();
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {
				if (enable) {
					refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_CAN);
				} else {
					refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_CAN_NOT);
				}
            }
        });
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
        menu.setShadowWidthRes(R.dimen.sliding_shadow);
        // 设置阴影图片
//        menu.setShadowDrawable(R.drawable.shadow);
        // //设置滑出时主页面显示的剩余宽度
        menu.setBehindOffsetRes(R.dimen.sliding_offset_res);
        // 设置菜单是否渐变
        menu.setFadeEnabled(true);
        // 设置渐变效果的值
        menu.setFadeDegree(0.35f);
        //使SlidingMenu附加在Activity
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.sliding_menu);

        User user = application.getCurrentUser();

        ImageView topPanel = (ImageView) menu.findViewById(R.id.user_logo_bg);
        Bitmap image = ImageLoader.getInstance()
                .loadImageSync(user.getLogo() == null ? null : user.getLogo().getUrl(),
                        ImageLoaderOptions.getOptions());
        if (image == null) {
            image = BitmapFactory.decodeResource(getResources(), R.mipmap.image_default);
        }
        Bitmap newImg = Blur.fastblur(this, image, 12);
        BitmapDrawable bd = new BitmapDrawable(getResources(), newImg);
        topPanel.setBackgroundDrawable(bd);
//        topPanel.setImageBitmap(newImg);

        ImageView userLogoView = (ImageView) menu.findViewById(R.id.user_logo);
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        }
        userLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(new Intent(MainActivity.this, UserInfoActivity.class));
            }
        });
        TextView userNameView = (TextView) menu.findViewById(R.id.user_name);
        userNameView.setText(user.getUsername());
    }

	/**
	 * 分页加载数据
	 */
	private void loadPost() {
		BmobQuery<Post> query = new BmobQuery<Post>();
		query.order("-updatedAt");
		query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
		query.setLimit(PAGE_SIZE);
		query.setSkip(PAGE_SIZE * currentPageIndex);
        // 设置先从网络获取，若没则取缓存
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.findObjects(this, new FindListener<Post>() {
			@Override
			public void onSuccess(List<Post> object) {
				// 若是起始页，则删除列表
				if (currentPageIndex == 0) {
					postList.clear();
				}
				currentPageIndex ++;
				postList.addAll(object);
				postListAdapter.notifyDataSetChanged();

				refreshViewHolder.refreshHeaderView(RefreshViewHolder.REFRESH_FINISHED);
				refreshViewHolder.refreshFooterView(RefreshViewHolder.REFRESH_FINISHED);
				swipeRefreshLayout.setRefreshCompleted();
				swipeRefreshLayout.setLoadMoreCompleted();
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtil.showShort("查询失败！");
			}
		});
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
