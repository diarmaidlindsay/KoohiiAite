package tech.diarmaid.koohiiaite.model

import java.util.*

/**
 * Represent an entry in the heisig_kanji table
 */
class HeisigKanji {
    var id: Int = 0
        private set
    var kanji: String = ""
        private set
    var isJoyo: Boolean = false
        private set

    val joyo: Int
        get() = if (isJoyo) 1 else 0

    constructor(id: Int, kanji: String, joyo: Boolean) {
        this.id = id
        this.kanji = kanji
        this.isJoyo = joyo
    }

    constructor(id: Int, kanji: String, joyo: Int) {
        this.id = id
        this.kanji = kanji
        this.isJoyo = joyo != 0
    }

    constructor() {
        this.id = 0
        this.kanji = ""
        this.isJoyo = false
    }

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
                ids.add(hk.id.toString())
            }

            return ids
        }

        /**
         * For indexing other collections, using the heisigId as the index
         */
        fun getIds0Indexed(list: List<HeisigKanji>): Array<String> {
            val ids = ArrayList<String>()

            for (hk in list) {
                ids.add((hk.id - 1).toString())
            }

            return ids.toTypedArray()
        }

        fun getHeisigKanjiMatchingIds(ids: MutableList<Int>, masterList: List<HeisigKanji>): List<HeisigKanji> {
            val filteredList = ArrayList<HeisigKanji>()
            ids.sort()

            for (id in ids) {
                //convert to 0 notation when referencing java array
                filteredList.add(masterList.get(id - 1))
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
        }
    }
}
