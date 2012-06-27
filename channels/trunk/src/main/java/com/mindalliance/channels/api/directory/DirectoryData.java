package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.core.model.Employment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A directory of contacts extracted from procedures data.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/23/12
 * Time: 5:10 PM
 */
@XmlRootElement( name = "directory", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"date", "planIdentifier", "employments", "dateVersioned", "contacts"} )
public class DirectoryData {

    private ProceduresData proceduresData;

    public DirectoryData() {
        // required
    }

    public DirectoryData( ProceduresData proceduresData ) {
        this.proceduresData = proceduresData;
    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( proceduresData.getPlan() );
    }

    @XmlElement
    public String getDateVersioned() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( proceduresData.getPlan().getWhenVersioned() );
    }

    @XmlElement( name = "employment" )
    // Get given actor's or user's employments
    public List<EmploymentData> getEmployments() {
        return proceduresData.getEmployments();
    }

    @XmlElement( name = "contact" )
    public List<ContactData> getContacts() {
        List<ContactData> directoryContacts = new ArrayList<ContactData>();
        for ( Employment employment : findDirectoryEmployments() ) {
            directoryContacts.addAll( ContactData.findContactsFromEmployment(
                    employment,
                    proceduresData.getPlanService(),
                    proceduresData.getPlanParticipationService(),
                    proceduresData.getUser()
            ) );
        }
        return directoryContacts;
    }

    private Set<Employment> findDirectoryEmployments() {
        Set<Employment> employments = new HashSet<Employment>();
        for ( EmploymentData employmentData : proceduresData.getEmployments() ) {
            employments.add( employmentData.getEmployment() );
        }
        employments.addAll( proceduresData.getContactEmployments() );
        return employments;
    }


}
