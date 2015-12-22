package com.hideactive.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.model.User;

public class AboutActivity extends BaseActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.about));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        topBarLeftBtn.setText(getResources().getString(R.string.cancle));
        topBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }
}
