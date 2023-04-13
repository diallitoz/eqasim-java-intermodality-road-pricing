package org.eqasim.lille_metropolis;

import org.eqasim.core.components.road_pricing.RoadPricing;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModule;

import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModuleRoadPricing;
import org.geotools.data.FeatureReader;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.roadpricing.*;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.locationtech.jts.geom.Envelope;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
//import org.opengis.geometry.Geometry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class RunSimulationRoadPricing {
    static public void main(String[] args) throws ConfigurationException, IOException {

        CommandLine cmd = new CommandLine.Builder(args) //
                .requireOptions("config-path") //
                .allowPrefixes("mode-choice-parameter", "cost-parameter") //
                .build();

        Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"), MELConfigurator.getConfigGroups());
        //config.addModule(new RoadPricingConfigGroup());
        cmd.applyConfiguration(config);

        PlanCalcScoreConfigGroup scoringConfig = config.planCalcScore();

        // "car_pt interaction" definition
        PlanCalcScoreConfigGroup.ActivityParams toll_interaction = new PlanCalcScoreConfigGroup.ActivityParams("toll interaction");
        toll_interaction.setTypicalDuration(100.0);
        toll_interaction.setScoringThisActivityAtAll(false);

        scoringConfig.addActivityParams(toll_interaction);

        Scenario scenario = ScenarioUtils.createScenario(config);
        MELConfigurator.configureScenario(scenario);
        ScenarioUtils.loadScenario(scenario);

    /*
        RoadPricingSchemeImpl scheme = RoadPricingUtils.createAndRegisterMutableScheme(scenario);
        RoadPricingUtils.setType( scheme, "link" );



        //Envelope env = new Envelope( 700806, 707671, 7062556, 7058065 );
        final String pathname = "C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/shape/lille_shp/lille_shp_file.shp";
        FileDataStore store = FileDataStoreFinder.getDataStore(new File( pathname ));
        FeatureReader<SimpleFeatureType,SimpleFeature> reader = store.getFeatureReader();

        //Collection<SimpleFeature> features = ShapeFileReader.getAllFeatures(pathname);

        List<SimpleFeature> features = new ArrayList<>();

        for( ; reader.hasNext() ; ) {
            SimpleFeature result = reader.next();
            features.add(result) ;
        }

        reader.close();

        //System.out.println(features.get(0));


        //for ( SimpleFeature feature : features ) {
        //    System.out.println( feature );
        //    for ( Object attrib : feature.getAttributes() ) {
        //        System.out.println( attrib );
        //    }
        //}
*/
        /*

        SimpleFeature lille = features.get(0);

        final GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        //GeometryFactory gf = new GeometryFactory();

        Geometry polygon = (Geometry) lille.getAttributes().get(0);

        BoundingBox bb = lille.getBounds();

        /*
        FileWriter out = new FileWriter(new File("C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/shape/lille_shp/out-advanced.csv"));
        out.write("X;Y;||\n");
        for (int i = 0; i < 1000; i++) {
            double xx = bb.getMinX() + Math.random() * (bb.getMaxX() - bb.getMinX());
            double yy = bb.getMinY() + Math.random() * (bb.getMaxY() - bb.getMinY());
            Geometry point = gf.createPoint(new Coordinate(xx,yy));

            if(polygon.contains(point)){
                out.write(xx+";"+yy+";"+i+"\n");
            }
        }
        out.close();

         */
        /*

        for( Link link : scenario.getNetwork().getLinks().values() ){
            final Coordinate coordToNode = CoordUtils.createGeotoolsCoordinate( link.getToNode().getCoord() );
            //if ( env.contains( coord ) ) {
            //    RoadPricingUtils.addLink( scheme, link.getId() );
            //    RoadPricingUtils.addLinkSpecificCost(scheme,link.getId(),0.,999999.,10.);
            //}

            //for( SimpleFeature feature : features ){
            //    if(feature.getType().equals("Polygon")){
            //        Geometry polygon = (Geometry) feature.getDefaultGeometry();
            //        if ( polygon.contains((DirectPosition) gf.createPoint(coord))) {
            //            RoadPricingUtils.addLink( scheme, link.getId() );
            //            RoadPricingUtils.addLinkSpecificCost(scheme,link.getId(),0.,999999.,10.);
            //        }
            //    }
            //}

            Geometry point = gf.createPoint(new Coordinate(coordToNode.x,coordToNode.y));

            if(polygon.contains(point)){
                RoadPricingUtils.addLink( scheme, link.getId() );
                //RoadPricingUtils.addLinkSpecificCost(scheme,link.getId(),18000.,32400.,10.);//morning toll: 5-9 am
                RoadPricingUtils.addLinkSpecificCost(scheme,link.getId(),54000.,64800.0,999999.);//evining toll : 3-6 pm
            }
        }

        new RoadPricingWriterXMLv1(scheme).writeFile("C:/Users/azise.oumar.diallo/Documents/AziseThesis/GenerationPopulationSynthetique/MEL_Simulations/roadpricing/lilleToll.xml.gz");
*/


        Controler controller = new Controler(scenario);
        MELConfigurator.configureController(controller);
        controller.addOverridingModule(new EqasimAnalysisModule());
        controller.addOverridingModule(new EqasimModeChoiceModule());
        controller.addOverridingModule(new MELModeChoiceModuleRoadPricing(cmd, scenario.getPopulation().getFactory()));
        //controller.addOverridingModule(new RoadPricingModule());
        //RoadPricing.configure(controller);
        controller.run();

    }

}
