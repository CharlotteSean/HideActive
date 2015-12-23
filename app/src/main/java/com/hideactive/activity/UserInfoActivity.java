package com.hideactive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hideactive.R;
import com.hideactive.config.Constant;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.CameraOrNativeDialog;
import com.hideactive.dialog.EditTextDialog;
import com.hideactive.dialog.SelectSexDialog;
import com.hideactive.model.User;
import com.hideactive.util.Blur;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
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
            Bitmap image = ImageLoader.getInstance().loadImageSync(user.getLogo().getUrl());
            Bitmap newImg = Blur.fastblur(UserInfoActivity.this, image, 12);
            BitmapDrawable bd = new BitmapDrawable(getResources(), newImg);
            userLogoBgView.setBackgroundDrawable(bd);
            userLogoBgView.setImageBitmap(newImg);
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
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
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                if (object != null && object.size() > 0) {
                    user = object.get(0);
                    // 加载页面
                    refreshView();
                } else {
                    ToastUtil.showShort("用户信息获取失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                ToastUtil.showShort("用户信息获取失败：" + msg);
            }
        });
    }

}