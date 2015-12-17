package com.hideactive.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper{
	
	// 数据库版本号
	private static final int DATABASE_VERSION = 1;
	// 数据库名
	public static final String DATABASE_NAME = "HideActive.db";

    private String account;
    
    public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    public DBOpenHelper(Context context, String account) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.account = account;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_create_like = "CREATE TABLE IF NOT EXISTS "
                + LikesDB.TB_NAME + "_" + account
                + "(userId text, postId text, createTime text)";


        db.execSQL(sql_create_like);
        Log. d("Database" ,"Database_Create" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + LikesDB.TB_NAME + "_" + account);
        onCreate(db);
        Log. d("Database" ,"onUpgrade" );
    }
}
