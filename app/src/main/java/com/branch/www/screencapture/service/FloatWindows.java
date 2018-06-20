package com.branch.www.screencapture.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.branch.www.screencapture.R;
import com.branch.www.screencapture.StaticResource;
import com.branch.www.screencapture.activity.MainActivity;
import com.branch.www.screencapture.thread.ImageManage;

/**
 * @author branch
 * @date 2016-5-25
 * <p>
 * 启动悬浮窗界面
 */
public class FloatWindows extends Service {

    public static int buttonTintColor = Color.parseColor("#ffffff");

    private static Intent mResultData = null;
    private boolean noteBtnMoving = false;
    private float offsetX, offsetY;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private Button noteBtn, captureBtn, exitBtn, settingBtn;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private float systemWidthDpi;
    private int[] noteBtnPos = new int[2];
    private boolean noteBtnClicked = false;
    private int btnSize;
    private int btnDividerSize;

    private Handler handler = new Handler();

    private Runnable runDeleteBtn = new Runnable() {
        @Override
        public void run() {
            try {
                mWindowManager.removeView(captureBtn);
                mWindowManager.removeView(exitBtn);
                mWindowManager.removeView(settingBtn);
            } catch (IllegalArgumentException ignored) {
            }
            noteBtn.setBackgroundResource(R.drawable.ic_create_white_24dp);
            noteBtnClicked = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        captureBtn = new Button(FloatWindows.this);

        exitBtn = new Button(FloatWindows.this);

        settingBtn = new Button(FloatWindows.this);

        getSizes();

        setOnClickListenersOnBtn();

        createFloatView();

        createImageReader();
    }

    private void setOnClickListenersOnBtn() {
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runDeleteBtn);
                mWindowManager.removeView(captureBtn);
                mWindowManager.removeView(exitBtn);
                mWindowManager.removeView(settingBtn);
                noteBtn.setVisibility(View.GONE);
                noteBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        noteBtn.setBackgroundResource(R.drawable.ic_create_white_24dp);
                        noteBtn.setVisibility(View.VISIBLE);
                    }
                }, 1000);
                noteBtnClicked = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startScreenShot();
                    }
                }, 50);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runDeleteBtn);
                mWindowManager.removeView(captureBtn);
                mWindowManager.removeView(exitBtn);
                mWindowManager.removeView(settingBtn);
                stopService(new Intent(FloatWindows.this, FloatWindows.class));
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runDeleteBtn);
                mWindowManager.removeView(captureBtn);
                mWindowManager.removeView(exitBtn);
                mWindowManager.removeView(settingBtn);
                mWindowManager.removeView(noteBtn);
                startActivity(new Intent(FloatWindows.this, MainActivity.class));
                stopSelf();
            }
        });
    }

    private void getSizes() {
        final DisplayMetrics displayMetrics = FloatWindows.this.getResources().getDisplayMetrics();
        systemWidthDpi = displayMetrics.widthPixels / displayMetrics.density;
        btnSize = (int) (systemWidthDpi * StaticResource.BUTTON_CRITERIA);
        btnDividerSize = (int) (systemWidthDpi * StaticResource.BUTTON_DIVIDER_CRITERIA);
    }

    public static void setResultData(Intent mResultData) {
        FloatWindows.mResultData = mResultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {

        NoteBtnListener noteBtnListener = new NoteBtnListener();

        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.x = mScreenWidth;
        mLayoutParams.y = btnSize;
        mLayoutParams.width = btnSize;
        mLayoutParams.height = btnSize;

        noteBtn = new Button(FloatWindows.this);
        noteBtn.setBackgroundResource(R.drawable.ic_create_white_24dp);

        mWindowManager.addView(noteBtn, mLayoutParams);

        noteBtn.setOnTouchListener(noteBtnListener);
        noteBtn.setOnClickListener(noteBtnListener);

        setBackgroundColorTint();
    }

    private void setBackgroundColorTint() {
        noteBtn.setBackgroundTintList(ColorStateList.valueOf(buttonTintColor));
        captureBtn.setBackgroundTintList(ColorStateList.valueOf(buttonTintColor));
        exitBtn.setBackgroundTintList(ColorStateList.valueOf(buttonTintColor));
        settingBtn.setBackgroundTintList(ColorStateList.valueOf(buttonTintColor));
    }

    private class NoteBtnListener implements View.OnClickListener, View.OnTouchListener {
        @Override
        public void onClick(View view) {
            noteBtn.getLocationOnScreen(noteBtnPos);
            if (!noteBtnClicked) {
                noteBtnClicked = true;
                captureBtn.setBackgroundResource(R.drawable.ic_camera_alt_white_24dp);
                exitBtn.setBackgroundResource(R.drawable.ic_cancel_white_24dp);
                settingBtn.setBackgroundResource(R.drawable.ic_settings_white_24dp);
                createBtns();
            } else {
                handler.removeCallbacks(runDeleteBtn);
                mWindowManager.removeView(captureBtn);
                mWindowManager.removeView(exitBtn);
                mWindowManager.removeView(settingBtn);
                settingBtn.setBackgroundResource(R.drawable.ic_settings_white_24dp);
                noteBtn.setBackgroundResource(R.drawable.ic_create_white_24dp);
                noteBtnClicked = false;
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY();

                noteBtnMoving = false;

                noteBtn.getLocationOnScreen(noteBtnPos);

                offsetX = noteBtnPos[0] - x;
                offsetY = noteBtnPos[1] - y;

            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && !noteBtnClicked) {

                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY() - systemWidthDpi * StaticResource.BUTTON_TOUCH_X;

                WindowManager.LayoutParams params = (WindowManager.LayoutParams) noteBtn.getLayoutParams();

                int newX = (int) (offsetX + x);
                int newY = (int) (offsetY + y) - (int) (systemWidthDpi * StaticResource.BUTTON_TOUCH_X);

                if (Math.abs(newX - noteBtnPos[0]) < 1 && Math.abs(newY - noteBtnPos[1]) < 1 && !noteBtnMoving) {
                    return false;
                }

                params.x = newX;
                params.y = newY - btnDividerSize;

                mWindowManager.updateViewLayout(noteBtn, params);
                noteBtnMoving = true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                return noteBtnMoving;
            }
            return false;
        }

        /**
         * 기기의 가로 길이를 구하고 반으로 나눈 뒤, 현재 버튼의 위치에 따라 생성 위치를 결정
         */
        private void createBtns() {
            if (systemWidthDpi > noteBtnPos[0]) {

                noteBtn.setBackgroundResource(R.drawable.ic_undo_white_24dp);

                mLayoutParams.x = noteBtnPos[0] + btnSize;
                mLayoutParams.y = noteBtnPos[1] - btnDividerSize;

                mLayoutParams.width = btnSize;
                mLayoutParams.height = btnSize;

                mWindowManager.addView(captureBtn, mLayoutParams);
                mLayoutParams.x = noteBtnPos[0] + btnSize * 2;

                mWindowManager.addView(settingBtn, mLayoutParams);
                mLayoutParams.x = noteBtnPos[0] + btnSize * 3;

                mWindowManager.addView(exitBtn, mLayoutParams);

                handler.removeCallbacks(runDeleteBtn);
                handler.postDelayed(runDeleteBtn, 4000);
            } else {

                noteBtn.setBackgroundResource(R.drawable.ic_undo_white_24dp);

                mLayoutParams.x = noteBtnPos[0] - btnSize;
                mLayoutParams.y = noteBtnPos[1] - btnDividerSize;

                mLayoutParams.width = btnSize;
                mLayoutParams.height = btnSize;

                mWindowManager.addView(captureBtn, mLayoutParams);
                mLayoutParams.x = noteBtnPos[0] - btnSize * 2;

                mWindowManager.addView(settingBtn, mLayoutParams);
                mLayoutParams.x = noteBtnPos[0] - btnSize * 3;

                mWindowManager.addView(exitBtn, mLayoutParams);

                handler.removeCallbacks(runDeleteBtn);
                handler.postDelayed(runDeleteBtn, 4000);
            }
        }
    }

    private void startScreenShot() {

        noteBtn.setVisibility(View.GONE);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //start virtual
                startVirtual();
            }
        }, 5);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //capture the screen
                startCapture();
            }
        }, 30);
    }

    private void createImageReader() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        if (mResultData == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();

        if (image == null) {
            startScreenShot();
        } else {
            new ImageManage(getApplication(), FloatWindows.this, noteBtn, image).run();
        }
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mWindowManager.removeView(noteBtn);
        } catch (Exception ignored) {
        }
        stopVirtual();
        tearDownMediaProjection();
    }
}
