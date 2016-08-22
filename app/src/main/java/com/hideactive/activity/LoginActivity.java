package com.hideactive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hideactive.R;
import com.hideactive.dialog.OffsiteNotifyDialog;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_REGIST = 0;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.login);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance);
        setSupportActionBar(toolbar);

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
            openForResultActivity(new Intent(this, RegistActivity.class), REQUEST_CODE_REGIST);
			break;
		default:
			break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 注册成功回调
        if (requestCode == REQUEST_CODE_REGIST && resultCode == RESULT_OK) {
            if (application.getCurrentUser() != null) {
                // 跳转
                openActivityAndClose(new Intent(LoginActivity.this, MainActivity.class));
            }
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
        BmobUser.loginByAccount(account, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    // 跳转
                    openActivityAndClose(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    ToastUtil.showShort("账号或密码错误！");
                    loginButton.setEnabled(true);
                    loginButton.setText(getString(R.string.login));
                }
            }
        });
	}

}