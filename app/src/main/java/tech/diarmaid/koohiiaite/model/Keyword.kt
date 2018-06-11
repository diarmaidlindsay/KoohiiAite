package tech.diarmaid.koohiiaite.model

/**
 * Represent an entry in the keyword and user_keyword table
 */
class Keyword(val heisigId: Int, var keywordText: String) {
    companion object {

        fun getTextForId(list: List<Keyword>, heisigId: Int): String? {
            for (kw in list) {
                if (kw.heisigId == heisigId) {
                    return kw.keywordText
                }
            }

            return "NOT FOUND"
        }
    }
}
