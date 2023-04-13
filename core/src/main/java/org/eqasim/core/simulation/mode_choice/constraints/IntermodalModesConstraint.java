package org.eqasim.core.simulation.mode_choice.constraints;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

//import org.eqasim.core.components.car_pt.routing.ParkingFinder;
import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.facilities.Facility;

import org.matsim.contribs.discrete_mode_choice.components.utils.LocationUtils;
import org.matsim.contribs.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.contribs.discrete_mode_choice.model.tour_based.TourCandidate;
import org.matsim.contribs.discrete_mode_choice.model.tour_based.TourConstraint;
import org.matsim.contribs.discrete_mode_choice.model.tour_based.TourConstraintFactory;

public class IntermodalModesConstraint implements TourConstraint{
    private final Collection<String> restrictedModes;
    private final Id<? extends BasicLocation> homeLocationId;
    private final List<Coord> parkRideCoords;
    private final Network network;

    public IntermodalModesConstraint(Collection<String> restrictedModes, Id<? extends BasicLocation> homeLocationId,
                                     List<Coord> parkRideCoords, Network network) {
        this.restrictedModes = restrictedModes;
        this.homeLocationId = homeLocationId;
        this.parkRideCoords = parkRideCoords;
        this.network = network;
    }

    private int getFirstIndex(String mode, List<String> modes) {
        for (int i = 0; i < modes.size(); i++) {
            if (modes.get(i).equals(mode)) {
                return i;
            }
        }

        return -1;
    }

    private int getLastIndex(String mode, List<String> modes) {
        for (int i = modes.size() - 1; i >= 0; i--) {
            if (modes.get(i).equals(mode)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean validateBeforeEstimation(List<DiscreteModeChoiceTrip> tour, List<String> modes,
                                            List<List<String>> previousModes) {

        boolean found_car_pt = false;
        boolean found_pt_car = false;

        // checking car_pt and pt_car in the list of possible modes to be used
        /*
         * for (String mode : modes) { if (mode.equals("pt_car")) { found_pt_car = true;
         * }
         *
         * if (mode.equals("car_pt")) { if (!found_pt_car) { return false; }
         *
         * found_pt_car = false; } }
         */
        for (String mode : modes) {
            if (mode.equals("car_pt")) {
                found_car_pt = true;
            }

            if (mode.equals("pt_car")) {
                if (!found_car_pt) {
                    return false;
                }

                found_car_pt = false;
            }
        }

        if (found_car_pt) {
            return false;
        }

        Id<? extends BasicLocation> latestCarPtOriginId = null;
        Facility prkFacilityOrig = null;
        Facility prkFacilityDest = null;
        Activity intermodalInteractionGoing = null;
        Activity intermodalInteractionComing = null;
        for (int i = 0; i < tour.size(); i++) {

            if (modes.get(i).equals("car_pt")) {
                latestCarPtOriginId = LocationUtils.getLocationId(tour.get(i).getOriginActivity());

                if(!tour.get(i).getOriginActivity().getType().equals("home")) {
                    return false;
                }

                //ParkingFinder prFinderOri = new ParkingFinder(parkRideCoords);

                //prkFacilityOrig = prFinderOri.getParking(tour.get(i).getOriginActivity().getCoord(), network);

                //Link prLink = NetworkUtils.getNearestLink(network, prkFacilityOri.getCoord());
                //intermodalInteractionGoing = PopulationUtils.createActivityFromCoordAndLinkId(
                //		"intermodal interaction going", prkFacilityOri.getCoord(), prLink.getId());

                // To do parking location constraint
                // if
                // (!latestCarPtOriginId.equals(LocationUtils.getLocationId(intermodalInteraction)))
                // {
                // return false;
                // }

            }

            if (modes.get(i).equals("pt_car")) {
                Id<? extends BasicLocation> currentLocationId = LocationUtils
                        .getLocationId(tour.get(i).getDestinationActivity());

                // Checking for Origin of car_pt and destination of pt_car
                //if (!latestCarPtOriginId.equals(currentLocationId)) {
                //	return false;
                //}

                if(!tour.get(i).getDestinationActivity().getType().equals("home"))
                    return false;

                // Checking for parking plot location according to the location of the origin
                // activity of car_pt and the destination activitty of pt_car
                //ParkingFinder prFinderDest = new ParkingFinder(parkRideCoords);

                //prkFacilityDest = prFinderDest.getParking(tour.get(i).getDestinationActivity().getCoord(),
                //		network);

                //Link prLink = NetworkUtils.getNearestLink(network, prkFacilityDest.getCoord());

                //intermodalInteractionComing = PopulationUtils.createActivityFromCoordAndLinkId(
                //		"intermodal interaction coming", prkFacilityDest.getCoord(), prLink.getId());

                // To do parking location constraint
                //if (!prkFacilityOrig.getCoord().equals(prkFacilityDest.getCoord())){
                //	return false;
                //}

            }
        }

        return true;
    }

    @Override
    public boolean validateAfterEstimation(List<DiscreteModeChoiceTrip> tour, TourCandidate candidate,
                                           List<TourCandidate> previousCandidates) {
        return true;
    }

    public static class Factory implements TourConstraintFactory {
        private final Collection<String> restrictedModes;
        private final HomeFinder homeFinder;
        private final List<Coord> parkRideCoords;
        private final Network network;

        public Factory(Collection<String> restrictedModes, HomeFinder homeFinder, List<Coord> parkRideCoords,
                       Network network) {
            this.restrictedModes = restrictedModes;
            this.homeFinder = homeFinder;
            this.parkRideCoords = parkRideCoords;
            this.network = network;
        }

        @Override
        public TourConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> planTrips,
                                               Collection<String> availableModes) {
            return new IntermodalModesConstraint(restrictedModes, homeFinder.getHomeLocationId(planTrips),
                    parkRideCoords, network);
        }
    }

}
