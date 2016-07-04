
package com.clubmycab.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicRide {

    private String id;
    private String rGid;
    private String name;
    private List<Ride> rides = new ArrayList<Ride>();

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The rides
     */
    public List<Ride> getRides() {
        return rides;
    }

    /**
     * 
     * @param rides
     *     The rides
     */
    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

   

}
