package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.model.Part;

/**
 * A visitor of matched resources.
 */
public interface QueryVisitor {

    void visit( Part part );

}
