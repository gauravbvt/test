package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.model.Actor;
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
        "maxDelay", "employments", "mediumIds", "failureImpact","consumingTask", /*"agreements",*/ "documentation"} )
public class RequestData extends AbstractFlowData {

    /**
     * Whether the assignment is issuing a request (false) or a reply (true).
     */
    private boolean replying;

    public RequestData() {
        // required
    }

    public RequestData(
            Flow request,
            boolean replying,
            Assignment assignment,
            PlanService planService ) {
        super( request, assignment, planService );
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
            return new TaskData( (Part)getRequest().getTarget(), getPlanService() );
        else
            return null;
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

    @Override
    protected List<Employment> contacts() {
        Set<Employment> contacts = new HashSet<Employment>(  );
        Actor assignedActor = getAssignment().getActor();
        List<Commitment> commitments = getPlanService().findAllCommitments( getRequest() );
        for ( Commitment commitment : commitments ) {
            if ( replying ) {
                if ( commitment.getCommitter().getActor().equals( assignedActor )
                        && !commitment.getBeneficiary().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getBeneficiary().getEmployment() );
                }
            } else { // asking
                if ( commitment.getBeneficiary().getActor().equals( assignedActor )
                        && !commitment.getCommitter().getActor().equals( assignedActor ) ) {
                    contacts.add( commitment.getCommitter().getEmployment() );
                }
            }
        }
        return new ArrayList<Employment>( contacts );
    }

    private Flow getRequest() {
        return getSharing();
    }

}
