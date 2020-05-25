package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Represent an entry in the heisig_kanji table
 */
@Entity(tableName = "heisig_kanji")
data class HeisigKanji(
        @PrimaryKey @ColumnInfo(name = "id") var heisigId: Int = 0,
        var kanji: String = "",
        var joyo: Boolean = false) {

    companion object {

        /**
         * They are stored 1 indexed in the database.
         * This should be used for display purposes.
         *
         * If they are to be used for indexing other tables should
         * get the 0 Indexed list of ids instead.
         */
        fun getIds1Indexed(list: List<HeisigKanji>): List<String> {
            val ids = ArrayList<String>()

            for (hk in list) {
                ids.add(hk.heisigId.toString())
            }

            return ids
        }

        fun getHeisigKanjiMatchingIds(ids: MutableList<Int>, masterList: List<HeisigKanji>): List<HeisigKanji> {
            val filteredList = ArrayList<HeisigKanji>()
            ids.sort()

            for (id in ids) {
                //convert to 0 notation when referencing java array
                filteredList.add(masterList[id - 1])
            }

            return filteredList
        }

        /**
         * Return 4 digit Heisig Frame number for display
         */
        fun getHeisigIdAsString(heisigId: Int): String {
            var prefixZeros = ""

            if (heisigId < 1000) {
                prefixZeros += "0"
                if (heisigId < 100) {
                    prefixZeros += "0"
                    if (heisigId < 10) {
                        prefixZeros += "0"
                    }
                }
            }

            return prefixZeros + heisigId

            // 1 - 0001
            // 10 - 0010
            // 100 - 0100
            // 1000 - 1000
        }
    }
}
