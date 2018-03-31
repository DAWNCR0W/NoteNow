package com.branch.www.screencapture.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.branch.www.screencapture.GlobalScreenShot;
import com.branch.www.screencapture.R;
import com.branch.www.screencapture.ScreenCaptureApplication;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PaintActivity extends Activity implements GlobalScreenShot.onScreenShotListener {

    PhotoEditorView mPhotoEditorView;
    PhotoEditor photoEditor;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PaintActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        mPhotoEditorView = findViewById(R.id.photoEditorView);

        photoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        photoEditor.setBrushDrawingMode(true);

        Bitmap bitmap = ((ScreenCaptureApplication) getApplication()).getmScreenCaptureBitmap();

        GlobalScreenShot screenshot = new GlobalScreenShot(getApplicationContext());

        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        mPhotoEditorView.setVisibility(View.GONE);

        if (bitmap != null) {
            screenshot.takeScreenshot(bitmap, this);
        }
    }

    @Override
    public void onStartShot() {

    }

    @Override
    public void onFinishShot(boolean success) {
        mPhotoEditorView.setVisibility(View.VISIBLE);
    }
}
