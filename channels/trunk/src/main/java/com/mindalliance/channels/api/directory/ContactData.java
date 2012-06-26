package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.UserContactInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 9:13 PM
 */
@XmlType( propOrder = {"id", "employment", "workChannels", "personalChannels", "supervisorContact", "organizationChannels"} )
public class ContactData {

    private Employment employment;
    private ChannelsUserInfo userInfo;
    private boolean includeSupervisor;
    private QueryService queryService;
    private PlanParticipationService planParticipationservice;

    public ContactData() {
        // required
    }

    public ContactData(
            Employment employment,
            ChannelsUserInfo userInfo,
            boolean includeSupervisor,
            QueryService queryService,
            PlanParticipationService planParticipationservice ) {
        this.employment = employment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        this.queryService = queryService;
        this.planParticipationservice = planParticipationservice;
    }

    /**
     * Find a user's contacts from san employment.
     * @param employment an employment
     * @param queryService a plan service
     * @param planParticipationService a plan participation service
     * @param user a user
     * @return a list of contact data
     */
    static public List<ContactData> findContactsFromEmployment(
            Employment employment,
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        List<ContactData> contactList = new ArrayList<ContactData>(  );
        Actor actor = employment.getActor();
        if ( actor.isAnonymousParticipation() ) {
            contactList.add( new ContactData(
                    employment,
                    null,
                    true,
                    queryService,
                    planParticipationService ) );
        } else {
            List<PlanParticipation> otherParticipations = getOtherParticipations(
                    actor,
                    queryService,
                    planParticipationService,
                    user );
            if ( otherParticipations.isEmpty() || !actor.isSingularParticipation() ) {
                contactList.add( new ContactData(
                        employment,
                        null,
                        true,
                        queryService,
                        planParticipationService ) );
            }
            for ( PlanParticipation otherParticipation : otherParticipations ) {
                contactList.add( new ContactData(
                        employment,
                        otherParticipation.getParticipant(),
                        true,
                        queryService,
                        planParticipationService ) );
            }
        }
        return contactList;
    }

    // Find list of participation as actor other than by the user.
    static private List<PlanParticipation> getOtherParticipations(
            Actor actor,
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        String username = user == null ? null : user.getUsername();
        List<PlanParticipation> otherParticipations = new ArrayList<PlanParticipation>();
        List<PlanParticipation> participations = planParticipationService.getParticipations(
                queryService.getPlan(),
                actor,
                queryService );
        for ( PlanParticipation participation : participations ) {
            if ( username == null || !username.equals( participation.getParticipantUsername() ) ) {
                otherParticipations.add( participation );
            }
        }
        return otherParticipations;
    }

    @XmlElement
    public String getId() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( userInfo == null ? "" : userInfo.getId() );
        sb.append( "_" );
        sb.append( employment.getActor().getId() );
        sb.append( "_" );
        sb.append( employment.getRole().getId() );
        sb.append( "_" );
        sb.append( employment.getOrganization().getId() );
        return sb.toString();
    }

    @XmlElement( name = "identity" )
    public EmploymentData getEmployment() {
        return userInfo == null
                ? new EmploymentData( employment )
                : new EmploymentData( employment, userInfo );
    }

    @XmlElement( name = "workChannel" )
    public List<ChannelData> getWorkChannels() {
        List<ChannelData> channelDataList = new ArrayList<ChannelData>();
        for ( Channel channel : getActor().getEffectiveChannels() ) {
            channelDataList.add( new ChannelData( channel, queryService ) );
        }
        return channelDataList;
    }

    @XmlElement( name = "supervisor" )
    public List<ContactData> getSupervisorContact() {
        List<ContactData> supervisorContacts = new ArrayList<ContactData>();
        if ( includeSupervisor && getSupervisor() != null ) {
            Actor supervisor = getSupervisor();
            Employment sameOrgEmployment = null;
            Employment parentOrgEmployment = null;
            Iterator<Employment> iter = queryService.findAllEmploymentsForActor( supervisor ).iterator();
            List<Organization> ancestors = getOrganization().ancestors();
            while ( sameOrgEmployment == null && iter.hasNext() ) {
                Employment supervisorEmployment = iter.next();
                if ( supervisorEmployment.getOrganization().equals( getOrganization() ) ) {
                    sameOrgEmployment = supervisorEmployment;
                } else if ( parentOrgEmployment == null
                        && ancestors.contains( supervisorEmployment.getOrganization() ) ) {
                    parentOrgEmployment = supervisorEmployment;
                }
            }
            Employment supervisorEmployment = sameOrgEmployment != null
                    ? sameOrgEmployment
                    : parentOrgEmployment;
            if ( supervisorEmployment != null ) {
                if ( supervisor.isAnonymousParticipation() ) {
                    supervisorContacts.add( new ContactData(
                            supervisorEmployment,
                            null,
                            false,
                            queryService,
                            planParticipationservice ) );
                } else {
                    List<PlanParticipation> participations = planParticipationservice.getParticipations(
                            queryService.getPlan(),
                            supervisor,
                            queryService );
                    for ( PlanParticipation participation : participations ) {
                        supervisorContacts.add( new ContactData(
                                supervisorEmployment,
                                participation.getParticipant(),
                                false,
                                queryService,
                                planParticipationservice ) );
                    }
                }
            }
        }
        return supervisorContacts;
   }


    @XmlElement( name = "organizationChannel" )
    public List<ChannelData> getOrganizationChannels() {
        List<ChannelData> channels = new ArrayList<ChannelData>();
        for ( Channel channel : getOrganization().getEffectiveChannels() ) {
            channels.add( new ChannelData( channel, queryService ) );
        }
        return channels;
    }

    @XmlElement( name = "personalChannel" )
    public List<ChannelData> getPersonalChannels() {
        List<ChannelData> channels = new ArrayList<ChannelData>();
        if ( userInfo != null ) {
            for ( UserContactInfo userContactInfo : userInfo.getContactInfoList() ) {  // todo - will this work?
                channels.add( new ChannelData(
                        userContactInfo.getTransmissionMediumId(),
                        userContactInfo.getAddress(),
                        queryService ) );
            }
        }
        return channels;

    }

    private Actor getActor() {
        return employment.getActor();
    }

    private Organization getOrganization() {
        return employment.getOrganization();
    }

    private Actor getSupervisor() {
        return employment.getSupervisor();
    }

    public String toLabel() {
        StringBuilder sb = new StringBuilder(  );
        sb.append( getContactName() );
        sb.append( ", " );
        sb.append( employment.getTitle() );
        sb.append( ", " );
        if ( employment.getJurisdiction() != null ) {
            sb.append( employment.getJurisdiction().getName() );
            sb.append( ", " );
        }
        sb.append( employment.getOrganization().getName() );
        return sb.toString();
    }

    public String getContactName() {
       return  userInfo != null ? userInfo.getFullName() : getActor().getName();
    }

    public String firstLetterOfName() {
        return getContactName().substring( 0, 1 );
    }

    @Override
    public boolean equals ( Object other ) {
        return other instanceof ChannelData
                && employment.equals( other );
    }

    @Override
    public int hashCode() {
        return employment.hashCode();
    }

    public String getNormalizedContactName() {
        if ( userInfo == null )
            return getActor().getName();
        else
            return ChannelsUser.normalizeFullName( userInfo.getFullName() );
    }
}
