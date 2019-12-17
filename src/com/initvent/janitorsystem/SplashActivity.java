package com.initvent.janitorsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {
	
	static SQLiteDatabase mydb;

	private static String DBNAME = "tms.db";
	private static String Login = "Login";
	private static String Task = "Task";
	private static String Building = "Building";
	private static String Tasktype = "Tasktype";
	private static String Customer = "Customer";
	private static String Tenant = "Tenant";
	private static String Janitor = "Janitor";
	private static String User = "User";
	private static String Contract = "Contract";
	private static String ContractTasktype = "ContractTasktype";
	private static String UserTasktype = "UserTasktype";
	private static String Info = "Info";
	private static String Attachment = "Attachment";
	private static String Photo = "Photo";

	
	
	private static int SPLASH_TIME_OUT = 2000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mydb = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Login
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " servername  VARCHAR(100) ," + " servicename   VARCHAR(100)," + " userId   VARCHAR(100) ,"
					+ " password VARCHAR(100))");

		mydb.execSQL("CREATE TABLE IF NOT EXISTS " + Task
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ " taskHeading  VARCHAR(50) ," + " taskNote   VARCHAR(256)," + " taskDeadline   VARCHAR(100) ,"
					+ " taskSlip VARCHAR(100)," + " taskStarttimestamp  VARCHAR(50) ,"  + " taskEndtimestamp  VARCHAR(50) ,"
					 + " taskDuration  VARCHAR(50) ," + " taskDurationRounded  VARCHAR(50) ," + " tasktype_Id  VARCHAR(100) ,"
					 + " building_Id  VARCHAR(100) ," + " tenant_Id  VARCHAR(100) ," + " user_Id  VARCHAR(100), " + " taskCreateDate  VARCHAR(50)," + " task_Id  VARCHAR(100)," + " whichcolor  VARCHAR(2))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Building
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " buildingname  VARCHAR(50) ," + " buildingaddress   VARCHAR(100)," + " buildingnote   VARCHAR(256) ,"
				+ " customer_id VARCHAR(100)," + " building_id VARCHAR(100))");  //building_id for service reference

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Tasktype
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " tasktypeheading  VARCHAR(100) ," 
				+ " tasktypecost VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Customer
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " cusName  VARCHAR(50) ,cusAddress1  VARCHAR(100) ,cusAddress2  VARCHAR(100) ,cusPostalcode  VARCHAR(30) ," 
				+ " cusCity  VARCHAR(100) ,cusPhonenumber  VARCHAR(30) ,cusEmail  VARCHAR(100) ,cusHomepage VARCHAR(100),customer_id VARCHAR(100),taskType_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Tenant
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " tenName  VARCHAR(50) ,tenPhonenumber  VARCHAR(30) ,tenEmail  VARCHAR(100) ," 
				+ " tenApartmentnumber VARCHAR(100) ,tenAddress  VARCHAR(100) ,building_id VARCHAR(100),tenant_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Janitor
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " userName  VARCHAR(50) ," 
				+ " userPhonenumber VARCHAR(30),user_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + User
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " userName  VARCHAR(50) ," 
				+ " userPhonenumber VARCHAR(30),user_id VARCHAR(100),sysUser_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Contract
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " contractCost  VARCHAR(100), contractNote VARCHAR(100)," 
				+ " customer_id VARCHAR(100), building_Id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + ContractTasktype
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " tasktype_id  VARCHAR(100), " 
				+ " contract_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + UserTasktype
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " tasktype_id  VARCHAR(100), " 
				+ " user_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Info
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " infocreateDate  VARCHAR(50), infoNote VARCHAR(100), building_id VARCHAR(100),info_id VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Attachment
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " invoiceId  VARCHAR(50), attachName VARCHAR(100), attachNote VARCHAR(100), task_id VARCHAR(100), attach_id VARCHAR(100), attachmentCost  VARCHAR(100))");

		mydb.execSQL(" CREATE TABLE IF NOT EXISTS " + Photo
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " picture BLOB, task_id  VARCHAR(100), attach_id VARCHAR(100))");

		Log.e("Databasehealper", "********************************");
		try {
			File f1 = new File("/data/data/com.initvent.janitorsystem/databases/tms.db");
			if (f1.exists()) {

				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/tms.db");
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

		new Handler().postDelayed(new Runnable() {

		/*
		 * Showing splash screen with a timer. This will be useful when you
		 * want to show case your app logo / company
		 */

		@Override
		public void run() {
			// This method will be executed once the timer is over
			// Start your app main activity
			Intent i = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(i);

			// close this activity
			finish();
		}
	}, SPLASH_TIME_OUT);

	
	
}

}