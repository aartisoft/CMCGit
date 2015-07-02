package com.clubmycab.model;

import android.location.Address;

public class AddressModel {
	
	String shortname;
	Address address;
	
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	
    public String getShortname() {
		return shortname;
	}
    
    public void setAddress(Address address) {
		this.address = address;
	}
    
    public Address getAddress() {
		return address;
	}

}
