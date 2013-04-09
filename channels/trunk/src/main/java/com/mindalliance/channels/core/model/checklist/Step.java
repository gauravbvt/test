package com.mindalliance.channels.core.model.checklist;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public abstract class Step implements Serializable {

    public abstract String getRef();

    public abstract boolean isActionStep();

    public abstract boolean isCommunicationStep();

    public abstract boolean isSubTaskStep();

    public abstract String getLabel();

    public abstract String getPrerequisiteLabel();

    public abstract boolean isTerminating();

    public abstract boolean isRequired();
}
