package com.ihs.demo.message_2013011301;

import android.support.v7.app.ActionBarActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.android.maps.MapController;
import com.ihs.message_2013011301.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends ActionBarActivity {
	MapView mMapView = null;
	MapController mMapController = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		SDKInitializer.initialize(getApplicationContext());
		mMapView = (MapView)findViewById(R.id.bmapView);
		//设定中心点坐标
		Intent intent = getIntent();
		final String[] usrInfo = intent.getStringArrayExtra("info");
		double x = Double.valueOf(usrInfo[0]);
		double y = Double.valueOf(usrInfo[1]);
		String text = usrInfo[2];
		if(text==null||text.equals(""))
		{
			text = "Unknown Address";
		}
		LatLng cenpt =  new LatLng(x,y);  
		//定义地图状态 
		MapStatus mMapStatus = new MapStatus.Builder() 
		.target(cenpt) 
		.zoom(12) 
		.build(); 

		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus); 
	      BitmapDescriptor bitmap = BitmapDescriptorFactory
	          .fromResource(R.drawable.arrow);    
	      OverlayOptions option = new MarkerOptions()
	          .position(cenpt)    
	          .icon(bitmap);    
	      mMapView.getMap().addOverlay(option);
	        
	        
	    OverlayOptions textOption = new TextOptions()
	        .bgColor(0xAAFFFF00)    
	        .fontSize(28)    
	        .fontColor(0xFFFF00FF)    
	        .text(text)    
	        .rotate(0)    
	        .position(cenpt);    
	    mMapView.getMap().addOverlay(textOption);
		mMapView.getMap().setMapStatus(mMapStatusUpdate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
