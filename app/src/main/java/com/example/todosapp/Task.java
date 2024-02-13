package com.example.todosapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
@Entity(tableName = "task")
@TypeConverters(DateConverter.class) // Add this line
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
    private Date dueDate;

    @Ignore
    Task(int id, String name, String description, int status, Date dueDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    @Ignore
    Task(String name, String description, int status, Date dueDate) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
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
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    public Date getDueDate() {
        return dueDate;
    }
    public void setStatus(int status) {
        this.status = status;
    }

}
