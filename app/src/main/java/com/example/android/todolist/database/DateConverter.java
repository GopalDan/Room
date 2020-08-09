package com.example.android.todolist.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    // While reading from Database
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
    // While writing into database as SQLite data type is different
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
