package com.baskrun.app.bean;

/**
 * 市JavaBean --
 */
public class City {
	private int id;
	private String cityName; // 城市名称
	private String cityCode; // 城市编码
	private int provinceId; // 城市属于哪个的省份

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

}
