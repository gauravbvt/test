package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.PlanService;

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

    public ResearchData() {
        super();
    }

    public ResearchData( Flow requestToSelf, Assignment assignment, PlanService planService ) {
        super( assignment, planService );
        this.requestToSelf = requestToSelf;
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
        return new TaskData( (Part)requestToSelf.getSource(), getPlanService() );
    }

    @XmlElement
    public TaskData getConsumingTask() {
        return new TaskData( (Part)requestToSelf.getTarget(), getPlanService() );
    }


    @XmlElement
    public String getInstructions() {
        String instructions = getSharing().getDescription();
        return instructions == null
                ? null
                : instructions;
    }

    @XmlElement
    public String getFailureImpact() {
        return getPlanService().computeSharingPriority( getSharing() ).getNegativeLabel();
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( requestToSelf );
    }

    private Flow getSharing() {
        return requestToSelf;
    }


}
