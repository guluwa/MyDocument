package android.ocr.bmk.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class RomUtils {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";

    public static boolean isMiUIV7OrAbove() {
        try {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            String uiCode = properties.getProperty(KEY_MIUI_VERSION_CODE, null);
            if (uiCode != null) {
                int code = Integer.parseInt(uiCode);
                return code >= 5;
            } else {
                return false;
            }

        } catch (final Exception e) {
            return false;
        }
    }
}
