package com.templatemela.camscanner.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
//import com.google.android.gms.ads.AdView;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.snackbar.Snackbar;
import com.shockwave.pdfium.PdfDocument;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.utils.AdsUtils;

import java.util.List;
import java.util.Objects;

//pdfviewActivity using a library to load a pdf to the pdfview
public class PDFViewerActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    private static final String TAG = "PDFViewerActivity";
    private int page_no = 0;
    private PDFView pdfView;
    protected Uri pdf_uri;
    protected String title;
    protected TextView tv_page;
    protected TextView tv_title;
    private boolean isProtected = false;
    private String password = "";
//    private AdView adView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pdfviewer);
        init();
    }

    private void init() {
//        adView = findViewById(R.id.adView);
//        AdsUtils.showGoogleBannerAd(this, adView);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_page = (TextView) findViewById(R.id.tv_page);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.setBackgroundColor(getResources().getColor(R.color.bg_color));
        title = getIntent().getStringExtra("title");
        isProtected = getIntent().getBooleanExtra("isProtected",false);
        tv_title.setText(title);
        pdf_uri = Uri.parse(getIntent().getStringExtra("pdf_path"));

        loadPDF(pdf_uri);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPDF(Uri uri) {
        if(isProtected){
            passwordProtectPDF();
        }else{
            pdfView.fromUri(uri).defaultPage(page_no).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(new DefaultScrollHandle(this)).spacing(12).onPageError(this).load();
        }
    }

    private void passwordProtectPDF() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.set_password)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);

        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                    }
                });

        positiveAction.setOnClickListener(v -> {
            if (isEmpty(passwordInput.getText())) {
                showSnackbar(this, R.string.snackbar_password_cannot_be_blank);
            } else {
                password = passwordInput.getText().toString();
                pdfView.fromUri(pdf_uri).password(password).defaultPage(page_no).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(new DefaultScrollHandle(this)).spacing(12).onPageError(this).load();
                dialog.dismiss();

            }
        });

        dialog.show();
        positiveAction.setEnabled(false);
    }

    public boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }
    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, 10000).show();
    }
    @Override
    public void loadComplete(int i) {
        PdfDocument.Meta documentMeta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + documentMeta.getTitle());
        Log.e(TAG, "author = " + documentMeta.getAuthor());
        Log.e(TAG, "subject = " + documentMeta.getSubject());
        Log.e(TAG, "keywords = " + documentMeta.getKeywords());
        Log.e(TAG, "creator = " + documentMeta.getCreator());
        Log.e(TAG, "producer = " + documentMeta.getProducer());
        Log.e(TAG, "creationDate = " + documentMeta.getCreationDate());
        Log.e(TAG, "modDate = " + documentMeta.getModDate());
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> list, String str) {
        for (PdfDocument.Bookmark next : list) {
            Log.e(TAG, String.format("%s %s, p %d", new Object[]{str, next.getTitle(), Long.valueOf(next.getPageIdx())}));
            if (next.hasChildren()) {
                List<PdfDocument.Bookmark> children = next.getChildren();
                printBookmarksTree(children, str + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int i, int i2) {
        page_no = i;
        tv_page.setText(String.format("%s / %s", new Object[]{Integer.valueOf(i + 1), Integer.valueOf(i2)}));
    }

    @Override
    public void onPageError(int i, Throwable th) {
        Log.e(TAG, "Cannot load page " + i);
    }
}
