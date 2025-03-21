package pdf.dudepdftools.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import pdf.dudepdftools.R;
import pdf.dudepdftools.adapter.PreviewAdapter;
import pdf.dudepdftools.util.ThemeUtils;

import static pdf.dudepdftools.util.Constants.PREVIEW_IMAGES;

public class ImagesPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);

        ButterKnife.bind(this);
        // Extract mImagesArrayList uri array from the intent
        Intent intent = getIntent();
        ArrayList<String> mImagesArrayList = intent.getStringArrayListExtra(PREVIEW_IMAGES);

        ViewPager mViewPager = findViewById(R.id.viewpager);
        PreviewAdapter mPreviewAdapter = new PreviewAdapter(this, mImagesArrayList);
        mViewPager.setAdapter(mPreviewAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * get start intent for this activity
     * @param context - context to start activity from
     * @param uris - extra images uri
     * @return - start intent
     */
    public static Intent getStartIntent(Context context, ArrayList<String>  uris) {
        Intent intent = new Intent(context, ImagesPreviewActivity.class);
        intent.putExtra(PREVIEW_IMAGES, uris);
        return intent;
    }
}