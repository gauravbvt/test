package com.mindalliance.channels.core.model.checklist;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public abstract class Step  extends AbstractChecklistElement {

    public abstract String getRef();

    public abstract boolean isActionStep();

    public abstract boolean isCommunicationStep();

    public abstract boolean isReceiptConfirmation();

    public abstract boolean isSubTaskStep();

    public abstract String getLabel();

    public abstract String getPrerequisiteLabel();

    public abstract boolean isTerminating();

    public abstract boolean isRequired();

    @Override
    public boolean isStep() {
        return true;
    }

    @Override
    public Step getStep() {
        return this;
    }

    public String getAssetConnectionsLabel( Checklist checklist ) {
        return ""; // DEFAULT
    }
}
