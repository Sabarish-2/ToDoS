package com.example.todosapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Task.class, exportSchema = false, version = 2)
public abstract class LocalDB extends RoomDatabase {
    private static final String DATABASE_NAME = "TasksDB";

    public static LocalDB instance;
    public static synchronized LocalDB getDB(Context context)
    {
        if (instance == null) {
            instance = Room.databaseBuilder(context, LocalDB.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract TaskDao taskDao();
}
