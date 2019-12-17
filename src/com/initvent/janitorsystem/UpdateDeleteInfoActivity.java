package com.initvent.janitorsystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UpdateDeleteInfoActivity extends Activity implements OnClickListener {
	
	String memberId,info_guid;
	
	EditText etInfoNote;
	Spinner spnBuilding;
	Button btnSave,btnDelete;
	final Context context = this;
	private ArrayAdapter<String> arradapter;
	List<String> osList=new ArrayList<String>(); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		etInfoNote=(EditText)findViewById(R.id.etNote);
		spnBuilding=(Spinner)findViewById(R.id.spnBuilding);
		btnSave=(Button)findViewById(R.id.btnAdd);
		btnDelete=(Button)findViewById(R.id.btnDelete);
		btnSave.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		
		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnSave.setWidth(width/2);
	    btnDelete.setWidth(width/2);

	    btnDelete.setVisibility(View.VISIBLE);

		Intent i = getIntent();
		memberId = i.getStringExtra("chng_taskID");//Only Id from table
		
		Cursor cursor_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building ORDER BY buildingname ",null);
		while(cursor_building.moveToNext())
		{
			osList.add(cursor_building.getString(1));
		}		
		arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
		spnBuilding.setAdapter(arradapter);		
		
		Cursor cursor_info_id = SplashActivity.mydb.rawQuery("SELECT * FROM info WHERE id='"+memberId+"'",null);
		
		while(cursor_info_id.moveToNext())
		{
			info_guid=cursor_info_id.getString(4);
			etInfoNote.setText(cursor_info_id.getString(2));
			
			String building_id="";
			building_id=cursor_info_id.getString(3);
			Cursor cur_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where building_id='"+building_id+"' ",null);
			while(cur_building.moveToNext())
			{
				spnBuilding.setSelection(osList.indexOf(cur_building.getString(1)));
			}	
			//spnBuilding.setSelection(osList.indexOf(cursor_info_id.getString(1)));
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
	    case R.id.btnAdd:
	    	if(isConnected()){
	     		new ExportInfoOnline().execute(); //task export to server
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
	    case R.id.btnDelete:
	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			// set title
			alertDialogBuilder.setTitle("This info will be deleted");

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
							     		new DeleteInfoOnline().execute(); //task export to server
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
		}
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
	
	public class ExportInfoOnline extends AsyncTask<String, Void, Boolean> {
		//private final ProgressDialog dialog = new ProgressDialog(InfoActivity.this);
	     private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			//this.dialog.setMessage("Please wait...");
			//this.dialog.setTitle("Exporting Data To Online");
			//this.dialog.show();
	           super.onPreExecute();
	           
	           pDialog = new ProgressDialog(UpdateDeleteInfoActivity.this);
	           pDialog.setMessage("Getting Data ...");
	           pDialog.setIndeterminate(false);
	           pDialog.setCancelable(true);
	           pDialog.show();

		}

		protected Boolean doInBackground(final String... args) {

			try {

				JSONObject parrent = new JSONObject();
				JSONArray Array = new JSONArray();

				Cursor Online = SplashActivity.mydb.rawQuery(
				"select * from info where info_id='"+info_guid+"'", null);

				JSONObject new_member = new JSONObject();
				
				while (Online.moveToNext()) {
					SimpleDateFormat format = new SimpleDateFormat("Z");
					SimpleDateFormat dformat=new SimpleDateFormat( "yyyy-MM-dd hh:mm a");
					//Date date = new Date();	
					Date date = dformat.parse(Online.getString(1));						
					new_member.put("createDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");
					new_member.put("note", Uri.encode(etInfoNote.getText().toString()));
					
					String building_id="";
					Cursor cur_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+spnBuilding.getSelectedItem().toString()+"' ",null);
					while(cur_building.moveToNext())
					{
						building_id=cur_building.getString(5);
					}	
					
					new_member.put("building_id", building_id);
					new_member.put("id", info_guid);
					
					//Array.put(new_member);
				}
				parrent.put("infos", new_member);

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
				parrent.put("info_id", info_guid);
				parrent.put("isDelete", 0);

				String URL = "http://"+server+"/"+service+"/TMSService.svc/InfoUpdateDeleteForAndroid";
				
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
						  
						  Toast.makeText(UpdateDeleteInfoActivity.this, "Export failed",Toast.LENGTH_SHORT).show();
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
			//if (this.dialog.isShowing()) {
			//	this.dialog.dismiss();
			//}
			
			//if (this.pDialog.isShowing()) {
			//	this.pDialog.dismiss();
			//}
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
				Toast.makeText(UpdateDeleteInfoActivity.this, "Export successful!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(UpdateDeleteInfoActivity.this, "Export failed",
						Toast.LENGTH_SHORT).show();
				
				 //mydb.execSQL("DELETE FROM " + TABLE);
				
			}
		}
	}
	public class DeleteInfoOnline extends AsyncTask<String, Void, Boolean> {
		//private final ProgressDialog dialog = new ProgressDialog(InfoActivity.this);
	     private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			//this.dialog.setMessage("Please wait...");
			//this.dialog.setTitle("Exporting Data To Online");
			//this.dialog.show();
	           super.onPreExecute();
	           
	           pDialog = new ProgressDialog(UpdateDeleteInfoActivity.this);
	           pDialog.setMessage("Getting Data ...");
	           pDialog.setIndeterminate(false);
	           pDialog.setCancelable(true);
	           pDialog.show();

		}

		protected Boolean doInBackground(final String... args) {

			try {

				JSONObject parrent = new JSONObject();
/*				JSONArray Array = new JSONArray();

				Cursor Online = SplashActivity.mydb.rawQuery(
				"select * from info where info_id='"+info_guid+"'", null);

				JSONObject new_member = new JSONObject();
				
				while (Online.moveToNext()) {
					SimpleDateFormat format = new SimpleDateFormat("Z");
					SimpleDateFormat dformat=new SimpleDateFormat( "yyyy-MM-dd hh:mm a");
					//Date date = new Date();	
					Date date = dformat.parse(Online.getString(1));						
					new_member.put("createDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");
					new_member.put("note", etInfoNote.getText().toString());
					
					String building_id="";
					Cursor cur_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+spnBuilding.getSelectedItem().toString()+"' ",null);
					while(cur_building.moveToNext())
					{
						building_id=cur_building.getString(5);
					}	
					
					new_member.put("building_id", building_id);
					new_member.put("id", info_guid);
					
					//Array.put(new_member);
				}
				parrent.put("infos", new_member);
*/
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
				parrent.put("info_id", info_guid);
				parrent.put("isDelete", 1);

				String URL = "http://"+server+"/"+service+"/TMSService.svc/InfoUpdateDeleteForAndroid";
				
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
						  
						  Toast.makeText(UpdateDeleteInfoActivity.this, "Export failed",Toast.LENGTH_SHORT).show();
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
			//if (this.dialog.isShowing()) {
			//	this.dialog.dismiss();
			//}
			
			//if (this.pDialog.isShowing()) {
			//	this.pDialog.dismiss();
			//}
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
				Toast.makeText(UpdateDeleteInfoActivity.this, "Export successful!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(UpdateDeleteInfoActivity.this, "Export failed",
						Toast.LENGTH_SHORT).show();
				
				 //mydb.execSQL("DELETE FROM " + TABLE);
				
			}
		}
	}

}
