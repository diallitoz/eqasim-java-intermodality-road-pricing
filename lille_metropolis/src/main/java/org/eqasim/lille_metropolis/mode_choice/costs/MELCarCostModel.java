package org.eqasim.lille_metropolis.mode_choice.costs;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.cost.AbstractCostModel;
import org.eqasim.lille_metropolis.mode_choice.parameters.MELCostParameters;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

import com.google.inject.Inject;

public class MELCarCostModel extends AbstractCostModel{
    private final MELCostParameters costParameters;

    @Inject
    public MELCarCostModel(MELCostParameters costParameters) {
        super("car");
        this.costParameters = costParameters;
    }

    @Override
    public double calculateCost_MU(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        return costParameters.carCost_EUR_km * getInVehicleDistance_km(elements);
    }
}
