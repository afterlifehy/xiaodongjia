package com.wbb.xiaodongjia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wbb.base.bean.CountrySortBean
import com.wbb.xiaodongjia.R
import java.util.*
import kotlin.collections.ArrayList

class CountryCodeAdapter(
    private var countryList: ArrayList<CountrySortBean>,
    val onClickListener: View.OnClickListener
) : RecyclerView.Adapter<CountryCodeAdapter.CountryViewHolder>(), SectionIndexer {
    override fun getSections(): Array<Any>? {
        return null
    }

    override fun getSectionForPosition(position: Int): Int {
        return countryList[position].sortLetters[0].toInt()
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        if (sectionIndex != 42) {
            for (i in 0 until itemCount) {
                val sortStr = countryList[i].sortLetters
                val firstChar = sortStr.toUpperCase(Locale.CHINESE)[0]
                if (firstChar.toInt() == sectionIndex) {
                    return i
                }
            }
        } else {
            return 0
        }

        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country, null)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        // 根据position获取分类的首字母的Char ascii值
        val country = countryList[position]
        val section = getSectionForPosition(position)

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tv_countryTitle.visibility = View.VISIBLE
            holder.tv_countryTitle.text = country.sortLetters
        } else {
            holder.tv_countryTitle.visibility = View.GONE
        }

        holder.tv_countryName.text = countryList[position].countryName
        holder.tv_countryCode.text = countryList[position].countryNumber
        holder.rl_country.tag = country
        holder.rl_country.setOnClickListener(onClickListener)
    }

    fun setNewData(allCountryList: ArrayList<CountrySortBean>) {
        this.countryList = allCountryList
        notifyDataSetChanged()
    }

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 国家码简拼所属的字母范围
        internal var tv_countryTitle: TextView = itemView.findViewById(R.id.tv_countryTitle)

        // 国家名
        internal var tv_countryName: TextView = itemView.findViewById(R.id.tv_countryName)

        // 代码
        internal var tv_countryCode: TextView = itemView.findViewById(R.id.tv_countryCode)

        internal var rl_country: RelativeLayout = itemView.findViewById(R.id.rl_country)
    }
}