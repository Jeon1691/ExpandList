package bootpay.co.kr.expandabletest

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

open class BaseExpandableHolder(parent: ViewGroup, val item: IAdapterItemView<Any>) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(item.getLayoutResId(), parent, attachToRoot@ false)) {
    init {
        itemView.isClickable = true
        item.onBindViews(itemView)
        item.view = itemView
        item.onSetViews()
    }
}