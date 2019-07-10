package tech.diarmaid.koohiiaite.viewmodel

import androidx.lifecycle.ViewModel

class KanjiDetailViewModel : ViewModel() {
    var heisigId: Int? = null
    var keyword: String? = null
    var userKeyword: String? = null
    var kanji: String? = null
}