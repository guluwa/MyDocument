package android.ocr.bmk.data.bean

class PassportBean {

    var log_id = ""
    var words_result_num = ""
    var words_result: Any = Any()

//    big-training.com/ocr/ocr.php?type=passport&mode=write&username=baimingkai&
//    CountryCode=CHN&Name=袁运筹&NamePinyin=SC&Sex=男/M&PassportNumber=E21202282&
//    BirthDate=24NOV1990&BirthAddess=江苏/JIANGSU&IssueDate=08月/AUG2014&
//    ExpirationDate=078月/AUG2024&IssuePlace=福建/FUJIAN

    var CountryCode = ""
    var Name = ""
    var NamePinyin = ""
    var Sex = ""
    var PassportNumber = ""
    var BirthDate = ""
    var BirthAddess = ""
    var IssueDate = ""
    var ExpirationDate = ""
    var IssuePlace = ""

    override fun toString(): String {
        if (CountryCode != "" && Name != "" && NamePinyin != "" && Sex != "" &&
                PassportNumber != "" && BirthDate != "" && BirthAddess != "" &&
                IssueDate != "" && ExpirationDate != "" && IssuePlace != "") {
            return "国家码：" + CountryCode + "\n" +
                    "姓名：" + Name + "\n" +
                    "姓名拼音：" + NamePinyin + "\n" +
                    "性别：" + Sex + "\n" +
                    "护照号码：" + PassportNumber + "\n" +
                    "生日：" + BirthDate + "\n" +
                    "出生地点：" + BirthAddess + "\n" +
                    "签发日期：" + IssueDate + "\n" +
                    "有效期至：" + ExpirationDate + "\n" +
                    "护照签发地点：" + IssuePlace
        } else {
            return ""
        }
    }
}