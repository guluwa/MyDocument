package android.ocr.bmk.ui.adapter

import android.databinding.DataBindingUtil
import android.ocr.bmk.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.ocr.bmk.data.bean.CertificateBean
import android.ocr.bmk.data.bean.PageTipBean
import android.ocr.bmk.databinding.MainMenuItemBinding
import android.ocr.bmk.ui.listener.OnClickListener

class MainMenuAdapter(val list: List<Any>, val listener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (p1 == 0) {
            return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.main_menu_item, p0, false))
        } else {
            return ViewHolderTip(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.page_list_tip_item, p0, false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is CertificateBean) 0 else 1
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (getItemViewType(p1) == 0) {
            val item = list[p1] as CertificateBean
            (p0 as ViewHolder).mItemBinding.tvMainMenu.text = item.name
        } else {
            (p0 as ViewHolderTip).databinding.pageTipBean = list[p1] as PageTipBean
        }
    }

    inner class ViewHolder(val mItemBinding: MainMenuItemBinding) : RecyclerView.ViewHolder(mItemBinding.root) {
        init {
            mItemBinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, adapterPosition)
                }
            }
            mItemBinding.root.setOnLongClickListener {
                if (adapterPosition != -1) {
                    listener.click(2, adapterPosition)
                }
                return@setOnLongClickListener false
            }
        }
    }
}