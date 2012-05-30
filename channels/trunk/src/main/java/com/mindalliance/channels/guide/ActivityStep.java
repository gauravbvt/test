package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/30/12
 * Time: 2:12 PM
 */
public class ActivityStep implements Serializable {

    private String description;

    @XStreamAlias( value = "script" )
    private ActivityScript activityScript;

    public ActivityStep() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public ActivityScript getActivityScript() {
        return activityScript;
    }

    public void setActivityScript( ActivityScript activityScript ) {
        this.activityScript = activityScript;
    }
}
