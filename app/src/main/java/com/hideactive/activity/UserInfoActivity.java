package com.hideactive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.model.User;
import com.hideactive.util.ToastUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity {

    private ImageView userLogoBgView;
    private CircleImageView userLogoView;
    private TextView userNameView;
    private TextView userSexView;
    private TextView userAgeView;
    private TextView userSignatureView;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        loadUser(getIntent().getStringExtra("uId"));
    }

    /**
     * 提供打开方法，解耦
     * @param context
     * @param uId
     */
    public static void start(Context context, String uId) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("uId", uId);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void initView() {
        Button closeBtn = (Button) findViewById(R.id.btn_close);
        closeBtn.setText(getString(R.string.back));
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        userLogoBgView = (ImageView) findViewById(R.id.user_logo_bg);
        userLogoView = (CircleImageView) findViewById(R.id.user_logo);
        userNameView = (TextView) findViewById(R.id.user_name);
        userSexView = (TextView) findViewById(R.id.user_sex);
        userAgeView = (TextView) findViewById(R.id.user_age);
        userSignatureView = (TextView) findViewById(R.id.user_signature);

    }

    private void refreshView() {
        if (user.getLogo() != null) {
//            Bitmap image = ImageLoader.getInstance().loadImageSync(user.getLogo().getUrl());
//            Bitmap newImg = Blur.fastblur(UserInfoActivity.this, image, 12);
//            BitmapDrawable bd = new BitmapDrawable(getResources(), newImg);
//            userLogoBgView.setBackgroundDrawable(bd);
//            userLogoBgView.setImageBitmap(newImg);
//            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
//                    userLogoView, ImageLoaderOptions.getOptions());
        } else {
            userLogoView.setImageResource(R.mipmap.user_logo_default);
        }
        userNameView.setText(TextUtils.isEmpty(user.getNickname()) ?
                getString(R.string.wu) : user.getNickname().toString());
        userSexView.setText(user.getSex().intValue() == 0 ?
                getString(R.string.male) : getString(R.string.female));
        userAgeView.setText(user.getAge().toString());
        userSignatureView.setText(TextUtils.isEmpty(user.getSignature()) ?
                getString(R.string.wu) : user.getSignature().toString());
    }

    /**
     * 加载用户
     * @param uId
     */
    private void loadUser(String uId) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("objectId", uId);
        query.getObject(uId, new QueryListener<User>() {
            @Override
            public void done(User object, BmobException e) {
                if (e == null) {
                    user = object;
                    // 加载页面
                    refreshView();
                } else {
                    ToastUtil.showShort("用户信息获取失败：" + e.getMessage());
                }
            }
        });
    }

}