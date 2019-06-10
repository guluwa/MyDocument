package android.ocr.bmk.ui.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.MainActivity
import android.ocr.bmk.ui.viewmodel.LoginViewModel
import android.ocr.bmk.utils.AppUtils
import android.ocr.bmk.utils.FinishActivityManager
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_register

    private var mViewModel: LoginViewModel? = null

    private var type = "2"

    private var sex = "1"

    override fun initViews() {
        initImage()
        initClickEvent()
    }

    private fun initImage() {
        Glide.with(this).asBitmap()
                .apply(RequestOptions().circleCrop())
                .load(R.mipmap.app_launcher)
                .into(ivAppLogo)
    }

    private fun initClickEvent() {
        ivBack.setOnClickListener { finish() }
        tvRegister.setOnClickListener {
            userRegister()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        if (!mViewModel!!.userRegister()!!.hasObservers()) {
            mViewModel!!.userRegister()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showToastMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        showToastMsg(parseErrorDate(if (it.error!!.message == "") "注册失败" else it.error.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        if (it.data!!.code == 0) {
                            showToastMsg("注册成功")
                            AppUtils.setString(Contacts.NAME, etAccount.text.toString().trim())
                            FinishActivityManager.getInstance().finishAllActivity()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            showToastMsg("注册失败")
                        }
                    }
                }
            })
        }
    }

    private fun userRegister() {
        if (TextUtils.isEmpty(etAccount.text)) {
            showToastMsg("请输入用户名")
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            showToastMsg("请输入密码")
            return
        }
        if (TextUtils.isEmpty(etName.text)) {
            showToastMsg("请输入昵称")
            return
        }
        if (TextUtils.isEmpty(etPhone.text)) {
            showToastMsg("请输入手机号")
            return
        }
        if (TextUtils.isEmpty(etEmail.text)) {
            showToastMsg("请输入邮箱")
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etAccount.text.toString().trim()
        map["password"] = etPassword.text.toString().trim()
        map["name"] = etName.text.toString().trim()
        map["tel"] = etPhone.text.toString().trim()
        map["email"] = etEmail.text.toString().trim()
        map["tid"] = type
        map["sex"] = sex
        mViewModel?.freshUserRegister(map, true)
    }
}
