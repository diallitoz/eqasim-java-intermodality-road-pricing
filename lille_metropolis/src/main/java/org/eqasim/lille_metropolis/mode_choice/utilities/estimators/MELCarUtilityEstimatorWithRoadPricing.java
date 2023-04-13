package org.eqasim.lille_metropolis.mode_choice.utilities.estimators;

import com.google.inject.Inject;
import org.eqasim.core.simulation.mode_choice.utilities.estimators.CarUtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.CarPredictor;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELModeParameters;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELCarRoadPricingPredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.predictors.MELSpatialPredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.variables.MELCarRoadPricingVariables;
import org.eqasim.lille_metropolis.mode_choice.utilities.variables.MELSpatialVariables;
import org.matsim.api.core.v01.events.PersonMoneyEvent;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

import java.util.List;

public class MELCarUtilityEstimatorWithRoadPricing extends CarUtilityEstimator{
    private final MELModeParameters parameters;
    private final MELSpatialPredictor spatialPredictor;
    //private final PersonMoneyEvent personMoneyEvent;
    private final MELCarRoadPricingPredictor melCarRoadPricingPredictor;

    @Inject
    public MELCarUtilityEstimatorWithRoadPricing(MELModeParameters parameters, MELSpatialPredictor spatialPredictor,
                                                 CarPredictor carPredictor, MELCarRoadPricingPredictor melCarRoadPricingPredictor) {
        super(parameters, carPredictor);
        //super(parameters, carPredictor);
        this.parameters = parameters;
        this.spatialPredictor = spatialPredictor;
        this.melCarRoadPricingPredictor = melCarRoadPricingPredictor;
        //this.personMoneyEvent = personMoneyEvent;
    }

    protected double estimateUrbanUtility(MELSpatialVariables variables) {
        double utility = 0.0;

        if (variables.hasUrbanOrigin && variables.hasUrbanDestination) {
            utility += parameters.idfCar.betaInsideUrbanArea;
        }

        if (variables.hasUrbanOrigin || variables.hasUrbanDestination) {
            utility += parameters.idfCar.betaCrossingUrbanArea;
        }

        return utility;
    }

    protected double estimateRoadPricing(MELCarRoadPricingVariables variables) {
        double utility = 0.0;

        utility += parameters.betaCost_u_MU * variables.road_pricing_fee;

        return utility;
    }

    @Override
    public double estimateUtility(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        MELSpatialVariables variables = spatialPredictor.predictVariables(person, trip, elements);
        MELCarRoadPricingVariables melCarRoadPricingVariables = melCarRoadPricingPredictor.predictVariables(person, trip, elements);

        double utility = 0.0;

        utility += super.estimateUtility(person, trip, elements);
        utility += estimateUrbanUtility(variables);
        utility += estimateRoadPricing(melCarRoadPricingVariables);

        return utility;
    }
}
