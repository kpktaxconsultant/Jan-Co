package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val email: String,
    val city: String,
    val occupation: String,
    val purpose: String,
    val timestamp: Long = System.currentTimeMillis()
)
