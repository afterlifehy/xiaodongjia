package com.wbb.base.mvvm.base

import com.wbb.base.bean.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * Created by zj on 2021/2/23.
 */
interface Api {
    /**
     * 登录接口
     */
    @POST("login/loginMsg")
    suspend fun mobileLogin(@Body login: Map<String, String>): HttpWrapper<String>

    /**
     * 极验code
     */
//    @POST("Common/getCode")
//    suspend fun getJyCode(@Body login: Map<String, String>): HttpWrapper<String>
    @POST("geetest/getCode")
    suspend fun getJyCode(@Body login: Map<String, String>): HttpWrapper<String>

    /**
     *发送登录短信
     */
    @POST("login/sendMsgGeetest")
    suspend fun sendMsgGeetest(@Body login: Map<String, String>): HttpWrapper<String>

    /**
     *短信登录
     */
    @POST("login/loginSgw/loginByMsg")
    suspend fun loginSms(@Body login: Map<String, String>): HttpWrapper<UserInfo>


    /**
     *极验一键登录
     */
    @POST("login/loginSgw/loginTokenVerify")
    suspend fun loginTokenVerify(@Body login: Map<String, String>): HttpWrapper<UserInfo>

    /**
     * 直接/间接推荐列表
     */
    @POST("member/getRecommendList")
    suspend fun getRecommendList(@Body par: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<List<RecommendListBean>>

    /**
     * 获取关注列表
     */
    @POST("follow/getFollowList")
    suspend fun getFollowList(@Body par: Map<String, String>): HttpWrapper<PaginationInfo<MerchantFolownfo>>

    /**
     * 添加关注
     */
    @POST("follow/addFollow")
    suspend fun addFollow(@Body par: Map<String, String>): HttpWrapper<String>

    /**
     * 关注搜索
     */
    @POST("getIsFollowList")
    suspend fun getIsFollowList(@Body par: Map<String, String>): HttpWrapper<String>

    /**
     *商户分类
     */
    @GET("category")
    suspend fun getCategory(@QueryMap par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<MutableList<HomeMenuInfo>>

    /**
     *获取已开通城市列表
     */
    @GET("openCity")
    suspend fun getOpenCity(): HttpWrapper<MutableList<CityItemInfo>>

    /**
     * 保存用户昵称和推荐人
     */
    @POST("login/saveNameRecommend")
    suspend fun saveNameRecommend(@Body par: Map<String, String>): HttpWrapper<String>


    /**
     * 活动分页列表
     */
    @GET("activity/list")
    suspend fun activityList(@QueryMap par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<List<LifeCircleActivityBean>>

    /**
     * 我的
     */
    @POST("user/userInfoView")
    suspend fun userInfoView(): HttpWrapper<MineBean>

    /**
     * 账单查询
     */
    @POST("portal/order/getOrder")
    suspend fun getOrder(@Body par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<PaginationInfo<BillListBean>>

    /**
     * 账单详情查询
     */
    @POST("portal/order/getOrderDetail")
    suspend fun getOrderDetail(@Body par: @JvmSuppressWildcards Map<String, String>): HttpWrapper<BillDetailBean>

    /**
     * 获取商户列表
     */
    @GET("search")
    suspend fun getMerchantSearchList(@QueryMap par: Map<String, String>): HttpWrapper<PaginationInfo<MerchantInfo>>

    /**
     *获取所有城市
     */
    @GET("basic/geo/list/allSort")
    suspend fun getAllSort(): HttpWrapper<MutableList<CityItemInfo>>

    /**
     *获取所有城市
     */
    @POST("user/getLevelCount")
    suspend fun getLevelCount(): HttpWrapper<Boolean>


    /**
     * 账单详情查询
     */
    @POST("portal/order/getMemberCard")
    suspend fun getMemberCard(): HttpWrapper<List<CardPackageBean>>

    /**
     *获取首页WBB数量
     */
    @POST("portal/member/getProfit")
    suspend fun getProfit(): HttpWrapper<ProfitInfo>

    /**
     * 搜索数目统计
     */
    @GET("count")
    suspend fun count(@QueryMap par: Map<String, String>): HttpWrapper<SearchCountBean>

    /**
     * 商户课程活动搜索
     */
    @GET("searchAll")
    suspend fun searchAll(@QueryMap par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<PaginationInfo<SearchResultBean>>

    /**
     *获取推荐
     */
    @POST("portal/user/getSourceMaterial")
    suspend fun getSourceMaterial(@Body par: @JvmSuppressWildcards Map<String, String>): HttpWrapper<List<CommenTools>>

    /**
     *获取热门搜索
     */
    @POST("hot/search/key/getKeyList")
    suspend fun getKeyList(@Body par: Map<String, String>): String

    /**
     * 获取基础配置数据
     */
    @GET("merchant/portal/activity/basicConfig")
    suspend fun getConfigSet(@QueryMap par: Map<String, String>): HttpWrapper<CityChildItemInfo>

    /**
     * 课程搜索
     */
    @GET("courseTypeSearch")
    suspend fun courseTypeSearch(@QueryMap par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<PaginationInfo<DongClassBean>>

    /**
     * 获取课程播放列表
     */
    @GET("courseList")
    suspend fun courseList(@QueryMap par: @JvmSuppressWildcards Map<String, String>): HttpWrapper<PaginationInfo<VideoPlayInfo>>

    /**
     * 买单页余额查询
     */
    @POST("portal/order/ֺgetPay")
    suspend fun getPay(@Body par: Map<String, String>): HttpWrapper<GetPayBalanceBean>

    /**
     * 下单并支付
     */
    @POST("portal/order/addOrderAndPay")
    suspend fun addOrderAndPay(@Body par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<PayResultBean>

    /**
     * 微信下单
     */
    @POST("portal/order/addOrder")
    suspend fun addOrder(@Body par: @JvmSuppressWildcards Map<String, Any>): HttpWrapper<BillListBean>

    /**
     * 充值预创建订单
     */
    @POST("portal/order/initRecharge")
    suspend fun initRecharge(@Body par: Map<String, String>): HttpWrapper<RechargeResultBean>

    /**
     * 获取课程播放详情
     */
    @GET("courseDetail")
    suspend fun getCourseDetail(@QueryMap par: @JvmSuppressWildcards Map<String, String>): HttpWrapper<VideoPlayInfo>


    /**
     * 获取消息列表
     */
    @GET("merchant/portal/getMineMsgPage")
    suspend fun getMineMsgPage(@QueryMap par: @JvmSuppressWildcards Map<String, String>): HttpWrapper<PaginationInfo<MsgData>>

    /**
     * app检查更新
     */
    @POST("common/appVersion")
    suspend fun appVersion(): HttpWrapper<UpdateBean>

    /**
     * 查询支付结果
     */
    @POST("portal/order/getPayStatus")
    suspend fun getPayStatus(@Body par: Map<String, String>): HttpWrapper<PayStatusBean>

    /**
     * 用户端查询商户详情
     */
    @GET("merchantStoreDetail")
    suspend fun merchantStoreDetail(@QueryMap par: Map<String, String>): HttpWrapper<MerchantDetailBean>
}