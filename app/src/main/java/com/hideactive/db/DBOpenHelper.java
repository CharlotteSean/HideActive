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
	public static final String DATABASE_NAME = "_HideActive.db";

    public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LikesDB.SQL_CREATE);
        Log. d("Database" ,"Database_Create" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + LikesDB.TB_NAME);
        onCreate(db);
        Log. d("Database" ,"onUpgrade" );
    }
}
