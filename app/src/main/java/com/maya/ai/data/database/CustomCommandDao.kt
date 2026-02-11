package com.maya.ai.data.database

import androidx.room.*
import com.maya.ai.data.models.CustomCommand
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCommandDao {
    @Query("SELECT * FROM custom_commands WHERE enabled = 1 ORDER BY trigger ASC")
    fun getEnabledCommands(): Flow<List<CustomCommand>>

    @Query("SELECT * FROM custom_commands ORDER BY createdAt DESC")
    fun getAllCommands(): Flow<List<CustomCommand>>

    @Query("SELECT * FROM custom_commands WHERE trigger LIKE '%' || :query || '%' AND enabled = 1")
    suspend fun searchCommands(query: String): List<CustomCommand>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: CustomCommand): Long

    @Update
    suspend fun updateCommand(command: CustomCommand)

    @Delete
    suspend fun deleteCommand(command: CustomCommand)

    @Query("DELETE FROM custom_commands")
    suspend fun deleteAllCommands()
}
