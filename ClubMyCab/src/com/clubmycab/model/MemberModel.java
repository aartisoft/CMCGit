package com.clubmycab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MemberModel implements Parcelable{
	private String memberName = "";
	private String memberNumber = "";
	private String ownerName = "";
	private String ownerNumber = "";

	public MemberModel() {

	}

	private MemberModel(Parcel in) {
		this.memberName = in.readString();
		this.memberNumber = in.readString();
		this.ownerName = in.readString();
		this.ownerNumber = in.readString();
		
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerNumber() {
		return ownerNumber;
	}
	public void setOwnerNumber(String ownerNumber) {
		this.ownerNumber = ownerNumber;
	}
	
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getMemberNumber() {
		return memberNumber;
	}
	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(memberName);
		dest.writeString(memberNumber);
		dest.writeString(ownerName);
		dest.writeString(ownerNumber);
		
	}
	
	public static final Parcelable.Creator<MemberModel> CREATOR = new Parcelable.Creator<MemberModel>() {

		public MemberModel createFromParcel(Parcel in) {
			return new MemberModel(in);
		}

		public MemberModel[] newArray(int size) {
			return new MemberModel[size];
		}
	};

}
