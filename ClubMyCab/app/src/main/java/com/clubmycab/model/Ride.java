
package com.clubmycab.model;

public class Ride {

    private String CabId;
    private String MobileNumber;
    private String OwnerName;
    private String FromLocation;
    private String ToLocation;
    private String FromShortName;
    private String ToShortName;
    private String TravelDate;
    private String TravelTime;
    private String Seats;
    private String Distance;
    private String ExpTripDuration;
    private String OpenTime;
    private String CabStatus;
    private String status;
    private String RateNotificationSend;
    private String ExpStartDateTime;
    private String ExpEndDateTime;
    private String OwnerChatStatus;
    private Object FareDetails;
    private String RemainingSeats;
    private String IsOwner;
    private String SeatStatus;
    private String rideType;
    private String perKmCharge;
    private String imagename;
    private Object BookingRefNo;
    private Object CabName;
    private Object DriverName;
    private Object DriverNumber;
    private Object CarNumber;
    private Object CarType;
    private String PoolId;
    private String PoolName;
    private String rGid;
    private String vehicleModel;
    private String registrationNumber;
    private String isCommercial;

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



    /**
     * 
     * @return
     *     The CabId
     */
    public String getCabId() {
        return CabId;
    }

    /**
     * 
     * @param CabId
     *     The CabId
     */
    public void setCabId(String CabId) {
        this.CabId = CabId;
    }

    /**
     * 
     * @return
     *     The MobileNumber
     */
    public String getMobileNumber() {
        return MobileNumber;
    }

    /**
     * 
     * @param MobileNumber
     *     The MobileNumber
     */
    public void setMobileNumber(String MobileNumber) {
        this.MobileNumber = MobileNumber;
    }

    /**
     * 
     * @return
     *     The OwnerName
     */
    public String getOwnerName() {
        return OwnerName;
    }

    /**
     * 
     * @param OwnerName
     *     The OwnerName
     */
    public void setOwnerName(String OwnerName) {
        this.OwnerName = OwnerName;
    }

    /**
     * 
     * @return
     *     The FromLocation
     */
    public String getFromLocation() {
        return FromLocation;
    }

    /**
     * 
     * @param FromLocation
     *     The FromLocation
     */
    public void setFromLocation(String FromLocation) {
        this.FromLocation = FromLocation;
    }

    /**
     * 
     * @return
     *     The ToLocation
     */
    public String getToLocation() {
        return ToLocation;
    }

    /**
     * 
     * @param ToLocation
     *     The ToLocation
     */
    public void setToLocation(String ToLocation) {
        this.ToLocation = ToLocation;
    }

    /**
     * 
     * @return
     *     The FromShortName
     */
    public String getFromShortName() {
        return FromShortName;
    }

    /**
     * 
     * @param FromShortName
     *     The FromShortName
     */
    public void setFromShortName(String FromShortName) {
        this.FromShortName = FromShortName;
    }

    /**
     * 
     * @return
     *     The ToShortName
     */
    public String getToShortName() {
        return ToShortName;
    }

    /**
     * 
     * @param ToShortName
     *     The ToShortName
     */
    public void setToShortName(String ToShortName) {
        this.ToShortName = ToShortName;
    }

    /**
     * 
     * @return
     *     The TravelDate
     */
    public String getTravelDate() {
        return TravelDate;
    }

    /**
     * 
     * @param TravelDate
     *     The TravelDate
     */
    public void setTravelDate(String TravelDate) {
        this.TravelDate = TravelDate;
    }

    /**
     * 
     * @return
     *     The TravelTime
     */
    public String getTravelTime() {
        return TravelTime;
    }

    /**
     * 
     * @param TravelTime
     *     The TravelTime
     */
    public void setTravelTime(String TravelTime) {
        this.TravelTime = TravelTime;
    }

    /**
     * 
     * @return
     *     The Seats
     */
    public String getSeats() {
        return Seats;
    }

    /**
     * 
     * @param Seats
     *     The Seats
     */
    public void setSeats(String Seats) {
        this.Seats = Seats;
    }

    /**
     * 
     * @return
     *     The Distance
     */
    public String getDistance() {
        return Distance;
    }

    /**
     * 
     * @param Distance
     *     The Distance
     */
    public void setDistance(String Distance) {
        this.Distance = Distance;
    }

    /**
     * 
     * @return
     *     The ExpTripDuration
     */
    public String getExpTripDuration() {
        return ExpTripDuration;
    }

    /**
     * 
     * @param ExpTripDuration
     *     The ExpTripDuration
     */
    public void setExpTripDuration(String ExpTripDuration) {
        this.ExpTripDuration = ExpTripDuration;
    }

    /**
     * 
     * @return
     *     The OpenTime
     */
    public String getOpenTime() {
        return OpenTime;
    }

    /**
     * 
     * @param OpenTime
     *     The OpenTime
     */
    public void setOpenTime(String OpenTime) {
        this.OpenTime = OpenTime;
    }

    /**
     * 
     * @return
     *     The CabStatus
     */
    public String getCabStatus() {
        return CabStatus;
    }

