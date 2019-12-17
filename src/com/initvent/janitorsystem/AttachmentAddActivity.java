package com.initvent.janitorsystem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AttachmentAddActivity extends Activity implements OnClickListener{
	
	EditText etInvoiceId,etAttachName,etAttachNote,etAttachCost;
	Button btnAttachAddPhoto,btnAttachSave,btnAttachDelete;
	ListView lvAttachPhoto;
	
	ImageView imageView1;
	
	private Uri fileUri;
	ContentValues cv;
	Bitmap img1,rbitmap;
	
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayListAdapterPhoto adapter;
	static String task_id="";
	
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
		lvAttachPhoto=(ListView)findViewById(R.id.lvAttachPhoto);

		imageView1=(ImageView)findViewById(R.id.imageView1);

		btnAttachDelete.setVisibility(View.GONE);
		btnAttachAddPhoto.setVisibility(View.GONE);
		imageView1.setVisibility(View.GONE);

		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnAttachSave.setWidth(width/2);
	    
		btnAttachSave.setOnClickListener(this);
		btnAttachAddPhoto.setOnClickListener(this);
		
		task_id=getIntent().getStringExtra("sel_taskID"); 
		
		setListViewHeightBasedOnChildren(lvAttachPhoto);            //Dynamic listview for photo 
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

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btnAttachSave:
			insertAttachment();
			Intent attachtotask = new Intent(this, UpdateDeleteTask.class);
			attachtotask.putExtra("chng_taskID", task_id);
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
		                     addPhoto();
		             		 setListViewHeightBasedOnChildren(lvAttachPhoto);            //Dynamic listview for photo 

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
	private void addPhoto() {
		// TODO Auto-generated method stub
		String attach_id="";
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
			
			byte[] data = getBitmapAsByteArray(rbitmap);
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
