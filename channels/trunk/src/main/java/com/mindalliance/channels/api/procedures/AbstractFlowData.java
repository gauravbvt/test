package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 1:37 PM
 */
public abstract class AbstractFlowData extends AbstractProcedureElementData {

    private Flow sharing;
    private List<ContactData> contacts;
    private List<Employment> employments;

    public AbstractFlowData() {
        // required
    }

    public AbstractFlowData(
            Flow sharing,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
        this.sharing = sharing;
    }

    protected Flow getSharing() {
        return sharing;
    }

    public boolean getReceiptConfirmationRequested() {
        return getSharing().isReceiptConfirmationRequested();
    }

    public InformationData getInformation() {
        return new InformationData( getSharing() );
    }

    public String getIntent() {
        return getSharing().getIntent() == null
                ? null
                : getSharing().getIntent().getLabel();
    }

    public List<ContactData> getContacts() {
        if ( contacts == null ) {
            contacts = new ArrayList<ContactData>();
            for ( Employment employment : contactEmployments() ) {
                Actor actor = employment.getActor();
                if ( actor.isAnonymousParticipation() ) {
                    contacts.add( new ContactData(
                            employment,
                            null,
                            true,
                            getPlanService(),
                            getPlanParticipationService() ) );
                } else {
                    List<PlanParticipation> otherParticipations = getOtherParticipations( actor );
                    if ( otherParticipations.isEmpty() ) {
                        contacts.add( new ContactData(
                                employment,
                                null,
                                true,
                                getPlanService(),
                                getPlanParticipationService() ) );
                    } else {
                        for ( PlanParticipation otherParticipation : otherParticipations ) {
                            contacts.add( new ContactData(
                                    employment,
                                    otherParticipation.getParticipant(),
                                    true,
                                    getPlanService(),
                                    getPlanParticipationService() ) );
                        }
                    }
                }
            }
        }
        return contacts;
    }

    private List<PlanParticipation> getOtherParticipations( Actor actor ) {
        List<PlanParticipation> otherParticipations = new ArrayList<PlanParticipation>(  );
        List<PlanParticipation> participations = getPlanParticipationService().getParticipations(
                getPlan(),
                actor,
                getPlanService() );
        for ( PlanParticipation participation : participations ) {
            if ( !getUsername().equals( participation.getParticipant().getUsername() ) ) {
                otherParticipations.add( participation );
            }
        }
        return otherParticipations;
    }

    public List<Long> getMediumIds() {
        List<Long> media = new ArrayList<Long>();
        for ( TransmissionMedium medium : getSharing().transmissionMedia() ) {
            media.add( medium.getId() );
        }
        return media;
    }

    public boolean getTaskFailed() {
        return sharing.isIfTaskFails();
    }

    public boolean getContactAll() {
        return getSharing().isAll();
    }

    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( getSharing().getMaxDelay() );
    }

    public String getInstructions() {
        String instructions = getSharing().getDescription();
        return instructions == null
                ? null
                : instructions;
    }

    public String getFailureImpact() {
        return getPlanService().computeSharingPriority( getSharing() ).getNegativeLabel();
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : contactEmployments() ) {
            ids.add( employment.getOrganization().getId() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : contactEmployments() ) {
            ids.add( employment.getActor().getId() );
            if ( employment.getSupervisor() != null )
                ids.add( employment.getSupervisor().getId() );
        }
        for ( TransmissionMedium medium : getSharing().transmissionMedia() ) {
            if ( medium.getQualification() != null )
                ids.add( medium.getQualification().getId() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : contactEmployments() ) {
            ids.add( employment.getRole().getId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : contactEmployments() ) {
            if ( employment.getJurisdiction() != null )
                ids.add( employment.getJurisdiction().getId() );
        }
        return ids;
    }


/*    public List<AgreementData> getAgreements() {
        List<AgreementData> agreements = new ArrayList<AgreementData>(  );
        for ( Agreement agreement : getPlanService().findAllConfirmedAgreementsCovering( sharing ) ) {
            agreements.add( new AgreementData( agreement ) );
        }
        return agreements;
    }*/


    public DocumentationData getDocumentation() {
        return new DocumentationData( getSharing() );
    }

    protected List<Employment> contactEmployments() {
        if ( employments == null ) {
            employments = findContactEmployments();
        }
        return employments;
    }

    protected abstract List<Employment> findContactEmployments();

}
