package com.branch.www.screencapture.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.branch.www.screencapture.ChangeViewColor;
import com.branch.www.screencapture.R;
import com.branch.www.screencapture.service.FloatWindows;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import es.dmoral.toasty.Toasty;

public class MainActivity extends FragmentActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 18;
    public static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FloatWindows.noteBtnColor = R.drawable.ic_create_white_24dp;
//        FloatWindows.exitBtnColor = R.drawable.ic_cancel_white_24dp;
//        FloatWindows.undoBtnColor = R.drawable.ic_undo_white_24dp;
//        FloatWindows.settingBtnColor = R.drawable.ic_settings_white_24dp;
    }

    public void requestCapturePermission() {
        //스크린샷에 필요한 권한 요청

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasReadWritePermissions(MainActivity.this, PERMISSIONS)) {
            Toast.makeText(this, "앱 실행에 필요한 권한을 허용해 주세요!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST);
        } else {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 123);
            } else {
                if (mediaProjectionManager != null) {
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                            REQUEST_MEDIA_PROJECTION);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //서비스를 실행하여 백그라운드로 실행
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    FloatWindows.setResultData(data);
                    Intent i = new Intent(MainActivity.this, FloatWindows.class);
                    startService(i);
                    finish();
                }
                break;
        }

    }

    private static boolean hasReadWritePermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void StartServiceBtnClicked(View view) {
        requestCapturePermission();
    }

    public void changeColorBtnClicked(View view) {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("버튼 색상을 선택하세요")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {
                        FloatWindows.buttonTintColor = i;
                        Toasty.custom(MainActivity.this,
                                "색상이 변경되었습니다",
                                getResources().getDrawable(R.drawable.check, null),
                                i,
                                Toast.LENGTH_SHORT, false, true).show();
                    }
                })
                .setPositiveButton("확인", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .build()
                .show();
    }
}
