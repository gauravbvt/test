package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 1:37 PM
 */
public abstract class AbstractFlowData extends AbstractProcedureElementData {

    private Flow sharing;

    public AbstractFlowData() {
        // required
    }

    public AbstractFlowData(
            Flow sharing,
            Assignment assignment,
            PlanService planService ) {
        super( assignment, planService );
        this.sharing = sharing;
    }

    protected Flow getSharing() {
        return sharing;
    }

    public boolean getReceiptConfirmationRequested() {
        return sharing.isReceiptConfirmationRequested();
    }

    public InformationData getInformation() {
        return new InformationData( sharing );
    }

    public String getIntent() {
        return sharing.getIntent() == null
                ? null
                : sharing.getIntent().getLabel();
    }

    public List<EmploymentData> getEmployments() {
        List<EmploymentData> employments = new ArrayList<EmploymentData>(  );
        for ( Employment employment : contacts() ) {
            employments.add( new EmploymentData( employment ) );
        }
        return employments;
    }

    public List<Long> getMediaIds() {
        List<Long> media = new ArrayList<Long>(  );
        for ( TransmissionMedium medium : sharing.transmissionMedia() ) {
            media.add( medium.getId() );
        }
        return media;
    }

    public boolean getContactAll() {
        return sharing.isAll();
    }

    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( sharing.getMaxDelay() );
    }

    public String getInstructions() {
        String instructions = sharing.getDescription();
        return instructions == null
                ? null
                : instructions;
    }

    public String getFailureImpact() {
        return getPlanService().computeSharingPriority( sharing ).getNegativeLabel();
    }

    protected abstract List<Employment> contacts();

}
