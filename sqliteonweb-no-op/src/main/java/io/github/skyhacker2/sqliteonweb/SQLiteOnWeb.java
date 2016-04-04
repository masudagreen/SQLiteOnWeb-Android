package io.github.skyhacker2.sqliteonweb;

import android.content.Context;

/**
 * Created by eleven on 16/4/3.
 */
public class SQLiteOnWeb {
    private Context mContext;
    private static SQLiteOnWeb mSQLiteOnWeb;

    public static SQLiteOnWeb init(Context context) {
        if (mSQLiteOnWeb == null){
            mSQLiteOnWeb = new SQLiteOnWeb(context);
        }
        return mSQLiteOnWeb;
    }

    public static SQLiteOnWeb init(Context context, int port) {
        if (mSQLiteOnWeb == null){
            mSQLiteOnWeb = new SQLiteOnWeb(context, port);
        }
        return mSQLiteOnWeb;
    }

    private SQLiteOnWeb(Context context) {
        mContext = context;
    }

    private SQLiteOnWeb(Context context, int port) {
        mContext = context;
    }

    public void start() {

    }
}
