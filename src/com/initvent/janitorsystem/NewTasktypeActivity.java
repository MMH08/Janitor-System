package com.initvent.janitorsystem;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTasktypeActivity extends Activity implements OnClickListener{

	Button btnTasktypeAdd;
	EditText etTasktypeHeading,etTasktypeCost;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newtasktype);
		
		etTasktypeHeading=(EditText)findViewById(R.id.etTasktypeHeading);		
		etTasktypeCost=(EditText)findViewById(R.id.etTasktypeCost);		
		
		btnTasktypeAdd=(Button)findViewById(R.id.btnTasktypeAdd);
		
		btnTasktypeAdd.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnTasktypeAdd:
			insertTasktype();
			break;

		default:
			break;
		}
	}
	private void insertTasktype() {
		// TODO Auto-generated method stub
		String tasktypeHeading = etTasktypeHeading.getText().toString().trim();
		String tasktypeCost = etTasktypeCost.getText().toString().trim();

		SplashActivity.mydb.execSQL("insert into Tasktype(tasktypeheading,tasktypecost)"
				+" Values('" + tasktypeHeading + "','" + tasktypeCost + "')");

		Toast.makeText(getBaseContext(),"Inserted successfully",Toast.LENGTH_LONG).show();
	}

}
