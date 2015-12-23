package com.hideactive.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.activity.AboutActivity;
import com.hideactive.activity.LoginActivity;
import com.hideactive.activity.MainActivity;
import com.hideactive.activity.PersonalInfoActivity;
import com.hideactive.config.Constant;
import com.hideactive.config.UserConfig;
import com.hideactive.util.FileUtil;
import com.hideactive.util.PushUtil;
import com.hideactive.util.ToastUtil;
import com.zcw.togglebutton.ToggleButton;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View userInfoItemView;
    private View isNotifyView;
    private View isNotifyDetailView;
    private View isNotifyVoiceView;
    private View isNotifyVirbateView;
    private View clearCacheView;
    private View isAboutView;

    private ToggleButton isNotifyTb;
    private ToggleButton isNotifyDetailTb;
    private ToggleButton isNotifyVoiceTb;
    private ToggleButton isNotifyVirbateTb;
    private TextView cacheSizeTv;
    private Button logoutBtn;

    private UserConfig userConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        init();
    }

    private void init() {
        userConfig = application.getUserConfig();

        if (userConfig.isAllowNotify()) {
            isNotifyTb.setToggleOn();
        } else {
            isNotifyTb.setToggleOff();
        }
        if (userConfig.isAllowNotifyDetail()) {
            isNotifyDetailTb.setToggleOn();
        } else {
            isNotifyDetailTb.setToggleOff();
        }
        if (userConfig.isAllowVoice()) {
            isNotifyVoiceTb.setToggleOn();
        } else {
            isNotifyVoiceTb.setToggleOff();
        }
        if (userConfig.isAllowVibrate()) {
            isNotifyVirbateTb.setToggleOn();
        } else {
            isNotifyVirbateTb.setToggleOff();
        }
    }

    private void initView() {
        userInfoItemView = findViewById(R.id.setting_item_user_info);
        isNotifyView = findViewById(R.id.setting_item_is_notify);
        isNotifyDetailView = findViewById(R.id.setting_item_is_notify_detail);
        isNotifyVoiceView = findViewById(R.id.setting_item_is_notify_voice);
        isNotifyVirbateView = findViewById(R.id.setting_item_is_notify_virbate);
        clearCacheView = findViewById(R.id.setting_item_clear_cache);
        isAboutView = findViewById(R.id.setting_item_about);

        userInfoItemView.setOnClickListener(this);
        clearCacheView.setOnClickListener(this);
        isAboutView.setOnClickListener(this);

        isNotifyTb = (ToggleButton) findViewById(R.id.tb_is_notify);
        isNotifyDetailTb = (ToggleButton) findViewById(R.id.tb_is_notify_detail);
        isNotifyVoiceTb = (ToggleButton) findViewById(R.id.tb_is_notify_voice);
        isNotifyVirbateTb = (ToggleButton) findViewById(R.id.tb_is_notify_virbate);

        // 设置是否允许推送
        isNotifyTb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                userConfig.setPushNotifyEnable(on);
                // 若不推送，则下面设置隐藏
                if (on) {
                    isNotifyDetailView.setVisibility(View.VISIBLE);
                    isNotifyVoiceView.setVisibility(View.VISIBLE);
                    isNotifyVirbateView.setVisibility(View.VISIBLE);
                } else {
                    isNotifyDetailView.setVisibility(View.GONE);
                    isNotifyVoiceView.setVisibility(View.GONE);
                    isNotifyVirbateView.setVisibility(View.GONE);
                }
            }
        });
        // 设置是否推送显示详情
        isNotifyDetailTb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                userConfig.setNotifyDetailEnable(on);
            }
        });
        // 设置推送是否有声音
        isNotifyVoiceTb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                userConfig.setAllowVoiceEnable(on);
            }
        });
        // 设置推送是否振动
        isNotifyVirbateTb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                userConfig.setAllowVibrateEnable(on);
            }
        });

        cacheSizeTv = (TextView) findViewById(R.id.tv_cache_size);
        cacheSizeTv.setText(FileUtil.getFormatSize(FileUtil.getFolderSize(new File(Constant.IMAGE_CACHE_PATH))));

        logoutBtn = (Button) findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_item_user_info:
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.setting_item_clear_cache:
                clearCacheTask.execute();
                break;
            case R.id.setting_item_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case R.id.btn_logout:
                // 注销登录设备
                PushUtil.logoutInstallation(getActivity());
                application.logout();
                break;
        }
    }

    /**
     * 清除缓存异步任务
     */
    AsyncTask clearCacheTask = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] params) {
            File file = new File(Constant.IMAGE_CACHE_PATH);
            FileUtil.clearCache(file);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            ToastUtil.showShort("缓存清除完毕");
            cacheSizeTv.setText(null);
        }

    };

}