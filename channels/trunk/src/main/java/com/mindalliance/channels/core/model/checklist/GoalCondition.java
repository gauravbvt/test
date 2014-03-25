package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Goal;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:51 PM
 */
public class GoalCondition extends Condition {

    public static final String REF_PREFIX = "goal|";

    private Goal goal;

    public GoalCondition( Goal goal ) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public String getRef() {
        return REF_PREFIX +  goal.getCategoryLabel() + "|" + goal.getOrganization().getId() + "|" + goal.getLevel().name();
    }

    @Override
    public boolean isEventTimingCondition() {
        return false;
    }

    @Override
    public boolean isGoalCondition() {
        return true;
    }

    @Override
    public boolean isLocalCondition() {
        return false;
    }

    @Override
    public boolean isTaskFailedCondition() {
        return false;
    }

    @Override
    public boolean isAssetAvailableCondition() {
        return false;
    }

    @Override
    public boolean matches( Outcome outcome ) {
        if ( outcome.isGoalAchievedOutcome() ) {
            Goal outcomeGoal = ((GoalAchievedOutcome)outcome).getGoal();
            return outcomeGoal.narrowsOrEquals( getGoal() );
        } else {
            return false;
        }
    }

    @Override
    public boolean isNeedSatisfiedCondition() {
        return false;
    }

    @Override
    public String getLabel() {
        return getGoal().getStepConditionLabel();
    }

    public static boolean isGoalRef( String conditionRef ) {
        return conditionRef.startsWith( REF_PREFIX );
    }

    @Override
    public int hashCode() {
        return goal.hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof GoalCondition
                && goal.equals( ((GoalCondition)object).getGoal() );
    }

}
