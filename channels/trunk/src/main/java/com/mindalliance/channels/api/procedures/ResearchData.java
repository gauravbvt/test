package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a request to oneself.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 8:49 PM
 */
@XmlType( propOrder = {"information", "maxDelay", "researchTask", "consumingTask", "instructions",
        "failureImpact", "documentation"} )
public class ResearchData extends AbstractProcedureElementData {

    private Flow requestToSelf;
    private TaskData researchTaskData;
    private TaskData consumingTaskData;
    private String failureImpact;

    public ResearchData() {
        super();
    }

    public ResearchData(
            Flow requestToSelf,
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
        this.requestToSelf = requestToSelf;
        initData( planService, planParticipationService );
    }

    private void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        initResearchTask( planService, planParticipationService );
        initConsumingTaskData( planService, planParticipationService );
        failureImpact = planService.computeSharingPriority( getSharing() ).getNegativeLabel();
    }

    private void initConsumingTaskData( PlanService planService, PlanParticipationService planParticipationService ) {
        consumingTaskData = new TaskData(
                (Part)requestToSelf.getTarget(),
                planService,
                planParticipationService,
                getUser() );
    }

    private void initResearchTask( PlanService planService, PlanParticipationService planParticipationService ) {
        researchTaskData = new TaskData(
                (Part)requestToSelf.getSource(),
                planService,
                planParticipationService,
                getUser() );
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getSharing() );
    }

    @XmlElement
    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( getSharing().getMaxDelay() );
    }

    @XmlElement
    public TaskData getResearchTask() {
        return researchTaskData;
    }

    @XmlElement
    public TaskData getConsumingTask() {
        return  consumingTaskData;
    }


    @XmlElement
    public String getInstructions() {
        String instructions = getSharing().getDescription();
        return instructions == null
                ? null
                : StringEscapeUtils.escapeXml( instructions );
    }

    @XmlElement
    public String getFailureImpact() {
        return failureImpact;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( requestToSelf );
    }

    private Flow getSharing() {
        return requestToSelf;
    }


}
