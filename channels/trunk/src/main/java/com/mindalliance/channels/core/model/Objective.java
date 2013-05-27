package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;

/**
 * A goal to be achieved.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/23/13
 * Time: 10:46 AM
 */
public class Objective implements Serializable {

    private Goal.Category goalCategory;
    private boolean positive;

    public Objective() {
    }

    public Objective( Goal.Category goalCategory, boolean positive ) {
        this.goalCategory = goalCategory;
        this.positive = positive;
    }

    public Goal.Category getGoalCategory() {
        return goalCategory;
    }

    public void setGoalCategory( Goal.Category goalCategory ) {
        this.goalCategory = goalCategory;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive( boolean positive ) {
        this.positive = positive;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof Objective ) {
            Objective other = (Objective) object;
            return goalCategory.equals( ( (Objective) object ).getGoalCategory() )
                    && Boolean.valueOf( positive ).equals( Boolean.valueOf( other.isPositive() ) );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + goalCategory.hashCode();
        hash = hash * 31 + Boolean.valueOf( positive ).hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return
                ( positive
                        ? "Achieve gain: "
                        : "Mitigate risk: "
                ) + goalCategory.getLabel( positive );
    }

    public String getLabel() {
        return toString();
    }

    public boolean implementedBy( Part part, QueryService queryService  ) {
        return CollectionUtils.exists(
                queryService.findAllGoalsImpactedByFailure( part ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Goal goal = (Goal) object;
                        return goal.isPositive() == isPositive()
                                && goal.getCategory().equals( getGoalCategory() );
                    }
                }
        );
    }

    public Goal findMatchingGoal( Segment segment ) {
        return (Goal)CollectionUtils.find(
                segment.getGoals(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Goal goal = (Goal)object;
                        return goal.isPositive() == isPositive()
                                && goal.getCategory().equals( getGoalCategory() );
                    }
                }
        );
    }
}
