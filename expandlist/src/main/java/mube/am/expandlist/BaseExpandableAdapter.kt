package bootpay.co.kr.expandabletest

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

abstract class BaseExpandableAdapter(val data: ArrayList<Any>) :
        RecyclerView.Adapter<BaseExpandableHolder>(),
        ExpandableViewOf.ParentItemListener<IExpandableModel> {

    val recyclerViewList by lazy { arrayListOf<RecyclerView>() }
    val threadPool: Scheduler by lazy { Schedulers.trampoline() }
    var listener: ExpandCollapseListener? = null

    init {
        checkExpand()
    }

    private fun checkExpand() {
        Flowable.just(data)
                .filter { it is IExpandableModel }
                .map { it as IExpandableModel }
                .filter { it.isExpanded }
                .doOnNext { data.addAll(data.indexOf(it) + 1, it.getChildItemList()) }
                .subscribeOn(threadPool)
                .subscribe { notifyItemRangeInserted(data.indexOf(it) + 1, data.indexOf(it) + 1 + it.getChildItemList().size) }
    }

    fun update(data: List<Any>) {
        Observable.just(this.data)
                .doOnNext { it.clear() }
                .doOnNext { add(data) }
                .subscribeOn(threadPool)
                .subscribe { checkExpand() }
    }

    fun add(item: Any) {
        Observable.just(item)
                .subscribeOn(threadPool)
                .doOnNext { data.add(item) }
    }

    fun add(pos: Int, item: Any) {
        Observable.just(item)
                .subscribeOn(threadPool)
                .doOnNext { data.add(pos, it) }
                .subscribe { notifyItemInserted(pos) }
    }

    fun add(items: List<Any>) {
        Observable.just(items)
                .subscribeOn(threadPool)
                .forEach { add(it) }
    }

    fun add(pos: Int, items: List<Any>) {
        Observable.just(Any())
                .subscribeOn(threadPool)
                .subscribe { items.forEachIndexed { i, item -> add(pos + i, item) } }
    }

    fun change(pos: Int, item: Any) {
        data.getOrNull(pos)?.let {
            data[pos] = item
            notifyItemChanged(pos, item)
        } ?: run {
            data.add(pos, item)
            notifyItemInserted(pos)
        }
    }

    fun remove(pos: Int) {
        data.getOrNull(pos)?.let { remove(it) }
    }

    fun remove(item: Any) {
        try {
            Observable.just(item)
                    .subscribeOn(threadPool)
                    .filter { it is IExpandableModel }
                    .map { it as IExpandableModel }
                    .doOnNext { it.isExpanded = false }
                    .map { it.getChildItemList() }
                    .subscribe { it.forEach { remove(it) } }
            Observable.just(data.indexOf(item))
                    .subscribeOn(threadPool)
                    .doOnNext { data.remove(item) }
                    .subscribe { notifyItemRemoved(it + 1) }
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onParentListItemCollapsed(item: IExpandableModel) {
        collapseParent(item, isTriggeredByItemClick = true)
    }

    override fun onParentListItemExpanded(item: IExpandableModel) {
        expandParent(item, isTriggeredByClick = true)
    }

    private fun collapseParent(listItem: IExpandableModel, isTriggeredByItemClick: Boolean = default@ false) {
        if (listItem.isExpanded) {
            listItem.isExpanded = false
            if (isTriggeredByItemClick) data.indexOf(listItem).let { index -> listener?.onListItemCollapsed(index - getExpandedItemCount(index)) }
            listItem.getChildItemList().forEach { remove(it) }
        }
    }


    fun getParents(isExpand: Boolean = false): List<IExpandableModel> {
        return data
                .filter { it is IExpandableModel }
                .map { it as IExpandableModel }
                .filter { it.isExpanded == isExpand }
    }

    protected fun expandParent(listItem: IExpandableModel, isTriggeredByClick: Boolean = default@ false) {
        if (!listItem.isExpanded) {
            data.indexOf(listItem).let { index ->
                Observable.just(listItem)
                        .doOnNext { it.getChildItemList().forEachIndexed { i, item -> add(index + i + 1, item) } }
                        .doOnNext { it.isExpanded = true }
                        .subscribeOn(threadPool)
                        .subscribe { if (isTriggeredByClick) listener?.onListItemExpanded(index + getExpandedItemCount(index)) }
            }
        }
    }

    fun expandParent(pos: Int) {
        data.getOrNull(pos)?.takeIf { it is IExpandableModel }?.let { expandParent(it as IExpandableModel) }
    }

    fun expandAllParents() {
        getParents().forEach { expandParent(it, isTriggeredByClick = false) }
    }

    private fun getExpandedItemCount(pos: Int): Int = data
            .slice(0..pos)
            .filter { it !is IExpandableModel }
            .count()

    protected fun getListItem(pos: Int): Any? = data.getOrNull(pos)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = TypeUtil.getIntType(data[position])

    @Suppress("unchecked cast")
    abstract fun <MODEL : Any> mapModelToView(listItem: MODEL): Any?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseExpandableHolder? =
            (mapModelToView(TypeUtil.getType(viewType)) as? IAdapterItemView<Any>)?.let { view -> BaseExpandableHolder(parent, view) }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseExpandableHolder, pos: Int) {
        data.getOrNull(pos)?.let { if (it is IExpandableModel) if (holder.item is ExpandableViewOf<*>) holder.item.listener = this }
        holder.item.onUpdateViews(data[pos], pos)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView?.let { recyclerViewList.add(it) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView?.let { recyclerViewList.remove(it) }
    }

    interface ExpandCollapseListener {
        fun onListItemExpanded(pos: Int)

        fun onListItemCollapsed(pos: Int)
    }

}
