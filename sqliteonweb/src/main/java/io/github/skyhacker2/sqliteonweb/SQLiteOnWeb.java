package io.github.skyhacker2.sqliteonweb;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.skyhacker2.sqliteonweb.dtos.Message;
import io.github.skyhacker2.sqliteonweb.dtos.QueryResult;

/**
 * Created by eleven on 16/4/3.
 */
public class SQLiteOnWeb implements AndroidServer.AndroidServerListener{
    private final static String TAG = SQLiteOnWeb.class.getSimpleName();
    private Context mContext;
    private AndroidServer mServer;
    private static SQLiteOnWeb mSQLiteOnWeb;
    private SQLiteDatabase mDatabase;
    private File mDatabaseDir;

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
        mServer = new AndroidServer(context, 9000);
        mServer.setListener(this);
        getDatabaseDir();
    }

    private SQLiteOnWeb(Context context, int port) {
        mContext = context;
        mServer = new AndroidServer(context, port);
        mServer.setListener(this);
        getDatabaseDir();
    }

    public void start() {
        if (!mServer.isAlive()) {
            try {
                mServer.start();
                Log.i(TAG, "SQLiteOnWeb running on: " + getIpAccess());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Please connect wifi first");
            }
        }

    }

    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":" + mServer.getListeningPort();
    }

    private void getDatabaseDir() {
        File root = mContext.getFilesDir().getParentFile();
        File dbRoot = new File(root, "/databases");
        mDatabaseDir = dbRoot;
    }

    /// Begin AndroidServer.AndroidServerListener
    @Override
    public Message onOpenRequest(Map<String, String> params) {
        String name = params.get("name");
        try {
            mDatabase = mContext.openOrCreateDatabase(name, 0, null);
            Message msg = new Message();
            msg.code = 0;
            msg.text = "Open Success!";
            return msg;
        } catch (SQLiteException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.code = -1;
            msg.text = e.getMessage();
            return msg;
        }
    }

    @Override
    public Message onExecSQLRequest(Map<String, String> params) {
        String sql = params.get("sql");
        String first = sql.split(" ")[0].toLowerCase();
        if (first.equals("select")) {
            return query(sql);
        } else {
            return exec(sql);
        }
    }

    private Message exec(String sql) {
        Message msg = new Message();
        try {
            mDatabase.execSQL(sql);
        } catch (SQLiteException e) {
            e.printStackTrace();
            msg.code = -1;
            msg.text = e.getMessage();
            return msg;
        }
        msg.code = 0;
        msg.text = "Execute Success.";
        return msg;
    }

    private Message query(String sql) {
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(sql, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.code = -1;
            msg.text = e.getMessage();
            return msg;
        }

        if (cursor != null) {
            cursor.moveToFirst();
            QueryResult queryResult = new QueryResult();
            queryResult.code = 0;
            queryResult.text = "";
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);
//                if (cursor.getCount() > 0) {
//                    switch (cursor.getType(i)) {
//                        case Cursor.FIELD_TYPE_FLOAT:
//                            name += "(Float)";
//                            break;
//                        case Cursor.FIELD_TYPE_BLOB:
//                            name += "(Blob)";
//                            break;
//                        case Cursor.FIELD_TYPE_INTEGER:
//                            name += "(Integer)";
//                            break;
//                        case Cursor.FIELD_TYPE_NULL:
//                            name += "(NULL)";
//                            break;
//                        case Cursor.FIELD_TYPE_STRING:
//                            name += "(Text)";
//                            break;
//                    }
//                }
                columnNames.add(name);
            }
            queryResult.columnNames = columnNames;

            if (cursor.getCount() > 0) {
                do {
                    List row = new ArrayList();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                row.add(cursor.getBlob(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.add(Float.valueOf(cursor.getFloat(i)));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.add(Long.valueOf(cursor.getLong(i)));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                row.add(cursor.getString(i));
                                break;
                            default:
                                row.add("");
                        }
                    }
                    queryResult.rows.add(row);

                } while (cursor.moveToNext());
            }

            return queryResult;
        } else {
            Message msg = new Message();
            msg.code = -1;
            msg.text = "Cursor is null";
            return msg;
        }
    }

    @Override
    public Message onGetDBList(Map<String, String> params) {
        QueryResult queryResult = new QueryResult();
        for(String name : mDatabaseDir.list()) {
            queryResult.rows.add(name);
        }
        queryResult.code = 0;
        queryResult.text = "Query Success";
        return queryResult;
    }

    /// End AndroidServer.AndroidServerListener
}
