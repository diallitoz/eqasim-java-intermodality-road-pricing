package org.eqasim.core.components.road_pricing;

import org.matsim.core.controler.AllowsConfiguration;
import org.matsim.contrib.roadpricing.*;

public final class RoadPricing {
    private RoadPricing(){

    }

    public static void configure(AllowsConfiguration ao){
        ao.addOverridingModule(new RoadPricingModule());
    }
}
