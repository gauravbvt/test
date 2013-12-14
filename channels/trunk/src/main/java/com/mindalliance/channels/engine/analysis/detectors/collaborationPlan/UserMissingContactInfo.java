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
        Set<TransmissionMedium> knownMedia = new HashSet<TransmissionMedium>();
        for ( ContactInfo contactInfo : user.getUserRecord().getContactInfoList() ) {
            try {
                knownMedia.add( communityService.find( TransmissionMedium.class, contactInfo.getTransmissionMediumId() ) );
            } catch ( NotFoundException e ) {
                // Ignore
            }
        }
        List<Agent> userAgents = communityService.getParticipationManager().listAgentsUserParticipatesAs( user, communityService );
        if ( !userAgents.isEmpty() ) {
            Set<TransmissionMedium> requiredMedia = new HashSet<TransmissionMedium>();
            CommunityCommitments communityCommitments = communityService.getAllCommitments( false );
            for ( Agent agent : userAgents ) {
                requiredMedia.addAll( findRequireMedia( communityCommitments.to( agent ), true ) ); // agent as beneficiary of notifications
                requiredMedia.addAll( findRequireMedia( communityCommitments.from( agent ), false ) ); // agent as committer to reply to requests
            }
            for ( final TransmissionMedium requiredMedium : requiredMedia ) {
                if ( requiredMedium.isUnicast() && requiredMedium.requiresAddress() ) {
                    boolean known = CollectionUtils.exists(
                            knownMedia,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    TransmissionMedium knownMedium = (TransmissionMedium) object;
                                    return knownMedium.narrowsOrEquals( requiredMedium );
                                }
                            }
                    );
                    if ( !known ) {
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
                }
            }
        }
        return issues;
    }

    private Set<TransmissionMedium> findRequireMedia( CommunityCommitments communityCommitments, boolean isBeneficiary ) {
        Set<TransmissionMedium> media = new HashSet<TransmissionMedium>();
        for ( CommunityCommitment communityCommitment : communityCommitments ) {
            Flow sharing = communityCommitment.getSharing();
            if ( isBeneficiary ) {
                if ( sharing.isNotification() ) {
                    for ( Channel channel : sharing.getEffectiveChannels() ) {
                        media.add( channel.getMedium() );
                    }
                }
            } else {
                if ( sharing.isAskedFor() ) {
                    for ( Channel channel : sharing.getEffectiveChannels() ) {
                        media.add( channel.getMedium() );
                    }
                }
            }
        }
        return media;
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
