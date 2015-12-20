package com.hideactive.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hideactive.R;
import com.hideactive.config.Constant;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class CreatePostActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_IMAGE_NATIVE = 0;
    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    private EditText inputView;
    private ImageButton nativeButton;
    private ImageButton cameraButton;
    private ImageView showImage;
    private ImageButton showImageDeleteBtn;
    private RelativeLayout showImageEare;

    private String localCameraPath;// 拍照后得到的图片地址
    private String imagePath;// 上传的图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    public void initView() {
        TextView topBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        topBarTitle.setText(getResources().getString(R.string.edit));
        Button topBarLeftBtn = (Button) findViewById(R.id.btn_top_bar_left);
        topBarLeftBtn.setVisibility(View.VISIBLE);
        topBarLeftBtn.setText(getResources().getString(R.string.cancle));
        topBarLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
        Button topBarRightBtn = (Button) findViewById(R.id.btn_top_bar_right);
        topBarRightBtn.setVisibility(View.VISIBLE);
        topBarRightBtn.setText(getResources().getString(R.string.post));
        topBarRightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });


        inputView = (EditText) findViewById(R.id.input_eare);
        nativeButton = (ImageButton) findViewById(R.id.image_native);
        nativeButton.setOnClickListener(this);
        cameraButton = (ImageButton) findViewById(R.id.image_camera);
        cameraButton.setOnClickListener(this);
        showImage = (ImageView) findViewById(R.id.show_image);
        showImageDeleteBtn = (ImageButton) findViewById(R.id.show_image_delete);
        showImageDeleteBtn.setOnClickListener(this);
        showImageEare = (RelativeLayout) findViewById(R.id.show_image_eare);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_native:
                selectImageFromLocal();
                break;
            case R.id.image_camera:
                selectImageFromCamera();
                break;
            case R.id.show_image_delete:
                localCameraPath = "";
                imagePath = "";
                showImageEare.setVisibility(View.GONE);
                showImage.setImageDrawable(null);
                break;
            default:
                break;
        }
    }

    /**
     * 发表前上传图片
     */
    private void post() {
        loadingDialog.show();
        if (TextUtils.isEmpty(imagePath)) {
            // 没有图片，直接发表
            publishPost(null);
            return;
        }
        // 有图片，上传图片
        BmobProFile.getInstance(this).upload(imagePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                Log.d("bmob", "bmobFile-Url：" + bmobFile.getUrl());
                publishPost(bmobFile);
            }

            @Override
            public void onProgress(int i) {
                Log.d("bmob", "onProgress：" + i);
            }

            @Override
            public void onError(int i, String s) {
                loadingDialog.dismiss();
                ToastUtil.showShort("发表失败：" + s);
            }
        });
    }

    /**
     * 发表
     * @param imageFile
     */
    private void publishPost(BmobFile imageFile) {
        String postContent = inputView.getText().toString();
        // 若没有图片，则发表内容不能为空
        if (imageFile == null && TextUtils.isEmpty(postContent)) {
            loadingDialog.dismiss();
            ToastUtil.showShort("请输入内容或添加图片！");
            return;
        }
        User user = application.getCurrentUser();
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postContent);
        post.setImage(imageFile);
        post.setCommentNum(0);
        post.setLikeNum(0);
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                ToastUtil.showShort("发表成功！");
                closeActivity();
            }

            @Override
            public void onFailure(int i, String s) {
                loadingDialog.dismiss();
                ToastUtil.showShort("发表失败：" + s);
            }
        });
    }

    /**
     * 启动相机拍照
     */
    public void selectImageFromCamera() {
        File dir = new File(Constant.IMAGE_CACHE_PATH + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis()));
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
                    String cameraPath = Constant.IMAGE_CACHE_PATH + File.separator + String.valueOf(System.currentTimeMillis());
                    Bitmap cameraBitmap = PhotoUtil.compressImage(localCameraPath, cameraPath, true);
                    // 界面显示
                    showImageEare.setVisibility(View.VISIBLE);
                    showImage.setImageBitmap(cameraBitmap);
                    imagePath = cameraPath;
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
                                String nativePath = Constant.IMAGE_CACHE_PATH  + File.separator + String.valueOf(System.currentTimeMillis());
                                nativeBitmap = PhotoUtil.compressImage(localSelectPath, nativePath, false);
                                imagePath = nativePath;
                            }
                            // 界面显示
                            showImageEare.setVisibility(View.VISIBLE);
                            showImage.setImageBitmap(nativeBitmap);
                        }
                    }
                    break;
            }
        }
        Log.d("", "imagePath : " + imagePath);
    }
}