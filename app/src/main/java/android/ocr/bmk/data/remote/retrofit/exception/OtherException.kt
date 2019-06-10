package android.ocr.bmk.data.remote.retrofit.exception

/**
 * Created by Administrator on 2017/12/11.
 */

class OtherException(code: Int, msg: String) : BaseException() {

    init {
        this.code = code
        this.msg = msg
    }
}
