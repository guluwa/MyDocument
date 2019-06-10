package android.ocr.bmk.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.ocr.bmk.data.bean.FreshBean
import android.ocr.bmk.data.bean.ResultBean
import android.ocr.bmk.data.bean.ViewDataBean
import android.ocr.bmk.data.remote.retrofit.RemoteDataSource

class IDCardViewModel:ViewModel() {

    private var readIdCardFresh = MutableLiveData<FreshBean>()

    private var readIdCardResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun readIdCard(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (readIdCardResult == null) {
            readIdCardResult = Transformations.switchMap(readIdCardFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().readIdCard(it.map)
                } else {
                    null
                }
            }
        }
        return readIdCardResult!!
    }

    fun freshReadIdCard(map: HashMap<String, String>, isFresh: Boolean) {
        readIdCardFresh.value = FreshBean(map, isFresh)
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

    private var updateAndLoseFresh = MutableLiveData<FreshBean>()

    private var updateAndLoseResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun updateAndLose(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (updateAndLoseResult == null) {
            updateAndLoseResult = Transformations.switchMap(updateAndLoseFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().updateAndLose(it.map)
                } else {
                    null
                }
            }
        }
        return updateAndLoseResult!!
    }

    fun freshUpdateAndLose(map: HashMap<String, String>, isFresh: Boolean) {
        updateAndLoseFresh.value = FreshBean(map, isFresh)
    }
}