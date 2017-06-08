package bootpay.co.kr.expandabletest

import android.util.SparseArray

object TypeUtil {

    private val typeSArr = SparseArray<Any>()

    fun getIntType(type: Any): Int {
        val value = typeSArr.indexOfValue(type)
        return if (value < 0) typeSArr.size().also { typeSArr.append(it, type) } else value
    }

    fun getType(int: Int): Any = typeSArr.get(int)
}
