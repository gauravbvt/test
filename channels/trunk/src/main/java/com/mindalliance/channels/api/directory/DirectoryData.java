package com.mindalliance.channels.api.directory;

import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.procedures.ProtocolsData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;

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

    private ProtocolsData protocolsData;

    private List<ContactData> directoryContacts;

    public DirectoryData() {
        // required
    }

    public DirectoryData( ProtocolsData protocolsData ) {
        this.protocolsData = protocolsData;
        initData( );
    }

    private void initData(  ) {
        initDirectoryContacts( );
    }

    private void initDirectoryContacts(  ) {
        directoryContacts = new ArrayList<ContactData>();
        for ( ChecklistData procedureData : protocolsData.getChecklists() ) {
            directoryContacts.addAll( procedureData.allContacts() );
        }

    }

    @XmlElement
    public String getDate() {
        return new SimpleDateFormat( "yyyy/MM/dd H:mm:ss z" ).format( new Date() );
    }

    @XmlElement( name = "plan" )
    public PlanIdentifierData getPlanIdentifier() {
        return protocolsData.getPlanIdentifier();
    }

    @XmlElement
    public String getDateVersioned() {
        return protocolsData.getDateVersioned();
    }

    @XmlElement( name = "employment" )
    // Get given actor's or user's employments
    public List<EmploymentData> getEmployments() {
        return protocolsData.getEmployments();
    }

    @XmlElement( name = "contact" )
    public List<ContactData> getContacts() {
        return directoryContacts;
    }


}
