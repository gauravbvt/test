package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.io.Serializable;

/**
 * An information need.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 9:36 AM
 */
public class InfoNeed implements Serializable {

    private Information information;
    private Delay maxDelay;

    public InfoNeed( Flow receive ) {
        information = new Information( receive );
        maxDelay = receive.getMaxDelay();
    }

    public Information getInformation() {
        return information;
    }

    public void setInformation( Information information ) {
        this.information = information;
    }

    public Delay getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay( Delay maxDelay ) {
        this.maxDelay = maxDelay;
    }

    public String getStepConditionLabel() {
        // The info is needed if a step condition
        Information info = getInformation();
        StringBuilder sb = new StringBuilder();
        sb.append( "The need for information \"" )
                .append( info.getName().isEmpty()
                        ? "something"
                        : info.getName() );
        sb.append( "\"");
        if ( !info.getEois().isEmpty() ) {
            sb.append( " with element " )
                    .append( ChannelsUtils.listToString( info.getEffectiveEoiNames(), ", ", " and " ) );
        }
        sb.append( " is satisfied" );
        return sb.toString();
    }


    public boolean narrowsOrEquals( InfoNeed other ) {
        return information.narrowsOrEquals( other.getInformation() )
                && maxDelay.compareTo( other.getMaxDelay() ) <= 0;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof  InfoNeed ) {
            InfoNeed other = (InfoNeed)obj;
            return information.equals( other.getInformation() )
                    && maxDelay.equals( other.getMaxDelay() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * information.hashCode();
        hash = hash + 31 * maxDelay.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "\""
                + information
                + "\" needed"
                + (maxDelay.isImmediate() ? " " : " within ")
                + maxDelay;
    }

 }
