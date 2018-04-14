package com.branch.www.screencapture.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.branch.www.screencapture.R;
import com.branch.www.screencapture.service.FloatWindows;

public class MainActivity extends FragmentActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 18;
    public static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatWindows.noteBtnColor = R.drawable.ic_create_white_24dp;
        FloatWindows.exitBtnColor = R.drawable.ic_cancel_white_24dp;
        FloatWindows.undoBtnColor = R.drawable.ic_undo_white_24dp;
        FloatWindows.settingBtnColor = R.drawable.ic_settings_white_24dp;
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

    public void WhiteBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_white_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_white_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_white_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_white_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_white_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_white_24dp;
            Toast.makeText(this, "버튼의 색상이 하양으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 하양입니다", Toast.LENGTH_SHORT).show();
    }

    public void BlackBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_black_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_black_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_black_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_black_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_black_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_black_24dp;
            Toast.makeText(this, "버튼의 색상이 검정으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 검정입니다", Toast.LENGTH_SHORT).show();
    }

    public void GrinBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_grin_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_grin_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_grin_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_grin_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_grin_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_grin_24dp;
            Toast.makeText(this, "버튼의 색상이 초록으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 초록입니다", Toast.LENGTH_SHORT).show();
    }

    public void BlueBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_blue_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_blue_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_blue_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_blue_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_blue_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_blue_24dp;
            Toast.makeText(this, "버튼의 색상이 파랑으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 파랑입니다", Toast.LENGTH_SHORT).show();
    }

    public void PinkBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_pink_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_pink_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_pink_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_pink_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_pink_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_pink_24dp;
            Toast.makeText(this, "버튼의 색상이 분홍으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 분홍입니다", Toast.LENGTH_SHORT).show();
    }

    public void YellowBtnClicked(View view) {
        if (FloatWindows.captureBtnColor != R.drawable.ic_camera_alt_yellow_24dp) {
            FloatWindows.captureBtnColor = R.drawable.ic_camera_alt_yellow_24dp;
            FloatWindows.exitBtnColor = R.drawable.ic_cancel_yellow_24dp;
            FloatWindows.undoBtnColor = R.drawable.ic_undo_yellow_24dp;
            FloatWindows.noteBtnColor = R.drawable.ic_create_yellow_24dp;
            FloatWindows.settingBtnColor = R.drawable.ic_settings_yellow_24dp;
            Toast.makeText(this, "버튼의 색상이 노랑으로 변경되었습니다", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "버튼의 색상이 이미 노랑입니다", Toast.LENGTH_SHORT).show();
    }

    public void StartServiceBtnClicked(View view) {
        requestCapturePermission();
    }
}
