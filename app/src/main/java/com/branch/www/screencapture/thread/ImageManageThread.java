package com.branch.www.screencapture.thread;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.view.View;

import com.branch.www.screencapture.FileUtil;
import com.branch.www.screencapture.ScreenCaptureApplication;
import com.branch.www.screencapture.activity.MainActivity;
import com.branch.www.screencapture.activity.PaintActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageManageThread extends Thread {
    private Image[] params;
    private Context context;
    private View noteBtn;

    public ImageManageThread(View noteBtn, Context context, Image... params) {
        this.params = params;
        this.context = context;
        this.noteBtn = noteBtn;
    }

    @Override
    public void run() throws NullPointerException {

        Image image = params[0];

        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        File fileImage = null;
        if (bitmap != null) {
            try {
                fileImage = new File(FileUtil.getScreenShotsName(context));
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(fileImage);
                media.setData(contentUri);
                context.sendBroadcast(media);
            } catch (IOException e) {
                e.printStackTrace();
                fileImage = null;
            }
        }

        final File finalFileImage = fileImage;
        final Bitmap finalBitmap = bitmap;
        noteBtn.post(new Runnable() {
            @Override
            public void run() {
                if (finalFileImage != null) {
                    ((ScreenCaptureApplication) MainActivity.application).setmScreenCaptureBitmap(finalBitmap);
                    context.startActivity(PaintActivity.newIntent(context));

                    noteBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
