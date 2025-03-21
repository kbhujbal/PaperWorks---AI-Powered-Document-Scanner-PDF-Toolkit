package pdf.dudepdftools.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.templatemela.camscanner.R;
import com.templatemela.camscanner.activity.BaseActivity;
import com.templatemela.camscanner.activity.MainActivity;

public class SplashActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash_acvtivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                startActivity(new Intent(SplashActivity.this, MainActivity2.class));
                finish();
            }
        },1500);
    }
}
