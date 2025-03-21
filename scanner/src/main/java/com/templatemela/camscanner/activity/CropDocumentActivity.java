package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

//import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.scanlibrary.ScanActivity;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.db.DBHelper;
import com.templatemela.camscanner.main_utils.AdjustUtil;
import com.templatemela.camscanner.main_utils.BitmapUtils;
import com.templatemela.camscanner.main_utils.Constant;
import com.templatemela.camscanner.models.DBModel;
import com.templatemela.camscanner.utils.AdsUtils;
import com.templatemela.camscanner.utils.VerticalSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import me.pqpo.smartcropperlib.view.CropImageView;

//perform operation on bitmap using a scanLibrary
//ScanActivity.getBWBitmap() -> ocv
//ScanActivity.getMagicColorBitmap -> color
//ScanActivity.getGrayBitmap() -> sharp black
public class CropDocumentActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "CropDocumentActivity";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.IdentifyActivity.equals("DocumentEditorActivity_Crop")) {
                //when user click on edit button of the actionbar
                Intent intent2 = new Intent(CropDocumentActivity.this, DocumentEditorActivity.class);
                intent2.putExtra("TAG", "SavedDocumentActivity");
                intent2.putExtra("scan_doc_group_name", selected_group_name);
                intent2.putExtra("current_doc_name", current_docs_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("CurrentFilterActivity")) {
                startActivity(new Intent(CropDocumentActivity.this, CurrentFilterActivity.class));
                Constant.IdentifyActivity = "";
                finish();
            } else if (Constant.IdentifyActivity.equals("ScannerActivity_Retake")) {
                //retake
                startActivity(new Intent(CropDocumentActivity.this, ScannerActivity.class));
                Constant.IdentifyActivity = "";
                finish();
            }
        }
    };


    public String current_docs_name;

    public DBHelper dbHelper;
    protected ImageView iv_back;
    protected TextView iv_Rotate_Doc;
    protected ImageView iv_edit;
    protected ImageView iv_done;
    protected ImageView iv_full_crop;
    private CropImageView iv_preview_crop;
    protected TextView iv_retake;
    protected LinearLayout ly_current_filter;
    protected LinearLayout ly_rotate_doc;

    public String selected_group_name;
