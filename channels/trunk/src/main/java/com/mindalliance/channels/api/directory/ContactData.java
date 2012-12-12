package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 9:13 PM
 */
@XmlType( propOrder = {"id", "ref", "normalizedContactName", "picture", "employment", "workChannels", "personalChannels", "supervisorContacts", "organizationChannels", "bypassToAll", "bypassContacts"} )
public class ContactData implements Serializable {

    private Employment employment;
    private Commitment commitment; // can be null if not in the context of a notification or request
    private ChannelsUserInfo userInfo;
    private boolean includeSupervisor;
    private List<ChannelData> workChannels;
    private List<ContactData> supervisorContacts;
    private List<ChannelData> organizationChannels;
    private List<ChannelData> personalChannels;
    private List<ContactData> bypassContacts;
    private List<Employment> bypassEmployments;
    private Boolean bypassToAll = null;
    private String pictureUrl;

    public ContactData() {
        // required
    }

    public ContactData(
            String serverUrl,
            Employment employment,
            ChannelsUserInfo userInfo,
            boolean includeSupervisor,
            PlanCommunity planCommunity ) {
        this.employment = employment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        init( serverUrl, planCommunity );
    }

    public ContactData( // create contact data of employment contacted in commitment
                        String serverUrl,
                        Employment employment,
                        Commitment commitment,
                        ChannelsUserInfo userInfo,
                        boolean includeSupervisor,
                        PlanCommunity planCommunity ) {
        this.employment = employment;
        this.commitment = commitment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        init( serverUrl, planCommunity );
    }

