package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/14/13
 * Time: 3:32 PM
 */
public class ChecklistElementHolder implements ChecklistElement {

    private ChecklistElement checklistElement;
    private long id;
    private String context;

    public ChecklistElementHolder( ChecklistElement checklistElement, long id ) {
        this.checklistElement = checklistElement;
        this.id = id;
    }

    public ChecklistElement getChecklistElement() {
        return checklistElement;
    }

    @Override
    public Condition getCondition() {
        return checklistElement.getCondition();
    }

    @Override
    public Outcome getOutcome() {
        return checklistElement.getOutcome();
    }

     @Override
    public boolean isOutcome() {
        return checklistElement.isOutcome();
    }

    @Override
    public String getRef() {
        return checklistElement.getRef();
    }

    @Override
    public void setId( long id ) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getContext() {
        return context == null ? "" : context;
    }

    public void setContext( String context ) {
        this.context = context;
    }

    @Override
    public boolean isStep() {
        return checklistElement.isStep();
    }

    @Override
    public boolean isCondition() {
        return checklistElement.isCondition();
    }

    @Override
    public Step getStep() {
        return checklistElement.getStep();
    }

    @Override
    public String getDescription() {
        return checklistElement.getDescription();
    }

    @Override
    public String getTypeName() {
        return checklistElement.getTypeName();
    }

    @Override
    public boolean isModifiableInProduction() {
        return checklistElement.isModifiableInProduction();
    }

    @Override
    public String getClassLabel() {
        return checklistElement.getClassLabel();
    }

    @Override
    public String getName() {
        return checklistElement.getName();
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    @Override
    public String getUid() {
        return Long.toString( getId() );
    }

    @Override
    public String toString() {
        return checklistElement + "[" + id + "]";
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof ChecklistElementHolder ) {
            ChecklistElementHolder other = (ChecklistElementHolder)object;
           return checklistElement.equals( other.getChecklistElement() )
                   && id == other.getId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * Long.toString( id ).hashCode();
        hash = hash + 31 * checklistElement.hashCode();
        return hash;
    }
}
