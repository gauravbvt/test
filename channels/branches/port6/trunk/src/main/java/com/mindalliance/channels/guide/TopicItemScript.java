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
public class TopicItemScript implements Serializable {

    private String action;

    @XStreamImplicit( itemFieldName = "change" )
    private List<ScriptChange> scriptChanges;

    public TopicItemScript() {
    }

    public String getAction() {
        return action;
    }

    public void setAction( String action ) {
        this.action = action;
    }

    public List<ScriptChange> getScriptChanges() {
        return scriptChanges == null ? new ArrayList<ScriptChange>() : scriptChanges;
    }

    public void setScriptChanges( List<ScriptChange> scriptChanges ) {
        this.scriptChanges = scriptChanges;
    }
}
