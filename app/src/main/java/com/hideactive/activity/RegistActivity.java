package com.hideactive.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hideactive.R;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;

import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity {


	private EditText usernameView;
	private EditText passwordView;
	private EditText repasswordView;
	private Button registButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
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
		repasswordView = (EditText) findViewById(R.id.repassword);
		registButton = (Button) findViewById(R.id.regist);
		registButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = usernameView.getText().toString().trim();
				String password = passwordView.getText().toString().trim();
				String repassword = repasswordView.getText().toString().trim();
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
					ToastUtil.showShort("请填写完整信息！");
					return;
				}
				if (!password.equals(repassword)) {
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
	private void regist(String username, String password) {
		loadingDialog.show();
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.signUp(this, new SaveListener() {
			@Override
			public void onSuccess() {
				loadingDialog.dismiss();
				ToastUtil.showShort("注册成功！");
			}

			@Override
			public void onFailure(int i, String s) {
				loadingDialog.dismiss();
				ToastUtil.showShort(s);
			}
		});
	}

}