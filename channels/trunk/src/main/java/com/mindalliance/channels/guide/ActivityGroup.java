package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
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
 * Time: 9:35 PM
 */
public class ActivityGroup implements Serializable {

    @XStreamAsAttribute
    private String id;

    private String name;

    @XStreamImplicit( itemFieldName = "activity" )
    private List<Activity> activities;

    public ActivityGroup() {
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities( List<Activity> activities ) {
        this.activities = activities;
    }

    public Activity derefActivity( final String activityId ) {
        return (Activity) CollectionUtils.find(
                activities,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Activity) object ).getId().equals( activityId );
                    }
                } );
    }

}
