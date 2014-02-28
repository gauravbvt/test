package com.mindalliance.channels.api.procedures;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for all user procedures according to a plan.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 2:44 PM
 */
@XmlRootElement(name = "allChecklists", namespace = "http://mind-alliance.com/api/isp/v1/")
@XmlType
public class AllChecklistsData implements Serializable {

    private List<ChecklistsData> allChecklists = new ArrayList<ChecklistsData>();

    public AllChecklistsData() {
        // required
    }

    @XmlElement( name = "userChecklists" )
    public List<ChecklistsData> getAllChecklists() {
        return allChecklists;
    }

    public void addChecklistsData( ChecklistsData checklistsData ) {
        allChecklists.add( checklistsData );
    }
}
