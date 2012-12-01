package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
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
    private DocumentationData documentation;

    public ResearchData() {
        super();
    }

    public ResearchData(
            String serverUrl,
            PlanCommunity planCommunity,
            Flow requestToSelf,
            Assignment assignment,
            ChannelsUser user ) {
        super( planCommunity, assignment, user );
        this.requestToSelf = requestToSelf;
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        initResearchTask( serverUrl, planCommunity );
        initConsumingTaskData( serverUrl, planCommunity );
        failureImpact = planCommunity.getPlanService().computeSharingPriority( getSharing() ).getNegativeLabel();
        documentation =  new DocumentationData( serverUrl, requestToSelf );
    }

    private void initConsumingTaskData( String serverUrl, PlanCommunity planCommunity ) {
        consumingTaskData = new TaskData(
                serverUrl,
                planCommunity,
                (Part)requestToSelf.getTarget(),
                getUser() );
    }

    private void initResearchTask( String serverUrl, PlanCommunity planCommunity ) {
        researchTaskData = new TaskData(
                serverUrl,
                planCommunity,
                (Part)requestToSelf.getSource(),
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
        return documentation;
    }

    private Flow getSharing() {
        return requestToSelf;
    }


}
