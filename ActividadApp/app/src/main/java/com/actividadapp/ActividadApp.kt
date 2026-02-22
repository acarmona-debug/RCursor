package com.actividadapp

import android.app.Application
import com.actividadapp.data.AppDatabase
import com.actividadapp.data.DatabaseProvider

class ActividadApp : Application() {
    val database: AppDatabase by lazy { DatabaseProvider.getDatabase(this) }
}
