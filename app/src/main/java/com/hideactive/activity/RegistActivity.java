package com.hideactive.activity;

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.model.User;
import com.hideactive.util.ActivityCollector;
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

		initView();
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	loadingDialog.dismiss();
    }

    public void initView() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.action_bar_custom);
		ImageButton actionBar_img = (ImageButton) actionBar.getCustomView().findViewById(R.id.custom_actionbar_img);
		actionBar_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeActivity();
			}
		});
		TextView actionBar_text = (TextView) actionBar.getCustomView().findViewById(R.id.custom_actionbar_text);
		actionBar_text.setText(getResources().getString(R.string.regist));

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
                closeActivity();
			}

			@Override
			public void onFailure(int i, String s) {
				loadingDialog.dismiss();
				ToastUtil.showShort(s);
			}
		});
	}

}