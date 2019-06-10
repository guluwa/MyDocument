package android.ocr.bmk.data.bean

import java.io.Serializable

/**
 * Created by guluwa on 2018/4/2.
 * 页面状态0：加载 1：错误 2:数据
 */
class PageTipBean(var tip: String, var status: Int) : Serializable