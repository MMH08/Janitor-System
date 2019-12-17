package com.initvent.janitorsystem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class InfoActivity extends Activity implements OnClickListener{

	Button btnInfoAdd,btnDelete;
	EditText etInfoNote;
	Spinner spnBuilding;

	private ArrayAdapter<String> arradapter;
	List<String> osList=new ArrayList<String>(); 
	String spnSelcteditem="";
	String building_id, infoNote="", cur_date_time="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		etInfoNote=(EditText)findViewById(R.id.etNote);		
		spnBuilding=(Spinner)findViewById(R.id.spnBuilding);
		
		btnInfoAdd=(Button)findViewById(R.id.btnAdd);
		btnDelete=(Button)findViewById(R.id.btnDelete);
		
		Display display=getWindowManager().getDefaultDisplay();
	    int width=display.getWidth();
	    btnInfoAdd.setWidth(width/2);
	    btnDelete.setWidth(width/2);

	    btnDelete.setVisibility(View.GONE);
	    
		//String[] test={"Test1","Test2"};

		
		if(getIntent().getStringExtra("selectedBuilding").equals(""))
		{
			int flagselbul=1;
			Cursor cursor_building = SplashActivity.mydb.rawQuery("SELECT * FROM  Building order by buildingname",null);
			while(cursor_building.moveToNext())
			{
				if(flagselbul==1)
    				osList.add("Select Building");
				osList.add(cursor_building.getString(1));
				flagselbul++;
			}
			
			arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
			spnBuilding.setAdapter(arradapter);
		}
		else
		{
			osList.add(getIntent().getStringExtra("selectedBuilding"));
			arradapter = new ArrayAdapter<String>(this,R.layout.spinner_item,osList);
			spnBuilding.setAdapter(arradapter);
			spnBuilding.setEnabled(false);
			spnBuilding.setClickable(false);
		}
		btnInfoAdd.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnAdd:
			insertInfo();
			Intent tasttomain = new Intent(this, MainActivity.class);
			tasttomain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(tasttomain);
			break;

		default:
			break;
		}
	}
	private void insertInfo() {
		
		spnSelcteditem=spnBuilding.getSelectedItem().toString();
		
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
		
		Cursor cursor_buildingid = SplashActivity.mydb.rawQuery("SELECT * FROM  Building where buildingname='"+ spnSelcteditem +"'",null);
		while(cursor_buildingid.moveToNext())
		{
			building_id = cursor_buildingid.getString(5);
		}

		// TODO Auto-generated method stub
		infoNote = etInfoNote.getText().toString().trim();
//		cur_date_time=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(new Date());
		cur_date_time=new SimpleDateFormat( "yyyy-MM-dd hh:mm a", Locale.getDefault()).format(new Date());

		SplashActivity.mydb.execSQL("insert into Info(infocreateDate,infoNote,building_id)"
				+" Values('" + cur_date_time + "','" + infoNote + "','" + building_id + "')");

		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();
		
     	if(isConnected()){
     		new ExportInfoOnline().execute(); //task export to server
     	}
        else{
        	Toast.makeText(
					getBaseContext(),
					"Oops!You are not Connected to INTERNET",
					Toast.LENGTH_LONG).show();
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
	           
	           pDialog = new ProgressDialog(InfoActivity.this);
	           pDialog.setMessage("Getting Data ...");
	           pDialog.setIndeterminate(false);
	           pDialog.setCancelable(true);
	           pDialog.show();

		}

		protected Boolean doInBackground(final String... args) {

			try {

				JSONObject parrent = new JSONObject();
				JSONArray Array = new JSONArray();

				JSONObject new_member = new JSONObject();
				SimpleDateFormat format = new SimpleDateFormat("Z");
				Date date = new Date();

				new_member.put("building_id", building_id);
				new_member.put("createDate", "/Date(" + String.valueOf(date.getTime()) + format.format(date) + ")/");
				new_member.put("note", Uri.encode(infoNote));
				Array.put(new_member);
				
				parrent.put("Infos", Array);

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

				String URL = "http://"+server+"/"+service+"/TMSService.svc/InfoForAndroid";
				
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
						  
						  Toast.makeText(InfoActivity.this, "Export failed",Toast.LENGTH_SHORT).show();
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
				Toast.makeText(InfoActivity.this, "Export successful!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(InfoActivity.this, "Export failed",
						Toast.LENGTH_SHORT).show();
				
				 //mydb.execSQL("DELETE FROM " + TABLE);
				
			}
		}
	}

}
