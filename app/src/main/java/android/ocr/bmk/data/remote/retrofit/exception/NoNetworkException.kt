package android.ocr.bmk.data.remote.retrofit.exception

/**
 * Created by 俊康 on 2017/8/8.
 */

class NoNetworkException(msg: String) : BaseException() {

    init {
        this.msg = msg
    }

    companion object {

        private val serialVersionUID = -347636838706746243L
    }
}
