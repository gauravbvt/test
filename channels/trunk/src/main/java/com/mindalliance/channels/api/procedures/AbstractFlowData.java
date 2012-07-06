package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
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

    private String serverUrl;
    private boolean initiating;
    private Flow flow;
    private Level failureSeverity;
    private List<Employment> allEmployments;
    private DocumentationData documentation;

    public AbstractFlowData() {
        // required
    }

    public AbstractFlowData(
            String serverUrl,
            boolean initiating,
            Flow flow,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
        this.serverUrl = serverUrl;
        this.initiating = initiating;
        this.flow = flow;
    }

    protected void initOtherData( PlanService planService ) {
        initFailureSeverity( planService );
        documentation = new DocumentationData( serverUrl, getSharing() );
    }

    private void initFailureSeverity( PlanService planService ) {
        failureSeverity = planService.computeSharingPriority( getSharing() );
    }

    public boolean isInitiating() {
        return initiating;
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
        return getSharing().isReferencesEventPhase();
    }

    public String getCommunicableContext() {
        return getSharing().getSegment().getPhaseEventTitle();
    }

    public String getCommunicatedContext() {
        return getSharing().isReferencesEventPhase()
                ? getSharing().getSegment().getPhaseEventTitle()
                : null;
    }

    private List<ContactData> findContactsFromEmployment(
            Employment employment,
            Commitment commitment,
            PlanService planService,
            PlanParticipationService planParticipationService ) {
        return ContactData.findContactsFromEmployment(
                serverUrl,
                employment,
                commitment,
                planService,
                planParticipationService,
                getUser() == null ? null : getUser().getUserInfo()
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
        return getSharing().isIfTaskFails();
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
        for ( Employment employment : findAllEmployments() ) {
            ids.add( employment.getOrganization().getId() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : findAllEmployments() ) {
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
        for ( Employment employment : findAllEmployments() ) {
            ids.add( employment.getRole().getId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Employment employment : findAllEmployments() ) {
            if ( employment.getJurisdiction() != null )
                ids.add( employment.getJurisdiction().getId() );
        }
        return ids;
    }

    private List<Employment> findAllEmployments() {
        if ( allEmployments == null ) {
            Set<Employment> employmentSet = new HashSet<Employment>();
            for ( ContactData contactData : getContacts() ) {
                employmentSet.add( contactData.employment() );
                employmentSet.addAll( contactData.bypassEmployments() );
            }
            allEmployments = new ArrayList<Employment>( employmentSet );
        }
        return allEmployments;
    }

    public abstract List<ContactData> getContacts();


/*    public List<AgreementData> getAgreements() {
        List<AgreementData> agreements = new ArrayList<AgreementData>(  );
        for ( Agreement agreement : getPlanService().findAllConfirmedAgreementsCovering( sharing ) ) {
            agreements.add( new AgreementData( agreement ) );
        }
        return agreements;
    }*/


    public DocumentationData getDocumentation() {
        return documentation;
    }


    public abstract boolean isNotification();


    public Flow getSharing() {
        return flow;
    }

    public abstract List<Employment> findContactEmployments();

    public Flow flow() {
        return flow;
    }
}
