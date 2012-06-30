package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a request.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
@XmlType( propOrder = {"information", "intent", "communicatedContext", "taskFailed", "receiptConfirmationRequested",
        "instructions", "contactAll", "maxDelay", "contacts", "bypassContacts", "mediumIds", "failureImpact",
        "consumingTask", /*"agreements",*/ "documentation"} )
public class RequestData extends AbstractFlowData {

    /**
     * Whether the assignment is issuing a request (false) or a reply (true).
     */
    private boolean replying;
    private TaskData consumingTaskData;
    private List<Employment> contactEmployments;
    private List<Employment> bypassContactEmployments;

    public RequestData() {
        // required
    }

    public RequestData(
            Commitment request,
            boolean replying,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( request, assignment, planService, planParticipationService, user );
        this.replying = replying;
        initData( planService, planParticipationService );
    }

    protected void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        initContactEmployments( planService );
        initBypassContactEmployments( planService );
        initConsumingTask( planService, planParticipationService );
        super.initData( planService, planParticipationService );
    }

    private void initBypassContactEmployments( PlanService planService ) {
        Set<Employment> contacts = new HashSet<Employment>();
        if ( !replying ) {
            List<Commitment> bypassCommitments = planService
                    .findAllBypassCommitments( getRequest().getSharing() );
            if ( !bypassCommitments.isEmpty() ) {
                Actor assignedActor = getAssignment().getActor();
                List<Actor> directContacts = findDirectContacts();
                for ( Commitment commitment : bypassCommitments ) {
                    if ( directContacts.contains( commitment.getBeneficiary().getActor() )
                            && !commitment.getCommitter().getActor().equals( assignedActor )
                            && !directContacts.contains( commitment.getCommitter().getActor() ) ) {
                        contacts.add( commitment.getCommitter().getEmployment() );
                    }
                }
            }
        }
        bypassContactEmployments = new ArrayList<Employment>( contacts );

    }

    private void initContactEmployments( PlanService planService ) {
        Set<Employment> contacts = new HashSet<Employment>();
        Actor assignedActor = getAssignment().getActor();
        List<Commitment> commitments = planService.findAllCommitments( getRequest().getSharing() );
        for ( Commitment commitment : commitments ) {
            if ( replying ) {
                if ( commitment.getCommitter().getActor().equals( assignedActor )
                        && !commitment.getBeneficiary().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getBeneficiary().getEmployment() );
                }
            } else { // asking
                if ( commitment.getBeneficiary().getActor().equals( assignedActor )
                        && !commitment.getCommitter().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getCommitter().getEmployment() );
                }
            }
        }
        contactEmployments = new ArrayList<Employment>( contacts );

    }

    private void initConsumingTask( PlanService planService, PlanParticipationService planParticipationService ) {
        if ( replying )
            consumingTaskData = new TaskData(
                    getRequest().getBeneficiary(),
                    planService,
                    planParticipationService,
                    getUser() );
        else
            consumingTaskData = null;

    }

    @Override
    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getRequest().getSharing() );
    }

    @Override
    @XmlElement
    public String getIntent() {
        return super.getIntent();
    }

    @Override
    @XmlElement
    public String getCommunicatedContext() {
        return super.getCommunicatedContext();
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
        return StringEscapeUtils.escapeXml( super.getInstructions() );
    }

    @Override
    @XmlElement( name = "contact" )
    public List<ContactData> getContacts() {
        return super.getContacts();
    }

    @Override
    @XmlElement( name = "disintermediatedContact" )
    public List<ContactData> getBypassContacts() {
        return super.getBypassContacts();
    }

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
        return consumingTaskData;
    }

/*
    @Override
    @XmlElement( name = "agreement" )
    public List<AgreementData> getAgreements() {
        return super.getAgreements();
    }
*/

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    @Override
    public boolean isNotification() {
        return false;
    }

    @Override
    public List<Employment> findContactEmployments() {
        return contactEmployments;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    // Don't repeat any (direct) contact employment
    public List<Employment> findBypassContactEmployments() {
        return bypassContactEmployments;
    }

    @SuppressWarnings( "unchecked" )
    private List<Actor> findDirectContacts() {
        return (List<Actor>) CollectionUtils.collect(
                findContactEmployments(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Employment) input ).getActor();
                    }
                }
        );
    }

    private Commitment getRequest() {
        return getCommitment();
    }

}
