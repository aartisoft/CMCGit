package com.clubmycab;

public class ClubObject {

	private String contactName;
	private String clubmembers;
	private boolean selected;

	private String NoofMembers;
	private String ClubOwnerName;

	public String getClubOwnerName() {
		return ClubOwnerName;
	}

	public void setClubOwnerName(String clubOwnerName) {
		ClubOwnerName = clubOwnerName;
	}

	public String getNoofMembers() {
		return NoofMembers;
	}

	public void setNoofMembers(String noofMembers) {
		NoofMembers = noofMembers;
	}

	public String getName() {
		return contactName;
	}

	public String getClubmembers() {
		return clubmembers;
	}

	public void setClubmembers(String clubmembers) {
		this.clubmembers = clubmembers;
	}

	public void setName(String contactName) {
		this.contactName = contactName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
