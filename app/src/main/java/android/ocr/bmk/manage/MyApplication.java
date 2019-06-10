package android.ocr.bmk.manage;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by guluwa on 2018/6/1.
 */
public class MyApplication extends Application {

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        handleError();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * RxJava2 当取消订阅后(dispose())，RxJava抛出的异常后续无法接收(此时后台线程仍在跑，可能会抛出IO等异常),全部由RxJavaPlugin接收，需要提前设置ErrorHandler
     * 详情：http://engineering.rallyhealth.com/mobile/rxjava/reactive/2017/03/15/migrating-to-rxjava-2.html#Error Handling
     */
    private void handleError() {
        RxJavaPlugins.setErrorHandler(throwable -> Log.e("error", throwable.getMessage()));
    }
}