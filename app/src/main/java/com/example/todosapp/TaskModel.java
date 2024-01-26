package com.example.todosapp;

public class TaskModel {
    int cb_id, status;
    String id, name;

    public TaskModel(String id, String name, int cb_id, int status)
    {
        this.id = id;
        this.name = name;
        this.cb_id = cb_id;
        this.status = status;
    }
}
