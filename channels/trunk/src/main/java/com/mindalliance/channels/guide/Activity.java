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

    @XStreamImplicit( itemFieldName = "step" )
    private List<ActivityStep> activitySteps;

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

    public List<ActivityStep> getActivitySteps() {
        return activitySteps;
    }

    public void setActivitySteps( List<ActivityStep> activitySteps ) {
        this.activitySteps = activitySteps;
    }

    public List<ActivityRef> getNextActivities() {
        return nextActivities == null ? new ArrayList<ActivityRef>() : nextActivities;
    }

    public void setNextActivities( List<ActivityRef> nextActivities ) {
        this.nextActivities = nextActivities;
    }
}
