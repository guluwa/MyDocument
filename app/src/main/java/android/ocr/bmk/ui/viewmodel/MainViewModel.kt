package android.ocr.bmk.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.ocr.bmk.data.bean.*
import android.ocr.bmk.data.remote.retrofit.RemoteDataSource

class MainViewModel : ViewModel() {

    private var mainMenuFresh = MutableLiveData<FreshBean>()

    private var mainMenuResult: LiveData<ViewDataBean<ResultBean<List<CertificateBean>>>>? = null

    fun mainMenu(): LiveData<ViewDataBean<ResultBean<List<CertificateBean>>>>? {
        if (mainMenuResult == null) {
            mainMenuResult = Transformations.switchMap(mainMenuFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().mainMenu(it.map)
                } else {
                    null
                }
            }
        }
        return mainMenuResult!!
    }

    fun freshMainMenu(map: HashMap<String, String>, isFresh: Boolean) {
        mainMenuFresh.value = FreshBean(map, isFresh)
    }

    private var queryPictureFresh = MutableLiveData<FreshBean>()

    private var queryPictureResult: LiveData<ViewDataBean<ResultBean<UploadImageBean>>>? = null

    fun queryPicture(): LiveData<ViewDataBean<ResultBean<UploadImageBean>>>? {
        if (queryPictureResult == null) {
            queryPictureResult = Transformations.switchMap(queryPictureFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().queryPicture(it.map)
                } else {
                    null
                }
            }
        }
        return queryPictureResult!!
    }

    fun freshQueryPicture(map: HashMap<String, String>, isFresh: Boolean) {
        queryPictureFresh.value = FreshBean(map, isFresh)
    }

    private var uploadPictureFresh = MutableLiveData<FreshBean>()

    private var uploadPictureResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun uploadPicture(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (uploadPictureResult == null) {
            uploadPictureResult = Transformations.switchMap(uploadPictureFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().uploadPicture(it.map)
                } else {
                    null
                }
            }
        }
        return uploadPictureResult!!
    }

    fun freshUploadPicture(map: HashMap<String, String>, isFresh: Boolean) {
        uploadPictureFresh.value = FreshBean(map, isFresh)
    }

    private var saveIdCardFresh = MutableLiveData<FreshBean>()

    private var saveIdCardResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun saveIdCard(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (saveIdCardResult == null) {
            saveIdCardResult = Transformations.switchMap(saveIdCardFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().saveIdCard(it.map)
                } else {
                    null
                }
            }
        }
        return saveIdCardResult!!
    }

    fun freshSaveIdCard(map: HashMap<String, String>, isFresh: Boolean) {
        saveIdCardFresh.value = FreshBean(map, isFresh)
    }
}