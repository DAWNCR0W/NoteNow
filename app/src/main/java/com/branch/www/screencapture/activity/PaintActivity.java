package com.branch.www.screencapture.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.branch.www.screencapture.FileUtil;
import com.branch.www.screencapture.GlobalScreenShot;
import com.branch.www.screencapture.R;
import com.branch.www.screencapture.ScreenCaptureApplication;
import com.branch.www.screencapture.fragment.EmojiFragment;
import com.branch.www.screencapture.fragment.PropertiesFragment;
import com.branch.www.screencapture.fragment.StickerFragment;
import com.branch.www.screencapture.fragment.TextEditorDialogFragment;
import com.branch.www.screencapture.service.FloatWindows;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import static com.branch.www.screencapture.StaticResource.CAMERA_REQUEST;
import static com.branch.www.screencapture.StaticResource.PICK_REQUEST;
import static com.branch.www.screencapture.StaticResource.READ_WRITE_STORAGE;

/**
 * @author dawncrow
 */
public class PaintActivity extends BaseActivity implements GlobalScreenShot.onScreenShotListener,
        OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesFragment.Properties,
        EmojiFragment.EmojiListener,
        StickerFragment.StickerListener {

    private static final String TAG = PaintActivity.class.getSimpleName();
    PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;
    private PropertiesFragment mPropertiesFragment;
    private EmojiFragment mEmojiFragment;
    private StickerFragment mStickerFragment;
    private TextView mTxtCurrentTool;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PaintActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(PaintActivity.this, FloatWindows.class));
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);
        mPhotoEditorView = findViewById(R.id.photoEditorView);

        initViews();

        mPropertiesFragment = new PropertiesFragment();
        mEmojiFragment = new EmojiFragment();
        mStickerFragment = new StickerFragment();
        mStickerFragment.setStickerListener(this);
        mEmojiFragment.setEmojiListener(this);
        mPropertiesFragment.setPropertiesChangeListener(this);

        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");


        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        mPhotoEditor.setBrushDrawingMode(true);

        Bitmap bitmap = ((ScreenCaptureApplication) getApplication()).getmScreenCaptureBitmap();

        GlobalScreenShot screenshot = new GlobalScreenShot(PaintActivity.this);

        if (bitmap != null) {
            screenshot.takeScreenshot(bitmap, this);
        }

        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        mPhotoEditorView.setVisibility(View.GONE);
    }

    @Override
    public void onStartShot() {
    }

    @Override
    public void onFinishShot(boolean success) {
        mPhotoEditorView.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        ImageView imgPencil;
        ImageView imgEraser;
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgText;
        ImageView imgSticker;
        ImageView imgEmo;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);

        imgEmo = findViewById(R.id.imgEmoji);
        imgEmo.setOnClickListener(this);

        imgSticker = findViewById(R.id.imgSticker);
        imgSticker.setOnClickListener(this);

        imgPencil = findViewById(R.id.imgPencil);
        imgPencil.setOnClickListener(this);

        imgText = findViewById(R.id.imgText);
        imgText.setOnClickListener(this);

        imgEraser = findViewById(R.id.btnEraser);
        imgEraser.setOnClickListener(this);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPencil:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesFragment.show(getSupportFragmentManager(), mPropertiesFragment.getTag());
                break;
            case R.id.btnEraser:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case R.id.imgText:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;
            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;
            case R.id.imgSave:
                saveImage();
                break;
            case R.id.imgClose:
                if (!mPhotoEditor.isCacheEmpty()) {
                    showSaveDialog();
                } else {
                    finishAffinity();
                }
                break;
            case R.id.imgSticker:
                mStickerFragment.show(getSupportFragmentManager(), mStickerFragment.getTag());
                break;
            case R.id.imgEmoji:
                mEmojiFragment.show(getSupportFragmentManager(), mEmojiFragment.getTag());
                break;
            default:
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading();
            File file = new File(FileUtil.getEditedScreencapture(PaintActivity.this));
            try {
                file.createNewFile();
                mPhotoEditor.saveImage(file.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("저장되었습니다");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("저장에 실패하였습니다");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        } else {
            Toasty.error(this, "권한이 없습니다. 앱을 다시 실행시켜 권한을 설정해 주세요!").show();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("저장하지 않고 종료 하시겠습니까?");
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("저장하지 않고 종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        startService(new Intent(PaintActivity.this, FloatWindows.class));
    }
}
