package com.initvent.janitorsystem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import android.widget.AdapterView.OnItemSelectedListener;

public class UpdateDeleteTask extends Activity implements OnClickListener{

	static String memberId="";
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayListAdapterPhoto adapter;
	
	ArrayList<GetListView> attarrList = new ArrayList<GetListView>();
	ArrayAdapterInfoList attadapter;	
	ListView lvTaskPhoto,lvTaskAttach;

	Button btnTaskAttachAdd,btnTaskPhotoAdd,btnTaskAdd,btnTaskSave,btnTaskDelete;
	EditText etTaskHeading,etTaskNote;
	Spinner spnBuilding,spnTenant;
	ImageView imageView1;
	
	private ArrayAdapter<String> arradapter;
	List<String> osList=new ArrayList<String>(); 
	private ArrayAdapter<String> arradapterten;
	List<String> osListten=new ArrayList<String>(); 

	private Uri fileUri;
	ContentValues cv;
	Bitmap img1,rbitmap;

	String cur_date_time,starttimestamp="",endtimestamp="";
	
	final Context context = this;
	int ph_id=0,at_id=0;

	String spnSelcteditem="",spnTenSelItem="",task_guid_id="";

	TextView chng_attachID;

	 String server;
	 String service;
	 String userid;
	 String pass;
	 String encode_pass;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		
		lvTaskPhoto=(ListView)findViewById(R.id.lvTaskPhoto);
		lvTaskAttach=(ListView)findViewById(R.id.lvTaskAttach);
		
		btnTaskAttachAdd=(Button)findViewById(R.id.btnTaskAttachAdd);
		btnTaskPhotoAdd=(Button)findViewById(R.id.btnTaskPhotoAdd);
		btnTaskAdd=(Button)findViewById(R.id.btnTaskAdd);
		btnTaskSave=(Button)findViewById(R.id.btnTaskSave);
		btnTaskDelete=(Button)findViewById(R.id.btnTaskDelete);

		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnTaskAttachAdd.setWidth(width/2);
	    btnTaskPhotoAdd.setWidth(width/2);
	    btnTaskAdd.setWidth(width/2);
	    btnTaskSave.setWidth(width/2);
	    btnTaskDelete.setWidth(width/2);

		imageView1=(ImageView)findViewById(R.id.imageView1);

		btnTaskAttachAdd.setVisibility(View.VISIBLE);
		btnTaskPhotoAdd.setVisibility(View.VISIBLE);
		btnTaskAdd.setVisibility(View.VISIBLE);
		lvTaskPhoto.setVisibility(View.VISIBLE);
		lvTaskAttach.setVisibility(View.VISIBLE);
		imageView1.setVisibility(View.VISIBLE);
		btnTaskDelete.setVisibility(View.VISIBLE);

		etTaskHeading=(EditText)findViewById(R.id.etTaskHeading);
		etTaskNote=(EditText)findViewById(R.id.etTaskNote);
		spnBuilding=(Spinner)findViewById(R.id.spnBuilding);
		spnTenant=(Spinner)findViewById(R.id.spnTenant);

		btnTaskAttachAdd.setOnClickListener(this);
		btnTaskPhotoAdd.setOnClickListener(this);
		btnTaskAdd.setOnClickListener(this);
		btnTaskSave.setOnClickListener(this);
		btnTaskDelete.setOnClickListener(this);
		
		Intent i = getIntent();
		memberId = i.getStringExtra("chng_taskID");//Only Id from table
		//TaskActivity lPhoto = new TaskActivity();
		Cursor cursor_task_guid = SplashActivity.mydb.rawQuery("SELECT * FROM Task WHERE id='"+memberId+"'",null);
		while(cursor_task_guid.moveToNext())
		{
			memberId=cursor_task_guid.getString(14);
		}
		listPhoto(memberId);
		taskattachment(memberId);
		taskBasicinfo(memberId);
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
				alertDialogBuilder.setTitle("This Photo will be deleted");

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
												UpdateDeleteTask.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										home_intent.putExtra("chng_taskID", memberId);

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
				chng_attachID = (TextView) view.findViewById(R.id.txtadpId);

