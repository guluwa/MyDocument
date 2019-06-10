package android.ocr.bmk.ui

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.IdCardBean
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.viewmodel.IDCardViewModel
import android.ocr.bmk.utils.AppUtils
import android.ocr.bmk.utils.FileUtil
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.IDCardParams
import com.baidu.ocr.sdk.model.IDCardResult
import com.baidu.ocr.ui.camera.CameraActivity
import com.baidu.ocr.ui.camera.CameraNativeHelper
import com.baidu.ocr.ui.camera.CameraView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_idcard.*
import kotlinx.android.synthetic.main.common_tool_bar.*
import java.io.File
import java.util.HashMap

class IDCardActivity : BaseActivity() {

    private var mFrontPicPath = ""

    private var mBackPicPath = ""

    override val viewLayoutId: Int get() = R.layout.activity_idcard

    //data
    private var mViewModel: IDCardViewModel? = null

    private var mIdCardBean = IdCardBean()

    private var mFileName = ""

    override fun initViews() {
        initToolBar()
        initCamera()
        initClickEvent()
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { finish() }
        tvToolBarTitle.text = "身份证"
        tvRightBtn.text = "保存"
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            saveIdCard()
        }
    }

    private fun initCamera() {
        //  初始化本地质量控制模型,释放代码在onDestory中
        //  调用身份证扫描必须加上 intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true); 关闭自动初始化和释放本地模型
        CameraNativeHelper.init(
                this, OCR.getInstance(this).license
        ) { errorCode, e ->
            val msg: String
            when (errorCode) {
                CameraView.NATIVE_SOLOAD_FAIL -> msg = "加载so失败，请确保apk中存在ui部分的so"
                CameraView.NATIVE_AUTH_FAIL -> msg = "授权本地质量控制token获取失败"
                CameraView.NATIVE_INIT_FAIL -> msg = "本地质量控制"
                else -> msg = errorCode.toString()
            }
            Log.e("error", "本地质量控制初始化错误，错误原因： $msg")
        }
    }

    private fun initClickEvent() {
        // 身份证正面拍照
        mFrontCardView.setOnClickListener(View.OnClickListener {
            mFileName = "idcard_front_" + System.currentTimeMillis()
            val intent = Intent(this@IDCardActivity, CameraActivity::class.java)
            intent.putExtra(
                    CameraActivity.KEY_OUTPUT_FILE_PATH,
                    FileUtil.getSaveFile(application, mFileName).absolutePath
            )
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        })

        // 身份证反面拍照
        mBackCardView.setOnClickListener(View.OnClickListener {
            mFileName = "idcard_back_" + System.currentTimeMillis()
            val intent = Intent(this@IDCardActivity, CameraActivity::class.java)
            intent.putExtra(
                    CameraActivity.KEY_OUTPUT_FILE_PATH,
                    FileUtil.getSaveFile(application, mFileName).absolutePath
            )
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK)
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        })
    }

    private fun recIDCard(idCardSide: String, filePath: String?) {
        val param = IDCardParams()
        param.imageFile = File(filePath)
        // 设置身份证正反面
        param.idCardSide = idCardSide
        // 设置方向检测
        param.isDetectDirection = true
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.imageQuality = 20

        OCR.getInstance(this).recognizeIDCard(param, object : OnResultListener<IDCardResult> {
            override fun onResult(result: IDCardResult?) {
                if (result != null) {
                    if (idCardSide == IDCardParams.ID_CARD_SIDE_FRONT) {
                        mIdCardBean.name = if (result.name == null) "" else result.name.words
                        mIdCardBean.address = if (result.address == null) "" else result.address.words
                        mIdCardBean.card_number = if (result.idNumber == null) "" else result.idNumber.words
                        mIdCardBean.date_of_birth = if (result.birthday == null) "" else result.birthday.words
                        mIdCardBean.sex = if (result.gender == null) "" else result.gender.words
                        mIdCardBean.nationality = if (result.ethnic == null) "" else result.ethnic.words
                    } else {
                        mIdCardBean.signDate = if (result.signDate == null) "" else result.signDate.words
                        mIdCardBean.expiration_date = if (result.expiryDate == null) "" else result.expiryDate.words
                        mIdCardBean.issuing_authority =
                                if (result.issueAuthority == null) "" else result.issueAuthority.words
                    }
                }
            }

            override fun onError(error: OCRError) {
                showToastMsg(error.message ?: "识别失败")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE)
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT == contentType) {
                        //加载并识别
                        mFrontPicPath =
                                FileUtil.getSaveFile(applicationContext, mFileName)
                                        .absolutePath
                        Glide.with(this@IDCardActivity).asBitmap()
                                .apply(RequestOptions().centerCrop())
                                .load(mFrontPicPath)
                                .into(ivFrontPhoto)
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, mFrontPicPath)
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK == contentType) {
                        //加载并识别
                        mBackPicPath =
                                FileUtil.getSaveFile(applicationContext, mFileName)
                                        .absolutePath
                        Glide.with(this@IDCardActivity).asBitmap()
                                .apply(RequestOptions().centerCrop())
                                .load(mBackPicPath)
                                .into(ivBackPhoto)
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, mBackPicPath)
                    }
                }
            }
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(IDCardViewModel::class.java)
        }
        if (!mViewModel!!.uploadPicture()!!.hasObservers()) {
            mViewModel!!.uploadPicture()!!.observe(this, Observer {
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
            mViewModel!!.saveIdCard()!!.observe(this, Observer {
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
                            finish()
                        } else {
                            showToastMsg(getString(R.string.network_request_error))
                        }
                    }
                }
            })
        }
    }

    private fun saveIdCard() {
        if (mIdCardBean.name != "" && mIdCardBean.sex != "" && mIdCardBean.nationality != "" &&
                mIdCardBean.date_of_birth != "" && mIdCardBean.address != "" &&
                mIdCardBean.card_number != "" && mIdCardBean.signDate != "" &&
                mIdCardBean.expiration_date != "" && mIdCardBean.issuing_authority != "") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("请确认信息是否正确")
            val sb = StringBuilder()
            sb.append("姓名：").append(mIdCardBean.name).append("\n")
            sb.append("性别：").append(mIdCardBean.sex).append("\n")
            sb.append("民族：").append(mIdCardBean.nationality).append("\n")
            sb.append("生日：").append(mIdCardBean.date_of_birth).append("\n")
            sb.append("地址：").append(mIdCardBean.address).append("\n")
            sb.append("身份证号：").append(mIdCardBean.card_number).append("\n")
            sb.append("有效期：").append(mIdCardBean.signDate).append("-").append(mIdCardBean.expiration_date).append("\n")
            sb.append("签发机关：").append(mIdCardBean.issuing_authority).append("\n")
            builder.setMessage(sb.toString())
            builder.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            builder.setPositiveButton("保存") { dialog, _ ->
                savePicInfo()
                dialog.dismiss()
            }
            builder.setCancelable(false)
            builder.show()
        } else {
            showToastMsg("信息识别失败，请重新拍照")
        }
    }

    private fun savePicInfo() {
        val map = HashMap<String, String>()
        map["path"] = mFrontPicPath
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["type"] = "id_card"
        mViewModel?.freshUploadPicture(map, true)
    }

    private fun saveTextInfo() {
        val map = HashMap<String, String>()
        map["type"] = "id_card"
        map["mode"] = "write"
        map["username"] = AppUtils.getString(Contacts.NAME, "")
        map["name"] = mIdCardBean.name
        map["sex"] = mIdCardBean.sex
        map["nationality"] = mIdCardBean.nationality
        map["date_of_birth"] = mIdCardBean.date_of_birth
        map["address"] = mIdCardBean.address
        map["card_number"] = mIdCardBean.card_number
        map["issuing_authority"] = mIdCardBean.issuing_authority
        map["expiration_date"] = mIdCardBean.expiration_date
        mViewModel?.freshSaveIdCard(map, true)
    }

    override fun onDestroy() {
        // 释放本地质量控制模型
        CameraNativeHelper.release()
        super.onDestroy()
    }

    companion object {

        private val REQUEST_CODE_CAMERA = 102
    }
}
