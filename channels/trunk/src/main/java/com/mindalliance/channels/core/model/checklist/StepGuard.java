package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Conditional state guarding a step.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:51 PM
 */
public class StepGuard implements Serializable, Mappable {

    private String conditionRef = "";
    private boolean positive = true;
    private String stepRef = "";

    public StepGuard() {
    }

    public StepGuard( Condition condition, Step step, boolean positive ) {
        conditionRef = condition.getRef();
        stepRef = step.getRef();
        this.positive = positive;
    }

    public String getConditionRef() {
        return conditionRef;
    }

    public void setConditionRef( String conditionRef ) {
        this.conditionRef = conditionRef;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive( boolean positive ) {
        this.positive = positive;
    }

    public String getStepRef() {
        return stepRef;
    }

    public void setStepRef( String stepRef ) {
        this.stepRef = stepRef;
    }

    public boolean isEffective( List<Step>steps, List<Condition> conditions ) {
        return CollectionUtils.exists(
                conditions,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Condition)object).getRef().equals( conditionRef );
                    }
                }
        )
                && CollectionUtils.exists(
                steps,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Step)object).getRef().equals( stepRef );
                    }
                }
        );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( positive) hash = hash + 31;
        hash = hash + 31 * conditionRef.hashCode();
        hash = hash + 31 * stepRef.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof StepGuard
                && positive == ((StepGuard)object).isPositive()
                && conditionRef.equals( ((StepGuard)object).getConditionRef() )
                && stepRef.equals( ((StepGuard)object).getStepRef() );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "positive", positive );
        map.put( "conditionRef", conditionRef );
        map.put( "stepRef", stepRef );
    }
}
