package com.example.todo_list_with_geolocation.repository

import androidx.lifecycle.LiveData
import com.example.todo_list_with_geolocation.database.TaskDao
import com.example.todo_list_with_geolocation.database.TaskEntity

class TaskRepository(val taskDao: TaskDao) {
    suspend fun insertTask(taskEntity: TaskEntity) = taskDao.insert(taskEntity)
    suspend fun updateTask(taskEntity: TaskEntity) = taskDao.update(taskEntity)
    suspend fun deleteTask(taskEntity: TaskEntity) = taskDao.delete(taskEntity)
    suspend fun deleteAllTasks() {
        taskDao.deleteAll()
    }

    fun getAllTasks() : LiveData<List<TaskEntity>> = taskDao.getAll()
    fun getAllPriorityTasks() : LiveData<List<TaskEntity>> = taskDao.getAllPriorityTasks()
    fun searchDatabase(searchQuery: String) : LiveData<List<TaskEntity>> {
        return taskDao.searchDatabase(searchQuery)
    }
}