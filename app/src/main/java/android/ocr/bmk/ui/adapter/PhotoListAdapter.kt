package android.ocr.bmk.ui.adapter

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.ocr.bmk.R
import android.ocr.bmk.databinding.PhotoListItemBinding
import android.ocr.bmk.ui.listener.OnClickListener
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class PhotoListAdapter(val list: List<String>, val listener: OnClickListener) :
    RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.photo_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(p0.itemView.context).asBitmap()
            .load(list[p1])
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(p0.mItemBinding.ivPhotoItem)
    }

    inner class ViewHolder(val mItemBinding: PhotoListItemBinding) : RecyclerView.ViewHolder(mItemBinding.root) {
        init {
            mItemBinding.ivPhotoItem.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(0, (mItemBinding.ivPhotoItem.drawable as BitmapDrawable).bitmap)
                }
            }
        }
    }
}