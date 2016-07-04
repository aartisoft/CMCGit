package com.clubmycab.model;

import android.location.Address;

public class AddressModel {
	
	String shortname;
	String longname;
	Address address;
	
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	
    public String getShortname() {
		return shortname;
	}
    
    public String getLongname() {
		return longname;
	}

	public void setLongname(String longname) {
		this.longname = longname;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
    
    public Address getAddress() {
		return address;
	}

}
