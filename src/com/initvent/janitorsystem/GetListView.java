package com.initvent.janitorsystem;

public class GetListView {
	// private variables
	int _id;
	String _taskHeading;
	String _building_id;
	String _taskNote;
	String _taskCreateuser;
	String _taskDeadline;
	String _taskSlip;
	
	int _photoid;
	byte[] _image;
	

	// Empty constructor
	public GetListView() {

	}

	// constructor
	public GetListView(int keyId, String taskHeading, String building_id, String taskNote, String taskCreateuser,String taskDeadline, String taskSlip) {
	this._id = keyId;
	this._taskHeading = taskHeading;
	this._building_id = building_id;
	this._taskNote = taskNote;
	this._taskCreateuser = taskCreateuser;
	this._taskDeadline = taskDeadline;
	this._taskSlip = taskSlip;
	}

	//constructor
	public GetListView(int keyId, byte[] image) {
	this._photoid = keyId;
	this._image = image;

	}

	// getting ID
	public int getID() {
	return this._id;
	}

	// setting id
	public void setID(int keyId) {
	this._id = keyId;
	}

	// getting task heading
	public String gettaskHeading() {
	return this._taskHeading;
	}

	// setting task heading
	public void settaskHeading(String taskHeading) {
	this._taskHeading = taskHeading;
	}
	
	// getting task heading
	public String getbuilding_id() {
	return this._building_id;
	}

	// setting task heading
	public void setbuilding_id(String building_id) {
	this._building_id = building_id;
	}
	// getting task heading
	public String gettaskNote() {
	return this._taskNote;
	}

	// setting task heading
	public void settaskNote(String taskNote) {
	this._taskNote = taskNote;
	}
	// getting task heading
	public String gettaskCreateuser() {
	return this._taskCreateuser;
	}

	// setting task heading
	public void settaskCreateuser(String taskCreateuser) {
	this._taskCreateuser = taskCreateuser;
	}

	// getting task heading
	public String gettaskDeadline() {
	return this._taskDeadline;
	}

	// setting task heading
	public void settaskDeadline(String taskDeadline) {
	this._taskDeadline = taskDeadline;
	}

	// getting task heading
	public String gettaskSlip() {
	return this._taskSlip;
	}

	// setting task heading
	public void settaskSlip(String taskSlip) {
	this._taskSlip = taskSlip;
	}

	
	// getting phone number
	public int getPhotoid() {
	return this._photoid;
	}

	// setting phone number
	public void setPhotoid(int photoId) {
	this._photoid = photoId;
	}

	// getting phone number
	public byte[] getImage() {
	return this._image;
	}

	// setting phone number
	public void setImage(byte[] image) {
	this._image = image;
	}
	
	
	// getting ID
	public int getattachID() {
	return this._id;
	}

	// setting id
	public void setattachID(int keyId) {
	this._id = keyId;
	}

	// getting task heading
	public String getattachInvoiceid() {
	return this._building_id;
	}

	// setting task heading
	public void setattachInvoiceid(String building_id) {
	this._building_id = building_id;
	}

	// getting task heading
	public String getattachName() {
	return this._taskHeading;
	}

	// setting task heading
	public void setattachName(String taskHeading) {
	this._taskHeading = taskHeading;
	}
	
	// getting task heading
	public String getattachNote() {
	return this._taskNote;
	}

	// setting task heading
	public void setattachNote(String taskNote) {
	this._taskNote = taskNote;
	}
	
	// getting attach phone number
	public int getattachPhotoid() {
	return this._photoid;
	}

	// setting attach phone number
	public void setattachPhotoid(int photoId) {
	this._photoid = photoId;
	}

	// getting attach phone number
	public byte[] getattachImage() {
	return this._image;
	}

	// setting attach phone number
	public void setattachImage(byte[] image) {
	this._image = image;
	}
	


}
