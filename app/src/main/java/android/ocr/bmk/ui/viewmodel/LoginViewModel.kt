package android.ocr.bmk.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.ocr.bmk.data.bean.FreshBean
import android.ocr.bmk.data.bean.LoginBean
import android.ocr.bmk.data.bean.ResultBean
import android.ocr.bmk.data.bean.ViewDataBean
import android.ocr.bmk.data.remote.retrofit.RemoteDataSource


class LoginViewModel : ViewModel() {

    private var userLoginFresh = MutableLiveData<FreshBean>()

    private var userLoginResult: LiveData<ViewDataBean<ResultBean<LoginBean>>>? = null

    fun passwordLogin(): LiveData<ViewDataBean<ResultBean<LoginBean>>>? {
        if (userLoginResult == null) {
            userLoginResult = Transformations.switchMap(userLoginFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().userLogin(it.map)
                } else {
                    null
                }
            }
        }
        return userLoginResult!!
    }

    fun freshPasswordLogin(map: HashMap<String, String>, isFresh: Boolean) {
        userLoginFresh.value = FreshBean(map, isFresh)
    }

    private var userRegisterFresh = MutableLiveData<FreshBean>()

    private var userRegisterResult: LiveData<ViewDataBean<ResultBean<LoginBean>>>? = null

    fun userRegister(): LiveData<ViewDataBean<ResultBean<LoginBean>>>? {
        if (userRegisterResult == null) {
            userRegisterResult = Transformations.switchMap(userRegisterFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().userRegister(it.map)
                } else {
                    null
                }
            }
        }
        return userRegisterResult!!
    }

    fun freshUserRegister(map: HashMap<String, String>, isFresh: Boolean) {
        userRegisterFresh.value = FreshBean(map, isFresh)
    }
}