package com.initvent.janitorsystem;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.initvent.janitorsystem.ArrayAdapterList.TextHolder;

public class ArrayListAdapterPhoto extends ArrayAdapter<GetListView>{
	Context context;
	int layoutResourceId;
	ArrayList<GetListView> data=new ArrayList<GetListView>();
	
	public ArrayListAdapterPhoto(Context context, int layoutResourceId, ArrayList<GetListView> data) {
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
			holder.txtId = (TextView)row.findViewById(R.id.txtPhotoId);
			holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
			row.setTag(holder);
		}
		else
		{
			holder = (TextHolder)row.getTag();
		}
		
		GetListView getdata = data.get(position);
		holder.txtId.setText(""+getdata._photoid);
		byte[] outImage=getdata._image;
		ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
		Bitmap theImage = BitmapFactory.decodeStream(imageStream);
		holder.imgIcon.setImageBitmap(theImage);
		return row;
	}
	static class TextHolder
	{
		//TextView txtTitle;
		TextView txtId;
		ImageView imgIcon;
		//TextView txtHeading;
		//TextView txtBuildingid;
		//TextView txtNote;
	}


}
