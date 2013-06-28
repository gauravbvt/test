package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.db.data.users.UserRecord;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

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
@XmlType( propOrder = {"id", "information", "intent", "intentText", "communicatedContext", "taskFailed", "receiptConfirmationRequested",
        "instructions", "contactAll", "maxDelay", "contacts", "mediumIds", "failureImpact",
        "consumingTask", "impactOnConsumingTask", "documentation"/*, "agreements"*/} )
public class NotificationData extends AbstractFlowData {

    private List<CommunityCommitment> commitments;
    private List<CommunityEmployment> contactEmployments;
    private List<ContactData> contacts;
    private TaskData consumingTaskData;
    private String impactOnConsumingTask;

    public NotificationData() {
        // required
    }

    public NotificationData(
            String serverUrl,
            CommunityService communityService,
            Flow notification,
            boolean initiating,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        super( serverUrl,  communityService, initiating, notification, assignment, user );
        initData(
                serverUrl,
                communityService,
                user == null ? null : user.getUserRecord() );
    }

    @Override
    public List<CommunityEmployment> findContactEmployments() {
        return contactEmployments;
    }

    protected void initData(
            String serverUrl,
            CommunityService communityService,
            UserRecord userInfo ) {
        initCommitments( communityService );
        initContactEmployments( serverUrl, communityService, userInfo );
        initConsumingTask( serverUrl, communityService );
        initOtherData( communityService );
    }

    private void initCommitments( CommunityService communityService ) {
        commitments = new ArrayList<CommunityCommitment>();
        for ( CommunityCommitment commitment : communityService.findAllCommitments( getSharing(), false ) ) {   // no not to self
            if ( isInitiating() ) {
                if ( commitment.getCommitter().equals( getAssignment() ) ) {
                    commitments.add( commitment );
                }
            } else {
                if ( commitment.getBeneficiary().equals( getAssignment() ) ) {
                    commitments.add( commitment );
                }
            }
        }
    }

    private void initContactEmployments(
            String serverUrl,
            CommunityService communityService,
            UserRecord userInfo ) {
        Set<CommunityEmployment> employments = new HashSet<CommunityEmployment>();
        Set<ContactData> contactDataSet = new HashSet<ContactData>();
        for ( CommunityCommitment commitment : commitments ) {
            if ( isInitiating() ) {  // notifying
                CommunityEmployment employment = commitment.getBeneficiary().getEmployment();
                employments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        employment,
                        commitment,
                        communityService,
                        userInfo ) );
            } else { // being notified
                CommunityEmployment employment = commitment.getCommitter().getEmployment();
                employments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        employment,
                        commitment,
                        communityService,
                        userInfo ) );
            }
        }
        contactEmployments = new ArrayList<CommunityEmployment>( employments );
        contacts = new ArrayList<ContactData>( contactDataSet );
    }

    private void initConsumingTask( String serverUrl, CommunityService communityService ) {
        if ( !isInitiating() )
            consumingTaskData = null;
        else {
            Part consumingPart = (Part) getNotification().getTarget();
            consumingTaskData = new TaskData(
                    serverUrl,
                    communityService,
                    consumingPart,
                    getUser() );
            if ( flow().isTriggeringToTarget() )
                impactOnConsumingTask = "triggers";
            else if ( flow().isTerminatingToTarget() )
                impactOnConsumingTask = "terminates";
            else if ( flow().isCritical() )
                impactOnConsumingTask = "critical";
            else
                impactOnConsumingTask = "useful";
        }

    }

    @XmlElement
    public String getImpactOnConsumingTask() {
        return impactOnConsumingTask;
    }

    @Override
    @XmlElement
    public SharedInformationData getInformation() {
        return new SharedInformationData( getNotification() );
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
        return super.getInstructions();
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
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    @XmlElement
    @Override
    public String getIntentText() {
        return super.getIntentText();
    }

    @XmlElement
    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public boolean isNotification() {
        return true;
    }

    /*   @Override
        @XmlElement( name = "agreement" )
        public List<AgreementData> getAgreements() {
            return super.getAgreements();
        }
    */

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

    private Flow getNotification() {
        return getSharing();
    }

}
