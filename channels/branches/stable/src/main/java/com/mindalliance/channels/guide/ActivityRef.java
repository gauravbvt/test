package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:37 PM
 */
public class ActivityRef implements Serializable {

    @XStreamAsAttribute
    @XStreamAlias("group")
    private String groupId;

    @XStreamAsAttribute
    @XStreamAlias("activity")
    private String activityId;

    public ActivityRef() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId( String groupId ) {
        this.groupId = groupId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId( String activityId ) {
        this.activityId = activityId;
    }
}
