package android.ocr.bmk.ui.detail

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.ui.adapter.ViewPagerAdapter
import android.ocr.bmk.ui.viewmodel.IDCardViewModel
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_certificate_detail.*
import kotlinx.android.synthetic.main.common_tool_bar.*
import java.util.HashMap
import android.graphics.BitmapFactory
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.utils.AppUtils
import java.io.IOException

class CertificateDetailActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_certificate_detail

    private var mTitles = arrayListOf<String>()

    private var mPagers = arrayListOf<Fragment>()

    private var mAdapter: ViewPagerAdapter? = null

    override fun initViews() {
        initToolBar()
        initViewPager()
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { finish() }
        tvToolBarTitle.text = intent.getStringExtra("title")
    }

    private fun initViewPager() {
        with(mTitles) {
            add("图片")
            add("详情")
        }
        with(mPagers) {
            add(PhotoFragment.newInstance(intent.getStringExtra("title"), intent.getStringExtra("url")))
            add(DetailFragment.newInstance(intent.getStringExtra("title")))
        }
        mAdapter = ViewPagerAdapter(supportFragmentManager, mPagers, mTitles)
        mViewPager.adapter = mAdapter
    }
}