package com.initvent.janitorsystem;

public class GetFinishedTaskListView {
	int _id;
	String _task_id;
	String _taskHeading;
	String _taskNote;
	String _taskStarttimestamp;
	String _taskEndtimestamp;
	String _duration;
	
	// Empty constructor
	public GetFinishedTaskListView() {

	}

	// constructor
	public GetFinishedTaskListView(int keyId, String task_id, String taskHeading, String taskNote, String taskStarttimestamp,String taskEndtimestamp, String duration) {
	this._id = keyId;
	this._task_id = task_id;
	this._taskHeading = taskHeading;
	this._taskNote = taskNote;
	this._taskStarttimestamp = taskStarttimestamp;
	this._taskEndtimestamp = taskEndtimestamp;
	this._duration = duration;
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
	public String gettask_id() {
	return this._task_id;
	}

	// setting task heading
	public void settask_id(String task_id) {
	this._task_id = task_id;
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
	public String gettaskNote() {
	return this._taskNote;
	}

	// setting task heading
	public void settaskNote(String taskNote) {
	this._taskNote = taskNote;
	}
	// getting task heading
	public String gettaskStarttimestamp() {
	return this._taskStarttimestamp;
	}

	// setting task heading
	public void settaskStarttimestamp(String taskStarttimestamp) {
	this._taskStarttimestamp = taskStarttimestamp;
	}
	// getting task heading
	public String gettaskEndtimestamp() {
	return this._taskEndtimestamp;
	}

	// setting task heading
	public void settaskEndtimestamp(String taskEndtimestamp) {
	this._taskEndtimestamp = taskEndtimestamp;
	}
	// getting task heading
	public String geduration() {
	return this._duration;
	}

	// setting task heading
	public void setduration(String duration) {
	this._duration = duration;
	}
}
