package com.baskrun.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.baskrun.app.R;
import com.baskrun.app.bean.City;
import com.baskrun.app.bean.County;
import com.baskrun.app.bean.Province;
import com.baskrun.app.dao.BaskRunDB;
import com.baskrun.app.util.HttpCallbackListener;
import com.baskrun.app.util.HttpUtil;
import com.baskrun.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *	遍历省市县数据的活动
 */
public class ChooseAreaActivity extends Activity{
	
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	private static final String TAG = "ChooseAreaActivity";
	/**当前选择的级别	 */
	private int currentLevel;
	private ListView listView;
	private TextView titleText;
	
	
	private ArrayAdapter<String> adapter;	
	private BaskRunDB baskRunDB;
	private Province selectProvince;
	private City selectCity;
	
	/** 省份信息	 */
	private List<Province> provinceList;
	/**市列表	 */
	private List<City> cityList;
	/** 县列表 */
	private List<County> countyList;
	
	private List<String> dataList=new ArrayList<String>();
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		titleText=(TextView)findViewById(R.id.title_text);
		listView=(ListView)findViewById(R.id.list_view);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		//获取数据库操作类实例
		baskRunDB=BaskRunDB.getInstance(this);
		//子项点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				//点击的是"省"份信息的item，就进行显示该省份的所有"市"
				if (currentLevel==LEVEL_PROVINCE) {
					selectProvince=provinceList.get(position);
					queryCityies();
				}
				//点击的是"市"信息的item，就进行显示该"市"的所有"县"
				else if (currentLevel==LEVEL_CITY) {
					selectCity=cityList.get(position);
					queryCounties();	//查询所有县
				}
			}
		});
		queryProvinces();	//默认先显示省级信息
	}
	
	/**
	 * 查询全国所有省份，优先从数据库查询，如果没有再到服务器上查询
	 */
	private void queryProvinces(){
		//数据库中查询
		provinceList=baskRunDB.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();	//数据改变，唤醒
			//setSelection()内部就是调用了setSelectionFromTop()，只不过是Y轴的偏移量是0而已。
			listView.setSelection(0);
			titleText.setText("中国");
			//设置当前级别
			currentLevel=LEVEL_PROVINCE;
		}else {
			queryFromService(null,"province");
		}
	}
	/** 查询选中省内所有的市，优先从数据库查询，如果没有查询到，再到服务器上查询 */
	private void queryCityies(){
		cityList=baskRunDB.loadCities(selectProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();	//清除之后再放数据
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else {
			queryFromService(selectProvince.getProvinceCode(),"city");
		}
	}
	/** 查询选中市内所有的县，优先从数据库查询。
	 * 如果没有查询到，再到服务器上查询 */
	private void queryCounties(){
		countyList=baskRunDB.loadCounty(selectCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else {
			queryFromService(selectCity.getCityCode(),"county");
		}
	}
	
	/**根据传入的代号和类型从服务器上查询省、市、县数据 */
	private void queryFromService(String code,final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
			//网络请求处理成功
			@Override
			public void onFinish(String response) {
				boolean result=false;
				if ("province".equals(type)) {
					result=Utility.handleProvinceResponse(baskRunDB, response);
				}else if ("city".equals(type)) {
					result=Utility.handleCityResponse(baskRunDB, response,selectProvince.getId());
				}else if ("county".equals(type)) {
					result=Utility.handleCountyResponse(baskRunDB, response, selectCity.getId());
				}
				if (result) {
					//如果处理成功通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCityies();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "地址加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**显示进度对话框	 */
	private void showProgressDialog() {
		if (progressDialog==null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCancelable(false);	//设置取消按钮不可用
		}
		progressDialog.show();
	}
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
		Log.d(TAG,"onBackPressed()");
		//如果是县级别的，点击返回，就加载市级别的
		if (currentLevel==LEVEL_COUNTY) {
			queryCityies();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG,"onDestroy()");
	}
	
}