			    String memberID_val = chng_attachID.getText().toString();

				Intent modify_intent = new Intent(getApplicationContext(),AttachmentUpdateActivity.class);
				
				modify_intent.putExtra("chng_attachID", memberID_val);
				
				startActivity(modify_intent);
			}
		});		

	}
	private void listPhoto(String task_id2) {
		// TODO Auto-generated method stub
		arrList.clear();

		List<GetListView> taskList = new ArrayList<GetListView>();
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT * FROM  Photo where task_id='"+ task_id2 +"' and attach_id is null ",null);
		 // looping through all rows and adding to list
		GetListView contact;
		 //if (cursor_Task.moveToFirst()) {
			 while (cursor_Task.moveToNext()) 
			 {
			 contact = new GetListView();
			 contact.setPhotoid(Integer.parseInt(cursor_Task.getString(0)));
			 contact.setImage(cursor_Task.getBlob(1));
			 // Adding contact to list
			 taskList.add(contact);
			 } 
		 //}
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
			 //if (cursor_Task.moveToFirst()) {
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
			 //}
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

		}

	}
	@SuppressLint("NewApi")
	private void taskBasicinfo(String memberId) {
		// TODO Auto-generated method stub
		Cursor cursor_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building ORDER BY id ",null);
		while(cursor_building.moveToNext())
		{
			osList.add(cursor_building.getString(1));
		}		
		arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
		spnBuilding.setAdapter(arradapter);		
		
		Cursor cursor_tenant = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant ",null);
		while(cursor_tenant.moveToNext())
		{
			osListten.add(cursor_tenant.getString(1));
		}
		arradapterten = new ArrayAdapter<String>(this,R.layout.spinner_item,osListten);
		spnTenant.setAdapter(arradapterten);

		String taskheading="",tasknote="",starttimestmp="";
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task WHERE task_id='"+memberId+"'",null);
		while(cursor_task_id.moveToNext())
		{
			taskheading=cursor_task_id.getString(1);
			tasknote=cursor_task_id.getString(2);
			starttimestmp=cursor_task_id.getString(5);
			
			etTaskHeading.setText(taskheading);
			etTaskNote.setText(tasknote);

			//if(starttimestmp.equals("null"))
			//	starttimestmp="";
				
			if(starttimestmp != null && !starttimestmp.isEmpty() && !starttimestmp.equals("null"))
			{
				btnTaskAdd.setText("Finish");
			}
			else
			{
				btnTaskAdd.setText("Start");
				
			}
			String building_id="";
			building_id=cursor_task_id.getString(10);
			Cursor cur_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where building_id='"+building_id+"' ",null);
			while(cur_building.moveToNext())
			{
				spnBuilding.setSelection(osList.indexOf(cur_building.getString(1)));
			}	
			
			String tenant_id="";
			tenant_id=cursor_task_id.getString(11);
			Cursor cur_ten = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenant_id='"+tenant_id+"' ",null);
			while(cur_ten.moveToNext())
			{
				spnTenant.setSelection(osListten.indexOf(cur_ten.getString(1)));
			}
			
			//task_user_id=cursor_task_id.getString(12);
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnTaskAttachAdd:
				Intent attachIntent = new Intent(this, AttachmentAddActivity.class);
				attachIntent.putExtra("sel_taskID", memberId);
				attachIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(attachIntent);
				break;
			case R.id.btnTaskPhotoAdd:
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
				taskAdd();
				Intent tasttomain = new Intent(this, MainActivity.class);
				tasttomain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(tasttomain);
				break;
			case R.id.btnTaskDelete:
	    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("This Task will be deleted");

				// set dialog message
				alertDialogBuilder
						.setMessage("Click Yes to Delete")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										
										//SplashActivity.mydb.delete("Task", "id" + "="
										//		+ memberId, null);

										if(isConnected()){
								     		new DeleteTaskOnline().execute(); //task export to server
								     	}
								        else{
								        	Toast.makeText(
													getBaseContext(),
													"Oops!You are not Connected to INTERNET",
													Toast.LENGTH_LONG).show();
								        }
										
										//dbcon.deleteData(member_id);
										this.returnHome();
									}

									private void returnHome() {
										Intent home_intent = new Intent(getApplicationContext(),
												MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		String taskheading,building_id,tasknote,tenant_id;
		taskheading = etTaskHeading.getText().toString().trim();
		tasknote = etTaskNote.getText().toString().trim();

		spnSelcteditem=spnBuilding.getSelectedItem().toString();
		spnTenSelItem=spnTenant.getSelectedItem().toString();
		
		spnBuilding.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				spnSelcteditem=spnBuilding.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		building_id="";
		Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem +"'",null);
		while(cursor_buildingid.moveToNext())
		{
			building_id = cursor_buildingid.getString(5);
		}
		//timeStampUpdate();
		tenant_id="";
		Cursor cursor_tenantid = SplashActivity.mydb.rawQuery("SELECT * FROM  Tenant where tenant_id='"+ spnTenSelItem +"'",null);
		while(cursor_tenantid.moveToNext())
		{
			tenant_id = cursor_tenantid.getString(5);
		}
		String dvalue="";
     	SplashActivity.mydb.execSQL("update Task set"
				+ " taskHeading='"+taskheading+"',taskNote='"+tasknote+"',"
				+"taskDuration='"+dvalue+"',taskDurationRounded='"+dvalue+"',building_Id='"+building_id+"' WHERE task_id='"+memberId+"'");

     	if(isConnected()){
     		new ExportTaskOnline().execute(); //task export to server
     	}
        else{
        	Toast.makeText(
					getBaseContext(),
					"Oops!You are not Connected to INTERNET",
					Toast.LENGTH_LONG).show();
        }	
     	Toast.makeText(getBaseContext(),"Update successfully",Toast.LENGTH_LONG).show();
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
		                     rbitmap=getResizedBitmap(img1, 90, 90);
		                     ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);
		                     imageView1.setImageBitmap(rbitmap);
           					 addPhoto(memberId);
           					 setListViewHeightBasedOnChildren(lvTaskPhoto); //dynamic listview

           					 imageView1.setImageResource(R.id.imageView1);
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
	private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		// TODO Auto-generated method stub
		int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(
	        bm, 0, 0, width, height, matrix, false);
	    bm.recycle();
	    return resizedBitmap;
	}
	private void addPhoto(String taskId) {
		// TODO Auto-generated method stub
			cv = new ContentValues();
			//String name = etName.getText().toString();
			//String description = etDescription.getText().toString();
			
			cv.put("task_id",taskId);
			//cv.put(MEMBER_Description, description);
			
			byte[] data = getBitmapAsByteArray(rbitmap);
			cv.put("picture", data);
			SplashActivity.mydb.insert("Photo", null, cv);
			
			//copyDatabase();
			Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();
		//}

			listPhoto(taskId);
	}
	public static byte[] getBitmapAsByteArray(Bitmap bitmap)
	{
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 0, outputStream);
	    return outputStream.toByteArray();
	    
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
	private void timeStampUpdate() {
		// TODO Auto-generated method stub
		if(btnTaskAdd.getText().equals("Start"))
		{
			cur_date_time = new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());
			starttimestamp=cur_date_time;
			SplashActivity.mydb.execSQL("update Task set taskStarttimestamp='"+starttimestamp+"' where task_id='"+memberId+"'");
			btnTaskAdd.setText("Finish");
			Toast.makeText(getBaseContext(),"Task Started",Toast.LENGTH_LONG).show();
		}
		else
		{
			//building_id="";
			Cursor cursor_taskid = SplashActivity.mydb.rawQuery("SELECT * FROM  Task where task_id='"+ memberId +"'",null);
			while(cursor_taskid.moveToNext())
			{
				starttimestamp = cursor_taskid.getString(5);
			}
			//Comparing dates
            
			cur_date_time = new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());
            endtimestamp=cur_date_time;

			SplashActivity.mydb.execSQL("update Task set taskEndtimestamp='"+endtimestamp+"' where task_id='"+memberId+"'");
			Toast.makeText(getBaseContext(),"Task Ended",Toast.LENGTH_LONG).show();
		}
		
	}

	public class ExportTaskOnline extends AsyncTask<String, Void, Boolean> {
		   private ProgressDialog pDialog;
	    	
			 @Override
		   protected void onPreExecute() {
		         super.onPreExecute();
		         
		         pDialog = new ProgressDialog(UpdateDeleteTask.this);
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
					//"select * from Task where task_id='1'", null);
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
								 objAttach.put("attachmentName", Uri.encode(curattach.getString(2)));
								 objAttach.put("attachmentNote", Uri.encode(curattach.getString(3)));
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
						new_member.put("heading", Uri.encode(Online.getString(1)));
						new_member.put("note", Uri.encode(Online.getString(2)));
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
						if(ssdate != null && !ssdate.isEmpty() && !ssdate.equals("null"))
						{
							Date sdate = dformat.parse(Online.getString(5));
							new_member.put("startTimeStamp", "/Date(" + String.valueOf(sdate.getTime()) + format.format(sdate) + ")/");
						}
						if(sedate != null && !sedate.isEmpty() && !sedate.equals("null"))
						{
							Date edate = dformat.parse(Online.getString(6));
							new_member.put("endTimeStamp", "/Date(" + String.valueOf(edate.getTime()) + format.format(edate) + ")/");
						}

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
					parrent.put("task_id", memberId);

					//parrent.put("userPass", Pass);
					//parrent.put("userId",UserId );
	  			    
					//Cursor lin = SplashActivity.mydb.rawQuery("select * from Login",null);
					//lin.moveToLast();
					//String servername = lin.getString(1).trim();
					//String userid = lin.getString(2).trim();
		
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
							  
							  Toast.makeText(UpdateDeleteTask.this, "Export failed",Toast.LENGTH_SHORT).show();
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
					Toast.makeText(UpdateDeleteTask.this, "Export successful!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(UpdateDeleteTask.this, "Export failed",
							Toast.LENGTH_SHORT).show();
					
					 //mydb.execSQL("DELETE FROM " + TABLE);
					
				}
			}
		}
	
	public class DeleteTaskOnline extends AsyncTask<String, Void, Boolean> {
		   private ProgressDialog pDialog;
	    	
			 @Override
		   protected void onPreExecute() {
		         super.onPreExecute();
		         
		         pDialog = new ProgressDialog(UpdateDeleteTask.this);
		         pDialog.setMessage("Getting Data ...");
		         pDialog.setIndeterminate(false);
		         pDialog.setCancelable(true);
		         //pDialog.show();
		         
		 	}
			@SuppressLint("NewApi")
			protected Boolean doInBackground(final String... args) {

				try {

					JSONObject parrent = new JSONObject();
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
							//UserId= cursor_login.getString(3);
							//Pass=cursor_login.getString(4);
						
						}catch(Exception e){
							
							
						}
						//cursor_login.close();
						
					}
					parrent.put("userId",userid );
					parrent.put("userPass", pass);
					parrent.put("task_id", memberId);

					//parrent.put("userPass", Pass);
					//parrent.put("userId",UserId );
	  			    
					//Cursor lin = SplashActivity.mydb.rawQuery("select * from Login",null);
					//lin.moveToLast();
					//String servername = lin.getString(1).trim();
					//String userid = lin.getString(2).trim();
		
					String URL = "http://"+server+"/"+service+"/TMSService.svc/DeleteTaskForAndroid";
					
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
							  
							  Toast.makeText(UpdateDeleteTask.this, "Export failed",Toast.LENGTH_SHORT).show();
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
					Toast.makeText(UpdateDeleteTask.this, "Delete successful!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(UpdateDeleteTask.this, "Delete failed",
							Toast.LENGTH_SHORT).show();
					
					 //mydb.execSQL("DELETE FROM " + TABLE);
					
				}
			}
		}
}
