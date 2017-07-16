package lk.ac.mrt.cse.smartremotecontroller.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chamath on 7/7/17.
 */

public class RemoteControllerDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "ChildData.db";


    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataContract.RemoteControllerColumns.TABLE_NAME + " (" +
                    DataContract.RemoteControllerColumns._ID + " INTEGER PRIMARY KEY," +
                    DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME + " TEXT NOT NULL " + COMMA_SEP +
                    DataContract.RemoteControllerColumns.COLUMN_NAME_BRAND_NAME + " TEXT NOT NULL " + COMMA_SEP +
                    DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_NAME + " TEXT NOT NULL" + COMMA_SEP +
                    DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_SIGNAL + " TEXT " + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataContract.RemoteControllerColumns.TABLE_NAME;


    private static RemoteControllerDBHelper dbHelper;

    private RemoteControllerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static RemoteControllerDBHelper getDbHelper(Context context) {
        if (dbHelper == null) dbHelper = new RemoteControllerDBHelper(context);
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}