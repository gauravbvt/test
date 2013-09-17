package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.community.protocols.CommunityEmployment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.db.data.users.UserRecord;
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
        "consumingTask", "impactOnConsumingTask", "documentation"} )
public class RequestData extends AbstractFlowData {

    private List<CommunityCommitment> commitments;
    private TaskData consumingTaskData;
    private List<ContactData> contacts;
    private List<CommunityEmployment> contactEmployments;
    private String impactOnConsumingTask;
    private AssignmentData assignmentData;

    public RequestData() {
        // required
    }

    public RequestData(
            String serverUrl,
            CommunityService communityService,
            Flow request,
            boolean initiating,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        super( serverUrl, communityService, initiating, request, assignment, user );
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
            UserRecord userInfo  ) {
        initCommitments( communityService );
        initContactEmployments( serverUrl, communityService, userInfo );
        initConsumingTask( serverUrl, communityService );
        initAssignmentData( serverUrl, communityService, new ChannelsUser( userInfo ) );
        initOtherData( communityService );
    }

    private void initAssignmentData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        CommunityCommitments commitments = communityService.findAllCommitments( getSharing(), false );
        assignmentData = new AssignmentData(
                serverUrl,
                getAssignment(),
                commitments.benefiting( getAssignment() ),
                commitments.committing( getAssignment() ),
                communityService,
                user
        );
    }

    private void initCommitments( CommunityService communityService ) {
        commitments = new ArrayList<CommunityCommitment>(  ) ;
        for ( CommunityCommitment commitment : communityService.findAllCommitments( getSharing(), false ) ) {   // not to self
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
            CommunityService communityService,
            UserRecord userInfo ) {
        Set<CommunityEmployment> contactedEmployments = new HashSet<CommunityEmployment>();
        Set<ContactData> contactDataSet = new HashSet<ContactData>(  );
        for ( CommunityCommitment commitment : commitments ) {
            if ( isInitiating() ) {  // asking
                CommunityEmployment employment = commitment.getCommitter().getEmployment();
                contactedEmployments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        employment,
                        commitment,
                        communityService,
                        userInfo ) ) ;
            } else { // replying
                CommunityEmployment employment = commitment.getBeneficiary().getEmployment();
                contactedEmployments.add( employment );
                contactDataSet.addAll( ContactData.findContactsFromEmploymentAndCommitment(
                        serverUrl,
                        employment,
                        commitment,
                        communityService,
                        userInfo ) ) ;
            }
        }
        contactEmployments = new ArrayList<CommunityEmployment>( contactedEmployments );
        contacts = new ArrayList<ContactData>( contactDataSet );
    }

    private void initConsumingTask( String serverUrl, CommunityService communityService ) {
        if ( !isInitiating() )  {
            consumingTaskData = new TaskData(
                    serverUrl,
                    communityService,
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
    public SharedInformationData getInformation() {
        return new SharedInformationData( getSharing() );
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

    public AssignmentData getAssignmentData() {
        return assignmentData;
    }
}
