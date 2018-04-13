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
import android.widget.Toast;

import com.branch.www.screencapture.R;
import com.branch.www.screencapture.service.FloatWindows;

public class MainActivity extends FragmentActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 18;
    public static final int REQUEST = 112;

    @Override
    protected void onResume() {
        super.onResume();

        requestCapturePermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    i.putExtra("Selected color", "blue");
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

}
