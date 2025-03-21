package pdf.dudepdftools.util;

import android.content.Context;

import java.util.ArrayList;

import pdf.dudepdftools.R;
import pdf.dudepdftools.model.EnhancementOptionsEntity;

public class MergePdfEnhancementOptionsUtils {
    private static class SingletonHolder {
        static final MergePdfEnhancementOptionsUtils INSTANCE = new MergePdfEnhancementOptionsUtils();
    }

    public static MergePdfEnhancementOptionsUtils getInstance() {
        return MergePdfEnhancementOptionsUtils.SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> options = new ArrayList<>();

        options.add(new EnhancementOptionsEntity(
                context, R.drawable.ic_add_password, R.string.set_password));
        return options;
    }
}