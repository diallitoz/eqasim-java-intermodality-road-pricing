package org.eqasim.core.components.car_pt.routing;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;

/*
This class describes the all attributes a P+R lot and the usual functions
 */
public class ParkRideLot {
    private final Scenario scenario;
    private String id;
    private int capacity;
    private double price;
    private boolean isFull;
    private boolean isFree;
    private	double	xCoord;
    private	double	yCoord;
    private Coord coord;

    @Inject
    public ParkRideLot(Scenario scenario) {
        this.scenario = scenario;
    }


    public	double getxCoord(){
        return	this.xCoord;
    }
}
