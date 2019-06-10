package android.ocr.bmk.data.remote.retrofit

import android.arch.lifecycle.LiveData
import android.ocr.bmk.data.bean.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.HashMap

/**
 * Created by guluwa on 2018/1/12.
 */

class RemoteDataSource {

    object SingletonHolder {
        //单例（静态内部类）
        val instance = RemoteDataSource()
    }

    companion object {

        fun getInstance() = SingletonHolder.instance
    }

    /**
     * main menu
     */
    fun mainMenu(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<CertificateBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.mainMenu(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * login
     */
    fun userLogin(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<LoginBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.userLogin(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * read idcard
     */
    fun readIdCard(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.readIdCard(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * save idcard
     */
    fun saveIdCard(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.saveIdCard(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * register
     */
    fun userRegister(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<LoginBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.userRegister(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * upload
     */
    fun uploadPicture(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        val path = map["path"]
        val file = File(path)
        val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
        val part = MultipartBody.Part.createFormData("uploaded_image", file.name, requestFile)
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.uploadPicture(
                RequestBody.create(MediaType.parse("text/plain"), "upload"),
                RequestBody.create(MediaType.parse("text/plain"), map["username"] ?: ""),
                RequestBody.create(MediaType.parse("text/plain"), map["type"] ?: ""),
                part
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * query
     */
    fun queryPicture(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<UploadImageBean>>> {
        val path = map["path"]
        val file = File(path)
        val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
        val part = MultipartBody.Part.createFormData("uploaded_image", file.name, requestFile)
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.queryPicture(
                RequestBody.create(MediaType.parse("text/plain"), "query"),
                RequestBody.create(MediaType.parse("text/plain"), map["username"] ?: ""),
                RequestBody.create(MediaType.parse("text/plain"), map["type"] ?: ""),
                part
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * update lose
     */
    fun updateAndLose(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
            RetrofitWorker.retrofitWorker.updateAndLose(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }
}