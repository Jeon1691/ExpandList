package bootpay.co.kr.expandabletest

interface IExpandableModel {

    var isExpanded: Boolean

    fun getChildItemList(): List<Any>
}
