package org.eqasim.lille_metropolis.mode_choice.parameters;

import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;

public class MELModeParameters extends ModeParameters{
    public class MELCarParameters {
        public double betaInsideUrbanArea;
        public double betaCrossingUrbanArea;
    }

    public class MELBikeParameters {
        public double betaInsideUrbanArea;
    }

    public final MELCarParameters idfCar = new MELCarParameters();
    public final MELBikeParameters idfBike = new MELBikeParameters();

    public static MELModeParameters buildDefault() {
        MELModeParameters parameters = new MELModeParameters();

        // Cost
        parameters.betaCost_u_MU = -0.206;
        parameters.lambdaCostEuclideanDistance = -0.4;
        parameters.referenceEuclideanDistance_km = 40.0;

        // Car
        parameters.car.alpha_u = 1.35;
        //@
        //parameters.car.alpha_u = 1.45;
        //parameters.car.alpha_u = 2;
        //parameters.car.alpha_u = 5;
        //parameters.car.alpha_u = 10;
        parameters.car.betaTravelTime_u_min = -0.06;
        //parameters.car.betaTravelTime_u_min = -0.05;
        //parameters.car.betaTravelTime_u_min = -0.04;
        //parameters.car.betaTravelTime_u_min = -0.03;

        parameters.car.constantAccessEgressWalkTime_min = 4.0;
        parameters.car.constantParkingSearchPenalty_min = 4.0;

        parameters.idfCar.betaInsideUrbanArea = -0.5;
        parameters.idfCar.betaCrossingUrbanArea = -1.0;

        // PT
        parameters.pt.alpha_u = 0.0;
        //parameters.pt.alpha_u = 1.0;
        //parameters.pt.alpha_u = 2.0;
        //parameters.pt.alpha_u = 5.0;
        parameters.pt.betaLineSwitch_u = -0.17;
        parameters.pt.betaInVehicleTime_u_min = -0.017;
        //parameters.pt.betaInVehicleTime_u_min = -0.015;
        //parameters.pt.betaInVehicleTime_u_min = -0.010;
        parameters.pt.betaWaitingTime_u_min = -0.0484;
        parameters.pt.betaAccessEgressTime_u_min = -0.0804;

        // Bike
        parameters.bike.alpha_u = -2.0;
        parameters.bike.betaTravelTime_u_min = -0.05;
        //parameters.bike.betaTravelTime_u_min = -0.06;
        //parameters.bike.betaTravelTime_u_min = -0.07;
        //parameters.bike.betaTravelTime_u_min = -0.08;
        parameters.bike.betaAgeOver18_u_a = -0.0496;

        parameters.idfBike.betaInsideUrbanArea = 1.5;

        // Walk
        parameters.walk.alpha_u = 1.43;
        //parameters.walk.alpha_u = 2;
        parameters.walk.betaTravelTime_u_min = -0.15;
        //parameters.walk.betaTravelTime_u_min = -0.16;
        //parameters.walk.betaTravelTime_u_min = -0.20;

        return parameters;
    }
}
