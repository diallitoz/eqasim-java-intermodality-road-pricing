package org.eqasim.core.components.car_pt.routing;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.controler.AbstractModule;

import com.google.inject.Provides;
import com.google.inject.Inject;
import com.google.inject.Singleton;


public class ParkRideManager extends AbstractModule{
    @Provides
    @Singleton
    public List<Coord> getCoordinates() {
        // Park and ride lot location
        List<Coord> parkRideCoords = new ArrayList<Coord>();

        double[] xCoord = { 695217.09, 691365.43, 703543.53, 702770.20, 693929.84, 704530.69, 708963.08, 711811.05,
                685914.90, 712180.02, 702337.39, 709906.41 };

        double[] yCoord = { 7059186.19, 7065019.42, 7057923.10, 7056776.29, 7050511.72, 7057833.24, 7061460.64,
                7068903.84, 7047847.26, 7071112.37, 7049972.24, 7056430.63 };

        /*
         * double Lat = 50.62964, Long = 2.93251586; X = 695217.09, Y = 7059186.19;
         * 50.6819458 2.878044; X = 691365.43, Y = 7065019.42; 50.6183128 3.049986 X =
         * 703543.53, Y = 7057923.10 50.6080246 3.03906918 X = 702770.20, Y = 7056776.29
         * 50.5517769 2.914486 X = 693929.84, Y = 7050511.72 50.6175 3.06391 X =
         * 704530.69, Y = 7057833.24 50.6500053 3.12651539 X = 708963.08, Y = 7061460.64
         * 50.71675 3.166937 X = 711811.05, Y = 7068903.84 50.5277328 2.80166936 X =
         * 685914.90, Y = 7047847.26 50.73656 3.17222 X = 712180.02, Y = 7071112.37
         * 50.54696 3.032925 X = 702337.39, Y = 7049972.24 50.60485 3.139705 X =
         * 709906.41, Y = 7056430.63
         */

        for (int i = 0; i < yCoord.length; i++) {
            Coord prCoord = new Coord(xCoord[i], yCoord[i]);
            parkRideCoords.add(prCoord);
        }

        return parkRideCoords;
    }

    @Override
    public void install() {
        // TODO Auto-generated method stub

    }
}
