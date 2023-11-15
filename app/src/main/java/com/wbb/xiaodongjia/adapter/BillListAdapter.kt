package com.wbb.xiaodongjia.adapter

import android.view.View
import com.aries.ui.view.radius.RadiusLinearLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wbb.base.adapter.CustomBaseQuickAdapter
import com.wbb.base.ext.i18n
import com.wbb.xiaodongjia.R
import com.wbb.base.bean.BillListBean
import com.wbb.base.util.DateUtil
import com.wbb.base.util.GlideUtils

/**
 * Created by hy on 2021/2/3.
 */
class BillListAdapter(data: MutableList<BillListBean>?, val onClickListener: View.OnClickListener) : CustomBaseQuickAdapter<BillListBean, BaseViewHolder>(R.layout.item_bill, data) {

    override fun convert(holder: BaseViewHolder, item: BillListBean) {
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_shopLogo), item.merchantImg, R.mipmap.ic_tools_placeholder)
        GlideUtils.getInstance().loadImage(holder.getView(R.id.riv_goodsLogo), item.headIcon, R.mipmap.ic_header_placeholder)
        holder.setText(R.id.tv_shopName, item.merchantName)
        holder.setText(R.id.tv_paymentStatus, item.status)
        holder.setText(R.id.tv_orderSn, i18n(R.string.订单号) + "：" + item.orderNo)
        if (item.isRecharge) {
            holder.setText(R.id.tv_paymentMethod, i18n(R.string.充值方式) + "：" + item.payType)
            holder.setText(R.id.tv_paymentTime, i18n(R.string.充值时间) + "：" + DateUtil.getLongToString(item.payTime, "yyyy-MM-dd HH:mm"))
        } else {
            holder.setText(R.id.tv_paymentMethod, i18n(R.string.付款方式) + "：" + item.payType)
            holder.setText(R.id.tv_paymentTime, i18n(R.string.付款时间) + "：" + DateUtil.getLongToString(item.payTime, "yyyy-MM-dd HH:mm"))
        }
        holder.setText(R.id.tv_paymentAmount, "¥ " + item.amount)
        holder.getView<RadiusLinearLayout>(R.id.rll_bill).tag = item
        holder.getView<RadiusLinearLayout>(R.id.rll_bill).setOnClickListener(onClickListener)
    }
}