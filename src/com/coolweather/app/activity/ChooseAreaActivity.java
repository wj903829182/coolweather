package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

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

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListenter;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;//进步条
	private TextView titleText;//文本头
	private ListView listView;//listView对象
	private ArrayAdapter<String> adapter;//适配器
	private CoolWeatherDB coolWeatherDB;//数据库操作对象
	private List<String> dataList=new ArrayList<String>();
	
//	省列表
	private List<Province> provinceList;
//	市列表
	private List<City> cityList;
//	县列表
	private List<County> countyList;
//	选中的省份
	private Province selectedProvince;
//	选中的城市
	private City selectedCity;
//	当前选中的级别
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);//设置布局
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1,dataList);
		//设置适配器
		listView.setAdapter(adapter);
		//得到数据库操作实例
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		//进行选项监听
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}
			}
		});
		//加载省级数据
		queryProvinces();
		
	}
	
	
	
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到在到服务器上查询
	 * */
	
	private void queryProvinces(){
		provinceList = coolWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();//清除数据
			for(Province province: provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			LogUtil.d("debug", "ChooseAreaActivity110");
			queryFromServer(null,"province");
			LogUtil.d("debug", "ChooseAreaActivity112");
		}
	}
	
	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到在去服务器上查询
	 * */
	private void queryCities(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city: cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	
	
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
	 * */
	private void queryCounties(){
		countyList=coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county: countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	
	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * */
	private void queryFromServer(final String code,
			final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			LogUtil.d("debug", "ChooseAreaActivity165");
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
			LogUtil.d("debug", "ChooseAreaActivity169");
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListenter() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				LogUtil.d("debug", "ChooseAreaActivity179");
				if("province".equals(type)){
					result=Utility.handleProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(coolWeatherDB, response,
							selectedProvince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(coolWeatherDB, response,
							selectedCity.getId());
				}
				
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
						
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, 
								"加载失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
		});
		
		
	}
	
	
	
	/*
	 * 显示进度对话框
	*/
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * 关闭进度对话框
	*/
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}


	/*
	 * 捕获Back按键，根据当前的级别来判断，此时是应该返回市列表，省列表，还是直接退出
	*/
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*super.onBackPressed();*/
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
	
	

}
