package com.example.todo_list_with_geolocation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo_list_with_geolocation.database.TaskDatabase
import com.example.todo_list_with_geolocation.database.TaskEntity
import com.example.todo_list_with_geolocation.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val repository : TaskRepository

    val getAllTasks: LiveData<List<TaskEntity>>
    val getAllPriorityTasks: LiveData<List<TaskEntity>>

    init {
        repository = TaskRepository(taskDao)
        getAllTasks = repository.getAllTasks()
        getAllPriorityTasks = repository.getAllPriorityTasks()
    }

    fun insert(taskEntity: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(taskEntity)
        }
    }

    fun update(taskEntity: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(taskEntity)
        }
    }

    fun delete(taskEntity: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskEntity)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }

    fun searchDatabase(searchQuery: String) : LiveData<List<TaskEntity>> {
        return repository.searchDatabase(searchQuery)
    }
}