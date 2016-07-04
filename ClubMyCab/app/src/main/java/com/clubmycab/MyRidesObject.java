package com.clubmycab;

public class MyRidesObject {

	private String CabId;
	private String MobileNumber;
	private String OwnerName;
	private String FromLocation;
	private String ToLocation;
	private String FromShortName;
	private String ToShortName;
	private String TravelDate;

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getIsCommercial() {
		return isCommercial;
	}

	public void setIsCommercial(String isCommercial) {
		this.isCommercial = isCommercial;
	}

	private String vehicleModel;
	private String registrationNumber;
	private String isCommercial;
	public String getIsOwner() {
		return IsOwner;
	}

	public void setIsOwner(String isOwner) {
		IsOwner = isOwner;
	}

	private String IsOwner;

	public String getCabId() {
		return CabId;
	}

	public void setCabId(String cabId) {
		CabId = cabId;
	}

	public String getMobileNumber() {
		return MobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}

	public String getOwnerName() {
		return OwnerName;
	}

	public void setOwnerName(String ownerName) {
		OwnerName = ownerName;
	}

	public String getFromLocation() {
		return FromLocation;
	}

	public void setFromLocation(String fromLocation) {
		FromLocation = fromLocation;
	}

	public String getToLocation() {
		return ToLocation;
	}

	public void setToLocation(String toLocation) {
		ToLocation = toLocation;
	}

	public String getFromShortName() {
		return FromShortName;
	}

	public void setFromShortName(String fromShortName) {
		FromShortName = fromShortName;
	}

	public String getToShortName() {
		return ToShortName;
	}

	public void setToShortName(String toShortName) {
		ToShortName = toShortName;
	}

	public String getTravelDate() {
		return TravelDate;
	}

	public void setTravelDate(String travelDate) {
		TravelDate = travelDate;
	}

	public String getTravelTime() {
		return TravelTime;
	}

	public void setTravelTime(String travelTime) {
		TravelTime = travelTime;
	}

	public String getSeats() {
		return Seats;
	}

	public void setSeats(String seats) {
		Seats = seats;
	}

	public String getRemainingSeats() {
		return RemainingSeats;
	}

	public void setRemainingSeats(String remainingSeats) {
		RemainingSeats = remainingSeats;
	}

	public String getSeat_Status() {
		return Seat_Status;
	}

	public void setSeat_Status(String seat_Status) {
		Seat_Status = seat_Status;
	}

	public String getDistance() {
		return Distance;
	}

	public void setDistance(String distance) {
		Distance = distance;
	}

	public String getOpenTime() {
		return OpenTime;
	}

	public void setOpenTime(String openTime) {
		OpenTime = openTime;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public String getBookingRefNo() {
		return BookingRefNo;
	}

	public void setBookingRefNo(String bookingRefNo) {
		BookingRefNo = bookingRefNo;
	}

	public String getDriverName() {
		return DriverName;
	}

	public void setDriverName(String driverName) {
		DriverName = driverName;
	}

	public String getDriverNumber() {
		return DriverNumber;
	}

	public void setDriverNumber(String driverNumber) {
		DriverNumber = driverNumber;
	}

	public String getCarNumber() {
		return CarNumber;
	}

	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
	}

	private String TravelTime;
	private String Seats;
	private String RemainingSeats;
	private String Seat_Status;
	private String Distance;
	private String OpenTime;
	private String CabStatus;

	public String getCabStatus() {
		return CabStatus;
	}

	public void setCabStatus(String cabStatus) {
		CabStatus = cabStatus;
	}

	private String imagename;
	private String BookingRefNo;
	private String DriverName;
	private String DriverNumber;
	private String CarNumber;
	
	private String CabName;
	private String status;
	private String ExpTripDuration;

	public String getCabName() {
		return CabName;
	}

	public void setCabName(String cabName) {
		CabName = cabName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExpTripDuration() {
		return ExpTripDuration;
	}

	public void setExpTripDuration(String expTripDuration) {
		ExpTripDuration = expTripDuration;
	}

}
