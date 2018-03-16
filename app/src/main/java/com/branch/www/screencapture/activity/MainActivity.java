package com.branch.www.screencapture.activity;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.branch.www.screencapture.service.FloatWindowsService;
import com.branch.www.screencapture.R;

public class MainActivity extends FragmentActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestCapturePermission();
    }

    public void requestCapturePermission() {
        //스크린샷에 필요한 권한 요청
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        assert mediaProjectionManager != null;
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //서비스를 실행하여 백그라운드로 실행
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:

                if (resultCode == RESULT_OK && data != null) {
                    FloatWindowsService.setResultData(data);
                    startService(new Intent(getApplicationContext(), FloatWindowsService.class));
                }
                break;
        }

    }

}
