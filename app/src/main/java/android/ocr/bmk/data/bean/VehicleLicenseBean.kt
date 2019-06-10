package android.ocr.bmk.data.bean

class VehicleLicenseBean {

    var log_id = ""
    var words_result_num = ""
    var words_result: Any = Any()

//    big-training.com/ocr/ocr.php?type=vehicle_license&mode=write&username=baimingkai&
//    BrandModel=保时捷GT37182RUCRE&IssueDate=20160104&UsageCharacteristics=非营运&
//    EngineNumber=20832&NumberBrand=苏A001&Owner=圆圆&Address=南京市江宁区弘景大道&
//    RegisterDate=20160104&VehicleIdentificationCode=HCE58&VehicleType=小型轿车

    var BrandModel = ""
    var IssueDate = ""
    var UsageCharacteristics = ""
    var EngineNumber = ""
    var NumberBrand = ""
    var Owner = ""
    var Address = ""
    var RegisterDate = ""
    var VehicleIdentificationCode = ""
    var VehicleType = ""

    override fun toString(): String {
        if (BrandModel != "" && IssueDate != "" && UsageCharacteristics != "" &&
                EngineNumber != "" && NumberBrand != "" && Owner != "" &&
                Address != "" && RegisterDate != "" && VehicleIdentificationCode != "" &&
                VehicleType != "") {
            return "品牌型号：" + BrandModel + "\n" +
                    "发证日期：" + IssueDate + "\n" +
                    "使用性质：" + UsageCharacteristics + "\n" +
                    "发动机号码：" + EngineNumber + "\n" +
                    "号牌号码：" + NumberBrand + "\n" +
                    "所有人：" + Owner + "\n" +
                    "住址：" + Address + "\n" +
                    "注册日期：" + RegisterDate + "\n" +
                    "车辆识别代号：" + VehicleIdentificationCode + "\n" +
                    "车辆类型：" + VehicleType
        } else {
            return ""
        }
    }
}