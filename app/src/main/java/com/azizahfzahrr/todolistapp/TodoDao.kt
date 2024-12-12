package com.azizahfzahrr.todolistapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(todoModel: TodoModel): Long

    @Query("SELECT * FROM TodoModel WHERE isFinished == 0")
    fun getAllTasks(): LiveData<List<TodoModel>>

    @Query("UPDATE TodoModel SET isFinished = 1 WHERE id = :uid")
    suspend fun finishTask(uid: Long)

    @Query("DELETE FROM TodoModel WHERE id = :uid")
    suspend fun deleteTask(uid: Long)

    @Query("SELECT * FROM TodoModel WHERE (title LIKE :query OR description LIKE :query) AND isFinished == 0")
    fun searchTasks(query: String): LiveData<List<TodoModel>>
}
