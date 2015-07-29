package com.baskrun.app.dao;

import java.util.ArrayList;
import java.util.List;

import com.baskrun.app.bean.City;
import com.baskrun.app.bean.County;
import com.baskrun.app.bean.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *	sqlite数据库操作	单列--getInstance()获取实例
 */
public class BaskRunDB {
	/** 数据库名称 */
	public static final String DB_NAME="bask_run_weather";
	/** 数据库版本 */
	public static final int VERSION=1;
	private static BaskRunDB bashRunDB;
	
	private SQLiteDatabase dbWrite;	//可写数据库操作
	private SQLiteDatabase dbRead;	//可读数据库操作
	private BaskRunOpenHelper dbHelper;	//数据库创建
	private Cursor cursor;	//数据库游标
	
	private BaskRunDB(Context context){
		dbHelper=new BaskRunOpenHelper(context,DB_NAME, null,VERSION);
		dbWrite=dbHelper.getWritableDatabase();
	}
	/**
	 * 获取BashRunDB的单列
	 * @param context
	 * @return BashRunDB
	 */
	public synchronized static BaskRunDB getInstance(Context context){
		if (bashRunDB==null) {
			bashRunDB=new BaskRunDB(context);
		}
		return bashRunDB;
	}
	/**
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			dbWrite.insert("Province",null, values);
		}
	}
	/**读取全国所有的省份信息
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> list=new ArrayList<Province>();
		dbRead=dbHelper.getReadableDatabase();
		cursor=dbRead.query("Province",new String[]{"id","province_name","province_code"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Province province=new Province();
			province.setId(cursor.getInt(0));
			province.setProvinceName(cursor.getString(1));
			province.setProvinceCode(cursor.getString(2));
			list.add(province);
		}
		return list;
	}
	/**
	 * 将City(市)实例存储到数据库
	 */
	public void saveCities(City city) {
		if (city!=null) {
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			dbWrite.insert("City",null, values);
		}
	}
	
	/**读取全国所有的市信息
	 * @param provinceId
	 * @return	List<City>
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list=new ArrayList<City>();
		dbRead=dbHelper.getReadableDatabase();
		cursor=dbRead.query("City", null,"province_id=?",new String[]{String.valueOf(provinceId)}, null, null, null);
		//下一行是否有效
		if (cursor.moveToFirst()) {
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 将County(县)实例存储到数据库
	 */
	public void saveCounty(County county) {
		if (county!=null) {
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			dbWrite.insert("County",null, values);
		}
	}
	
	/**读取全国所有县的信息
	 * @param provinceId
	 * @return	List<City>
	 */
	public List<County> loadCounty(int cityId) {
		List<County> list=new ArrayList<County>();
		dbRead=dbHelper.getReadableDatabase();
		cursor=dbRead.query("County", null,"city_id=?",new String[]{String.valueOf(cityId)}, null, null, null);
		//下一行是否有效
		if (cursor.moveToFirst()) {
			do {
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
