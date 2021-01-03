package com.netease.nim.uikit.business.session.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.hbzhou.open.flowcamera.CustomCameraView;
import com.hbzhou.open.flowcamera.listener.FlowCameraListener;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.helper.VideoMessageHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.util.C;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.otaliastudios.cameraview.controls.Hdr;
import com.otaliastudios.cameraview.controls.WhiteBalance;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.hbzhou.open.flowcamera.CustomCameraView.BUTTON_STATE_BOTH;

public class VideoActivity extends AppCompatActivity {
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private JCameraView jCameraView;
    private CustomCameraView flowCamera;
    private boolean granted = false;
    private static VideoMessageHelper.VideoMessageHelperListener listener;
    private static ImageListener imageListener;

    public static void setImageListener(ImageListener imageListener) {
        VideoActivity.imageListener = imageListener;
    }

    public interface ImageListener {
        void onImage(File file);
    }

    public static void setListener(VideoMessageHelper.VideoMessageHelperListener listener) {
        VideoActivity.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        flowCamera= (CustomCameraView) findViewById(R.id.customCamera);
        // 绑定生命周期 您就不用关心Camera的开启和关闭了 不绑定无法预览
        flowCamera.setBindToLifecycle(this);
        // 设置白平衡模式
        flowCamera.setWhiteBalance(WhiteBalance.AUTO);
        // 设置只支持单独拍照拍视频还是都支持
        // BUTTON_STATE_ONLY_CAPTURE  BUTTON_STATE_ONLY_RECORDER  BUTTON_STATE_BOTH
        flowCamera.setCaptureMode(BUTTON_STATE_BOTH);
        // 开启HDR
        flowCamera.setHdrEnable(Hdr.ON);
        // 设置最大可拍摄小视频时长
        flowCamera.setRecordVideoMaxTime(10);
        // 设置拍照或拍视频回调监听
        flowCamera.setFlowCameraListener(new  FlowCameraListener() {
            @Override
            public void captureSuccess(@NonNull File file) {
                if (file != null && imageListener != null) {
                    imageListener.onImage(file);
                }
                finish();
            }

            @Override
            public void recordSuccess(@NonNull File file) {
                if (listener != null) {
                    String md5 = MD5.getStreamMD5(file.getAbsolutePath());
                    String filename = md5 + "." + FileUtil.getExtensionName(file.getAbsolutePath());
                    String md5Path = StorageUtil.getWritePath(filename, StorageType.TYPE_VIDEO);
                    if (AttachmentStore.copy(file.getAbsolutePath(), md5Path) != -1) {
                        if (listener != null) {
                            listener.onVideoPicked(new File(md5Path), md5);
                        }
                    } else {
                        ToastHelper.showToast(VideoActivity.this, R.string.video_exception);
                    }
                    finish();
                }
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {

            }
            // 录制完成视频文件返回
//            override fun recordSuccess(File file ) {
//                ToastUtils.showLong(file.absolutePath);
//                finish()
//            }
//            // 操作拍照或录视频出错
//            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
//
//            }
//            // 拍照返回
//            override fun captureSuccess(file: File) {
//                ToastUtils.showLong(file.absolutePath)
//                finish()
//            }
        });
        //左边按钮点击事件
        flowCamera.setLeftClickListener(this::finish);

//        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
////设置视频质量
//        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
//        //设置视频保存路径
//        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
//        //JCameraView监听
//        jCameraView.setJCameraLisenter(new JCameraListener() {
//            @Override
//            public void captureSuccess(Bitmap bitmap) {
//                //获取图片bitmap
//                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
//                File file = saveBitmapFile(bitmap);
//                if (file != null && imageListener != null) {
//                    imageListener.onImage(file);
//                }
//                finish();
//            }
//
//            @Override
//            public void recordSuccess(String filePath, Bitmap firstFrame) {
//                //获取视频路径
//                Log.i("CJT", "url = " + filePath);
//                if (listener != null) {
//                    String md5 = MD5.getStreamMD5(filePath);
//                    String filename = md5 + "." + FileUtil.getExtensionName(filePath);
//                    String md5Path = StorageUtil.getWritePath(filename, StorageType.TYPE_VIDEO);
//                    if (AttachmentStore.copy(filePath, md5Path) != -1) {
//                        if (listener != null) {
//                            listener.onVideoPicked(new File(md5Path), md5);
//                        }
//                    } else {
//                        ToastHelper.showToast(VideoActivity.this, R.string.video_exception);
//                    }
//                    finish();
//                }
//            }
//        });
//        //左边按钮点击事件
//        jCameraView.setLeftClickListener(this::finish);
        //6.0动态权限获取
        getPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (granted) {
//            jCameraView.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  jCameraView.onPause();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
                granted = true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
                granted = false;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                 //   jCameraView.onResume();
                } else {
                    ToastHelper.showToast(this, "请到设置-权限管理中开启");
                    finish();
                }
            }
        }
    }

    public File saveBitmapFile(Bitmap bitmap) {
        String imagePath = StorageUtil.getWritePath(this, StringUtil.get36UUID() + C.FileSuffix.PNG,
                StorageType.TYPE_IMAGE);
        File file = new File(imagePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return file;
        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }
    }
}
