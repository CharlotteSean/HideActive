package com.hideactive.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.config.Constant;
import com.hideactive.dialog.CameraOrNativeDialog;
import com.hideactive.dialog.EditTextDialog;
import com.hideactive.dialog.SelectSexDialog;
import com.hideactive.model.User;
import com.hideactive.util.FileUtil;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_IMAGE_NATIVE = 0;
    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    private CircleImageView userLogoView;
    private TextView userNameView;
    private TextView userSexView;
    private TextView userAgeView;
    private TextView userSignatureView;

    private View userLogoItemView;
    private View userNameItemView;
    private View userSexItemView;
    private View userAgeItemView;
    private View userSignatureItemView;

    private String localCameraPath;// 拍照后得到的图片地址
    private String imagePath;// 上传的图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initView();
    }

    public void initView() {
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.user_info));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        topBarLeftBtn.setText(getResources().getString(R.string.back));
        topBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        userLogoView = (CircleImageView) findViewById(R.id.user_logo);
        userNameView = (TextView) findViewById(R.id.user_name);
        userSexView = (TextView) findViewById(R.id.user_sex);
        userAgeView = (TextView) findViewById(R.id.user_age);
        userSignatureView = (TextView) findViewById(R.id.user_signature);

        User user = application.getCurrentUser();
        if (user.getLogo() != null) {
//            ImageLoader.getInstance().displayImage(user.getLogo().getUrl(),
//                    userLogoView, ImageLoaderOptions.getOptions());
        } else {
            userLogoView.setImageResource(R.mipmap.user_logo_default);
        }
        userNameView.setText(TextUtils.isEmpty(user.getNickname()) ?
                getString(R.string.click_to_edit) : user.getNickname().toString());
        userSexView.setText(user.getSex().intValue() == 0 ? "男" : "女");
        userAgeView.setText(user.getAge().toString());
        userSignatureView.setText(TextUtils.isEmpty(user.getSignature()) ?
                getString(R.string.click_to_edit) : user.getSignature().toString());

        userLogoItemView = findViewById(R.id.user_item_logo);
        userNameItemView = findViewById(R.id.user_item_name);
        userSexItemView = findViewById(R.id.user_item_sex);
        userAgeItemView = findViewById(R.id.user_item_age);
        userSignatureItemView = findViewById(R.id.user_item_signature);

        userLogoItemView.setOnClickListener(this);
        userNameItemView.setOnClickListener(this);
        userSexItemView.setOnClickListener(this);
        userAgeItemView.setOnClickListener(this);
        userSignatureItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_item_logo:
                CameraOrNativeDialog cameraOrNativeDialog = new CameraOrNativeDialog(this, new CameraOrNativeDialog.OnSelectedListener() {
                    @Override
                    public void onSelected(int type) {
                        switch (type) {
                            case CameraOrNativeDialog.CAMERA:
                                selectImageFromCamera();
                                break;
                            case CameraOrNativeDialog.NATIVE:
                                selectImageFromLocal();
                                break;
                        }
                    }
                });
                cameraOrNativeDialog.show();
                break;
            case R.id.user_item_name:
                EditTextDialog editNameDialog = new EditTextDialog(this,
                        getString(R.string.user_name), "起个绚丽的名字吧！", InputType.TYPE_CLASS_TEXT,
                        application.getCurrentUser().getNickname(), 16,
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setNickname(contentStr);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            userNameView.setText(contentStr);
                                            loadingDialog.dismiss();
                                        } else {
                                            ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                editNameDialog.show();
                break;
            case R.id.user_item_sex:
                SelectSexDialog selectSexDialog = new SelectSexDialog(this,
                        application.getCurrentUser().getSex(), new SelectSexDialog.OnDoneListener(){
                            @Override
                            public void onDone(final Integer sex) {
                                loadingDialog.show();
                                User user = new User();
                                user.setSex(sex);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            userSexView.setText(sex.intValue() == 0 ? "男" : "女");
                                            loadingDialog.dismiss();
                                        } else {
                                            ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                selectSexDialog.show();
                break;
            case R.id.user_item_age:
                EditTextDialog editAgeDialog = new EditTextDialog(this,
                        getString(R.string.user_age), "你还是18岁吗？", InputType.TYPE_CLASS_NUMBER,
                        String.valueOf(application.getCurrentUser().getAge()), 3,
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setAge(Integer.parseInt(contentStr));
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            userAgeView.setText(contentStr);
                                            loadingDialog.dismiss();
                                        } else {
                                            ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                editAgeDialog.show();
                break;
            case R.id.user_item_signature:
                EditTextDialog editSignatureDialog = new EditTextDialog(this,
                        getString(R.string.user_signature), "你是个随意的人吗？", InputType.TYPE_CLASS_TEXT,
                        application.getCurrentUser().getSignature(), 50,
                        new EditTextDialog.OnDoneListener() {
                            @Override
                            public void onDone(final String contentStr) {
                                loadingDialog.show();
                                User user = new User();
                                user.setSignature(contentStr);
                                updateUser(user, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            userSignatureView.setText(contentStr);
                                            loadingDialog.dismiss();
                                        } else {
                                            ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                editSignatureDialog.show();
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
        final BmobFile bmobFile = new BmobFile(new File(imagePath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    user.setLogo(bmobFile);
                    updateUser(user, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                userLogoView.setImageBitmap(bitmap);
                                loadingDialog.dismiss();
                            } else {
                                ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                                loadingDialog.dismiss();
                            }
                        }
                    });
                } else {
                    ToastUtil.showShort("更新用户信息失败:" + e.getMessage());
                    loadingDialog.dismiss();
                }
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
        newUser.update(currentUser.getObjectId(), listener);
    }

    /**
     * 启动相机拍照
     */
    public void selectImageFromCamera() {
        File file = FileUtil.getDiskCacheDir(this, String.valueOf(System.currentTimeMillis()));
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, REQUEST_CODE_IMAGE_CAMERA);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE_CAMERA:
                    // 获取拍照的压缩图片
                    String cameraPath = FileUtil.getDiskCacheDir(this, String.valueOf(System.currentTimeMillis()) + ".jpg").getPath();
                    Bitmap cameraBitmap = PhotoUtil.compressImage(localCameraPath, cameraPath, true);
                    imagePath = cameraPath;
                    // 更新头像
                    uploadLogo(imagePath, cameraBitmap);
                    break;
                case REQUEST_CODE_IMAGE_NATIVE:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null || localSelectPath.equals("null")) {
                                ToastUtil.showShort("未取到图片！");
                                return;
                            }
                            Bitmap nativeBitmap = null;
                            File localFile = new File(localSelectPath);
                            // 若此文件小于100KB，直接使用。为了减轻缓存容量
                            if (localFile.length() < 102400) {
                                nativeBitmap = BitmapFactory.decodeFile(localSelectPath);
                                imagePath = localSelectPath;
                            } else {
                                String nativePath = FileUtil.getDiskCacheDir(this, String.valueOf(System.currentTimeMillis()) + ".jpg").getPath();
                                nativeBitmap = PhotoUtil.compressImage(localSelectPath, nativePath, false);
                                imagePath = nativePath;
                            }
                            // 更新头像
                            uploadLogo(imagePath, nativeBitmap);
                        }
                    }
                    break;
            }
        }
    }

}