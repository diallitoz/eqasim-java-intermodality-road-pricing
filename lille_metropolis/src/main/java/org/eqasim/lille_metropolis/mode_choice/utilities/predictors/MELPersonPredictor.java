package org.eqasim.lille_metropolis.mode_choice.utilities.predictors;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.utilities.predictors.CachedVariablePredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.variables.MELPersonVariables;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class MELPersonPredictor extends CachedVariablePredictor<MELPersonVariables> {
    @Override
    protected MELPersonVariables predict(Person person, DiscreteModeChoiceTrip trip,
                                         List<? extends PlanElement> elements) {
        boolean hasSubscription = MELPredictorUtils.hasSubscription(person);
        return new MELPersonVariables(hasSubscription);
    }
}
