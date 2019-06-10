package android.ocr.bmk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static File getSaveFile(Context context, String name) {
        File file = new File(context.getFilesDir(), name + ".jpg");
        return file;
    }

    /** * 将图片存到本地 */
    public static Uri saveBitmap(Bitmap bm, String picName) {
        try {
            String dir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/mydocument/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();    }
        return null;
    }
}