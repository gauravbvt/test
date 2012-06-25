package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.TriggerData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    Map<String,List<ContactData>> triggerRolodex;

    public ProtocolsFinder( ProceduresData proceduresData ) {
        this.proceduresData = proceduresData;
        initFinder();
    }

    private void initFinder() {
        ongoingProcedures = new ArrayList<ProcedureData>(  );
        onObservations = new HashMap<TriggerData, List<ProcedureData>>(  );
        onNotifications = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        onRequests = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        triggerRolodex = new HashMap<String,List<ContactData>>();
        for ( ProcedureData procedureData : proceduresData.getProcedures() ) {
            processProcedureData( procedureData );
        }
    }

    private void processProcedureData( ProcedureData procedureData ) {
        if ( procedureData.isOngoing() ) {
            ongoingProcedures.add( procedureData );
        } else {
            for (TriggerData triggerData : procedureData.getObservationTriggers() ) {
                addTo( onObservations, triggerData, procedureData );
            }
            for (TriggerData triggerData : procedureData.getRequestTriggers() ) {
                for ( ContactData contactData : triggerData.getOnRequest().getContacts() ) {
                    addTo( onRequests, contactData, triggerData, procedureData );
                    addTo( triggerRolodex, contactData );
                }
            }
            for (TriggerData triggerData : procedureData.getNotificationTriggers() ) {
                for ( ContactData contactData : triggerData.getOnNotification().getContacts() ) {
                    addTo( onNotifications, contactData, triggerData, procedureData );
                    addTo( triggerRolodex, contactData );
                }
            }
        }
    }

    private void addTo(
            Map<ContactData, Map<TriggerData, List<ProcedureData>>> map,
            ContactData contactData,
            TriggerData triggerData,
            ProcedureData procedureData ) {
        Map<TriggerData, List<ProcedureData>> procMap = map.get( contactData );
        if ( procMap == null ) {
            procMap = new HashMap<TriggerData, List<ProcedureData>>();
            map.put( contactData, procMap );
        }
        addTo( procMap, triggerData, procedureData );
    }

    private void addTo( Map<String, List<ContactData>> map, ContactData contactData ) {
        String firstLetter = contactData.firstLetterOfName();
        List<ContactData> list = map.get( firstLetter );
        if ( list == null )  {
            list = new ArrayList<ContactData>(  );
            map.put( firstLetter, list );
        }
        list.add( contactData );
    }

    private void addTo(
            Map<TriggerData,List<ProcedureData>> map,
            TriggerData triggerData,
            ProcedureData procedureData ) {
        List<ProcedureData> list = map.get( triggerData );
        if ( list == null )  {
            list = new ArrayList<ProcedureData>(  );
            map.put( triggerData, list );
        }
        list.add( procedureData );
    }

    public List<ProcedureData> getOngoingProcedures() {
        return ongoingProcedures;
    }

    public Map<TriggerData, List<ProcedureData>> getOnObservations() {
        return onObservations;
    }

    public Map<String,List<ContactData>> getTriggerRolodex() {
        return triggerRolodex;
    }

    public List<String> getSortedRolodexTabs() {
        List<String> sortedTabs = new ArrayList<String>( triggerRolodex.keySet() );
        Collections.sort( sortedTabs );
        return sortedTabs;
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringNotificationsFrom( ContactData contactData ) {
        return onNotifications.get( contactData );
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringRequestsFrom( ContactData contactData ) {
        return onRequests.get( contactData );
    }

}
