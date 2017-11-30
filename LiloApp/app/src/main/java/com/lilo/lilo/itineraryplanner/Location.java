package com.lilo.lilo.itineraryplanner;

import java.util.*;

/**
 * Created by Oon Tong on 11/26/2017.
 */
public class Location {
    public String name;
    public int locationID;
    /*List<Path> edges = new LinkedList<>();*/
    List<double[]> edges = new LinkedList<>();

    public Location(String name,int ID){
        this.name = name;
        this.locationID = ID;
    }



}
