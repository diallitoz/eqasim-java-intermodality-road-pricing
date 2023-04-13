package org.eqasim.lille_metropolis;

import org.eqasim.core.components.car_pt.routing.EqasimCarPtModule;
import org.eqasim.core.components.car_pt.routing.EqasimPtCarModule;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModuleCarPt;
import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModuleCarPt;
import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModuleRoadPricing;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RunSimulationCarPtRoadPricing {
    static public void main(String[] args) throws ConfigurationException {
        CommandLine cmd = new CommandLine.Builder(args) //
                .requireOptions("config-path") //
                .allowPrefixes("mode-choice-parameter", "cost-parameter") //
                .build();

        // Global config definition
        Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"), MELConfigurator.getConfigGroups());
        cmd.applyConfiguration(config);

        // Park and ride lot location
        List<Coord> parkRideCoords = new ArrayList<Coord>();

        double[] xCoord = { 695217.09, 691365.43, 703543.53, 702770.20, 693929.84, 704530.69, 708963.08, 711811.05,
                685914.90, 712180.02, 702337.39, 709906.41 };

        double[] yCoord = { 7059186.19, 7065019.42, 7057923.10, 7056776.29, 7050511.72, 7057833.24, 7061460.64,
                7068903.84, 7047847.26, 7071112.37, 7049972.24, 7056430.63 };

        /*
         * double Lat = 50.62964, Long = 2.93251586; X = 695217.09, Y = 7059186.19;
         * 50.6819458 2.878044; X = 691365.43, Y = 7065019.42; 50.6183128 3.049986 X =
         * 703543.53, Y = 7057923.10 50.6080246 3.03906918 X = 702770.20, Y = 7056776.29
         * 50.5517769 2.914486 X = 693929.84, Y = 7050511.72 50.6175 3.06391 X =
         * 704530.69, Y = 7057833.24 50.6500053 3.12651539 X = 708963.08, Y = 7061460.64
         * 50.71675 3.166937 X = 711811.05, Y = 7068903.84 50.5277328 2.80166936 X =
         * 685914.90, Y = 7047847.26 50.73656 3.17222 X = 712180.02, Y = 7071112.37
         * 50.54696 3.032925 X = 702337.39, Y = 7049972.24 50.60485 3.139705 X =
         * 709906.41, Y = 7056430.63
         */

        for (int i = 0; i < yCoord.length; i++) {
            Coord prCoord = new Coord(xCoord[i], yCoord[i]);
            parkRideCoords.add(prCoord);
        }

        // Eqasim config definition to add the mode car_pt estimation
        EqasimConfigGroup eqasimConfig = EqasimConfigGroup.get(config);
        eqasimConfig.setEstimator("car_pt", "CarPtUtilityEstimator");
        eqasimConfig.setEstimator("pt_car", "PtCarUtilityEstimator");

        // Scoring config definition to add the mode cat_pt parameters
        PlanCalcScoreConfigGroup scoringConfig = config.planCalcScore();
        ModeParams carPtParams = new ModeParams("car_pt");
        ModeParams ptCarParams = new ModeParams("pt_car");
        scoringConfig.addModeParams(carPtParams);
        scoringConfig.addModeParams(ptCarParams);

        // "car_pt interaction" definition
        ActivityParams paramsCarPtInterAct = new ActivityParams("carPt interaction");
        paramsCarPtInterAct.setTypicalDuration(100.0);
        paramsCarPtInterAct.setScoringThisActivityAtAll(false);

        // "pt_car interaction" definition
        ActivityParams paramsPtCarInterAct = new ActivityParams("ptCar interaction");
        paramsPtCarInterAct.setTypicalDuration(100.0);
        paramsPtCarInterAct.setScoringThisActivityAtAll(false);

        // Adding "car_pt interaction" to the scoring
        scoringConfig.addActivityParams(paramsCarPtInterAct);
        scoringConfig.addActivityParams(paramsPtCarInterAct);

        // DMC config definition
        DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
                .get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

        // Adding the mode "car_pt" and "pt_car" to CachedModes
        Collection<String> cachedModes = new HashSet<>(dmcConfig.getCachedModes());
        cachedModes.add("car_pt");
        cachedModes.add("pt_car");
        dmcConfig.setCachedModes(cachedModes);

        // Activation of constraint intermodal modes Using
        Collection<String> tourConstraints = new HashSet<>(dmcConfig.getTourConstraints());
        //tourConstraints.add("VehicleTourConstraintWithCar_Pt");
        tourConstraints.add("IntermodalModesConstraint");
        dmcConfig.setTourConstraints(tourConstraints);


        for (StrategySettings strategy : config.strategy().getStrategySettings()) {
            if(strategy.getName().equals("DiscreteModeChoice")) {
                strategy.setWeight(10000000.0);
            }
        }


        Scenario scenario = ScenarioUtils.createScenario(config);
        MELConfigurator.configureScenario(scenario);
        ScenarioUtils.loadScenario(scenario);

        Controler controller = new Controler(scenario);
        MELConfigurator.configureController(controller);
        controller.addOverridingModule(new EqasimAnalysisModule());
        controller.addOverridingModule(new EqasimModeChoiceModuleCarPt());
        controller.addOverridingModule(new MELModeChoiceModuleRoadPricing(cmd, scenario.getPopulation().getFactory()));
        controller.addOverridingModule(new MELModeChoiceModuleCarPt(cmd, parkRideCoords,scenario.getNetwork(), scenario.getPopulation().getFactory()));
        controller.addOverridingModule(new EqasimCarPtModule(parkRideCoords));
        controller.addOverridingModule(new EqasimPtCarModule(parkRideCoords));
        controller.run();
    }

}
