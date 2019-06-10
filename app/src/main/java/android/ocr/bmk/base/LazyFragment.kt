package android.ocr.bmk.base

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.ocr.bmk.R
import android.ocr.bmk.data.bean.PageTipBean
import android.ocr.bmk.utils.ToastUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hwangjr.rxbus.RxBus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by guluwa on 2018/3/30.
 */

abstract class LazyFragment : Fragment(), IBaseView {

    var mViewDataBinding: ViewDataBinding? = null
    private var mIsMulti = false
    private var savedState: Bundle? = null
    /**
     * 进度对话框
     */
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.get().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mViewDataBinding == null) {
            mViewDataBinding = DataBindingUtil.inflate(inflater, viewLayoutId, container, false)
        }
        val parent = mViewDataBinding!!.root.parent
        if (parent != null) {
            (parent as ViewGroup).removeView(mViewDataBinding!!.root)
        }
        return mViewDataBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    open fun initViewModel() {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Restore State Here
        if (!restoreStateFromArguments()) {
            // First Time, Initialize something here
            onFirstTimeLaunched()
        }
        if (userVisibleHint && mViewDataBinding != null && !mIsMulti) {
            mIsMulti = true
            lazyLoad()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser && isVisible && mViewDataBinding != null && !mIsMulti) {
            mIsMulti = true
            lazyLoad()
        } else {
            super.setUserVisibleHint(isVisibleToUser)
        }
    }

    /**
     * 绑定布局文件
     *
     * @return 布局文件ID
     */
    abstract val viewLayoutId: Int

    /**
     * 初始化视图控件
     */
    protected abstract fun initViews()

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract fun lazyLoad()

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected fun stopLoad() {

    }

    override fun onDestroy() {
        RxBus.get().unregister(this)
        super.onDestroy()
    }

    /**
     * 弹出Toast
     */
    override fun showToastMsg(msg: String) {
        ToastUtil.getInstance().showToast(msg)
    }

    open fun onFirstTimeLaunched() {
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save State Here
        saveStateToArguments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Save State Here
        saveStateToArguments()
    }

    private fun saveStateToArguments() {
        if (view != null)
            savedState = saveState()
        if (savedState != null) {
            arguments?.putBundle("internalSavedViewState8954201239547", savedState)
        }
    }

    private fun restoreStateFromArguments(): Boolean {
        savedState = arguments?.getBundle("internalSavedViewState8954201239547")
        if (savedState != null) {
            restoreState()
            return true
        }
        return false
    }

    private fun restoreState() {
        if (savedState != null) {
            onRestoreState(savedState!!)
        }
    }

    open fun onRestoreState(savedInstanceState: Bundle) {
        initViewModel()
    }

    private fun saveState(): Bundle {
        val state = Bundle()
        onSaveState(state)
        return state
    }

    open fun onSaveState(outState: Bundle) {

    }

    override fun showProgressDialog(msg: String) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        mProgressDialog!!.setMessage(msg)
        mProgressDialog!!.show()
    }

    override fun dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private var disposableCountDown: Disposable? = null

    fun showCountDownProgressDialog(msg: String, num: Long) {
        showProgressDialog(msg)
        disposableCountDown = Observable.interval(0, num, TimeUnit.SECONDS)// 倒计时
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ aLong ->
                if (aLong != 0L) {
                    dismissProgressDialog()
                    disposableCountDown?.dispose()
                    disposableCountDown = null
                }
            }) {
                dismissProgressDialog()
                disposableCountDown?.dispose()
                disposableCountDown = null
            }
    }

    /**
     * 异常数据解析
     */
    open fun parseErrorDate(msg: String): PageTipBean {
        return when {
            msg.contains("IllegalStateException") -> PageTipBean(getString(R.string.data_format_error), 1)
            else -> PageTipBean(msg, 1)
        }
    }

}