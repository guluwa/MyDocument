package android.ocr.bmk.ui.detail

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.util.Log
import android.view.View
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.UiSettings
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.common_tool_bar.*

class MapActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_map

    override fun initViews() {
        initToolBar()
        val tencentMap = mMapView.map
        //设置地图中心点
        val latLng = LatLng(intent!!.getDoubleExtra("Latitude", 29.18), intent!!.getDoubleExtra("Longitude", 120.04))
        val cameraSigma = CameraUpdateFactory.newCameraPosition(
                CameraPosition(
                        latLng, //新的中心点坐标
                        15f, //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f
                )
        ) //偏航角 0~360° (正北方为0)
        // 移动地图
        tencentMap.moveCamera(cameraSigma)
        val marker = tencentMap.addMarker(MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker()).draggable(true))
        marker.showInfoWindow()// 设置默认显示一个infoWindow
        val uiSettings = tencentMap.uiSettings
        //设置logo到屏幕底部中心
//        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM)
        //设置比例尺到屏幕右下角
//        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM)
        //启用缩放手势(更多的手势控制请参考开发手册)
        uiSettings.isZoomGesturesEnabled = true
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { finish() }
        tvToolBarTitle.text = intent.getStringExtra("title")
        tvRightBtn.text = "详情"
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            val i = Intent(this, UpdateLoseInfoActivity::class.java)
            i.putExtra("data", intent.getStringExtra("data"))
            startActivity(i)
        }
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onResume() {
        mMapView.onResume()
        super.onResume()
    }

    override fun onStop() {
        mMapView.onStop()
        super.onStop()
    }
}
