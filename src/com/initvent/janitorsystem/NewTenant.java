package com.initvent.janitorsystem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTenant extends Activity implements OnClickListener{

	Button btntenAdd;
	EditText ettenNane,ettenPhonenumber,ettenEmail,ettenApartmentnumber,ettenAddress,etBuildingid;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tenant);
		
		ettenNane=(EditText)findViewById(R.id.ettenNane);		
		ettenPhonenumber=(EditText)findViewById(R.id.ettenPhonenumber);		
		ettenEmail=(EditText)findViewById(R.id.ettenEmail);		
		ettenApartmentnumber=(EditText)findViewById(R.id.ettenApartmentnumber);		
		ettenAddress=(EditText)findViewById(R.id.ettenAddress);		
		etBuildingid=(EditText)findViewById(R.id.etBuildingid);		
		
		btntenAdd=(Button)findViewById(R.id.btntenAdd);
		
		btntenAdd.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btntenAdd:
			insertTenant();
			break;

		default:
			break;
		}
	}
	private void insertTenant() {
		// TODO Auto-generated method stub
		String tenNane = ettenNane.getText().toString().trim();
		String tenPhonenumber = ettenPhonenumber.getText().toString().trim();
		String tenEmail = ettenEmail.getText().toString().trim();
		String tenApartmentnumber = ettenApartmentnumber.getText().toString().trim();
		String tenAddress = ettenAddress.getText().toString().trim();
		String Buildingid = etBuildingid.getText().toString().trim();

		SplashActivity.mydb.execSQL("insert into Tenant(tenName,tenPhonenumber,tenEmail,tenApartmentnumber,tenAddress,building_id)"
				+" Values('" + tenNane + "','" + tenPhonenumber + "','" + tenEmail + "','" + tenApartmentnumber + "','" + tenAddress + "','" + Buildingid + "')");

		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();
	}

}
