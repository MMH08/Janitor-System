package com.initvent.janitorsystem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AttachmentActivity extends Activity implements OnClickListener{
	
	EditText etInvoiceId,etAttachName,etAttachNote,etAttachCost;
	Button btnAttachAddPhoto,btnAttachSave,btnAttachDelete;
	ListView lvAttachPhoto;
	
	private Uri fileUri;
	ContentValues cv;
	Bitmap img1;
	
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayListAdapterPhoto adapter;
	static String task_id="";
	String attach_id="";
	
	final Context context = this;
	int ph_id=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attachment);
		
		etInvoiceId=(EditText)findViewById(R.id.etInvoiceId);
		etAttachName=(EditText)findViewById(R.id.etAttachName);
		etAttachNote=(EditText)findViewById(R.id.etAttachNote);
		etAttachCost=(EditText)findViewById(R.id.etAttachmentCost);
		
		etInvoiceId.setEnabled(false);
		etInvoiceId.setClickable(false);
		
		btnAttachSave=(Button)findViewById(R.id.btnAttachSave);
		btnAttachAddPhoto=(Button)findViewById(R.id.btnAttachAddPhoto);
		btnAttachDelete=(Button)findViewById(R.id.btnAttachDelete);
		
		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnAttachSave.setWidth(width/2);
	    btnAttachAddPhoto.setWidth(width/2);
	    
	    btnAttachDelete.setVisibility(View.GONE);
	    
		lvAttachPhoto=(ListView)findViewById(R.id.lvAttachPhoto);
		
		btnAttachSave.setOnClickListener(this);
		btnAttachAddPhoto.setOnClickListener(this);
		
    	setListViewHeightBasedOnChildren(lvAttachPhoto);      //dynamic listview for photo
    	lvAttachPhoto.setOnTouchListener(new ListView.OnTouchListener() {
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

		lvAttachPhoto.setOnItemClickListener(new OnItemClickListener() {
			
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
												AttachmentActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										home_intent.putExtra("attachtoTask", attach_id);

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btnAttachSave:
			insertAttachment();
			Intent attachtotask = new Intent(this, TaskActivity.class);
			attachtotask.putExtra("attachtoTask", task_id);
			attachtotask.putExtra("selectedBuilding", "");
			attachtotask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(attachtotask);
			break;
		case R.id.btnAttachAddPhoto:
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

		default:
			break;
		}
	}
	private void insertAttachment() {
		// TODO Auto-generated method stub
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task ORDER BY id DESC LIMIT 1",null);
		int intTaskid=1;
		while(cursor_task_id.moveToNext())
		{
			intTaskid=Integer.parseInt(cursor_task_id.getString(0));
			//task_id=cursor_task_id.getString(14);
		}
		task_id =String.valueOf(intTaskid);

		Cursor cursor_attach_id = SplashActivity.mydb.rawQuery("SELECT Count(*) attid FROM  Attachment where task_id='"+task_id+"'",null);
		String invoiceId="1";
		while(cursor_attach_id.moveToNext())
		{
			int intattachid=0;
    		intattachid=Integer.parseInt(cursor_attach_id.getString(0))+1;
			
			invoiceId="Attachment_"+String.valueOf(intattachid);
		}
		
		//String invoiceId = etInvoiceId.getText().toString().trim();
		String attachName = etAttachName.getText().toString().trim();
		String attachNote = etAttachNote.getText().toString().trim();
		String attachCost = etAttachCost.getText().toString().trim();

		SplashActivity.mydb.execSQL("insert into Attachment(invoiceId,attachName,attachNote,task_id,attachmentCost)"
				+" Values('" + invoiceId + "','" + attachName + "','" + attachNote + "','" + task_id + "','" + attachCost + "')");

		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();

	}
	private void addPhoto() {
		// TODO Auto-generated method stub
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Task ORDER BY id DESC LIMIT 1",null);
		int intTaskid=1;
		while(cursor_task_id.moveToNext())
		{
			intTaskid=Integer.parseInt(cursor_task_id.getString(0));
		}
		task_id =String.valueOf(intTaskid);
		
		Cursor cursor_attach_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Attachment ORDER BY id DESC LIMIT 1",null);
		int intAttachid=1;
		while(cursor_attach_id.moveToNext())
		{
			intAttachid=Integer.parseInt(cursor_attach_id.getString(0))+1;
		}
			attach_id =String.valueOf(intAttachid);
			
			cv = new ContentValues();
			//String name = etName.getText().toString();
			//String description = etDescription.getText().toString();
			cv.put("task_id",task_id);
			
			cv.put("attach_id",attach_id);
			//cv.put(MEMBER_Description, description);
			
			byte[] data = getBitmapAsByteArray(img1);
			cv.put("picture", data);
			SplashActivity.mydb.insert("Photo", null, cv);
			
			Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();

			listPhoto(attach_id);
		//}

	}
	
	public static byte[] getBitmapAsByteArray(Bitmap bitmap)
	 {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 0, outputStream);
	    return outputStream.toByteArray();
	    
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
 		                 	 setListViewHeightBasedOnChildren(lvAttachPhoto);      //dynamic listview for photo

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
	
	private void listPhoto(String attach_id2) {
		// TODO Auto-generated method stub
		arrList.clear();

		List<GetListView> taskList = new ArrayList<GetListView>();
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT * FROM  Photo where attach_id='"+ attach_id2 +"' ",null);
		 // looping through all rows and adding to list
		GetListView contact;
		 //if (cursor_Task.moveToFirst()) {
			 while (cursor_Task.moveToNext()) 
			 {
			 contact = new GetListView();
			 contact.setattachPhotoid(Integer.parseInt(cursor_Task.getString(0)));
			 contact.setattachImage(cursor_Task.getBlob(1));
			 // Adding contact to list
			 taskList.add(contact);
			 } 
		 //}
			for (GetListView cn : taskList) {
				String log = "ID:" + cn.getattachPhotoid() + " Photo: " + cn.getattachImage();

				// Writing Contacts to log
				Log.d("Result: ", log);
				//add contacts data in arrayList
				arrList.add(cn);

				}
				adapter = new ArrayListAdapterPhoto(this, R.layout.photo_list,arrList);			
				lvAttachPhoto.setAdapter(adapter);

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

}
