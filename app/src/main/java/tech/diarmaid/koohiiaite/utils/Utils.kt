package tech.diarmaid.koohiiaite.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Utility class for my application
 */
object Utils {

    fun toIntArray(list: List<Int>): IntArray {
        val ret = IntArray(list.size)
        var i = 0
        for (e in list)
            ret[i++] = e
        return ret
    }

    fun isNumeric(value: String): Boolean {
        return value.matches("\\d+".toRegex())
    }

    fun isKanji(value: Char): Boolean {
        return Character.UnicodeBlock.of(value) === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    }

    fun zipLiveData(vararg liveItems: LiveData<*>): MediatorLiveData<ArrayList<Any>> {
        return MediatorLiveData<ArrayList<Any>>().apply {
            val zippedObjects = ArrayList<Any>()
            liveItems.forEach {
                addSource(it) { item ->
                    if (!zippedObjects.contains(item as Any)) {
                        zippedObjects.add(item)
                    }
                    value = zippedObjects
                }
            }
        }
    }
}
