package tech.diarmaid.koohiiaite

import android.app.Application
import tech.diarmaid.koohiiaite.database.DatabaseCopier

class KoohiiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseCopier().init(this)
    }
}