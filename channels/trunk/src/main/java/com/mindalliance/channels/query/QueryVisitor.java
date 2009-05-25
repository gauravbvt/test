package com.mindalliance.channels.query;

import com.mindalliance.channels.model.Part;

/**
 * A visitor of matched resources.
 */
public interface QueryVisitor {

    void visit( Part part );

}
