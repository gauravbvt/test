package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.time.Delay;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 9:43 AM
 */
public class InfoCapability implements Serializable {

    private Information information;
    private ResourceSpec targetSpec;
    private Delay maxDelay;

    public InfoCapability( Flow send ) {
        information = new Information( send );
        targetSpec = send.getTargetResourceSpec();
        maxDelay = send.getMaxDelay();
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

    public ResourceSpec getTargetSpec() {
        return targetSpec;
    }

    public void setTargetSpec( ResourceSpec targetSpec ) {
        this.targetSpec = targetSpec;
    }

    public String getStepOutcomeLabel() {
        // The info is needed if a step condition
        Information info = getInformation();
        StringBuilder sb = new StringBuilder();
        sb.append( "The information \"" )
                .append( info.getName().isEmpty()
                        ? "something"
                        : info.getName() );
        sb.append( "\"");
        if ( !info.getEois().isEmpty() ) {
            sb.append( " with element " )
                    .append( ChannelsUtils.listToString( info.getEffectiveEoiNames(), ", ", " and " ) );
        }
        sb.append( " can be shared" );
        ResourceSpec sourceSpec = getTargetSpec();
        if ( !sourceSpec.isAnyone() ) {
            sb.append( " with ")
                    .append( sourceSpec.getLabel() );
        }
        return sb.toString();
    }



    public boolean narrowsOrEquals( InfoCapability other ) {
        return information.narrowsOrEquals( other.getInformation() )
                && targetSpec.narrowsOrEquals( other.getTargetSpec(), Place.UNKNOWN )
                && maxDelay.compareTo( other.getMaxDelay() ) <= 0;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof InfoCapability ) {
            InfoCapability other = (InfoCapability)obj;
            return information.equals( other.getInformation() )
                    && maxDelay.equals( other.getMaxDelay() )
                    && targetSpec.equals( other.getTargetSpec() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * information.hashCode();
        hash = hash + 31 * maxDelay.hashCode();
        hash = hash + 31 * targetSpec.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "\""
                + information
                + "\" shareable with "
                + targetSpec
                + (maxDelay.isImmediate() ? " " : " within ")
                + maxDelay;
    }

}
