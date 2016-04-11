package com.bupt.xkc.historytoday.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bupt.xkc.historytoday.R;
import com.bupt.xkc.historytoday.callbacks.LoadImageCallback;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.HintMessage;
import com.bupt.xkc.historytoday.utils.SysApplicationManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by xkc on 4/7/16.
 */
public class BigImageActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = BigImageActivity.class.getSimpleName();
    private PhotoView photoView;
    private Intent intent;
    private String imageUrl;

    private FloatingActionButton fab_big_image;
    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        SysApplicationManager.getInstance().addActivity(this);

        initOperation();
        

    }


    private void initOperation() {
        photoView = (PhotoView) findViewById(R.id.pv_big_image);
        fab_big_image = (FloatingActionButton) findViewById(R.id.fab_big_image);

        intent = getIntent();
        Bundle bundle = intent.getExtras();
        imageUrl = bundle.getString("imageUrl");

        if (imageUrl != null) {
            Picasso.with(BigImageActivity.this)
                    .load(imageUrl)
                    .into(photoView);
        }

        attacher = new PhotoViewAttacher(photoView);
        fab_big_image.setOnClickListener(this);


        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_big_image:
                MobclickAgent.onEvent(this,"PicDownload");

                downLoadPic(new LoadImageCallback() {
                    @Override
                    public void onImageLoadFinish(Exception e, Object o) {
                        if (e == null) {
                            File picFile = (File) o;
                            //将图片插入到图库并通知图库更新
                            updateGallery(picFile);

                            Snackbar.make(fab_big_image,HintMessage.IMAGE_DOWNLOAD_FINISH,
                                    Snackbar.LENGTH_LONG)
                                    .setAction("查看", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //TODO：Action事件为空，点击Snackbar，自身消失
                                            fab_big_image.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                    .show();
                        }

                    }
                });
                break;
        }
    }



    //更新系统图库
    private void updateGallery(File picFile) {
        //把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    picFile.getAbsolutePath(), picFile.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + picFile.getAbsolutePath())));
    }

    //将图片下载到本地
    private void downLoadPic(final LoadImageCallback callback) {
        if (imageUrl != null) {
            Picasso.with(BigImageActivity.this)
                    .load(imageUrl)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded( final Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                   saveBitmapToLocal(bitmap,imageUrl,callback);
                                }
                            }).start();
                        }

                        @Override
                        public void onBitmapFailed(Drawable drawable) {
                            Snackbar.make(fab_big_image, HintMessage.IMAGE_DOWNLOAD_ERROR,
                                    Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable drawable) {

                        }
                    });
        }
    }


    /**
     * 将图片保存到本地
     * eg:imageUrl = http://images.juheapi.com/history/4364_1.jpg
     */
    public void saveBitmapToLocal(Bitmap bitmap, String imageUrl,LoadImageCallback callback){
        File dir = new File(APIs.LOCAL.IMAGE_DIR);//建立图片文件夹目录
        if (!dir.exists()){
            dir.mkdirs();
        }
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));//4364_1.jpg
        File file = new File(dir,fileName);
        if (file.exists()){
            Snackbar.make(fab_big_image,HintMessage.IMAGE_EXIST,Snackbar.LENGTH_LONG).show();
        }else {
            FileOutputStream out = null;
            try {
                file.createNewFile();
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){
                    out.flush();
                    out.close();
                }

                callback.onImageLoadFinish(null,file);

            } catch (Exception e) {
                Log.e(LOG_TAG, "====>saveBitmapToLocal exception!");
                callback.onImageLoadFinish(e,e.getMessage());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
