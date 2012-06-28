package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ChannelsService;
import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.SituationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A page with a user's (or agent's) protocols.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/12
 * Time: 10:45 AM
 */
public class ProtocolsPage extends AbstractChannelsBasicPage {

    private static final Logger LOG = LoggerFactory.getLogger( ProtocolsPage.class );
    private ProceduresData proceduresData;
    private ProtocolsFinder finder;
    private DirectoryData directoryData;
    private String username;
    private long actorId;
    private Actor actor;
    private ChannelsUser protocolsUser;

    @SpringBean( name="channelsService" )
    private ChannelsService channelsService;

    private WebMarkupContainer aboutContainer;
    private WebMarkupContainer finderContainer;
    private WebMarkupContainer protocolsContainer;
    private WebMarkupContainer directoryContainer;

    public ProtocolsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "protocols-contents";
    }

    @Override
    protected String getPageName() {
        return "Protocols";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PROTOCOLS;
    }

    @Override
    protected void addContent() {
        try {
            PageParameters parameters = getPageParameters();
            actorId = parameters.get( "agent" ).toLong( -1 );
            username = parameters.get( "user" ).toString( getUser().getUsername() );
            initData();
            doAddContent();
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve protocols", e );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
        }
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }

    private void initData() throws Exception {
        Plan plan = getPlan();
        if ( actorId >= 0 ) {
            actor = getQueryService().find( Actor.class, actorId );
            proceduresData = channelsService.getAgentProcedures(
                    plan.getUri(),
                    Integer.toString( plan.getVersion() ),
                    Long.toString( actorId ) );
        } else {
            protocolsUser = getUserDao().getUserNamed( username );
            if ( protocolsUser == null )
                throw new Exception( "Failed to retrieve protocols" );
            else {
                if ( protocolsUser.isPlanner( getPlan().getUri() ) ) {
                    proceduresData = channelsService.getUserProcedures(
                            plan.getUri(),
                            Integer.toString( plan.getVersion() ),
                            username );
                } else if ( getUser().getUsername().equals( username ) ) {
                    proceduresData = channelsService.getMyProcedures( getPlan().getUri() );
                } else {
                    throw new Exception( "Failed to retrieve protocols" );
                }
            }
        }
        finder = new ProtocolsFinder(
                proceduresData,
                getQueryService(),
                getPlanParticipationService(),
                protocolsUser,
                channelsService,
                username,
                actorId );
        directoryData = new DirectoryData( proceduresData, getQueryService(), getPlanParticipationService() );
    }

    private void doAddContent() {
        addAboutProtocols();
        addParticipation();
        addProtocolsFinder();
        addProtocols();
        addDirectory();
    }

    // ABOUT

    private void addAboutProtocols() {
        PlanIdentifierData planIdentifierData = proceduresData.getPlanIdentifier();
        aboutContainer = new WebMarkupContainer( "about" );
        getContainer().add( aboutContainer );
        aboutContainer
                .add( new Label( "planName", planIdentifierData.getName() ) )
                .add( new Label( "userOrAgentName", getParticipantName() ) )
                .add( new Label( "planVersion", Integer.toString( planIdentifierData.getVersion() ) ) )
                .add( new Label( "planDate", planIdentifierData.getDateVersioned() ) );
    }

    private String getParticipantName() {
        if ( username != null )
            return getUserDao().getFullName( username );
        else if ( actor != null )
            return actor.getName();
        else
            return "???";
    }

    // PARTICIPATION

    private void addParticipation() {
        ListView<EmploymentData> employmentsList = new ListView<EmploymentData>(
                "participationList",
                proceduresData.getEmployments()
        ) {
            @Override
            protected void populateItem( ListItem<EmploymentData> item ) {
                EmploymentData employmentData = item.getModelObject();
                item.add( new Label( "employment", employmentData.getLabel() ) );
            }
        };
        getContainer().add( employmentsList );
    }

    // FINDER

    private void addProtocolsFinder() {
        finderContainer = new WebMarkupContainer( "finder" );
        getContainer().add( finderContainer );
        addOngoingFinder();
        addOnObservationFinder();
        addOnCommunicationFinder();
    }

    private void addOngoingFinder() {
        List<ProcedureData> procedures = finder.getOngoingProcedures();
        WebMarkupContainer ongoingToc = new WebMarkupContainer( "ongoing-toc" );
        finderContainer.add( ongoingToc );
        ongoingToc.setVisible( !procedures.isEmpty() );
        ongoingToc.add( makeProcedureLinks( "ongoingLinks", procedures ) );
    }

    private ListView<ProcedureData> makeProcedureLinks( String id, List<ProcedureData> procedureDataList ) {
        List<ProcedureData> sortedProcedureDataList = new ArrayList<ProcedureData>( procedureDataList );
        Collections.sort( sortedProcedureDataList, new Comparator<ProcedureData>() {
            @Override
            public int compare( ProcedureData pd1, ProcedureData pd2 ) {
                return pd1.getLabel().compareTo( pd2.getLabel() );
            }
        } );
        return new ListView<ProcedureData>(
                id,
                sortedProcedureDataList
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ProcedureDataLinkPanel( "procLink", item.getModelObject(), finder ) );
            }
        };
    }

    private void addOnObservationFinder() {
        final Map<TriggerData, List<ProcedureData>> onObservations = finder.getOnObservationProcedures();
        WebMarkupContainer observationsToc = new WebMarkupContainer( "observations-toc" );
        finderContainer.add( observationsToc );
        observationsToc.setVisible( !onObservations.isEmpty() );
        List<TriggerData> sortedTriggers = finder.sortTriggerData( onObservations.keySet() );
        ListView<TriggerData> observationList = new ListView<TriggerData>(
                "observations",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                Label observationLabel = new Label( "observation", triggerData.getLabel() );
                item.add( observationLabel );
                item.add( makeProcedureLinks( "procLinks", onObservations.get( triggerData ) ) );
            }
        };
        observationsToc.add( observationList );
    }

    private void addOnCommunicationFinder() {
        final Map<String, List<ContactData>> triggerRolodex = finder.getAlphabetizedTriggerRolodex();
        WebMarkupContainer communicationsToc = new WebMarkupContainer( "communications-toc" );
        finderContainer.add( communicationsToc );
        communicationsToc.setVisible( !triggerRolodex.isEmpty() );
        ListView<String> commSectionListView = new ListView<String>(
                "commSections",
                finder.getSortedTriggerRolodexTabs()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                String letter = item.getModelObject();
                item.add( new Label( "letter", letter ) );
                item.add( makeInterlocutorListView( triggerRolodex.get( letter ) ) );
            }
        };
        communicationsToc.add( commSectionListView );
    }

    private ListView<ContactData> makeInterlocutorListView( List<ContactData> contactDataList ) {
        return new ListView<ContactData>(
                "interlocutors",
                contactDataList
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                item.add( new ContactLinkPanel( "contact", contactData, finder ) );
                // requests
                Map<TriggerData, List<ProcedureData>> triggeringRequests =
                        finder.getTriggeringRequestsFrom( contactData );
                WebMarkupContainer requestsFromInterlocutor = new WebMarkupContainer( "askedYou" );
                item.add( requestsFromInterlocutor );
                requestsFromInterlocutor.setVisible( !triggeringRequests.isEmpty() );
                requestsFromInterlocutor.add( makeInterlocutorRequestsListView( triggeringRequests ) );
                // notifications
                Map<TriggerData, List<ProcedureData>> triggeringNotifications =
                        finder.getTriggeringNotificationsFrom( contactData );
                WebMarkupContainer notificationsFromInterlocutor = new WebMarkupContainer( "notifiedYou" );
                item.add( notificationsFromInterlocutor );
                notificationsFromInterlocutor.setVisible( !triggeringNotifications.isEmpty() );
                notificationsFromInterlocutor.add( makeInterlocutorNotificationsListView( triggeringNotifications ) );
            }
        };
    }

    private ListView<TriggerData> makeInterlocutorRequestsListView(
            final Map<TriggerData, List<ProcedureData>> triggeringRequests ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringRequests.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
                    @Override
                    public int compare( TriggerData td1, TriggerData td2 ) {
                        return td1.getLabel().compareTo( td2.getLabel() );
                    }
                } );
        ListView<TriggerData> interlocutorRequestsListView = new ListView<TriggerData>(
                "tocRequests",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                item.add( new Label( "request", triggerData.getLabel() ) );
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null ? "" : communicatedContext.getTriggerLabel() );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringRequests.get( triggerData ) ) );
            }
        };
        return interlocutorRequestsListView;
    }

    private ListView<TriggerData> makeInterlocutorNotificationsListView(
            final Map<TriggerData, List<ProcedureData>> triggeringNotifications ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringNotifications.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
                    @Override
                    public int compare( TriggerData td1, TriggerData td2 ) {
                        return td1.getLabel().compareTo( td2.getLabel() );
                    }
                } );
        ListView<TriggerData> interlocutorNotificationsListView = new ListView<TriggerData>(
                "tocNotifications",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                item.add( new Label( "notification", triggerData.getLabel() ) );
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null ? "" : communicatedContext.getTriggerLabel() );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringNotifications.get( triggerData ) ) );
            }
        };
        return interlocutorNotificationsListView;
    }

    // PROTOCOLS

    private void addProtocols() {
        protocolsContainer = new WebMarkupContainer( "protocols" );
        getContainer().add( protocolsContainer );
        addOngoingProcedures();
        addOnObservationProcedures();
        addOnRequestProcedures();
        addOnNotificationProcedures();
        addOnDiscoveryProcedures();
        addOnResearchProcedures();
    }


    private void addOngoingProcedures() {
        List<ProcedureData> sortedProcedures = new ArrayList<ProcedureData>( finder.getOngoingProcedures() );
        Collections.sort(
                sortedProcedures,
                new Comparator<ProcedureData>() {
                    @Override
                    public int compare( ProcedureData pd1, ProcedureData pd2 ) {
                        return pd1.getLabel().compareTo( pd2.getLabel() );
                    }
                } );
        WebMarkupContainer ongoingContainer = new WebMarkupContainer( "ongoing" );
        protocolsContainer.add( ongoingContainer );
        ongoingContainer.setVisible( !sortedProcedures.isEmpty() );
        ListView<ProcedureData> ongoingProcsListView = new ListView<ProcedureData>(
                "ongoingProcedures",
                sortedProcedures
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                ProcedureData procedureData = item.getModelObject();
                item.add( new ProcedureDataPanel( "procedure", procedureData, finder ) );
            }
        };
        ongoingContainer.add( ongoingProcsListView );
    }

    private void addOnObservationProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onObservations",
                finder.getOnObservationProcedures() ) );
    }

    private void addOnRequestProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onRequests",
                finder.getOnRequestProcedures() ) );
    }

    private void addOnNotificationProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onNotifications",
                finder.getOnNotificationProcedures() ) );
    }

    private void addOnDiscoveryProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onDiscoveries",
                finder.getOnDiscoveryProcedures() ) );
    }

    private void addOnResearchProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onResearches",
                finder.getOnResearchProcedures() ) );
    }

    private WebMarkupContainer makeTriggeredProceduresContainer(
            String procsContainerId,
            final Map<TriggerData, List<ProcedureData>> procedureDataMap
    ) {
        WebMarkupContainer procsContainer = new WebMarkupContainer( procsContainerId );
        procsContainer.setVisible( !procedureDataMap.isEmpty() );
        protocolsContainer.add( procsContainer );
        List<TriggerData> triggers = finder.sortTriggerData( procedureDataMap.keySet() );
        procsContainer.add(  new ListView<TriggerData>(
                "triggered",
                triggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData trigger = item.getModelObject();
                item.add( makeTriggerDataPanel( "trigger", trigger ) );
                item.add( makeProcedurePanels( "procedures", procedureDataMap.get( trigger ) ) );
            }
        }
        );
        return procsContainer;
    }

    private AbstractDataPanel makeTriggerDataPanel( String id, TriggerData triggerData ) {
        if ( triggerData.isOnObserving() )
            return new ObservationTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnNotificationFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnRequestFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnDiscovering() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnResearching() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else throw new RuntimeException( "Unknown trigger " + triggerData.getLabel() );
    }


    private ListView<ProcedureData> makeProcedurePanels( String id, List<ProcedureData> procedureDataList ) {
        return new ListView<ProcedureData>(
                id,
                procedureDataList
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                ProcedureData procedureData = item.getModelObject();
                item.add( new ProcedureDataPanel( "procedure", procedureData, finder ) );
            }
        };
    }


    // DIRECTORY

    private void addDirectory() {
        directoryContainer = new WebMarkupContainer( "directory" );
        getContainer().add( directoryContainer );
        List<String> orgNames = finder.getSortedOrganizationNames();
        ListView<String> orgContactsListView = new ListView<String>(
                "orgContacts",
                orgNames
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String orgName = item.getModelObject();
                Organization org = getQueryService().findActualEntity( Organization.class, orgName );
                if ( org == null ) {
                    item.add( new Label( "orgContact", "" ) );
                    item.add( new Label( "employeeContacts", "" ) );
                } else {
                    item.add( new OrganizationContactPanel( "orgContact", org, finder ) );
                    ListView<ContactData> employeeContactsListView = new ListView<ContactData>(
                            "employeeContacts",
                            finder.getContactsInOrganization( orgName )
                    ) {
                        @Override
                        protected void populateItem( ListItem<ContactData> subItem ) {
                            ContactData contact = subItem.getModelObject();
                            subItem.add( new ContactDataPanel( "employeeContact", contact, finder ) );
                        }
                    };
                    item.add( employeeContactsListView );
                }
            }
        };
        directoryContainer.add( orgContactsListView );
    }

    //////////////


    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return finder.findInScope( moDataClass, moId );
    }

    public <T extends ModelObject> T find( Class<T> moClass, long moId ) {
        try {
            return getQueryService().find( moClass, moId );
        } catch ( NotFoundException e ) {
            return null;
        }
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

    private WebMarkupContainer makeAnchor( String id, String anchor ) {
        WebMarkupContainer anchorContainer = new WebMarkupContainer( id );
        anchorContainer.add( new AttributeModifier( "name", anchor ) );
        return anchorContainer;
    }



}
