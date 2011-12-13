package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.PlanService;

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
@XmlType( propOrder = {"information", "intent", "receiptConfirmationRequested", "instructions", "contactAll",
        "maxDelay", "employments", "mediumIds", "failureImpact","consumingTask", "agreements", "documentation"} )
public class RequestData extends AbstractFlowData {

    /**
     * Whether the assignment is issuing a request (false) or a reply (true).
     */
    private boolean replying;

    public RequestData() {
        // required
    }

    public RequestData(
            Commitment requestCommitment,
            boolean replying,
            Assignment assignment,
            PlanService planService ) {
        super( requestCommitment, assignment, planService );
        this.replying = replying;
    }

    @Override
    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getRequest() );
    }

    @Override
    @XmlElement
    public String getIntent() {
        return super.getIntent();
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
    public List<EmploymentData> getEmployments() {
       return super.getEmployments();
    }

    @Override
    @XmlElement( name = "transmissionMediumId" )
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
        if ( replying )
            return null;
        else
            return new TaskData( (Part)getRequest().getTarget(), getPlanService() );
    }

    @Override
    @XmlElement( name = "agreement" )
    public List<AgreementData> getAgreements() {
        return super.getAgreements();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    protected List<Employment> contacts() {
        Set<Employment> contacts = new HashSet<Employment>(  );
        Part part = replying
                ? (Part)getSharing().getTarget() :
                (Part)getSharing().getSource();
        for (Assignment otherAssignment : getPlanService().findAllAssignments( part, false ) ) {
            Employment employment = otherAssignment.getEmployment();
            if ( !employment.getActor().equals( getAssignment().getActor() ) ) {
                contacts.add(  employment );
            }
        }
        return new ArrayList<Employment>( contacts );
    }

    private Flow getRequest() {
        return getSharing();
    }

}
