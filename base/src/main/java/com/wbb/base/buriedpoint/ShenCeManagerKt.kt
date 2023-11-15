package com.wbb.base.buriedpoint

import android.text.TextUtils
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.wbb.base.BuildConfig
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by zj on 2019/7/22.
 */
class ShenCeManagerKt private constructor() : OnBuriedPointLinsener {
    companion object {
        //测试
        const val SC_API_URL_TEST =
            "https://datasink.water-bridge.ltd/sa?project=default&token=safdb6167c"

        //正式
        const val SC_API_URL_FORML = "https://datasink.water-bridge.ltd/sa?project=production"

        //神策URl
        private var SC_API_URL = SC_API_URL_FORML

        init {
            if (BuildConfig.ISDEV == 4) {//正式环境
                SC_API_URL = SC_API_URL_FORML
            } else {
                SC_API_URL = SC_API_URL_TEST
            }
        }

        fun getSC_URL(): String {
            return SC_API_URL
        }

        private var instance: ShenCeManagerKt? = null
            get() {
                if (field == null) {
                    field = ShenCeManagerKt()
                }
                return field
            }

        fun get(): ShenCeManagerKt {
            return instance!!
        }
    }

    /**
     * 生成事件
     */
    fun clickPrefix(clickStr: String): String {
        return projectPrefix() + clickStr
    }

    /**
     * 事件统一拼接
     */
    fun projectPrefix(): String {
        return "JF_"
    }

