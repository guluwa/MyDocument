package android.ocr.bmk.data.bean

class DrivingLicenseBean {

    var log_id = ""
    var words_result_num = ""
    var words_result: Any = Any()

//    big-training.com/ocr/ocr.php?type=driving_license&mode=write&username=baimingkai&
//    CertificateNumber=3208231999053090&ValidityPeriod=6年&VehicleType=B2&
//    EffectiveStartDate=20101125&Address=江苏省南通市海门镇秀山新城&Name=小欧欧&
//    Nationality=中国&BirthDate=19990530&Sex=男&FirstIssueDate=20100125


    var CertificateNumber = ""
    var ValidityPeriod = ""
    var VehicleType = ""
    var EffectiveStartDate = ""
    var Address = ""
    var Name = ""
    var Nationality = ""
    var BirthDate = ""
    var Sex = ""
    var FirstIssueDate = ""

    override fun toString(): String {
        if (CertificateNumber != "" && ValidityPeriod != "" && VehicleType != "" &&
                EffectiveStartDate != "" && Address != "" && Name != "" &&
                Nationality != "" && BirthDate != "" && Sex != "" && FirstIssueDate != "") {
            return "证号：" + CertificateNumber + "\n" +
                    "有效期限：" + ValidityPeriod + "\n" +
                    "准驾车型：" + VehicleType + "\n" +
                    "有效起始日期：" + EffectiveStartDate + "\n" +
                    "住址：" + Address + "\n" +
                    "姓名：" + Name + "\n" +
                    "国籍：" + Nationality + "\n" +
                    "出生日期：" + BirthDate + "\n" +
                    "性别：" + Sex + "\n" +
                    "初次领证日期：" + FirstIssueDate
        } else {
            return ""
        }
    }
}