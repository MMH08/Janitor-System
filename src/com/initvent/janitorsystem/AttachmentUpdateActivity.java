package com.initvent.janitorsystem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AttachmentUpdateActivity extends Activity implements android.view.View.OnClickListener{
	
	EditText etInvoiceId,etAttachName,etAttachNote,etAttachCost;
	Button btnAttachAddPhoto,btnAttachSave,btnAttachDelete;
	ListView lvAttachPhoto;
	ImageView imageView1;

	static String memberId="";
	ArrayList<GetListView> arrList = new ArrayList<GetListView>();
	ArrayListAdapterPhoto adapter;
	
	private Uri fileUri;
	ContentValues cv;
	Bitmap img1,rbitmap;

	static String task_id="",attachment_id="";
	
	final Context context = this;
	int ph_id=0,at_id=0;

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

		btnAttachDelete.setVisibility(View.VISIBLE);
		btnAttachAddPhoto.setVisibility(View.VISIBLE);
		imageView1.setVisibility(View.VISIBLE);

		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnAttachSave.setWidth(width/2);
	    btnAttachAddPhoto.setWidth(width/2);
	    btnAttachDelete.setWidth(width/2);
	    
		btnAttachSave.setOnClickListener(this);
		btnAttachAddPhoto.setOnClickListener(this);
		btnAttachDelete.setOnClickListener(this);

		Intent i = getIntent();
		memberId = i.getStringExtra("chng_attachID");//Only Id from table
		
		Cursor cursor_attach_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Attachment WHERE id='"+memberId+"'",null);
		while(cursor_attach_id.moveToNext())
		{
			task_id=cursor_attach_id.getString(4);
			attachment_id=cursor_attach_id.getString(5);
		}

		listPhoto(memberId);
		attachBasicinfo(memberId);
		
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

		lvAttachPhoto.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				TextView txtPhotoId = (TextView) view.findViewById(R.id.txtPhotoId);

			    ph_id = Integer.parseInt(txtPhotoId.getText().toString());
			    
	    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("This photo will be deleted");

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
												AttachmentUpdateActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										home_intent.putExtra("chng_attachID", memberId);

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
	private void listPhoto(String attach_id2) {
		// TODO Auto-generated method stub
		arrList.clear();

		List<GetListView> taskList = new ArrayList<GetListView>();
		Cursor cursor_Task = SplashActivity.mydb.rawQuery("SELECT * FROM  Photo where attach_id='"+ attach_id2 +"'",null);
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
				lvAttachPhoto.setAdapter(adapter);

	}
	private void attachBasicinfo(String attachId2) {
		String attInvId="",attname="",attnote="",attcost;
		Cursor cursor_task_id = SplashActivity.mydb.rawQuery("SELECT * FROM  Attachment WHERE id='"+attachId2+"'",null);
		while(cursor_task_id.moveToNext())
		{
			attInvId=cursor_task_id.getString(1);
			attname=cursor_task_id.getString(2);
			attnote=cursor_task_id.getString(3);
			attcost=cursor_task_id.getString(6);
			
			etInvoiceId.setText(attInvId);
			etAttachName.setText(attname);
			etAttachNote.setText(attnote);
			etAttachCost.setText(attcost);
		}
			
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btnAttachSave:
			updateAttachment();
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

		case R.id.btnAttachDelete:		
		    at_id=Integer.parseInt(memberId);
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			// set title
			alertDialogBuilder.setTitle("This attachment will be deleted");

			// set dialog message
			alertDialogBuilder
					.setMessage("Click Yes to Delete")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									
									SplashActivity.mydb.delete("Photo", "attach_id" + "="
											+ memberId, null);
									SplashActivity.mydb.delete("Attachment", "id" + "="
											+ at_id, null);

									//dbcon.deleteData(member_id);
									this.returnHome();
								}

								private void returnHome() {
									Intent home_intent = new Intent(getApplicationContext(),
											UpdateDeleteTask.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									home_intent.putExtra("chng_taskID", task_id);

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
		default:
			break;
		}

	}
	private void updateAttachment() {
		// TODO Auto-generated method stub
		String attachName = etAttachName.getText().toString().trim();
		String attachNote = etAttachNote.getText().toString().trim();
		String attachCost = etAttachCost.getText().toString().trim();

		SplashActivity.mydb.execSQL("update Attachment set attachName='" + attachName + "',attachNote='" + attachNote + "',task_id='" + task_id + "',attachmentCost='" + attachCost + "'"
				+" where id='"+memberId+"'");

		Toast.makeText(getBaseContext(),"Update successfully",Toast.LENGTH_LONG).show();

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
			attach_id =memberId;
			
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
