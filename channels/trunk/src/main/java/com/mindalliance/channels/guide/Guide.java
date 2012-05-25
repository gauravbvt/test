package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:34 PM
 */
public class Guide implements Serializable {

    private String name;

    @XStreamImplicit( itemFieldName = "group" )
    private List<ActivityGroup> activityGroups;

    public Guide() {
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<ActivityGroup> getActivityGroups() {
        return activityGroups;
    }

    public void setActivityGroups( List<ActivityGroup> activityGroups ) {
        this.activityGroups = activityGroups;
    }

    public ActivityGroup derefGroup( final String groupId ) {
        return (ActivityGroup) CollectionUtils.find(
                activityGroups,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ActivityGroup) object ).getId().equals( groupId );
                    }
                } );
    }

    public int findGroupIndex( ActivityGroup nextGroup ) {
        return getActivityGroups().indexOf( nextGroup );
    }
}
