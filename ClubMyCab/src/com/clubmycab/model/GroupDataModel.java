package com.clubmycab.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupDataModel implements Parcelable {
	private String ownerName = "";
	private String ownerNumber = "";
	private String numberOfMembers = "";
	private String poolName = "";
	private String poolId = "";
	private ArrayList<MemberModel> memberList;

	public GroupDataModel() {

	}

	private GroupDataModel(Parcel in) {
		this.ownerName = in.readString();
		this.ownerNumber = in.readString();
		this.numberOfMembers = in.readString();
		this.poolName = in.readString();
		this.poolId = in.readString();
		this.memberList = in.readArrayList(getClass().getClassLoader());
	}

	public String getNumberOfMembers() {
		return numberOfMembers;
	}

	public void setNumberOfMembers(String numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
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

	public void setMemberList(ArrayList<MemberModel> memberList) {
		this.memberList = memberList;
	}

	public ArrayList<MemberModel> getMemberList() {
		return memberList;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ownerName);
		dest.writeString(ownerNumber);
		dest.writeString(numberOfMembers);
		dest.writeString(poolName);
		dest.writeString(poolId);
		dest.writeList(memberList);

	}

	public static final Parcelable.Creator<GroupDataModel> CREATOR = new Parcelable.Creator<GroupDataModel>() {

		public GroupDataModel createFromParcel(Parcel in) {
			return new GroupDataModel(in);
		}

		public GroupDataModel[] newArray(int size) {
			return new GroupDataModel[size];
		}
	};
}
