package android.ocr.bmk.ui.adapter

import android.databinding.DataBindingUtil
import android.ocr.bmk.R
import android.ocr.bmk.data.bean.CertificateBean
import android.ocr.bmk.data.bean.ListItemBean
import android.ocr.bmk.data.bean.PageTipBean
import android.ocr.bmk.databinding.DetailListItemBinding
import android.ocr.bmk.ui.listener.OnClickListener
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class DetailListAdapter(val list: List<Any>, val listener: OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (p1 == 0) {
            return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.detail_list_item, p0, false))
        } else {
            return ViewHolderTip(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.page_list_tip_item, p0, false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is ListItemBean) 0 else 1
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (getItemViewType(p1) == 0) {
            val item = list[p1] as ListItemBean
            (p0 as ViewHolder).mItemBinding.tvDetailListItemTitle.text = item.key
            p0.mItemBinding.tvDetailListItemContent.text = item.value
        } else {
            (p0 as ViewHolderTip).databinding.pageTipBean = list[p1] as PageTipBean
        }
    }

    inner class ViewHolder(val mItemBinding: DetailListItemBinding) : RecyclerView.ViewHolder(mItemBinding.root) {
        init {
            mItemBinding.tvDetailListItemContent.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(adapterPosition, list[adapterPosition])
                }
            }
        }
    }
}