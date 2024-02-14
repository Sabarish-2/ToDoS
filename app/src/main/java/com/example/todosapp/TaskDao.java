package com.example.todosapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("select * from task")
    List<Task> getAllTask();

    @Insert
    void addTask(Task task);

    @Update
    void editTask(Task task);

    @Delete
    void delTask(Task task);

    @Query("select id from task where name = :name and status = :status")
    int getTaskId(String name, int status);
}