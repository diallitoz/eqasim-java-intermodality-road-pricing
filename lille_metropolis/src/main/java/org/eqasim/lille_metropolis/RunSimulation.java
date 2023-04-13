package org.eqasim.lille_metropolis;

import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.lille_metropolis.mode_choice.MELModeChoiceModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

public class RunSimulation {
    static public void main(String[] args) throws ConfigurationException {
        CommandLine cmd = new CommandLine.Builder(args) //
                .requireOptions("config-path") //
                .allowPrefixes("mode-choice-parameter", "cost-parameter") //
                .build();

        Config config = ConfigUtils.loadConfig(cmd.getOptionStrict("config-path"), MELConfigurator.getConfigGroups());
        cmd.applyConfiguration(config);

        Scenario scenario = ScenarioUtils.createScenario(config);
        MELConfigurator.configureScenario(scenario);
        ScenarioUtils.loadScenario(scenario);

        Controler controller = new Controler(scenario);
        MELConfigurator.configureController(controller);
        controller.addOverridingModule(new EqasimAnalysisModule());
        controller.addOverridingModule(new EqasimModeChoiceModule());
        controller.addOverridingModule(new MELModeChoiceModule(cmd));
        controller.run();
    }

}
