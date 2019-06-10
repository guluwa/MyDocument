package android.ocr.bmk.ui.detail

import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.ListItemBean
import android.ocr.bmk.data.bean.PageTipBean
import android.ocr.bmk.data.bean.ResultBean
import android.ocr.bmk.ui.adapter.DetailListAdapter
import android.ocr.bmk.ui.listener.OnClickListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_update_lose_info.*
import kotlinx.android.synthetic.main.common_tool_bar.*
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.content.Intent
import android.net.Uri

class UpdateLoseInfoActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_update_lose_info

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val data = intent.getStringExtra("data")
        val list = mutableListOf<Any>()
        if (data != "") {
            val array = data.split(";;")
            for (item in array) {
                val split = item.split("::")
                if (split.size == 2) {
                    list.add(ListItemBean(split[0], split[1]))
                }
            }
        }
        if (list.isEmpty()) {
            list.add(PageTipBean("暂无详情", 1))
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = DetailListAdapter(list, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                if ((arg2 as ListItemBean).key == "Telephone") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + arg2.value))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else if (arg2.key == "Website") {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val content_url = Uri.parse(arg2.value)
                    intent.data = content_url
                    startActivity(intent)
                }
            }
        })
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { finish() }
        tvToolBarTitle.text = "详情"
    }
}
