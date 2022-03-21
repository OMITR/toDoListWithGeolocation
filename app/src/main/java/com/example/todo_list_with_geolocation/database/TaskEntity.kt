package com.example.todo_list_with_geolocation.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var task: String,
    var priority: Int,
    var date: Long,
    var isRepeating: Boolean,
    var notificationId: Int
        ):Parcelable