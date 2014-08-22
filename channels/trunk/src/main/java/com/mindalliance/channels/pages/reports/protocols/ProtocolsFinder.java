package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.PlanCommunityEndPoint;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.plan.ModelScopeData;
import com.mindalliance.channels.api.procedures.ChecklistsData;
import com.mindalliance.channels.api.procedures.ObservationData;
import com.mindalliance.channels.api.procedures.RequestData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
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

    private String serverUrl;
    private ChecklistsData checklistsData;
    private final ChannelsUser user;
    private final String username;
    private final Agent agent;
    //
    private List<ChecklistData> ongoingProcedures;
    private List<ChecklistData> repeatingProcedures;
    private Set<String> communicationContexts;
    private Map<ObservationData, List<ChecklistData>> onObservations;
    private Map<TriggerData, List<ChecklistData>> onRequests;
    private Map<TriggerData, List<ChecklistData>> onNotifications;
    // Communication context => ( info => (contact => trigger) )
    private Map<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>> onNotificationsInContextWithInfoByContact;
    private Map<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>> onRequestsInContextForInfoByContact;
    private Map<TriggerData, List<ChecklistData>> onFollowUps;
    private Map<TriggerData, List<ChecklistData>> onResearches;
    private List<RequestData> expectedQueries;
    private Set<ContactData> rolodex;
    private ModelScopeData modelScopeData;
    private List<String> sortedTabs;
    private Map<String, List<ContactData>> alphabetizedTriggerRolodex;

    private Map<ChecklistData, List<TriggerData>> checklistsWithTriggers;

    public ProtocolsFinder(
            String serverUrl,
            ChecklistsData checklistsData,
            CommunityService communityService,
            ChannelsUser user,
            PlanCommunityEndPoint channelsService,
            String username,
            Agent agent ) {
        this.serverUrl = serverUrl;
        this.checklistsData = checklistsData;
        this.user = user;
        this.username = username;
        this.agent = agent;
        initFinder( communityService, channelsService );
    }

    private void initFinder(
            CommunityService communityService,
            PlanCommunityEndPoint channelsService ) {
        CollaborationModel collaborationModel = communityService.getPlan();
        communicationContexts = new HashSet<String>();
        modelScopeData = channelsService.modelScope( collaborationModel.getUri(), Integer.toString( collaborationModel.getVersion() ), false );
        ongoingProcedures = new ArrayList<ChecklistData>();
        repeatingProcedures = new ArrayList<ChecklistData>();
        onObservations = new HashMap<ObservationData, List<ChecklistData>>();
        onNotificationsInContextWithInfoByContact = new HashMap<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>>();
        onRequestsInContextForInfoByContact = new HashMap<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>>();
        onRequests = new HashMap<TriggerData, List<ChecklistData>>();
        onNotifications = new HashMap<TriggerData, List<ChecklistData>>();
        onFollowUps = new HashMap<TriggerData, List<ChecklistData>>();
        onResearches = new HashMap<TriggerData, List<ChecklistData>>();
        expectedQueries = new ArrayList<RequestData>();
        rolodex = new HashSet<ContactData>();
        checklistsWithTriggers = new HashMap<ChecklistData, List<TriggerData>>();
        for ( ChecklistData checklistData : checklistsData.getChecklists() ) {
            processChecklistData( checklistData, communityService, user );
        }
        expectedQueries = checklistsData.getExpectedQueries();
    }

    private void processChecklistData(
            ChecklistData checklistData,
            CommunityService communityService,
            ChannelsUser user ) {
        checklistsWithTriggers.put( checklistData, checklistData.getTriggers() );
        if ( checklistData.isOngoing() ) {
            ongoingProcedures.add( checklistData );
        } else if ( checklistData.isRepeating() ) {
            repeatingProcedures.add( checklistData );
        } else {
            for ( TriggerData triggerData : checklistData.getObservationTriggers() ) {
                addTo( onObservations, triggerData.getOnObservation(), checklistData );
            }
            for ( TriggerData triggerData : checklistData.getRequestTriggers() ) {
                addTo( onRequests, triggerData, checklistData );
                String communicationContext = triggerData.getSituationLabel();
                if ( communicationContext != null )
                    communicationContexts.add( communicationContext );
                for ( ContactData contactData : triggerData.getOnRequest().getContacts() ) {
                    addTo( onRequestsInContextForInfoByContact,
                            communicationContext,
                            triggerData.getOnRequest().getInformation().getName(),
                            contactData,
                            triggerData,
                            checklistData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : checklistData.getNotificationTriggers() ) {
                addTo( onNotifications, triggerData, checklistData );
                String communicationContext = triggerData.getSituationLabel();
                communicationContexts.add( communicationContext );
                for ( ContactData contactData : triggerData.getOnNotification().getContacts() ) {
                    addTo( onNotificationsInContextWithInfoByContact,
                            communicationContext,
                            triggerData.getOnNotification().getInformation().getName(),
                            contactData,
                            triggerData,
                            checklistData );
                    rolodex.add( contactData );
                }
            }
            for ( TriggerData triggerData : checklistData.getFollowUpTriggers() ) {
                addTo( onFollowUps, triggerData, checklistData );
            }
            for ( TriggerData triggerData : checklistData.getResearchTriggers() ) {
                addTo( onResearches, triggerData, checklistData );
            }
        }
        // add contacts not yet added as direct, trigger contacts
        rolodex.addAll( checklistData.allContacts() );
    }

    private void addTo( Map<ObservationData, List<ChecklistData>> map,
                        ObservationData onObservation,
                        ChecklistData checklistData ) {
        List<ChecklistData> list = map.get( onObservation );
        if ( list == null ) {
            list = new ArrayList<ChecklistData>();
            map.put( onObservation, list );
        }
        list.add( checklistData );
    }

    private void addTo(
            Map<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>> map,
            String communicationContext,
            String info,
            ContactData contactData,
            TriggerData triggerData,
            ChecklistData checklistData ) {
        Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>> infoTriggers = map.get( communicationContext );
        if ( infoTriggers == null ) {
            infoTriggers = new HashMap<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>();
            map.put( communicationContext, infoTriggers );
        }
        Map<ContactData, Map<TriggerData, List<ChecklistData>>> contactTriggers = infoTriggers.get( info );
        if ( contactTriggers == null ) {
            contactTriggers = new HashMap<ContactData, Map<TriggerData, List<ChecklistData>>>();
            infoTriggers.put( info, contactTriggers );
        }
        Map<TriggerData, List<ChecklistData>> procMap = contactTriggers.get( contactData );
        if ( procMap == null ) {
            procMap = new HashMap<TriggerData, List<ChecklistData>>();
            contactTriggers.put( contactData, procMap );
        }
        addTo( procMap, triggerData, checklistData );
    }

    private void addTo(
            Map<TriggerData, List<ChecklistData>> map,
            TriggerData triggerData,
            ChecklistData checklistData ) {
        List<ChecklistData> list = map.get( triggerData );
        if ( list == null ) {
            list = new ArrayList<ChecklistData>();
            map.put( triggerData, list );
        }
        if ( !list.contains( checklistData ) ) list.add( checklistData );
    }

    public Set<String> getCommunicationContexts() {
        return communicationContexts;
    }

    public List<RequestData> getExpectedQueries() {
        return expectedQueries;
    }

    public List<ChecklistData> getOngoingProcedures() {
        return ongoingProcedures;
    }

    public List<ChecklistData> getRepeatingProcedures() {
        return repeatingProcedures;
    }

    public Map<ObservationData, List<ChecklistData>> getOnObservationChecklists() {
        return onObservations;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<ContactData>> getAlphabetizedTriggerRolodex() {
        if ( alphabetizedTriggerRolodex == null ) {
            alphabetizedTriggerRolodex = new HashMap<String, List<ContactData>>();
            for ( final String letter : getSortedTriggerRolodexTabs() ) {
                Set<ContactData> contacts = new HashSet<ContactData>();
                contacts.addAll(
                        CollectionUtils.select(
                                getAllContactsIn( onNotificationsInContextWithInfoByContact ),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (ContactData) object ).getNormalizedContactName().substring( 0, 1 )
                                                .equalsIgnoreCase( letter );
                                    }
                                }
                        )
                );
                contacts.addAll(
                        CollectionUtils.select(
                                getAllContactsIn( onRequestsInContextForInfoByContact ),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return ( (ContactData) object ).getNormalizedContactName().substring( 0, 1 )
                                                .equalsIgnoreCase( letter );
                                    }
                                }
                        )
                );
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

    private List<ContactData> getAllContactsIn( Map<String, Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>>> map ) {
        Set<ContactData> contacts = new HashSet<ContactData>();
        for ( String communicationContext : map.keySet() ) {
            for ( String info : map.get( communicationContext ).keySet() )
                contacts.addAll( map.get( communicationContext ).get( info ).keySet() );
        }
        return new ArrayList<ContactData>( contacts );
    }

    public List<String> getSortedTriggerRolodexTabs() {
        if ( sortedTabs == null ) {
            Set<String> firstLetters = new HashSet<String>();
            for ( String communicationContext : onNotificationsInContextWithInfoByContact.keySet() ) {
                for ( String info : onNotificationsInContextWithInfoByContact.get( communicationContext ).keySet() ) {
                    for ( ContactData contactData : onNotificationsInContextWithInfoByContact.get( communicationContext ).get( info ).keySet() ) {
                        firstLetters.add( contactData.getNormalizedContactName().substring( 0, 1 ).toUpperCase() );
                    }
                }
            }
            for ( String communicationContext : onRequestsInContextForInfoByContact.keySet() ) {
                for ( String info : onRequestsInContextForInfoByContact.get( communicationContext ).keySet() ) {
                    for ( ContactData contactData : onRequestsInContextForInfoByContact.get( communicationContext ).get( info ).keySet() ) {
                        firstLetters.add( contactData.getNormalizedContactName().substring( 0, 1 ).toUpperCase() );
                    }
                }
            }
            sortedTabs = new ArrayList<String>( firstLetters );
            Collections.sort( sortedTabs );
        }
        return sortedTabs;
    }

    public List<String> getSortedAgencyNames() {
        Set<String> names = new HashSet<String>();
        for ( ContactData contactData : rolodex ) {
            names.add( contactData.getEmployment().getAgencyName() );
        }
        List<String> sortedNames = new ArrayList<String>( names );
        Collections.sort( sortedNames );
        return sortedNames;
    }

    @SuppressWarnings("unchecked")
    public List<ContactData> getContactsInAgencyNamed( final String agencyName ) {
        Set<ContactData> contacts = new HashSet<ContactData>();
        for ( ContactData contactData : rolodex ) {
            if ( contactData.getEmployment().getAgencyName().equals( agencyName ) ) {
                contacts.add( contactData );
            }
        }
        List<ContactData> sortedOrgContacts = new ArrayList<ContactData>( contacts );
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
                }
        );
    }


    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return modelScopeData.findInScope( moDataClass, moId );
    }

    public void sortObservations( List<ObservationData> sortedObservations ) {
        Collections.sort( sortedObservations, new Comparator<ObservationData>() {
            @Override
            public int compare( ObservationData od1, ObservationData od2 ) {
                return od1.getLabel().compareTo( od2.getLabel() );
            }
        } );
    }

    public List<Agent> getParticipatingAgents() {
        List<Agent> participations = checklistsData.getParticipatingAgents();
        return participations == null ? new ArrayList<Agent>() : participations;
    }

    public boolean isMultipleParticipation() {
        return getParticipatingAgents().size() > 1;
    }

    public Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>> getRequestsInContext( String communicationContext ) {
        return onRequestsInContextForInfoByContact.get( communicationContext );
    }

    public Map<String, Map<ContactData, Map<TriggerData, List<ChecklistData>>>> getNotificationsInContext( String communicationContext ) {
        return onNotificationsInContextWithInfoByContact.get( communicationContext );
    }

    public List<ChecklistData> getTriggeredChecklists() {
        List<ChecklistData> triggeredChecklists = new ArrayList<ChecklistData>( checklistsWithTriggers.keySet() );
        Collections.sort( triggeredChecklists, new Comparator<ChecklistData>() {
            @Override
            public int compare( ChecklistData cd1, ChecklistData cd2 ) {
                return cd1.getTaskLabel().compareTo( cd2.getTaskLabel() );
            }
        } );
        return triggeredChecklists;
    }

    public List<TriggerData> getChecklistTriggers( ChecklistData checklistData ) {
        List<TriggerData> triggerDataList = checklistsWithTriggers.get( checklistData );
        Collections.sort( triggerDataList, new Comparator<TriggerData>() {
            @Override
            public int compare( TriggerData td1, TriggerData td2 ) {
                if ( td1.isOngoing() ) return -1;
                if ( td2.isOngoing() ) return 1;
                if ( td1.isRepeating() ) return -1;
                if ( td2.isRepeating() ) return 1;
                if ( td1.isOnObserving() ) return -1;
                if ( td2.isOnObserving() ) return 1;
                if ( td1.isOnResearching() ) return -1;
                if ( td2.isOnResearching() ) return 1;
                if ( td1.isOnFollowingUp() ) return -1;
                if ( td2.isOnFollowingUp() ) return 1;
                return td1.getLabel().compareTo( td2.getLabel() );
            }
        } );
        return triggerDataList;
    }

}
