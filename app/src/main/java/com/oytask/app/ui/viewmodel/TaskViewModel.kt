package com.oytask.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.oytask.app.data.db.TaskDatabase
import com.oytask.app.data.model.*
import com.oytask.app.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val _currentFilter = MutableLiveData(TaskFilter.ALL)
    val currentFilter: LiveData<TaskFilter> = _currentFilter

    private val _searchQuery = MutableLiveData("")
    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    val pendingCount: LiveData<Int>
    val overdueCount: LiveData<Int>

    val tasks: LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        pendingCount = repository.pendingCount
        overdueCount = repository.getOverdueCount()

        tasks = MediatorLiveData<List<Task>>().apply {
            addSource(_currentFilter) { filter ->
                updateTasks(this, filter, _searchQuery.value ?: "")
            }
            addSource(_searchQuery) { query ->
                if (query.isNotEmpty()) {
                    updateSearchResults(this, query)
                } else {
                    updateTasks(this, _currentFilter.value ?: TaskFilter.ALL, "")
                }
            }
        }
    }

    private fun updateTasks(
        result: MediatorLiveData<List<Task>>,
        filter: TaskFilter,
        query: String
    ) {
        if (query.isNotEmpty()) {
            updateSearchResults(result, query)
            return
        }

        val source = when (filter) {
            TaskFilter.ALL -> repository.allTasks
            TaskFilter.PENDING -> repository.pendingTasks
            TaskFilter.COMPLETED -> repository.completedTasks
            TaskFilter.TODAY -> repository.getTodayTasks()
            TaskFilter.OVERDUE -> repository.getOverdueTasks()
            TaskFilter.THIS_WEEK -> repository.getThisWeekTasks()
        }

        result.addSource(source) { tasks ->
            result.value = tasks
            result.removeSource(source)
            reobserve(result)
        }
    }

    private fun updateSearchResults(result: MediatorLiveData<List<Task>>, query: String) {
        val source = repository.searchTasks(query)
        result.addSource(source) { tasks ->
            result.value = tasks
            result.removeSource(source)
        }
    }

    private fun reobserve(result: MediatorLiveData<List<Task>>) {
        val filter = _currentFilter.value ?: TaskFilter.ALL
        val query = _searchQuery.value ?: ""

        if (query.isNotEmpty()) return

        val source = when (filter) {
            TaskFilter.ALL -> repository.allTasks
            TaskFilter.PENDING -> repository.pendingTasks
            TaskFilter.COMPLETED -> repository.completedTasks
            TaskFilter.TODAY -> repository.getTodayTasks()
            TaskFilter.OVERDUE -> repository.getOverdueTasks()
            TaskFilter.THIS_WEEK -> repository.getThisWeekTasks()
        }

        result.addSource(source) { tasks ->
            result.value = tasks
        }
    }

    fun setFilter(filter: TaskFilter) {
        _searchQuery.value = ""
        _isSearching.value = false
        _currentFilter.value = filter
    }

    fun search(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotEmpty()
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun toggleComplete(task: Task) = viewModelScope.launch {
        repository.toggleComplete(task)
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        repository.deleteCompletedTasks()
    }
}