//    private AdView adView;


    private TextView iv_ocv_black;
    private TextView iv_original;
    private TextView iv_color;
    private TextView iv_sharp_black;


    //store the bitmap after performing the operation on it(on selected -> original/ ocv/ sharp/color)
    private Bitmap tempBitmap;

    //store the original bitmap came from previous activity
    public Bitmap original;
    private ProgressDialog progressDialog;

    private ImageView iv_add_new_scan;
    private SeekBar seekBarBrightness;

    private boolean fromGallery;
    private LinearLayout cropview_navigation;
    private Button save_btn;
    private Button rotateButton;
    private ImageView previousImageButton;
    private TextView imagecount;
    private ImageView nextimageButton;
    private LinearLayout retake_rotate;
    private ArrayList<Bitmap> cropBitmap;

    private int  imageIndex = 0;
    private boolean mCurrentImageEdited = false;
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".DocumentEditorActivity_Crop"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".CurrentFilterActivity"));
        registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + ".ScannerActivity_Retake"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_crop_document);
        dbHelper = new DBHelper(this);
        init();
    }

    private void init() {
//        adView = findViewById(R.id.adView);
//        iv_add_new_scan = (ImageView) findViewById(R.id.iv_add_new_scan);
        seekBarBrightness = (SeekBar) findViewById(R.id.seekBarBrightness);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_done = (ImageView) findViewById(R.id.iv_done);
        iv_preview_crop = (CropImageView) findViewById(R.id.iv_preview_crop);
        iv_full_crop = (ImageView) findViewById(R.id.iv_full_crop);
        ly_rotate_doc = (LinearLayout) findViewById(R.id.ly_rotate_doc);
        ly_current_filter = (LinearLayout) findViewById(R.id.ly_current_filter);
        iv_retake = (TextView) findViewById(R.id.iv_retake);
        iv_Rotate_Doc = (TextView) findViewById(R.id.iv_Rotate_Doc);
        iv_original = (TextView) findViewById(R.id.iv_original);
        iv_color = (TextView) findViewById(R.id.iv_color);
        iv_sharp_black = (TextView) findViewById(R.id.iv_sharp_black);
        iv_ocv_black = (TextView) findViewById(R.id.iv_ocv_black);

        cropview_navigation = findViewById(R.id.cropview_navigation);
        save_btn = findViewById(R.id.save_btn);
        rotateButton = findViewById(R.id.rotateButton);
        previousImageButton = findViewById(R.id.previousImageButton);
        imagecount = findViewById(R.id.imagecount);
        nextimageButton = findViewById(R.id.nextimageButton);
        retake_rotate = findViewById(R.id.retake_rotate);
        fromGallery = getIntent().getBooleanExtra("fromGallery",false);


        if(fromGallery){
            retake_rotate.setVisibility(View.GONE);
            cropview_navigation.setVisibility(View.VISIBLE);
            cropBitmap = Constant.multipleScan;
            if(cropBitmap.size() == 0){
                finish();
            }
            Constant.original = cropBitmap.get(0);
            imagecount.setText("Crop Image " + (imageIndex+1) + " of " + cropBitmap.size());
        }
        if (Constant.original != null) {
            //some image must have been set before moving to this activity
            //set image to crop view
            original = Constant.original;
            iv_preview_crop.setImageToCrop(original);
            changeBrightness(20);
        }
//        AdsUtils.loadGoogleInterstitialAd(this, CropDocumentActivity.this);
//        AdsUtils.showGoogleBannerAd(this, adView);

        seekBarBrightness.setOnSeekBarChangeListener(this);
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
//        if (id == R.id.iv_add_new_scan) {
//            return;
//        }

            if (id == R.id.iv_original) {//set the original image to the crop view
                try {
                showProgressDialog();
                tempBitmap = original;
                iv_preview_crop.setImageBitmap(original);
                dismissProgressDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                dismissProgressDialog();
            }

            //select the original tab and deselect the other tab
            iv_original.setBackgroundResource(R.drawable.filter_selection_bg);
            iv_original.setTextColor(getResources().getColor(R.color.white));

            iv_color.setBackgroundResource(R.drawable.filter_bg);
            iv_color.setTextColor(getResources().getColor(R.color.black));

            iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
            iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

            iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
            iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
            return;
        } else if (id == R.id.iv_ocv_black) {
                mCurrentImageEdited = true;
            showProgressDialog();
            //ScanActivity -> a scanLibrary project
            //if we are successfull in converting a image to given form then we will update the cropView with given updtad image else set the original image
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        tempBitmap = ScanActivity.getBWBitmap(original);
                    } catch (OutOfMemoryError e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tempBitmap = original;
                                iv_preview_crop.setImageBitmap(original);
                                e.printStackTrace();
                                dismissProgressDialog();
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_preview_crop.setImageBitmap(tempBitmap);
                            dismissProgressDialog();
                        }
                    });
                }
            });

            //select the ocv black tab and deselect the other tab
            iv_original.setBackgroundResource(R.drawable.filter_bg);
            iv_original.setTextColor(getResources().getColor(R.color.black));

            iv_color.setBackgroundResource(R.drawable.filter_bg);
            iv_color.setTextColor(getResources().getColor(R.color.black));

            iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
            iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

            iv_ocv_black.setBackgroundResource(R.drawable.filter_selection_bg);
            iv_ocv_black.setTextColor(getResources().getColor(R.color.white));

            return;
        } else if (id == R.id.iv_color) {
                mCurrentImageEdited = true;
            showProgressDialog();

            //if we are successfull in converting a image to given form then we will update the cropView with given updtad image else set the original image
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        tempBitmap = ScanActivity.getMagicColorBitmap(original);
                    } catch (OutOfMemoryError e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tempBitmap = original;
                                iv_preview_crop.setImageBitmap(original);
                                e.printStackTrace();
                                dismissProgressDialog();
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_preview_crop.setImageBitmap(tempBitmap);
                            dismissProgressDialog();
                        }
                    });
                }
            });
            //select the color tab and deselect the other tab
            iv_original.setBackgroundResource(R.drawable.filter_bg);
            iv_original.setTextColor(getResources().getColor(R.color.black));

            iv_color.setBackgroundResource(R.drawable.filter_selection_bg);
            iv_color.setTextColor(getResources().getColor(R.color.white));

            iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
            iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

            iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
            iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
            return;
        } else if (id == R.id.iv_sharp_black) {
                mCurrentImageEdited = true;
            showProgressDialog();
            //if we are successfull in converting a image to given form then we will update the cropView with given updtad image else set the original image
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        tempBitmap = ScanActivity.getGrayBitmap(original);
                    } catch (OutOfMemoryError e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tempBitmap = original;
                                iv_preview_crop.setImageBitmap(original);
                                e.printStackTrace();
                                dismissProgressDialog();
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_preview_crop.setImageBitmap(tempBitmap);
                            dismissProgressDialog();
                        }
                    });
                }
            });
            //select the sharp black tab and deselect the other tab
            iv_original.setBackgroundResource(R.drawable.filter_bg);
            iv_original.setTextColor(getResources().getColor(R.color.black));

            iv_color.setBackgroundResource(R.drawable.filter_bg);
            iv_color.setTextColor(getResources().getColor(R.color.black));

            iv_sharp_black.setBackgroundResource(R.drawable.filter_selection_bg);
            iv_sharp_black.setTextColor(getResources().getColor(R.color.white));

            iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
            iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
            return;
        } else if (id == R.id.iv_done) {//Whether the selection is a tetra-sided(4 side) shape
            if (iv_preview_crop.canRightCrop()) {
                //store the crop image in the constant
                if(fromGallery){
                    new addDocGroupMultiple().execute(new Bitmap[0]);
                }else{
                    Constant.original = iv_preview_crop.crop();
                    if(!Constant.current_camera_view.equals("Document")) {
                        //add a image to the group if already exist or create a new group and add a images to that group
                        //and lastly move to the groupdocumentActivity
                        new addDocGroup().execute(new Bitmap[]{Constant.original});
                    }else{
                        Constant.multipleScan.add(Constant.original);
                        Intent intent = new Intent(CropDocumentActivity.this,ScannerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("fromCrop",true);
                        startActivity(intent);
                        finish();
                    }
                }
                return;
            }
            return;
        } else if (id == R.id.iv_back) {
            onBackPressed();
            return;
        } else if (id == R.id.iv_Rotate_Doc || id == R.id.rotateButton) {//rotate a bitmap and update a bitmap to crop view and original variable of the constant class and this class
            mCurrentImageEdited = true;
                Bitmap bitmap = Constant.original;
            Matrix matrix = new Matrix();
            //rotating a matrix to 90degree
            matrix.postRotate(90.0f);
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Constant.original.recycle();
            System.gc();
            Constant.original = createBitmap;
            original = createBitmap;
            iv_preview_crop.setImageToCrop(Constant.original);
            iv_preview_crop.setFullImgCrop();
            Log.e(TAG, "onClick: Rotate");
                /*if (iv_preview_crop.canRightCrop()) {
                    Constant.original = iv_preview_crop.crop();
                    Constant.IdentifyActivity = "CurrentFilterActivity";
                    AdsUtils.showGoogleInterstitialAd(CropDocumentActivity.this, true);
                    return;
                }*/
            return;
        } else if (id == R.id.iv_edit) {
            if (iv_preview_crop.canRightCrop()) {
                //crop the image and update the constant
                //and move to the DocumentEditorActivity_Crop
                Constant.original = iv_preview_crop.crop();
                new addGroup().execute(new Bitmap[]{Constant.original});
                return;
            }
            return;
        } else if (id == R.id.iv_full_crop) {//set full image crop
            iv_preview_crop.setFullImgCrop();
            return;
        } else if (id == R.id.iv_retake) {//
                if(Constant.current_camera_view.equals("Document")){
                    Intent intent = new Intent(CropDocumentActivity.this, ScannerActivity.class);
                    intent.putExtra("fromCrop" ,true);
                    startActivity(intent);
                    finish();
                }else{
                    Constant.IdentifyActivity = "ScannerActivity_Retake";
                    AdsUtils.jumpNextActivity(CropDocumentActivity.this);
                }
//            Constant.IdentifyActivity = "ScannerActivity_Retake";
//            AdsUtils.jumpNextActivity(CropDocumentActivity.this);
//                AdsUtils.showGoogleInterstitialAd(CropDocumentActivity.this, true);
            return;
        } else if (id == R.id.ly_current_filter) {//jump to currentFilteractivity
            if (iv_preview_crop.canRightCrop()) {
                Constant.original = iv_preview_crop.crop();
                Constant.IdentifyActivity = "CurrentFilterActivity";
                AdsUtils.jumpNextActivity(CropDocumentActivity.this);
//                    AdsUtils.showGoogleInterstitialAd(CropDocumentActivity.this, true);
                return;
            }
            return;
          /*  case R.id.ly_rotate_doc:
                Bitmap bitmap = Constant.original;
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Constant.original.recycle();
                System.gc();
                Constant.original = createBitmap;
                iv_preview_crop.setImageToCrop(Constant.original);
                iv_preview_crop.setFullImgCrop();
                return;*/
        }
            else if(id == R.id.save_btn){
                cropButtonClicked();
            }else if(id == R.id.previousImageButton){
                prevImgBtnClicked();
            }else if(id == R.id.nextimageButton){
                nextImageClicked();
            }
    }

    public void nextImageClicked() {
        if ( Constant.multipleScan.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            imageIndex = (imageIndex + 1) % Constant.multipleScan.size();
            Constant.original = cropBitmap.get(imageIndex);
            original = Constant.original;
            iv_preview_crop.setImageToCrop(original);
            mCurrentImageEdited = false;
            imagecount.setText("Crop Image " + (imageIndex+1) + " of " + cropBitmap.size());
            resetView();
        } else {
            showSnackbar(this, R.string.save_first);
        }
    }

    public void prevImgBtnClicked() {
        if ( Constant.multipleScan.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            if (imageIndex == 0) {
                imageIndex = Constant.multipleScan.size();
            }
            imageIndex = (imageIndex - 1) % Constant.multipleScan.size();

            Constant.original = cropBitmap.get(imageIndex);
            original = Constant.original;
            iv_preview_crop.setImageToCrop(original);
            mCurrentImageEdited = false;
            imagecount.setText("Crop Image " + (imageIndex+1) + " of " + cropBitmap.size());
            resetView();
        } else {
            showSnackbar(this, R.string.save_first);
        }
    }

    public void cropButtonClicked(){
        mCurrentImageEdited = false;
        Constant.original = iv_preview_crop.crop();
        original = Constant.original;
        cropBitmap.set(imageIndex,original);
        iv_preview_crop.setImageToCrop(original);
    }
    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, 1000).show();
    }
    private void changeBrightness(float brightness) {
        //change the brightness of the image and set a new image to the crop view
        iv_preview_crop.setImageBitmap(AdjustUtil.changeBitmapContrastBrightness(original, 1.0f, brightness));
    }

    public void resetView(){

        iv_original.setBackgroundResource(R.drawable.filter_selection_bg);
        iv_original.setTextColor(getResources().getColor(R.color.white));

        iv_color.setBackgroundResource(R.drawable.filter_bg);
        iv_color.setTextColor(getResources().getColor(R.color.black));

        iv_sharp_black.setBackgroundResource(R.drawable.filter_bg);
        iv_sharp_black.setTextColor(getResources().getColor(R.color.black));

        iv_ocv_black.setBackgroundResource(R.drawable.filter_bg);
        iv_ocv_black.setTextColor(getResources().getColor(R.color.black));
    }







    //overide the method of the OnSeekBarChangeListener
    //1. onProgressChanged
    //2. onStartTrackingTouch
    //3. onStopTrackingTouch

    //notificatrion that the progeress level has been changed
    //change the brightness according to the progress
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seekBarBrightness) {
            changeBrightness(progress);
        }
    }

    //notification that the user has started a touch gesture
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    //notification that the user has stoped the touch gesture
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class addDocGroupMultiple extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addDocGroupMultiple() {

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CropDocumentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            //
            ArrayList<Bitmap> multiplescan = cropBitmap;
//            Bitmap bitmap = Constant.original;
            if(multiplescan.isEmpty()){
                return null;
            }

            ArrayList<File> files = new ArrayList<>(multiplescan.size());
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            for(int i =0; i < multiplescan.size(); i++){
                Bitmap bitmap = multiplescan.get(i);
                byte[] bytes = BitmapUtils.getBytes(bitmap);
                File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bytes);
                    fileOutputStream.close();
                    files.add(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (Constant.inputType.equals("Group")) {
                group_name = Constant.foldname + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                //if creating a new group then create a table with group name and add image to that group(table)
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, files.get(0).getPath(), Constant.current_tag));


                for(int i =0; i < files.size(); i++){
                    current_doc_name = "Doc_" + System.currentTimeMillis();
                    dbHelper.addGroupDoc(group_name, files.get(i).getPath(), current_doc_name, "Insert text here...");
                }
                return null;
            }
            //if group already exist then add the image to the that group
            group_name = GroupDocumentActivity.current_group;

            for(int i =0; i < files.size(); i++){
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dbHelper.addGroupDoc(group_name, files.get(i).getPath(), current_doc_name, "Insert text here...");
            }
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;

            if(Constant.inputType.equals("Group")){
                Intent intent2 = new Intent(CropDocumentActivity.this, GroupDocumentActivity.class);
                intent2.putExtra("current_group", selected_group_name);
                startActivity(intent2);
                Constant.IdentifyActivity = "";
                finish();
            }else{
                finish();
            }
        }
    }












    //add a doc to the group
    //if the group already exist then add a image to that group
    //else create a new table with default name and add a images to that table
    //lastly move to the DocumentEditorActivity_Crop

    private class addGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addGroup() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CropDocumentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(bitmap);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            group_name = GroupDocumentActivity.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;
            Constant.IdentifyActivity = "DocumentEditorActivity_Crop";
            AdsUtils.jumpNextActivity(CropDocumentActivity.this);
