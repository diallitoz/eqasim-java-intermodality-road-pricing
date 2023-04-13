package org.eqasim.core.simulation.mode_choice.utilities.estimators;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.core.simulation.mode_choice.utilities.UtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.PersonPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.predictors.PtCarPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.variables.PtCarVariables;
//import org.eqasim.core.simulation.mode_choice.utilities.variables.PersonVariables;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;

import com.google.inject.Inject;

//import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class PtCarUtilityEstimator implements UtilityEstimator {
    private final ModeParameters parameters;
    private final PtCarPredictor ptCarPredictor;
    // private final PersonPredictor personPredictor;

    @Inject
    public PtCarUtilityEstimator(ModeParameters parameters,
                                 PtCarPredictor ptCarPredictor) {
        this.parameters = parameters;
        this.ptCarPredictor = ptCarPredictor;
        // this.personPredictor = personPredictor;
    }

    protected double estimateConstantUtility() {
        //return parameters.car.alpha_u + parameters.pt.alpha_u;
        return 1.25;

    }

    protected double estimateTravelTimeUtility(PtCarVariables variables) {
        return parameters.car.betaTravelTime_u_min * variables.travelTime_min;
    }

    protected double estimateAccessEgressTimeUtility(PtCarVariables variables) {
        return parameters.walk.betaTravelTime_u_min * variables.accessEgressTime_min_car
                + parameters.pt.betaAccessEgressTime_u_min * variables.accessEgressTime_min_pt;
    }

    protected double estimateInVehicleTimeUtility(PtCarVariables variables) {
        return parameters.pt.betaInVehicleTime_u_min * variables.inVehicleTime_min;
    }

    protected double estimateWaitingTimeUtility(PtCarVariables variables) {
        return parameters.pt.betaWaitingTime_u_min * variables.waitingTime_min;
    }

    protected double estimateLineSwitchUtility(PtCarVariables variables) {
        return parameters.pt.betaLineSwitch_u * variables.numberOfLineSwitches;
    }

    protected double estimateMonetaryCostUtility(PtCarVariables variables) {
        return parameters.betaCost_u_MU
                * EstimatorUtils.interaction(variables.euclideanDistance_km_car,
                parameters.referenceEuclideanDistance_km, parameters.lambdaCostEuclideanDistance)
                * variables.cost_MU_car
                + parameters.betaCost_u_MU
                * EstimatorUtils.interaction(variables.euclideanDistance_km_pt,
                parameters.referenceEuclideanDistance_km, parameters.lambdaCostEuclideanDistance)
                * variables.cost_MU_pt;
    }

    @Override
    public double estimateUtility(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        PtCarVariables variables = ptCarPredictor.predictVariables(person, trip, elements);

         //double utility = 1000000.0;

        double utility = 0.0;

        utility += estimateConstantUtility();
        utility += estimateTravelTimeUtility(variables);
        utility += estimateAccessEgressTimeUtility(variables);
        utility += estimateInVehicleTimeUtility(variables);
        utility += estimateWaitingTimeUtility(variables);
        utility += estimateLineSwitchUtility(variables);
        utility += estimateMonetaryCostUtility(variables);



        return utility;
    }
}
