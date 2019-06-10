package android.ocr.bmk.base

/**
 * Created by Administrator on 2017/12/11.
 */

interface IBaseView {

    fun showToastMsg(msg: String)

    fun showProgressDialog(msg: String)

    fun dismissProgressDialog()
}
