package com.mindalliance.channels.core.orm.model;

import com.mindalliance.channels.core.model.Identifiable;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 11:16 AM
 */
public interface PersistentPlanObject extends Timestamped, Identifiable, Serializable {

    /**
     * Get the date associated with this object.
     *
     * @return a creation date, usually
     */
    Date getCreated();

    /**
     * Get the unique id of this object.
     *
     * @return a string
     */
    long getId();

    /**
     * Get the uri of the plan this belongs to.
     *
     * @return string
     */
    String getPlanUri();

    /**
     * Get the username of the owner.
     *
     * @return a string
     */
    String getUsername();

}
