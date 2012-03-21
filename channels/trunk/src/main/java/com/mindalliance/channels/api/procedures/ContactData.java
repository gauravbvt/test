package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.UserContactInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.PlanService;

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
@XmlType( propOrder = {"employment", "workChannels", "personalChannels", "supervisorContact", "organizationChannels"} )
public class ContactData {

    private Employment employment;
    private ChannelsUserInfo userInfo;
    private boolean includeSupervisor;
    private PlanService planService;
    private PlanParticipationService planParticipationservice;

    public ContactData() {
        // required
    }

    public ContactData(
            Employment employment,
            ChannelsUserInfo userInfo,
            boolean includeSupervisor,
            PlanService planService,
            PlanParticipationService planParticipationservice ) {
        this.employment = employment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        this.planService = planService;
        this.planParticipationservice = planParticipationservice;
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
            channelDataList.add( new ChannelData( channel, planService ) );
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
            Iterator<Employment> iter = planService.findAllEmploymentsForActor( supervisor ).iterator();
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
                            planService,
                            planParticipationservice ) );
                } else {
                    List<PlanParticipation> participations = planParticipationservice.getParticipations(
                            planService.getPlan(),
                            supervisor,
                            planService );
                    for ( PlanParticipation participation : participations ) {
                        supervisorContacts.add( new ContactData(
                                supervisorEmployment,
                                participation.getParticipant(),
                                false,
                                planService,
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
            channels.add( new ChannelData( channel, planService ) );
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
                        planService ) );
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
}
