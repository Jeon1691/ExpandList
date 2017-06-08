package bootpay.co.kr.expandabletest


abstract class ExpandableViewOf<T : IExpandableModel> : AdapterItemView<T>(), IExpandableView {
    var expandedListItem: T? = null

    var listener: ParentItemListener<T>? = null

    override fun onUpdateViews(model: T, pos: Int) {
        expandedListItem = model
    }

    interface ParentItemListener<in T : IExpandableModel> {
        fun onParentListItemExpanded(item: T)
        fun onParentListItemCollapsed(item: T)
    }

    abstract fun onExpansionToggle(isExpanded: Boolean = false)

    override fun expand() {
        onExpansionToggle(isExpanded = true)
        expandedListItem?.let { listener?.onParentListItemExpanded(it) }
    }

    override fun collapse() {
        onExpansionToggle(isExpanded = false)
        expandedListItem?.let { listener?.onParentListItemCollapsed(it) }
    }

    override fun doExpandOrCollapse() {
        expandedListItem?.let {
            when (it.isExpanded) {
                true  -> collapse()
                false -> expand()
            }
        }
    }
}