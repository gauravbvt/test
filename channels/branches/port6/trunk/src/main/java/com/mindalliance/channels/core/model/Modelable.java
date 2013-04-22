package com.mindalliance.channels.core.model;

/**
 * Something with a name that knows its model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2010
 * Time: 10:44:27 AM
 */
public interface Modelable extends Nameable {
    /**
     * Get related model object.
     *
     * @return a model object
     */
    ModelObject getModelObject();
}
