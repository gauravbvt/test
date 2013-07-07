package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.ReceiptConfirmationStep;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Web service data for a step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/5/13
 * Time: 8:52 PM
 */
@XmlType( propOrder = {"index", "actionStep", "notificationStep", "requestStep", "answerStep",
        "researchStep", "followUpStep", "receiptConfirmation"} )
public class ChecklistStepData implements Serializable {

    private int index;
    private ChecklistData checklist;
    private Step step;
    // Exactly one below is not null
    private ActionStepData actionStep;
    private NotificationStepData notificationStep;
    private RequestStepData requestStep;
    private AnswerStepData answerStep;
    private ResearchStepData researchStep;
    private FollowUpStepData followUpStep;
    private ReceiptConfirmationStepData receiptConfirmationStep;

    public ChecklistStepData() {
        // required
    }

    public ChecklistStepData( Step step,
                              ChecklistData checklist,
                              String serverUrl,
                              CommunityService communityService,
                              ChannelsUser user ) {
        this.step = step;
        this.checklist = checklist;
        initData( serverUrl, communityService, user );
    }

    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        index = checklist.indexOfStep( step );
        if ( step.isActionStep() ) {
            actionStep = new ActionStepData( step, checklist, serverUrl, communityService, user );
        } else if ( step.isCommunicationStep() ) {
            CommunicationStep communicationStep = (CommunicationStep) step;
            if ( communicationStep.isNotification() ) {
                notificationStep = new NotificationStepData( step, checklist, serverUrl, communityService, user );
            } else if ( communicationStep.isRequest() ) {
                requestStep = new RequestStepData( step, checklist, serverUrl, communityService, user );
            } else if ( communicationStep.isAnswer() ) {
                answerStep = new AnswerStepData( step, checklist, serverUrl, communityService, user );
            } else {
                throw new RuntimeException( "Unknown communication step" );
            }
        } else if ( step.isSubTaskStep() ) {
            SubTaskStep subTaskStep = (SubTaskStep)step;
            if ( subTaskStep.isResearch() ) {
                researchStep = new ResearchStepData( step, checklist, serverUrl, communityService, user );
            } else if ( subTaskStep.isFollowUp() ) {
                followUpStep = new FollowUpStepData(step, checklist, serverUrl, communityService, user);
            }  else {
                throw new RuntimeException( "Unknown sub-task step" );
            }
        } else if ( step.isReceiptConfirmation() ) {
            receiptConfirmationStep = new ReceiptConfirmationStepData(
                    (ReceiptConfirmationStep)step,
                    checklist,
                    serverUrl,
                    communityService,
                    user );
        } else {
            throw new RuntimeException( "Unknown step" );
        }

    }

    @XmlElement
    public int getIndex() {
        return index;
    }

    @XmlElement
    public ActionStepData getActionStep() {
        return actionStep;
    }

    @XmlElement
    public AnswerStepData getAnswerStep() {
        return answerStep;
    }

    @XmlElement
    public NotificationStepData getNotificationStep() {
        return notificationStep;
    }

    @XmlElement
    public RequestStepData getRequestStep() {
        return requestStep;
    }

    @XmlElement
    public ReceiptConfirmationStepData getReceiptConfirmation() {
        return receiptConfirmationStep;
    }

    @XmlElement
    public FollowUpStepData getFollowUpStep() {
        return followUpStep;
    }

    @XmlElement
    public ResearchStepData getResearchStep() {
        return researchStep;
    }

    public Set<Long> allEventIds() {
        return getStepData().allEventIds();
    }

    public Set<Long> allPhaseIds() {
        return getStepData().allPhaseIds();
    }

    public Set<Long> allOrganizationIds() {
        return getStepData().allOrganizationIds();
    }

    public Set<Long> allActorIds() {
        return getStepData().allActorIds();
    }

    public Set<Long> allRoleIds() {
        return getStepData().allRoleIds();
    }

    public Set<Long> allPlaceIds() {
        return getStepData().allPlaceIds();
    }

    public Set<Long> allMediumIds() {
        return getStepData().allMediumIds();
    }

    public Set<Long> allInfoProductIds() {
        return getStepData().allInfoProductIds();
    }

    public Set<Long> allInfoFormatIds() {
        return getStepData().allInfoFormatIds();
    }


    private AbstractStepData getStepData() {
        AbstractStepData stepData = actionStep != null
                ? actionStep
                : notificationStep != null
                ? notificationStep
                : requestStep != null
                ? requestStep
                : answerStep != null
                ? answerStep
                : researchStep != null
                ? researchStep
                : followUpStep != null
                ? followUpStep
                : receiptConfirmationStep != null
                ? receiptConfirmationStep
                : null;
        assert stepData != null;
        return stepData;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>(  );
        if ( notificationStep != null )
            allContacts.addAll( notificationStep.allContacts() );
        else if ( requestStep != null )
            allContacts.addAll( requestStep.allContacts() );
        else if ( answerStep != null )
            allContacts.addAll( answerStep.allContacts() );
        // todo add contacts to confirm receipt
        return allContacts;
    }

    public Step getStep() {
        return getStepData().getStep();
    }

}
