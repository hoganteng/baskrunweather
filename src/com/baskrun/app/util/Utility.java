package com.baskrun.app.util;

import android.text.TextUtils;

import com.baskrun.app.bean.City;
import com.baskrun.app.bean.County;
import com.baskrun.app.bean.Province;
import com.baskrun.app.dao.BaskRunDB;

/**
 *全国地址获取解析  
 *处理服务器返回的省、市、县数据
 *	格式：“代号|城市|,代号|城市”
 */
public class Utility {
	
	/**省信息处理
	 * 解析和处理服务器返回的省级数据，并插入到数据库
	 * @param baskRunDB 数据库操作类
	 * @param response 服务器返回的数据
	 * @return boolean 返回true，解析的数据插入到数据库。
	 * 					返回false,提供的数据为空，未解析
	 */
	public synchronized static boolean handleProvinceResponse(BaskRunDB baskRunDB,String response) {
		//如果不无效
		if (!TextUtils.isEmpty(response)) {
			//response的数据格式：01|北京,02|上海
			String[] allProvinces=response.split(",");
			for (String p : allProvinces) {
				String[] array=p.split("\\|");	//"|":竖线是转义字符
				Province province=new Province();
				province.setProvinceCode(array[0]);
				province.setProvinceName(array[1]);
				//将解析出来的数据存储到Province表
				baskRunDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}
	
	/**市信息处理
	 * 解析和处理服务器返回的市级数据，并插入到数据库
	 * @param baskRunDB		BashRunDB的实例
	 * @param response		服务器相应的数据
	 * @param provinceId	市归属于哪个省份的id
	 * @return	返回true，解析的数据插入到数据库。
	 * 			返回false,提供的数据为空，未解析。
	 */
	public static boolean handleCityResponse(BaskRunDB baskRunDB,String response,int provinceId) {
		//如果不无效
				if (!TextUtils.isEmpty(response)) {
					//response的数据格式：2501|长沙,2502|湘潭
					String[] allCity=response.split(",");
					for (String p : allCity) {
						String[] array=p.split("\\|");	//"|":竖线是转义字符
						City city=new City();
						city.setCityCode(array[0]);
						city.setCityName(array[1]);
						city.setProvinceId(provinceId);
						//将解析出来的数据存储到Province表
						baskRunDB.saveCities(city);
					}
					return true;
				}
				return false;
	}
	
	/**县信息处理
	 * 解析和处理服务器返回的县级数据，并插入到数据库
	 * @param baskRunDB		BashRunDB的实例
	 * @param response		服务器相应的数据
	 * @param cityId		县归属于哪个市的id
	 * @return	返回true，解析的数据插入到数据库。
	 * 			返回false,提供的数据为空，未解析。
	 */
	public static boolean handleCountyResponse(BaskRunDB baskRunDB,String response,int cityId) {
		//如果不无效
		if (!TextUtils.isEmpty(response)) {
			//response的数据格式：2501|长沙,2502|湘潭
			String[] allCounty=response.split(",");
			for (String p : allCounty) {
				String[] array=p.split("\\|");	//"|":竖线是转义字符
				County county=new County();
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(cityId);
				//将解析出来的数据存储到Province表
				baskRunDB.saveCounty(county);
			}
			return true;
		}
		return false;
	}
	
}
