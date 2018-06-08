package com.branch.www.screencapture;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author dawncrow
 * @date 2016-5-26
 */
public class FileUtil {

    private static final String SCREENCAPTURE_PATH = "NoteNowScreenShot" + File.separator + "Screenshots" + File.separator;
    private static final String EDITED_SCREENCAPTURE_PATH = "NoteNowScreenShot" + File.separator + "Edited" + File.separator;
    private static final String SCREENSHOT_NAME = "NoteNow";

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

    private static String getEditedScreenShots(Context context) {
        StringBuilder stringBuffer = new StringBuilder(getAppPath(context));
        stringBuffer.append(File.separator);
        stringBuffer.append(EDITED_SCREENCAPTURE_PATH);
        File file = new File(stringBuffer.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return stringBuffer.toString();
    }

    public static String getScreenShotsName(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format), Locale.KOREA);
        String date = simpleDateFormat.format(new Date());
        return getScreenShots(context) + SCREENSHOT_NAME +
                "_" +
                date +
                ".png";
    }

    public static String getEditedScreencapture(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format), Locale.KOREA);
        String date = simpleDateFormat.format(new Date());
        return getEditedScreenShots(context) + SCREENSHOT_NAME +
                "_" +
                date +
                ".png";
    }

}
