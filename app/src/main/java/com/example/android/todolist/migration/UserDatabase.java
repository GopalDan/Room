package com.example.android.todolist.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

/**
 * Created by Gopal on 7/24/2020.
 */

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN last_update INTEGER");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))");
            // Copy the data
            database.execSQL(
                    "INSERT INTO users_new (userid, username, last_update) SELECT userid, username, last_update FROM users");
            // Remove the old table
            database.execSQL("DROP TABLE users");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE users_new RENAME TO users");
        }
    };
    private static final String LOG_TAG = UserDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static UserDatabase sInstance;

    public static UserDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        UserDatabase.class, UserDatabase.DATABASE_NAME)
                        // Queries should be done in a separate thread to avoid locking the UI
                        // We will allow this ONLY TEMPORALLY to see that our DB is working
//                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }
}
