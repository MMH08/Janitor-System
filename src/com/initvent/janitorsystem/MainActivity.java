package com.initvent.janitorsystem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	Spinner spnBuilding1;
	AutoCompleteTextView spnBuilding;
	Button btnNewTask, btnNewInfo,btnSettings,btnMaster;
	ListView lvTask;
	// Session Manager Class
	SessionManager session;
	public static int flagforlogincall=1;
	
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayAdapterList adapter;
	
	private ArrayAdapter<String> arradapter;
	List<String> osList=new ArrayList<String>(); 
	String spnSelcteditem="";
	static String building_id;
	static int datapopFlag=1;
	
	TextView chng_taskID;
	
	SharedPreferences pref;
	
	 String server;
	 String service;
	 String userid;
	 String pass;
	 String encode_pass;
	 
	 ContentValues cv;
	 String sbuild="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spnBuilding1=(Spinner)findViewById(R.id.spnBuilding);
		spnBuilding=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		btnNewTask=(Button)findViewById(R.id.btnNewTask);
		btnNewInfo=(Button)findViewById(R.id.btnNewInfo);
		btnSettings=(Button)findViewById(R.id.btnSettings);
		btnMaster=(Button)findViewById(R.id.btnMaster);
		
		lvTask=(ListView)findViewById(R.id.lvTask);
		
		// Session class instance
        session = new SessionManager(getApplicationContext());

        //if(flagforlogincall==1)
       // {
	        //session.checkLogin();   //Every time force login
	        //flagforlogincall++;
        //}
		
		btnNewTask.setOnClickListener(this);
		btnNewInfo.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		btnMaster.setOnClickListener(this);
		
		btnSettings.setVisibility(View.INVISIBLE);
		btnMaster.setVisibility(View.INVISIBLE);
		spnBuilding1.setVisibility(View.INVISIBLE);
		//String[] test={"Test1","Test2"};

		int flagselBuild=1;
		//if(flagselBuild==1)
			//spnBuilding.setText("Select Building");
		//	osList.add("Select Building");
		osList.clear();
		Cursor cursor_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building order by buildingname",null);
		while(cursor_building.moveToNext())
		{
			osList.add(cursor_building.getString(1));
			flagselBuild++;
		}
		
		arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
		//spnBuilding=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		spnBuilding.setThreshold(1);
		spnBuilding.setAdapter(arradapter);
		
     
		pref = getSharedPreferences("selBuild", MODE_PRIVATE);
		
		sbuild = pref.getString("sbuild", "");
		if(!sbuild.equals(""))
			spnBuilding.setText(sbuild);//(osList.indexOf(sbuild));

		// OnCLickListiner For List Items
		lvTask.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				SharedPreferences.Editor editor = pref.edit();
				editor.putString("sbuild", spnSelcteditem);
				editor.commit();
				
				chng_taskID = (TextView) view.findViewById(R.id.txtadpId);

			    String memberID_val = chng_taskID.getText().toString();

				Intent modify_intent = new Intent(getApplicationContext(),UpdateDeleteTask.class);
				
				modify_intent.putExtra("chng_taskID", memberID_val);
				
				startActivity(modify_intent);
				
			}
		});	
		
		Cursor cursor_login = SplashActivity.mydb.rawQuery("SELECT * FROM  Login ",null);
		if (cursor_login != null) {
			cursor_login.moveToLast();
			try{
			if(isConnected()){
				
				 server=cursor_login.getString(1);
		   		 service=cursor_login.getString(2);
		   		 userid=cursor_login.getString(3);
		   		 pass=cursor_login.getString(4);
		   		 pass=Uri.encode(pass);
		   		 
		   		 
		   		 
		         String task_url = "http://"+server+"/"+service+"/TMSService.svc/TaskListForAndroid?user_Id="+ userid +"&user_Pass="+ pass;	
		         new Tasks().execute(task_url);
		         
		         //populatelist();
		         
		         String info_url = "http://"+server+"/"+service+"/TMSService.svc/InfoForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
		         new Building_Info().execute(info_url);
		         
			}
			}
			catch(Exception e){
				
				
			}
			cursor_login.close();
		}
		spnBuilding.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count){
		        //if "s" is your "special" text clear the textview
		    	System.out.println("OnTextChanged.");
		    	populatelist();
		    }
		    		
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				populatelist();
				System.out.println("OnTextChanged.");
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				System.out.println("OnTextChanged.");
				populatelist();
			}
		});
		//populatelist();
	}
	private boolean isConnected() {
		// TODO Auto-generated method stub
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) 
            return true;
        else
            return false;   
	}
	// Menu Activities
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    //pref.edit().remove("sbuild").commit();
	}
	//@Override
	//public void onBackPressed(){
	//     // do something here and don't write super.onBackPressed()
	//	pref.edit().remove("sbuild").commit();
	//}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:			
			Intent settingsIntent = new Intent(this, LoginActivity.class);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(settingsIntent);
			return true;			
		case R.id.infolist:
			Intent masterIntent = new Intent(this, InfoListActivity.class);
			masterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(masterIntent);
			return true;			
		case R.id.finishedTask:
			Intent finIntent = new Intent(this, FinishedTaskActivity.class);
			finIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(finIntent);
			return true;			
		default:
			return super.onOptionsItemSelected(item);
       }

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnNewTask:
				if(spnBuilding.getText().toString().equals(""))
					Toast.makeText(getBaseContext(),"Building data should not blank",Toast.LENGTH_LONG).show();
				else
				{
					SharedPreferences.Editor editor = pref.edit();
					editor.putString("sbuild", spnBuilding.getText().toString());
					editor.commit();
					
					Intent taskIntent = new Intent(this, TaskActivity.class);
					taskIntent.putExtra("selectedBuilding", spnBuilding.getText().toString());//spnBuilding.getSelectedItem().toString()
					taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(taskIntent);
				}
				break;
			case R.id.btnNewInfo:
				if(spnBuilding.getText().toString().equals(""))
					Toast.makeText(getBaseContext(),"Building data should not blank",Toast.LENGTH_LONG).show();
				else
				{
					SharedPreferences.Editor editor = pref.edit();
				    //editor = pref.edit();
					editor.putString("sbuild", spnBuilding.getText().toString());
					editor.commit();
	
					Intent infoIntent = new Intent(this, InfoActivity.class);
					infoIntent.putExtra("selectedBuilding", spnBuilding.getText().toString());//spnBuilding.getSelectedItem().toString()
					infoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(infoIntent);
				}
				break;
			case R.id.btnSettings:
				Intent settingsIntent = new Intent(this, LoginActivity.class);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(settingsIntent);
				break;
			case R.id.btnMaster:
				Intent masterIntent = new Intent(this, MasterActivity.class);
				masterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(masterIntent);
				break;
			default:
				break;
		
		}
	}
	private void populatelist() {
		// TODO Auto-generated method stub
		int position=0;
		/*
		if(sbuild.equals("") || sbuild.equals("Select Building"))
		{
			spnSelcteditem=spnBuilding.getText().toString();
//			spnSelcteditem=spnBuilding.getSelectedItem().toString();
		}
		else
		{
			spnSelcteditem=sbuild;
			position=1;
		}
		*/
		if(spnBuilding.getText().toString().equals(""))
		{
			spnSelcteditem=spnBuilding.getText().toString();
		}
		else
		{
			spnSelcteditem=spnBuilding.getText().toString();
			position=1;
		}
		//if(datapopFlag==1)
		populateforSelecteditem(spnSelcteditem,position);
		/*
		spnBuilding.setOnItemClickListener(new OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                int pos, long id) {
	            //Toast.makeText(MainActivity.this,
	            //        adapter.getItem(position).toString(),
	            //        Toast.LENGTH_SHORT).show();
	        	spnSelcteditem=spnBuilding.getText().toString();
				
				if(pos==0)
				{
					populateforSelecteditem(spnSelcteditem,pos);
				}
				else
				{
					populateforSelecteditem(spnSelcteditem,pos);
				}
	        }
	    });  
		*/
/*		
		spnBuilding.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				spnSelcteditem=spnBuilding.getText().toString();
//				spnSelcteditem=spnBuilding.getSelectedItem().toString();
								
				if(pos==0)
				{
					populateforSelecteditem(spnSelcteditem,pos);
				}
				else
				{
					populateforSelecteditem(spnSelcteditem,pos);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
*/		
		//adapter.clear();
		
	}
	private void populateforSelecteditem(String spnSelcteditem2,int pos) {
		// TODO Auto-generated method stub
		//lvTask.cl
		arrList.clear();
		building_id="";
		Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem2 +"'",null);
		while(cursor_buildingid.moveToNext())
		{
			building_id = cursor_buildingid.getString(5);
		}


		List<GetListView> taskList = new ArrayList<GetListView>();// 
		Cursor cursor_Task=null;
		if(pos==0)
		{
			cursor_Task = SplashActivity.mydb.rawQuery("SELECT t.id,taskHeading,taskNote,taskCreateDate,taskDeadline,u.user_id,taskSlip,substr(taskDeadline,1,10) date FROM  Task t inner Join User u on t.user_Id=u.sysUser_id WHERE t.taskEndtimestamp='' ORDER BY Date(date)",null);			
		}
		else
		{
			cursor_Task = SplashActivity.mydb.rawQuery("SELECT t.id,taskHeading,taskNote,taskCreateDate,taskDeadline,u.user_id,taskSlip,substr(taskDeadline,1,10) date FROM  Task t inner Join User u on t.user_Id=u.sysUser_id  WHERE t.building_Id='" + building_id.trim() + "' and t.taskEndtimestamp='' ORDER BY Date(date)",null);
		}
		
		/*
		if(pos==0)
		{
			cursor_Task = SplashActivity.mydb.rawQuery("SELECT t.id,taskHeading,taskNote,taskCreateDate,taskDeadline,u.userName FROM  Task t Left join Janitor u on t.user_Id=u.user_id WHERE t.taskEndtimestamp='' ORDER BY taskDeadline",null);			
		}
		else
		{
			cursor_Task = SplashActivity.mydb.rawQuery("SELECT t.id,taskHeading,taskNote,taskCreateDate,taskDeadline,u.userName FROM  Task t Left Join Janitor u on t.user_Id=u.user_id  WHERE t.building_Id='" + building_id.trim() + "' and t.taskEndtimestamp='' ORDER BY taskDeadline",null);
		}
		*/

		 // looping through all rows and adding to list
		GetListView contact;
		 //if (cursor_Task.moveToFirst()) {
			 while (cursor_Task.moveToNext()) 
			 {
			 contact = new GetListView();
			 //contact.setID(Integer.parseInt(cursor_Task.getString(0)));
			 contact.setID(Integer.parseInt(cursor_Task.getString(0)));
			 contact.settaskHeading(cursor_Task.getString(1));
			 contact.setbuilding_id(cursor_Task.getString(2));
			 contact.settaskNote(cursor_Task.getString(3));
			 contact.settaskCreateuser(cursor_Task.getString(5));
			 contact.settaskDeadline(cursor_Task.getString(4));
			 contact.settaskSlip(cursor_Task.getString(6));
			 // Adding contact to list
			 taskList.add(contact);
			 } 
		 //}
			for (GetListView cn : taskList) {
				String log = "ID: " + cn.getID() + " Heading: " + cn.gettaskHeading()
				+ ", Task start time: " + cn.getbuilding_id() + " Task create time: " + cn.gettaskNote()+ " Task created user: " + cn.gettaskCreateuser();

				// Writing Contacts to log
				Log.d("Result: ", log);
				//add contacts data in arrayList
				arrList.add(cn);

				}
				adapter = new ArrayAdapterList(this, R.layout.screen_list_task,arrList);			
				lvTask.setAdapter(adapter);
				//arradapter.clear();
				//arrList.clear();
				//taskList.clear();
	}
	
	private class Tasks extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(MainActivity.this);
	          pDialog.setMessage("Getting Data ...");
	          pDialog.setIndeterminate(false);
	          pDialog.setCancelable(true);
	          pDialog.show();
	          
	  	}
			
			@Override
			protected String doInBackground(String... urls) {

				return GET(urls[0]);
			}

			private String GET(String url) {
				InputStream inputStream = null;
				String result = "";
				try 
				{

					// create HttpClient
					HttpClient httpclient = new DefaultHttpClient();

					// make GET request to the given URL
					HttpResponse httpResponse = httpclient
							.execute(new HttpGet(url));

					// receive response as inputStream
					inputStream = httpResponse.getEntity().getContent();

					// convert inputstream to string
					if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";

				} 
				catch (Exception e) 
				{
					Log.d("InputStream", e.getLocalizedMessage());
				}
				//pDialog.dismiss();
		        //if (pDialog != null && pDialog.isShowing()) {
		        //    pDialog.dismiss();
		        //}
		        
				return result;
			}

			// onPostExecute displays the results of the AsyncTask.
			@Override
			protected void onPostExecute(String result) {		
				//pDialog.dismiss();
				try {
			        if ((this.pDialog != null) && this.pDialog.isShowing()) {
			            this.pDialog.dismiss();
			        }
			    } catch (final IllegalArgumentException e) {
			        // Handle or log or ignore
			    } catch (final Exception e) {
			        // Handle or log or ignore
			    } finally {
			        this.pDialog = null;
			    }  

				// Integer OutputData;
		        try 
				{

		        	JSONArray jsonarray = new JSONArray(result);
					int lengthJsonArr = jsonarray.length();
					
					SplashActivity.mydb.execSQL("DELETE FROM Task");
					SplashActivity.mydb.execSQL("DELETE FROM Photo");
					SplashActivity.mydb.execSQL("DELETE FROM Attachment");
					for (int i = 0; i < lengthJsonArr; i++) 
					{
						JSONObject jsonChildNode = jsonarray.getJSONObject(i);

						String heading = jsonChildNode.optString("heading").toString();
		 				String note = jsonChildNode.optString("note").toString();
		 				String slip = jsonChildNode.optString("slip").toString();
		 				String whichColor = jsonChildNode.optString("whichColor").toString();

		 				String createDate = jsonChildNode.optString("taskCreateDate").toString();
	 					if(!createDate.equals("null"))
	 					{
		 					int idx1 = createDate.indexOf("(");
		 				    int idx2 = createDate.indexOf(")") - 5;
		 				    String s = createDate.substring(idx1+1, idx2);
		 				    long l = Long.valueOf(s);
		 				    createDate=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(l);
	 					}
	 					else
	 					{
	 						createDate="";
	 					}
		 				String startDate = jsonChildNode.optString("startTimeStamp").toString();
	 					if(!startDate.equals("null"))
	 					{
		 					int idx1 = startDate.indexOf("(");
		 				    int idx2 = startDate.indexOf(")") - 5;
		 				    String s = startDate.substring(idx1+1, idx2);
		 				    long l = Long.valueOf(s);
		 				   startDate=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(l);
	 					}
	 					else
	 					{
	 						startDate="";
	 					}
	 					
	 					String finishDate = jsonChildNode.optString("endTimeStamp").toString();
	 					if(!finishDate.equals("null"))
	 					{
		 					int idx1 = finishDate.indexOf("(");
		 				    int idx2 = finishDate.indexOf(")") - 5;
		 				    String s = finishDate.substring(idx1+1, idx2);
		 				    long l = Long.valueOf(s);
		 				   finishDate=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(l);
	 					}
	 					else
	 					{
	 						finishDate="";
	 					}
	 					String tasktype_id = jsonChildNode.optString("tasktype_id").toString();
	 					String building_id = jsonChildNode.optString("building_id").toString();
		 				String tenant_id = jsonChildNode.optString("tenant_id").toString();
		 				String user_id = jsonChildNode.optString("user_id").toString();
		 				
		 				String task_deadline = jsonChildNode.optString("deadline").toString();
		 				if(!task_deadline.equals("null"))
	 					{
		 					int idx1 = task_deadline.indexOf("(");
		 				    int idx2 = task_deadline.indexOf(")") - 5;
		 				    String s = task_deadline.substring(idx1+1, idx2);
		 				    long l = Long.valueOf(s);
		 				   task_deadline=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(l);
	 					}
		 				else
	 					{
		 					task_deadline="";
	 					}
		 				String task_id = jsonChildNode.optString("id").toString();
		 				
		 				//image
		 				String photo = jsonChildNode.optString("photoAttachmentFiles").toString();
		 				JSONArray jsonarrayPhoto = new JSONArray(photo);
		 				int lengthJsonArrPhoto = jsonarrayPhoto.length();
		 				for (int j = 0; j < lengthJsonArrPhoto; j++) 
						{
			 				JSONObject jsonChildNodePhoto = jsonarrayPhoto.getJSONObject(j);
			 				
				 				String img = jsonChildNodePhoto.optString("taskImg").toString();
				 				
				 				Bitmap decodedString=decodeBase64(img);
				 				byte[] data = getBitmapAsByteArray(decodedString);

				 				//SplashActivity.mydb.execSQL("insert into Photo"
			    				//		+ " (picture,task_id) "
			    				//		+" VALUES ('"+ data + "','"+ task_id + "')");

				 				cv = new ContentValues();
				 				cv.put("task_id",task_id);				 				
				 				cv.put("picture", data);
				 				
				 				SplashActivity.mydb.insert("Photo", null, cv);
						}
		 				
		 				//atachment
		 				String attach = jsonChildNode.optString("attachmentList").toString();
		 				JSONArray jsonarrayAttach = new JSONArray(attach);
		 				int lengthJsonArrAttach = jsonarrayAttach.length();
		 				for (int j = 0; j < lengthJsonArrAttach; j++) 
						{
			 				JSONObject jsonChildNodeAttach = jsonarrayAttach.getJSONObject(j);
			 				
			 				String InvoiceId = jsonChildNodeAttach.optString("attachmentID").toString();
			 				String attachName = jsonChildNodeAttach.optString("attachmentName").toString();
			 				String attachNote = jsonChildNodeAttach.optString("attachmentNote").toString();
			 				String attach_id = jsonChildNodeAttach.optString("id").toString();
			 				String attachCost = jsonChildNodeAttach.optString("attachmentCost").toString();
			 				//String task_id = jsonChildNodeAttach.optString("task_id").toString();
			 				
			 				SplashActivity.mydb.execSQL("insert into Attachment"
		    						+ " (invoiceId,attachName,attachNote,task_id,attach_id,attachmentCost) "
		    						+" VALUES ('"+ InvoiceId + "','"+ attachName + "','"+ attachNote + "','"+ task_id + "','"+ attach_id + "','"+ attachCost + "')");

			 				// image under atachment
			 				String attachmentInfoFilesList = jsonChildNodeAttach.optString("attachmentInfoFilesList").toString();
			 				JSONArray jsonarrayAttachimg = new JSONArray(attachmentInfoFilesList);
			 				int lengthJsonArrAttachimg = jsonarrayAttachimg.length();
			 				for (int k = 0; k < lengthJsonArrAttachimg; k++) 
							{
				 				JSONObject jsonChildNodeAttachimg = jsonarrayAttachimg.getJSONObject(k);
				 				String img = jsonChildNodeAttachimg.optString("TaskAtthImg").toString();
				 				
				 				Bitmap decodedString=decodeBase64(img);
				 				byte[] data = getBitmapAsByteArray(decodedString);
				 				
				 				Cursor cursor_attimg = SplashActivity.mydb.rawQuery("Select * from Attachment ORDER BY id DESC LIMIT 1",null);
				 				while(cursor_attimg.moveToNext())
				 				{
				 					attach_id=cursor_attimg.getString(0);
				 				}
				 				
				 				ContentValues cv1;
				 				cv1 = new ContentValues();
				 				cv1.put("task_id",task_id);				 				
				 				cv1.put("attach_id",attach_id);				 				
				 				cv1.put("picture", data);
				 				
				 				SplashActivity.mydb.insert("Photo", null, cv1);

							}
			 				
						}

		 				SplashActivity.mydb.execSQL("insert into Task"
		    						+ " (taskHeading,taskNote,taskDeadline,taskSlip,taskCreateDate,taskStarttimestamp,taskEndtimestamp,tasktype_Id,building_Id,tenant_Id,user_Id,task_Id,whichColor) "
		    						+" VALUES ('"+ heading + "','"+ note + "','"+ task_deadline + "','"+ slip + "','"+ createDate + "','"+ startDate + "','"+ finishDate + "','"+ tasktype_id + "','"+ building_id + "','"+ tenant_id + "','"+ user_id + "','"+ task_id + "','"+ whichColor + "')");
						
					}

					//Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
					//startActivity(logintomain);
					//finish();
					populatelist();
					//String spnSitem=spnBuilding.getSelectedItem().toString();
					//populateforSelecteditem(spnSitem,0);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
					//Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
					//startActivity(logintomain);
					//finish();
				}

			}

			public Bitmap decodeBase64(String input)
			{
			    byte[] decodedBytes = Base64.decode(input, 0);
			    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
			}
			public byte[] getBitmapAsByteArray(Bitmap bitmap)
			{
			    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			    bitmap.compress(CompressFormat.PNG, 100, outputStream);
			    return outputStream.toByteArray();
			    
			}
		}
