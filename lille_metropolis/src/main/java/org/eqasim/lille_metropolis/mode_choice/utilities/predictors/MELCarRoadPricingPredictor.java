package org.eqasim.lille_metropolis.mode_choice.utilities.predictors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eqasim.core.components.car_pt.routing.ParkRideManager;
import org.eqasim.core.components.car_pt.routing.ParkingFinder;
import org.eqasim.core.simulation.mode_choice.cost.CostModel;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.CachedVariablePredictor;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.PredictorUtils;
import org.eqasim.lille_metropolis.mode_choice.utilities.variables.MELCarRoadPricingVariables;
import org.geotools.data.FeatureReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.roadpricing.RoadPricingUtils;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.Facility;
import org.matsim.pt.routes.TransitPassengerRoute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MELCarRoadPricingPredictor extends CachedVariablePredictor<MELCarRoadPricingVariables> {

    private final Network network;
    private final PopulationFactory populationFactory;
    //private final ModeParameters parameters;


    @Inject
    public MELCarRoadPricingPredictor(Network network, PopulationFactory populationFactory) {

        this.network = network;
        this.populationFactory = populationFactory;
    }

    @Override
    public MELCarRoadPricingVariables predict(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        //Shape sans les PR autour de Lille
        //final String pathname_lille = "C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/shape/zone_peage_Lille_Mel/zone_peage.shp";
        final String pathname_lille = "/home/dialloaziseoumar/AziseThesis/Simulations/shape/zone_peage_Lille_Mel/zone_peage.shp";
        //final String pathname_lille = "C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/shape/lille_shp/lille_shp_file.shp";
        //final String pathname_lille_centre = "C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/shape/lille_centre/lille_centre.shp";
        final String pathname_lille_centre = "/home/dialloaziseoumar/AziseThesis/Simulations/shape/lille_centre/lille_centre.shp";

        FileDataStore store_lille = null, store_centre = null;

        //Cas de Lille ville entiere
        try {
            store_lille = FileDataStoreFinder.getDataStore(new File( pathname_lille ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FeatureReader<SimpleFeatureType, SimpleFeature> reader_lille = null;
        try {
            reader_lille = store_lille.getFeatureReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(pathname);

        List<SimpleFeature> features_lille = new ArrayList<>();

        if (reader_lille != null) {

                SimpleFeature result = null;
                try {
                    result = reader_lille.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                features_lille.add(result) ;
        }

        try {
            reader_lille.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        SimpleFeature lille = features_lille.get(0);

        Geometry polygon_lille_ville = (Geometry) lille.getAttributes().get(0);

        BoundingBox bb_lille_ville = lille.getBounds();

        //Fin Lille ville


        //Cas de Lille centre
        try {
            store_centre = FileDataStoreFinder.getDataStore(new File( pathname_lille_centre ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FeatureReader<SimpleFeatureType, SimpleFeature> reader_centre = null;
        try {
            reader_centre = store_centre.getFeatureReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(pathname);

        List<SimpleFeature> features_centre = new ArrayList<>();

        if (reader_centre != null) {

            SimpleFeature result_centre = null;
            try {
                result_centre = reader_centre.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
            features_centre.add(result_centre) ;
        }

        try {
            reader_centre.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        SimpleFeature lille_centre = features_centre.get(0);

        Geometry polygon_centre = (Geometry) lille_centre.getAttributes().get(0);

        BoundingBox bb_centre = lille_centre.getBounds();

        //Fin Lille centre


        final GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        //GeometryFactory gf = new GeometryFactory();

        Coord toolPoint = new Coord();

        double fee_toll = 0.0;


        //final Coordinate coordToNode = CoordUtils.createGeotoolsCoordinate( link.getToNode().getCoord() );

        final Coordinate coordOrigin = CoordUtils.createGeotoolsCoordinate(trip.getOriginActivity().getCoord());

        final Coordinate coordDestination = CoordUtils.createGeotoolsCoordinate(trip.getDestinationActivity().getCoord());

        Geometry pointOrigin = gf.createPoint(new Coordinate(coordOrigin.x,coordOrigin.y));

        Geometry pointDestination = gf.createPoint(new Coordinate(coordDestination.x,coordDestination.y));


        //TO DO: Add the time of the road pricing: 5h30-9h (330 - 540) and 15h30-19h (930 - 1140) according to the arrival time computed by using:
        Leg leg = (Leg) elements.get(0);

        double travelTime_min = leg.getTravelTime().seconds() / 60;

        //cas 1 application du road pricing zonale (aire) entre 5h-9h
        //if (travelTime_min >= 330 && travelTime_min <= 540){

            //Origine/Destination dans la ville de Lille
            if(polygon_lille_ville.contains(pointOrigin) || polygon_lille_ville.contains(pointDestination)){
                toolPoint = trip.getDestinationActivity().getCoord();
                Activity toll_activity = (Activity) populationFactory.createActivityFromCoord("toll interaction",
                        toolPoint);
                toll_activity.setMaximumDuration(10);// 10 s

                fee_toll = 17.5;// 20 euros
            }
        //}

        return new MELCarRoadPricingVariables(fee_toll);
    }
}