    /**
     * Find a user's contacts from san employment.
     *
     * @param employment               an employment
     * @param planCommunity             a plan community
     * @param userInfo                 a user info
     * @return a list of contact data
     */
    static public List<ContactData> findContactsFromEmployment(
            String serverUrl,
            Employment employment,
            Commitment commitment,
            PlanCommunity planCommunity,
            ChannelsUserInfo userInfo ) {
        List<ContactData> contactList = new ArrayList<ContactData>();
        Actor actor = employment.getActor();
        if ( actor.isAnonymousParticipation() ) {
            contactList.add( new ContactData(
                    serverUrl,
                    employment,
                    commitment,
                    null,
                    true,
                    planCommunity ) );
        } else {
            List<UserParticipation> otherParticipations = getOtherParticipations(
                    actor,
                    planCommunity,
                    userInfo );
            if ( otherParticipations.isEmpty() || !actor.isSingularParticipation() ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        commitment,
                        null,
                        true,
                        planCommunity ) );
            }
            for ( UserParticipation otherParticipation : otherParticipations ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        commitment,
                        otherParticipation.getParticipant(),
                        true,
                        planCommunity ) );
            }
        }
        return contactList;
    }

    private void init(
            String serverUrl,
            PlanCommunity planCommunity ) {
        initWorkChannels( planCommunity.getPlanService() );
        initPersonalChannels( planCommunity.getPlanService() );
        initSupervisorContacts( serverUrl, planCommunity );
        initOrganizationChannels( planCommunity.getPlanService() );
        initBypassContacts( serverUrl, planCommunity );
        initPictureUrl( serverUrl );
    }

     private void initPictureUrl( String serverUrl ) {
        // todo use user picture instead of actor's if available
        String url = getActor().getImageUrl();
        if ( url != null ) {
            String prefix = serverUrl.endsWith( "/" ) ? serverUrl : ( serverUrl + "/" );
            pictureUrl = StringEscapeUtils.escapeXml( url.toLowerCase().startsWith( "http" )
                    ? url
                    : prefix + url );
        }
    }

    private void initPersonalChannels( QueryService queryService  ) {
        personalChannels = new ArrayList<ChannelData>();
        if ( userInfo != null ) {
            for ( Channel channel : queryService.getUserContactInfoService().findChannels( userInfo, queryService ) ) {
                personalChannels.add( new ChannelData(
                        channel.getMedium().getId(),
                        channel.getAddress(),
                        queryService ) );
            }
        }

    }

    private void initWorkChannels( QueryService queryService ) {
        workChannels = new ArrayList<ChannelData>();
        for ( Channel channel : getActor().getEffectiveChannels() ) {
            workChannels.add( new ChannelData( channel, queryService ) );
        }
    }


    private void initOrganizationChannels( QueryService queryService ) {
        organizationChannels = new ArrayList<ChannelData>();
        for ( Channel channel : getOrganization().getEffectiveChannels() ) {
            organizationChannels.add( new ChannelData( channel, queryService ) );
        }

    }

    private void initSupervisorContacts( String serverUrl, PlanCommunity planCommunity ) {
        PlanService planService = planCommunity.getPlanService();
        UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
        supervisorContacts = new ArrayList<ContactData>();
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
                            serverUrl,
                            supervisorEmployment,
                            null,
                            false,
                            planCommunity ) );
                } else {
                    List<UserParticipation> participations = userParticipationService.getParticipationsAsAgent(
                            new Agent( supervisor ),  // todo - agents!
                            planCommunity );
                    for ( UserParticipation participation : participations ) {
                        supervisorContacts.add( new ContactData(
                                serverUrl,
                                supervisorEmployment,
                                participation.getParticipant(),
                                false,planCommunity ) );
                    }
                }
            }
        }
    }

    private void initBypassContacts(
            String serverUrl,
            PlanCommunity planCommunity ) {
        Set<Employment> bypassEmploymentSet = new HashSet<Employment>();
        if ( commitment() != null ) {
            Set<ContactData> bypassContactSet = new HashSet<ContactData>();
            for ( Employment bypassEmployment : findBypassContactEmployments( planCommunity.getPlanService() ) ) {
                bypassEmploymentSet.add( bypassEmployment );
                bypassContactSet.addAll( findContactsFromEmployment(
                        serverUrl,
                        bypassEmployment,
                        null,   // todo -- bypassing is not transitive, right?
                        planCommunity,
                        userInfo ) );
            }
            bypassContacts = new ArrayList<ContactData>( bypassContactSet );
        } else {
            bypassContacts = new ArrayList<ContactData>();
        }
        bypassEmployments = new ArrayList<Employment>( bypassEmploymentSet );
    }

    private List<Employment> findBypassContactEmployments( QueryService queryService ) {
        assert commitment != null;
        Set<Employment> bypassEmployments = new HashSet<Employment>();
        bypassToAll = commitment().getSharing().isAll();
        List<Commitment> bypassCommitments = queryService
                .findAllBypassCommitments( commitment.getSharing() );
        for ( Commitment bypassCommitment : bypassCommitments ) {
            if ( commitment().getSharing().isNotification() ) {
                if ( bypassCommitment.getCommitter().getEmployment().equals( contactedEmployment() ) ) {
                    bypassEmployments.add( bypassCommitment.getBeneficiary().getEmployment() );
                    bypassToAll = bypassToAll || bypassCommitment.getSharing().isAll();
                }
            } else { // a request
                if ( bypassCommitment.getBeneficiary().getEmployment().equals( contactedEmployment() ) ) {
                    bypassEmployments.add( bypassCommitment.getCommitter().getEmployment() );
                    bypassToAll = bypassToAll || bypassCommitment.getSharing().isAll();
                }
            }
        }
        return new ArrayList<Employment>( bypassEmployments );
    }


    // Find list of participation as actor other than by the user.
    static private List<UserParticipation> getOtherParticipations(
            Actor actor,                           // todo - agents!
            PlanCommunity planCommunity,
            ChannelsUserInfo userInfo ) {
        UserParticipationService userParticipationService = planCommunity.getUserParticipationService();
        String username = userInfo == null ? null : userInfo.getUsername();
        List<UserParticipation> otherParticipations = new ArrayList<UserParticipation>();
        List<UserParticipation> participations = userParticipationService.getParticipationsAsAgent(
                new Agent( actor ),
                planCommunity );
        for ( UserParticipation participation : participations ) {
            if ( username == null || !username.equals( participation.getParticipantUsername() ) ) {
                otherParticipations.add( participation );
            }
        }
        return otherParticipations;
    }

    public String anchor() {
        StringBuilder sb = new StringBuilder();
        sb.append( userInfo == null ? "" : userInfo.getId() );
        sb.append( "_" );
        sb.append( contactedEmployment().getActor().getId() );
        sb.append( "_" );
        sb.append( contactedEmployment().getRole().getId() );
        sb.append( "_" );
        sb.append( contactedEmployment().getOrganization().getId() );
        return sb.toString();
    }

    private Employment contactedEmployment() {
        return employment;
    }

    @XmlElement( name = "name" )
    public String getNormalizedContactName() {
        if ( userInfo == null )
            return getActor().getName();
        else
            return ChannelsUser.normalizeFullName( userInfo.getFullName() );
    }

    @XmlElement
    public String getId() {
        return commitment == null ? anchor() : null;
    }

    @XmlElement
    public String getRef() {
        return commitment == null ? null : anchor();
    }

    @XmlElement( name = "identity" )
    public EmploymentData getEmployment() {
        return userInfo == null
                ? new EmploymentData( contactedEmployment() )
                : new EmploymentData( contactedEmployment(), userInfo );
    }

    @XmlElement( name = "workChannel" )
    public List<ChannelData> getWorkChannels() {
        return workChannels;
    }

    @XmlElement( name = "supervisor" )
    public List<ContactData> getSupervisorContacts() {
        return supervisorContacts;
    }


    @XmlElement( name = "organizationChannel" )
    public List<ChannelData> getOrganizationChannels() {
        return organizationChannels;
    }

    @XmlElement( name = "personalChannel" )
    public List<ChannelData> getPersonalChannels() {
        return personalChannels;

    }

    @XmlElement
    public Boolean getBypassToAll() {
        return bypassToAll;
    }


    @XmlElement( name = "bypassContact" )
    public List<ContactData> getBypassContacts() {
        return bypassContacts;
    }

    @XmlElement
    public String getPicture() {
        return pictureUrl;
    }


    public boolean bypassToAll() {
        return bypassToAll;
    }

    private Actor getActor() {
        return contactedEmployment().getActor();
    }

    private Organization getOrganization() {
        return contactedEmployment().getOrganization();
    }

    private Actor getSupervisor() {
        return contactedEmployment().getSupervisor();
    }

    public String toLabel() {
        return getContactName() + ", " + getContactJob();
    }

    public String getContactName() {
        return userInfo != null ? userInfo.getFullName() : getActor().getName();
    }

    public String getContactJob() {
        Employment contacted = contactedEmployment();
        StringBuilder sb = new StringBuilder();
        sb.append( contacted.getTitleOrRole() );
        sb.append( ", " );
        if ( contacted.getJurisdiction() != null ) {
            sb.append( contacted.getJurisdiction().getName() );
            sb.append( ", " );
        }
        sb.append( contacted.getOrganization().getName() );
        return sb.toString();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof ContactData ) {
            ContactData other = (ContactData) object;
            return employment().equals( other.employment() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return employment.hashCode();
    }

    public Employment employment() {
        return employment;
    }

    public Commitment commitment() {
        return commitment;
    }

    public List<Employment> bypassEmployments() {
        return bypassEmployments;
    }

    public List<Long> getBypassMediumIds() {
        Set<Long> mediumIds = new HashSet<Long>();
        if ( commitment != null ) {
            for ( Channel channel : commitment.getSharing().getEffectiveChannels() ) {
                mediumIds.add( channel.getMedium().getId() );
            }
        }
        return new ArrayList<Long>( mediumIds );
    }

    public boolean forNotification() {
        return commitment != null && commitment().getSharing().isNotification();
    }

    public String username() {
        return userInfo.getUsername();
    }
}
