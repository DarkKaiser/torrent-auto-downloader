package kr.co.darkkaiser.jv.vocabulary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbSQLiteOpenHelper extends SQLiteOpenHelper {

    public UserDbSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    @SuppressWarnings("StringBufferReplaceableByString")
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append(" CREATE TABLE TBL_USER_VOCABULARY ( ")
             .append("      V_IDX                       INTEGER PRIMARY KEY NOT NULL UNIQUE,")
             .append("      MEMORIZE_TARGET             INTEGER DEFAULT (0),")
             .append("      MEMORIZE_COMPLETED          INTEGER DEFAULT (0),")
             .append("      MEMORIZE_COMPLETED_COUNT    INTEGER DEFAULT (0)")
             .append(" )");

        db.execSQL(sbSQL.toString());
        db.execSQL("CREATE UNIQUE INDEX TBL_USER_VOCABULARY_INDEX01 ON TBL_USER_VOCABULARY(V_IDX)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TBL_USER_VOCABULARY");
        onCreate(db);
    }

}
