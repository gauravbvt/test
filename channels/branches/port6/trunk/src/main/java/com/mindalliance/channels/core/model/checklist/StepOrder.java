package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Ordering of two steps.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:51 PM
 */
public class StepOrder implements Serializable, Mappable {
    private String prerequisiteStepRef;
    private String stepRef;

    public StepOrder() {
    }

    public StepOrder( Step prereq, Step step ) {
        prerequisiteStepRef = prereq.getRef();
        stepRef = step.getRef();
    }

    public String getPrerequisiteStepRef() {
        return prerequisiteStepRef;
    }

    public void setPrerequisiteStepRef( String prerequisiteStepRef ) {
        this.prerequisiteStepRef = prerequisiteStepRef;
    }

    public String getStepRef() {
        return stepRef;
    }

    public void setStepRef( String stepRef ) {
        this.stepRef = stepRef;
    }

    public boolean isEffective( List<Step> steps ) {
        return !prerequisiteStepRef.equals( stepRef )
                && CollectionUtils.exists(
                steps,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Step) object ).getRef().equals( prerequisiteStepRef );
                    }
                }
        )
                && CollectionUtils.exists(
                steps,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Step) object ).getRef().equals( stepRef );
                    }
                }
        );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * prerequisiteStepRef.hashCode();
        hash = hash + 31 * stepRef.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof StepOrder
                && prerequisiteStepRef.equals( ( (StepOrder) object ).getPrerequisiteStepRef() )
                && stepRef.equals( ( (StepOrder) object ).getStepRef() );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "prerequisiteStepRef", prerequisiteStepRef );
        map.put( "stepRef", stepRef );
    }
}

