package com.example.todosapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TasksDB";
    private static final String TABLE_TASKS = "tasks";
    private static final String TASKS_ID = "id";
    private static final String CB_ID = "cb_id";
    private static final String TASKS_NAME = "name";
    private static final String TASKS_STATUS = "status";
    private static final int DATABASE_VERSION = 1;

    public LocalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_TASKS + "(" +
                TASKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TASKS_NAME + " TEXT NOT NULL," +
                CB_ID + " INTEGER NOT NULL," +
                TASKS_STATUS + " INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        onCreate(db);
    }

    public ArrayList<TaskModel> fetchTask() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cr = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        ArrayList<TaskModel> arrTask = new ArrayList<>();

        while (cr.moveToNext())
        {
            TaskModel model = new TaskModel(cr.getString(0), cr.getString(1), cr.getInt(2), cr.getInt(3));
            arrTask.add(model);
        }
        cr.close();
        return arrTask;
    }

    public void editTask(String name, int cb_id, int status, String newName)
    {
        int id = getId(name, cb_id, status);
        if (id == -1) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASKS_NAME, newName);
        db.update(TABLE_TASKS, cv, TASKS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void delTask(String name, Integer cb_id, Integer status)
    {
        int id = getId(name, cb_id, status);
        if (id == -1) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, TASKS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int getId(String name, Integer cb_id, Integer status)
    {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cr = db.rawQuery("SELECT " + TASKS_ID + " FROM " + TABLE_TASKS + " WHERE " + TASKS_NAME + " = ? AND " + CB_ID + " = ? AND " + TASKS_STATUS + " = ?", new String[]{name, String.valueOf(cb_id), String.valueOf(status)});
        if (cr.moveToFirst())
            id = cr.getInt(0);
        cr.close();
        return id;
    }

    public TaskModel getTask(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        TaskModel model = new TaskModel("-1", "Default", -1, 0);
                Cursor cr = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + TASKS_ID + " = ?", new String[]{id});
        if (cr.moveToFirst())
            model = new TaskModel(cr.getString(0), cr.getString(1), cr.getInt(2), cr.getInt(3));
        cr.close();
        return model;
    }

    public void doneTask(String name, int cb_id)
    {
        int id = getId(name, cb_id, 0);
        if (id == -1) return;
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASKS_STATUS, 1);
        database.update(TABLE_TASKS, values, TASKS_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void addTask(String name, Integer cb_id) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASKS_NAME, name);
        values.put(CB_ID, cb_id);
        values.put(TASKS_STATUS, 0);

        database.insert(TABLE_TASKS, null, values);
    }
}
