package com.initvent.janitorsystem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterFinishedTaskList extends ArrayAdapter<GetFinishedTaskListView>{

	Context context;
	int layoutResourceId;
	ArrayList<GetFinishedTaskListView> data=new ArrayList<GetFinishedTaskListView>();
	
	public ArrayAdapterFinishedTaskList(Context context, int layoutResourceId, ArrayList<GetFinishedTaskListView> data) {
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
			holder.txttask_id = (TextView)row.findViewById(R.id.txtguidId);
			holder.txtHeading = (TextView)row.findViewById(R.id.txtadpHeading);
			//holder.txtNote = (TextView)row.findViewById(R.id.txtadpNote);
			holder.txttaskStarttimestamp = (TextView)row.findViewById(R.id.txttaskStarttimestamp);
			holder.txttaskEndtimestamp = (TextView)row.findViewById(R.id.txttaskEndtimestamp);
			holder.txtduration = (TextView)row.findViewById(R.id.txtduration);
			row.setTag(holder);
		}
		else
		{
			holder = (TextHolder)row.getTag();
		}
		
		GetFinishedTaskListView getdata = data.get(position);
		holder.txtId.setText(""+getdata._id);
		holder.txttask_id.setText(""+getdata ._task_id);
		holder.txtHeading.setText(""+getdata ._taskHeading);
		//holder.txtNote.setText(""+getdata ._taskNote);
		holder.txttaskStarttimestamp.setText(""+getdata ._taskStarttimestamp);
		holder.txttaskEndtimestamp.setText(""+getdata ._taskEndtimestamp);
		holder.txtduration.setText(""+getdata ._duration);
		return row;
	}
	static class TextHolder
	{
		//ImageView imgIcon;
		//TextView txtTitle;
		TextView txtId;
		TextView txttask_id;
		TextView txtHeading;
		TextView txtNote;
		TextView txttaskStarttimestamp;
		TextView txttaskEndtimestamp;
		TextView txtduration;
	}

}
