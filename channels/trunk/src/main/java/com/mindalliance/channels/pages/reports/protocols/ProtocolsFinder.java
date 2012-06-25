package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.TriggerData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Protocols finder.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/25/12
 * Time: 11:09 AM
 */
public class ProtocolsFinder implements Serializable {

    private ProceduresData proceduresData;
    //
    List<ProcedureData> ongoingProcedures;
    Map<TriggerData, List<ProcedureData>> onObservations;
    Map<ContactData, Map<TriggerData, List<ProcedureData>>> onNotifications;
    Map<ContactData, Map<TriggerData, List<ProcedureData>>> onRequests;

    public ProtocolsFinder( ProceduresData proceduresData ) {
        this.proceduresData = proceduresData;
        initFinder();
    }

    private void initFinder() {
        // todo
    }
}
