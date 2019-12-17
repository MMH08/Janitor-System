package com.initvent.janitorsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	// Email, password edittext
		EditText txtUsername, txtPassword,txtServer,txtService;
		
		// login button
		 Button btnLogin;
		 String server;
		 String service;
		 String userid;
		 String pass;
		 String encode_pass;
		 String centerName;
		// Alert Dialog Manager
		AlertDialogManager alert = new AlertDialogManager();
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		
		 // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.userid);
        txtPassword = (EditText) findViewById(R.id.password); 
        txtServer = (EditText) findViewById(R.id.serverName);
        txtService = (EditText) findViewById(R.id.serviceName); 
        txtUsername.setPadding (15, 5, 5, 5);
        txtService.setPadding (15, 5, 5, 5);
        txtServer.setPadding (15, 5, 5, 5);
        txtPassword.setPadding (15, 5, 5, 5);
        
        Cursor cursor_login = SplashActivity.mydb.rawQuery("SELECT * FROM  Login ",null);

		if (cursor_login != null) {
			cursor_login.moveToLast();
			try{
				
				
				txtServer.setText(cursor_login.getString(1));
				txtService.setText(cursor_login.getString(2));
				txtUsername.setText(cursor_login.getString(3));
				txtPassword.setText(cursor_login.getString(4));
			
			}catch(Exception e){
				
				
			}
			cursor_login.close();
			
		}
        
        // Login button
        btnLogin = (Button) findViewById(R.id.login);
        
        
        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Get username, password from EditText
				
				String Servername = txtServer.getText().toString();
				String ServiceName = txtService.getText().toString();
				String username = txtUsername.getText().toString();
				String password = txtPassword.getText().toString();
				
				// Check if username, password is filled				
				if(username.trim().length() > 0 && password.trim().length() > 0 && Servername.trim().length() > 0 && ServiceName.trim().length() > 0){
				
					/*String server="10.1.15.5";
		    		 String service="pksfsvc";
		    		 String userid="44";
		    		 String pass="Pass@123";
		    		 String encode_pass=Uri.encode(pass);*/
					
					if(isConnected()){
						 server=Servername.trim();
			    		 service=ServiceName.trim();
			    		 userid=username.trim();
			    		 pass=password.trim();
			    		 pass=Uri.encode(pass);

			    		SplashActivity.mydb.execSQL("DELETE FROM Login");
	                 	SplashActivity.mydb.execSQL( "insert into Login"
	            				+ " (Servername,servicename,userId,Password) VALUES ('"+Servername
	            				+"','"+ServiceName+"','"+username+"','"+password+"')");
	                 	
				         //SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); // 0 - for private mode
				         //SharedPreferences.Editor editor = pref.edit();
				         //editor.putString("username", userid);

	                 	//String tenant_url = "http://10.1.15.10/taskManSysSvc/TMSService.svc/Tenants?userId="+ userid +" &userPass="+ pass;				          
	    				 
				         String tenant_url = "http://"+server+"/"+service+"/TMSService.svc/TenantsForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         new Tenant().execute(tenant_url);
				         
				         String building_url = "http://"+server+"/"+service+"/TMSService.svc/BuildingsForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         new Building().execute(building_url);
			    
				         String user_url = "http://"+server+"/"+service+"/TMSService.svc/UserInfoListForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         new User_Info().execute(user_url);
				         
				         String CustomertaskType_url = "http://"+server+"/"+service+"/TMSService.svc/CustomertaskTypeForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         new CustomertaskType().execute(CustomertaskType_url);

				         String info_url = "http://"+server+"/"+service+"/TMSService.svc/InfoForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         //new Building_Info().execute(info_url);

				         String task_url = "http://"+server+"/"+service+"/TMSService.svc/TaskListForAndroid?user_Id="+ userid +"&user_Pass="+ pass;	
				         //new Tasks().execute(task_url);

				         String janitor_url = "http://"+server+"/"+service+"/TMSService.svc/JenitorListForAndroid?user_Id="+ userid +"&user_Pass="+ pass;				          
				         new Janitor().execute(janitor_url);
				         //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				        // SharedPreferences.Editor editor = settings.edit();
				         
					}
			        else
			        {
			        	Toast.makeText(getBaseContext(),"Oops!You are not Connected to INTERNET",Toast.LENGTH_LONG).show();
			        }
				
				}
				else
				{
					alert.showAlertDialog(LoginActivity.this, "Log In Failed..", "Please enter correct query", false);
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
		});

}
	
	private class Tenant extends AsyncTask<String, Void, String> {
		
	     private ProgressDialog pDialog;
		    	
	  	 @Override
	     protected void onPreExecute() {
	           super.onPreExecute();
	           
	           pDialog = new ProgressDialog(LoginActivity.this);
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
					
					SplashActivity.mydb.execSQL("DELETE FROM Tenant");
					for (int i = 0; i < lengthJsonArr; i++) 
					{

						 JSONObject jsonChildNode = jsonarray.getJSONObject(i);

						 String tenName = jsonChildNode.optString("name").toString();
	  					 String tenPhonenumber = jsonChildNode.optString("phonenNo").toString();
						 String tenEmail = jsonChildNode.optString("email").toString();
						 String tenApartmentnumber = jsonChildNode.optString("aparmentNo").toString();
						 String tenAddress = jsonChildNode.optString("address").toString();
						 String building_id = jsonChildNode.optString("building_id").toString();
						 String tenant_id = jsonChildNode.optString("id").toString();
						
						
						 SplashActivity.mydb.execSQL("insert into Tenant"
		    			 + " (tenName,tenPhonenumber,tenEmail,tenApartmentnumber,tenAddress,building_id,tenant_id)" +
		    			 " VALUES ('"+ tenName + "','"+ tenPhonenumber + "','"+ tenEmail + "','"+ tenApartmentnumber + "','"+ tenAddress + "','"+ building_id + "','"+ tenant_id + "')");
						
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
	private class Building extends AsyncTask<String, Void, String> {
		
    private ProgressDialog pDialog;
	    	
 	 @Override
    protected void onPreExecute() {
          super.onPreExecute();
          
          pDialog = new ProgressDialog(LoginActivity.this);
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
				
				SplashActivity.mydb.execSQL("DELETE FROM Building");
				for (int i = 0; i < lengthJsonArr; i++) 
				{

					 JSONObject jsonChildNode = jsonarray.getJSONObject(i);

					 String name = jsonChildNode.optString("name").toString();
 					 String address1 = jsonChildNode.optString("address1").toString();
					 String note = jsonChildNode.optString("note").toString();
					 String customer_id = jsonChildNode.optString("customer_id").toString();
					 String building_id = jsonChildNode.optString("id").toString();
					
					
					 SplashActivity.mydb.execSQL("insert into Building"
	    						+ " (buildingname,buildingaddress,buildingnote,customer_id,building_id) VALUES ('"+ name + "','"+ address1 + "','"+ note + "','"+ customer_id + "','"+ building_id + "')");
					
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
	
	private class User_Info extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(LoginActivity.this);
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
					
					SplashActivity.mydb.execSQL("DELETE FROM User");
					for (int i = 0; i < lengthJsonArr; i++) 
					{
						 JSONObject jsonChildNode = jsonarray.getJSONObject(i);
						 //String id = jsonChildNode.optString("id").toString();
						 String username = jsonChildNode.optString("userName").toString();
	 					 String phone_no = jsonChildNode.optString("mobile").toString();
	 					 String user_id = jsonChildNode.optString("userId").toString();
						 String sysUser_id = jsonChildNode.optString("sysUser_id").toString();
						
						
						 SplashActivity.mydb.execSQL("insert into User"
		    						+ " (userName,userPhonenumber,user_id,sysUser_id) VALUES ('"+ username + "','"+ phone_no + "','"+ user_id + "','"+ sysUser_id + "')");
						
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
	
private class CustomertaskType extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(LoginActivity.this);
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
					
					SplashActivity.mydb.execSQL("DELETE FROM Customer");
					for (int i = 0; i < lengthJsonArr; i++) 
					{
						 JSONObject jsonChildNode = jsonarray.getJSONObject(i);

						 String customer_id = jsonChildNode.optString("id").toString();
	 					 String taskType_id = jsonChildNode.optString("taskType_id").toString();
	 					 String name = jsonChildNode.optString("name").toString();
						
						
						 SplashActivity.mydb.execSQL("insert into Customer"
		    						+ " (customer_id,taskType_id,cusName) VALUES ('"+ customer_id + "','"+ taskType_id + "','"+ name + "')");
						
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

	private class Building_Info extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(LoginActivity.this);
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
	 				    createDate=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(l);
	 					
	 					String note = jsonChildNode.optString("note").toString();
						String building_id = jsonChildNode.optString("building_id").toString();
						
						SplashActivity.mydb.execSQL("insert into Info"
		    						+ " (infocreateDate,infoNote,building_id) VALUES ('"+ createDate + "','"+ note + "','"+ building_id + "')");
						
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
	private class Tasks extends AsyncTask<String, Void, String> {
		
	    private ProgressDialog pDialog;
		    	
	 	 @Override
	    protected void onPreExecute() {
	          super.onPreExecute();
	          
	          pDialog = new ProgressDialog(LoginActivity.this);
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
					for (int i = 0; i < lengthJsonArr; i++) 
					{
						JSONObject jsonChildNode = jsonarray.getJSONObject(i);

						String heading = jsonChildNode.optString("heading").toString();
		 				String note = jsonChildNode.optString("note").toString();

		 				String createDate = jsonChildNode.optString("taskCreateDate").toString();
	 					if(!createDate.equals("null"))
	 					{
		 					int idx1 = createDate.indexOf("(");
		 				    int idx2 = createDate.indexOf(")") - 5;
		 				    String s = createDate.substring(idx1+1, idx2);
		 				    long l = Long.valueOf(s);
		 				    createDate=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(l);
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
		 				   startDate=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(l);
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
		 				   finishDate=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(l);
	 					}
	 					else
	 					{
	 						finishDate="";
	 					}
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
		 				   task_deadline=new SimpleDateFormat( "EEEE, MMM dd, yyyy, hh:mm aa", Locale.getDefault()).format(l);
	 					}
		 				else
	 					{
		 					task_deadline="";
	 					}
		 				String task_id = jsonChildNode.optString("id").toString();
		 				
						SplashActivity.mydb.execSQL("insert into Task"
		    						+ " (taskHeading,taskNote,taskCreateDate,taskStarttimestamp,taskEndtimestamp,building_Id,tenant_Id,user_Id,taskDeadline,task_Id) "
		    						+" VALUES ('"+ heading + "','"+ note + "','"+ createDate + "','"+ startDate + "','"+ finishDate + "','"+ building_id + "','"+ tenant_id + "','"+ user_id + "','"+ task_deadline + "','"+ task_id + "')");
						
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
	private class Janitor extends AsyncTask<String, Void, String> {
		
	     private ProgressDialog pDialog;
		    	
	  	 @Override
	     protected void onPreExecute() {
	           super.onPreExecute();
	           
	           pDialog = new ProgressDialog(LoginActivity.this);
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
					
					SplashActivity.mydb.execSQL("DELETE FROM Janitor");
					for (int i = 0; i < lengthJsonArr; i++) 
					{

						 JSONObject jsonChildNode = jsonarray.getJSONObject(i);

						 String username = jsonChildNode.optString("name").toString();
	 					 String phone_no = jsonChildNode.optString("phoneNo").toString();
	 					 String user_id = jsonChildNode.optString("id").toString();
						
						 SplashActivity.mydb.execSQL("insert into Janitor"
		    						+ " (userName,userPhonenumber,user_id) VALUES ('"+ username + "','"+ phone_no + "','"+ user_id + "')");
						
					}

					//Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
					//startActivity(logintomain);
					//finish();
			        Intent logintomain = new Intent(getApplicationContext(), MainActivity.class);
			        logintomain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(logintomain);
					finish();

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
