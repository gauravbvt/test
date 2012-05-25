package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:36 PM
 */
public class Activity implements Serializable {

    @XStreamAsAttribute
    private String id;

    private String name;

    private String description;

    @XStreamImplicit( itemFieldName = "change" )
    private List<ActivityChange> activityChanges;

    @XStreamImplicit( itemFieldName = "next" )
    private List<ActivityRef> nextActivities;

    public Activity() {
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

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public List<ActivityChange> getActivityChanges() {
        return activityChanges == null ? new ArrayList<ActivityChange>() : activityChanges;
    }

    public void setActivityChanges( List<ActivityChange> activityChanges ) {
        this.activityChanges = activityChanges;
    }

    public List<ActivityRef> getNextActivities() {
        return nextActivities == null ? new ArrayList<ActivityRef>() : nextActivities;
    }

    public void setNextActivities( List<ActivityRef> nextActivities ) {
        this.nextActivities = nextActivities;
    }
}
