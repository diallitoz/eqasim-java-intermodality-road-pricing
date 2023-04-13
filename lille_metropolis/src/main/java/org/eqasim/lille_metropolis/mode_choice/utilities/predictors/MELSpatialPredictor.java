package org.eqasim.lille_metropolis.mode_choice.utilities.predictors;
import java.util.List;

import org.eqasim.core.simulation.mode_choice.utilities.predictors.CachedVariablePredictor;
import org.eqasim.lille_metropolis.mode_choice.utilities.variables.MELSpatialVariables;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

import com.google.inject.Singleton;

public class MELSpatialPredictor extends CachedVariablePredictor<MELSpatialVariables>{
    @Override
    protected MELSpatialVariables predict(Person person, DiscreteModeChoiceTrip trip,
                                          List<? extends PlanElement> elements) {
        boolean hasUrbanOrigin = MELPredictorUtils.isUrbanArea(trip.getOriginActivity());
        boolean hasUrbanDestination = MELPredictorUtils.isUrbanArea(trip.getDestinationActivity());

        return new MELSpatialVariables(hasUrbanOrigin, hasUrbanDestination);
    }
}
