package com.hideactive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.dialog.OffsiteNotifyDialog;
import com.hideactive.model.User;
import com.hideactive.util.PushUtil;
import com.hideactive.util.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private EditText usernameView;
    private EditText passwordView;
    private Button loginButton;
    private Button registButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        // 异地登录提示
        boolean isOffsite = getIntent().getBooleanExtra("isOffsite", false);
        if (isOffsite) {
            OffsiteNotifyDialog offsiteNotifyDialog = new OffsiteNotifyDialog(this);
            offsiteNotifyDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.login));

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        registButton = (Button) findViewById(R.id.regist);
        registButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			String username = usernameView.getText().toString().trim();
			String password = passwordView.getText().toString();
			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
				login(username, password);
			} else {
                ToastUtil.showShort("请填写账号或密码！");
			}
			break;
		case R.id.regist:
            openActivity(new Intent(this, RegistActivity.class));
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
        hideSoftInputView();
        loginButton.setEnabled(false);
        loginButton.setText(getString(R.string.logining));
        BmobUser.loginByAccount(this, account, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    // 检查异地登录，并更新登录信息
                    PushUtil.notifyOffsite(LoginActivity.this, user.getObjectId());
                    // 跳转
                    openActivityAndClose(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    ToastUtil.showShort("账号或密码错误！");
                }
            }
        });
	}

}