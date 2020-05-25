package tech.diarmaid.koohiiaite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class KanjiDetailViewModel(application: Application) : AndroidViewModel(application) {
    var heisigId: MutableLiveData<Int> = MutableLiveData()
    var keyword: MutableLiveData<String?> = MutableLiveData()
    var userKeyword: MutableLiveData<String?> = MutableLiveData()
    var kanji: MutableLiveData<String?> = MutableLiveData()
}