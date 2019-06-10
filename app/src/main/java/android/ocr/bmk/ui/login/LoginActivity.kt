package android.ocr.bmk.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.data.bean.PageStatus
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.MainActivity
import android.ocr.bmk.ui.register.RegisterActivity
import android.ocr.bmk.ui.viewmodel.LoginViewModel
import android.ocr.bmk.utils.AppUtils
import android.ocr.bmk.utils.FinishActivityManager
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_login

    private var mViewModel: LoginViewModel? = null

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
        tvRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        tvLogin.setOnClickListener {
            userLogin()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        if (!mViewModel!!.passwordLogin()!!.hasObservers()) {
            mViewModel!!.passwordLogin()!!.observe(this, Observer {
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
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        showToastMsg(parseErrorDate(if (it.error!!.message == "") "登录失败" else it.error.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        if (it.data!!.code == 0) {
                            showToastMsg("登录成功")
                            AppUtils.setString(Contacts.NAME, etAccount.text.toString().trim())
                            FinishActivityManager.getInstance().finishAllActivity()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            showToastMsg("登录失败")
                        }
                    }
                }
            })
        }
    }

    private fun userLogin() {
        if (TextUtils.isEmpty(etAccount.text)) {
            showToastMsg("请输入用户名")
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            showToastMsg("请输入密码")
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etAccount.text.toString().trim()
        map["password"] = etPassword.text.toString().trim()
        mViewModel?.freshPasswordLogin(map, true)
    }
}
