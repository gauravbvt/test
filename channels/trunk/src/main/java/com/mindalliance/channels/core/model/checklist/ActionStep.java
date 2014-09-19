package com.mindalliance.channels.core.model.checklist;

import com.mindalliance.channels.core.model.Mappable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/13
 * Time: 11:50 PM
 */
public class ActionStep extends Step implements Mappable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ActionStep.class );

    public static final String REF_PREFIX = "action|";

    private String uid = "";
    private String action = "";
    private boolean required = true;
    private String instructions;

    public ActionStep() {
        setUid( UUID.randomUUID().toString() );
    }

    public String getUid() {
        if ( uid == null || uid.isEmpty() )
            uid = UUID.randomUUID().toString(); // should never happen
        return uid;
    }

    public void setUid( String uid ) {
        if ( uid != null && !uid.isEmpty() )
            this.uid = uid;
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
        assert !uid.isEmpty();
        return REF_PREFIX + uid;
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
        StringBuilder sb = new StringBuilder();
        sb.append( isRequired() ? "(Expected) " : "" )
                .append( action );
        return sb.toString();
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
        map.put( "uid", uid );
        map.put( "required", required );
        map.put( "action", action );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if ( required ) hash = hash + 31;
        hash = hash + 31 * uid.hashCode();
        hash = hash + 31 * action.hashCode();
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ActionStep
                && uid.equals( ( (ActionStep) object ).getUid() )
                && required == ( (ActionStep) object ).isRequired()
                && action.equals( ( (ActionStep) object ).getAction() );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getLabel();
    }


    @Override
    public String getAssetConnectionsLabel( Checklist checklist ) {
        List<String> provisionedAssets = new ArrayList<String>();
        for ( Outcome outcome : checklist.listOutcomesFor( this ) ) {
            if ( outcome.isAssetProvisionedOutcome() ) {
                provisionedAssets.add( ( (AssetProvisionedOutcome) outcome ).getProvisionedAsset().getName() );
            }
        }
        if ( provisionedAssets.isEmpty() ) {
            return "";
        } else {
            Collections.sort( provisionedAssets );
            StringBuilder sb = new StringBuilder();
            sb.append( "Deliver " );
            sb.append( ChannelsUtils.listToString( provisionedAssets, " and " ) );
            return sb.toString();
        }
    }
}
