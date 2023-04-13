package org.eqasim.lille_metropolis.scenario;

import org.eqasim.core.components.config.ConfigAdapter;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.lille_metropolis.MELConfigurator;
import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModule;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;

public class RunAdaptConfig {
    static public void main(String[] args) throws ConfigurationException {
        ConfigAdapter.run(args, MELConfigurator.getConfigGroups(), RunAdaptConfig::adaptConfiguration);
    }

    static public void adaptConfiguration(Config config) {
        // Adjust eqasim config
        EqasimConfigGroup eqasimConfig = EqasimConfigGroup.get(config);

        eqasimConfig.setCostModel(TransportMode.car, MELModeChoiceModule.CAR_COST_MODEL_NAME);
        eqasimConfig.setCostModel(TransportMode.pt, MELModeChoiceModule.PT_COST_MODEL_NAME);

        eqasimConfig.setEstimator(TransportMode.car, MELModeChoiceModule.CAR_ESTIMATOR_NAME);
        eqasimConfig.setEstimator(TransportMode.bike, MELModeChoiceModule.BIKE_ESTIMATOR_NAME);

        DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
                .get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

        dmcConfig.setModeAvailability(MELModeChoiceModule.MODE_AVAILABILITY_NAME);

        // Calibration results for 5%

        if (eqasimConfig.getSampleSize() == 0.05) {
            // Adjust flow and storage capacity
            config.qsim().setFlowCapFactor(0.045);
            config.qsim().setStorageCapFactor(0.045);
        }
    }
}
