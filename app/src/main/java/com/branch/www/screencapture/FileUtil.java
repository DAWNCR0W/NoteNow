package com.branch.www.screencapture;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ryze on 2016-5-26.
 */
public class FileUtil {

    private static final String SCREENCAPTURE_PATH = "ScreenCapture" + File.separator + "Screenshots" + File.separator;

    private static final String SCREENSHOT_NAME = "Screenshot";

    private static String getAppPath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            return Environment.getExternalStorageDirectory().toString();

        } else {

            return context.getFilesDir().toString();

        }

    }

    private static String getScreenShots(Context context) {

        StringBuilder stringBuffer = new StringBuilder(getAppPath(context));
        stringBuffer.append(File.separator);

        stringBuffer.append(SCREENCAPTURE_PATH);

        File file = new File(stringBuffer.toString());

        if (!file.exists()) {
            file.mkdirs();
        }

        return stringBuffer.toString();

    }

    public static String getScreenShotsName(Context context) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

        String date = simpleDateFormat.format(new Date());

        return getScreenShots(context) + SCREENSHOT_NAME +
                "_" +
                date +
                ".png";

    }


}
