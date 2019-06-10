package android.ocr.bmk.data.bean

class BusinessLicenseBean {

    var log_id = ""
    var words_result_num = ""
    var words_result: Any = Any()

//    big-training.com/ocr/ocr.php?type=business_license&mode=write&username=baimingkai&
//    UnitName=袁氏财团有限公司&Type=有限责任公司（自然人独资）&LegalPerson=袁运筹&
//    Address=江苏省南京市中山东路19号&ExpirationDate=2015年02月12日&
//    CredentialID=苏餐证字(2019)第666602666661号&SocialCreditCode=无

    var UnitName = ""
    var Type = ""
    var LegalPerson = ""
    var Address = ""
    var ExpirationDate = ""
    var CredentialID = ""
    var SocialCreditCode = ""

    override fun toString(): String {
        if (UnitName != "" && Type != "" && LegalPerson != "" && Address != "" &&
                ExpirationDate != "" && CredentialID != "" && SocialCreditCode != "") {
            return "社会信用代码：" + SocialCreditCode + "\n" +
                    "单位名称：" + UnitName + "\n" +
                    "法人：" + LegalPerson + "\n" +
                    "证件编号：" + CredentialID + "\n" +
                    "地址：" + Address + "\n" +
                    "类型：" + Type + "\n" +
                    "有效期：" + ExpirationDate
        } else {
            return ""
        }
    }
}