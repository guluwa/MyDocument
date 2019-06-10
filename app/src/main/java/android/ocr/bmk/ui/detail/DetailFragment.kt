package android.ocr.bmk.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.ocr.bmk.R
import android.ocr.bmk.base.LazyFragment
import android.ocr.bmk.data.bean.CertificateBean
import android.ocr.bmk.data.bean.ListItemBean
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.data.bean.PageTipBean
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.adapter.DetailListAdapter
import android.ocr.bmk.ui.listener.OnClickListener
import android.ocr.bmk.ui.viewmodel.IDCardViewModel
import android.ocr.bmk.utils.AppUtils
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.Serializable
import java.util.HashMap

class DetailFragment : LazyFragment() {

    companion object {
        fun newInstance(title: String): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val viewLayoutId: Int get() = R.layout.fragment_detail

    //data
    private var mViewModel: IDCardViewModel? = null

    override fun initViews() {

    }

    private fun initRecyclerView(data: Any) {
        val list = mutableListOf<Any>()
        if (data is List<*>) {
            if (data.isNotEmpty()) {
                val item = data[0]
                if (item is LinkedTreeMap<*, *>) {
                    for (key in item.keys) {
                        if (key.toString().toIntOrNull() != null || key.toString() == "id") {
                            continue
                        }
                        list.add(ListItemBean(key as String, item[key] as String))
                    }
                }
            }
        } else if (data is LinkedTreeMap<*, *>) {
            for (key in data.keys) {
                if (key.toString().toIntOrNull() != null || key.toString() == "id") {
                    continue
                }
                list.add(ListItemBean(key as String, data[key] as String))
            }
        }
        if (list.isEmpty()) {
            list.add(PageTipBean("暂无详情", 1))
        }
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = DetailListAdapter(list, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {

            }
        })
    }

    override fun lazyLoad() {
        readIdCard()
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(IDCardViewModel::class.java)
        }
        if (!mViewModel!!.readIdCard()!!.hasObservers()) {
            mViewModel!!.readIdCard()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showToastMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("请稍候")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReadIdCard(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReadIdCard(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReadIdCard(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            initRecyclerView(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
        }
    }

    private fun readIdCard() {
        val map = HashMap<String, String>()
        val certificateBean = CertificateBean()
        certificateBean.name = arguments!!.getString("title")!!
        map["type"] = AppUtils.getType(certificateBean)
        map["mode"] = "read"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        mViewModel?.freshReadIdCard(map, true)
    }
}