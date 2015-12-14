package com.hideactive.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity implements OnClickListener {


    private CircleImageView userLogoView;
    private TextView userNameView;
    private TextView userSexView;
    private TextView userAgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
        Button actionBarLeftBtn = (Button) findViewById(R.id.btn_action_bar_left);
        Button actionBarRightBtn = (Button) findViewById(R.id.btn_action_bar_right);
        TextView actionBarTitle = (TextView) findViewById(R.id.tv_action_bar_title);
        Drawable img_left = getResources().getDrawable(R.mipmap.actionbar_up);
        img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
        actionBarLeftBtn.setCompoundDrawables(img_left, null, null, null);
        actionBarLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        actionBarRightBtn.setVisibility(View.GONE);
        actionBarTitle.setText(getResources().getString(R.string.regist));

        userLogoView = (CircleImageView) findViewById(R.id.user_logo);
        userNameView = (TextView) findViewById(R.id.user_name);
        userSexView = (TextView) findViewById(R.id.user_sex);
        userAgeView = (TextView) findViewById(R.id.user_age);

        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        }
        userNameView.setText(user.getUsername());
        userSexView.setText(user.getSex() == 0 ? "男" : "女");
        userAgeView.setText(user.getAge() + "");

    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
//			String username = usernameView.getText().toString().trim();
//			String password = passwordView.getText().toString();
//			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
//				login(username, password);
//			} else {
//                ToastUtil.showShort("请填写账号或密码！");
//			}
			break;
		case R.id.regist:
            openActivity(new Intent(this, RegistActivity.class));
            closeActivity();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 用户登录
	 * @param account
	 * @param password
	 */
	private void login(final String account, String password) {
		loadingDialog.show();
        BmobUser.loginByAccount(this, account, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    openActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                    closeActivity();
                } else {
                    ToastUtil.showShort("账号或密码错误！");
                }
            }
        });
	}
}