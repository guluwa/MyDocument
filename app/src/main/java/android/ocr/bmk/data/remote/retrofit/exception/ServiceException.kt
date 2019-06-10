package android.ocr.bmk.data.remote.retrofit.exception

/**
 * Created by 俊康 on 2017/9/27.
 */

class ServiceException(msg: String) : BaseException() {

    init {
        this.msg = msg
    }

    companion object {

        private val serialVersionUID = 2211853108336484888L
    }
}