    fun registerSuperProperties() {
        // 将'平台类型'作为事件公共属性，后续所有触发事件都会添加上 "app_name" 属性，且属性值为 "Android"
        try {
            val properties = JSONObject()
//            properties.put("app_name", BaseApplication.instance().packageName)
//            properties.put("app_id", AppUtil.getVerName())
            properties.put("role", "用户")
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun OneClickLogin(description: String) {
        val properties = JSONObject()
        properties.put("operation_type", description)
        SensorsDataAPI.sharedInstance().track(clickPrefix("OneLoginClick"), properties)
    }

    /**
     * 图形验证码调用
     */
    override fun onRegisterLoginGraphic(operation_type: String, is_succeed: Boolean) {
        val properties = JSONObject()
        properties.put("operation_type", operation_type)
        properties.put("is_succeed", booleanToInt(is_succeed))
        SensorsDataAPI.sharedInstance().track(clickPrefix("RegisterLoginGraphic"), properties)
    }

    override fun onRegisterLoginSMS(
        operation_type: String,
        telephone: String,
        is_succeed: Boolean
    ) {
        val properties = JSONObject()
        properties.put("operation_type", operation_type)
        properties.put("telephone", telephone)
        properties.put("is_succeed", booleanToInt(is_succeed))
        SensorsDataAPI.sharedInstance().track(clickPrefix("RegisterLoginSMS"), properties)
    }

    fun booleanToInt(is_succeed: Boolean): Int {
        return if (is_succeed) 1 else 0
    }

    override fun onLoginResult(
        operation_type: String,
        is_succeed: Boolean,
        reason_failure: String
    ) {
        val properties = JSONObject()
        properties.put("operation_type", operation_type)
        properties.put("is_succeed", booleanToInt(is_succeed))
        properties.put("reason_failure", reason_failure)
        SensorsDataAPI.sharedInstance().track(clickPrefix("LoginResult"), properties)
    }

    override fun ButtonClick(locate_tab: String, locate_page: String, button_name: String) {
        val properties = JSONObject()
        properties.put("locate_tab", locate_tab)
        properties.put("locate_page", locate_page)
        properties.put("button_name", button_name)
        SensorsDataAPI.sharedInstance().track(clickPrefix("ButtonClick"), properties)
    }

    override fun OnTabClickTabClick(tab_name: String) {
        val properties = JSONObject()
        properties.put("tab_name", tab_name)
        SensorsDataAPI.sharedInstance().track(clickPrefix("TabClick"), properties)
    }

    override fun OnGetUserLocation(is_open: String, is_succeed: String) {
        val properties = JSONObject()
        properties.put("is_succeed", is_succeed)
        properties.put("is_open", is_open)
        SensorsDataAPI.sharedInstance().track(clickPrefix("GetUserLocation"), properties)
    }

    override fun OnCitiesSwitch(city: String, tab_name: String, is_succeed: String) {
        val properties = JSONObject()
        properties.put("city", city)
        properties.put("tab_name", tab_name)
        properties.put("is_succeed", is_succeed)
        SensorsDataAPI.sharedInstance().track(clickPrefix("CitiesSwitch"), properties)
    }

    override fun OnClassifyingLists(
        locate_tab: String,
        locate_page: String,
        classifyinglists_name: String
    ) {
        val properties = JSONObject()
        properties.put("locate_tab", locate_tab)
        properties.put("locate_page", locate_page)
        properties.put("classifyinglists_name", classifyinglists_name)
        SensorsDataAPI.sharedInstance().track(clickPrefix("ClassifyingLists"), properties)
    }

    override fun OnIconClick(icon_id: Int, icon_name: String, page_name: String, tab_name: String) {
        val properties = JSONObject()
        properties.put("icon_id", icon_id)
        properties.put("icon_name", icon_name)
        properties.put("page_name", page_name)
        properties.put("tab_name", tab_name)
        SensorsDataAPI.sharedInstance().track(clickPrefix("IconClick"), properties)
    }

    override fun AppSearch(key_word: String, is_hotword: Int, is_history: Int) {
        val properties = JSONObject()
        properties.put("key_word", key_word)
        properties.put("is_hotword", is_hotword)
        properties.put("is_history", is_history)
        SensorsDataAPI.sharedInstance().track(clickPrefix("AppSearch"), properties)
    }

    override fun CourseSearch(key_word: String, is_hotword: Int, is_history: Int) {
        val properties = JSONObject()
        properties.put("key_word", key_word)
        properties.put("is_hotword", is_hotword)
        properties.put("is_history", is_history)
        SensorsDataAPI.sharedInstance().track(clickPrefix("CourseSearch"), properties)
    }

    override fun SearchResultView(searchresult_cat: String, title: String, title_url: String) {
        val properties = JSONObject()
        properties.put("searchresult_cat", searchresult_cat)
        properties.put("title", title)
        properties.put("title_url", title_url)
        SensorsDataAPI.sharedInstance().track(clickPrefix("SearchResultView"), properties)
    }

    override fun ScanCode(shop_code: String, is_succeed: Int, reason_failure: String) {
        val properties = JSONObject()
        properties.put("shop_code", shop_code)
        properties.put("is_succeed", is_succeed)
        properties.put("reason_failure", reason_failure)
        SensorsDataAPI.sharedInstance().track(clickPrefix("ScanCode"), properties)
    }

    override fun DjRecharge(
        shop_name: String,
        dj_payamount: String,
        is_succeed: Int,
        pay_failure: String
    ) {
        val properties = JSONObject()
        properties.put("shop_name", shop_name)
        properties.put("dj_payamount", dj_payamount.toDouble())
        properties.put("is_succeed", is_succeed)
        properties.put("pay_failure", pay_failure)
        SensorsDataAPI.sharedInstance().track(clickPrefix("DjRecharge"), properties)
    }

    override fun PayOrder(
        discount_amount: String,
        plus_payamount: String,
        dj_payamount: String,
        total_payamount: String,
        pay_type: String,
        pay_is_succeed: String,
        pay_failure: String,
        shop_code: String
    ) {
        //支付金额可能为空
        if (TextUtils.isEmpty(discount_amount) || TextUtils.isEmpty(plus_payamount) || TextUtils.isEmpty(
                dj_payamount
            ) || TextUtils.isEmpty(total_payamount)
        ) {
            return

        }
        val properties = JSONObject()
        properties.put("discount_amount", discount_amount.toDouble())
        properties.put("plus_payamount", plus_payamount.toDouble())
        properties.put("dj_payamount", dj_payamount.toDouble())
        properties.put("total_payamount", total_payamount.toDouble())
        properties.put("pay_type", pay_type)
        properties.put("pay_is_succeed", pay_is_succeed)
        properties.put("pay_failure", pay_failure)
        properties.put("shop_code", shop_code)
        SensorsDataAPI.sharedInstance().track(clickPrefix("PayOrder"), properties)
    }

    override fun SavePoster(
        share_way: String,
        share_is__succeed: String,
        recommend_poster: String
    ) {
        val properties = JSONObject()
        properties.put("share_way", share_way)
        properties.put("share_is__succeed", share_is__succeed)
        properties.put("recommend_poster", recommend_poster)
        SensorsDataAPI.sharedInstance().track(clickPrefix("SavePoster"), properties)
    }

    override fun onCourseView(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        watch_duration: String
    ) {
        val properties = JSONObject()
        properties.put("course_cat", course_cat)
        properties.put("course_name", course_name)
        properties.put("course_chapter", course_chapter)
        properties.put("watch_duration", watch_duration)
        SensorsDataAPI.sharedInstance().track(clickPrefix("CourseView"), properties)
    }

    override fun onCourseShare(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        is_share: String,
        share_way: String
    ) {
        val properties = JSONObject()
        properties.put("course_cat", course_cat)
        properties.put("course_name", course_name)
        properties.put("course_chapter", course_chapter)
        properties.put("is_share", is_share)
        properties.put("share_way", share_way)
        SensorsDataAPI.sharedInstance().track(clickPrefix("CourseShare"), properties)
    }

    override fun onCourseCollection(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        is_collection: String
    ) {
        val properties = JSONObject()
        properties.put("course_cat", course_cat)
        properties.put("course_name", course_name)
        properties.put("course_chapter", course_chapter)
        properties.put("is_collection", is_collection)
        SensorsDataAPI.sharedInstance().track(clickPrefix("CourseCollection"), properties)
    }

    override fun onArticleView(
        article_id: String,
        article_name: String,
        article_cat: String,
        is_collection: String,
        distiance_cat: String,
        shop_cat1: String,
        shop_cat2: String,
        enter_source: String
    ) {
        val properties = JSONObject()
        properties.put("article_id", article_id)
        properties.put("article_name", article_name)
        properties.put("article_cat", article_cat)
        properties.put("is_collection", is_collection)

        properties.put("distiance_cat", distiance_cat)
        properties.put("shop_cat1", shop_cat1)
        properties.put("shop_cat2", shop_cat2)
        properties.put("enter_source", enter_source)
        SensorsDataAPI.sharedInstance().track(clickPrefix("ArticleView"), properties)
    }

    override fun PlusRecharge(plus_payamount: String, is_succeed: Int, pay_failure: String) {
        val properties = JSONObject()
        properties.put("plus_payamount", plus_payamount)
        properties.put("is_succeed", is_succeed)
        properties.put("pay_failure", pay_failure)
        SensorsDataAPI.sharedInstance().track(clickPrefix("ArticleView"), properties)
    }

    override fun FillInfo_FillInfo(
        telephone: String,
        nickname: String,
        recommend: String,
        is_succeed: String
    ) {
        val properties = JSONObject()
        properties.put("telephone", telephone)
        properties.put("nickname", nickname)
        properties.put("recommend", recommend)
        properties.put("is_succeed", is_succeed)
        SensorsDataAPI.sharedInstance().track(clickPrefix("FillInfo"), properties)

    }

    fun loginReg(account: String) {
        SensorsDataAPI.sharedInstance().login(account)
    }

}
