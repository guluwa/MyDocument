package android.ocr.bmk.ui

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.*
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.adapter.MainMenuAdapter
import android.ocr.bmk.ui.detail.CertificateDetailActivity
import android.ocr.bmk.ui.listener.OnClickListener
import android.ocr.bmk.ui.login.LoginActivity
import android.ocr.bmk.ui.viewmodel.MainViewModel
import android.ocr.bmk.utils.AppUtils
import android.ocr.bmk.utils.FileUtil
import android.ocr.bmk.utils.RecognizeService
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.ui.camera.CameraActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_tool_bar.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {

    companion object {

        private val REQUEST_CODE_BUSINESS_LICENSE = 123

        private val REQUEST_CODE_PASSPORT = 125

        private val REQUEST_CODE_DRIVING_LICENSE = 121

        private val REQUEST_CODE_VEHICLE_LICENSE = 120
    }

    override val viewLayoutId: Int get() = R.layout.activity_main

    /**拖拽功能 */
    private var itemTouchHelper: ItemTouchHelper? = null
    private var currentPagePosition = -1//当前拖拽的item的原始位置，从0开始【长按时赋值】，用来和currentPageNewPosition对比进行判断是否执行排序接口
    private var currentPageNewPosition = -1//当前item拖拽后的位置，从0开始
    private var newOrder = false//标记是否拖拽排序过，默认是false

    //RecyclerView
    private var mAdapter: MainMenuAdapter? = null
    private val list = arrayListOf<Any>(PageTipBean("", 0))

    //data
    private var mViewModel: MainViewModel? = null

    private var mCurrentCertificateBean: CertificateBean? = null

    private var mFileName = ""

    private var mPicPath = ""

    private var mVehicleLicenseBean: VehicleLicenseBean? = null

    private var mDrivingLicenseBean: DrivingLicenseBean? = null

    private var mBusinessLicenseBean: BusinessLicenseBean? = null

    private var mPassportBean: PassportBean? = null

    override fun initViews() {
        initToolBar()
        initRecyclerView()
        initAccessTokenWithAkSk()
    }

    private fun initToolBar() {
        ivBack.visibility = View.GONE
        tvToolBarTitle.text = getString(android.ocr.bmk.R.string.app_name)
        tvRightBtn.text = "退出"
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            AppUtils.setString(Contacts.NAME, "")
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                return if (list[p0] is PageTipBean) 3 else 1
            }
        }
        mRecyclerView.layoutManager = layoutManager
        mAdapter = MainMenuAdapter(list, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                if (arg1 == 1) {
                    mCurrentCertificateBean = list[arg2 as Int] as CertificateBean
                    when (mCurrentCertificateBean!!.name) {
                        "身份证", "营业执照", "护照", "驾驶证", "行驶证" -> {
                            queryPicture()
                        }
                        else -> {
                            showToastMsg("暂时不能添加")
                        }
                    }
                } else {
                    Log.e("error", "LongClick $arg2")
                    currentPagePosition = arg2 as Int
                }
            }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun distinguishOrDetail(isInput: Boolean, url: String) {
        val intent: Intent
        if (isInput) {
            intent = Intent(this, CertificateDetailActivity::class.java)
            intent.putExtra("title", mCurrentCertificateBean!!.name)
            intent.putExtra("url", url)
            startActivity(intent)
        } else {
            when (mCurrentCertificateBean!!.name) {
                "身份证" -> {
                    intent = Intent(this, IDCardActivity::class.java)
                    startActivity(intent)
                }
                "营业执照" -> {
                    mFileName = "business_license_" + System.currentTimeMillis()
                    intent = Intent(this@MainActivity, CameraActivity::class.java)
                    intent.putExtra(
                            CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(application, mFileName).absolutePath
                    )
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL)
                    startActivityForResult(intent, REQUEST_CODE_BUSINESS_LICENSE)
                }
                "护照" -> {
                    mFileName = "passport_" + System.currentTimeMillis()
                    intent = Intent(this@MainActivity, CameraActivity::class.java)
                    intent.putExtra(
                            CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(application, mFileName).absolutePath
                    )
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_PASSPORT)
                    startActivityForResult(intent, REQUEST_CODE_PASSPORT)
                }
                "驾驶证" -> {
                    mFileName = "drive_license_" + System.currentTimeMillis()
                    intent = Intent(this@MainActivity, CameraActivity::class.java)
                    intent.putExtra(
                            CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(application, mFileName).absolutePath
                    )
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL)
                    startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE)
                }
                "行驶证" -> {
                    mFileName = "vehicle_license_" + System.currentTimeMillis()
                    intent = Intent(this@MainActivity, CameraActivity::class.java)
                    intent.putExtra(
                            CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(application, mFileName).absolutePath
                    )
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL)
                    startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE)
                }
                else -> {
                    showToastMsg("暂时不能添加")
                }
            }
        }
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            //开启长按拖拽功能，默认为true【暂时用不到】
            //如果需要我们自定义拖拽和滑动，可以设置为false，然后调用itemTouchHelper.startDrag(ViewHolder)方法来开启！
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            //开始滑动功能，默认为true【暂时用不到】
            //如果需要我们自定义拖拽和滑动，可以设置为false，然后调用itemTouchHelper.startSwipe(ViewHolder)方法来开启！
            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            /*用于设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
              比如如果是列表类型的RecyclerView，拖拽只有UP、DOWN两个方向
              而如果是网格类型的则有UP、DOWN、LEFT、RIGHT四个方向
              */
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                var dragFlags = 0//dragFlags 是拖拽标志
                var swipeFlags = 0//swipeFlags是侧滑标志，我们把swipeFlags 都设置为0，表示不处理滑动操作
                if (recyclerView.layoutManager is GridLayoutManager) {
                    dragFlags =
                            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    swipeFlags = 0
                } else {
                    dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    swipeFlags = 0
                }
                Log.w("ItemTouchHelper", "{getMovementFlags}dragFlags=$dragFlags;swipeFlags=$swipeFlags")
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            /*如果我们设置了非0的dragFlags ，那么当我们长按item的时候就会进入拖拽并在拖拽过程中不断回调onMove()方法
              我们就在这个方法里获取当前拖拽的item和已经被拖拽到所处位置的item的ViewHolder，
              有了这2个ViewHolder，我们就可以交换他们的数据集并调用Adapter的notifyItemMoved方法来刷新item
              */
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition//得到拖动ViewHolder的position
                val toPosition = target.adapterPosition//得到目标ViewHolder的position
                Log.w("ItemTouchHelper", "{onMove}fromPosition=$fromPosition;toPosition=$toPosition")
                //这里可以添加判断，实现某一项不可交换
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(list, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(list, i, i - 1)
                    }
                }
                mAdapter?.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            /*同理如果我们设置了非0的swipeFlags，我们在侧滑item的时候就会回调onSwiped的方法，我们不处理这个事件，空着就行了。
              */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            //我们希望拖拽的Item在拖拽的过程中发生震动或者颜色变深，这样就需要继续重写下面两个方法
            //当长按选中item的时候（拖拽开始的时候）调用
            //ACTION_STATE_IDLE：闲置状态
            //ACTION_STATE_SWIPE：滑动状态
            //ACTION_STATE_DRAG：拖拽状态
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                Log.w("ItemTouchHelper", "{onSelectedChanged}actionState=$actionState")
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.isPressed = true
//                    viewHolder?.itemView?.setBackgroundColor(parseColor("#ff0000"))//演示拖拽的时候item背景颜色加深（实际情况中去掉）
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            //当手指松开的时候（拖拽或滑动完成的时候）调用，这时候我们可以将item恢复为原来的状态（相对于背景颜色加深来说的）
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                Log.w("ItemTouchHelper", "{clearView}viewHolder.getAdapterPosition=" + viewHolder.adapterPosition)
                viewHolder.itemView.isPressed = false
                currentPageNewPosition = viewHolder.adapterPosition
                Log.w("ItemTouchHelper", "{clearView}currentPagePosition=$currentPagePosition")
                Log.w("ItemTouchHelper", "{clearView}currentPageNewPosition=$currentPageNewPosition")
                if (!(currentPagePosition == currentPageNewPosition)) {
                    newOrder = true
                    //执行其他方法，比如设置拖拽后的item为选中状态
                }

