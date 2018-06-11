package tech.diarmaid.koohiiaite.enumeration

import android.util.Log

enum class FilterState(val stateNum: Int) {
    UNSET(0),
    YES(1),
    NO(2);

    companion object {

        fun getStateFor(stateNum: Int): FilterState {
            for (state in values()) {
                if (state.stateNum == stateNum) {
                    return state
                }
            }
            Log.e("FilterState", "Couldn't find given filter state : $stateNum")
            return UNSET
        }
    }
}