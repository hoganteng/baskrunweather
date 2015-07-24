package com.baskrun.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *	创建SQLite数据库
 *		省份表、市表、县表
 */
public class BaskRunOpenHelper extends SQLiteOpenHelper{
	//省表
	public static final String CREATE_PROVINCE="CREATE TABLE Province ("
			+"[id] INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"[province_name] VARCHAR,"
			+"[province_code] VARCHAR)";
	//市表
	public static final String CREATE_CITY="CREATE TABLE City ("
			+"[id] INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"[city_name] VARCHAR,"
			+"[city_code] VARCHAR)"
			+"[province_id] INTEGER)";		
	//县表
	public static final String CREATE_COUNTY="CREATE TABLE County ("
			+"[id] INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"[county_name] VARCHAR,"
			+"[county_code] VARCHAR)"
			+"[city_id] INTEGER)";	

	public BaskRunOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	//第一个初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);	//创建province表
		db.execSQL(CREATE_CITY);		//创建city表
		db.execSQL(CREATE_COUNTY);		//创建county表
	}

	//更新的时候使用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
