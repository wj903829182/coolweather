package com.coolweather.app.db;

import com.coolweather.app.util.LogUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	/*
	 * Province表建表语句
	 * create table Province(
	 *    id integer primary key autoincrement,
	 *    province_name text,
	 *    province_code text
	 * );
	 * id 自动增长主键
	 * province_name  表示省名
	 * province_code  表示省级代号
	 * */
	public static final String CREATE_PROVINCE="create table Province ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
	
	/*
	 * City表建表语句
	 * create table City(
	 *    id integer primary key autoincrement,
	 *    city_name text,
	 *    city_code text,
	 *    province_id integer
	 * );
	 * id 自动增长主键
	 * province_name  表示城市名
	 * province_code  表示城市代号
	 * province_id    关联表Province表的外键
	 * */
	public static final String CREATE_CITY="create table City ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer)";
	
	
	/*
	 * County表建表语句
	 * create table County(
	 *    id integer primary key autoincrement,
	 *    county_name text,
	 *    county_code text,
	 *    city_id integer
	 * );
	 * id 自动增长主键
	 * county_name  表示县名
	 * county_code  表示县级代号
	 * city_id    关联表City表的外键
	 * */
	public static final String CREATE_COUNTY="create table County ("
			+ "id integer primary key autoincrement, "
			+ "county_name text, "
			+ "county_code text, "
			+ "city_id integer)";
	
	
	
	public CoolWeatherOpenHelper(Context context,String name,CursorFactory factory,
			int version){
		super(context, name, factory, version);
	}
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        
		
		db.execSQL(CREATE_PROVINCE);//创建Province表
		db.execSQL(CREATE_CITY);//创建City表
		db.execSQL(CREATE_COUNTY);//创建County表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
