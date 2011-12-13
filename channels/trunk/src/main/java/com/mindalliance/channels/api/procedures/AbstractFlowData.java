package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/9/11
 * Time: 1:37 PM
 */
public abstract class AbstractFlowData extends AbstractProcedureElementData {

    private Flow sharing;
    private List<EmploymentData> employments;

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
        if ( employments == null ) {
            employments = new ArrayList<EmploymentData>();
            for ( Employment employment : contacts() ) {
                employments.add( new EmploymentData( employment ) );
            }
        }
        return employments;
    }

    public List<Long> getMediumIds() {
        List<Long> media = new ArrayList<Long>();
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

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employment : getEmployments() ) {
            ids.add( employment.getOrganizationId() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employment : getEmployments() ) {
            ids.addAll( employment.allActorIds() );
        }
        for ( TransmissionMedium medium : sharing.transmissionMedia() ) {
            if ( medium.getQualification() != null )
                ids.add( medium.getQualification().getId() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employment : getEmployments() ) {
            ids.add( employment.getRoleId() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( EmploymentData employment : getEmployments() ) {
            if ( employment.getJurisdictionId() != null )
                ids.add( employment.getJurisdictionId() );
        }
        return ids;
    }


    protected abstract List<Employment> contacts();

}
