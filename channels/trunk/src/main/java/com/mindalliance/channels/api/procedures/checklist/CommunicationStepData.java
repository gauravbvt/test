package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Step;

import java.util.List;

/**
 * Web service data for a communication collaboration act.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 9:27 PM
 */
public abstract class CommunicationStepData extends AbstractStepData {

    public CommunicationStepData() {
        // required
    }

    public abstract List<ContactData> allContacts();


    public CommunicationStepData( Step step,
                                  ChecklistData checklist,
                                  String serverUrl,
                                  CommunityService communityService,
                                  ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    public boolean isNotification() {
        return getCommunicationStep().isNotification();
    }

    public boolean isRequest() {
        return getCommunicationStep().isRequest();
    }

    public boolean isAnswer() {
        return getCommunicationStep().isAnswer();
    }

    protected CommunicationStep getCommunicationStep() {
        return ( CommunicationStep )getStep();
    }
}
