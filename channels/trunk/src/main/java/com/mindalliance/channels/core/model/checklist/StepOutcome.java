package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Outcome of a step.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/23/13
 * Time: 12:24 PM
 */
public class StepOutcome implements Serializable, Mappable {

    private String outcomeRef = "";
    private String stepRef = "";

    public StepOutcome() {
    }

    public StepOutcome( Outcome outcome, Step step ) {
        outcomeRef = outcome.getRef();
        stepRef = step.getRef();
    }

    public String getOutcomeRef() {
        return outcomeRef;
    }

    public void setOutcomeRef( String outcomeRef ) {
        this.outcomeRef = outcomeRef;
    }

    public String getStepRef() {
        return stepRef;
    }

    public void setStepRef( String stepRef ) {
        this.stepRef = stepRef;
    }

    public boolean isEffective( List<Step> steps, List<Outcome> outcomes ) {
        return CollectionUtils.exists(
                outcomes,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Outcome) object ).getRef().equals( outcomeRef );
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
        hash = hash + 31 * outcomeRef.hashCode();
        hash = hash + 31 * stepRef.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof StepOutcome
                && outcomeRef.equals( ((StepOutcome)object).getOutcomeRef() )
                && stepRef.equals( ((StepOutcome)object).getStepRef() );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "outcomeRef", outcomeRef );
        map.put( "stepRef", stepRef );
    }


}
