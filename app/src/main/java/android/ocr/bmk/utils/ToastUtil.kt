package android.ocr.bmk.utils

import android.content.Context
import android.ocr.bmk.R
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import android.ocr.bmk.manage.MyApplication

/**
 * Toast 工具类
 */

class ToastUtil private constructor() {

    private var mToastInstance: Toast? = null

    fun showToast(message: String) {
        makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT, true).show()
    }

    private fun makeText(context: Context, message: String, showTime: Int, isCenter: Boolean): Toast {
        if (mToastInstance == null) {
            mToastInstance = Toast(context)
            val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflate.inflate(R.layout.toast_content_view, null)
            mToastInstance!!.view = v
        }
        (mToastInstance!!.view.findViewById(R.id.message) as TextView).text = message
        mToastInstance!!.duration = showTime
        if (isCenter) {
            mToastInstance!!.setGravity(Gravity.CENTER, 0, 0)
        } else {
            mToastInstance!!.setGravity(Gravity.BOTTOM, 0, 0)
        }
        return mToastInstance as Toast
    }

    object SingletonHolder {
        //单例（静态内部类）
        val instance = ToastUtil()
    }

    companion object {
        fun getInstance() = SingletonHolder.instance
    }
}
