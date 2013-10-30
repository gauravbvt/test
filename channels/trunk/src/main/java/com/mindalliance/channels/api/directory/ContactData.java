package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
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
 * Contact data.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 9:13 PM
 */
@XmlType(propOrder = {"id", "ref", "normalizedContactName", "picture", "employment", "workChannels",
        "personalChannels", "supervisorContacts", "organizationChannels", "bypassToAll", "bypassContacts"})
public class ContactData implements Serializable {

    private static final String USER_PHOTO_PATH = "users/photos/";

    private CommunityEmployment employment;
    private CommunityCommitment commitment; // can be null if not in the context of a notification or request
    private UserRecord userInfo;
    private boolean includeSupervisor;
    private List<ChannelData> workChannels;
    private List<ContactData> supervisorContacts;
    private List<ChannelData> organizationChannels;
    private List<ChannelData> personalChannels;
    private List<ContactData> bypassContacts;
    private List<CommunityEmployment> bypassEmployments;
    private Boolean bypassToAll = null;
    private String pictureUrl;

    public ContactData() {
        // required
    }

    public ContactData(
            String serverUrl,
            CommunityEmployment employment,
            UserRecord userInfo,
            boolean includeSupervisor,
            CommunityService communityService ) {
        this.employment = employment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        init( serverUrl, communityService );
    }

    public ContactData( // create contact data of employment contacted in commitment
                        String serverUrl,
                        CommunityEmployment employment,
                        CommunityCommitment commitment,
                        UserRecord userInfo,
                        boolean includeSupervisor,
                        CommunityService communityService ) {
        this.employment = employment;
        this.commitment = commitment;
        this.userInfo = userInfo;
        this.includeSupervisor = includeSupervisor;
        init( serverUrl, communityService );
    }

