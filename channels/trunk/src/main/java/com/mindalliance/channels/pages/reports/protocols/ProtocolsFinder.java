package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ChannelsService;
import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.procedures.ObservationData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Plan;
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
    private final ChannelsUser user;
    private final String username;
    private final Long actorId;
    //
    private List<ProcedureData> ongoingProcedures;
    private Map<ObservationData, List<ProcedureData>> onObservations;
    Map<TriggerData, List<ProcedureData>> onRequests;
    Map<TriggerData, List<ProcedureData>> onNotifications;
    Map<TriggerData, List<ProcedureData>> onDiscoveries;
    Map<TriggerData, List<ProcedureData>> onResearches;
    private Map<ContactData, Map<TriggerData, List<ProcedureData>>> onNotificationsByContact;
    private Map<ContactData, Map<TriggerData, List<ProcedureData>>> onRequestsByContact;
    Set<ContactData> rolodex;
    private PlanScopeData planScopeData;
    private DirectoryData directoryData;
    private List<String> sortedTabs;
    private Map<String, List<ContactData>> alphabetizedTriggerRolodex;

    public ProtocolsFinder( ProceduresData proceduresData,
                            QueryService queryService,
                            PlanParticipationService planParticipationService,
                            ChannelsUser user,
                            ChannelsService channelsService,
                            String username,
                            Long actorId ) {
        this.proceduresData = proceduresData;
        this.user = user;
        this.username = username;
        this.actorId = actorId;
        initFinder( queryService, planParticipationService, channelsService );
    }

    private void initFinder(
            QueryService queryService,
            PlanParticipationService planParticipationService,
            ChannelsService channelsService ) {
        Plan plan = queryService.getPlan();
        planScopeData = channelsService.getPlanScope( plan.getUri(), Integer.toString( plan.getVersion() ) );
        directoryData = actorId != null
                ? channelsService.getAgentDirectory( plan.getUri(), Integer.toString( plan.getVersion() ), Long.toString( actorId ) )
                : channelsService.getUserDirectory( plan.getUri(), Integer.toString( plan.getVersion() ), username );
        ongoingProcedures = new ArrayList<ProcedureData>();
        onObservations = new HashMap<ObservationData, List<ProcedureData>>();
        onNotificationsByContact = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        onRequestsByContact = new HashMap<ContactData, Map<TriggerData, List<ProcedureData>>>();
        onRequests = new HashMap<TriggerData, List<ProcedureData>>();    // todo - using triggerData is wrong? Use NotificationData etc.
        onNotifications = new HashMap<TriggerData, List<ProcedureData>>();
        onDiscoveries = new HashMap<TriggerData, List<ProcedureData>>();
        onResearches = new HashMap<TriggerData, List<ProcedureData>>();
        rolodex = new HashSet<ContactData>();
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
                addTo( onObservations, triggerData.getOnObservation(), procedureData );
            }
            for ( TriggerData triggerData : procedureData.getRequestTriggers() ) {
                addTo( onRequests, triggerData, procedureData );
                for ( ContactData contactData : triggerData.getOnRequest().getContacts() ) {
                    addTo( onRequestsByContact, contactData, triggerData, procedureData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : procedureData.getNotificationTriggers() ) {
                addTo( onNotifications, triggerData, procedureData );
                for ( ContactData contactData : triggerData.getOnNotification().getContacts() ) {
                    addTo( onNotificationsByContact, contactData, triggerData, procedureData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : procedureData.getDiscoveryTriggers() ) {
                addTo( onDiscoveries, triggerData, procedureData );
            }
            for ( TriggerData triggerData : procedureData.getResearchTriggers() ) {
                addTo( onResearches, triggerData, procedureData );
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
                rolodex.add( contactData );
            }
        }
    }

    private void addTo( Map<ObservationData, List<ProcedureData>> map,
                        ObservationData onObservation,
                        ProcedureData procedureData ) {
        List<ProcedureData> list = map.get( onObservation );
        if ( list == null ) {
            list = new ArrayList<ProcedureData>();
            map.put( onObservation, list );
        }
        list.add( procedureData );
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

    public Map<ObservationData, List<ProcedureData>> getOnObservationProcedures() {
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

    public List<ContactData> getSortedRolodex() {
        List<ContactData> sortedRolodex = new ArrayList<ContactData>( rolodex );
        sortContacts( sortedRolodex );
        return sortedRolodex;
    }

    @SuppressWarnings( "unchecked" )
    public Map<String, List<ContactData>> getAlphabetizedTriggerRolodex() {
        if ( alphabetizedTriggerRolodex == null ) {
            alphabetizedTriggerRolodex = new HashMap<String, List<ContactData>>();
            for ( final String letter : getSortedTriggerRolodexTabs() ) {
                Set<ContactData> contacts = new HashSet<ContactData>();
                contacts.addAll(
                        CollectionUtils.select(
                                onNotificationsByContact.keySet(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (ContactData) object ).getNormalizedContactName().substring( 0, 1 )
                                                .equalsIgnoreCase( letter );
                                    }
                                }
                        ) );
                contacts.addAll(
                        CollectionUtils.select(
                                onRequestsByContact.keySet(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (ContactData) object ).getNormalizedContactName().substring( 0, 1 )
                                                .equalsIgnoreCase( letter );
                                    }
                                }
                        ) );
                List<ContactData> sortedList = new ArrayList<ContactData>( contacts );
                Collections.sort( sortedList, new Comparator<ContactData>() {
                    @Override
                    public int compare( ContactData cd1, ContactData cd2 ) {
                        return cd1.getNormalizedContactName().compareTo( cd2.getNormalizedContactName() );
                    }
                } );
                alphabetizedTriggerRolodex.put( letter, sortedList );
            }
        }
        return alphabetizedTriggerRolodex;
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

    public List<String> getSortedTriggerRolodexTabs() {
        if ( sortedTabs == null ) {
            Set<String> firstLetters = new HashSet<String>();
            for ( ContactData contactData : onNotificationsByContact.keySet() ) {
                firstLetters.add( contactData.getNormalizedContactName().substring( 0, 1 ).toUpperCase() );
            }
            for ( ContactData contactData : onRequestsByContact.keySet() ) {
                firstLetters.add( contactData.getNormalizedContactName().substring( 0, 1 ).toUpperCase() );
            }
            sortedTabs = new ArrayList<String>( firstLetters );
            Collections.sort( sortedTabs );
        }
        return sortedTabs;
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringNotificationsFrom( ContactData contactData ) {
        return retrieveTriggeredProcedures( onNotificationsByContact, contactData );
    }

    public Map<TriggerData, List<ProcedureData>> getTriggeringRequestsFrom( ContactData contactData ) {
        return retrieveTriggeredProcedures( onRequestsByContact, contactData );
    }

    private Map<TriggerData, List<ProcedureData>> retrieveTriggeredProcedures( Map<ContactData, Map<TriggerData, List<ProcedureData>>> map, ContactData contactData ) {
        Map<TriggerData, List<ProcedureData>> results = map.get( contactData );
        return results == null
                ? new HashMap<TriggerData, List<ProcedureData>>()
                : results;
    }

    public List<String> getSortedOrganizationNames() {
        Set<String> names = new HashSet<String>();
        for ( ContactData contactData : rolodex ) {
            names.add( contactData.getEmployment().getOrganizationName() );
        }
        List<String> sortedNames = new ArrayList<String>( names );
        Collections.sort( sortedNames );
        return sortedNames;
    }

    @SuppressWarnings( "unchecked" )
    public List<ContactData> getContactsInOrganization( final String orgName ) {
        Set<ContactData> contacts = new HashSet<ContactData>();
        for (ContactData contactData : rolodex ) {
            if (contactData.getEmployment().getOrganizationName().equals( orgName ) ) {
                contacts.add( contactData );
            }
        }
        List<ContactData> sortedOrgContacts = new ArrayList<ContactData>( contacts  );
        sortContacts( sortedOrgContacts );
        return sortedOrgContacts;
    }

    public void sortContacts( List<ContactData> contacts ) {
        Collections.sort(
                contacts,
                new Comparator<ContactData>() {
                    @Override
                    public int compare( ContactData c1, ContactData c2 ) {
                        return c1.getNormalizedContactName().compareTo( c2.getNormalizedContactName() );
                    }
                } );
    }


    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return planScopeData.findInScope( moDataClass, moId );
    }

    @SuppressWarnings( "unchecked" )
    public List<ContactData> findContacts( final long actorId ) {
        return (List<ContactData>) CollectionUtils.select(
                directoryData.getContacts(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ContactData) object ).getEmployment().getActorId() == actorId;
                    }
                }
        );
    }


    public void sortObservations( List<ObservationData> sortedObservations ) {
        Collections.sort( sortedObservations, new Comparator<ObservationData>() {
            @Override
            public int compare( ObservationData od1, ObservationData od2 ) {
                return od1.getLabel().compareTo( od2.getLabel() );
            }
        });
    }
}
