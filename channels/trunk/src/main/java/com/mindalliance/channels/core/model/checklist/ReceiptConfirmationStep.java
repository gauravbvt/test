package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Flow;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/20/13
 * Time: 3:09 PM
 */
public class ReceiptConfirmationStep extends Step {

    public static final String REF_PREFIX = "conf|";

    private Flow sharing;

    public ReceiptConfirmationStep( Flow sharing ) {
        this.sharing = sharing;
    }

    public Flow getSharingToConfirm() {
        return sharing;
    }

    @Override
    public String getLabel() {
        return "(Required) Confirm receipt of "
                + (sharing.isNotification() ? "notification of" : "request for")
        + " \"" + sharing.getName() + "\"";
    }

    @Override
    public String getRef() {
        return REF_PREFIX + sharing.getId();
    }

    @Override
    public boolean isActionStep() {
        return false;
    }

    @Override
    public boolean isCommunicationStep() {
        return false;
    }

    @Override
    public boolean isReceiptConfirmation() {
        return true;
    }

    @Override
    public boolean isSubTaskStep() {
        return false;
    }

    @Override
    public String getPrerequisiteLabel() {
        return "Confirmed receipt of "
                + (sharing.isNotification() ? "notification of" : "query for")
                + " \"" + sharing.getName() + "\"";
    }

    @Override
    public boolean isTerminating() {
        return false;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int hashCode() {
        return sharing.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ReceiptConfirmationStep
                && sharing.equals( ( (ReceiptConfirmationStep) object ).getSharingToConfirm() );
    }

}
