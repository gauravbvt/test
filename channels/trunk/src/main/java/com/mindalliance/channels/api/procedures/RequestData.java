package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
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
@XmlType( propOrder = {"id", "information", "intent", "intentText", "communicatedContext", "taskFailed", "receiptConfirmationRequested",
        "instructions", "contactAll", "maxDelay", "contacts", "mediumIds", "failureImpact",
        "consumingTask", "impactOnConsumingTask", /*"agreements",*/ "documentation"} )
public class RequestData extends AbstractFlowData {

    private List<Commitment> commitments;
    private TaskData consumingTaskData;
    private List<ContactData> contacts;
    private List<Employment> contactEmployments;
    private String impactOnConsumingTask;

    public RequestData() {
        // required
    }

    public RequestData(
            String serverUrl,
            PlanCommunity planCommunity,
            Flow request,
            boolean initiating,
            Assignment assignment,
            ChannelsUser user ) {
        super( serverUrl, planCommunity, initiating, request, assignment, user );
        initData(
                serverUrl,
                planCommunity,
                user == null ? null : user.getUserInfo() );
    }

    @Override
    public List<Employment> findContactEmployments() {
        return contactEmployments;
    }

    protected void initData(
            String serverUrl,
            PlanCommunity planCommunity,
            ChannelsUserInfo userInfo  ) {
        initCommitments( planCommunity );
        initContactEmployments( serverUrl, planCommunity, userInfo );
        initConsumingTask( serverUrl, planCommunity );
        initOtherData( planCommunity );
    }

    private void initCommitments( PlanCommunity planCommunity ) {
        commitments = new ArrayList<Commitment>(  ) ;
        for ( Commitment commitment : planCommunity.getPlanService().findAllCommitments( getSharing(), false, false ) ) {   // no unknowns, not to self
            if ( isInitiating() ) {  // requesting
                if ( commitment.getBeneficiary().equals( getAssignment() ) ) {
                    commitments.add( commitment );
                }
            } else {   // replying
                if ( commitment.getCommitter().equals( getAssignment() ) ) {
                    commitments.add( commitment );
                }
            }
        }
    }

    private void initContactEmployments(
            String serverUrl,
            PlanCommunity planCommunity,
            ChannelsUserInfo userInfo ) {
        Set<Employment> contactedEmployments = new HashSet<Employment>();
        Set<ContactData> contactDataSet = new HashSet<ContactData>(  );
        for ( Commitment commitment : commitments ) {
            if ( isInitiating() ) {  // asking
                Employment employment = commitment.getCommitter().getEmployment();
                contactedEmployments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmployment(
                        serverUrl,
                        employment,
                        commitment,
                        planCommunity,
                        userInfo ) ) ;
            } else { // replying
                Employment employment = commitment.getBeneficiary().getEmployment();
                contactedEmployments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmployment(
                        serverUrl,
                         employment,
                        commitment,
                        planCommunity,
                        userInfo ) ) ;
            }
        }
        contactEmployments = new ArrayList<Employment>( contactedEmployments );
        contacts = new ArrayList<ContactData>( contactDataSet );
    }

    private void initConsumingTask( String serverUrl, PlanCommunity planCommunity ) {
        if ( !isInitiating() )  {
            consumingTaskData = new TaskData(
                    serverUrl,
                    planCommunity,
                    (Part)getSharing().getTarget(),
                    getUser() );
            if ( flow().isTerminatingToTarget() )
                impactOnConsumingTask = "terminates";
            else if ( flow().isCritical() )
                impactOnConsumingTask = "critical";
            else
                impactOnConsumingTask = "useful";
        }
        else
            consumingTaskData = null;

    }

    @Override
    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getSharing() );
    }

    @Override
    @XmlElement
    public String getIntent() {
        return super.getIntent();
    }

    @XmlElement
    @Override
    public String getIntentText() {
        return super.getIntentText();
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
        return contacts;
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

    @XmlElement
    public String getImpactOnConsumingTask() {
        return impactOnConsumingTask;
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

    @XmlElement
    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public boolean isNotification() {
        return false;
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


}
