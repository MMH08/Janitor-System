package com.initvent.janitorsystem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewBuildingActivity extends Activity implements OnClickListener{

	Button btnBuildingAdd;
	EditText etBuildingName,etBuildingAddress,etBuildingNote,etBuildingCustomer;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newbuilding);
		
		etBuildingName=(EditText)findViewById(R.id.etBuildingName);		
		etBuildingAddress=(EditText)findViewById(R.id.etBuildingAddress);		
		etBuildingNote=(EditText)findViewById(R.id.etBuildingNote);		
		etBuildingCustomer=(EditText)findViewById(R.id.etBuildingCusid);		
		
		btnBuildingAdd=(Button)findViewById(R.id.btnBuildingAdd);
		
		btnBuildingAdd.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnBuildingAdd:
			insertBuilding();
			break;

		default:
			break;
		}
	}
	private void insertBuilding() {
		// TODO Auto-generated method stub
		String buildingName = etBuildingName.getText().toString().trim();
		String buildingAddress = etBuildingAddress.getText().toString().trim();
		String buildingNote = etBuildingNote.getText().toString().trim();
		String buildingCustomer = etBuildingCustomer.getText().toString().trim();

		SplashActivity.mydb.execSQL("insert into Building(buildingname,buildingaddress,buildingnote,customer_id)"
				+" Values('" + buildingName + "','" + buildingAddress + "','" + buildingNote + "','" + buildingCustomer + "')");

		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();
	}

}
