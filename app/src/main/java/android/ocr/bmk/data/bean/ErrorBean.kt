package android.ocr.bmk.data.bean

/**
 * Created by 俊康 on 2017/8/8.
 *
 * 1：网络错误，2：数据为空，3：数据错我，4：服务器404，5：token错误，6：请求超时，7：其他
 */

class ErrorBean(message: String, code: Int) : BaseBean(message, code)
