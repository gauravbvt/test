package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 1:18 PM
 */
@XmlType( propOrder = {"information", "doingTask", "followUpTask", "documentation"} )
public class InfoDiscoveredData {

    private Commitment notificationToSelf;
    private PlanService planService;

    public InfoDiscoveredData() {
        // required
    }

    public InfoDiscoveredData( Commitment notificationToSelf, PlanService planService ) {
        this.notificationToSelf = notificationToSelf;
        this.planService = planService;
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( notificationToSelf.getSharing() );
    }

    @XmlElement
    public TaskData getDoingTask() {
        return new TaskData( notificationToSelf.getCommitter(), planService );
    }

    @XmlElement
    public TaskData getFollowUpTask() {
        return new TaskData( notificationToSelf.getBeneficiary(), planService );
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( notificationToSelf.getSharing() );
    }

}
