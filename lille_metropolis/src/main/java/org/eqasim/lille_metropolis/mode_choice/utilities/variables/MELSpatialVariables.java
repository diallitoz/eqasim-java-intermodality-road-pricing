package org.eqasim.lille_metropolis.mode_choice.utilities.variables;

import org.eqasim.core.simulation.mode_choice.utilities.variables.BaseVariables;

public class MELSpatialVariables implements BaseVariables {
    public final boolean hasUrbanOrigin;
    public final boolean hasUrbanDestination;

    public MELSpatialVariables(boolean hasUrbanOrigin, boolean hasUrbanDestination) {
        this.hasUrbanOrigin = hasUrbanOrigin;
        this.hasUrbanDestination = hasUrbanDestination;
    }
}