//            AdsUtils.showGoogleInterstitialAd(CropDocumentActivity.this, true);
        }
    }

    //add a doc to the group
    //if the group already exist then add a image to that group
    //else create a new table with default name and add a images to that table
    //lastly move to the groupdocumentActivity
    private class addDocGroup extends AsyncTask<Bitmap, Void, Bitmap> {
        String current_doc_name;
        String group_date;
        String group_name;
        ProgressDialog progressDialog;

        private addDocGroup() {

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CropDocumentActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
        }

        @Override
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = Constant.original;
            if (bitmap == null) {
                return null;
            }
            byte[] bytes = BitmapUtils.getBytes(bitmap);
//            Environment.DIRECTORY_DOWNLOADS
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(externalFilesDir, System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Constant.inputType.equals("Group")) {
                group_name = "CamScanner" + Constant.getDateTime("_ddMMHHmmss");
                group_date = Constant.getDateTime("yyyy-MM-dd  hh:mm a");
                current_doc_name = "Doc_" + System.currentTimeMillis();

                //if creating a new group then create a table with group name and add image to that group(table)
                dbHelper.createDocTable(group_name);
                dbHelper.addGroup(new DBModel(group_name, group_date, file.getPath(), Constant.current_tag));
                dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
                return null;
            }
            //if group already exist then add the image to the that group
            group_name = GroupDocumentActivity.current_group;
            current_doc_name = "Doc_" + System.currentTimeMillis();
            dbHelper.addGroupDoc(group_name, file.getPath(), current_doc_name, "Insert text here...");
            return null;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressDialog.dismiss();
            selected_group_name = group_name;
            current_docs_name = current_doc_name;

            Intent intent2 = new Intent(CropDocumentActivity.this, GroupDocumentActivity.class);
            intent2.putExtra("current_group", selected_group_name);
            startActivity(intent2);
            Constant.IdentifyActivity = "";
            finish();
        }
    }

//    show progress dailog
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Applying Filter...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    //dismiss the dialog
    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(fromGallery){
            finish();
        } else if(Constant.current_camera_view.equals("Document")){
            Intent intent = new Intent(CropDocumentActivity.this, ScannerActivity.class);
            intent.putExtra("fromCrop" ,true);
            startActivity(intent);
            finish();
        }else{
            Constant.IdentifyActivity = "ScannerActivity_Retake";
            AdsUtils.jumpNextActivity(CropDocumentActivity.this);
        }
    }
}
