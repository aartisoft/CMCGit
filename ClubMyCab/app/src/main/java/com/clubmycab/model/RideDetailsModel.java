package com.clubmycab.model;

public class RideDetailsModel {

	String CabId;
	String MobileNumber;
	String OwnerName;
	String FromLocation;
	String ToLocation;
	String FromShortName;
	String ToShortName;
	String TravelDate;
	String TravelTime;
	String Seats;
	String Distance;
	String ExpTripDuration;
	String OpenTime;
	String CabStatus;
	String status;
	String RateNotificationSend;
	String ExpStartDateTime;
	String ExpEndDateTime;
	String OwnerChatStatus;
	String FareDetails;
	String RemainingSeats;
	String IsOwner;
	String Seat_Status;
	String imagename;
	String BookingRefNo;
	String CabName;
	String DriverName;
	String DriverNumber;
	String CarNumber;
	String CarType;
	String rideType;
	String perKmCharge;
    private String PoolId;
    private String PoolName;
    private String rGid;
	public String getsLatLon() {
		return sLatLon;
	}

	public void setsLatLon(String sLatLon) {
		this.sLatLon = sLatLon;
	}

	public String geteLatLon() {
		return eLatLon;
	}

	public void seteLatLon(String eLatLon) {
		this.eLatLon = eLatLon;
	}

	private String sLatLon;
	private String eLatLon;

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

	public String getDistance() {
		return Distance;
	}

	public void setDistance(String distance) {
		Distance = distance;
	}

	public String getExpTripDuration() {
		return ExpTripDuration;
	}

	public void setExpTripDuration(String expTripDuration) {
		ExpTripDuration = expTripDuration;
	}

	public String getOpenTime() {
		return OpenTime;
	}

	public void setOpenTime(String openTime) {
		OpenTime = openTime;
	}

	public String getCabStatus() {
		return CabStatus;
	}

	public void setCabStatus(String cabStatus) {
		CabStatus = cabStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRateNotificationSend() {
		return RateNotificationSend;
	}

	public void setRateNotificationSend(String rateNotificationSend) {
		RateNotificationSend = rateNotificationSend;
	}

	public String getExpStartDateTime() {
		return ExpStartDateTime;
	}

	public void setExpStartDateTime(String expStartDateTime) {
		ExpStartDateTime = expStartDateTime;
	}

	public String getExpEndDateTime() {
		return ExpEndDateTime;
	}

	public void setExpEndDateTime(String expEndDateTime) {
		ExpEndDateTime = expEndDateTime;
	}

	public String getOwnerChatStatus() {
		return OwnerChatStatus;
	}

	public void setOwnerChatStatus(String ownerChatStatus) {
		OwnerChatStatus = ownerChatStatus;
	}

	public String getFareDetails() {
		return FareDetails;
	}

	public void setFareDetails(String fareDetails) {
		FareDetails = fareDetails;
	}

	public String getRemainingSeats() {
		return RemainingSeats;
	}

	public void setRemainingSeats(String remainingSeats) {
		RemainingSeats = remainingSeats;
	}

	public String getIsOwner() {
		return IsOwner;
	}

	public void setIsOwner(String isOwner) {
		IsOwner = isOwner;
	}

	public String getSeat_Status() {
		return Seat_Status;
	}

	public void setSeat_Status(String seat_Status) {
		Seat_Status = seat_Status;
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

	public String getCabName() {
		return CabName;
	}

	public void setCabName(String cabName) {
		CabName = cabName;
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

	public String getCarType() {
		return CarType;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public String getRideType() {
		return rideType;
	}

	public void setRideType(String rideType) {
		this.rideType = rideType;
	}

	public String getPerKmCharge() {
		return perKmCharge;
	}

	public void setPerKmCharge(String perKmCharge) {
		this.perKmCharge = perKmCharge;
	}
	 /**
     * 
     * @return
     *     The PoolId
     */
    public String getPoolId() {
        return PoolId;
    }

    /**
     * 
     * @param PoolId
     *     The PoolId
     */
    public void setPoolId(String PoolId) {
        this.PoolId = PoolId;
    }

    /**
     * 
     * @return
     *     The PoolName
     */
    public String getPoolName() {
        return PoolName;
    }

    /**
     * 
     * @param PoolName
     *     The PoolName
     */
    public void setPoolName(String PoolName) {
        this.PoolName = PoolName;
    }

    /**
     * 
     * @return
     *     The rGid
     */
    public String getRGid() {
        return rGid;
    }

    /**
     * 
     * @param rGid
     *     The rGid
     */
    public void setRGid(String rGid) {
        this.rGid = rGid;
    }


}
