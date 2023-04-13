package org.eqasim.lille_metropolis.mode_choice.utilities.variables;

import org.eqasim.core.simulation.mode_choice.utilities.variables.BaseVariables;

public class MELPersonVariables implements BaseVariables {
    public final boolean hasSubscription;

    public MELPersonVariables(boolean hasSubscription) {
        this.hasSubscription = hasSubscription;
    }
}
