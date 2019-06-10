package android.ocr.bmk.ui.detail

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.ocr.bmk.R
import android.ocr.bmk.base.LazyFragment
import android.ocr.bmk.ui.adapter.PhotoListAdapter
import android.ocr.bmk.ui.listener.OnClickListener
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.IOException
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.ui.viewmodel.IDCardViewModel
import android.ocr.bmk.utils.AppUtils
import android.os.Looper.getMainLooper
import android.support.v4.print.PrintHelper
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import java.util.HashMap
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import java.lang.StringBuilder

class PhotoFragment : LazyFragment(), TencentLocationListener {

    companion object {
        fun newInstance(title: String, url: String): PhotoFragment {
            val fragment = PhotoFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("url", url)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val viewLayoutId: Int get() = R.layout.fragment_photo

    //data
    private var mViewModel: IDCardViewModel? = null

    private var mCurrentCity = ""

    private var mLocationManager: TencentLocationManager? = null

    override fun initViews() {
        initRecyclerView()
        initLocation()
        tvUpdate.setOnClickListener {
            updateAndLose("change_new")
        }
        tvLose.setOnClickListener {
            updateAndLose("report_loss")
        }
    }

    private fun initLocation() {
        mLocationManager = TencentLocationManager.getInstance(context)
        val error = mLocationManager?.requestLocationUpdates(
                TencentLocationRequest.create()
                        .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI)
                        .setInterval(3000).setAllowCache(true), this, getMainLooper()
        )
        Log.e("error", error.toString())
    }

    private fun goMapPage(data: Any) {
        val sb = StringBuilder()
        var Latitude = 29.18
        var Longitude = 120.04
        if (data is List<*>) {
            for (item in data) {
                if (item is LinkedTreeMap<*, *>) {
                    for (key in item.keys) {
                        if (key.toString().toIntOrNull() != null || key.toString() == "id") {
                            continue
                        }
                        if (key == "Latitude") {
                            Latitude = item[key].toString().toDouble()
                        }
                        if (key == "Longitude") {
                            Longitude = item[key].toString().toDouble()
                        }
                        sb.append(key).append("::").append(item[key] as String).append(";;")
                    }
                }
            }
        }
        val intent = Intent(context, MapActivity::class.java)
        intent.putExtra("title", arguments!!.getString("title")!!)
        intent.putExtra("data", sb.toString())
        intent.putExtra("Latitude", Latitude)
        intent.putExtra("Longitude", Longitude)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerView.adapter = PhotoListAdapter(arrayListOf(arguments!!.getString("url")),
                object : OnClickListener {
                    override fun click(arg1: Int, arg2: Any) {
                        showMoreDialog(arg2 as Bitmap)
                    }
                })
    }

    override fun lazyLoad() {

    }

    private fun showMoreDialog(bitmap: Bitmap) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("提示")
        builder.setMessage("请选择您想执行的操作")
        builder.setNegativeButton("分享") { dialog, _ ->
            dialog.dismiss()
            sharePics(bitmap)
        }
        builder.setPositiveButton("打印") { dialog, _ ->
            dialog.dismiss()
            printPics(bitmap)
        }
        builder.show()
    }

    private fun printPics(bitmap: Bitmap) {
        val photoPrinter = PrintHelper(activity!!)
        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
        photoPrinter.printBitmap("droids.jpg - test print", bitmap)
    }

    private fun sharePics(bitmap: Bitmap) {
        /** * 分享图片 */
        var share_intent = Intent()
        share_intent.action = Intent.ACTION_SEND//设置分享行为
        share_intent.type = "image/*"  //设置分享内容的类型
        share_intent.putExtra(
                Intent.EXTRA_STREAM,
                AppUtils.getImageContentUri(context!!, AppUtils.saveBitmap(bitmap, "sharepic", false))
        )
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "图片分享")
        activity!!.startActivity(share_intent)
    }

    /** * 从Assets中读取图片  */
    private fun getImageFromAssetsFile(fileName: String): Bitmap? {
        var image: Bitmap? = null
        val am = resources.assets
        try {
            val `is` = am.open(fileName)
            image = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(IDCardViewModel::class.java)
        }
        if (!mViewModel!!.updateAndLose()!!.hasObservers()) {
            mViewModel!!.updateAndLose()!!.observe(this, Observer {
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
                        mViewModel!!.freshUpdateAndLose(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateAndLose(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateAndLose(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            goMapPage(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
        }
    }

    private fun updateAndLose(recommend: String) {
        val map = HashMap<String, String>()
        map["type"] = arguments!!.getString("title")!!
        map["recommand"] = recommend
        map["current_city"] = mCurrentCity
//        map["current_city"] = "东莞市"
        mViewModel?.freshUpdateAndLose(map, true)
    }

    override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {

    }

    override fun onLocationChanged(location: TencentLocation?, error: Int, reason: String?) {
        if (error == TencentLocation.ERROR_OK) {
            if (location != null && location.district != null) {
                mCurrentCity = location.district
                mLocationManager?.removeUpdates(this)
            }
        } else {
            showToastMsg("定位失败")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationManager?.removeUpdates(this)
    }
}