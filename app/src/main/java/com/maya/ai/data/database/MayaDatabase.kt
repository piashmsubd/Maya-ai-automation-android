package com.maya.ai.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maya.ai.data.models.Conversation
import com.maya.ai.data.models.CustomCommand
import com.maya.ai.data.models.Message

@Database(
    entities = [
        Message::class,
        Conversation::class,
        CustomCommand::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MayaDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun customCommandDao(): CustomCommandDao

    companion object {
        @Volatile
        private var INSTANCE: MayaDatabase? = null

        fun getDatabase(context: Context): MayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MayaDatabase::class.java,
                    "maya_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
