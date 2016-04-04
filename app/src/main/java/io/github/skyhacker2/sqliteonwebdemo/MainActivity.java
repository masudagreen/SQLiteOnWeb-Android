package io.github.skyhacker2.sqliteonwebdemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = DBHelper.getInstance(this);
        if (mDBHelper.count() == 0) {
            Log.d(TAG, "init database");
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            for (int i = 0; i < 10; i++) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COL_TITLE, "test" + i);
                db.insert(DBHelper.TABLE_TODO, null, values);
            }
        }


        SQLiteOnWeb.init(this).start();
    }
}
