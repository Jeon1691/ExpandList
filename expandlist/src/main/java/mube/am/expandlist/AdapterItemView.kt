package bootpay.co.kr.expandabletest

import android.support.annotation.LayoutRes
import android.view.View

abstract class AdapterItemView<in T : Any> : IAdapterItemView<T> {
    @LayoutRes
    abstract override fun getLayoutResId(): Int

    lateinit override var view: View

    abstract override fun onBindViews(view: View)

    abstract override fun onSetViews()

    abstract override fun onUpdateViews(model: T, pos: Int)
}