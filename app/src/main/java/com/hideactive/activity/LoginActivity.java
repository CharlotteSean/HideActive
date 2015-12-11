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
			String name = usernameView.getText().toString().trim();
			String psw = passwordView.getText().toString();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(psw)) {
				login(name, psw);
			} else {
				Toast.makeText(this, "请填写账号或密码！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.regist:
			startActivity(new Intent(this, RegistActivity.class));
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
	}
	
}