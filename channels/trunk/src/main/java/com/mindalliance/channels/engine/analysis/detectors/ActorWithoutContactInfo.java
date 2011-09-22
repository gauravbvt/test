package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor has no contact info.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 29, 2010
 * Time: 5:04:11 PM
 */
public class ActorWithoutContactInfo extends AbstractIssueDetector {

    public ActorWithoutContactInfo() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Actor actor = (Actor) modelObject;
        if ( actor.isActual() && !actor.isUnknown() && actor.getEffectiveChannels().isEmpty() ) {
            Issue issue = makeIssue( queryService, Issue.COMPLETENESS, actor );
            issue.setDescription( actor.getName() + " has no contact info." );
            issue.setRemediation( "Add a channel via which to contact " + actor.getName() );
            issue.setSeverity( Level.High );
            issues.add( issue );
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Actor;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Agent without contact info";
    }

    /**
      * {@inheritDoc}
      */
    public boolean canBeWaived() {
        return true;
    }
}
