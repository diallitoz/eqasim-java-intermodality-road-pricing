package org.eqasim.lille_metropolis.mode_choice.utilities.variables;

import org.eqasim.core.simulation.mode_choice.utilities.variables.BaseVariables;

public class MELCarRoadPricingVariables implements BaseVariables {
    // Toll
    final public double road_pricing_fee;


    public MELCarRoadPricingVariables(double road_pricing_fee) {
        this.road_pricing_fee = road_pricing_fee;
    }
}
