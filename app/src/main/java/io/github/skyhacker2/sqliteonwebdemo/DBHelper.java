package io.github.skyhacker2.sqliteonwebdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eleven on 16/4/3.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private Context mContext;
    private static DBHelper instance;
    private static final String DB_NAME = "test.db";
    private static final int DB_VERSION = 1 ;

    public static final String TABLE_TODO = "todo";
    public static final String TABLE_TASK = "task";
    public static final String COL_TITLE = "title";
    public static final String COL_CREATE_AT = "created_at";
    public static final String COL_FINISHED = "finished";
    public static final String COL_TODO_ID = "todo_id";


    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTodoTable(db);
        createTaskTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTodoTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_TODO + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TITLE + " TEXT);";
        db.execSQL(sql);
    }

    private void createTaskTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_TASK + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TODO_ID + " INTEGER," +
                COL_TITLE + " TEXT," +
                COL_FINISHED + " TEXT);";
        db.execSQL(sql);
    }

    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }

    public int count() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TODO, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }
}
