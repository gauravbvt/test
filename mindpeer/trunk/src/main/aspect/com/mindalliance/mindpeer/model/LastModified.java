// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import java.util.Date;

/**
 * An aspect to update modification times of model objects.
 */
@Aspect
public class LastModified {

    /**
     * Create a new LastModified instance.
     */
    public LastModified() {
    }

    /**
     * Update the lastModified field of model objects after setters have been invoked.
     * @param mo a model object
     */
    @AfterReturning(
            value = "this(mo) && execution( void (ModelObject+ && !ModelObject).set*(*) )",
            argNames = "mo" )
    public void updateLastModified( ModelObject mo ) {
        mo.setLastModified( new Date() );
    }

}
