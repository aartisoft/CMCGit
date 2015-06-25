package com.clubmycab;

public class ChatObject {

	// private variables
	int _id;
	String CabId;
	String FullName;
	String Text;
	String Datetime;

	// Empty constructor
	public ChatObject() {

	}

	// constructor
	public ChatObject(String cid, String fname, String txt, String dtime) {
		this.CabId = cid;
		this.FullName = fname;
		this.Text = txt;
		this.Datetime = dtime;
	}

	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;
	}

	public String getCabId() {
		return CabId;
	}

	public void setCabId(String cabId) {
		CabId = cabId;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getDatetime() {
		return Datetime;
	}

	public void setDatetime(String datetime) {
		Datetime = datetime;
	}
}
