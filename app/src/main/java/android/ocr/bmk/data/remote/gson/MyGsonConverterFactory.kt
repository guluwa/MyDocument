package android.ocr.bmk.data.remote.gson

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Created by 俊康 on 2017/8/8.
 */

class MyGsonConverterFactory private constructor(private val gson: Gson?) : Converter.Factory() {

    init {
        if (gson == null) throw NullPointerException("gson == null")
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return MyGsonResponseBodyConverter<Any>(gson!!, type!!)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        val adapter = gson!!.getAdapter(TypeToken.get(type!!))
        return MyGsonRequestBodyConverter(gson, adapter as TypeAdapter<Any>)
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson()): MyGsonConverterFactory {
            return MyGsonConverterFactory(gson)
        }
    }
}
