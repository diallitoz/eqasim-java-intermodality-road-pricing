package org.eqasim.core.simulation.mode_choice.utilities.predictors;

import java.util.ArrayList;
import java.util.List;

import org.eqasim.core.components.car_pt.routing.ParkRideManager;
import org.eqasim.core.components.car_pt.routing.ParkingFinder;
//import org.eqasim.core.components.transit.routing.EnrichedTransitRoute;
import org.eqasim.core.simulation.mode_choice.cost.CostModel;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.core.simulation.mode_choice.utilities.variables.PtCarVariables;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.router.RoutingModule;
import org.matsim.facilities.Facility;

import com.google.inject.Inject;
import com.google.inject.name.Named;

//import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.pt.routes.TransitPassengerRoute;

public class PtCarPredictor extends CachedVariablePredictor<PtCarVariables>{
    private final RoutingModule carRoutingModule;
    private final RoutingModule ptRoutingModule;
    private final CostModel carCostModel;
    private final CostModel ptCostModel;
    private final ModeParameters parameters;
    //private final List<Coord> parkRideCoords;
    private final Network network;
    private final PopulationFactory populationFactory;
    private final ParkRideManager parkRideMana;

    @Inject
    public PtCarPredictor(ModeParameters parameters, Network network, @Named("car") RoutingModule carRoutingModule,
                          @Named("pt") RoutingModule ptRoutingModule, PopulationFactory populationFactory, @Named("car") CostModel carCostModel,
                          @Named("pt") CostModel ptCostModel, ParkRideManager parkRideMana) {
        this.carCostModel = carCostModel;
        this.ptCostModel = ptCostModel;
        this.parameters = parameters;
        //this.parkRideCoords = parkRideCoords;
        this.network = network;
        this.carRoutingModule = carRoutingModule;
        this.ptRoutingModule = ptRoutingModule;
        this.populationFactory = populationFactory;
        this.parkRideMana = parkRideMana;
    }

    @Override
    public PtCarVariables predict(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        // double travelTime_min = ((Leg) elements.get(0)).getTravelTime() / 60.0;

        // Parking finder
        /*
        List<Coord> parkRideCoords = new ArrayList<Coord>();

        double[] xCoord = { 695217.09, 691365.43, 703543.53, 702770.20, 693929.84, 704530.69, 708963.08, 711811.05,
                685914.90, 712180.02, 702337.39, 709906.41 };

        double[] yCoord = { 7059186.19, 7065019.42, 7057923.10, 7056776.29, 7050511.72, 7057833.24, 7061460.64,
                7068903.84, 7047847.26, 7071112.37, 7049972.24, 7056430.63 };

        for (int i = 0; i < yCoord.length; i++) {
            Coord prCoord = new Coord(xCoord[i], yCoord[i]);
            parkRideCoords.add(prCoord);
        }

         */

        ParkingFinder prFinder = new ParkingFinder(parkRideMana.getCoordinates());
        //ParkingFinder prFinder = new ParkingFinder(parkRideCoords);
        Facility prkFacility = prFinder.getParking2(person, trip.getOriginActivity(), trip.getDestinationActivity(),
                network);

        // Creation of a pt leg from the PR facility to Destination
        Link fromLink = NetworkUtils.getNearestLink(network, trip.getOriginActivity().getCoord());
        Facility fromFacility = new LinkWrapperFacility(fromLink);

        List<? extends PlanElement> ptElements = ptRoutingModule.calcRoute(fromFacility, prkFacility,
                trip.getDepartureTime(), person);

        // "car_pt interaction" definition
        Activity car_pt = (Activity) populationFactory.createActivityFromCoord("car_pt interaction",
                prkFacility.getCoord());
        car_pt.setMaximumDuration(600);// 10 min
        car_pt.setLinkId(prkFacility.getLinkId());

        DiscreteModeChoiceTrip trip_pt = new DiscreteModeChoiceTrip(trip.getOriginActivity(), car_pt, "pt",
                ptElements, person.hashCode(),ptElements.get(0).hashCode(), 1000);

        int numberOfVehicularTrips = 0;
        boolean isFirstWaitingTime = true;

        // Track relevant variables
        double inVehicleTime_min = 0.0;
        double waitingTime_min = 0.0;
        double accessEgressTime_min_pt = 0.0;

        for (PlanElement element : ptElements) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;

                switch (leg.getMode()) {
                    case TransportMode.walk:
                    case TransportMode.non_network_walk:
                        accessEgressTime_min_pt += leg.getTravelTime().seconds() / 60.0;
                        break;
                    case TransportMode.transit_walk:
                        waitingTime_min += leg.getTravelTime().seconds() / 60.0;
                        break;
                    case TransportMode.pt:
                        TransitPassengerRoute route = (TransitPassengerRoute) leg.getRoute();

                        double departureTime = leg.getDepartureTime().seconds();
                        double waitingTime = route.getBoardingTime().seconds() - departureTime;
                        double inVehicleTime = leg.getTravelTime().seconds() - waitingTime;

                        inVehicleTime_min += inVehicleTime / 60.0;

                        if (!isFirstWaitingTime) {
                            waitingTime_min += waitingTime / 60.0;
                        } else {
                            isFirstWaitingTime = false;
                        }

                        numberOfVehicularTrips++;
                        break;
                    default:
                        throw new IllegalStateException("Unknown mode in PT trip: " + leg.getMode());
                }
            }
        }

        int numberOfLineSwitches = Math.max(0, numberOfVehicularTrips - 1);

        // Calculate cost
        double cost_MU_pt = carCostModel.calculateCost_MU(person, trip_pt, ptElements);

        double euclideanDistance_km_pt = PredictorUtils.calculateEuclideanDistance_km(trip_pt);

        // Creation of a car leg from PR facility to the Destination

        // We take 5 min to park the car and access to PT (transfer time)
        double timeToAccessPt = 5;
        double carDepartureTime = trip.getDepartureTime()
                + (inVehicleTime_min + waitingTime_min + accessEgressTime_min_pt + timeToAccessPt) * 60;
        Link toLink = NetworkUtils.getNearestLink(network, trip.getDestinationActivity().getCoord());
        Facility toFacility = new LinkWrapperFacility(toLink);

        List<? extends PlanElement> carElements = carRoutingModule.calcRoute(prkFacility, toFacility, carDepartureTime,
                null);
        if (carElements.size() > 1) {
            throw new IllegalStateException("We do not support multi-stage car trips yet.");
        }

        double vehicleTravelTime = Double.NaN;
        Leg leg_car = (Leg) carElements.get(0);
        vehicleTravelTime = leg_car.getRoute().getTravelTime().seconds() / 60.0 + parameters.car.constantParkingSearchPenalty_min;

        DiscreteModeChoiceTrip trip_car = new DiscreteModeChoiceTrip(car_pt, trip.getOriginActivity(), "car",
                carElements, person.hashCode(), leg_car.hashCode(),1000);
        double cost_MU_car = ptCostModel.calculateCost_MU(person, trip_car, carElements);

        double euclideanDistance_km_car = PredictorUtils.calculateEuclideanDistance_km(trip_car);
        double accessEgressTime_min_car = parameters.car.constantAccessEgressWalkTime_min;

        return new PtCarVariables(vehicleTravelTime, euclideanDistance_km_car, accessEgressTime_min_car, cost_MU_car,
                inVehicleTime_min, waitingTime_min, numberOfLineSwitches, euclideanDistance_km_pt,
                accessEgressTime_min_pt, cost_MU_pt);
    }
}
