package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class DirectoryData implements Serializable {

    private ProceduresData proceduresData;
    private List<ContactData> directoryContacts;

    public DirectoryData() {
        // required
    }

    public DirectoryData( ProceduresData proceduresData ) {
        this.proceduresData = proceduresData;
        initData( );
    }

    private void initData(  ) {
        initDirectoryContacts( );
    }

    private void initDirectoryContacts(  ) {
        directoryContacts = new ArrayList<ContactData>();
        for ( ProcedureData procedureData : proceduresData.getProcedures() ) {
            directoryContacts.addAll( procedureData.allContacts() );
        }

    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return proceduresData.getPlanIdentifier();
    }

    @XmlElement
    public String getDateVersioned() {
        return proceduresData.getDateVersioned();
    }

    @XmlElement( name = "employment" )
    // Get given actor's or user's employments
    public List<EmploymentData> getEmployments() {
        return proceduresData.getEmployments();
    }

    @XmlElement( name = "contact" )
    public List<ContactData> getContacts() {
        return directoryContacts;
    }


}
