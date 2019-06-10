package android.ocr.bmk.data.remote.retrofit.exception

import java.io.IOException

/**
 * Created by 俊康 on 2017/9/27.
 */

open class BaseException : IOException() {

    var msg: String = ""
    var code: Int = 0
    var data: String = ""
}
