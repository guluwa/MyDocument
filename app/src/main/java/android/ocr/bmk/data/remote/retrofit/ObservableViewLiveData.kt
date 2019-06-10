package android.ocr.bmk.data.remote.retrofit

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.ErrorBean
import android.ocr.bmk.data.bean.ViewDataBean
import android.ocr.bmk.data.remote.retrofit.exception.*
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.manage.MyApplication
import android.ocr.bmk.ui.login.LoginActivity
import android.ocr.bmk.utils.AppUtils
import android.ocr.bmk.utils.FinishActivityManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by guluwa on 2018/1/4.
 */

class ObservableViewLiveData<T>(private val mObservable: Observable<T>) : LiveData<ViewDataBean<T>>() {

    private var mDisposableRef: WeakReference<Disposable>? = null
    private val mLock = Any()

    override fun onActive() {
        super.onActive()

        mObservable.subscribe(object : Observer<T> {
            override fun onSubscribe(d: Disposable) {
                synchronized(mLock) {
                    mDisposableRef = WeakReference(d)
                }
                postValue(ViewDataBean.loading())
            }

            override fun onNext(t: T) {
                if (t == null) {
                    postValue(ViewDataBean.empty())
                } else {
                    postValue(ViewDataBean.content(t))
                }
            }

            override fun onError(e: Throwable) {
                if (e is BaseException)
                    println(e.msg)
                else
                    println(e.message)
                synchronized(mLock) {
                    mDisposableRef = null
                }
                postValue(ViewDataBean.error(handleException(e)))
            }

            override fun onComplete() {
                synchronized(mLock) {
                    mDisposableRef = null
                }
            }
        })
    }

    override fun onInactive() {
        super.onInactive()

        synchronized(mLock) {
            val disposableWeakReference = mDisposableRef
            if (disposableWeakReference != null) {
                val disposable = disposableWeakReference.get()
                disposable?.dispose()
                mDisposableRef = null
            }
        }
    }

    private fun handleException(t: Throwable): ErrorBean {
        println(t)
        return if (t is NoNetworkException) {
            ErrorBean(MyApplication.getContext().getString(R.string.network_error), 1)
        } else if (t is NoDataException) {
            ErrorBean(MyApplication.getContext().getString(R.string.data_error), 2)
        } else if (t is HttpException) {
            ErrorBean(MyApplication.getContext().getString(R.string.network_request_error), 3)
        } else if (t is OtherException) {
            if (t.msg == "请重新登录") {
                clearUserInfo()
                ErrorBean(MyApplication.getContext().getString(R.string.user_data_error), 8)
            } else  {
                ErrorBean(t.msg, 4)
            }
        } else if (t is SocketTimeoutException || t is UnknownHostException) {
            ErrorBean(MyApplication.getContext().getString(R.string.network_error), 5)
        } else if (t is TokenException) {
            ErrorBean(MyApplication.getContext().getString(R.string.user_info_over_time_error), 7)
        } else if (t is ReLoginException) {
            clearUserInfo()
            ErrorBean(MyApplication.getContext().getString(R.string.user_data_error), 8)
        } else {
            if (t is BaseException)
                ErrorBean("${MyApplication.getContext().getString(R.string.other_error)}," + t.msg, 6)
            else
                ErrorBean("${MyApplication.getContext().getString(R.string.other_error)},$t", 6)
        }
    }

    private fun clearUserInfo() {
        AppUtils.setString(Contacts.TOKEN, "")
        AppUtils.setString(Contacts.ID, "")
        AppUtils.setString(Contacts.TYPE, "")
        val finishActivityManager = FinishActivityManager.getInstance()
        if (finishActivityManager.currentActivity() != null) {
            (finishActivityManager.currentActivity()!! as BaseActivity).showToastMsg(MyApplication.getContext().getString(R.string.user_data_error))
            (finishActivityManager.currentActivity()!! as BaseActivity).startActivity(Intent(finishActivityManager.currentActivity()!!, LoginActivity::class.java))
            finishActivityManager.finishAllActivity()
        }
    }
}
