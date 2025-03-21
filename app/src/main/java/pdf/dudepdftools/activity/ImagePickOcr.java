package pdf.dudepdftools.activity;

import static pdf.dudepdftools.util.Constants.AUTHORITY_APP;
import static pdf.dudepdftools.util.Constants.READ_WRITE_CAMERA_PERMISSIONS;
import static pdf.dudepdftools.util.Constants.RESULT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;
import com.templatemela.camscanner.activity.ImageToTextActivity;
import com.templatemela.camscanner.main_utils.Constant;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import pdf.dudepdftools.R;
import pdf.dudepdftools.util.MorphButtonUtility;
import pdf.dudepdftools.util.PermissionsUtils;
import pdf.dudepdftools.util.StringUtils;

public class ImagePickOcr extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;


    private ImageView iv_back;
    private MorphingButton addImages;
    private MorphingButton findText;
    private TextView tvNoOfImages;

    private MorphButtonUtility mMorphButtonUtility;
    private boolean mPermissionGranted;
    private boolean mIsButtonAlreadyClicked;
    private ArrayList<String> mImagesUri = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick_ocr);
        iv_back = findViewById(R.id.iv_back);
        addImages = findViewById(R.id.addImages);
        findText = findViewById(R.id.findText);
        tvNoOfImages = findViewById(R.id.tvNoOfImages);
        iv_back.setOnClickListener(this);
        addImages.setOnClickListener(this);
        findText.setOnClickListener(this);
        mMorphButtonUtility = new MorphButtonUtility(this);
        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_back:
                onBackPressed();
                return;
            case R.id.addImages:
                if (!mPermissionGranted) {
                    getRuntimePermissions();
                    return;
                }
                if (!mIsButtonAlreadyClicked) {
                    selectImages();
                    mIsButtonAlreadyClicked = true;
                }
                return;
            case R.id.findText:
                Constant.original = BitmapFactory.decodeFile(mImagesUri.get(0));
                Intent intent = new Intent(this, ImageToTextActivity.class);
                startActivity(intent);
                return;
            default:
                return;
        }
    }


    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                READ_WRITE_CAMERA_PERMISSIONS,
                REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length < 1)
            return;

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                selectImages();
                StringUtils.getInstance().showSnackbar(ImagePickOcr.this, R.string.snackbar_permissions_given);
            } else
                StringUtils.getInstance().showSnackbar(ImagePickOcr.this, R.string.snackbar_insufficient_permissions);
        }
    }

    private void selectImages() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, AUTHORITY_APP))
                .maxSelectable(1)
                .imageEngine(new PicassoEngine())
                .forResult(INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                if (mImagesUri.size() > 0) {
                    tvNoOfImages.setText(String.format(getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                    tvNoOfImages.setVisibility(View.VISIBLE);
                    findText.setEnabled(true);
                    findText.unblockTouch();
                }
                mMorphButtonUtility.morphToSquare(findText, mMorphButtonUtility.integer());
                break;
        }
    }


}