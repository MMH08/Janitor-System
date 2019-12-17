package com.initvent.janitorsystem;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterInfoList extends ArrayAdapter<GetListView>{

	Context context;
	int layoutResourceId;
	ArrayList<GetListView> data=new ArrayList<GetListView>();
	
	public ArrayAdapterInfoList(Context context, int layoutResourceId, ArrayList<GetListView> data) {
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
		//if(getdata._taskNote.equals())
		//holder.txtNote.setTextColor(Color.RED);
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
