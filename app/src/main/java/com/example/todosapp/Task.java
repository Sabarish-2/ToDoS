package com.example.todosapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;


    public Task(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "status")
    private int status;
    @ColumnInfo(name = "date")
    private long calTIM;

    @Ignore
    Task(int id, String name, String description, int status, long calTIM) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.calTIM = calTIM;
    }

    @Ignore
    Task(String name, String description, int status, long calTIM) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.calTIM = calTIM;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getStatus() {
        return status;
    }
    public void setCalTIM(long calTIM) {
        this.calTIM = calTIM;
    }
    public long getCalTIM() {
        return calTIM;
    }
    public void setStatus(int status) {
        this.status = status;
    }

}
