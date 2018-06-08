package com.branch.www.screencapture;

/**
 * @author dawncrow
 */
public class StaticResource {
    public static final int READ_WRITE_STORAGE = 52;
    public static final int REQUEST_MEDIA_PROJECTION = 18;
    public static final int REQUEST = 112;
    public static final int CAMERA_REQUEST = 52;
    public static final int PICK_REQUEST = 53;

    static final int SCREENSHOT_FLASH_TO_PEAK_DURATION = 130;
    static final int SCREENSHOT_DROP_IN_DURATION = 430;
    static final int SCREENSHOT_DROP_OUT_DELAY = 500;
    static final int SCREENSHOT_DROP_OUT_DURATION = 430;
    static final int SCREENSHOT_DROP_OUT_SCALE_DURATION = 370;
    static final float BACKGROUND_ALPHA = 0.5f;
    static final float SCREENSHOT_SCALE = 1f;
    static final float SCREENSHOT_DROP_IN_MIN_SCALE = SCREENSHOT_SCALE * 0.725f;
    static final float SCREENSHOT_DROP_OUT_MIN_SCALE = SCREENSHOT_SCALE * 0.45f;
    static final float SCREENSHOT_DROP_OUT_MIN_SCALE_OFFSET = 0f;
}
