package org.eqasim.lille_metropolis.mode_choice;

import java.util.List;


import org.eqasim.lille_metropolis.mode_choice.utilities.estimators.MELCarUtilityEstimatorWithRoadPricing;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELCarRoadPricingPredictor;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.*;
import org.matsim.api.core.v01.network.Network;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.ParameterDefinition;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.lille_metropolis.mode_choice.costs.MELCarCostModel;
import org.eqasim.lille_metropolis.mode_choice.costs.MELPtCostModel;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELCostParameters;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELModeParameters;
import org.eqasim.lille_metropolis.mode_choice.utilities.estimators.MELBikeUtilityEstimator;
import org.eqasim.lille_metropolis.mode_choice.utilities.estimators.MELCarUtilityEstimator;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELPersonPredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELSpatialPredictor;

import org.matsim.contribs.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.contribs.discrete_mode_choice.modules.config.VehicleTourConstraintConfigGroup;

import org.matsim.core.config.CommandLine;
import org.matsim.core.population.routes.RouteFactories;
import org.matsim.facilities.ActivityFacility;

import org.eqasim.core.components.car_pt.routing.ParkRideManager;
import org.eqasim.core.simulation.mode_choice.utilities.estimators.CarPtUtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.estimators.PtCarUtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.CarPtPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.PtCarPredictor;

import org.eqasim.core.analysis.CarPtEventHandler;

import org.eqasim.core.simulation.mode_choice.constraints.IntermodalModesConstraint;
//import org.eqasim.core.simulation.mode_choice.constraints.VehicleTourConstraintWithCar_Pt;

import java.io.File;
import java.io.IOException;

public class MELModeChoiceModuleCarPt extends AbstractEqasimExtension {
    private final CommandLine commandLine;

    public static final String MODE_AVAILABILITY_NAME = "MELModeAvailabilityCarPt";

    public static final String CAR_COST_MODEL_NAME = "MELCarCostModel";
    public static final String PT_COST_MODEL_NAME = "MELPtCostModel";

    public static final String CAR_ESTIMATOR_NAME = "MELCarUtilityEstimator";
    public static final String BIKE_ESTIMATOR_NAME = "MELBikeUtilityEstimator";

    public final List<Coord> parkRideCoords;
    public final Network network;
    private final PopulationFactory populationFactory ;

    public MELModeChoiceModuleCarPt(CommandLine commandLine, List<Coord> parkRideCoords, Network network,
                                    PopulationFactory populationFactory) {
        this.commandLine = commandLine;
        this.parkRideCoords = parkRideCoords;
        this.network = network;
        this.populationFactory = populationFactory;
    }

    @Override
    protected void installEqasimExtension() {
        bindModeAvailability(MODE_AVAILABILITY_NAME).to(MELModeAvailabilityCarPt.class);

        bind(MELPersonPredictor.class);

        bindCostModel(CAR_COST_MODEL_NAME).to(MELCarCostModel.class);
        bindCostModel(PT_COST_MODEL_NAME).to(MELPtCostModel.class);

        bindUtilityEstimator(CAR_ESTIMATOR_NAME).to(MELCarUtilityEstimatorWithRoadPricing.class);
        bindUtilityEstimator(BIKE_ESTIMATOR_NAME).to(MELBikeUtilityEstimator.class);

        bind(MELCarRoadPricingPredictor.class);

        // Register the estimator
        bindUtilityEstimator("car_pt").to(CarPtUtilityEstimator.class);
        bindUtilityEstimator("pt_car").to(PtCarUtilityEstimator.class);


        bind(MELSpatialPredictor.class);

        // Register the predictor
        bind(ParkRideManager.class);
        bind(CarPtPredictor.class);
        bind(PtCarPredictor.class);

        bind(ModeParameters.class).to(MELModeParameters.class);

        // Constraint register
        bindTourConstraintFactory("IntermodalModesConstraint").to(IntermodalModesConstraint.Factory.class);

        // Intermodal count
        addEventHandlerBinding().to(CarPtEventHandler.class);
    }

    @Provides
    @Singleton
    public MELModeParameters provideModeChoiceParameters(EqasimConfigGroup config)
            throws IOException, CommandLine.ConfigurationException {
        MELModeParameters parameters = MELModeParameters.buildDefault();

        if (config.getModeParametersPath() != null) {
            ParameterDefinition.applyFile(new File(config.getModeParametersPath()), parameters);
        }

        ParameterDefinition.applyCommandLine("mode-choice-parameter", commandLine, parameters);
        return parameters;
    }

    @Provides
    @Singleton
    public MELCostParameters provideCostParameters(EqasimConfigGroup config) {
        MELCostParameters parameters = MELCostParameters.buildDefault();

        if (config.getCostParametersPath() != null) {
            ParameterDefinition.applyFile(new File(config.getCostParametersPath()), parameters);
        }

        ParameterDefinition.applyCommandLine("cost-parameter", commandLine, parameters);
        return parameters;
    }

    @Provides
    @Singleton
    public IntermodalModesConstraint.Factory provideIntermodalModesConstraintFactory(
            DiscreteModeChoiceConfigGroup dmcConfig, HomeFinder homeFinder) {
        VehicleTourConstraintConfigGroup config = dmcConfig.getVehicleTourConstraintConfig();
        return new IntermodalModesConstraint.Factory(config.getRestrictedModes(), homeFinder, parkRideCoords, network);
    }
}
