package android.ocr.bmk.ui

import android.content.Intent
import android.ocr.bmk.R
import android.ocr.bmk.base.BaseActivity
import android.ocr.bmk.manage.Contacts
import android.ocr.bmk.ui.login.LoginActivity
import android.ocr.bmk.utils.AppUtils
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_splash

    override fun initViews() {
        if (AppUtils.getString(Contacts.NAME, "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
