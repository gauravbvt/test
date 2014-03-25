package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Identifiable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 8:55 PM
 */
public interface ChecklistElement extends Identifiable {

    String getRef();

    void setId( long id );

    boolean isStep();

    boolean isCondition();

    boolean isOutcome();

    boolean isAssetProvisioned();

    Step getStep();

    Condition getCondition();

    Outcome getOutcome();

    AssetProvisioning getAssetProvisioning();

    String getContext();

}
