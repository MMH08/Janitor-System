package com.initvent.janitorsystem;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterList extends ArrayAdapter<GetListView>{

	Context context;
	int layoutResourceId;
	ArrayList<GetListView> data=new ArrayList<GetListView>();
	
	public ArrayAdapterList(Context context, int layoutResourceId, ArrayList<GetListView> data) {
		super(context, layoutResourceId, data);
		// TODO Auto-generated constructor stub

		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TextHolder holder = null;
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new TextHolder();
			holder.txtId = (TextView)row.findViewById(R.id.txtadpId);
			holder.txtHeading = (TextView)row.findViewById(R.id.txtadpHeading);
			holder.txtBuildingid = (TextView)row.findViewById(R.id.txtadpBuildingid);
			holder.txtNote = (TextView)row.findViewById(R.id.txtadpNote);
			holder.txttaskCreateuser = (TextView)row.findViewById(R.id.txttaskCreateuser);
			row.setTag(holder);
		}
		else
		{
			holder = (TextHolder)row.getTag();
		}
		
		GetListView getdata = data.get(position);
		//holder.txtId.setText(getdata._id);
		//holder.txtHeading.setText(""+getdata ._taskHeading);
		//holder.txtBuildingid.setText(""+getdata ._building_id);
		//holder.txtNote.setText(""+getdata ._taskNote);
		/*
		SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy");
		String cur_date_time = new SimpleDateFormat( "EEEE, MMM dd, yyyy", Locale.getDefault()).format(new Date());
		Date cur_date=new Date();
		
		Date deadline = new Date();
		//String crdate=getdata ._taskNote;
		String deadlinestr=getdata._taskDeadline;
		String slipstr=getdata._taskSlip;
		int slip=0;
		if(slipstr.equals("") || slipstr.equals("null"))
		{}
		else
			slip=Integer.parseInt(slipstr);
		 
	    try {
	    	cur_date = df.parse(cur_date_time);
	    	deadline = df.parse(deadlinestr);
	    } 
	    catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(deadline);
		calendar.add(Calendar.DATE, slip);
		Date deadlinewithSlip=new Date();
		String deadlinewithSlipstr=df.format(calendar.getTime());
		
		try {
			deadlinewithSlip = df.parse(deadlinewithSlipstr);
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(slip!=0 && !deadlinestr.equals(""))
		{
			if((cur_date.after(deadline) || cur_date.equals(deadline)) && (cur_date.before(deadlinewithSlip) || cur_date.equals(deadlinewithSlip)))
			{
				holder.txtHeading.setTextColor(Color.BLUE);
				holder.txtBuildingid.setTextColor(Color.BLUE);
				holder.txttaskCreateuser.setTextColor(Color.BLUE);
				holder.txtNote.setTextColor(Color.BLUE);			
			}
			else if(cur_date.after(deadlinewithSlip) || cur_date.equals(deadlinewithSlip))
			{
				holder.txtHeading.setTextColor(Color.RED);
				holder.txtBuildingid.setTextColor(Color.RED);
				holder.txttaskCreateuser.setTextColor(Color.RED);
				holder.txtNote.setTextColor(Color.RED);
							
			}
			else
			{
				holder.txtHeading.setTextColor(Color.BLACK);
				holder.txtBuildingid.setTextColor(Color.BLACK);
				holder.txttaskCreateuser.setTextColor(Color.BLACK);
				holder.txtNote.setTextColor(Color.BLACK);			
			}
		}
		else
		{
			holder.txtHeading.setTextColor(Color.BLACK);
			holder.txtBuildingid.setTextColor(Color.BLACK);
			holder.txttaskCreateuser.setTextColor(Color.BLACK);
			holder.txtNote.setTextColor(Color.BLACK);		
		}
		*/
		String whichcolor="";
		Cursor cursor_task = SplashActivity.mydb.rawQuery("SELECT * FROM  Task where id='"+ getdata._id +"'",null);
		while(cursor_task.moveToNext())
		{
			whichcolor = cursor_task.getString(15);
			if(whichcolor.equals("null"))
				whichcolor="0";
		}

		if(whichcolor.equals("3"))
		{
			holder.txtHeading.setTextColor(Color.BLUE);
			holder.txtBuildingid.setTextColor(Color.BLUE);
			holder.txttaskCreateuser.setTextColor(Color.BLUE);
			holder.txtNote.setTextColor(Color.BLUE);		
		}
		else if(whichcolor.equals("2"))
		{
			holder.txtHeading.setTextColor(Color.RED);
			holder.txtBuildingid.setTextColor(Color.RED);
			holder.txttaskCreateuser.setTextColor(Color.RED);
			holder.txtNote.setTextColor(Color.RED);
		}
		else if(whichcolor.equals("1"))
		{
			holder.txtHeading.setTextColor(Color.GRAY);
			holder.txtBuildingid.setTextColor(Color.GRAY);
			holder.txttaskCreateuser.setTextColor(Color.GRAY);
			holder.txtNote.setTextColor(Color.GRAY);
		}
		else
		{
			holder.txtHeading.setTextColor(Color.BLACK);
			holder.txtBuildingid.setTextColor(Color.BLACK);
			holder.txttaskCreateuser.setTextColor(Color.BLACK);
			holder.txtNote.setTextColor(Color.BLACK);
		}
		holder.txtId.setText(""+getdata._id);
		holder.txtHeading.setText(""+getdata ._taskHeading);
		holder.txtBuildingid.setText(""+getdata ._building_id);
		holder.txtNote.setText(""+getdata ._taskNote);
		holder.txttaskCreateuser.setText(""+getdata ._taskCreateuser);
		return row;
	}
	static class TextHolder
	{
		//ImageView imgIcon;
		//TextView txtTitle;
		TextView txtId;
		TextView txtHeading;
		TextView txtBuildingid;
		TextView txtNote;
		TextView txttaskCreateuser;
	}
	

}
