package com.initvent.janitorsystem;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoListActivity extends Activity{
	TextView txt1;
	ListView lvInfoList;
	TextView chng_taskID;
	
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayAdapterInfoList adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infolist);
		txt1=(TextView)findViewById(R.id.textView1);
		lvInfoList=(ListView)findViewById(R.id.listView1);

		arrList.clear();
		List<GetListView> taskList = new ArrayList<GetListView>();// 
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT i.id,substr(i.infocreateDate,1,10) as infocreateDate,i.infoNote,b.buildingname,substr(i.infocreateDate,1,10) date FROM  Info i inner join building b on i.building_id=b.building_id  ORDER BY DATE(date) DESC ",null);
		 // looping through all rows and adding to list
		GetListView contact;
			 while (cursor_Task.moveToNext()) 
			 {
			 contact = new GetListView();
			 contact.setID(Integer.parseInt(cursor_Task.getString(0)));
			 contact.setbuilding_id(cursor_Task.getString(3));
			 contact.settaskHeading(cursor_Task.getString(2));
			 contact.settaskNote(cursor_Task.getString(1));
			 taskList.add(contact);
			 } 
			for (GetListView cn : taskList) {
				String log = "ID: " + cn.getID() + "Note: " + cn.getbuilding_id() 
				+ ", Create date: " + cn.gettaskHeading()+ " Building id: " + cn.gettaskNote();

				// Writing Contacts to log
				Log.d("Result: ", log);
				//add contacts data in arrayList
				arrList.add(cn);

				}
				adapter = new ArrayAdapterInfoList(this, R.layout.screen_list,arrList);			
				lvInfoList.setAdapter(adapter);
				
				lvInfoList.setOnItemClickListener(new OnItemClickListener() {
					
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						chng_taskID = (TextView) view.findViewById(R.id.txtadpId);

					    String memberID_val = chng_taskID.getText().toString();

						Intent modify_intent = new Intent(getApplicationContext(),UpdateDeleteInfoActivity.class);
						
						modify_intent.putExtra("chng_taskID", memberID_val);
						
						startActivity(modify_intent);
						
					}
				});	
	}
	
}