private class Building_Info extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(MainActivity.this);
	          pDialog.setMessage("Getting Data ...");
	          pDialog.setIndeterminate(false);
	          pDialog.setCancelable(true);
	          pDialog.show();
	          
	  	}
			
			@Override
			protected String doInBackground(String... urls) {

				return GET(urls[0]);
			}

			private String GET(String url) {
				InputStream inputStream = null;
				String result = "";
				try 
				{

					// create HttpClient
					HttpClient httpclient = new DefaultHttpClient();

					// make GET request to the given URL
					HttpResponse httpResponse = httpclient
							.execute(new HttpGet(url));

					// receive response as inputStream
					inputStream = httpResponse.getEntity().getContent();

					// convert inputstream to string
					if (inputStream != null)
						result = convertInputStreamToString(inputStream);
					else
						result = "Did not work!";

				} 
				catch (Exception e) 
				{
					Log.d("InputStream", e.getLocalizedMessage());
				}
				//pDialog.dismiss();
		        //if (pDialog != null && pDialog.isShowing()) {
		        //    pDialog.dismiss();
		        //}
		        
				return result;
			}

			// onPostExecute displays the results of the AsyncTask.
			@Override
			protected void onPostExecute(String result) {		
				//pDialog.dismiss();

 				try {
			        if ((this.pDialog != null) && this.pDialog.isShowing()) {
			            this.pDialog.dismiss();
			        }
			    } catch (final IllegalArgumentException e) {
			        // Handle or log or ignore
			    } catch (final Exception e) {
			        // Handle or log or ignore
			    } finally {
			        this.pDialog = null;
			    }  

				// Integer OutputData;
		        try 
				{
		        	JSONArray jsonarray = new JSONArray(result);
					int lengthJsonArr = jsonarray.length();
					
					SplashActivity.mydb.execSQL("DELETE FROM Info");
					for (int i = 0; i < lengthJsonArr; i++) 
					{

						JSONObject jsonChildNode = jsonarray.getJSONObject(i);
	 					String createDate = jsonChildNode.optString("createDate").toString();
	 					 
	 					int idx1 = createDate.indexOf("(");
	 				    int idx2 = createDate.indexOf(")") - 5;
	 				    String s = createDate.substring(idx1+1, idx2);
	 				    long l = Long.valueOf(s);
	 				    createDate=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(l);
	 					
	 					String note = jsonChildNode.optString("note").toString();
						String building_id = jsonChildNode.optString("building_id").toString();
						String info_id = jsonChildNode.optString("id").toString();
						
						SplashActivity.mydb.execSQL("insert into Info"
		    						+ " (infocreateDate,infoNote,building_id,info_id) VALUES ('"+ createDate + "','"+ note + "','"+ building_id + "','"+ info_id + "')");
						
					}

					//Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
					//startActivity(logintomain);
					//finish();

				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
					//Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
					//startActivity(logintomain);
					//finish();
				}

			}
		}
	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

}
