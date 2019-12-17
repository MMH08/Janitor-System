package com.initvent.janitorsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MasterActivity extends Activity implements OnClickListener{
	Button btnNewBuilding,btnNewTasktype,btnNewCustomer,btnNewTenant,btnNewUser,btnNewContract;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master);
		btnNewBuilding=(Button)findViewById(R.id.btnNewBuilding);
		btnNewTasktype=(Button)findViewById(R.id.btnNewTasktype);
		btnNewCustomer=(Button)findViewById(R.id.btnNewCustomer);
		btnNewTenant=(Button)findViewById(R.id.btnNewTenant);
		btnNewUser=(Button)findViewById(R.id.btnNewUser);
		btnNewContract=(Button)findViewById(R.id.btnNewContract);
		
		btnNewBuilding.setOnClickListener(this);
		btnNewTasktype.setOnClickListener(this);
		btnNewCustomer.setOnClickListener(this);
		btnNewTenant.setOnClickListener(this);
		btnNewUser.setOnClickListener(this);
		btnNewContract.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnNewBuilding:
			Intent mastertonewbuilding =new Intent(this,NewBuildingActivity.class);
			mastertonewbuilding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mastertonewbuilding);
			break;

		case R.id.btnNewTasktype:
			Intent mastertonewtasktype = new Intent(this,NewTasktypeActivity.class);
			mastertonewtasktype.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mastertonewtasktype);
			break;
		case R.id.btnNewCustomer:
			//Intent mastertocustomer = new Intent(this,NewTasktypeActivity.class);
			//mastertocustomer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(mastertocustomer);
			break;
		case R.id.btnNewTenant:
			Intent mastertotenant = new Intent(this,NewTenant.class);
			mastertotenant.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mastertotenant);
			break;
		case R.id.btnNewUser:
			//Intent mastertouser = new Intent(this,NewTasktypeActivity.class);
			//mastertouser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(mastertouser);
			break;
		case R.id.btnNewContract:
			//Intent mastertocontract = new Intent(this,NewTasktypeActivity.class);
			//mastertocontract.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(mastertocontract);
			break;

		default:
			break;
		}
	}

}
