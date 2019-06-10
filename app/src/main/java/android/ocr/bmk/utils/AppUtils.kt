package android.ocr.bmk.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.ocr.bmk.manage.MyApplication
import android.os.Environment
import android.preference.PreferenceManager
import java.io.File
import java.io.FileOutputStream
import android.provider.MediaStore
import android.content.ContentValues
import android.net.Uri
import android.net.Uri.withAppendedPath
import android.ocr.bmk.data.bean.*
import android.util.Log
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import java.io.StringReader
import java.lang.Exception
import java.lang.StringBuilder


object AppUtils {
    //获取sharePreference String类型的值
    fun getString(key: String, defaultValue: String): String {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getContext())
        return if (settings.getString(key, defaultValue) == null) "" else settings.getString(key, defaultValue)!!
    }

    //设置sharePreference String类型的值
    fun setString(key: String, value: String) {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getContext())
        settings.edit().putString(key, value).apply()
    }

    /**
     * 检测网络是否连接
     */
    val isNetConnected: Boolean
        get() {
            val cm = MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo != null) {
                return true
            }
            return false
        }

    /**
     * 保存图片
     */
    fun saveBitmap(bitmap: Bitmap, fileName: String, needRecycle: Boolean): File {
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/baiedu")
        if (!file.exists()) {
            file.mkdirs()
        }
        val realFile = File(file, fileName)
        if (!realFile.exists()) {
            realFile.createNewFile()
        }
        val fos = FileOutputStream(realFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        if (!bitmap.isRecycled && needRecycle) {
            bitmap.recycle()
            System.gc() // 通知系统回收
        }
        return realFile
    }

    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                return null
            }
        }
    }

    fun getType(mCurrentCertificateBean: CertificateBean?): String {
        return if (mCurrentCertificateBean == null) {
            ""
        } else {
            when (mCurrentCertificateBean.name) {
                "身份证" -> "id_card"
                "户口本" -> "household"
                "护照" -> "passport"
                "营业执照" -> "business_license"
                "驾驶证" -> "driving_license"
                "行驶证" -> "vehicle_license"
                "出生医学证明" -> "birth_certificate"
                "港澳通行证" -> "hk_macau_exit_entry_permit"
                else -> "taiwan_exit_entry_permit"
            }
        }
    }

    fun getVehicleLicenseBean(result: String): VehicleLicenseBean {
        var vehicleLicenseBean: VehicleLicenseBean
        try {
            vehicleLicenseBean = Gson().fromJson(result, VehicleLicenseBean::class.java)
            if (vehicleLicenseBean.words_result is LinkedTreeMap<*, *>) {
                val map = vehicleLicenseBean.words_result as LinkedTreeMap<*, *>
                for (key in map.keys) {
                    val item = map[key]
                    if (item is LinkedTreeMap<*, *>) {
                        when (key) {
                            "品牌型号" -> {
                                vehicleLicenseBean.BrandModel = parseWords(item)
                            }
                            "发证日期" -> {
                                vehicleLicenseBean.IssueDate = parseWords(item)
                            }
                            "使用性质" -> {
                                vehicleLicenseBean.UsageCharacteristics = parseWords(item)
                            }
                            "发动机号码" -> {
                                vehicleLicenseBean.EngineNumber = parseWords(item)
                            }
                            "号牌号码" -> {
                                vehicleLicenseBean.NumberBrand = parseWords(item)
                            }
                            "所有人" -> {
                                vehicleLicenseBean.Owner = parseWords(item)
                            }
                            "住址" -> {
                                vehicleLicenseBean.Address = parseWords(item)
                            }
                            "注册日期" -> {
                                vehicleLicenseBean.RegisterDate = parseWords(item)
                            }
                            "车辆识别代号" -> {
                                vehicleLicenseBean.VehicleIdentificationCode =
                                        parseWords(item)
                            }
                            "车辆类型" -> {
                                vehicleLicenseBean.VehicleType = parseWords(item)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            vehicleLicenseBean = VehicleLicenseBean()
        }
        return vehicleLicenseBean
    }

    fun getDrivingLicenseBean(result: String): DrivingLicenseBean {
        var drivingLicenseBean: DrivingLicenseBean
        try {
            drivingLicenseBean = Gson().fromJson(result, DrivingLicenseBean::class.java)
            if (drivingLicenseBean.words_result is LinkedTreeMap<*, *>) {
                val map = drivingLicenseBean.words_result as LinkedTreeMap<*, *>
                Log.e("error", map.toString())
                for (key in map.keys) {
                    val item = map[key]
                    if (item is LinkedTreeMap<*, *>) {
                        when (key) {
                            "证号" -> {
                                drivingLicenseBean.CertificateNumber = parseWords(item)
                            }
                            "有效期限" -> {
                                drivingLicenseBean.ValidityPeriod = parseWords(item)
                            }
                            "准驾车型" -> {
                                drivingLicenseBean.VehicleType = parseWords(item)
                            }
                            "有效起始日期" -> {
                                drivingLicenseBean.EffectiveStartDate = parseWords(item)
                            }
                            "住址" -> {
                                drivingLicenseBean.Address = parseWords(item)
                            }
                            "姓名" -> {
                                drivingLicenseBean.Name = parseWords(item)
                            }
                            "国籍" -> {
                                drivingLicenseBean.Nationality = parseWords(item)
                            }
                            "出生日期" -> {
                                drivingLicenseBean.BirthDate = parseWords(item)
                            }
                            "性别" -> {
                                drivingLicenseBean.Sex =
                                        parseWords(item)
                            }
                            "初次领证日期" -> {
                                drivingLicenseBean.FirstIssueDate = parseWords(item)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            drivingLicenseBean = DrivingLicenseBean()
        }
        return drivingLicenseBean
    }

    private fun parseWords(map: LinkedTreeMap<*, *>): String {
        return map["words"] as String
    }

    fun getBusinessLicenseBean(result: String): BusinessLicenseBean {
        var businessLicenseBean: BusinessLicenseBean
        try {
            businessLicenseBean = Gson().fromJson(result, BusinessLicenseBean::class.java)
            if (businessLicenseBean.words_result is LinkedTreeMap<*, *>) {
                val map = businessLicenseBean.words_result as LinkedTreeMap<*, *>
                Log.e("error", map.toString())
                for (key in map.keys) {
                    val item = map[key]
                    if (item is LinkedTreeMap<*, *>) {
                        when (key) {
                            "社会信用代码" -> {
                                businessLicenseBean.SocialCreditCode = parseWords(item)
                            }
                            "单位名称" -> {
                                businessLicenseBean.UnitName = parseWords(item)
                            }
                            "法人" -> {
                                businessLicenseBean.LegalPerson = parseWords(item)
                            }
                            "证件编号" -> {
                                businessLicenseBean.CredentialID = parseWords(item)
                            }
                            "地址" -> {
                                businessLicenseBean.Address = parseWords(item)
                            }
                            "类型" -> {
                                businessLicenseBean.Type =
                                        parseWords(item)
                            }
                            "有效期" -> {
                                businessLicenseBean.ExpirationDate = parseWords(item)
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            businessLicenseBean = BusinessLicenseBean()
        }
        return businessLicenseBean
    }

    fun getPassportBean(result: String): PassportBean {
        var passportBean: PassportBean
        try {
            passportBean = Gson().fromJson(result, PassportBean::class.java)
            if (passportBean.words_result is LinkedTreeMap<*, *>) {
                val map = passportBean.words_result as LinkedTreeMap<*, *>
                Log.e("error", map.toString())
                for (key in map.keys) {
                    val item = map[key]
                    if (item is LinkedTreeMap<*, *>) {
                        when (key) {
                            "国家码" -> {
                                passportBean.CountryCode = parseWords(item)
                            }
                            "护照签发地点" -> {
                                passportBean.IssuePlace = parseWords(item)
                            }
                            "有效期至" -> {
                                passportBean.ExpirationDate = parseWords(item)
                            }
                            "护照号码" -> {
                                passportBean.PassportNumber = parseWords(item)
                            }
                            "签发日期" -> {
                                passportBean.IssueDate = parseWords(item)
                            }
                            "出生地点" -> {
                                passportBean.BirthAddess =
                                        parseWords(item)
                            }
                            "姓名" -> {
                                passportBean.Name = parseWords(item)
                            }
                            "姓名拼音" -> {
                                passportBean.NamePinyin = parseWords(item)
                            }
                            "生日" -> {
                                passportBean.BirthDate = parseWords(item)
                            }
                            "性别" -> {
                                passportBean.Sex = parseWords(item)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            passportBean = PassportBean()
        }
        return passportBean
    }
}