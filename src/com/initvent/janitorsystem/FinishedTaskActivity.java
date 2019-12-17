package com.initvent.janitorsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FinishedTaskActivity extends Activity{
	TextView txt1,txt2;
	ListView lvTask;
	
	TextView chng_taskID;
	
	ArrayList<GetFinishedTaskListView> arrList = new ArrayList<GetFinishedTaskListView>();
	ArrayAdapterFinishedTaskList adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_finish);
		txt1=(TextView)findViewById(R.id.textView1);
		txt2=(TextView)findViewById(R.id.textView2);
		txt2.setTypeface(null, Typeface.BOLD);
		lvTask=(ListView)findViewById(R.id.listView1);

		String loginname="";
		Cursor cursor_login = SplashActivity.mydb.rawQuery("SELECT * FROM  Login ",null);
		if(cursor_login.moveToNext())
		{
			loginname=cursor_login.getString(3);
		}
		arrList.clear();
		List<GetFinishedTaskListView> taskList = new ArrayList<GetFinishedTaskListView>();// 
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT t.id,task_id,taskHeading,taskNote,taskStarttimestamp,taskEndtimestamp,substr(taskEndtimestamp,1,10) date FROM  Task t inner Join User u on t.user_Id=u.sysUser_id where taskEndtimestamp<>'' and u.user_id='" + loginname + "' and DATE(date)=CURRENT_DATE",null);
		 // looping through all rows and adding to list
		GetFinishedTaskListView contact;
		double totalhour=0;
		while (cursor_Task.moveToNext()) 
			 {
				 contact = new GetFinishedTaskListView();
				 contact.setID(Integer.parseInt(cursor_Task.getString(0)));
				 contact.settask_id(cursor_Task.getString(1));
				 contact.settaskHeading(cursor_Task.getString(2));
				 contact.settaskNote(cursor_Task.getString(3));
				 //yyyy-MM-dd hh:mm a
				 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a"); 
				 long diffInMin=0;
				 try 
				 {
					Date stdate = dateFormat.parse(cursor_Task.getString(4));
	  			    Date endate = dateFormat.parse(cursor_Task.getString(5));
	  			    long diffInMillisec = endate.getTime() - stdate.getTime();
	  			    diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
				 } 
				 catch (ParseException e) 
				 {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
				 contact.settaskStarttimestamp(cursor_Task.getString(4));
				 contact.settaskEndtimestamp(cursor_Task.getString(5));
				 contact.setduration(""+String.format("%.2f",((double)diffInMin)/60));
				 taskList.add(contact);
				 
				 totalhour=totalhour+((double)diffInMin)/60;
			 } 
			txt2.setText(""+String.format("%.2f",totalhour));
			for (GetFinishedTaskListView cn : taskList) {
				String log = "ID: " + cn.getID() 
				+ ", Create date: " + cn.gettaskHeading()+ " Building id: " + cn.gettaskNote();

				// Writing Contacts to log
				Log.d("Result: ", log);
				//add contacts data in arrayList
				arrList.add(cn);

				}
				adapter = new ArrayAdapterFinishedTaskList(this, R.layout.screen_list_finished_task,arrList);			
				lvTask.setAdapter(adapter);

				lvTask.setOnItemClickListener(new OnItemClickListener() {
					
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						chng_taskID = (TextView) view.findViewById(R.id.txtguidId);

					    String memberID_val = chng_taskID.getText().toString();

						Intent modify_intent = new Intent(getApplicationContext(),UpdateTaskTimeActivity.class);
						
						modify_intent.putExtra("chng_taskID", memberID_val);
						
						startActivity(modify_intent);
						
					}
				});	
	}
}
