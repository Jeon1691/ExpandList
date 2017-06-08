package bootpay.co.kr.expandabletest

import android.view.View

interface IAdapterItemView<in T: Any> {
    fun getLayoutResId(): Int

    var view: View

    fun onBindViews(view: View)

    fun onSetViews()

    fun onUpdateViews(model: T, pos: Int)
}