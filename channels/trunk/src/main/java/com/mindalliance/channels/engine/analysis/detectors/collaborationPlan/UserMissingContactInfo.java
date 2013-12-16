package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User did not provide needed contact info.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/13
 * Time: 3:21 PM
 */
public class UserMissingContactInfo extends AbstractIssueDetector {

    public UserMissingContactInfo() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof ChannelsUser;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        ChannelsUser user = (ChannelsUser) identifiable;
        for ( TransmissionMedium requiredMedium : communityService.findMissingContactInfoMedia( user ) ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, user );
            issue.setDescription( user.getFullName()
                    + " has not provided "
                    + requiredMedium.getName()
                    + " contact information" );
            issue.setSeverity( Level.High );
            issue.setRemediation( "Have "
                    + user.getFullName()
                    + " provide "
                    + requiredMedium.getName()
                    + " contact information." );
            issues.add( issue );
        }
        return issues;
    }


    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "User contact info is missing";
    }
}
