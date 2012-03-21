package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a notification.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
@XmlType( propOrder = {"information", "intent", "taskFailed", "receiptConfirmationRequested", "instructions", "contactAll",
        "maxDelay", "contacts", "mediumIds", "failureImpact", "consumingTask", "documentation"/*, "agreements"*/} )
public class NotificationData extends AbstractFlowData {

    private boolean consuming;

    public NotificationData() {
        // required
    }

    public NotificationData(
            Flow notification,
            boolean consuming,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( notification, assignment, planService, planParticipationService, user );
        this.consuming = consuming;
    }

    @Override
    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getNotification() );
    }

    @Override
    @XmlElement
    public String getIntent() {
        return super.getIntent();
    }

    @Override
    @XmlElement
    public boolean getTaskFailed() {
        return super.getTaskFailed();
    }


    @Override
    @XmlElement
    public boolean getReceiptConfirmationRequested() {
        return super.getReceiptConfirmationRequested();
    }


    @Override
    @XmlElement
    public String getInstructions() {
        return super.getInstructions();
    }

    @Override
    @XmlElement( name = "contact" )
    public List<ContactData> getContacts() {
        return super.getContacts();
    }

    // TODO - add disintermediated contacts notifying or to notify

    @Override
    @XmlElement( name = "preferredTransmissionMedium" )
    public List<Long> getMediumIds() {
        return super.getMediumIds();
    }

    @Override
    @XmlElement
    public boolean getContactAll() {
        return super.getContactAll();
    }

    @Override
    @XmlElement
    public TimeDelayData getMaxDelay() {
        return super.getMaxDelay();
    }

    @Override
    @XmlElement
    public String getFailureImpact() {
        return super.getFailureImpact();
    }

    @XmlElement
    public TaskData getConsumingTask() {
        if ( consuming )
            return null;
        else
            return new TaskData( getNotification().getContactedPart(),
                    getPlanService(),
                    getPlanParticipationService(),
                    getUser() );
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    /*   @Override
        @XmlElement( name = "agreement" )
        public List<AgreementData> getAgreements() {
            return super.getAgreements();
        }
    */

    @Override
    protected List<Employment> findContactEmployments() {
        Set<Employment> contacts = new HashSet<Employment>();
        Actor assignedActor = getAssignment().getActor();
        List<Commitment> commitments = getPlanService().findAllCommitments( getNotification() );
        for ( Commitment commitment : commitments ) {
            if ( consuming ) {
                if ( commitment.getBeneficiary().getActor().equals( assignedActor )
                        && !commitment.getCommitter().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getCommitter().getEmployment() );
                }
            } else { // producing
                if ( commitment.getCommitter().getActor().equals( assignedActor )
                        && !commitment.getBeneficiary().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getBeneficiary().getEmployment() );
                }
            }
        }
        return new ArrayList<Employment>( contacts );
    }

    private Flow getNotification() {
        return getSharing();
    }

}
