package com.example.todosapp;

public class TaskModel {
    int status;
    String id, name;

    public TaskModel(String id, String name, int status)
    {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
