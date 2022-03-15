package com.example.todo_list_with_geolocation.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(taskEntry: TaskEntity)

    @Update
    suspend fun update(taskEntry: TaskEntity)

    @Delete
    suspend fun delete(taskEntry: TaskEntity)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAll() : LiveData<List<TaskEntity>>

    @Query("SELECT * FROM task_table ORDER BY priority ASC")
    fun getAllPriorityTasks() : LiveData<List<TaskEntity>>

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery ORDER BY date DESC")
    fun searchDatabase(searchQuery: String) : LiveData<List<TaskEntity>>
}