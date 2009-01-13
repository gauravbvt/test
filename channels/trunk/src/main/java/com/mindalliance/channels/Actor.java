package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Play;

import java.util.List;
import java.util.ArrayList;

/**
 * Someone or something playing a part in a scenario.
 */
public class Actor extends ModelObject implements Player {

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays() {
        return new ArrayList<Play>();  //Todo
    }
}
