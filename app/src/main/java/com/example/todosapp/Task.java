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
    @ColumnInfo(name = "status")
    private int status;


    Task(int id, String name, int status, long calTIM, int rep, int freq) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.calTIM = calTIM;
        this.rep = rep;
        this.freq = freq;
    }

    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "date")
    private long calTIM;
    @ColumnInfo(name = "rep")
    private int rep;

    @ColumnInfo(name = "freq")
    private int freq;

    @Ignore
    Task(int id, String name, String description, int status, long calTIM) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.calTIM = calTIM;
    }


    @Ignore
    Task(int id, String name, String description, int status, long calTIM, int rep, int freq) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.calTIM = calTIM;
        this.freq = freq;
        this.rep = rep;
    }

    @Ignore
    Task(String name, String description, int status, long calTIM, int rep, int freq) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.calTIM = calTIM;
        this.freq = freq;
        this.rep = rep;
    }


    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public int getStatus() {return status;}
    public int getRep() {return rep;}

    public int getFreq() {return freq;}
    public void setFreq(int freq) {this.freq = freq;}
    public long getCalTIM() {return calTIM;}
    public void setStatus(int status) {this.status = status;}

}
