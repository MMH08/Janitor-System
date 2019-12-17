package com.initvent.janitorsystem;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class UpdateTaskTimeActivity extends Activity implements OnClickListener {
	
	TimePicker sttime,entime;
	Button btnSave;
	
	String memberId;
	String startTime,endTime;
	int shour, sminute, ehour, eminute;
	Timestamp stimestamp,etimestamp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_task_time);
		
		sttime=(TimePicker)findViewById(R.id.timePicker3);
		entime=(TimePicker)findViewById(R.id.timePicker2);
		btnSave=(Button)findViewById(R.id.button1);
		btnSave.setOnClickListener(this);
		
		sttime.setIs24HourView(true);
		entime.setIs24HourView(true);
		
		Intent i = getIntent();
		memberId = i.getStringExtra("chng_taskID");//Only Id from table
		
		Cursor cursor_task_guid = SplashActivity.mydb.rawQuery("SELECT * FROM Task WHERE task_id='"+memberId+"'",null);
		while(cursor_task_guid.moveToNext())
		{
			startTime=cursor_task_guid.getString(5);
			endTime=cursor_task_guid.getString(6);
		}
		Date sdate=null,edate=null;
		try {
			sdate = new SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(startTime);
			edate = new SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(endTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sttime.setCurrentHour(sdate.getHours());
		sttime.setCurrentMinute(sdate.getMinutes());
		entime.setCurrentHour(edate.getHours());
		entime.setCurrentMinute(edate.getMinutes());
		/*
		if(hourOfDay < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
		*/
	}

	@Override
	public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()){
		    case R.id.button1:
		    	shour = sttime.getCurrentHour();
		    	sminute = sttime.getCurrentMinute();
		    	ehour = entime.getCurrentHour();
		    	eminute = entime.getCurrentMinute();
		    	
		    	String sshour="",ssminute="",eehour="",eeminute="";
		    	if(shour>=0 && shour<10)
		    		sshour="0"+shour;
		    	else
		    		sshour=""+shour;
		    	if(sminute>=0 && sminute<10)
		    		ssminute="0"+sminute;
		    	else
		    		ssminute=""+sminute;
		    	if(ehour>=0 && ehour<10)
		    		eehour="0"+ehour;
		    	else
		    		eehour=""+ehour;
		    	if(eminute>=0 && eminute<10)
		    		eeminute="0"+eminute;
		    	else
		    		eeminute=""+eminute;

		    	String sstr = sshour+":"+ssminute+":00";
		    	String estr = eehour+":"+eeminute+":00";
	    		stimestamp =Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd ").format(new Date()).concat(sstr));
	    		etimestamp =Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd ").format(new Date()).concat(estr));
		    	
		    	if(isConnected()){
		     		new ExportTaskOnline().execute(); //task export to server
		     	}
		        else{
		        	Toast.makeText(
							getBaseContext(),
							"Oops!You are not Connected to INTERNET",
							Toast.LENGTH_LONG).show();
		        	
		        }
	        	Intent tasttomain = new Intent(this, MainActivity.class);
				tasttomain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(tasttomain);
		    	
		    	break;
			
		}
	}
	public class ExportTaskOnline extends AsyncTask<String, Void, Boolean> {
		   private ProgressDialog pDialog;
	    	
			 @Override
		   protected void onPreExecute() {
		         super.onPreExecute();
		         
		         pDialog = new ProgressDialog(UpdateTaskTimeActivity.this);
		         pDialog.setMessage("Getting Data ...");
		         pDialog.setIndeterminate(false);
		         pDialog.setCancelable(true);
		         //pDialog.show();
		         
		 	}
			@SuppressLint("NewApi")
			protected Boolean doInBackground(final String... args) {

				try {

					JSONObject parrent = new JSONObject();
					JSONArray Array = new JSONArray();
					Cursor Online = SplashActivity.mydb.rawQuery(
							"select * from Task where task_id='"+memberId+"'", null);

							JSONObject new_member = new JSONObject();
							
							while (Online.moveToNext()) {

								 Cursor curPhoto = SplashActivity.mydb.rawQuery("select * from Photo WHERE task_id='"+Online.getString(14)+"'  and attach_id is null",null);

								 JSONArray photoList = new JSONArray();
									
								 if (curPhoto != null) {
									 
									 while (curPhoto.moveToNext()) {
										 
										 JSONObject objPhoto = new JSONObject(); 
										 
										 byte[] picture = curPhoto.getBlob(curPhoto.getColumnIndex("picture"));
										 String image = Base64.encodeToString(picture, Base64.DEFAULT);
										 if(image.length()>50)
										 {
											 objPhoto.put("taskImg",image);
											 photoList.put(objPhoto);
										 }
										 //objPhoto.put("task_Id","C34C8AC4-28D7-4BAF-BE38-0844B45210A9");
								 
										 
									 
									 }
								 }
								 
								 Cursor curattach = SplashActivity.mydb.rawQuery("select * from Attachment WHERE task_id='"+Online.getString(14)+"'",null);

								 JSONArray attachList = new JSONArray();
									
								 if (curattach != null) {
									 
									 while (curattach.moveToNext()) {
										 
										 JSONObject objAttach = new JSONObject(); 
										 
										 objAttach.put("attachmentID", curattach.getString(1));
										 objAttach.put("attachmentName", curattach.getString(2));
										 objAttach.put("attachmentNote", curattach.getString(3));
										 objAttach.put("task_id", curattach.getString(4));
										 objAttach.put("attachmentCost", curattach.getString(6));
										 //objPhoto.put("task_Id","C34C8AC4-28D7-4BAF-BE38-0844B45210A9");
								 
										 Cursor curPhotoattach = SplashActivity.mydb.rawQuery("select * from Photo WHERE task_id='"+Online.getString(14)+"' and attach_id='"+curattach.getString(0)+"'",null);

										 JSONArray photoListattach = new JSONArray();
											
										 if (curPhotoattach != null) {
											 
											 while (curPhotoattach.moveToNext()) {
												 
												 JSONObject objPhotoattach = new JSONObject(); 
												 
												 byte[] picture = curPhotoattach.getBlob(curPhoto.getColumnIndex("picture"));
												 String image = Base64.encodeToString(picture, Base64.DEFAULT);
												 if(image.length()>50)				
												 {
													 objPhotoattach.put("TaskAtthImg",image);
													 photoListattach.put(objPhotoattach);
												 }
												 //objPhoto.put("task_Id","C34C8AC4-28D7-4BAF-BE38-0844B45210A9");
										 
												 
											 
											 }
										 }
										 objAttach.put("attachmentInfoFilesList", photoListattach);

										 
										 attachList.put(objAttach);
									 
									 }
									 
								 }
								 
								new_member.put("photoAttachmentFiles",photoList);
								new_member.put("attachmentList",attachList);
								new_member.put("heading", Online.getString(1));
								new_member.put("note", Online.getString(2));
								new_member.put("tasktype_id", Online.getString(9));
								new_member.put("building_id", Online.getString(10));
								
								if(Online.getString(11) != null && !Online.getString(11).isEmpty() && !Online.getString(11).equals("null"))						
									new_member.put("tenant_id", Online.getString(11));
								
								Cursor cursor_loginuser = SplashActivity.mydb.rawQuery("SELECT u.sysUser_id FROM  Login l inner Join User u on l.userId=u.user_id",null);			

								while(cursor_loginuser.moveToNext())
								{
									new_member.put("user_id", cursor_loginuser.getString(0));
									new_member.put("userJanitor_id", cursor_loginuser.getString(0));
								}
								SimpleDateFormat format = new SimpleDateFormat("Z");
								SimpleDateFormat dformat=new SimpleDateFormat( "yyyy-MM-dd hh:mm a");

								//Date date = new Date();
								Date date = dformat.parse(Online.getString(13));						
								new_member.put("taskCreateDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");
								
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a"); 
								String ssdate=Online.getString(5);
								String sedate=Online.getString(6);
								//Date sdate = dateFormat.parse(Online.getString(5));
								//Date edate = dateFormat.parse(Online.getString(6));
								
								//new_member.put("taskCreateDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");
								//starttimestmp != null && !starttimestmp.isEmpty() && !starttimestmp.equals("null")
								new_member.put("startTimeStamp", "/Date(" + String.valueOf(stimestamp.getTime()) + format.format(stimestamp) + ")/");
								new_member.put("endTimeStamp", "/Date(" + String.valueOf(etimestamp.getTime()) + format.format(etimestamp) + ")/");

								new_member.put("slip", Online.getString(4));
								String ddate=Online.getString(3);
								
								if(ddate != null && !ddate.isEmpty() && !ddate.equals("null"))
								{
									Date sdate = dformat.parse(Online.getString(3));
									new_member.put("deadline", "/Date(" + String.valueOf(sdate.getTime()) + format.format(sdate) + ")/");
								}
								
								Array.put(new_member);
							}
							parrent.put("Tasks", Array);

					Cursor cursor_login = SplashActivity.mydb.rawQuery("SELECT *FROM  Login ",null);
					String server="",service="",userid="",pass="";
					
					if (cursor_login != null) {
						cursor_login.moveToLast();
						try{
							
							 server=cursor_login.getString(1);
				    		 service=cursor_login.getString(2);
				    		 userid=cursor_login.getString(3);
				    		 pass=cursor_login.getString(4);
				    		 pass=Uri.encode(pass);;
						
						}catch(Exception e){
							
							
						}
						//cursor_login.close();
						
					}
					parrent.put("userId",userid );
					parrent.put("userPass", pass);
					parrent.put("task_id", memberId);

					String URL = "http://"+server+"/"+service+"/TMSService.svc/TaskForAndroid";
					
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httppostreq = new HttpPost(URL);
					StringEntity se = new StringEntity(parrent.toString());
					se.setContentType("application/json;charset=UTF-8");
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
					httppostreq.setEntity(se);
					HttpResponse httpresponse = httpclient.execute(httppostreq);
					
					 try {
		                 
						  String t= EntityUtils.toString(httpresponse.getEntity());
						  if(t.contentEquals("true")){
							  Toast.makeText(getApplicationContext(),"Export successful!"+t, Toast.LENGTH_LONG).show();
						  }
						  else{
							  
							  Toast.makeText(UpdateTaskTimeActivity.this, "Export failed",Toast.LENGTH_SHORT).show();
						  }

						 
		              } catch (ClientProtocolException e) {
		                 
		                  e.printStackTrace();
		              } catch (IOException e) {
		                  
		                  e.printStackTrace();
		              }
					return true;

				}

				catch (Exception e) {
					Log.e("ActivityB", e.getMessage(), e);
					return true;
				}
			}

			protected void onPostExecute(final Boolean success) {
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

				if (success) {
					Toast.makeText(UpdateTaskTimeActivity.this, "Update successful!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(UpdateTaskTimeActivity.this, "Update failed",
							Toast.LENGTH_SHORT).show();
					
					 //mydb.execSQL("DELETE FROM " + TABLE);
					
				}
			}
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
}
