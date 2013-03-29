package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Flow;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public class CommunicationStep extends Step {

    public static final String REF_PREFIX = "comm|";

    private Flow sharing;

    public CommunicationStep( Flow sharing ) {
        this.sharing = sharing;
    }

    public Flow getSharing() {
        return sharing;
    }

    @Override
    public String getRef() {
        return "flow|" + sharing.getId();
    }

    @Override
    public boolean isActionStep() {
        return false;
    }

    @Override
    public boolean isCommunicationStep() {
        return true;
    }

    @Override
    public String getLabel() {
        return ( isNotification() ? "(Required) " : "" ) + getSharing().getStepTitle( false );
    }

    @Override
    public String getPrerequisiteLabel() {
        return getSharing().getStepTitle( true );
    }

    public static boolean isCommunicationStepRef( String stepRef ) {
        return stepRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return sharing.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof CommunicationStep
                && sharing.equals( ((CommunicationStep)object).getSharing() );
    }

    public boolean isNotification() {
        return sharing.isNotification();
    }
}
