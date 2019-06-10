package android.ocr.bmk.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.ocr.bmk.R;
import android.ocr.bmk.base.BaseActivity;
import android.ocr.bmk.utils.FinishActivityManager;
import android.ocr.bmk.utils.ToastUtil;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;
import java.io.File;

/**
 * 版本更新升级 广播接受者
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            System.out.println("下载完成啦啦啦啦" + downloadApkId);
            installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, long downloadApkId) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downId = sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadApkId == downId) {
            File downloadApkFile = queryDownloadedApk(context, downloadApkId);
            if (downloadApkFile != null) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            ToastUtil.Companion.getInstance().showToast("请打开允许自动安装" +context.getString(R.string.app_name) + "权限");
                            if (FinishActivityManager.Companion.getInstance().currentActivity() instanceof BaseActivity)
                                ((BaseActivity) FinishActivityManager.Companion.getInstance().currentActivity()).startInstallPermissionSettingActivity();
                            return;
                        } else {
                            System.out.println("请打开允许自动安装" + context.getString(R.string.app_name) + "权限");
                        }
                    }
                    Uri uri = FileProvider.getUriForFile(context, context.getString(R.string.application_id)+".provider", downloadApkFile);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    install.setDataAndType(Uri.fromFile(downloadApkFile), "application/vnd.android.package-archive");
                }
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     *
     * @param context
     * @return
     */
    public static File queryDownloadedApk(Context context, long downloadId) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;

    }
}
