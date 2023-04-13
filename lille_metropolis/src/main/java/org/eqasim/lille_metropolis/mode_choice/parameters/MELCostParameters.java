package org.eqasim.lille_metropolis.mode_choice.parameters;

import org.eqasim.core.simulation.mode_choice.ParameterDefinition;

public class MELCostParameters implements ParameterDefinition{
    public double carCost_EUR_km = 0.0;

    public static MELCostParameters buildDefault() {
        MELCostParameters parameters = new MELCostParameters();

        parameters.carCost_EUR_km = 0.15;

        return parameters;
    }
}
