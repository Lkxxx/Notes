package com.lk.notes;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lk on 06/03.
 */
public class ImageProcessing {

    public ImageProcessing saveScalePhoto(Bitmap bitmap, String id, Context context) {

        String pathUrl = Environment.getExternalStorageDirectory().getPath() + "/Notes/image/";

        FileOutputStream fos = null;
        File file = new File(pathUrl);
        file.mkdirs();// 创建文件夹
        Bitmap newBitmap;
        Log.e("widgh+height", bitmap.getWidth() + "宽高" + bitmap.getHeight());
        if (bitmap.getWidth() > 1500) {
            newBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1080 * bitmap.getHeight() / bitmap.getWidth(), true);
            try {
                fos = new FileOutputStream(pathUrl + id);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newBitmap.recycle();
            }
        } else {
            try {
                new BackupTask(context).fileCopy(new File(getPhotopath()), new File(pathUrl + id));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return null;
    }

    public static String getPhotopath() {
        String fileName = "";
        String pathUrl = Environment.getExternalStorageDirectory() + "/Notes/image/cache/";
        String imageName = "cache";
        File file = new File(pathUrl);
        file.mkdirs();
        fileName = pathUrl + imageName;
        return fileName;
    }

    public Bitmap setPhoto(WindowManager m, String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        options.inJustDecodeBounds = false;
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        int mScreenHeight = localDisplayMetrics.heightPixels;
        Log.e("Screenwidgh+height", mScreenWidth + "宽高" + mScreenHeight);
        Log.e("widgh+height", bitmap.getWidth() + "宽高" + bitmap.getHeight());

        Bitmap newBitmap;
        newBitmap = Bitmap.createScaledBitmap(bitmap, mScreenWidth, mScreenWidth * mHeight / mWidth, true);


        return newBitmap;

    }


    public Bitmap bigBitmap(Bitmap bitmap) {
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        Bitmap newBitmap = null;

        if (mWidth > 2048 || mHeight > 2048) {
            newBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1080 * bitmap.getHeight() / bitmap.getWidth(), true);
        } else {
            newBitmap = bitmap;
        }

        return newBitmap;
    }

    public int getPhotoDirection(String path) {
        int direction = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            direction = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direction;
    }

    public Bitmap getPhotoCache(String path, WindowManager m) {
        Bitmap bitmap = bigBitmap(BitmapFactory.decodeFile(path));
        int direction = getPhotoDirection(path);
        Matrix matrix = new Matrix();
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        int mScreenHeight = localDisplayMetrics.heightPixels;
        Log.e("Screenwidgh+height", mScreenWidth + "宽高" + mScreenHeight);
        Log.e("widgh+height", bitmap.getWidth() + "宽高" + bitmap.getHeight());
        float postWidth = mScreenWidth / mWidth;
        matrix.postScale(postWidth, postWidth);
        matrix.reset();
        if (direction == 6) {
            matrix.postRotate(90);
        } else if (direction == 3) {
            matrix.postRotate(180);
        } else if (direction == 8) {
            matrix.postRotate(270);
        }

        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getPhotopath());
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap1;

    }


    public String uri2path(Uri uri, final Activity activity) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        activity.startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String mPhotoPath = cursor.getString(column_index);
        return mPhotoPath;
    }

    public void getPhotoRgb(final Window window, final Bitmap bitmap) {
        Palette.generateAsync(bitmap, 32, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                if (vibrant != null) {
                    window.setStatusBarColor(vibrant.getRgb());
                    Log.e("rgb", String.valueOf(vibrant.getRgb()));
                    window.setNavigationBarColor(vibrant.getRgb());
                }

            }
        });
    }




}
