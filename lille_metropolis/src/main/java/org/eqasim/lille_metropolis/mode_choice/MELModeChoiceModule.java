package org.eqasim.lille_metropolis.mode_choice;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.ParameterDefinition;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.matsim.core.config.CommandLine;
import org.eqasim.lille_metropolis.mode_choice.costs.MELCarCostModel;
import org.eqasim.lille_metropolis.mode_choice.costs.MELPtCostModel;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELCostParameters;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELModeParameters;
import org.eqasim.lille_metropolis.mode_choice.utilities.estimators.MELBikeUtilityEstimator;
import org.eqasim.lille_metropolis.mode_choice.utilities.estimators.MELCarUtilityEstimator;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELPersonPredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELSpatialPredictor;

import java.io.File;
import java.io.IOException;

public class MELModeChoiceModule extends AbstractEqasimExtension {
    private final CommandLine commandLine;

    public static final String MODE_AVAILABILITY_NAME = "MELModeAvailability";

    public static final String CAR_COST_MODEL_NAME = "MELCarCostModel";
    public static final String PT_COST_MODEL_NAME = "MELPtCostModel";

    public static final String CAR_ESTIMATOR_NAME = "MELCarUtilityEstimator";
    public static final String BIKE_ESTIMATOR_NAME = "MELBikeUtilityEstimator";

    public MELModeChoiceModule(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    protected void installEqasimExtension() {


        bindModeAvailability(MODE_AVAILABILITY_NAME).to(MELModeAvailability.class);

        bind(MELPersonPredictor.class);

        bindCostModel(CAR_COST_MODEL_NAME).to(MELCarCostModel.class);
        bindCostModel(PT_COST_MODEL_NAME).to(MELPtCostModel.class);

        bindUtilityEstimator(CAR_ESTIMATOR_NAME).to(MELCarUtilityEstimator.class);
        bindUtilityEstimator(BIKE_ESTIMATOR_NAME).to(MELBikeUtilityEstimator.class);
        bind(MELSpatialPredictor.class);

        bind(ModeParameters.class).to(MELModeParameters.class);
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
}
