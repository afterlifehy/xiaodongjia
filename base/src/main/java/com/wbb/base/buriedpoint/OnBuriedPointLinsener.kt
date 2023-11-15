package com.wbb.base.buriedpoint


/**
 * Created by zj on 2020/12/9.
 * 埋点接口
 */
interface OnBuriedPointLinsener {
    /**
     * 一键登录
     */
    fun OneClickLogin(json: String)

    /**
     * 图形验证码调用
     */
    fun onRegisterLoginGraphic(operation_type: String, is_succeed: Boolean)

    /**
     * 短信验证码
     */
    fun onRegisterLoginSMS(operation_type: String, telephone: String, is_succeed: Boolean)

    /**
     * 登录结果
     */
    fun onLoginResult(operation_type: String, is_succeed: Boolean, reason_failure: String)

    /**
     * 首页按钮点击
     */
    fun ButtonClick(
        locate_tab: String,
        locate_page: String,
        button_name: String
    )

    //-----------------------------------------------------------------------------------------
    /**
     * 首页tab点击
     */
    fun OnTabClickTabClick(tab_name: String)

    /**
     * 是否获取到权限
     */
    fun OnGetUserLocation(is_open: String, is_succeed: String)

    /**
     * 选择城市
     */
    fun OnCitiesSwitch(city: String, tab_name: String, is_succeed: String)

    /**
     * 首页tab点击
     */

    fun OnClassifyingLists(locate_tab: String, locate_page: String, classifyinglists_name: String)

    /**
     * 首页菜单点击
     */
    fun OnIconClick(icon_id: Int, icon_name: String, page_name: String, tab_name: String)

    /**
     * 搜索
     */
    fun AppSearch(key_word: String, is_hotword: Int, is_history: Int)

    /**
     * 课堂搜索
     */
    fun CourseSearch(key_word: String, is_hotword: Int, is_history: Int)

    /**
     * 搜索结果
     */
    fun SearchResultView(searchresult_cat: String, title: String, title_url: String)

    /**
     * 扫码
     */
    fun ScanCode(shop_code: String, is_succeed: Int, reason_failure: String)

    /**
     * 东家充值
     */
    fun DjRecharge(shop_name: String, dj_payamount: String, is_succeed: Int, pay_failure: String)

    /**
     * 商家账单支付
     */
    fun PayOrder(
        discount_amount: String,
        plus_payamount: String,
        dj_payamount: String,
        total_payamount: String,
        pay_type: String,
        pay_is_succeed: String,
        pay_failure: String,
        shop_code: String
    )

    /**
     * 海报分享
     */
    fun SavePoster(share_way: String, share_is__succeed: String, recommend_poster: String)

    /**
     * 课程浏览
     */
    fun onCourseView(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        watch_duration: String
    )

    /**
     * 课程分享
     */
    fun onCourseShare(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        is_share: String,
        share_way: String
    )

    /**
     * 课程是否收藏
     */
    fun onCourseCollection(
        course_cat: String,
        course_name: String,
        course_chapter: String,
        is_collection: String
    )

    /**
     * 商户点击
     */
    fun onArticleView(
        article_id: String,
        article_name: String,
        article_cat: String,
        is_collection: String,
        distiance_cat: String,
        shop_cat1: String,
        shop_cat2: String,
        enter_source: String
    )

    /**
     * plus充值
     */
    fun PlusRecharge(plus_payamount: String, is_succeed: Int, pay_failure: String)

    /**
     * 邀请信息埋点埋点
     */
    fun FillInfo_FillInfo(
        telephone: String,
        nickname: String,
        recommend: String,
        is_succeed: String
    )
}