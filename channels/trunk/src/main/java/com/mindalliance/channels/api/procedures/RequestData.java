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
 * Web Service data element for a request.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
@XmlRootElement( name = "request", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"information", "employments"} )
public class RequestData extends AbstractProcedureElementData {

    private Flow request;
    private boolean replying;

    public RequestData() {
        // required
    }

    public RequestData( Flow request, boolean replying, Assignment assignment, PlanService planService ) {
        super( assignment, planService );
        this.request = request;
        this.replying = replying;
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( request );
    }

    @XmlElement( name = "contact" )
    public List<EmploymentData> getEmployments() {
        List<EmploymentData> employments = new ArrayList<EmploymentData>(  );
        for ( Employment employment : contacts() ) {
            employments.add( new EmploymentData( employment ) );
        }
        return employments;
    }

    private List<Employment> contacts() {
        Set<Employment> contacts = new HashSet<Employment>(  );
        Part part = replying
                ? (Part)request.getSource() :
                (Part)request.getTarget();
        for (Assignment otherAssignment : getPlanService().findAllAssignments( part, false ) ) {
            Employment employment = otherAssignment.getEmployment();
            if ( !employment.getActor().equals( getAssignment().getActor() ) ) {
                contacts.add(  employment );
            }
        }
        return new ArrayList<Employment>( contacts );
    }

}
