package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;

/**
 * A sub task step.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 2:05 PM
 */
public class SubTaskStep extends Step {

    public static final String REF_PREFIX = "sub|";

    private Flow sharing;

    public SubTaskStep( Flow sharing ) {
        this.sharing = sharing;
    }

    public static boolean isSubTask( Flow flow ) {
        return isResearchSharing( flow ) || isFollowUpSharing( flow );
    }

    private static boolean isFollowUpSharing( Flow flow ) {
        return flow.isNotification() && flow.isTriggeringToTarget() && flow.isToSelf();
    }

    private static boolean isResearchSharing( Flow flow ) {
        return flow.isAskedFor() && flow.isTriggeringToSource() && flow.isToSelf();
    }

    public static boolean isSubTaskStepRef( String stepRef ) {
        return stepRef.startsWith( REF_PREFIX );
    }

    public boolean isResearch() {
        return isResearchSharing( sharing );
    }

    public boolean isFollowUp() {
        return isFollowUpSharing( sharing );
    }

    @Override
    public String getLabel() {
        String subTask = isResearch()
                ? ( (Part) sharing.getSource() ).getTask()
                : ( (Part) sharing.getTarget() ).getTask();
        String label = isResearch()
                ? ( "Research \"" + sharing.getName() + "\" by doing \"" + subTask + "\"" )
                : ( "(Required) Follow up with \"" + sharing.getName() + "\" by doing \"" + subTask + "\"" );
        if ( getSharing().isTerminatingToSource() )
            label += " - and stop";
        return label;
    }

    @Override
    public String getRef() {
        return REF_PREFIX + sharing.getId();
    }

    @Override
    public String getPrerequisiteLabel() {
        String subTask = isResearch()
                ? ( (Part) sharing.getSource() ).getTask()
                : ( (Part) sharing.getTarget() ).getTask();
        return isResearch()
                ? ( "Researching \"" + sharing.getName() + "\" by doing \"" + subTask + "\"" )
                : ( "Following up with \"" + sharing.getName() + "\" by doing \"" + subTask + "\"" );
    }

    @Override
    public boolean isTerminating() {
        return isFollowUp() && sharing.isTerminatingToSource();
    }

    @Override
    public boolean isRequired() {
        return isFollowUp();
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
    public boolean isSubTaskStep() {
        return true;
    }

    public Flow getSharing() {
        return sharing;
    }

    @Override
    public int hashCode() {
        return sharing.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof SubTaskStep
                && sharing.equals( ( (SubTaskStep) object ).getSharing() );
    }

    public boolean isTerminatingOnFollowUp() {
        return isFollowUp() && sharing.isTerminatingToSource();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getLabel();
    }

}
