package com.hideactive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity {

	private TextView usernametipsView;
	private TextView passwordtipsView;
	private EditText usernameView;
	private EditText passwordView;
	private EditText repasswordView;
	private Button registButton;

	private String regEx = "^[a-zA-Z0-9_]{4,15}$";
	private boolean isMatchUsername;
	private boolean isMatchPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

		initView();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
		TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
		topBarTitle.setText(getString(R.string.register));
		Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
		topBarLeftBtn.setVisibility(View.VISIBLE);
		topBarLeftBtn.setText(getString(R.string.cancle));
		topBarLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeActivity();
			}
		});

		usernametipsView = (TextView) findViewById(R.id.tv_username_tips);
		passwordtipsView = (TextView) findViewById(R.id.tv_password_tips);
		usernameView = (EditText) findViewById(R.id.username);
		passwordView = (EditText) findViewById(R.id.password);
		repasswordView = (EditText) findViewById(R.id.repassword);

		usernametipsView.setVisibility(View.GONE);
		usernameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// 失去焦点时判断合法性
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(usernameView.getText().toString());
					isMatchUsername = matcher.matches();
					if (!isMatchUsername) {
						usernametipsView.setVisibility(View.VISIBLE);
					} else {
						usernametipsView.setVisibility(View.GONE);
					}
				}
			}
		});
		passwordtipsView.setVisibility(View.GONE);
		passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// 失去焦点时判断合法性
					Pattern pattern = Pattern.compile(regEx);
					Matcher matcher = pattern.matcher(passwordView.getText().toString());
					isMatchPassword = matcher.matches();
					if (!isMatchPassword) {
						passwordtipsView.setVisibility(View.VISIBLE);
					} else {
						passwordtipsView.setVisibility(View.GONE);
					}
				}
			}
		});

		registButton = (Button) findViewById(R.id.regist);
		registButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isMatchUsername || !isMatchPassword) {
					return;
				}
				String username = usernameView.getText().toString().trim();
				String password = passwordView.getText().toString().trim();
				String rePassword = repasswordView.getText().toString().trim();
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
					ToastUtil.showShort("请填写完整信息！");
					return;
				}
				if (!password.equals(rePassword)) {
					ToastUtil.showShort("密码与确认密码不一致！");
					repasswordView.setText("");
					return;
				}
				regist(username, password);
			}
		});
    }

	/**
	 * 用户注册
	 * @param username
	 * @param password
	 */
	private void regist(final String username, String password) {
		loadingDialog.show();
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setAge(0);
		user.setSex(0);
		user.signUp(new SaveListener<User>() {
			@Override
			public void done(User user, BmobException e) {
				loadingDialog.dismiss();
				if (e == null) {
					ToastUtil.showShort("注册成功！");
					// 返回登录
					Intent intent = new Intent();
					intent.putExtra("username", username);
					setResult(RESULT_OK, intent);
					closeActivity();
				} else {
					ToastUtil.showShort(e.getMessage());
				}
			}
		});
	}

}