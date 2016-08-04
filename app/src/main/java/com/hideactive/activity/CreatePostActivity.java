package com.hideactive.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hideactive.R;
import com.hideactive.adapter.EmoViewPagerAdapter;
import com.hideactive.adapter.EmoteAdapter;
import com.hideactive.config.Constant;
import com.hideactive.model.FaceText;
import com.hideactive.model.Post;
import com.hideactive.model.User;
import com.hideactive.util.FaceTextUtils;
import com.hideactive.util.PhotoUtil;
import com.hideactive.util.ToastUtil;
import com.hideactive.widget.EmoticonsEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class CreatePostActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_IMAGE_NATIVE = 0;
    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;

    private static final int INPUT_LIMITED_LENGTH = 200;

    private EmoticonsEditText inputView;
    private TextView inputLenthTv;
    private ImageButton nativeButton;
    private ImageButton cameraButton;
    private ImageButton emojButton;
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
        initEmoView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.edit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        inputView = (EmoticonsEditText) findViewById(R.id.input_eare);
        inputView.setOnClickListener(this);
        // 监听内容输入区，动态显示剩余字数
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = INPUT_LIMITED_LENGTH - inputView.getText().toString().length();
                inputLenthTv.setText(String.valueOf(length));
            }
        });
        inputLenthTv = (TextView) findViewById(R.id.tv_input_length);

        nativeButton = (ImageButton) findViewById(R.id.image_native);
        nativeButton.setOnClickListener(this);
        cameraButton = (ImageButton) findViewById(R.id.image_camera);
        cameraButton.setOnClickListener(this);
        emojButton = (ImageButton) findViewById(R.id.image_emoj);
        emojButton.setOnClickListener(this);

        showImage = (ImageView) findViewById(R.id.show_image);
        showImageDeleteBtn = (ImageButton) findViewById(R.id.show_image_delete);
        showImageDeleteBtn.setOnClickListener(this);
        showImageEare = (RelativeLayout) findViewById(R.id.show_image_eare);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_eare:
                // 点击输入区域，隐藏表情输入区
                pager_emo.setVisibility(View.GONE);
                showSoftInputView();
                break;
            case R.id.image_native:
                selectImageFromLocal();
                break;
            case R.id.image_camera:
                selectImageFromCamera();
                break;
            case R.id.image_emoj:
                if (pager_emo.isShown()) {
                    pager_emo.setVisibility(View.GONE);
                } else {
                    hideSoftInputView();
                    pager_emo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pager_emo.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_post) {
            post();
        }
        return super.onOptionsItemSelected(item);
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
    }

    /******************************************* 表情部分 ******************************************************/

    private ViewPager pager_emo;
    private List<FaceText> emos;

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));

    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.layout_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(CreatePostActivity.this,
                list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (inputView != null && !TextUtils.isEmpty(key)) {
                        int start = inputView.getSelectionStart();
                        CharSequence content = inputView.getText()
                                .insert(start, key);
                        inputView.setText(content);
                        // 定位光标位置
                        CharSequence info = inputView.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(inputView, 0);
        }
    }

}