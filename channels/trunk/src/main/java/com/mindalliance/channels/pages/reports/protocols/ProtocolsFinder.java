package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final QueryService queryService;
    private final PlanParticipationService planParticipationService;
    private final ChannelsUser user;
    //
    private List<ProcedureData> ongoingProcedures;
    private Map<TriggerData, List<ProcedureData>> onObservations;
    Map<TriggerData, List<ProcedureData>> onRequests;
    Map<TriggerData, List<ProcedureData>> onNotifications;
    Map<TriggerData, List<ProcedureData>> onDiscoveries;
    Map<TriggerData, List<ProcedureData>> onResearches;
    private Map<ContactData, Map<TriggerData, List<ProcedureData>>> onNotificationsByContact;
    private Map<ContactData, Map<TriggerData, List<ProcedureData>>> onRequestsByContact;
    Set<ContactData> rolodex;
    private Map<String, List<ContactData>> alphabetizedRolodex;

    public ProtocolsFinder( ProceduresData proceduresData,
                            QueryService queryService,
                            PlanParticipationService planParticipationService,
                            ChannelsUser user ) {
        this.proceduresData = proceduresData;
        this.queryService = queryService;
        this.planParticipationService = planParticipationService;
        this.user = user;
        initFinder();
    }

    private void initFinder() {
        ongoingProcedures = new ArrayList<ProcedureData>();
        onObservations = new HashMap<TriggerData, List<ProcedureData>>();
        onNotificationsByContact = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        onRequestsByContact = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        rolodex = new HashSet<ContactData>(  );
        alphabetizedRolodex = new HashMap<String, List<ContactData>>();
        for ( ProcedureData procedureData : proceduresData.getProcedures() ) {
            processProcedureData( procedureData, queryService, planParticipationService, user );
        }
    }

    private void processProcedureData(
            ProcedureData procedureData,
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        if ( procedureData.isOngoing() ) {
            ongoingProcedures.add( procedureData );
        } else {
            for ( TriggerData triggerData : procedureData.getObservationTriggers() ) {
                addTo( onObservations, triggerData, procedureData );
            }
            for ( TriggerData triggerData : procedureData.getRequestTriggers() ) {
                addTo( onRequests, triggerData, procedureData );
                for ( ContactData contactData : triggerData.getOnRequest().getContacts() ) {
                    addTo( onRequestsByContact, contactData, triggerData, procedureData );
                    addTo( alphabetizedRolodex, contactData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : procedureData.getNotificationTriggers() ) {
                addTo( onNotifications, triggerData, procedureData );
                for ( ContactData contactData : triggerData.getOnNotification().getContacts() ) {
                    addTo( onNotificationsByContact, contactData, triggerData, procedureData );
                    addTo( alphabetizedRolodex, contactData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : procedureData.getDiscoveryTriggers() ) {
                addTo( onDiscoveries, triggerData, procedureData );
            }
            for ( TriggerData triggerData : procedureData.getResearchTriggers() ) {
                addTo( onDiscoveries, triggerData, procedureData );
            }
        }
        for ( Employment employment : procedureData.getNonTriggerContactEmployments() ) {
            List<ContactData> contacts = ContactData.findContactsFromEmployment(
                    employment,
                    queryService,
                    planParticipationService,
                    user
            );
            for ( ContactData contactData : contacts ) {
                addTo( alphabetizedRolodex, contactData );
                rolodex.add( contactData );
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
        if ( list == null ) {
            list = new ArrayList<ContactData>();
            map.put( firstLetter, list );
        }
        if ( !list.contains( contactData ) ) list.add( contactData );
    }

    private void addTo(
            Map<TriggerData, List<ProcedureData>> map,
            TriggerData triggerData,
            ProcedureData procedureData ) {
        List<ProcedureData> list = map.get( triggerData );
        if ( list == null ) {
            list = new ArrayList<ProcedureData>();
            map.put( triggerData, list );
        }
        list.add( procedureData );
    }

    public List<ProcedureData> getOngoingProcedures() {
        return ongoingProcedures;
    }

    public Map<TriggerData, List<ProcedureData>> getOnObservationProcedures() {
        return onObservations;
    }

    public Map<TriggerData, List<ProcedureData>> getOnRequestProcedures() {
        return onRequests;
    }

    public Map<TriggerData, List<ProcedureData>> getOnNotificationProcedures() {
        return onNotifications;
    }

    public Map<TriggerData, List<ProcedureData>> getOnDiscoveryProcedures() {
        return onDiscoveries;
    }

    public Map<TriggerData, List<ProcedureData>> getOnResearchProcedures() {
        return onResearches;
    }

    public Set<ContactData> getRolodex() {
        return rolodex;
    }

    public Map<String, List<ContactData>> getAlphabetizedRolodex() {
        return alphabetizedRolodex;
    }

    public List<TriggerData> sortTriggerData( Collection<TriggerData> triggerDataList ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggerDataList );
        Collections.sort( sortedTriggers, new Comparator<TriggerData>() {
            @Override
            public int compare( TriggerData t1, TriggerData t2 ) {
                return t1.getLabel().compareTo( t2.getLabel() );
            }
        } );
        return sortedTriggers;
    }

    public List<String> getSortedRolodexTabs() {
        List<String> sortedTabs = new ArrayList<String>( alphabetizedRolodex.keySet() );
        Collections.sort( sortedTabs );
        return sortedTabs;
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringNotificationsFrom( ContactData contactData ) {
        return onNotificationsByContact.get( contactData );
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringRequestsFrom( ContactData contactData ) {
        return onRequestsByContact.get( contactData );
    }

    public List<String> getSortedOrganizationNames() {
        Set<String> names = new HashSet<String>(  );
        for ( ContactData contactData : rolodex ) {
            names.add( contactData.getEmployment().getOrganizationName() );
        }
        List<String> sortedNames = new ArrayList<String>( names );
        Collections.sort( sortedNames );
        return sortedNames;
    }

    @SuppressWarnings( "unchecked" )
    public List<ContactData> getContactsInOrganization( final String orgName ) {
        List<ContactData> contacts =( List<ContactData>) CollectionUtils.select(
            rolodex,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ContactData)object).getEmployment().getOrganizationName().equals(  orgName );
                    }
                }
        );
        sortContacts( contacts );
        return contacts;
    }

    public void sortContacts( List<ContactData> contacts ) {
        Collections.sort(
                contacts,
                new Comparator<ContactData>() {
                    @Override
                    public int compare( ContactData c1, ContactData c2 ) {
                        return c1.getNormalizedContactName().compareTo( c2.getNormalizedContactName() );
                    }
                });
    }


}
