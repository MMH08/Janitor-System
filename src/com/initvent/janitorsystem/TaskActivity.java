package com.initvent.janitorsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskActivity extends ActionBarActivity implements OnClickListener{
	Button btnTaskAttachAdd,btnTaskPhotoAdd,btnTaskAdd,btnTaskSave,btnTaskDelete;
	EditText etTaskHeading,etDeadline,etSlip,etTaskNote;
	Spinner spnBuilding,spnTenant,spnTaskType;
	public static int dateFlag; 
	ListView lvTaskPhoto,lvTaskAttach;
	ImageView imageView1;
	
	private ArrayAdapter<String> arradapter;
	List<String> osList=new ArrayList<String>(); 
	
	String spnSelcteditem="",spnTenSelecteditem="";
	static String building_id;
	
	private ArrayAdapter<String> arradapterten;
	List<String> osListten=new ArrayList<String>(); 

	private Uri fileUri;
	ContentValues cv;
	Bitmap img1;

	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayListAdapterPhoto adapter;
	
	ArrayList<GetListView> attarrList = new ArrayList<GetListView>();
	ArrayAdapterInfoList attadapter;
	
	static String attachtoTaskid=null;

	String cur_date_time,starttimestamp="",endtimestamp="";
	
	final Context context = this;
	int ph_id=0,at_id=0;
	static String tk_id="";
	static int deleteFlag;
	
	String deleted_id="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		//copyDatabase();
		deleteFlag=1;
		
		btnTaskAttachAdd=(Button)findViewById(R.id.btnTaskAttachAdd);
		btnTaskPhotoAdd=(Button)findViewById(R.id.btnTaskPhotoAdd);
		btnTaskAdd=(Button)findViewById(R.id.btnTaskAdd);
		btnTaskSave=(Button)findViewById(R.id.btnTaskSave);
		btnTaskDelete=(Button)findViewById(R.id.btnTaskDelete);
		
		btnTaskAttachAdd.setOnClickListener(this);
		btnTaskPhotoAdd.setOnClickListener(this);
		btnTaskAdd.setOnClickListener(this);
		btnTaskSave.setOnClickListener(this);
		
		etTaskHeading=(EditText)findViewById(R.id.etTaskHeading);
		spnBuilding=(Spinner)findViewById(R.id.spnBuilding);
		spnTenant=(Spinner)findViewById(R.id.spnTenant);
		etTaskNote=(EditText)findViewById(R.id.etTaskNote);

		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnTaskAttachAdd.setWidth(width/2);
	    btnTaskPhotoAdd.setWidth(width/2);
	    btnTaskAdd.setWidth(width/2);
	    btnTaskSave.setWidth(width/2);
	    
		imageView1=(ImageView)findViewById(R.id.imageView1);

		lvTaskPhoto=(ListView)findViewById(R.id.lvTaskPhoto);
		lvTaskAttach=(ListView)findViewById(R.id.lvTaskAttach);
		
		attachtoTaskid=getIntent().getStringExtra("attachtoTask"); 
		
		//deleted_id=nullCheck();
		if(attachtoTaskid==null)
			attachtoTaskid="";
		
    	if(attachtoTaskid.equals(""))
    	{
    		if(!getIntent().getStringExtra("selectedBuilding").equals(""))
    		{
    			//arradapter.clear();
        		osList.add(getIntent().getStringExtra("selectedBuilding"));
        		arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
        		spnBuilding.setAdapter(arradapter);
        		spnBuilding.setEnabled(false);
        		spnBuilding.setClickable(false);
        		
         		//taskAdd();
        		String selbuilidten="";
        		Cursor selbuiltenlistcur= SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='" + getIntent().getStringExtra("selectedBuilding") + "' ",null);
        		while(selbuiltenlistcur.moveToNext())
        		{
        			selbuilidten=selbuiltenlistcur.getString(5);
        		}
        		
        		//arradapterten.clear();
            	int flagselTan=1;		
        		Cursor cursor_tenant = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where building_id='"+selbuilidten+"' ORDER BY tenName ",null);
        		while(cursor_tenant.moveToNext())
        		{
        			if(flagselTan==1)
        				osListten.add("Select Tenant");
        			osListten.add(cursor_tenant.getString(1));
        			flagselTan++;
        		}
        		
        		arradapterten = new ArrayAdapter<String>(this,R.layout.spinner_item,osListten);
        		spnTenant.setAdapter(arradapterten);
    		}
    	}
    	else
    	{

    	}
    	/*
    	spnBuilding.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if(pos!=0)
				{
					arradapterten.clear();
					String selectedbuilding="";
					selectedbuilding=spnBuilding.getSelectedItem().toString();
					
					Cursor selbuiltenlistcur= SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='" + selectedbuilding + "' ",null);
		    		while(selbuiltenlistcur.moveToNext())
		    		{
		    			selectedbuilding=selbuiltenlistcur.getString(5);
		    		}
		        	
		        	int flagselTan=1;
		    		Cursor cursor_tenant = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where building_id='"+selectedbuilding+"' ORDER BY tenName ",null);
		    		while(cursor_tenant.moveToNext())
		    		{
		    			if(flagselTan==1)
		    				osListten.add("Select Tenant");
		    			osListten.add(cursor_tenant.getString(1));
		    			flagselTan++;
		    		}
		    		
		    		arradapterten = new ArrayAdapter<String>(TaskActivity.this,R.layout.spinner_item,osListten);
		    		spnTenant.setAdapter(arradapterten);
					Cursor cur_tenan = SplashActivity.mydb.rawQuery("SELECT * FROM  Task where id='"+attachtoTaskid+"' ",null);
					while(cur_tenan.moveToNext())
					{
						spnTenant.setSelection(osListten.indexOf(cur_tenan.getString(11)));
					}
				}
				else
				{

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
    	*/
		taskattachment(attachtoTaskid);

    	String maintonewtask=getIntent().getStringExtra("selectedBuilding"); 
    	//taskAdd();
    	if(maintonewtask==null)
    		maintonewtask="";
    	
    	if(!maintonewtask.equals(""))
    	{
    		taskAdd();
    	}

    	btnTaskDelete.setVisibility(View.GONE);
		
    	setListViewHeightBasedOnChildren(lvTaskPhoto);      //dynamic listview for photo
		lvTaskPhoto.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
			}
	    });
    	setListViewHeightBasedOnChildren(lvTaskAttach);       //dynamic listview for attachment
    	lvTaskAttach.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
			}
	    });

		lvTaskPhoto.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				TextView txtPhotoId = (TextView) view.findViewById(R.id.txtPhotoId);

			    ph_id = Integer.parseInt(txtPhotoId.getText().toString());
			    
	    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("This cutomer will be deleted");

				// set dialog message
				alertDialogBuilder
						.setMessage("Click Yes to Delete")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										
										SplashActivity.mydb.delete("Photo", "id" + "="
												+ ph_id, null);

										//dbcon.deleteData(member_id);
										this.returnHome();
									}

									private void returnHome() {
										Intent home_intent = new Intent(getApplicationContext(),
												TaskActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										home_intent.putExtra("attachtoTask", tk_id);

										startActivity(home_intent);
										
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();



			}
		});	
		lvTaskAttach.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				TextView txtAttachId = (TextView) view.findViewById(R.id.txtadpId);

			    at_id = Integer.parseInt(txtAttachId.getText().toString());
			    
	    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("This cutomer will be deleted");

				// set dialog message
				alertDialogBuilder
						.setMessage("Click Yes to Delete")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										
										SplashActivity.mydb.delete("Attachment", "id" + "="
												+ at_id, null);

										//dbcon.deleteData(member_id);
										this.returnHome();
									}

									private void returnHome() {
										Intent home_intent = new Intent(getApplicationContext(),
												TaskActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										home_intent.putExtra("attachtoTask", tk_id);

										startActivity(home_intent);
										
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			}
		});		

	}
	@Override
	protected void onStop() {
	    super.onStop();

	    	deleted_id="";
	    	deleted_id=nullCheck();
	}
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			 	String deleted_id="";
			    deleted_id=nullCheck();
			    if(deleted_id.equals("not_null_record"))
			    	deleted_id=null;
		 		SplashActivity.mydb.delete("Attachment", "task_id" + "=" + deleted_id, null);
		 		SplashActivity.mydb.delete("Photo", "task_id" + "=" + deleted_id, null);
		 		SplashActivity.mydb.delete("Task", "id" + "=" + deleted_id, null);
	
		 }
		 
		 return super.onKeyDown(keyCode, event);
	 }
	private String nullCheck()
	{
		deleted_id="not_null_record";
		Cursor deleted_task = SplashActivity.mydb.rawQuery("SELECT id FROM Task WHERE taskHeading=''",null);
		while(deleted_task.moveToNext())
		{
			deleted_id = deleted_task.getString(0);
		}
		return deleted_id;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnTaskAttachAdd:
			    taskUpdateforattachment();
	    		deleteFlag++;

	    		Intent attachIntent = new Intent(this, AttachmentActivity.class);
				attachIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(attachIntent);
				break;
			case R.id.btnTaskPhotoAdd:
				deleteFlag++;
				 try {				 
			            PackageManager packageManager = getPackageManager();
			            boolean doesHaveCamera = packageManager
			                    .hasSystemFeature(PackageManager.FEATURE_CAMERA);

			            if (doesHaveCamera) {
			                // start the image capture Intent
			                Intent intent = new Intent(
			                        MediaStore.ACTION_IMAGE_CAPTURE);
			                // Get our fileURI
			                //fileUri = getOutputMediaFile();

			                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			                startActivityForResult(intent, 100);
			            }
			        } catch (Exception ex) {
			            Toast.makeText(getApplicationContext(),
			                    "There was an error with the camera.",
			                    Toast.LENGTH_LONG).show();
			        }

				break;
			case R.id.btnTaskSave:
				if(spnTenant.getSelectedItem().toString().equals("Select Tenant"))
				{
					Toast.makeText(getBaseContext(),"Tenant data should not blank",Toast.LENGTH_LONG).show();
				}
				else
					taskUpdate();
				break;
			case R.id.btnTaskAdd:
				timeStampUpdate();
				break;
			default:
				break;
		
		}

	}
	private void taskAdd() {
		// TODO Auto-generated method stub
		String taskheading,building_id,tenant_id,user_id,tasktype_id,tasknote;
		taskheading = etTaskHeading.getText().toString().trim();
		tasknote = etTaskNote.getText().toString().trim();
		//spnTenSelecteditem=spnTenant.getSelectedItem().toString();

		building_id="";
		Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem +"'",null);
		while(cursor_buildingid.moveToNext())
		{
			building_id = cursor_buildingid.getString(5);
		}

		tenant_id="";
		Cursor cursor_tenantid = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenName='"+ spnTenSelecteditem +"'",null);
		while(cursor_tenantid.moveToNext())
		{
			tenant_id = cursor_tenantid.getString(7);
		}
		
		user_id="";
		Cursor cursor_userid = SplashActivity.mydb.rawQuery("select sysUser_id,user_id,userId from User u inner join Login l on u.user_id=l.userId",null);
		while(cursor_userid.moveToNext())
		{
			user_id = cursor_userid.getString(0);
		}

		String dvalue="";
		cur_date_time = new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
	    try {
	        c.setTime(sdf.parse(cur_date_time));
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
	    
	    String deadline = sdf.format(c.getTime()); 
	    
	        SplashActivity.mydb.execSQL( "insert into Task"
					+ " (taskHeading,taskNote,taskDeadline,taskSlip,taskStarttimestamp,taskEndtimestamp,taskDuration,taskDurationRounded,building_Id,tenant_Id,user_Id,taskCreateDate)"
	     			+ " VALUES ('"+taskheading+"','"+tasknote+"','"+deadline+"','"+dvalue+"','"+starttimestamp+"','"+endtimestamp+"','"+dvalue+"','"+dvalue+"','"+building_id+"','"+tenant_id+"','"+user_id+"','"+cur_date_time+"')");

	    String task_id="";
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task ORDER BY id DESC LIMIT 1",null);
		int intAttachid=1;
		while(cursor_task_id.moveToNext())
		{
			intAttachid=Integer.parseInt(cursor_task_id.getString(0));
		}
		task_id =String.valueOf(intAttachid);
		tk_id =	task_id;

	}
	private void taskUpdateforattachment() {
		// TODO Auto-generated method stub

		deleteFlag++;
		String tkheading = etTaskHeading.getText().toString();
		String tknote = etTaskNote.getText().toString();
		if(!tkheading.equals(""))
		{
			String update_id="";
			Cursor update_task = SplashActivity.mydb.rawQuery("SELECT MAX(id) FROM Task ORDER BY id DESC LIMIT 1",null);
			while(update_task.moveToNext())
			{
				update_id = update_task.getString(0);
			}

			String buil_id="";
			spnSelcteditem=spnBuilding.getSelectedItem().toString();
			Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem +"'",null);
			while(cursor_buildingid.moveToNext())
			{
				buil_id = cursor_buildingid.getString(5);
			}

			String ten_id="";
			spnTenSelecteditem=spnTenant.getSelectedItem().toString();
			Cursor cursor_tenantid = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenName='"+ spnTenSelecteditem +"'",null);
			while(cursor_tenantid.moveToNext())
			{
				ten_id = cursor_tenantid.getString(7);
			}

	     	SplashActivity.mydb.execSQL("update Task set"
					+ " taskHeading='"+tkheading+"',taskNote='"+tknote+"',building_Id='" + buil_id + "', tenant_Id='"+ten_id+"' WHERE id='"+update_id+"'");
	     	
		}
	}

	private void taskUpdate() {
		// TODO Auto-generated method stub

		String tkheading = etTaskHeading.getText().toString();
		String tknote = etTaskNote.getText().toString();
		if(!tkheading.equals(""))
		{
			String update_id="";
			Cursor update_task = SplashActivity.mydb.rawQuery("SELECT MAX(id) FROM Task ORDER BY id DESC LIMIT 1",null);
			while(update_task.moveToNext())
			{
				update_id = update_task.getString(0);
			}

			String building_id="";
			spnSelcteditem=spnBuilding.getSelectedItem().toString();
			Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem +"'",null);
			while(cursor_buildingid.moveToNext())
			{
				building_id = cursor_buildingid.getString(5);
			}

			String ten_id="";
			spnTenSelecteditem=spnTenant.getSelectedItem().toString();
			Cursor cursor_tenantid = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenName='"+ spnTenSelecteditem +"'",null);
			while(cursor_tenantid.moveToNext())
			{
				ten_id = cursor_tenantid.getString(7);
			}
			
			String tasktype_id="";
			Cursor cursor_tasktypeid = SplashActivity.mydb.rawQuery("SELECT taskType_id  FROM  Building b inner join Customer c on b.customer_id=c.customer_id  where building_id='"+building_id+"'",null);
			while(cursor_tasktypeid.moveToNext())
			{
				tasktype_id = cursor_tasktypeid.getString(0);
			}
				

	     	SplashActivity.mydb.execSQL("update Task set"
					+ " taskHeading='"+tkheading+"',taskNote='"+tknote+"',tasktype_Id='"+tasktype_id+"',building_Id='" + building_id + "', tenant_Id='"+ten_id+"' WHERE id='"+update_id+"'");
	     	
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
		}
	}
	private void timeStampUpdate() {
		// TODO Auto-generated method stub
		String strTask_id="";
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task ORDER BY id DESC LIMIT 1",null);
		int intTaskid=1;
		while(cursor_task_id.moveToNext())
		{
			intTaskid=Integer.parseInt(cursor_task_id.getString(0));
		}
		strTask_id =String.valueOf(intTaskid);
		

		if(btnTaskAdd.getText().equals("Start"))
		{
			cur_date_time = new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());
			starttimestamp=cur_date_time;
			SplashActivity.mydb.execSQL("update Task set taskStarttimestamp='"+starttimestamp+"' where id='"+strTask_id+"'");
			btnTaskAdd.setText("Finish");
		}
		else
		{
			cur_date_time = new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());
			endtimestamp=cur_date_time;
			SplashActivity.mydb.execSQL("update Task set taskEndtimestamp='"+endtimestamp+"' where id='"+strTask_id+"'");
		}
	    
		
	}
	protected void onActivityResult(int requestCode, int resultCode,Intent intent)

    {
        if (requestCode == 100)
         {
            if (resultCode == RESULT_OK)
            {
                if (intent == null)
                {
                    // The picture was taken but not returned
                    Toast.makeText(
                            getApplicationContext(),
                            "The picture was taken and is located here: "
                                    + fileUri.toString(), Toast.LENGTH_LONG)
                            .show();
                }
                else
                {                    	
                    	try
                    	{               
	                    	 // The picture was returned
		                     Bundle extras = intent.getExtras();
		                     img1=(Bitmap) extras.get("data");
		                     ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);
		                     imageView1.setImageBitmap((Bitmap) extras.get("data"));
           					 addPhoto();
           					 setListViewHeightBasedOnChildren(lvTaskPhoto); //dynamic listview

                    	}
                    	catch(Exception e)
                    	{ 
                    		Toast.makeText(getApplicationContext(),"Please Take image: "
	                                    + fileUri.toString(), Toast.LENGTH_LONG).show();
                    	}
                    	
                    	
                    
                }
            }
        }
    }
	
	private void addPhoto() {
		// TODO Auto-generated method stub

		deleteFlag++;
		String task_id="";
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task ORDER BY id DESC LIMIT 1",null);
		int intAttachid=1;
		while(cursor_task_id.moveToNext())
		{
			intAttachid=Integer.parseInt(cursor_task_id.getString(0));
			//task_id=cursor_task_id.getString(14);
		}
		task_id =String.valueOf(intAttachid);

		cv = new ContentValues();
		cv.put("task_id",task_id);
		byte[] data = getBitmapAsByteArray(img1);
		cv.put("picture", data);
		SplashActivity.mydb.insert("Photo", null, cv);
		
		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();

		listPhoto(task_id);
	}
	public static byte[] getBitmapAsByteArray(Bitmap bitmap)
	{
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 0, outputStream);
	    return outputStream.toByteArray();
	    
	}
	
	private void listPhoto(String task_id2) {
		// TODO Auto-generated method stub
		arrList.clear();

		List<GetListView> taskList = new ArrayList<GetListView>();
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT * FROM  Photo where task_id='"+ task_id2 +"' and attach_id is null  ",null);
		 // looping through all rows and adding to list
		GetListView contact;
			 while (cursor_Task.moveToNext()) 
			 {
			 contact = new GetListView();
			 contact.setPhotoid(Integer.parseInt(cursor_Task.getString(0)));
			 contact.setImage(cursor_Task.getBlob(1));
			 // Adding contact to list
			 taskList.add(contact);
			 } 
			for (GetListView cn : taskList) {
				String log = "ID:" + cn.getPhotoid() + " Photo: " + cn.getImage();

				// Writing Contacts to log
				Log.d("Result: ", log);
				//add contacts data in arrayList
				arrList.add(cn);

				}
				adapter = new ArrayListAdapterPhoto(this, R.layout.photo_list,arrList);			
				lvTaskPhoto.setAdapter(adapter);

	}
	private void taskattachment(String attachtoTaskid2) {
		// TODO Auto-generated method stub
		if(attachtoTaskid2 != null)
		{
			attarrList.clear();
			List<GetListView> taskList = new ArrayList<GetListView>();
			Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT * FROM  Attachment where task_id='"+ attachtoTaskid2 +"' order by invoiceId desc ",null);
			 // looping through all rows and adding to list
			GetListView contact;
				 while (cursor_Task.moveToNext()) 
				 {
				 contact = new GetListView();
				 contact.setattachID(Integer.parseInt(cursor_Task.getString(0)));
				 contact.setattachInvoiceid(cursor_Task.getString(1));
				 contact.setattachName(cursor_Task.getString(2));
				 contact.setattachNote(cursor_Task.getString(3));
				 // Adding contact to list
				 taskList.add(contact);
				 } 
				for (GetListView cn : taskList) {
					String log = "ID:" + cn.getattachID() + " Invoice Id: " + cn.getattachInvoiceid()
					+ ", Name: " + cn.getattachName() + " Task Note: " + cn.getattachNote();

					// Writing Contacts to log
					Log.d("Result: ", log);
					//add contacts data in arrayList
					attarrList.add(cn);

					}
				    attadapter = new ArrayAdapterInfoList(this, R.layout.screen_list,attarrList);			
					lvTaskAttach.setAdapter(attadapter);
					listPhoto(attachtoTaskid2);
					taskBasicinfo(attachtoTaskid2);
		}

	}
	private void taskBasicinfo(String attachtoTaskid2) {
		// TODO Auto-generated method stub

		String taskheading="",tasknote="",starttimestmp="";
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task WHERE id='"+attachtoTaskid2+"'",null);
		while(cursor_task_id.moveToNext())
		{
			taskheading=cursor_task_id.getString(1);
			tasknote=cursor_task_id.getString(2);
			starttimestmp=cursor_task_id.getString(5);
			
			etTaskHeading.setText(taskheading);
			etTaskNote.setText(tasknote);
			
			String building_id="";
			building_id=cursor_task_id.getString(10);
			Cursor cur_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where building_id='"+building_id+"' ",null);
			while(cur_building.moveToNext())
			{
				osList.add(cur_building.getString(1));
				arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
				spnBuilding.setAdapter(arradapter);		
				spnBuilding.setSelection(osList.indexOf(cur_building.getString(1)));
			}	
			
			osListten.add("Select Tenant");
			Cursor cursor_tenant = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where building_id='"+building_id+"'  order by tenName",null);
			while(cursor_tenant.moveToNext())
			{
				osListten.add(cursor_tenant.getString(1));
			}
			arradapterten = new ArrayAdapter<String>(this,R.layout.spinner_item,osListten);
			spnTenant.setAdapter(arradapterten);
			
			String tenant_id="";
			tenant_id=cursor_task_id.getString(11);
			Cursor cur_ten = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenant_id='"+tenant_id+"' ",null);
			while(cur_ten.moveToNext())
			{
				spnTenant.setSelection(osListten.indexOf(cur_ten.getString(1)));
			}

			if(!starttimestmp.equals(""))
			{
				btnTaskAdd.setText("Finish");
			}
			else
			{
				btnTaskAdd.setText("Start");
				
			}
		}

	}
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    int listnoFlag=1;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	        if(listnoFlag==3)
	        	break;
	        listnoFlag++;
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	}
	public boolean isConnected()
	{
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) 
            return true;
        else
            return false;   
    }
	public class ExportTaskOnline extends AsyncTask<String, Void, Boolean> {
	   private ProgressDialog pDialog;
    	
		 @Override
	   protected void onPreExecute() {
	         super.onPreExecute();
	         
	         pDialog = new ProgressDialog(TaskActivity.this);
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
						"select * from Task ORDER BY id DESC LIMIT 1", null);

				JSONObject new_member = new JSONObject();
				
				while (Online.moveToNext()) {

					 Cursor curPhoto = SplashActivity.mydb.rawQuery("select * from Photo WHERE task_id='"+Online.getString(0)+"' and attach_id is null",null);

					 JSONArray photoList = new JSONArray();
						
					 if (curPhoto != null) {
						 
						 while (curPhoto.moveToNext()) {
							 
							 JSONObject objPhoto = new JSONObject(); 
							 
							 byte[] picture = curPhoto.getBlob(curPhoto.getColumnIndex("picture"));
							 String image = Base64.encodeToString(picture, Base64.DEFAULT);
							 
							 objPhoto.put("taskImg",image);
							 //objPhoto.put("task_Id","C34C8AC4-28D7-4BAF-BE38-0844B45210A9");
					 
							 photoList.put(objPhoto);
						 
						 }
					 }
					 
					 Cursor curattach = SplashActivity.mydb.rawQuery("select * from Attachment WHERE task_id='"+Online.getString(0)+"' ",null);

					 JSONArray attachList = new JSONArray();
						
					 if (curattach != null) {
						 
						 while (curattach.moveToNext()) {
							 
							 JSONObject objAttach = new JSONObject(); 
							 
							 objAttach.put("attachmentID", curattach.getString(1));
							 objAttach.put("attachmentName", Uri.encode(curattach.getString(2)));
							 objAttach.put("attachmentNote", Uri.encode(curattach.getString(3)));
							 objAttach.put("attachmentCost", curattach.getString(6));
							 //objPhoto.put("task_Id","C34C8AC4-28D7-4BAF-BE38-0844B45210A9");
					 
							 Cursor curPhotoattach = SplashActivity.mydb.rawQuery("select * from Photo WHERE task_id='"+Online.getString(0)+"' and attach_id='"+curattach.getString(0)+"'",null);

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
					new_member.put("heading", Uri.encode(Online.getString(1)));
					new_member.put("note", Uri.encode(Online.getString(2)));
					new_member.put("slip", "2");
					new_member.put("tasktype_id", Online.getString(9));
					new_member.put("building_id", Online.getString(10));
					new_member.put("tenant_id", Online.getString(11));
					new_member.put("user_id", Online.getString(12));
					new_member.put("userJanitor_id", Online.getString(12));

					SimpleDateFormat format = new SimpleDateFormat("Z");
					Date date = new Date();
					new_member.put("taskCreateDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a"); 
					String ssdate=Online.getString(5);
					String sedate=Online.getString(6);
					if(ssdate != null && !ssdate.isEmpty() && !ssdate.equals("null"))
					{
						Date sdate = dateFormat.parse(Online.getString(5));
						new_member.put("startTimeStamp", "/Date(" + String.valueOf(sdate.getTime()) + format.format(sdate) + ")/");
					}
					if(sedate != null && !sedate.isEmpty() && !sedate.equals("null"))
					{
						Date edate = dateFormat.parse(Online.getString(6));
						new_member.put("endTimeStamp", "/Date(" + String.valueOf(edate.getTime()) + format.format(edate) + ")/");
					}
					Date deadline=dateFormat.parse(Online.getString(3));
					new_member.put("deadline", "/Date(" + String.valueOf(deadline.getTime()) + format.format(deadline) + ")/");
					
					Array.put(new_member);
				}
				parrent.put("Tasks", Array);

				Cursor cursor_login = SplashActivity.mydb.rawQuery("SELECT * FROM  Login ",null);
				String server="",service="",userid="",pass="";
				
				if (cursor_login != null) {
					cursor_login.moveToLast();
					try{
						
						 server=cursor_login.getString(1);
			    		 service=cursor_login.getString(2);
			    		 userid=cursor_login.getString(3);
			    		 pass=cursor_login.getString(4);
			    		 pass=Uri.encode(pass);
						//UserId= cursor_login.getString(3);
						//Pass=cursor_login.getString(4);
					
					}catch(Exception e){
						
						
					}
					//cursor_login.close();
					
				}
				parrent.put("userId",userid );
				parrent.put("userPass", pass);

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
						  
						  Toast.makeText(TaskActivity.this, "Export failed",Toast.LENGTH_SHORT).show();
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
				Toast.makeText(TaskActivity.this, "Export successful!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TaskActivity.this, "Export failed",
						Toast.LENGTH_SHORT).show();
				
				 //mydb.execSQL("DELETE FROM " + TABLE);
				
			}
		}
	}
	private void copyDatabase() {
		// TODO Auto-generated method stub
		Log.e("Databasehealper", "********************************");
		try {
			File f1 = new File("/data/data/com.initvent.janitorsystem/databases/tms.db");
			if (f1.exists()) {

				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/SaveValue.db");
				f2.getParentFile().mkdirs();
				f2.createNewFile();
				InputStream in = new FileInputStream(f1);
				OutputStream out = new FileOutputStream(f2);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		Log.e("Databasehealper", "********************************");

	}

}
