package android.ocr.bmk.data.remote.retrofit

import android.ocr.bmk.data.remote.gson.MyGsonConverterFactory
import android.ocr.bmk.data.remote.retrofit.exception.NoNetworkException
import android.ocr.bmk.data.remote.retrofit.exception.ServiceException
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.manage.MyApplication
import android.ocr.bmk.utils.AppUtils
import android.util.Log
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

object RetrofitWorker {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(Contacts.BASEURL)
                .client(OkHttpClient.Builder()
                        .addInterceptor(ChuckInterceptor(MyApplication.getContext()))//打印
                        .addInterceptor(ChangeUrlInterceptor())
                        .addInterceptor(sLoggingInterceptor)
                        .addInterceptor { chain ->
                            val connected = AppUtils.isNetConnected
                            if (connected) {
                                chain.proceed(chain.request())
                            } else {
                                throw NoNetworkException("没有网络哦~~~")
                            }
                        }
                        .addInterceptor { chain ->
                            val proceed = chain.proceed(chain.request())
                            if (proceed.code() == 404) {
                                throw ServiceException("服务器好像出了点小问题~~~")
                            } else {
                                proceed
                            }
                        }
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true).build())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    val retrofitWorker: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    /**
     * 打印返回的json数据拦截器
     */
    private val sLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBuffer = Buffer()
        if (request.body() != null) {
            request.body()!!.writeTo(requestBuffer)
        } else {
            Log.d("LogTAG", "request.body() == null")
        }
        //打印url信息
        Log.w("LogTAG", request.url().toString() + if (request.body() != null) "?" + parseParams(request.body(), requestBuffer) else "")
        chain.proceed(request)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun parseParams(body: RequestBody?, requestBuffer: Buffer): String {
        return if (body!!.contentType() != null && !body.contentType()!!.toString().contains("multipart")) {
            URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8")
        } else "null"
    }
}
//1、课程表接口，取消上下午，一天的课程放在一个数组返回
//2、学生给老师、老师给学生留言、留言历史记录
//3、学校注册、登录返回要支付的金额，用于调用支付接口
//4、批改作业接口添加语音字段
//5、App版本更新检测接口