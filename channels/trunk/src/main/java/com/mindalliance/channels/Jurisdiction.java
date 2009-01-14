package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;

/**
 * A description of a jurisdiction.
 */
public class Jurisdiction extends Place  implements Resourceable {

    public Jurisdiction() {
    }

    public Jurisdiction( String name ) {
        this();
        setName( name );
    }

    /**
     * {@inheritDoc}
     */
    public List<Resource> findAllResources() {
        return new ArrayList<Resource>(); // Todo
    }


}
