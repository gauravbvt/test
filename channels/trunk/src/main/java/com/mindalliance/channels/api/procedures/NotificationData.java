package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a notification.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
@XmlRootElement( name = "notification", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"information", "intent", "receiptConfirmationRequested", "instructions", "contactAll", "maxDelay", "employments", "mediaIds", "failureImpact", "consumingTask"} )
public class NotificationData extends AbstractFlowData {

    private boolean consuming;

    public NotificationData() {
        // required
    }

    public NotificationData(
            Flow notification,
            boolean consuming,
            Assignment assignment,
            PlanService planService ) {
        super( notification, assignment, planService );
        this.consuming = consuming;
    }

    @Override
    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getNotification() );
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
    public List<Long> getMediaIds() {
        return super.getMediaIds();
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
        if ( consuming )
            return null;
        else
            return new TaskData( getNotification().getContactedPart(), getPlanService() );
    }

    protected List<Employment> contacts() {
        Set<Employment> contacts = new HashSet<Employment>(  );
        Part part = consuming
                ? (Part)getNotification().getSource() :
                (Part)getNotification().getTarget();
        for (Assignment otherAssignment : getPlanService().findAllAssignments( part, false ) ) {
            Employment employment = otherAssignment.getEmployment();
            if ( !employment.getActor().equals( getAssignment().getActor() ) ) {
                contacts.add(  employment );
            }
        }
        return new ArrayList<Employment>( contacts );
    }

    private Flow getNotification() {
        return getSharing();
    }

}
