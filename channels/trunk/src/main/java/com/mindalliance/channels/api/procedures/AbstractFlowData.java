package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Level;
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
    private List<ContactData> bypassContacts;
    private List<Employment> employments;
    private List<Employment> bypassEmployments;
    private Level failureSeverity;

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

    protected void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        initContacts( planService, planParticipationService );
        initBypassContact( planService, planParticipationService );
        initFailureSeverity( planService );
    }

    private void initFailureSeverity( PlanService planService ) {
        failureSeverity = planService.computeSharingPriority( getSharing() );
    }

    private void initBypassContact( PlanService planService, PlanParticipationService planParticipationService ) {
        bypassContacts = new ArrayList<ContactData>();
        for ( Employment employment : ListBypassContactEmployments() ) {
            bypassContacts.addAll( findContactsFromEmployment( employment, planService, planParticipationService ) );
        }
    }

    private void initContacts( PlanService planService, PlanParticipationService planParticipationService ) {
        contacts = new ArrayList<ContactData>();
        for ( Employment employment : listContactEmployments() ) {
            contacts.addAll( findContactsFromEmployment( employment, planService, planParticipationService ) );
        }
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

    public boolean isContextCommunicated() {
        return sharing.isReferencesEventPhase();
    }

    public String getCommunicableContext() {
        return sharing.getSegment().getPhaseEventTitle();
    }

    public String getCommunicatedContext() {
        return sharing.isReferencesEventPhase()
                ? sharing.getSegment().getPhaseEventTitle()
                : null;
    }

    public List<ContactData> getContacts() {
        return contacts;
    }

    public List<ContactData> getBypassContacts() {
        return bypassContacts;

    }

    private List<ContactData> findContactsFromEmployment(
            Employment employment,
            PlanService planService,
            PlanParticipationService planParticipationService ) {
        return ContactData.findContactsFromEmployment(
                employment,
                planService,
                planParticipationService,
                getUser()
        );

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
        return getFailureSeverity().getNegativeLabel();
    }

    public Level getFailureSeverity() {
        return failureSeverity;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : allEmployments() ) {
            ids.add( employment.getOrganization().getId() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : allEmployments() ) {
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
        for ( Employment employment : allEmployments() ) {
            ids.add( employment.getRole().getId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : allEmployments() ) {
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

    protected List<Employment> listContactEmployments() {
        if ( employments == null ) {
            employments = findContactEmployments();
        }
        return employments;
    }

    public abstract boolean isNotification();

    protected List<Employment> ListBypassContactEmployments() {
        if ( bypassEmployments == null ) {
            bypassEmployments = findBypassContactEmployments();
        }
        return bypassEmployments;
    }

    private List<Employment> allEmployments() {
        List<Employment> allEmployments = new ArrayList<Employment>();
        allEmployments.addAll( listContactEmployments() );
        allEmployments.addAll( ListBypassContactEmployments() );
        return allEmployments;
    }

    public abstract List<Employment> findContactEmployments();

    public abstract List<Employment> findBypassContactEmployments();


}
