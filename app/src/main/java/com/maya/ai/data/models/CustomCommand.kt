package com.maya.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_commands")
data class CustomCommand(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trigger: String, // e.g., "open spotify"
    val action: String, // e.g., "launch_app"
    val params: String, // JSON string with parameters
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