    /**
     * 
     * @param CabStatus
     *     The CabStatus
     */
    public void setCabStatus(String CabStatus) {
        this.CabStatus = CabStatus;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The RateNotificationSend
     */
    public String getRateNotificationSend() {
        return RateNotificationSend;
    }

    /**
     * 
     * @param RateNotificationSend
     *     The RateNotificationSend
     */
    public void setRateNotificationSend(String RateNotificationSend) {
        this.RateNotificationSend = RateNotificationSend;
    }

    /**
     * 
     * @return
     *     The ExpStartDateTime
     */
    public String getExpStartDateTime() {
        return ExpStartDateTime;
    }

    /**
     * 
     * @param ExpStartDateTime
     *     The ExpStartDateTime
     */
    public void setExpStartDateTime(String ExpStartDateTime) {
        this.ExpStartDateTime = ExpStartDateTime;
    }

    /**
     * 
     * @return
     *     The ExpEndDateTime
     */
    public String getExpEndDateTime() {
        return ExpEndDateTime;
    }

    /**
     * 
     * @param ExpEndDateTime
     *     The ExpEndDateTime
     */
    public void setExpEndDateTime(String ExpEndDateTime) {
        this.ExpEndDateTime = ExpEndDateTime;
    }

    /**
     * 
     * @return
     *     The OwnerChatStatus
     */
    public String getOwnerChatStatus() {
        return OwnerChatStatus;
    }

    /**
     * 
     * @param OwnerChatStatus
     *     The OwnerChatStatus
     */
    public void setOwnerChatStatus(String OwnerChatStatus) {
        this.OwnerChatStatus = OwnerChatStatus;
    }

    /**
     * 
     * @return
     *     The FareDetails
     */
    public Object getFareDetails() {
        return FareDetails;
    }

    /**
     * 
     * @param FareDetails
     *     The FareDetails
     */
    public void setFareDetails(Object FareDetails) {
        this.FareDetails = FareDetails;
    }

    /**
     * 
     * @return
     *     The RemainingSeats
     */
    public String getRemainingSeats() {
        return RemainingSeats;
    }

    /**
     * 
     * @param RemainingSeats
     *     The RemainingSeats
     */
    public void setRemainingSeats(String RemainingSeats) {
        this.RemainingSeats = RemainingSeats;
    }

    /**
     * 
     * @return
     *     The IsOwner
     */
    public String getIsOwner() {
        return IsOwner;
    }

    /**
     * 
     * @param IsOwner
     *     The IsOwner
     */
    public void setIsOwner(String IsOwner) {
        this.IsOwner = IsOwner;
    }

    /**
     * 
     * @return
     *     The SeatStatus
     */
    public String getSeatStatus() {
        return SeatStatus;
    }

    /**
     * 
     * @param SeatStatus
     *     The Seat_Status
     */
    public void setSeatStatus(String SeatStatus) {
        this.SeatStatus = SeatStatus;
    }

    /**
     * 
     * @return
     *     The rideType
     */
    public String getRideType() {
        return rideType;
    }

    /**
     * 
     * @param rideType
     *     The rideType
     */
    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    /**
     * 
     * @return
     *     The perKmCharge
     */
    public String getPerKmCharge() {
        return perKmCharge;
    }

    /**
     * 
     * @param perKmCharge
     *     The perKmCharge
     */
    public void setPerKmCharge(String perKmCharge) {
        this.perKmCharge = perKmCharge;
    }

    /**
     * 
     * @return
     *     The imagename
     */
    public String getImagename() {
        return imagename;
    }

    /**
     * 
     * @param imagename
     *     The imagename
     */
    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    /**
     * 
     * @return
     *     The BookingRefNo
     */
    public Object getBookingRefNo() {
        return BookingRefNo;
    }

    /**
     * 
     * @param BookingRefNo
     *     The BookingRefNo
     */
    public void setBookingRefNo(Object BookingRefNo) {
        this.BookingRefNo = BookingRefNo;
    }

    /**
     * 
     * @return
     *     The CabName
     */
    public Object getCabName() {
        return CabName;
    }

    /**
     * 
     * @param CabName
     *     The CabName
     */
    public void setCabName(Object CabName) {
        this.CabName = CabName;
    }

    /**
     * 
     * @return
     *     The DriverName
     */
    public Object getDriverName() {
        return DriverName;
    }

    /**
     * 
     * @param DriverName
     *     The DriverName
     */
    public void setDriverName(Object DriverName) {
        this.DriverName = DriverName;
    }

    /**
     * 
     * @return
     *     The DriverNumber
     */
    public Object getDriverNumber() {
        return DriverNumber;
    }

    /**
     * 
     * @param DriverNumber
     *     The DriverNumber
     */
    public void setDriverNumber(Object DriverNumber) {
        this.DriverNumber = DriverNumber;
    }

    /**
     * 
     * @return
     *     The CarNumber
     */
    public Object getCarNumber() {
        return CarNumber;
    }

    /**
     * 
     * @param CarNumber
     *     The CarNumber
     */
    public void setCarNumber(Object CarNumber) {
        this.CarNumber = CarNumber;
    }

    /**
     * 
     * @return
     *     The CarType
     */
    public Object getCarType() {
        return CarType;
    }

    /**
     * 
     * @param CarType
     *     The CarType
     */
    public void setCarType(Object CarType) {
        this.CarType = CarType;
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
