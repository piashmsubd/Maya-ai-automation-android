package com.maya.ai

import android.app.Application
import com.maya.ai.data.database.MayaDatabase
import com.maya.ai.data.datastore.PreferencesManager

class MayaApplication : Application() {

    lateinit var database: MayaDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize database
        database = MayaDatabase.getDatabase(this)

        // Initialize preferences
        preferencesManager = PreferencesManager(this)
    }

    companion object {
        lateinit var instance: MayaApplication
            private set
    }
}
