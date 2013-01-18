package com.mindalliance.channels.api.procedures;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for all user procedures according to a plan.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/18/13
 * Time: 2:38 PM
 */
@XmlRootElement( name = "allProcedures", namespace = "http://mind-alliance.com/api/isp/v1/" )
public class AllProceduresData implements Serializable {

    private List<ProceduresData> allProcedures = new ArrayList<ProceduresData>();

    public AllProceduresData() {
        // required
    }

    @XmlElement( name = "userProcedures" )
    public List<ProceduresData> getAllProcedures() {
        return allProcedures;
    }

    public void addProceduresData( ProceduresData proceduresData ) {
        allProcedures .add(  proceduresData );
    }
}
