package android.ocr.bmk.data.remote.retrofit

import android.ocr.bmk.data.bean.CertificateBean
import android.ocr.bmk.data.bean.LoginBean
import android.ocr.bmk.data.bean.ResultBean
import android.ocr.bmk.data.bean.UploadImageBean
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by guluwa on 2018/1/11.
 */

interface ApiService {

    /**
     * main menu
     */
    @GET("ocr.php")
    fun mainMenu(@QueryMap map: Map<String, String>): Observable<ResultBean<List<CertificateBean>>>

    /**
     * save idcard
     */
    @GET("ocr.php")
    fun saveIdCard(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * read idcard
     */
    @GET("ocr.php")
    fun readIdCard(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * login
     */
    @GET("api/reg/login")
    @Headers("url_type:monjaz")
    fun userLogin(@QueryMap map: Map<String, String>): Observable<ResultBean<LoginBean>>

    /**
     * register
     */
    @POST("api/reg/index")
    @FormUrlEncoded
    @Headers("url_type:monjaz")
    fun userRegister(@FieldMap map: Map<String, String>): Observable<ResultBean<LoginBean>>

    /**
     * upload
     */
    @POST("ocr.php")
    @Multipart
    fun uploadPicture(@Part("want") mode: RequestBody,
                      @Part("userid") username: RequestBody,
                      @Part("document") type: RequestBody,
                      @Part file: MultipartBody.Part): Observable<ResultBean<Any>>


    /**
     * query
     */
    @POST("ocr.php")
    @Multipart
    fun queryPicture(@Part("want") mode: RequestBody,
                     @Part("userid") username: RequestBody,
                     @Part("document") type: RequestBody,
                     @Part file: MultipartBody.Part): Observable<ResultBean<UploadImageBean>>

    /**
     * update lose
     */
    @GET("ocr.php")
    fun updateAndLose(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>
}