    /**
     * Find a user's contacts from an employment.
     *
     * @param employment       an employment
     * @param communityService a community service
     * @return a list of contact data
     */
    static public List<ContactData> findContactsFromEmployment(
            String serverUrl,
            CommunityEmployment employment,
            CommunityService communityService ) {
        List<ContactData> contactList = new ArrayList<ContactData>();
        Agent agent = employment.getAgent();
        if ( agent.isAnonymousParticipation() ) {
            contactList.add( new ContactData(
                    serverUrl,
                    employment,
                    null,
                    true,
                    communityService ) );
        } else {
            List<UserParticipation> participations = getParticipations(
                    agent,
                    communityService );
            if ( participations.isEmpty() || !agent.isSingularParticipation() ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        null,
                        true,
                        communityService ) );
            }
            for ( UserParticipation otherParticipation : participations ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        otherParticipation.getParticipant( communityService ),
                        true,
                        communityService ) );
            }
        }
        return contactList;
    }


    /**
     * Find a user's contacts from an employment and commitment.
     *
     * @param employment       an employment
     * @param commitment       a community commitment
     * @param communityService a community service
     * @param userInfo         a user info
     * @return a list of contact data
     */
    static public List<ContactData> findContactsFromEmploymentAndCommitment(
            String serverUrl,
            CommunityEmployment employment,
            CommunityCommitment commitment,
            CommunityService communityService,
            UserRecord userInfo ) {
        List<ContactData> contactList = new ArrayList<ContactData>();
        Agent agent = employment.getAgent();
        if ( agent.isAnonymousParticipation() ) {
            contactList.add( new ContactData(
                    serverUrl,
                    employment,
                    commitment,
                    null,
                    true,
                    communityService ) );
        } else {
            List<UserParticipation> otherParticipations = getOtherParticipations(
                    agent,
                    communityService,
                    userInfo );
            if ( otherParticipations.isEmpty() || !agent.isSingularParticipation() ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        commitment,
                        null,
                        true,
                        communityService ) );
            }
            for ( UserParticipation otherParticipation : otherParticipations ) {
                contactList.add( new ContactData(
                        serverUrl,
                        employment,
                        commitment,
                        otherParticipation.getParticipant( communityService ),
                        true,
                        communityService ) );
            }
        }
        return contactList;
    }

    private void init(
            String serverUrl,
            CommunityService communityService ) {
        initActorChannels( communityService );
        initPersonalChannels( communityService );
        initSupervisorContacts( serverUrl, communityService );
        initOrganizationChannels( communityService );
        initBypassContacts( serverUrl, communityService );
        initPictureUrl( serverUrl );
    }

    private void initPictureUrl( String serverUrl ) {
        String url = userInfo == null
                ? getAgent().getActor().getImageUrl()
                : userInfo.getPhoto();
        if ( url != null ) {
            String prefix = serverUrl.endsWith( "/" ) ? serverUrl : ( serverUrl + "/" );
            prefix += USER_PHOTO_PATH;
            pictureUrl = StringEscapeUtils.escapeXml( url.toLowerCase().startsWith( "http" )
                    ? url
                    : prefix + url );
        }
    }

    private void initPersonalChannels( CommunityService communityService ) {
        personalChannels = new ArrayList<ChannelData>();
        if ( userInfo != null ) {
            for ( Channel channel : communityService.getPlanService()
                    .getUserInfoService().findChannels( userInfo, communityService ) ) {
                personalChannels.add( new ChannelData(
                        channel.getMedium().getId(),
                        channel.getAddress(),
                        communityService ) );
            }
        }
    }

    private void initActorChannels( CommunityService communityService ) {
        workChannels = new ArrayList<ChannelData>();
        for ( Channel channel : getAgent().getActor().getEffectiveChannels() ) {
            workChannels.add( new ChannelData( channel, communityService ) );
        }
    }


    private void initOrganizationChannels( CommunityService communityService ) {
        organizationChannels = new ArrayList<ChannelData>();
        for ( Channel channel : getAgency().getEffectiveChannels() ) {
            organizationChannels.add( new ChannelData( channel, communityService ) );
        }

    }

    private void initSupervisorContacts( String serverUrl, CommunityService communityService ) {
        ParticipationManager participationManager = communityService.getParticipationManager();
        supervisorContacts = new ArrayList<ContactData>();
        if ( includeSupervisor && getSupervisor() != null ) {
            Agent supervisor = getSupervisor();
            CommunityEmployment sameOrgEmployment = null;
            CommunityEmployment parentOrgEmployment = null;
            Iterator<CommunityEmployment> iter = communityService.getParticipationManager()
                    .findAllEmploymentsForAgent( supervisor, communityService )
                    .iterator();
            List<Agency> ancestors = getAgency().ancestors( communityService );
            while ( sameOrgEmployment == null && iter.hasNext() ) {
                CommunityEmployment supervisorEmployment = iter.next();
                if ( supervisorEmployment.getEmployer().equals( getAgency() ) ) {
                    sameOrgEmployment = supervisorEmployment;
                } else if ( parentOrgEmployment == null
                        && ancestors.contains( supervisorEmployment.getEmployer() ) ) {
                    parentOrgEmployment = supervisorEmployment;
                }
            }
            CommunityEmployment supervisorEmployment = sameOrgEmployment != null
                    ? sameOrgEmployment
                    : parentOrgEmployment;
            if ( supervisorEmployment != null ) {
                if ( supervisor.isAnonymousParticipation() ) {
                    supervisorContacts.add( new ContactData(
                            serverUrl,
                            supervisorEmployment,
                            null,
                            false,
                            communityService ) );
                } else {
                    List<UserParticipation> participations = participationManager.getParticipationsAsAgent(
                            supervisor,
                            communityService );
                    for ( UserParticipation participation : participations ) {
                        supervisorContacts.add( new ContactData(
                                serverUrl,
                                supervisorEmployment,
                                participation.getParticipant( communityService ),
                                false,
                                communityService ) );
                    }
                }
            }
        }
    }

    private void initBypassContacts(
            String serverUrl,
            CommunityService communityService ) {
        Set<CommunityEmployment> bypassEmploymentSet = new HashSet<CommunityEmployment>();
        if ( commitment() != null ) {
            Set<ContactData> bypassContactSet = new HashSet<ContactData>();
            for ( CommunityEmployment bypassEmployment : findBypassContactEmployments( communityService ) ) {
                bypassEmploymentSet.add( bypassEmployment );
                bypassContactSet.addAll( findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        bypassEmployment,
                        null,   // todo -- bypassing is not transitive, right?
                        communityService,
                        userInfo ) );
            }
            bypassContacts = new ArrayList<ContactData>( bypassContactSet );
        } else {
            bypassContacts = new ArrayList<ContactData>();
        }
        bypassEmployments = new ArrayList<CommunityEmployment>( bypassEmploymentSet );
    }

    private List<CommunityEmployment> findBypassContactEmployments( CommunityService communityService ) {
        assert commitment != null;
        Set<CommunityEmployment> bypassEmployments = new HashSet<CommunityEmployment>();
        bypassToAll = commitment().getSharing().isAll();
        CommunityCommitments bypassCommitments =
                communityService.findAllBypassCommitments( commitment.getSharing() );
        for ( CommunityCommitment bypassCommitment : bypassCommitments ) {
            if ( commitment().getSharing().isNotification() ) {
                if ( bypassCommitment.getCommitter().getCommunityEmployment().equals( contactedEmployment() ) ) {
                    bypassEmployments.add( bypassCommitment.getBeneficiary().getCommunityEmployment() );
                    bypassToAll = bypassToAll || bypassCommitment.getSharing().isAll();
                }
            } else { // a request
                if ( bypassCommitment.getBeneficiary().getCommunityEmployment().equals( contactedEmployment() ) ) {
                    bypassEmployments.add( bypassCommitment.getCommitter().getCommunityEmployment() );
                    bypassToAll = bypassToAll || bypassCommitment.getSharing().isAll();
                }
            }
        }
        return new ArrayList<CommunityEmployment>( bypassEmployments );
    }

    // Find list of participation as agent.
    static private List<UserParticipation> getParticipations(
            Agent agent,
            CommunityService communityService ) {
        ParticipationManager participationManager = communityService.getParticipationManager();
        return participationManager.getParticipationsAsAgent(
                agent,
                communityService );
     }

    // Find list of participation as agent other than by the user.
    static private List<UserParticipation> getOtherParticipations(
            Agent agent,
            CommunityService communityService,
            UserRecord userInfo ) {
        ParticipationManager participationManager = communityService.getParticipationManager();
        String username = userInfo == null ? null : userInfo.getUsername();
        List<UserParticipation> otherParticipations = new ArrayList<UserParticipation>();
        List<UserParticipation> participations = participationManager.getParticipationsAsAgent(
                agent,
                communityService );
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
        sb.append( contactedEmployment().getAgent().getActorId() );
        sb.append( "_" );
        sb.append( contactedEmployment().getRole().getId() );
        sb.append( "_" );
        sb.append( contactedEmployment().getEmployer().getUid() );
        return sb.toString();
    }

    private CommunityEmployment contactedEmployment() {
        return employment;
    }

    @XmlElement(name = "name")
    public String getNormalizedContactName() {
        if ( userInfo == null )
            return getAgent().getName();
        else
            return ChannelsUser.normalizeFullName( userInfo.getFullName() );
    }

    public String getUserFullName() {
        return userInfo == null ? null : ChannelsUser.normalizeFullName( userInfo.getFullName() );
    }

    @XmlElement
    public String getId() {
        return commitment == null ? anchor() : null;
    }

    @XmlElement
    public String getRef() {
        return commitment == null ? null : anchor();
    }

    @XmlElement(name = "identity")
    public EmploymentData getEmployment() {
        return new EmploymentData( contactedEmployment() );
    }

    @XmlElement(name = "workChannel")
    public List<ChannelData> getWorkChannels() {
        return workChannels;
    }

    @XmlElement(name = "supervisor")
    public List<ContactData> getSupervisorContacts() {
        return supervisorContacts;
    }


    @XmlElement(name = "organizationChannel")
    public List<ChannelData> getOrganizationChannels() {
        return organizationChannels;
    }

    @XmlElement(name = "personalChannel")
    public List<ChannelData> getPersonalChannels() {
        return personalChannels;

    }

    @XmlElement
    public Boolean getBypassToAll() {
        return bypassToAll;
    }


    @XmlElement(name = "bypassContact")
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

    private Agent getAgent() {
        return contactedEmployment().getAgent();
    }

    private Agency getAgency() {
        return contactedEmployment().getEmployer();
    }

    private Agent getSupervisor() {
        return contactedEmployment().getSupervisor();
    }

    public String toLabel() {
        return getContactName() + ", " + getContactJob();
    }

    public String getContactName() {
        return userInfo != null ? userInfo.getFullName() : getAgent().getName();
    }

    public String getContactJob() {
        CommunityEmployment contacted = contactedEmployment();
        StringBuilder sb = new StringBuilder();
        sb.append( contacted.getTitle() );
        sb.append( ", " );
        if ( contacted.getJurisdiction() != null ) {
            sb.append( contacted.getJurisdiction().getName() );
            sb.append( ", " );
        }
        sb.append( contacted.getEmployer().getName() );
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

    public CommunityEmployment employment() {
        return employment;
    }

    public CommunityCommitment commitment() {
        return commitment;
    }

    public List<CommunityEmployment> bypassEmployments() {
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
