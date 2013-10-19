package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;

import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public class ActionStep extends Step implements Mappable {

    public static final String REF_PREFIX = "action|";


    private String action = "";
    private boolean required = true;
    private String instructions;

    public ActionStep() {
    }

    public ActionStep( String action ) {
        this.action = action;
    }

    public String getAction() {
        return action == null ? "" : action;
    }

    public void setAction( String action ) {
        this.action = action;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired( boolean required ) {
        this.required = required;
    }

    public String getInstructions() {
        return instructions == null ? "" : instructions;
    }

    public void setInstructions( String instructions ) {
        this.instructions = instructions;
    }

    public String getRef() {
        return REF_PREFIX + action;
    }

    @Override
    public boolean isActionStep() {
        return true;
    }

    @Override
    public boolean isCommunicationStep() {
        return false;
    }

    @Override
    public boolean isReceiptConfirmation() {
        return false;
    }

    @Override
    public boolean isSubTaskStep() {
        return false;
    }

    @Override
    public String getLabel() {
        return ( isRequired() ? "(Required) " : "" ) + action;
    }

    @Override
    public String getPrerequisiteLabel() {
        return "Completion of " + action;
    }

    @Override
    public boolean isTerminating() {
        return false;
    }

    public static boolean isActionStepRef( String stepRef ) {
        return stepRef.startsWith( REF_PREFIX );
    }

    @Override
    public void map( Map<String, Object> map ) {
        map.put( "required", required );
        map.put( "action", action );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( required ) hash = hash + 31;
        hash = hash + 31 * action.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ActionStep
                && required == ( (ActionStep) object ).isRequired()
                && action.equals( ( (ActionStep) object ).getAction() );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getLabel();
    }


 }
