package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Goal;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 11:04 AM
 */
public class GoalAchievedOutcome extends Outcome {

    public static final String REF_PREFIX = "goalAchieved|";

    private Goal goal;

    public GoalAchievedOutcome( Goal goal ) {
        this.goal = goal;
    }

    public static boolean isGoalAchievedOutcomeRef( String outcomeRef ) {
        return outcomeRef.startsWith( REF_PREFIX );
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public boolean isCapabilityCreatedOutcome() {
        return false;
    }

    @Override
    public boolean isEventTimingOutcome() {
        return false;
    }

    @Override
    public boolean isGoalAchievedOutcome() {
        return true;
    }

    @Override
    public String getLabel() {
        return goal.getStepOutcomeLabel();
    }

    @Override
    public String getRef() {
        return REF_PREFIX +  goal.getCategoryLabel() + "|" + goal.getOrganization().getId() + "|" + goal.getLevel().name();
    }

    @Override
    public int hashCode() {
        return goal.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof GoalAchievedOutcome
                && goal.equals( ((GoalAchievedOutcome)object).getGoal() );
    }

}
