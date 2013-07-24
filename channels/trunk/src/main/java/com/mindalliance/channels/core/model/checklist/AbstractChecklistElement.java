package com.mindalliance.channels.core.model.checklist;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 8:59 PM
 */
public abstract class AbstractChecklistElement implements ChecklistElement {

    /// ChecklistElement

    private long id;


    public abstract String getLabel();

    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId( long id ) {
        this.id = id;
    }

    @Override
    public boolean isStep() {
        return false; // DEFAULT
    }

    @Override
    public boolean isCondition() {
        return false;  // DEFAULT
    }

    @Override
    public boolean isOutcome() {
        return false; // DEFAULT
    }

    @Override
    public Step getStep() {
        return null; // DEFAULT
    }

    @Override
    public Condition getCondition() {
        return null;  // DEFAULT
    }

    @Override
    public Outcome getOutcome() {
        return null;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return getClassLabel();
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getName() {
        return getLabel();
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public String getContext() {
        return "";
    }
}
