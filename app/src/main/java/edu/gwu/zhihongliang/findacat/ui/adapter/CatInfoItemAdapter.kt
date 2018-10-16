package edu.gwu.zhihongliang.findacat.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.squareup.picasso.Picasso
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import kotlinx.android.synthetic.main.layout_list_item.view.*


class CatInfoItemAdapter(private val catInfoList: List<CatInfo>,
                         private val context: Context,
                         private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(catInfo: CatInfo, itemView: View)
    }

    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(catInfo: CatInfo, listener: OnItemClickListener) = with(itemView) {
            val uri = catInfo.photo
            Picasso.with(context).load(uri).resize(Const.IMAGE_SIZE, Const.IMAGE_SIZE).centerCrop().into(cat_Image)
            cat_name.text = catInfo.name
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

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).bind(catInfoList[position], clickListener)
        setAnimation(holder.itemView, position);
    }

    override fun getItemCount(): Int {
        return catInfoList.size
    }

}