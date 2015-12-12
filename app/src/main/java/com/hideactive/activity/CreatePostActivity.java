package com.hideactive.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bmob.BTPFileResponse;
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

    private String localCameraPath;// 拍照后得到的图片地址
    private String imagePath;// 上传的图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getActionBar().setDisplayShowHomeEnabled(false);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    public void initView() {
        inputView = (EditText) findViewById(R.id.input_eare);
        nativeButton = (ImageButton) findViewById(R.id.image_native);
        nativeButton.setOnClickListener(this);
        cameraButton = (ImageButton) findViewById(R.id.image_camera);
        cameraButton.setOnClickListener(this);
        showImage = (ImageView) findViewById(R.id.show_image);
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
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CreateMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuChoice(item);
    }

    private void CreateMenu(Menu menu) {
        MenuItem publishItem = menu.add(0, 0, 0, "发表");
        publishItem.setIcon(R.mipmap.actionbar_ok);
        publishItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    private boolean MenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                post();
                return true;
        }
        return false;
    }

    /**
     * 发表前上传图片
     */
    private void post() {
        if (TextUtils.isEmpty(imagePath)) {
            // 没有图片，直接发表
            publishPost(null);
            return;
        }
        // 有图片，上传图片
        BmobProFile.getInstance(this).upload(imagePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                Log.e("bmob", "bmobFile-Url：" + bmobFile.getUrl());
                publishPost(bmobFile);
            }

            @Override
            public void onProgress(int i) {
                Log.e("bmob", "onProgress：" + i);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("bmob", "文件上传失败：" + s);
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
            ToastUtil.showShort("请输入内容！");
            return;
        }
        User user = application.getCurrentUser();
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postContent);
        post.setImage(imageFile);
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtil.showShort("发表成功！");
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtil.showShort("发表失败：" + s);
            }
        });
    }

    /**
     * 启动相机拍照
     */
    public void selectImageFromCamera() {
        File dir = new File(Constant.IMAGE_CACHE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
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
                    String cameraPath = Constant.IMAGE_CACHE_PATH + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    Bitmap cameraBitmap = PhotoUtil.compressImage(localCameraPath, cameraPath, true);
                    // 界面显示
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
                            String nativePath = Constant.IMAGE_CACHE_PATH + String.valueOf(System.currentTimeMillis()) + ".jpg";
                            Bitmap nativeBitmap = PhotoUtil.compressImage(localSelectPath, nativePath, false);
                            // 界面显示
                            showImage.setImageBitmap(nativeBitmap);
                            imagePath = nativePath;
                        }
                    }
                    break;
            }
        }
        Log.e("", "imagePath : " + imagePath);
    }
}