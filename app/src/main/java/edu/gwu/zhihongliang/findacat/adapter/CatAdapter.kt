package edu.gwu.zhihongliang.findacat.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import kotlinx.android.synthetic.main.layout_list_item.view.*

class CatAdapter(private val catInfoList: List<CatInfo>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(catInfo: CatInfo, listener: OnItemClickListener) = with(itemView) {
            val uri = catInfo.photo
            Picasso.with(context).load(uri).into(cat_Image)
            setOnClickListener {
                listener.onItemClick(catInfo, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.layout_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).bind(catInfoList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return catInfoList.size
    }

    interface OnItemClickListener {
        fun onItemClick(catInfo: CatInfo, itemView: View)
    }

}