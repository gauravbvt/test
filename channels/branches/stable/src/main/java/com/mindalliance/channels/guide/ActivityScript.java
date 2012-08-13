package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of activity changes.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/30/12
 * Time: 1:17 PM
 */
public class ActivityScript implements Serializable {

    private String action;

    @XStreamImplicit( itemFieldName = "change" )
    private List<ActivityChange> activityChanges;

    public ActivityScript() {
    }

    public String getAction() {
        return action;
    }

    public void setAction( String action ) {
        this.action = action;
    }

    public List<ActivityChange> getActivityChanges() {
        return activityChanges == null ? new ArrayList<ActivityChange>() : activityChanges;
    }

    public void setActivityChanges( List<ActivityChange> activityChanges ) {
        this.activityChanges = activityChanges;
    }
}
