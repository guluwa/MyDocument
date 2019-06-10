package android.ocr.bmk.data.remote.retrofit


import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.utils.AppUtils
import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by guluwa on 2018/6/5.
 */
class ChangeUrlInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //获取request
        val request = chain.request()
        //从request中获取原有的HttpUrl实例oldHttpUrl
        val oldHttpUrl = request.url()
        //获取request的创建者builder
        val builder1 = request.newBuilder()
        //从request中获取headers，通过给定的键url_name
        val headerValues = request.headers("url_type")
        if (headerValues != null && headerValues.size > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder1.removeHeader("url_type")
            //匹配获得新的BaseUrl
            val headerValue = headerValues[0]
            val newBaseUrl = when (headerValue) {
                "monjaz" -> HttpUrl.parse(Contacts.LOGIN_URL)
                else -> oldHttpUrl
            }
            //重建新的HttpUrl，修改需要修改的url部分
            val newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme("http")//更换网络协议
                    .host(newBaseUrl!!.host())//更换主机名
                    .port(newBaseUrl.port())//更换端口
                    .removePathSegment(0)//移除第一个参数
                    .build()
            //重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            Log.e("Url", "intercept: $newFullUrl")

            return chain.proceed(builder1.url(newFullUrl)
                    .addHeader("Cookie", AppUtils.getString("cookieStr", "")).build())
        }
        return chain.proceed(request)
    }
}
