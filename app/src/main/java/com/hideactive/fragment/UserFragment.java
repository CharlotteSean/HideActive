package com.hideactive.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hideactive.R;
import com.hideactive.config.Constant;
import com.hideactive.config.ImageLoaderOptions;
import com.hideactive.dialog.EditTextDialog;
import com.hideactive.dialog.SelectSexDialog;
import com.hideactive.model.User;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_IMAGE_NATIVE = 0;

    private CircleImageView userLogoView;
    private TextView userNameView;
    private TextView userSexView;
    private TextView userAgeView;

    private View userLogoItemView;
    private View userNameItemView;
    private View userSexItemView;
    private View userAgeItemView;
    private View settingItemView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    /**
     * 监听当前fragment是否可见，第一次创建时不调用
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().getActionBar().setTitle(R.string.me);
        }
    }

    public void initView() {
        getActivity().getActionBar().setTitle(R.string.me);

        userLogoView = (CircleImageView) findViewById(R.id.user_logo);
        userNameView = (TextView) findViewById(R.id.user_name);
        userSexView = (TextView) findViewById(R.id.user_sex);
        userAgeView = (TextView) findViewById(R.id.user_age);

        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
                    userLogoView, ImageLoaderOptions.getOptions());
        }
        userNameView.setText(user.getNickname());
        userSexView.setText(user.getSex() != null && user.getSex() == 0 ? "男" : "女");
        Integer age = user.getAge();
        userAgeView.setText(String.valueOf(age == null ? 0 : age) );

        userLogoItemView = findViewById(R.id.user_item_logo);
        userNameItemView = findViewById(R.id.user_item_name);
        userSexItemView = findViewById(R.id.user_item_sex);
        userAgeItemView = findViewById(R.id.user_item_age);
        settingItemView = findViewById(R.id.user_item_setting);

        userLogoItemView.setOnClickListener(this);
        userNameItemView.setOnClickListener(this);
        userSexItemView.setOnClickListener(this);
        userAgeItemView.setOnClickListener(this);
        settingItemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_item_logo:
                selectImageFromLocal();
                break;
            case R.id.user_item_name:
                EditTextDialog editNameDialog = new EditTextDialog(getActivity(),
                        "昵称", "起个绚丽的名字吧！", InputType.TYPE_CLASS_TEXT, application.getCurrentUser().getNickname(),
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setNickname(contentStr);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userNameView.setText(contentStr);
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                editNameDialog.show();
                break;
            case R.id.user_item_sex:
                SelectSexDialog selectSexDialog = new SelectSexDialog(getActivity(),
                        application.getCurrentUser().getSex(), new SelectSexDialog.OnDoneListener(){
                            @Override
                            public void onDone(final int sex) {
                                loadingDialog.show();
                                User user = new User();
                                user.setSex(sex);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userSexView.setText(sex == 0 ? "男" : "女");
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                selectSexDialog.show();
                break;
            case R.id.user_item_age:
                EditTextDialog editAgeDialog = new EditTextDialog(getActivity(),
                        "年龄", "你还是18岁吗？", InputType.TYPE_CLASS_NUMBER,
                        String.valueOf(application.getCurrentUser().getAge()),
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setAge(Integer.parseInt(contentStr));
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        userAgeView.setText(contentStr);
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        ToastUtil.showShort("更新用户信息失败:" + s);
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });
                editAgeDialog.show();
                break;
            case R.id.user_item_setting:

                break;
            default:
                break;
        }
    }

    /**
     * 上传用户头像
     * @param imagePath
     */
    private void uploadLogo(String imagePath, final Bitmap bitmap) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        loadingDialog.show();
        BmobProFile.getInstance(getActivity()).upload(imagePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                User user = new User();
                user.setLogo(bmobFile);
                updateUser(user, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        userLogoView.setImageBitmap(bitmap);
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastUtil.showShort("更新用户信息失败:" + s);
                        loadingDialog.dismiss();
                    }
                });
            }
            @Override
            public void onProgress(int i) {
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }

    /**
     * 更新user信息
     * @param newUser
     * @param listener
     */
    private void updateUser(User newUser, UpdateListener listener) {
        if (newUser == null) {
            return;
        }
        User currentUser = application.getCurrentUser();
        newUser.update(getActivity(), currentUser.getObjectId(), listener);
    }

    /**
     * 获取本地图片
     */
    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_IMAGE_NATIVE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE_NATIVE && data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImage, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex("_data");
                    String localSelectPath = cursor.getString(columnIndex);
                    cursor.close();
                    if (localSelectPath == null || localSelectPath.equals("null")) {
                        ToastUtil.showShort("未取到图片！");
                        return;
                    }
                    String nativePath = Constant.IMAGE_CACHE_PATH + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    Bitmap bitmap = PhotoUtil.compressImage(localSelectPath, nativePath, false);
                    uploadLogo(nativePath, bitmap);
                }
            }
        }
    }
}