package com.hideactive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hideactive.R;
import com.hideactive.model.User;
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
        getActionBar().setDisplayShowHomeEnabled(false);
        initView();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
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
			startActivity(new Intent(this, RegistActivity.class));
			finish();
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    ToastUtil.showShort("账号或密码错误！");
                }
            }
        });
	}
}