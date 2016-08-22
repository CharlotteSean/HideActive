package com.hideactive.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片处理类
 * Created by Senierr on 2015/12/12.
 */
public class PhotoUtil {

    /**
     * 压缩图片
     * @return
     */
    public static Bitmap compressImage(String srcPath, String newPath, boolean isDeteleOld) {
        Bitmap bitmap = getSmallBitmap(srcPath);
        int degree = readPictureDegree(srcPath);
        if(degree!=0){
            bitmap=rotateBitmap(bitmap,degree);
        }
        // 将旋转后的图片重新保存
        File newFile = new File(newPath);
        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        try {
            // 判断SD卡是否存在
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                FileOutputStream out = new FileOutputStream(newFile);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
                    out.flush();
                    out.close();
                }
            } else {
                ToastUtil.showShort("Save failed! Please make sure the SDCard is exist!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        // 删除原本图片
        File oldFile = new File(srcPath);
        if (isDeteleOld) {
            oldFile.delete();
        }
        // 返回新生成的图片
        return BitmapFactory.decodeFile(newPath);
    }

    /**
     * 计算图片的缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 读取图片旋转角度
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

}