//                viewHolder.itemView.setBackgroundColor(Color.parseColor("#c5c5c5"))//演示拖拽的完毕后item背景颜色恢复原样（实际情况中去掉）
                mAdapter?.notifyDataSetChanged()//解决重叠问题
            }
        })
        //设置是否可以排序
        itemTouchHelper?.attachToRecyclerView(mRecyclerView)
    }

    private var hasGotToken = false

    /**
     * 用明文ak，sk初始化
     */
    private fun initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                hasGotToken = true
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                Log.e("error", "AK，SK方式获取token失败 ${error.message}")
            }
        }, applicationContext, "2gYdStwdHVLwBgDGDnb4noHo", "beTPxGrIaIPRiNGAchI19aNVioM6YQpg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 识别成功回调，行驶证识别
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            mPicPath = FileUtil.getSaveFile(applicationContext, mFileName).absolutePath
            RecognizeService.recVehicleLicense(this, mPicPath)
            { result ->
                mVehicleLicenseBean = AppUtils.getVehicleLicenseBean(result)
                val info = mVehicleLicenseBean.toString()
                if (info == "") {
                    showToastMsg("信息识别失败，请重新拍照")
                } else {
                    saveInfo(info)
                }
            }
        }

        // 识别成功回调，驾驶证识别
        if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {
            mPicPath = FileUtil.getSaveFile(applicationContext, mFileName).absolutePath
            RecognizeService.recDrivingLicense(this, mPicPath)
            { result ->
                mDrivingLicenseBean = AppUtils.getDrivingLicenseBean(result)
                val info = mDrivingLicenseBean.toString()
                if (info == "") {
                    showToastMsg("信息识别失败，请重新拍照")
                } else {
                    saveInfo(info)
                }
            }
        }

        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
            mPicPath = FileUtil.getSaveFile(applicationContext, mFileName).absolutePath
            RecognizeService.recBusinessLicense(this, mPicPath)
            { result ->
                mBusinessLicenseBean = AppUtils.getBusinessLicenseBean(result)
                val info = mBusinessLicenseBean.toString()
                if (info == "") {
                    showToastMsg("信息识别失败，请重新拍照")
                } else {
                    saveInfo(info)
                }
            }
        }

        // 识别成功回调，护照
        if (requestCode == REQUEST_CODE_PASSPORT && resultCode == Activity.RESULT_OK) {
            mPicPath = FileUtil.getSaveFile(applicationContext, mFileName).absolutePath
            RecognizeService.recPassport(this, mPicPath)
            { result ->
                mPassportBean = AppUtils.getPassportBean(result)
                val info = mPassportBean.toString()
                if (info == "") {
                    showToastMsg("信息识别失败，请重新拍照")
                } else {
                    saveInfo(info)
                }
            }
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        if (!mViewModel!!.mainMenu()!!.hasObservers()) {
            mViewModel!!.mainMenu()!!.observe(this, android.arch.lifecycle.Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showErrorMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshMainMenu(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshMainMenu(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshMainMenu(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            showMainMenu(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
            mViewModel!!.queryPicture()!!.observe(this, android.arch.lifecycle.Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showErrorMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("请稍候")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryPicture(hashMapOf(), false)
                        if (it.error!!.code == 4) {
                            distinguishOrDetail(false, "")
                        } else {
                            showErrorMsg(parseErrorDate(it.error.message).tip)
                        }
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryPicture(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryPicture(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            val url = "http://" + it.data.data!!.url.substring(13)
                            distinguishOrDetail(true, url)
                        } else {
                            distinguishOrDetail(false, "")
                        }
                    }
                }
            })
            mViewModel!!.uploadPicture()!!.observe(this, android.arch.lifecycle.Observer {
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
                        mViewModel!!.freshUploadPicture(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadPicture(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        mViewModel!!.freshUploadPicture(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            saveTextInfo()
                        } else {
                            dismissProgressDialog()
                            showToastMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
            mViewModel!!.saveIdCard()!!.observe(this, android.arch.lifecycle.Observer {
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
                        mViewModel!!.freshSaveIdCard(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSaveIdCard(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSaveIdCard(hashMapOf(), false)
                        if (it.data!!.code == 200 && it.data.data != null) {
                            showToastMsg("保存成功")
                        } else {
                            showToastMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun saveTextInfo() {
        when (mCurrentCertificateBean!!.name) {
            "行驶证" -> {
                saveVehicleLicense()
            }
            "驾驶证" -> {
                saveDrvingLicense()
            }
            "营业执照" -> {
                saveBusinessLicense()
            }
            "护照" -> {
                savePassport()
            }
        }
    }

    private fun loadData() {
        val data = AppUtils.getString(Contacts.MAIN_MENU, "")
        if (data != "") {
            Log.e("error", data)
            showMainMenu(Gson().fromJson(data, MainMenuBean::class.java).list as List<CertificateBean>)
        } else {
            val map = HashMap<String, String>()
            map["type"] = "document_types"
            mViewModel?.freshMainMenu(map, true)
        }
    }

    private fun queryPicture() {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val file = AppUtils.saveBitmap(bitmap, "ic_launcher.png", false)
        val map = HashMap<String, String>()
        map["path"] = file.absolutePath
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        mViewModel?.freshQueryPicture(map, true)
    }

    private fun showMainMenu(data: List<CertificateBean>) {
        list.clear()
        list.addAll(data)
        mAdapter?.notifyDataSetChanged()
        initItemTouchHelper()
    }

    private fun showErrorMsg(msg: String) {
        if (list.size == 1 && list[0] is PageTipBean) {
            (list[0] as PageTipBean).tip = msg
            (list[0] as PageTipBean).status = 1
            mAdapter?.notifyItemChanged(0)
        } else {
            showToastMsg(msg)
        }
    }

    private fun saveInfo(info: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("请确认信息是否正确")
        builder.setMessage(info)
        builder.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
        builder.setPositiveButton("保存") { dialog, _ ->
            savePicInfo()
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun savePicInfo() {
        val map = HashMap<String, String>()
        map["path"] = mPicPath
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        mViewModel?.freshUploadPicture(map, true)
    }

    private fun saveVehicleLicense() {
        val map = HashMap<String, String>()
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        map["mode"] = "write"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["BrandModel"] = mVehicleLicenseBean!!.BrandModel
        map["IssueDate"] = mVehicleLicenseBean!!.IssueDate
        map["UsageCharacteristics"] = mVehicleLicenseBean!!.UsageCharacteristics
        map["EngineNumber"] = mVehicleLicenseBean!!.EngineNumber
        map["NumberBrand"] = mVehicleLicenseBean!!.NumberBrand
        map["Owner"] = mVehicleLicenseBean!!.Owner
        map["Address"] = mVehicleLicenseBean!!.Address
        map["RegisterDate"] = mVehicleLicenseBean!!.RegisterDate
        map["VehicleIdentificationCode"] = mVehicleLicenseBean!!.VehicleIdentificationCode
        map["VehicleType"] = mVehicleLicenseBean!!.VehicleType
        mViewModel?.freshSaveIdCard(map, true)
    }

    private fun saveDrvingLicense() {
        val map = HashMap<String, String>()
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        map["mode"] = "write"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["CertificateNumber"] = mDrivingLicenseBean!!.CertificateNumber
        map["ValidityPeriod"] = mDrivingLicenseBean!!.ValidityPeriod
        map["VehicleType"] = mDrivingLicenseBean!!.VehicleType
        map["EffectiveStartDate"] = mDrivingLicenseBean!!.EffectiveStartDate
        map["Address"] = mDrivingLicenseBean!!.Address
        map["Name"] = mDrivingLicenseBean!!.Name
        map["Nationality"] = mDrivingLicenseBean!!.Nationality
        map["BirthDate"] = mDrivingLicenseBean!!.BirthDate
        map["Sex"] = mDrivingLicenseBean!!.Sex
        map["FirstIssueDate"] = mDrivingLicenseBean!!.FirstIssueDate
        mViewModel?.freshSaveIdCard(map, true)
    }

    private fun saveBusinessLicense() {
        val map = HashMap<String, String>()
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        map["mode"] = "write"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["UnitName"] = mBusinessLicenseBean!!.UnitName
        map["Type"] = mBusinessLicenseBean!!.Type
        map["LegalPerson"] = mBusinessLicenseBean!!.LegalPerson
        map["Address"] = mBusinessLicenseBean!!.Address
        map["ExpirationDate"] = mBusinessLicenseBean!!.ExpirationDate
        map["CredentialID"] = mBusinessLicenseBean!!.CredentialID
        map["SocialCreditCode"] = mBusinessLicenseBean!!.SocialCreditCode
        mViewModel?.freshSaveIdCard(map, true)
    }

    private fun savePassport() {
        val map = HashMap<String, String>()
        map["type"] = AppUtils.getType(mCurrentCertificateBean)
        map["mode"] = "write"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["CountryCode"] = mPassportBean!!.CountryCode
        map["Name"] = mPassportBean!!.Name
        map["NamePinyin"] = mPassportBean!!.NamePinyin
        map["Sex"] = mPassportBean!!.Sex
        map["PassportNumber"] = mPassportBean!!.PassportNumber
        map["BirthDate"] = mPassportBean!!.BirthDate
        map["BirthAddess"] = mPassportBean!!.BirthAddess
        map["IssueDate"] = mPassportBean!!.IssueDate
        map["IssuePlace"] = mPassportBean!!.IssuePlace
        map["ExpirationDate"] = mPassportBean!!.ExpirationDate
        mViewModel?.freshSaveIdCard(map, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放内存资源
        OCR.getInstance(this).release()
        // 保存menu
        AppUtils.setString(
                Contacts.MAIN_MENU,
                Gson().toJson(MainMenuBean(list as List<CertificateBean>), MainMenuBean::class.java)
        )
    }
}
