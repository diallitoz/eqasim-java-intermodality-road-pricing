package org.eqasim.core.components.car_pt.routing;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;

import org.matsim.contribs.discrete_mode_choice.components.utils.LocationUtils;

public class ParkingFinder {
    // Park and ride lot location
    private final List<Coord> parkRideCoords;

    // Constructor
    public ParkingFinder(final List<Coord> parkRideCoords) {
        this.parkRideCoords = parkRideCoords;
    }

    /*
     * This function return a parking lot Facility based either on home location of
     * the agent either on origin location and destination location. The last case
     * can be spilt according the aim of the agent. For example, there are people
     * who want to drive by car a long part of the trip. There are also, those who
     * want to perform the most part of trip by PT. We start with the last point.
     */
    // Based on Activity location
    public Id<Facility> getParking(Person person, Activity origActivity, Activity destActivity, Network network) {
        double minDist = 999999999.0;
        double distance = 0.0;
        int minIndex = 0;
        for (int i = 0; i < parkRideCoords.size(); i++) {
            distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), origActivity.getCoord());
            if (minDist > distance) {
                minDist = distance;
                minIndex = i;
            }
        }
        Coord coord = new Coord(parkRideCoords.get(minIndex).getX(), parkRideCoords.get(minIndex).getY());
        Link prLink = NetworkUtils.getNearestLink(network, coord);

        Facility prFacility = new LinkWrapperFacility(prLink);

        return null;
    }

    // Based on Activity location and return Facility
    public Facility getParking2(Person person, Activity origActivity, Activity destActivity, Network network) {
        double minDist = 999999999.0;
        double distance = 0.0;
        Coord homeXY = new Coord(704835, 7060541);
        int minIndex = 0;
        for (PlanElement element : person.getSelectedPlan().getPlanElements()) {
            if (element instanceof Activity) {
                Activity activity = (Activity) element;

                if (activity.getType().equals("home")) {
                    homeXY = activity.getCoord();
                }

            }

        }

        for (int i = 0; i < parkRideCoords.size(); i++) {
            distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), homeXY);
            if (minDist > distance) {
                minDist = distance;
                minIndex = i;
            }
        }

		/*
		for (int i = 0; i < parkRideCoords.size(); i++) {
			if (origActivity.getType().equals("home")) {
				distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), origActivity.getCoord());
				if (minDist > distance) {
					minDist = distance;
					minIndex = i;
				}
			}
			if (destActivity.getType().equals("home")) {
				distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), destActivity.getCoord());
				if (minDist > distance) {
					minDist = distance;
					minIndex = i;
				}
			}
		}
		*/
        Coord coord = new Coord(parkRideCoords.get(minIndex).getX(), parkRideCoords.get(minIndex).getY());
        Link prLink = NetworkUtils.getNearestLink(network, coord);

        Facility prFacility = new LinkWrapperFacility(prLink);

        return prFacility;
    }

    // Based on Facility location
    public Facility getParking(Person person, Facility fromFacility, Facility toFacility, Network network) {
        double minDist = 999999999.0;
        double distance = 0.0;
        Coord homeXY = new Coord(704835, 7060541);
        int minIndex = 0;
        for (PlanElement element : person.getSelectedPlan().getPlanElements()) {
            if (element instanceof Activity) {
                Activity activity = (Activity) element;

                if (activity.getType().equals("home")) {
                    homeXY = activity.getCoord();
                }

            }

        }

        for (int i = 0; i < parkRideCoords.size(); i++) {
            distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), homeXY);
            if (minDist > distance) {
                minDist = distance;
                minIndex = i;
            }
        }
        Coord coord = new Coord(parkRideCoords.get(minIndex).getX(), parkRideCoords.get(minIndex).getY());
        Link prLink = NetworkUtils.getNearestLink(network, coord);

        Facility prFacility = new LinkWrapperFacility(prLink);

        return prFacility;
    }

    // Based on a given location (Coord) and a person
    public Facility getParking(Person person, Coord coord, Network network) {
        double minDist = 999999999.0;
        double distance = 0.0;
        int minIndex = 0;
        for (int i = 0; i < parkRideCoords.size(); i++) {
            distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), coord);
            if (minDist > distance) {
                minDist = distance;
                minIndex = i;
            }
        }
        Coord coordParking = new Coord(parkRideCoords.get(minIndex).getX(), parkRideCoords.get(minIndex).getY());
        Link prLink = NetworkUtils.getNearestLink(network, coordParking);

        Facility prFacility = new LinkWrapperFacility(prLink);

        return prFacility;
    }

    // Based on a given location (Coord)
    public Facility getParking(Coord coord, Network network) {

        double minDist = 999999999.0;
        double distance = 0.0;
        int minIndex = 0;
        for (int i = 0; i < parkRideCoords.size(); i++) {
            distance = CoordUtils.calcEuclideanDistance(parkRideCoords.get(i), coord);
            if (minDist > distance) {
                minDist = distance;
                minIndex = i;
            }
        }
        Coord coordParking = new Coord(parkRideCoords.get(minIndex).getX(), parkRideCoords.get(minIndex).getY());
        Link prLink = NetworkUtils.getNearestLink(network, coordParking);

        Facility prFacility = new LinkWrapperFacility(prLink);

        return prFacility;
    }
}
